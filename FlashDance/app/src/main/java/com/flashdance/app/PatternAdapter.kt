package com.flashdance.app

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PatternAdapter(
    private var patterns: List<DancePattern>,
    private val onClick: (DancePattern) -> Unit
) : RecyclerView.Adapter<PatternAdapter.PatternViewHolder>() {

    private var runningPatternId: Int? = null

    fun submitList(newPatterns: List<DancePattern>) {
        patterns = newPatterns
        notifyDataSetChanged()
    }

    fun setRunningPatternId(id: Int?) {
        runningPatternId = id
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatternViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pattern, parent, false)
        return PatternViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatternViewHolder, position: Int) {
        val pattern = patterns[position]
        holder.bind(pattern, pattern.id == runningPatternId, onClick)
    }

    override fun getItemCount(): Int = patterns.size

    override fun onViewRecycled(holder: PatternViewHolder) {
        holder.recycle()
    }

    class PatternViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView: TextView = itemView.findViewById(R.id.patternName)
        private val dotView: View = itemView.findViewById(R.id.statusDot)
        private var pulseAnimator: ObjectAnimator? = null

        fun bind(pattern: DancePattern, isRunning: Boolean, onClick: (DancePattern) -> Unit) {
            nameView.text = pattern.name
            itemView.setBackgroundResource(if (isRunning) R.drawable.bg_card_running else R.drawable.bg_card)
            dotView.setBackgroundResource(if (isRunning) R.drawable.dot_running else R.drawable.dot_idle)

            stopPulse()
            if (isRunning) startPulse()

            itemView.setOnClickListener { onClick(pattern) }
        }

        private fun startPulse() {
            pulseAnimator = ObjectAnimator.ofFloat(dotView, View.ALPHA, 1f, 0.35f, 1f).apply {
                duration = 900
                repeatCount = ObjectAnimator.INFINITE
                start()
            }
        }

        private fun stopPulse() {
            pulseAnimator?.cancel()
            pulseAnimator = null
            dotView.alpha = 1f
        }

        fun recycle() {
            stopPulse()
        }
    }
}
