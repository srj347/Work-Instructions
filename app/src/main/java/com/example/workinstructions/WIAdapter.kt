package com.example.workinstructions
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workinstructions.listeners.OnItemClickListener
import com.example.workinstructions.model.WorkInstruction

class WIAdapter(private val wiList: List<WorkInstruction>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<WIAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemview_wi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wi = wiList[position]
        holder.bind(wi, position)
    }

    override fun getItemCount(): Int = wiList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wiImageView = itemView.findViewById<ImageView>(R.id.wi_image)
        val wiNameTextView = itemView.findViewById<TextView>(R.id.wi_name)
        val wiNumberTextView = itemView.findViewById<TextView>(R.id.wi_number)

        fun bind(wiData: WorkInstruction, position: Int) {
            wiNameTextView.text = wiData.header
            wiNumberTextView.text = (position+1).toString()

            itemView.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition)
            }
        }
    }
}
