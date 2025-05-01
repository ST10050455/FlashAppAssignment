package com.example.flashapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private var categories: List<Category>, private val onDeleteClick: (Category) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    inner class CategoryHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_category_name)
        val limit: TextView = view.findViewById(R.id.item_category_limit)
        val description: TextView = view.findViewById(R.id.item_category_description)
        val icon: ImageView = view.findViewById(R.id.item_category_icon)
        val updateButton: TextView = view.findViewById(R.id.btnUpdate)
        val deleteButton: TextView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val category = categories[position]
        val context = holder.itemView.context

        holder.name.text = category.name
        holder.description.text = category.description
        holder.icon.setImageResource(category.iconResId) // Set the icon
        holder.limit.text = "R%.2f".format(category.limit)

        // Set text color based on category limit
        val colorRes = if (category.limit >= 0) R.color.green else R.color.red
        holder.limit.setTextColor(ContextCompat.getColor(context, colorRes))

        // Handle category click to open the DetailedActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("category", category)
            context.startActivity(intent)
        }

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            onDeleteClick(category) // Call the delete function passed from CategoryListActivity
        }

        // Handle update button click (if necessary)
        holder.updateButton.setOnClickListener {
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("category", category)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = categories.size

    // Update the adapter data and notify changes
    fun setData(newCategories: List<Category>) {
        this.categories = newCategories
        notifyDataSetChanged()
    }
}
