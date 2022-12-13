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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.vrcareer.myapplication.R
import com.vrcareer.myapplication.databinding.FragmentSignUpBinding
import com.vrcareer.myapplication.model.Voter


class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var binding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        binding.txtLogIn.setOnClickListener {
            navigateToLogIn()
        }
        binding.btnSignUp.setOnClickListener {
            signUp()
        }

        return binding.root
    }

    private fun signUp() {
        binding.progressSignup.visibility = View.VISIBLE

        val email = binding.etVoterEmailSignup.text.toString()
        val password = binding.etVoterPasswordSignup.text.toString()
        val name = binding.etVoterNameSignup.text.toString()
        val phone = binding.etVoterPhoneSignup.text.toString()
        val age = binding.etVoterAgeSignup.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val newVoter = Voter(id = user?.uid,name = name, phoneNo = phone, age = age)

                    db.reference.child("Voter").child("${user?.uid}").setValue(newVoter)
                        .addOnSuccessListener {
                        Toast.makeText(this.requireContext(), "User Created",
                            Toast.LENGTH_SHORT).show()

                        navigateToVoterScreen(user?.uid.toString())
                    }
                        .addOnFailureListener {
                            auth.currentUser?.delete()
                            Toast.makeText(this.requireContext(), "Please Retry",
                                Toast.LENGTH_SHORT).show()
                        }

                } else {

                    binding.progressSignup.visibility = View.GONE

                    Toast.makeText(this.requireContext(), "Network failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }

    private fun navigateToVoterScreen(uid:String) {
        val action = SignUpFragmentDirections.actionSignUpFragmentToVoterHomeFragment("$uid")
        findNavController().navigate(action)
    }

    private fun navigateToLogIn() {
        val action = SignUpFragmentDirections.actionSignUpFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
    }
}