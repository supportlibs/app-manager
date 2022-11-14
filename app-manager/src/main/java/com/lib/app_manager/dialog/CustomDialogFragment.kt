package com.lib.app_manager.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.lib.app_manager.R
import kotlinx.android.synthetic.main.custom_dialog.*

class CustomDialogFragment(
    private val title: String,
    private val message: String,
    private val buttonText: String,
    private val buttonSrc: Int,
    private val lottieRawRes: Int
) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.custom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        massage_title.text = title
        info.text = message
        btn_yes.text = buttonText
        btn_yes.background = ContextCompat.getDrawable(requireContext(), buttonSrc)
        img.setAnimation(lottieRawRes)
        btn_close.setOnClickListener {
            dismiss()
        }
        btn_yes.setOnClickListener {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            intent.data = Uri.fromParts("package", requireContext().packageName, null)
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                requireContext().startActivity(intent)
            } else {
                requireContext().startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
            dismiss()
        }
    }

    class Builder {

        private var title: String? = null
        private var message: String? = null
        private var buttonText: String? = null
        private var buttonSrc: Int? = null
        private var lottieRawRes: Int? = null

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        fun setButtonText(buttonText: String): Builder {
            this.buttonText = buttonText
            return this
        }

        fun setButtonSrc(buttonSrc: Int): Builder {
            this.buttonSrc = buttonSrc
            return this
        }

        fun setLottieRawRes(lottieRawRes: Int): Builder {
            this.lottieRawRes = lottieRawRes
            return this
        }

        fun build(): CustomDialogFragment = CustomDialogFragment(title!!, message!!, buttonText!!, buttonSrc!!, lottieRawRes!!)
    }
}