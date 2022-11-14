package com.lib.app_manager.adapters

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lib.app_manager.R
import com.lib.app_manager.model.UninstallAppModel
import com.lib.app_manager.providers.AppsProvider

class UninstallAppsRecyclerAdapter(
    private val appsList: List<UninstallAppModel>,
    private val onItemClicked: (UninstallAppModel) -> Unit,
    private val itemLayout: Int
) :
    RecyclerView.Adapter<UninstallAppsRecyclerAdapter.ViewHolder>() {

    var lastClikedPosition: Int = 0

    class ViewHolder(itemView: View, onItemClicked: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        var appIcon: ImageView? = null
        var appTitle: TextView? = null
        var appSize: TextView? = null
        var buttonUninstall: FrameLayout? = null

        init {
            appIcon = itemView.findViewById(R.id.app_icon)
            appTitle = itemView.findViewById(R.id.app_title)
            appSize = itemView.findViewById(R.id.memory_text)
            buttonUninstall = itemView.findViewById<FrameLayout?>(R.id.button_uninstall).apply {
                setOnClickListener { onItemClicked(adapterPosition) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(itemLayout, parent, false)
        return ViewHolder(itemView) {
            onItemClicked(appsList[it])
            lastClikedPosition = it
        }
    }

    fun removeItem(position: Int) {
        (appsList as ArrayList).remove(appsList[position])
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val icon = holder.appIcon
        val title = holder.appTitle
        val size = holder.appSize

        val app = appsList[position]

        icon?.setImageDrawable(app.icon)
        title?.text = app.title
        size?.text = AppsProvider.humanReadableByteCountSI(app.size)

    }

    override fun getItemCount() = appsList.size
}

class DividerItemDecorationLastExcluded(private val dividerDrawable: Drawable?) :
    RecyclerView.ItemDecoration() {

    private val dividerWidth = dividerDrawable?.intrinsicWidth
    private val dividerHeight = dividerDrawable?.intrinsicHeight

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let {
            // left margin for the divider
            val dividerLeft = 32

            // right margin for the divider with reference to the parent width
            val dividerRight: Int = parent.width - 32

            for (i in 0 until parent.childCount) {
                if (i != parent.childCount - 1) {
                    val child = parent.getChildAt(i)

                    val params = child.layoutParams as RecyclerView.LayoutParams

                    // calculating the distance of the divider to be drawn from the top
                    val dividerTop: Int = child.bottom + params.bottomMargin
                    val dividerBottom: Int = dividerTop + (dividerDrawable?.intrinsicHeight ?: 0)

                    dividerDrawable?.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                    dividerDrawable?.draw(canvas)
                }
            }
        }
    }
}