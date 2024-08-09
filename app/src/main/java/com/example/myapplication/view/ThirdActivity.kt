package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.UserAdapter
import com.example.myapplication.databinding.ActivityThirdBinding
import com.example.myapplication.viewmodel.UserViewModel

class ThirdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThirdBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        setupActionBar()
        setupRecyclerView()
        setupSwipeToRefresh()
        observeViewModel()

        userViewModel.fetchUsers()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false)
            setCustomView(R.layout.action_bar)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
            setDisplayHomeAsUpEnabled(true)
        }

        val customTitle = findViewById<TextView>(R.id.title_actionbar)
        customTitle.text = getString(R.string.thirdScreen)
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(mutableListOf()) { selectedUser ->
            val resultIntent = Intent()
            resultIntent.putExtra("SELECTED_USER_NAME", "${selectedUser.firstName} ${selectedUser.lastName}")
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = userAdapter

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, deltaX: Int, deltaY: Int) {
                super.onScrolled(recyclerView, deltaX, deltaY)
                if (deltaY > 0) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                    if (userViewModel.isLoading.value == false && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        userViewModel.loadNextPage()
                    }
                }
            }
        })
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            userViewModel.refresh()
        }
    }

    private fun observeViewModel() {
        userViewModel.users.observe(this, Observer { users ->
            if (users.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyState.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyState.visibility = View.GONE
                if (userViewModel.currentPage == 1) {
                    userAdapter.setUsers(users)
                } else {
                    userAdapter.addUsers(users)
                }
            }
            binding.swipeRefreshLayout.isRefreshing = false
        })

        userViewModel.isLoading.observe(this, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        })

        userViewModel.error.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
