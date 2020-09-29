package com.shahzaib.message

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.shahzaib.message.databinding.MessageActivityBinding

class MessageActivity: AppCompatActivity(){
    private val smsManager = SmsManager.getDefault() as SmsManager
    private lateinit var binding: MessageActivityBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: RecyclerView.Adapter<*>
    private var messageList: ArrayList<Message> = arrayListOf()
    
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val serializedMessage = intent!!.getStringExtra("Message")
            val message = deserialize(serializedMessage!!, Message::class.java)
            messageList.add(message)
            messageAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MessageActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.sendingNumberTextView.text = phoneNumber
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.RECEIVE_SMS), RECEIVE_SMS_REQUEST_CODE)

        binding.sendMessageButton.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED)
                sendSMS()
            else
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.SEND_SMS), SEND_SMS_REQUEST_CODE)
        }

        recyclerView = findViewById<RecyclerView>(R.id.messagesRecyclerView).apply {
            layoutManager = LinearLayoutManager(
                this@MessageActivity, LinearLayoutManager.VERTICAL, false)
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction("Messages")
        registerReceiver(mBroadcastReceiver, intentFilter)

        messageAdapter = MessagesAdapter(messageList, this@MessageActivity)
        recyclerView.adapter = messageAdapter
    }

    private fun sendSMS() {
        val sentPI: PendingIntent = PendingIntent.getBroadcast(this, 0,
            Intent(SENT), 0)
        val deliveredPI: PendingIntent = PendingIntent.getBroadcast(this, 0,
            Intent(DELIVERED), 0)

        //---when the SMS has been sent---
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    RESULT_OK ->
                        Toast.makeText(baseContext, "SMS sent", Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE ->
                        Toast.makeText(baseContext, "Generic failure", Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_NO_SERVICE ->
                        Toast.makeText(baseContext, "No service", Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_NULL_PDU ->
                        Toast.makeText(baseContext, "Null PDU", Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_RADIO_OFF ->
                        Toast.makeText(baseContext, "Radio off", Toast.LENGTH_SHORT).show()
                }
            }
        }, IntentFilter(SENT))

        //---when the SMS has been delivered---
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    RESULT_OK -> Toast.makeText(baseContext, "SMS delivered", Toast.LENGTH_SHORT).show()
                    RESULT_CANCELED -> Toast.makeText(baseContext, "SMS not delivered", Toast.LENGTH_SHORT).show()
                }
            }
        }, IntentFilter(DELIVERED))

        smsManager.sendTextMessage(phoneNumber, null, text, sentPI, deliveredPI)
    }


    override fun onStop() {
        super.onStop()
        unregisterReceiver(mBroadcastReceiver)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SEND_SMS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
        else if (requestCode == RECEIVE_SMS_REQUEST_CODE){
            Toast.makeText(this, "Receive Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    fun <T> deserialize(str: String, clazz: Class<T>): T {
        val gson = Gson()
        val obj = gson.fromJson(str, clazz)

        return obj as T
    }

    companion object {
        private const val SEND_SMS_REQUEST_CODE = 1
        private const val RECEIVE_SMS_REQUEST_CODE = 2
        private const val SENT = "SMS_SENT"
        private const val DELIVERED = "SMS_DELIVERED"

        private const val phoneNumber = "YOUR_PHONE_NUMBER"
        private const val text = "Welcome to C.H.I."
    }
}