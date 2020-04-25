package com.food.ordering.zinger.seller.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amulyakhare.textdrawable.TextDrawable
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.model.ShopConfigurationModel
import com.food.ordering.zinger.seller.databinding.ActivityHomeBinding
import com.food.ordering.zinger.seller.databinding.BottomSheetAccountSwitchBinding
import com.food.ordering.zinger.seller.databinding.HeaderLayoutBinding
import com.food.ordering.zinger.seller.ui.menu.MenuActivity
import com.food.ordering.zinger.seller.ui.order.OrderViewModel
import com.food.ordering.zinger.seller.ui.orderHistory.OrderHistoryActivity
import com.food.ordering.zinger.seller.ui.profile.ProfileActivity
import com.food.ordering.zinger.seller.ui.shopProfile.ShopProfileActivity
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: OrderViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var headerLayout: HeaderLayoutBinding
    private lateinit var drawer: Drawer
    private lateinit var progressDialog: ProgressDialog
    private lateinit var cartSnackBar: Snackbar
    private lateinit var errorSnackbar: Snackbar
    private var placeId = ""
    private var shopConfig: ShopConfigurationModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        shopConfig = preferencesHelper.getShop()!!
            .filter { it.shopModel.id == preferencesHelper.currentShop }.get(0)

        initView(savedInstanceState)
        setListeners()
        setupMaterialDrawer()
        setObservers()
        viewModel.getOrderByShopId(preferencesHelper.currentShop)
    }

    // This API end point is responsible for inserting the order details. It verifies the availability of all the items in the shop and calculates the total bill
    //  * amount.  After verifying
    private fun initView(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        headerLayout = DataBindingUtil.inflate(
            LayoutInflater.from(applicationContext),
            R.layout.header_layout,
            null,
            false
        )
        cartSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        cartSnackBar.setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.green))
        errorSnackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackbar.view.findViewById(R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        binding.imageMenu.setOnClickListener(this)
        binding.textShopName.text = shopConfig?.shopModel?.name
        binding.textShopRating.text = shopConfig?.ratingModel?.rating.toString()+" ("+shopConfig?.ratingModel?.userCount+")"
        progressDialog = ProgressDialog(this)


        var fragment = Fragment()
        preferencesHelper.role?.let {
            if (it == AppConstants.ROLE.DELIVERY.name) {
                binding.tabs.visibility = View.GONE
                fragment = ReadyFragment()
            } else {
                binding.tabs.visibility = View.VISIBLE
                fragment = NewOrdersFragment()
            }
        }

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
            .commit()

        setStatusBarHeight()

        Picasso.get().load(shopConfig?.shopModel?.photoUrl).placeholder(R.drawable.ic_shop)
            .into(binding.imageCompany)
    }

    private fun setListeners() {
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        val fragment = NewOrdersFragment()
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                            .commit()
                    }
                    1 -> {
                        val fragment = PreparingFragment()
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                            .commit()
                    }
                    2 -> {
                        val fragment = ReadyFragment()
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                            .commit()
                    }
                }
            }
        })

        binding.imageCompany.setOnClickListener { v ->
            showAccountListBottomSheet()
        }
    }

    private fun setStatusBarHeight() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rectangle = Rect()
                val window = window
                window.decorView.getWindowVisibleDisplayFrame(rectangle)
                val statusBarHeight = rectangle.top
                val layoutParams = headerLayout.statusbarSpaceView.layoutParams
                layoutParams.height = statusBarHeight
                headerLayout.statusbarSpaceView.layoutParams = layoutParams
                Log.d("Home", "status bar height $statusBarHeight")
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun updateHeaderLayoutUI() {
        headerLayout.textCustomerName.text = preferencesHelper.name
        headerLayout.textEmail.text = preferencesHelper.email
        val textDrawable = TextDrawable.builder()
            .buildRound(
                preferencesHelper.name?.get(0).toString().capitalize(),
                ContextCompat.getColor(this, R.color.accent)
            )
        headerLayout.imageProfilePic.setImageDrawable(textDrawable)

    }

    private fun setupMaterialDrawer() {
        var identifier = 0L
        val profileItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("My Profile")
            .withIcon(R.drawable.ic_drawer_user)
        val shopProfileItem =
            PrimaryDrawerItem().withIdentifier(++identifier).withName("Shop Profile")
                .withIcon(R.drawable.ic_home)
        val ordersItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Past Orders")
            .withIcon(R.drawable.ic_drawer_past_rides)
        val menuItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Shop Menu")
            .withIcon(R.drawable.ic_drawer_order)
        val contactUsItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Contact Us")
            .withIcon(R.drawable.ic_drawer_mail)
        val signOutItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Sign out")
            .withIcon(R.drawable.ic_drawer_log_out)
        val helpcenter = PrimaryDrawerItem().withIdentifier(++identifier).withName("Help Center")
            .withIcon(R.drawable.ic_drawer_info)



        drawer = DrawerBuilder()
            .withActivity(this)
            .withDisplayBelowStatusBar(false)
            .withHeader(headerLayout.root)
            .withTranslucentStatusBar(true)
            .withCloseOnClick(true)
            .withSelectedItem(-1)
            .addDrawerItems(
                profileItem,
                shopProfileItem,
                ordersItem,
                menuItem,
                helpcenter,
                contactUsItem,
                DividerDrawerItem(),
                signOutItem
            )
            .withOnDrawerItemClickListener { view, position, drawerItem ->
                if (profileItem.identifier == drawerItem.identifier) {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                }
                if (shopProfileItem.identifier == drawerItem.identifier) {
                    startActivity(Intent(applicationContext, ShopProfileActivity::class.java))
                }
                if (ordersItem.identifier == drawerItem.identifier) {
                    startActivity(Intent(applicationContext, OrderHistoryActivity::class.java))
                }
                if (menuItem.identifier == drawerItem.identifier) {
                    startActivity(Intent(applicationContext, MenuActivity::class.java))
                }
                if (helpcenter.identifier == drawerItem.identifier) { //TODO open help activity
                }
                if (contactUsItem.identifier == drawerItem.identifier) { //TODO open contact us activity
                }
                if (signOutItem.identifier == drawerItem.identifier) {
                    MaterialAlertDialogBuilder(this@HomeActivity)
                        .setTitle("Confirm Sign Out")
                        .setMessage("Are you sure want to sign out?")
                        .setPositiveButton("Yes") { dialog, which ->
                            FirebaseAuth.getInstance().signOut()
                            preferencesHelper.clearPreferences()
                            startActivity(Intent(applicationContext, ProfileActivity::class.java))
                            finish()
                        }
                        .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                        .show()
                }
                true
            }
            .build()
    }

    private fun setObservers() {

    }

    lateinit var accountAdapter: AccountAdapter
    private fun showAccountListBottomSheet() {
        val dialogBinding: BottomSheetAccountSwitchBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.bottom_sheet_account_switch,
                null,
                false
            )

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        var accountList: ArrayList<ShopConfigurationModel> = ArrayList()
        preferencesHelper.getShop()?.let {

            accountList.addAll(it)
            for (i in accountList.indices) {
                accountList[i].isSelected =
                    accountList[i].shopModel.id == preferencesHelper.currentShop
            }
        }
        accountAdapter = AccountAdapter(accountList, object : AccountAdapter.OnItemClickListener {
            override fun onItemClick(item: ShopConfigurationModel, position: Int) {
                // todo change the rating of the shop in header layout
                for (i in accountList.indices) {
                    accountList[i].isSelected = false
                }
                accountList[position].isSelected = true
                accountAdapter.notifyDataSetChanged()

                accountList[position].shopModel.id?.let {
                    shopConfig = accountList[position]
                    preferencesHelper.currentShop = it
                    Picasso.get().load(accountList[position].shopModel.photoUrl)
                        .placeholder(R.drawable.ic_shop)
                        .into(binding.imageCompany)
                    binding.textShopName.text = accountList[position].shopModel.name
                    binding.textShopRating.text = accountList[position].ratingModel.rating.toString() + " ("+ accountList[position].ratingModel.userCount+")"
                    this@HomeActivity.recreate()
                    //viewModel.getOrderByShopId(it)
                }

                dialog.dismiss()
            }
        })
        dialogBinding.recyclerAccounts.layoutManager = LinearLayoutManager(this)
        dialogBinding.recyclerAccounts.adapter = accountAdapter
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.image_menu -> {
                drawer.openDrawer()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shopConfig = preferencesHelper.getShop()!!.filter { it.shopModel.id == preferencesHelper.currentShop }.get(0)
        binding.textShopName.text = shopConfig?.shopModel?.name
        binding.textShopRating.text = shopConfig?.ratingModel?.rating.toString()+" ("+shopConfig?.ratingModel?.userCount+")"
        updateHeaderLayoutUI()
    }


    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this@HomeActivity)
            .setTitle("Exit app?")
            .setMessage("Are you sure want to exit the app?")
            .setPositiveButton("Yes") { dialog, which ->
                super.onBackPressed()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
            .show()
    }
}
