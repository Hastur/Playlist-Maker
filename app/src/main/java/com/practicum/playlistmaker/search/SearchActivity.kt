package com.practicum.playlistmaker.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }

    lateinit var searchField: EditText
    lateinit var clearButton: ImageView
    lateinit var rwTrackList: RecyclerView
    var searchQuery: String? = null
    val tracks = mutableListOf<Track>()
    val adapter = SearchAdapter()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val searchService = retrofit.create(SearchApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Toolbar>(R.id.toolbar_search).setNavigationOnClickListener {
            this.finish()
        }

        searchField = findViewById<EditText>(R.id.search_input)
        clearButton = findViewById<ImageView>(R.id.search_clear)

        clearButton.setOnClickListener {
            searchField.setText("")
            val inputManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(searchField.windowToken, 0)
            searchField.clearFocus()
            rwTrackList.isVisible = false
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //empty
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearButton.isVisible = !searchField.text.isNullOrEmpty()
            }

            override fun afterTextChanged(p0: Editable?) {
                searchQuery = searchField.text.toString()
            }
        }
        searchField.addTextChangedListener(textWatcher)

        rwTrackList = findViewById<RecyclerView>(R.id.track_list)
        rwTrackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter.trackList = tracks
        rwTrackList.adapter = adapter

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadData()
                true
            }
            false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchField.setText(savedInstanceState.getString(SEARCH_TEXT))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadData() {
        if (!searchQuery.isNullOrEmpty()) {
            searchService.searchTrack(searchQuery!!)
                .enqueue(object : Callback<SearchResponse> {
                    override fun onResponse(
                        call: Call<SearchResponse?>,
                        response: Response<SearchResponse?>
                    ) {
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results ?: listOf())
                                adapter.notifyDataSetChanged()
                                rwTrackList.layoutManager?.smoothScrollToPosition(
                                    rwTrackList, null, 0
                                )
                                toggleEmptyScreen(null, null)
                            } else toggleEmptyScreen(MessageTypes.NothingFound, null)
                        } else toggleEmptyScreen(
                            MessageTypes.NoInternet,
                            response.code().toString()
                        )
                    }

                    override fun onFailure(
                        call: Call<SearchResponse?>,
                        error: Throwable
                    ) {
                        toggleEmptyScreen(MessageTypes.NoInternet, error.message.toString())
                    }
                })
        }
    }

    private fun toggleEmptyScreen(type: MessageTypes?, error: String?) {
        val shouldShow = type != null

        findViewById<LinearLayout>(R.id.empty_screen).isVisible = shouldShow
        rwTrackList.isVisible = !shouldShow

        if (shouldShow) {
            findViewById<ImageView>(R.id.empty_screen_image).setImageResource(type.imageId)
            findViewById<TextView>(R.id.empty_screen_text).setText(type.messageId)

            val buttonRetry = findViewById<MaterialButton>(R.id.empty_screen_button)
            buttonRetry.isVisible = type == MessageTypes.NoInternet
            buttonRetry.setOnClickListener {
                loadData()
            }

            if (error != null) Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
        }
    }

    enum class MessageTypes(val imageId: Int, val messageId: Int) {
        NothingFound(R.drawable.ic_nothing_found, R.string.search_nothing_found),
        NoInternet(R.drawable.ic_no_internet, R.string.search_no_internet);
    }
}