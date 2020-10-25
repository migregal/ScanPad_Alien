package com.finnflare.scanpad.alien.ui.drawer_navigation

import android.content.Context
import android.os.Environment
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.finnflare.scanpad.alien.R
import com.finnflare.scanpad.alien.UIViewModel
import com.finnflare.scanpad.alien.ui.dialog.AppInfoDialog
import com.google.android.material.navigation.NavigationView
import com.obsez.android.lib.filechooser.ChooserDialog
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.inject

@ObsoleteCoroutinesApi
class DrawerNavigationListener(private val context: Context) :
    NavigationView.OnNavigationItemSelectedListener, KoinComponent {

    private val viewModel by inject<UIViewModel>()

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.d_nav_save_to_file ->
                AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.alert_dialog_save))
                    .setPositiveButton(context.getString(R.string.alert_dialog_yes)) { dialog, _ ->
                        dialog.cancel()
                        viewModel.saveResultsToFile(
                            Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                .absolutePath
                        )
                    }
                    .setNegativeButton(context.getString(R.string.alert_dialog_no)) { dialog, _ ->
                        dialog.cancel()
                    }
                    .create().show()
            R.id.d_nav_upload_from_file ->
                AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.alert_dialog_upload))
                    .setPositiveButton(context.getString(R.string.alert_dialog_yes)) { dialog, _ ->
                        dialog.cancel()
                        ChooserDialog(context)
                            .withFilter(false, false, "txt", "json")
                            .displayPath(true)
                            .withStartFile(
                                Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .absolutePath
                            )
                            .enableOptions(true)
                            .withChosenListener { _, pathFile ->
                                viewModel.uploadResultsFromFile(pathFile)
                            }
                            .cancelOnTouchOutside(true)
                            .build()
                            .show()
                    }
                    .setNegativeButton(context.getString(R.string.alert_dialog_no)) { dialog, _ ->
                        dialog.cancel()
                    }
                    .create().show()

            R.id.d_nav_clear_results ->
                AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.alert_dialog_reset))
                    .setPositiveButton(context.getString(R.string.alert_dialog_yes)) { dialog, _ ->
                        dialog.cancel()
                        viewModel.clearScanResults()
                    }
                    .setNegativeButton(context.getString(R.string.alert_dialog_no)) { dialog, _ ->
                        dialog.cancel()
                    }
                    .create().show()

            R.id.d_nav_about_app -> {
                val fm = (context as AppCompatActivity).supportFragmentManager
                val dialogFragment = AppInfoDialog()
                dialogFragment.show(fm, "dialog_fragment_info")
            }
        }

        return true
    }
}