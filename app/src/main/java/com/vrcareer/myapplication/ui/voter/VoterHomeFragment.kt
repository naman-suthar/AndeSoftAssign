package com.vrcareer.myapplication.ui.voter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.navArgs
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase
import com.vrcareer.myapplication.databinding.FragmentVoterHomeBinding
import com.vrcareer.myapplication.model.Party
import com.vrcareer.myapplication.model.Voter


class VoterHomeFragment : Fragment() {

    lateinit var db: FirebaseDatabase
    val args: VoterHomeFragmentArgs by navArgs()
    lateinit var binding: FragmentVoterHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentVoterHomeBinding.inflate(inflater, container, false)
        db = FirebaseDatabase.getInstance()

        var mUser: Voter? = null

        db.reference.child("Voter/${args.userid}").get().addOnSuccessListener {
            if (it.exists()) {
                mUser = it.getValue(Voter::class.java)!!
                updateIfVoted(mUser!!)
            }
        }

        binding.cardBjp.setOnClickListener {

            (it as MaterialCardView).let { mc ->
                mc.isChecked = true
                binding.cardAap.isChecked = false

                mUser?.let { user ->
                    user.hasVoted = true
                    user.votedTo = "BJP"

                    cancelMyVote("AAP", user)
                    voteThisParty("BJP", user)

                    db.reference.child("Voter").child(args.userid).setValue(mUser)
                }
            }
        }
        binding.cardAap.setOnClickListener {
            (it as MaterialCardView).let { mc ->
                mc.isChecked = true
                binding.cardBjp.isChecked = false

                mUser?.let { user ->
                    user.hasVoted = true
                    user.votedTo = "AAP"
                    cancelMyVote("BJP", user)
                    voteThisParty("AAP", user)
                    val voter = Voter(
                        name = mUser?.name,
                        id = mUser?.id,
                        age = mUser?.age,
                        phoneNo = mUser?.phoneNo,
                        role = mUser?.role,
                        hasVoted = mUser?.hasVoted,
                        votedTo = mUser?.votedTo
                    )
                    db.reference.child("Voter").child(args.userid).setValue(voter)
                }
            }
        }

        binding.btnSubmit.setOnClickListener {
            if (mUser?.hasVoted == true) {
                updateIfVoted(mUser!!)
            } else {
                Toast.makeText(
                    this.requireContext(),
                    "Select any one to submit",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this.requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d("Back", "Fragment back pressed invoked")
                    requireActivity().finish()

                }
            }
            )
        return binding.root
    }

    private fun updateIfVoted(mUser: Voter) {
        if (mUser.hasVoted == true) {
            binding.cardBjp?.isCheckable = false
            binding.cardAap?.isCheckable = false
            binding.cardAap?.isClickable = false
            binding.cardBjp?.isClickable = false
            binding.cardAap?.setOnClickListener(null)
            binding.cardBjp?.setOnClickListener(null)
            binding.txtVoteInfo?.text = "You have already voted for : ${mUser.votedTo}"
        }
    }

    private fun cancelMyVote(s: String, v: Voter) {
        db.reference.child("Party").child(s).get().addOnSuccessListener { ds ->
            if (ds.exists()) {
                val party = ds.getValue(Party::class.java)
                Log.e("Party", "$party")

                if (party?.votersList != null) {
                    for (voter in party?.votersList!!) {
                        if (voter.phoneNo == v.phoneNo) {
                            party?.votersList!!.remove(voter)
                        }
                    }
                }
                db.reference.child("Party").child(s).setValue(party).addOnSuccessListener {
                    Log.e("Changed", "Party")
                }
            }
        }
    }

    private fun voteThisParty(s: String, v: Voter) {
        db.reference.child("Party").child(s).get().addOnSuccessListener { ds ->
            if (ds.exists()) {
                val party = ds.getValue(Party::class.java)
                Log.e("Party", "$party")
                if (party?.votersList != null) {
                    party?.votersList!!.add(v)
                } else {
                    val list = mutableListOf<Voter>()
                    list.add(v)
                    party?.votersList = list
                }
                db.reference.child("Party").child(s).setValue(party).addOnSuccessListener {
                    Toast.makeText(
                        this.requireContext(),
                        "Your Vote is Accepted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }


}