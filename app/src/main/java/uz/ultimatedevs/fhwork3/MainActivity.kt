package uz.ultimatedevs.fhwork3

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var listContact: RecyclerView
    private lateinit var btnLoad: Button
    private lateinit var progress: ProgressBar
    private val adapter by lazy { ContactAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listContact = findViewById(R.id.list)
        btnLoad = findViewById(R.id.btnLoad)
        progress = findViewById(R.id.progress)

        listContact.adapter = adapter
        listContact.layoutManager = LinearLayoutManager(this)

        btnLoad.setOnClickListener {
            progress.visibility = View.VISIBLE
            Handler().postDelayed({
                GetMethod(
                    this,
                    adapter,
                    progress
                ).execute("https://637b0fb110a6f23f7f9e75a3.mockapi.io/contacts/list")
            }, 500)
        }
    }
}

class GetMethod(
    private val context: Context,
    private val adapter: ContactAdapter,
    private val progress: ProgressBar
) : AsyncTask<String, Void, String>() {
    private var server_response: String? = null
    private var exception: java.lang.Exception? = null

    override fun doInBackground(vararg strings: String?): String? {

        val url: URL
        var urlConnection: HttpURLConnection?
        try {
            url = URL(strings[0])
            urlConnection = url.openConnection() as HttpURLConnection
            val responseCode: Int = urlConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                server_response = readStream(urlConnection.inputStream)
                Log.v("CatalogClient", server_response.toString())
            }
        } catch (e: MalformedURLException) {
            exception = e
        } catch (e: IOException) {
            exception = e
        }
        return server_response
    }

    override fun onPostExecute(s: String?) {
        super.onPostExecute(s)
        progress.visibility = View.GONE
        if (server_response != null) {
            val jsonArray = JSONArray(s)
            val data = mutableListOf<Contact>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                data.add(
                    Contact(
                        jsonObject.getString("name"),
                        jsonObject.getString("phone")
                    )
                )
            }
            adapter.submitList(data)
        } else if (exception != null) {
            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(exception!!.message)
                .setPositiveButton("OK") { d, _ ->
                    d.dismiss()
                }
                .show()
        }
        Log.d("RRR", s.toString())
    }

}


// Converting InputStream to String
private fun readStream(`in`: InputStream): String {
    var reader: BufferedReader? = null
    val response = StringBuffer()
    try {
        reader = BufferedReader(InputStreamReader(`in`))
        var line: String? = ""
        while (reader.readLine().also { line = it } != null) {
            response.append(line)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        if (reader != null) {
            try {
                reader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    return response.toString()
}