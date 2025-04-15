package com.example.fitnesswithfriends.firebase

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.example.fitnesswithfriends.models.User
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import com.example.fitnesswithfriends.models.PastWorkout
import com.example.fitnesswithfriends.BuildConfig

class GoogleSignInClient(private var context: Context
    ) {

    private val credentialManager = CredentialManager.create(context)
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var currentUser: User? = null

    suspend fun signIn(): Boolean{
        if(isSignedIn()) {
            return true
        }
        try{
            val result = buildCredentialRequest()
            return handleSignIn(result)
        }
        catch (e: Exception){
            if(e is CancellationException) throw e
            println("SignIn Error ${e.message}")
            return false
        }

        return true;
    }

    private suspend fun handleSignIn(result: GetCredentialResponse): Boolean{
        var credential = result.credential

        if(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
            try{
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                currentUser = getUser(tokenCredential.id)
                if(currentUser == null) {
                    val newUser = User(name = tokenCredential.displayName.toString(), image_url = tokenCredential.profilePictureUri.toString(), email = tokenCredential.id)
                    currentUser = newUser
                    addUser(
                        user = newUser
                    ) { success ->
                        println("User write success: $success")
                    }
                }
                return true
            }
            catch(e: GoogleIdTokenParsingException){
                println("Error Parsing Token: " + e)
                return false
            }
        }
        else{
            return false
        }
    }

    suspend fun buildCredentialRequest(): GetCredentialResponse{

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(
                        BuildConfig.API_KEY
                    )
                    .setAutoSelectEnabled(false)
                    .build()
            )
            .build()

        return credentialManager.getCredential(
            request = request, context = context
        )
    }

    suspend fun getUser(email: String): User? {
        return try {
            val snapshot = db
                .collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()
            if (!snapshot.isEmpty) {
                val document = snapshot.documents[0]

                // Manually map the fields from the user document
                val name = document.getString("name") ?: "Unknown"
                val imageUrl = document.getString("image_url") ?: ""
                val userEmail = document.getString("email") ?: ""
                // Fetch the 'pastWorkouts' sub-collection
                val pastWorkoutsSnapshot = db
                    .collection("users")
                    .document(document.id) // Access the user's specific document by its ID
                    .collection("pastWorkouts")
                    .get()
                    .await()
                val pastWorkouts = pastWorkoutsSnapshot.documents.mapNotNull { pastWorkoutDoc ->
                    // Assuming PastWorkout is a data class
                    pastWorkoutDoc.toObject(PastWorkout::class.java)
                }

                return User(
                    name = name,
                    image_url = imageUrl,
                    email = userEmail,
                    pastWorkouts = pastWorkouts
                )
            } else {
                return null // No user found with that email
            }
        } catch (e: Exception) {
            println("Error checking user: ${e.message}")
            return null
        }
    }

    suspend fun signOut(){
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        firebaseAuth.signOut()
    }

    fun isSignedIn(): Boolean{
        return firebaseAuth.currentUser != null
    }

    fun addUser(user: User, onResult: (Boolean) -> Unit) {

        db.collection("users")
            .document(user.email)
            .set(user)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun getCurrentUser(): User {
        return currentUser ?: User(
            name = "Error",
            email = "error",
            image_url = "https://static.vecteezy.com/system/resources/thumbnails/020/765/399/small_2x/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg"
        )
    }


}