package uz.ultimatedevs.fhwork3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var data = emptyList<Contact>()

    fun submitList(data: List<Contact>) {
        this.data = data
        notifyDataSetChanged()
    }

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContactViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
    )

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val txtName: TextView = holder.itemView.findViewById(R.id.txtName)
        val txtPhone: TextView = holder.itemView.findViewById(R.id.txtPhone)
        val contact = data[position]
        txtName.text = contact.name
        txtPhone.text = contact.phone
    }

    override fun getItemCount(): Int = data.size
}