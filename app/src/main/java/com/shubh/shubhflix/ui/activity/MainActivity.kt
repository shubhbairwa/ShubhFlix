package com.shubh.shubhflix.ui.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.Glide
import com.shubh.shubhflix.R
import com.shubh.shubhflix.databinding.ActivityMainBinding
import com.shubh.shubhflix.helper.ApiState

import com.shubh.shubhflix.hilttest.HiltTest
import com.shubh.shubhflix.ui.activity.activity.VideoPlayerScreenActivity
import com.shubh.shubhflix.ui.activity.recycleradapter.VideoAdapter
import com.shubh.shubhflix.ui.activity.viewmodel.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    private val adapter = VideoAdapter()

    private val viewModel: VideoViewModel by viewModels()

    companion object {
        private const val TAG = "MainActivity"
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvVideoList.adapter = adapter

        adapter.setOnItemClickListener { data ->
            val intent = Intent(this, VideoPlayerScreenActivity::class.java).also {
                it.putExtra("url", data.videos.medium.url)
                it.putExtra("duration", data.duration.toString())
                it.putExtra("title", data.user)
                startActivity(it)
            }
        }

        observeList()

        if (viewModel.apiState.value !is ApiState.Success) {
            viewModel.fetchVideos("Android")
        }

//todo fro moving view from one pos to another
      /*  val windowMetrics = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics
        } else {
            TODO("VERSION.SDK_INT < R")
        }
        val bounds = windowMetrics.bounds
        val screenWidth = bounds.width()

        val animator = ObjectAnimator.ofFloat(binding.ivapp, "translationX", screenWidth.toFloat())
        animator.duration = 500 // 500ms duration
        animator.start()*/




        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()){
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        Log.e(TAG, "onTextChanged: ${s.toString()}")
                        searchedVideo(s.toString())
                    }
                }else{
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        Log.e(TAG, "onTextChanged: ${s.toString()}")
                        //searchedVideo(s.toString())
                        viewModel.fetchVideos("anime")
                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

    }

    private fun searchedVideo(searchQuery: String) {
        viewModel.fetchVideos(searchQuery)
    }

    private fun observeList() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.apiState.collectLatest { state ->
                    when (state) {
                        is ApiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvVideoList.visibility = View.GONE
                        }

                        is ApiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvVideoList.visibility = View.VISIBLE
                            adapter.submitList(state.data) // ✅ No need to submit an empty list first
                        }

                        is ApiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvVideoList.visibility = View.GONE
                            Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

}