package com.food.ordering.zinger.seller.ui.contributors

import android.animation.LayoutTransition
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.databinding.ActivityContributorsBinding
import org.koin.android.ext.android.inject

class ContributorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContributorsBinding
    private val preferencesHelper: PreferencesHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setListener()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contributors)
        binding.layoutContent.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun setListener(){
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
        binding.layoutShrikanth.setOnClickListener {
            val intent = Intent(applicationContext,ContributorDetailActivity::class.java)
            intent.putExtra("contributor_id",0)
            startActivity(intent)
        }
        binding.layoutHarsha.setOnClickListener {
            val intent = Intent(applicationContext,ContributorDetailActivity::class.java)
            intent.putExtra("contributor_id",1)
            startActivity(intent)
        }
        binding.layoutLogesh.setOnClickListener {
            val intent = Intent(applicationContext,ContributorDetailActivity::class.java)
            intent.putExtra("contributor_id",2)
            startActivity(intent)
        }
    }

}
