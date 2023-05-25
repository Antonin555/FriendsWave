package com.antonin.friendswave.adapter

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.antonin.friendswave.R
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel

//Documentation https://www.geeksforgeeks.org/gridview-using-baseadapter-in-android-with-example/

class MyGridViewAdapter(private val context: Context, private val values: List<String>, private val viewModel: HomeFragmentViewModel) : BaseAdapter() {

    private var selectedPositions = mutableListOf<Int>()

    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): Any {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView: TextView = if (convertView == null) {
            TextView(context)

        } else {
            convertView as TextView

        }

        textView.gravity = Gravity.CENTER
        textView.setPadding(8, 8, 8, 8)
        textView.text = values[position]

        if (selectedPositions.contains(position)) {
//            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_200))
            textView.setBackground(ContextCompat.getDrawable(context,R.drawable.custom_shape))
        }
        else if(viewModel.user_live.value!!.interet!!.contains(values[position])){
//            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_200))
            textView.setBackground(ContextCompat.getDrawable(context,R.drawable.custom_shape))
            selectedPositions.add(position)
        }
        else {
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }


        textView.setOnClickListener {
            if (selectedPositions.contains(position)) {
                selectedPositions.remove(position)
                viewModel.user_live.value!!.interet!!.remove(values[position])


            } else {
                selectedPositions.add(position)
                viewModel.user_live.value!!.interet = getSelectedValues()
//                Toast.makeText(context, "Vous avez cliqué sur l'élément ${viewModel.user_live.value!!.interet}", Toast.LENGTH_SHORT).show()
            }
            notifyDataSetChanged()
        }

        return textView
    }

// a tester
    fun getSelectedValues(): ArrayList<String> {
        return selectedPositions.map { values[it] } as ArrayList<String>
    }
}