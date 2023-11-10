package com.free.momobooklet_by_sm.presentation.help

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.free.momobooklet_by_sm.MainActivity
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.databinding.ActivityHelpBinding
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.CommissionViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * An free full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@AndroidEntryPoint
class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding
    private lateinit var myadapter: CommissionChartAdapter
    val mCommissionViewModel: CommissionViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        myadapter = CommissionChartAdapter()




        setQuitButtonOnclick()
        setUpReportsViews()
        setUpCommissionViews()
        setUpCommissionTable()
        setUpAccounts()
        setUpContactPoints()

        setContentView(binding.root)
    }




    private fun setUpAccounts() {
        binding.mainHelpAccounts.apply {
            helpPageSubsectionTitleTextview.text = getString(R.string.accounts)
            helpPageSubsectionBody.text = getString(R.string.account_help)
            helpPageSubsectionTitleImageview.setImageResource(R.drawable.user_account_icon)
        }
    }

    private fun setUpCommissionViews() {
        binding.mainHelpCommission.apply {
            helpPageSubsectionTitleTextview.text = getString(R.string.commission)
            helpPageSubsectionBody.text = getString(R.string.commmission_help)
            helpPageSubsectionTitleImageview.setImageResource(R.drawable.attach_money)
        }
    }



    private fun setUpCommissionChart() {
        mCommissionViewModel.commissionChart.observe(this) {
            myadapter.differ.submitList(it)

        }
    }


    private fun setUpCommissionTable() {
        setUpCommissionChart()

        binding.commissionChartTableContentRv.apply{
            this.adapter = myadapter
            this.layoutManager = LinearLayoutManager(this.context)
        }

    }




    private fun setUpReportsViews() {
        binding.mainHelpReports.apply {
            helpPageSubsectionTitleTextview.text = getString(R.string.reports)
            helpPageSubsectionBody.text = getString(R.string.reports_help)
            helpPageSubsectionTitleImageview.setImageResource(R.drawable.swap_icon)
        }

    }
    private fun setQuitButtonOnclick() {
        binding.mainHelpQuit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setUpContactPoints() {
        binding.helpContactUsPhone.setOnClickListener{
            performPhoneCall(Constants.MYNUMBER)
        }
        binding.helpContactUsWhatsapp.setOnClickListener{
            openWhatsappLink()
        }
    }

    private fun openWhatsappLink() {
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://wa.link/oc1up8"))
        startActivity(intent)
    }

    /**
     * Make a phone call. Send to the phone app
     *
     * @param phoneNumber the phone number to call
     */
    private fun performPhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        try { startActivity(intent)
        } catch (ex: Exception) {
            Toast.makeText(this,"Could'nt  auto place call", Toast.LENGTH_SHORT).show()
        }
    }
}