package com.vrcareer.myapplication.ui.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.database.FirebaseDatabase
import com.vrcareer.myapplication.R
import com.vrcareer.myapplication.VoterAdapter
import com.vrcareer.myapplication.databinding.FragmentAdminHomeBinding
import com.vrcareer.myapplication.model.Party


class AdminHomeFragment : Fragment() {
    lateinit var db: FirebaseDatabase
    lateinit var binding: FragmentAdminHomeBinding
    lateinit var adapter: VoterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdminHomeBinding.inflate(inflater,container,false)
        db = FirebaseDatabase.getInstance()
        adapter = VoterAdapter()
        binding.rvVoters.layoutManager = LinearLayoutManager(this.requireContext())
        binding.rvVoters.adapter = adapter
        getData("BJP")
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0-> getData("BJP")
                    1-> getData("AAP")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this.requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
            )
        return binding.root
    }

    private fun getData(s: String) {
        db.reference.child("Party").child(s).get().addOnSuccessListener {
            if (it.exists()){
                val party:Party? = it.getValue(Party::class.java)
                if (party?.votersList == null){
                    binding.txtVoteCount.text = "Voters : 0"
                    adapter.updateList(mutableListOf())
                }else{
                    binding.txtVoteCount.text = "Voters : ${party?.votersList?.size}"
                    party?.votersList?.let { it1 -> adapter.updateList(it1) }
                }

            }
        }
    }


}