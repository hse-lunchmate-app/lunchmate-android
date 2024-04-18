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
import com.example.lunchmate.model.User


class ProfilesAdapter(val onProfileClick: (User) -> Unit, accountsList: List<User>) :
    RecyclerView.Adapter<ProfilesAdapter.ViewHolder>() {
    private val accountsList: List<User>

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
        val model: User = accountsList[position]
        holder.name.setText(model.name)
        holder.info.setText(model.aboutMe)
        //holder.photo.setImageResource(model.)

        holder.parent.setOnClickListener {
            onProfileClick(model)
        }
    }

    override fun getItemCount(): Int {
        return accountsList.size
    }

    init {
        this.accountsList = accountsList
    }
}