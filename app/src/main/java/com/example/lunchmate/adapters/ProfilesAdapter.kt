package com.example.lunchmatelocal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.R
import com.example.lunchmate.databinding.ItemProfileBinding


class ProfilesAdapter(val onProfileClick: (Int) -> Unit, accountsList: ArrayList<Account>) :
    RecyclerView.Adapter<ProfilesAdapter.ViewHolder>() {
    private val accountsList: ArrayList<Account>

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemProfileBinding.bind(itemView)
        val parent: CardView
        val photo: ImageView
        val name: TextView
        val info: TextView
        init {
            photo = binding.itemPhoto
            name = binding.itemTitle
            info = binding.itemContent
            parent = binding.itemParent
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilesAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfilesAdapter.ViewHolder, position: Int) {
        val model: Account = accountsList[position]
        holder.name.setText(model.getName())
        holder.info.setText(model.getInfo())
        holder.photo.setImageResource(model.getPhoto())

        holder.parent.setOnClickListener {
            onProfileClick(position)
        }
    }

    override fun getItemCount(): Int {
        return accountsList.size
    }

    init {
        this.accountsList = accountsList
    }
}