package com.shahzaib.message

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.widget.Toast
import com.google.gson.Gson

class SmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val messages: Array<SmsMessage?> = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (message in messages) {
            Toast.makeText(context, message!!.displayOriginatingAddress + ": " + message.messageBody, Toast.LENGTH_LONG).show()

            val sendBroadcastIntent = Intent()
            sendBroadcastIntent.action = "Messages"
            sendBroadcastIntent.putExtra("Message", serialize(Message(message.displayOriginatingAddress, message.messageBody)))
            context.sendBroadcast(sendBroadcastIntent)
        }
    }

    fun serialize(obj: Any?): String? {
        if (obj == null)
            return null

        val gson = Gson()
        return gson.toJson(obj)
    }
}
