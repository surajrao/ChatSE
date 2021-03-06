package me.shreyasr.chatse.chat

import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.koushikdutta.ion.Ion
import me.shreyasr.chatse.R
import me.shreyasr.chatse.event.EventList
import me.shreyasr.chatse.event.presenter.message.MessageEvent
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(val events: EventList, var messages: List<MessageEvent> = ArrayList()) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onBindViewHolder(viewHolder: MessageViewHolder?, pos: Int) {
        viewHolder?.bindMessage(messages[pos])
    }

    fun update() {
        messages = events.messagePresenter.getEventsList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timestampFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())
        private val messageView = itemView.findViewById(R.id.message_content) as TextView
        private val userNameView = itemView.findViewById(R.id.message_user_name) as TextView
        private val messageTimestamp = itemView.findViewById(R.id.message_timestamp) as TextView
        private val editIndicator = itemView.findViewById(R.id.message_edit_indicator) as ImageView
        private val oneboxImage = itemView.findViewById(R.id.message_image) as ImageView

        fun bindMessage(message: MessageEvent) {
            val context = itemView?.context

            if (message.isDeleted) {
                messageView.setTextColor(ContextCompat.getColor(context, R.color.deleted))
                messageView.text = context?.getString(R.string.removed)
//                oneboxImage.setImageDrawable(null)
            } else {
                if (!message.onebox) {
                    messageView.setTextColor(ContextCompat.getColor(context, R.color.primary_text))
                    messageView.text = message.content
                    //If Android version is 24 and above use the updated version, otherwise use the deprecated version
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        messageView.text = Html.fromHtml(message.content, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        messageView.text = Html.fromHtml(message.content)
                    }
                } else {
//\                    Log.wtf("ImageURL", message.onebox_content + " " + message.onebox_type)
//                    Ion.with(context)
//                            .load(message.onebox_content)
//                            .intoImageView(oneboxImage)

                    //TODO fix the images, reverting to just showing the image URL for now
                    messageView.text = Html.fromHtml("<html><a href=\"" + message.onebox_content + "\">" + message.onebox_content + "</a></html>")
                }
            }

            userNameView.text = message.userName
            messageTimestamp.text = timestampFormat.format(Date(message.timestamp * 1000))
            editIndicator.visibility = if (message.isEdited) View.VISIBLE else View.INVISIBLE
        }
    }
}