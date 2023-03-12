package com.antonin.friendswave.ui.home

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.antonin.friendswave.R

class EditProfilActivity : AppCompatActivity() {


    lateinit var gridView: GridView
    lateinit var spinner: Spinner
    private lateinit var list: ArrayList<String>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)

        val languages = arrayOf("Français", "Anglais", "Allemand", "Espagnol", "Arabe")

        var hobbiesName = arrayOf("Sport", "Cinema", "Musique", "Restaurants",
            "Exposition", "Randonnée", "Nature", "Chill", "Culture", "TEST")
        spinner = findViewById(R.id.spinnerLangues)
        val aa: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, languages)

        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa)

        gridView = findViewById(R.id.gridView)
        val mainAdapter = MainAdapter(this@EditProfilActivity, hobbiesName)
        gridView.adapter = mainAdapter

    }


}


internal class MainAdapter(private val context: Context, private val numbersInWords: Array<String>) : BaseAdapter() {


    private var layoutInflater: LayoutInflater? = null

    private lateinit var textView: TextView

    override fun getCount(): Int {
        return numbersInWords.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        var convertView = convertView
        if (layoutInflater == null) {

            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        if (convertView == null) {

            convertView = layoutInflater!!.inflate(R.layout.activity_gridview, null)

        }

        textView = convertView!!.findViewById(R.id.txtTestGridd)
        textView.text = numbersInWords[position]

        return convertView
    }
}

