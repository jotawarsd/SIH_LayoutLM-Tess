package edu.stanford.rkpandey.cameraintegration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


public class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // use arrayadapter and define an array
        val arrayAdapter: ArrayAdapter<*>
//        val users = arrayOf(
//           "keyword1", "keyword2", "keyword3", "keyword4"
//        )


        val keywords =intent.getStringArrayListExtra("keyword_arr")

        // access the listView from xml file
        var mListView = findViewById<ListView>(R.id.list_item)
        if (keywords != null) {
            arrayAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, keywords.toMutableList())
            mListView.adapter = arrayAdapter
            mListView.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val options = arrayOf<CharSequence>("Diksha", "Google", "Wikipedia")
                val builder = AlertDialog.Builder(this@MainActivity2)
                builder.setTitle("Search From:")
                builder.setItems(options) { dialog, item ->
                    if (options[item] == "Diksha") {
                        val input = keywords.get(i)
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://diksha.gov.in/explore/1?key=$input&selectedTab=all")
                        )
                        startActivity(intent)
                    } else if (options[item] == "Google") {
                        val input = keywords.get(i)
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://scholar.google.com/scholar?q=$input")
                        )
                        startActivity(intent)
                    } else if (options[item] == "Wikipedia") {

                        val input = keywords.get(i)
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://en.m.wikipedia.org/wiki/$input")
                        )
                        startActivity(intent)

                    }
                }
                builder.show()
            })

        }



    }


    }
