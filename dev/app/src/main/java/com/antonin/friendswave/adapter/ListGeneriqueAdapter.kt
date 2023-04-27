package com.antonin.friendswave.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antonin.friendswave.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ListGeneriqueAdapter <T : ListItemViewModel>(@LayoutRes val layoutId: Int) :
        ListAdapter<T, ListGeneriqueAdapter.GenericViewHolder<T>>(WordsComparator()) {

        private val items = mutableListOf<T>()
        var storage: FirebaseStorage = Firebase.storage
        private var inflater: LayoutInflater? = null
        private var onListItemViewClickListener: OnListItemViewClickListener? = null

        fun addItems(items: List<T>) {
            this.items.clear()
            this.items.addAll(items)
            notifyDataSetChanged()
        }

        fun setOnListItemViewClickListener(onListItemViewClickListener: OnListItemViewClickListener?){
            this.onListItemViewClickListener = onListItemViewClickListener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<T> {
            val layoutInflater = inflater ?: LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, layoutId, parent, false)
            return GenericViewHolder(binding)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: GenericViewHolder<T>, position: Int) {
            val itemViewModel = items[position]

            itemViewModel.adapterPosition = position

            onListItemViewClickListener?.let { itemViewModel.onListItemViewClickListener = it }
            holder.bind(itemViewModel)


            val image_event = holder.itemView.findViewById<ImageView>(R.id.imageEvent)
            val image_profil = holder.itemView.findViewById<ImageView>(R.id.imageProfil)

            if(image_profil != null){

                val storageRef = storage.reference.child("photos/" + itemViewModel.img.toString())
                storageRef.downloadUrl.addOnSuccessListener {
                    Glide.with(image_profil.context)
                        .load(it)
                        .placeholder(R.drawable.profil)
                        .apply(RequestOptions().override(100, 100))
                        .centerCrop()
                        .into(image_profil)
                }.addOnFailureListener {
                    println(it)
                }

            }


        }


    class GenericViewHolder<T : ListItemViewModel>(private val binding: ViewDataBinding) :
            RecyclerView.ViewHolder(binding.root) {


            fun bind(itemViewModel: T) {
                binding.setVariable(BR.item, itemViewModel)
                binding.executePendingBindings()

            }
        }

        interface OnListItemViewClickListener{
            fun onClick(view: View, position: Int)

        }
    }


class WordsComparator <T>: DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}



