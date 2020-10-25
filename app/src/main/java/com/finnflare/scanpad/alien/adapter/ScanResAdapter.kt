package com.finnflare.scanpad.alien.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finnflare.scanpad.alien.R

class ScanResAdapter(values: List<RecyclerItem>) :
    RecyclerView.Adapter<ScanResAdapter.ViewHolder>() {
    private var mValues = values

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.scan_res_elem, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mValues.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scanRes = mValues[position]

        holder.resTextView.text = scanRes.scanResult
        holder.countTextView.text = scanRes.scanCount.toString()
    }

    fun changeData(newValues: List<RecyclerItem>) {
        mValues = newValues
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resTextView: TextView = view.findViewById(R.id.scanResText)
        val countTextView: TextView = view.findViewById(R.id.scanResCount)
    }
}