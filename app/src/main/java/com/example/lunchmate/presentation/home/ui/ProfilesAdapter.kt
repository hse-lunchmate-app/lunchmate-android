package com.example.lunchmate.presentation.home.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.R
import com.example.lunchmate.databinding.ItemProfileBinding
import com.example.lunchmate.domain.model.User


class ProfilesAdapter(
    val onProfileClick: (User) -> Unit,
    private val accountsList: List<User>
) :
    RecyclerView.Adapter<ProfilesAdapter.ViewHolder>() {

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

        fun bind(model: User) {
            name.text = model.name
            info.text = model.aboutMe
            //photo.setImageResource(model.)

            parent.setOnClickListener {
                onProfileClick(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: User = accountsList[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int {
        return accountsList.size
    }
}