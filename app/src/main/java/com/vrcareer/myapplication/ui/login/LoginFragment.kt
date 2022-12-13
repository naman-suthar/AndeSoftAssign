package com.vrcareer.myapplication.ui.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.vrcareer.myapplication.R
import com.vrcareer.myapplication.databinding.FragmentLoginBinding
import com.vrcareer.myapplication.model.Voter

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        binding.txtSignUp.setOnClickListener {
            navigateToSignUp()
        }

        binding.btnLogIn.setOnClickListener {
            binding.progressLogin.visibility = View.VISIBLE
           logInUser()
        }

        return binding.root
    }

    private fun logInUser() {
        val email = binding.etVoterEmail.text.toString()
        val password = binding.etVoterPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser

                    db.reference.child("Voter/${user?.uid}").get().addOnSuccessListener { dS ->
                        if (dS.exists()) {
                            for (snap in dS.children) {
                                if (snap.key == "role") {
                                    val role = snap.value
                                    Log.e("Login", "$role")
                                    if (role == "admin") {
                                        navigateToAdminScreen()
                                    } else {
                                       navigateToVoterScreen(user?.uid.toString())

                                    }
                                }
                            }
                        }
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    binding.progressLogin.visibility = View.GONE
                    Log.w("Login", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this.requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

    private fun navigateToVoterScreen(uid: String) {
        val action =
            LoginFragmentDirections.actionLoginFragmentToVoterHomeFragment(
                "$uid"
            )
        findNavController().navigate(action)
    }

    private fun navigateToAdminScreen() {
        val action =
            LoginFragmentDirections.actionLoginFragmentToAdminHomeFragment()
        findNavController().navigate(action)
    }

    private fun navigateToSignUp() {
        val action = LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
        findNavController().navigate(action)
    }


}