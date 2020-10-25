package com.finnflare.scanpad.alien.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.finnflare.scanpad.alien.BuildConfig
import com.finnflare.scanpad.alien.R

class AppInfoDialog : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.app_info_dialog, container, false).apply {
        this.findViewById<TextView>(R.id.app_version_info_field).text = BuildConfig.VERSION_NAME
    }
}