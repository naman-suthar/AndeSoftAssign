package com.vrcareer.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vrcareer.myapplication.model.Voter
import org.w3c.dom.Text

class VoterAdapter : RecyclerView.Adapter<VoterAdapter.VoterViewHolder>() {
    private val voterList: MutableList<Voter> = mutableListOf()
    fun updateList(list: MutableList<Voter>) {
        voterList.clear()
        voterList.addAll(list)
        notifyDataSetChanged()
    }

    class VoterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtIndex: TextView = view.findViewById(R.id.txt_index)
        val txtName: TextView = view.findViewById(R.id.voter_name)
        val txtAge: TextView = view.findViewById(R.id.voter_age)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.voter_single_item, parent, false)
        return VoterViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoterViewHolder, position: Int) {
        val currVoter = voterList[position]
        holder.txtIndex.text = "${position+1}"
        holder.txtName.text = currVoter.name
        holder.txtAge.text = currVoter.age
    }

    override fun getItemCount() = voterList.size
}