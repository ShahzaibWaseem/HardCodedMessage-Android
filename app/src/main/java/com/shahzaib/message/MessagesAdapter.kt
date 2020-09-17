package com.shahzaib.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessagesAdapter (private var messageList: MutableList<Message>, private val context: Context) :
    RecyclerView.Adapter<MessagesAdapter.MessageHolder>() {

    class MessageHolder(view: View): RecyclerView.ViewHolder(view) {
        val phoneNumber: TextView = view.findViewById(R.id.phoneNumber)
        val textMessage: TextView = view.findViewById(R.id.textMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return MessageHolder(view)
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        holder.phoneNumber.text = messageList[position].phoneNumber
        holder.textMessage.text = messageList[position].textMessage
    }

    override fun getItemCount() = messageList.size
}