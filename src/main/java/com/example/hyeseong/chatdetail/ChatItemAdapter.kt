package com.example.hyeseong.chatdetail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hyeseong.chatList.ChatListItem
import com.example.hyeseong.databinding.ItemChatBinding
import com.example.hyeseong.databinding.ItemChatlistBinding


class ChatItemAdapter : ListAdapter<ChatItem, ChatItemAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(chatItem: ChatItem) {

            binding.senderTextView.text = chatItem.senderId
            binding.messageTextView.text = chatItem.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatItem>() {
            override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                // 현재 노출하고 있는 아이템과 새로운 아이템이 같은지 비교;
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                // equals 비교;
                return oldItem == newItem

            }

        }
    }
}