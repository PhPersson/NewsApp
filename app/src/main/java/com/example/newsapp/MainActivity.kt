package com.example.newsapp

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.newsapp.databinding.ActivityMainBinding

class zMainActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) return

        supportFragmentManager.commit {
            add<HomeFragment>(R.id.container, null)
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.action_fav -> goToFavorites()
                R.id.action_news -> goToNews()
                R.id.action_search -> goToSearch()
                else -> false
            }
        }
    }

    private fun goToNews(): Boolean {
        supportFragmentManager.commit {
            replace<NewsFragment>(R.id.container, null, null)
        }
        return true
    }

    private fun goToSearch(): Boolean {
        supportFragmentManager.commit {
            replace<SearchFragment>(R.id.container,null,null)
        }
        return true
    }


    private fun goToFavorites(): Boolean {
        supportFragmentManager.commit {
            replace<FavoritesFragment>(R.id.container, null, null)
        }
        return true
    }
}