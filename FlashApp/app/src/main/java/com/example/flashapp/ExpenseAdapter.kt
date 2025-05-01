package com.example.flashapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ExpenseAdapter(
    private var expenses: List<ExpenseEntity>,
    private val context: Context
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseHolder>() {

    inner class ExpenseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amount: TextView = itemView.findViewById(R.id.item_amount)
        val category: TextView = itemView.findViewById(R.id.item_category)
        val description: TextView = itemView.findViewById(R.id.item_description)
        val date: TextView = itemView.findViewById(R.id.item_date)
        val image: ImageView = itemView.findViewById(R.id.item_image)
        val deleteBtn: Button = itemView.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseHolder, position: Int) {
        val expense = expenses[position]
        val symbol = if (expense.type == "INCOME") "+" else "–"
        val amountText = "$symbol R%.2f".format(expense.amount)

        holder.amount.text = amountText
        holder.category.text = expense.category
        holder.description.text = expense.description
        holder.date.text = expense.date

        val colorRes = if (expense.type == "INCOME") R.color.green else R.color.red
        holder.amount.setTextColor(ContextCompat.getColor(context, colorRes))

        if (!expense.imagePath.isNullOrEmpty()) {
            holder.image.visibility = View.VISIBLE
            holder.image.setImageURI(Uri.parse(expense.imagePath))
            holder.image.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.parse(expense.imagePath), "image/*")
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Unable to open image", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            holder.image.visibility = View.GONE
        }

        holder.deleteBtn.setOnClickListener {
            val db = AppDatabase.getDatabase(context)
            (context as? AppCompatActivity)?.lifecycleScope?.launch {
                db.expenseDao().delete(expense)
                setData(expenses.filter { it.id != expense.id })
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = expenses.size

    fun setData(newExpenses: List<ExpenseEntity>) {
        this.expenses = newExpenses
        notifyDataSetChanged()
    }
}
