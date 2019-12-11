package com.medialink.deco15backgroundthread

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), MyAsyncCallback {

    companion object {
        private const val INPUT_STRING = "hello ini demo async task"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val demoAsnyc = DemoAsnyc(this)
        demoAsnyc.execute(INPUT_STRING)*/

        GlobalScope.launch(Dispatchers.IO) {
            val input = INPUT_STRING
            var output: String? = null

            Log.d("debug", "status: do in background")
            try {
                output = "$input selamat belajar"
                delay(2000)
                withContext(Dispatchers.Main) {
                    tv_status.text = "status post"
                    tv_desc.text = output
                }
            } catch (e: Exception) {
                Log.d("debug", e.message.toString())
            }
        }
    }

    override fun onPreExectue() {
        tv_status.setText("pre execute")
        tv_desc.text = INPUT_STRING
    }

    override fun onPostExecute(text: String) {
        tv_status.text = "post execute"
        tv_desc.text = text
    }

    private class DemoAsnyc(val myListener: MyAsyncCallback): AsyncTask<String, Void, String>() {
        companion object {
            private val LOG_ASYNC = "demoasync"
        }
        private val myFuck: WeakReference<MyAsyncCallback>
        init {
            this.myFuck = WeakReference(myListener)
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d(LOG_ASYNC, "status: pre execute")
            val myListener = myFuck.get()
            myListener?.onPreExectue()
        }

        override fun doInBackground(vararg params: String?): String {
            Log.d(LOG_ASYNC, "status: do in backgroudn")
            var output: String? = null
            try {
                val input = params[0]
                output = "$input selamat belajar"
                Thread.sleep(2000)
            } catch (e: Exception) {
                Log.d(LOG_ASYNC, e.message)
            }

            return output.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.d(LOG_ASYNC, "status: on post execute")
            val myListener = this.myFuck.get()
            result?.let { myListener?.onPostExecute(it) }
        }
    }

}

internal interface MyAsyncCallback {
    fun onPreExectue()
    fun onPostExecute(text: String)
}
