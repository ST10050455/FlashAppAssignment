package com.example.flashapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

// Adapter for displaying a list of categories in a RecyclerView
class CategoryAdapter(
    private var categories: List<Category>,                     // List of categories to show
    private val onDeleteClick: (Category) -> Unit               // Function to handle delete clicks
) : RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    // ViewHolder holds the views for each item in the RecyclerView
    inner class CategoryHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_category_name)
        val limit: TextView = view.findViewById(R.id.item_category_limit)
        val description: TextView = view.findViewById(R.id.item_category_description)
        val icon: ImageView = view.findViewById(R.id.item_category_icon)
        val updateButton: TextView = view.findViewById(R.id.btnUpdate)
        val deleteButton: TextView = view.findViewById(R.id.btnDelete)
    }

    // Called when RecyclerView needs a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        // Inflate the item layout for each category
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryHolder(view)
    }

    // Binds data to each item in the RecyclerView
    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val category = categories[position]
        val context = holder.itemView.context

        // Set category name, description, icon, and limit
        holder.name.text = category.name
        holder.description.text = category.description
        holder.icon.setImageResource(category.iconResId)
        holder.limit.text = "R%.2f".format(category.limit)

        // Change limit text color based on positive or negative value
        val colorRes = if (category.limit >= 0) R.color.green else R.color.red
        holder.limit.setTextColor(ContextCompat.getColor(context, colorRes))

        // Open DetailedActivity when the item is clicked
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("category", category)
            context.startActivity(intent)
        }

        // Delete category when delete button is clicked
        holder.deleteButton.setOnClickListener {
            onDeleteClick(category)
        }

        // Open DetailedActivity (can be used to update) when update button is clicked
        holder.updateButton.setOnClickListener {
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("category", category)
            context.startActivity(intent)
        }
    }

    // Returns the number of items in the list
    override fun getItemCount(): Int = categories.size

    // Allows updating the list of categories and refreshing the view
    fun setData(newCategories: List<Category>) {
        this.categories = newCategories
        notifyDataSetChanged()
    }
}
