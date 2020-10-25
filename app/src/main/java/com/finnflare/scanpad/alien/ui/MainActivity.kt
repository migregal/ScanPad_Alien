package com.finnflare.scanpad.alien.ui

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alien.barcode.BarcodeReader
import com.alien.common.KeyCode.ALR_H450
import com.finnflare.scanpad.alien.R
import com.finnflare.scanpad.alien.UIViewModel
import com.finnflare.scanpad.alien.adapter.ScanResAdapter
import com.finnflare.scanpad.alien.ui.drawer_navigation.DrawerNavigationListener
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.inject

@ObsoleteCoroutinesApi
class MainActivity : AppCompatActivity(), KoinComponent {

    private val viewModel by inject<UIViewModel>()

    private lateinit var mAdapter: ScanResAdapter

    private lateinit var barcodeReader: BarcodeReader

    private lateinit var scanResCount: TextView

    override fun onStart() {
        super.onStart()
        barcodeReader = BarcodeReader(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolBar()

        configureDrawerNavigation()

        requestedOrientation = if (Build.MODEL.startsWith("VM1A")) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        configureRecyclerView()

        viewModel.scanResults.observe(this@MainActivity, Observer {
            mAdapter.changeData(it)
        })

        viewModel.resultsFileSaved.observe(this, Observer {
            if (!it)
                return@Observer

            Toast.makeText(this, getString(R.string.toast_msg_file_saved), Toast.LENGTH_SHORT)
                .show()

            viewModel.resultsFileSaved.postValue(false)
        })
    }

    private fun configureDrawerNavigation() {
        val drawerNavigationView = findViewById<NavigationView>(R.id.nav_view_items)
        drawerNavigationView.setNavigationItemSelectedListener(DrawerNavigationListener(this))
    }

    private fun configureToolBar() {
        setSupportActionBar(findViewById(R.id.toolbar))

        scanResCount = findViewById(R.id.scan_results_count)
        viewModel.scanResultCount.observe(this, Observer {
            scanResCount.text = it.toString()
        })

        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun configureBarcodeReader() {
        barcodeReader.setAllSymbologies(false)
        barcodeReader.setParameter(3, 1)
        barcodeReader.setParameter(292, 1)
    }

    private fun configureRecyclerView() {
        val layoutManager =
            GridLayoutManager(
                this@MainActivity,
                resources.getInteger(R.integer.order_columns_num)
            )
        val recyclerView = findViewById<RecyclerView>(R.id.scanResRecycler)

        mAdapter = ScanResAdapter(mutableListOf())
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                layoutManager.orientation
            )
        )

        viewModel.getScanResults()
    }

    override fun onResume() {
        super.onResume()

        configureBarcodeReader()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findViewById<DrawerLayout>(R.id.main_activity_layout).openDrawer(
                GravityCompat.START
            )
        }
        return true
    }

    override fun onPause() {
        super.onPause()

        if (barcodeReader.isRunning)
            barcodeReader.stop()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (barcodeReader.isRunning)
            barcodeReader.stop()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.repeatCount != 0 || keyCode != ALR_H450.SCAN)
            return false


        if (barcodeReader.isRunning)
            return true

        configureBarcodeReader()

        barcodeReader.start { scanRes ->
            viewModel.processScanResult(scanRes)
        }

        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.repeatCount != 0 || keyCode != ALR_H450.SCAN)
            return false

        if (barcodeReader.isRunning)
            barcodeReader.stop()

        return true
    }
}