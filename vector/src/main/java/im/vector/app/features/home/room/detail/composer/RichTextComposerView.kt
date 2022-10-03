/*
 * Copyright (c) 2022 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.features.home.room.detail.composer

import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.text.toSpannable
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import im.vector.app.R
import im.vector.app.core.extensions.setTextIfDifferent
import im.vector.app.databinding.ComposerRichTextLayoutBinding

class RichTextComposerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), MessageComposer {

    val views: ComposerRichTextLayoutBinding

    override var callback: MessageComposerView.Callback? = null

    private var currentConstraintSetId: Int = -1

    private val animationDuration = 100L

    override val text: Editable?
        get() = views.composerEditText.text
    override val editText: EditText
        get() = views.composerEditText
    override val emojiButton: ImageButton?
        get() = null
    override val sendButton: ImageButton
        get() = views.sendButton
    override val attachmentButton: ImageButton
        get() = views.attachmentButton
    override val composerRelatedMessageActionIcon: ImageView
        get() = views.composerRelatedMessageActionIcon
    override val composerRelatedMessageAvatar: ImageView
        get() = views.composerRelatedMessageAvatar
    override val composerRelatedMessageContent: TextView
        get() = views.composerRelatedMessageContent
    override val composerRelatedMessageImage: ImageView
        get() = views.composerRelatedMessageImage
    override val composerRelatedMessageTitle: TextView
        get() = views.composerRelatedMessageTitle
    override var isVisible: Boolean
        get() = views.root.isVisible
        set(value) { views.root.isVisible = value }

    init {
        inflate(context, R.layout.composer_rich_text_layout, this)
        views = ComposerRichTextLayoutBinding.bind(this)

        collapse(false)

        views.composerEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                callback?.onTextChanged(s)
            }
        })

        views.composerRelatedMessageCloseButton.setOnClickListener {
            collapse()
            callback?.onCloseRelatedMessage()
        }

        views.sendButton.setOnClickListener {
            val textMessage = text?.toSpannable() ?: ""
            callback?.onSendMessage(textMessage)
        }

        views.attachmentButton.setOnClickListener {
            callback?.onAddAttachment()
        }
    }

    override fun collapse(animate: Boolean, transitionComplete: (() -> Unit)?) {
        if (currentConstraintSetId == R.layout.composer_rich_text_layout_constraint_set_compact) {
            // ignore we good
            return
        }
        currentConstraintSetId = R.layout.composer_rich_text_layout_constraint_set_compact
        applyNewConstraintSet(animate, transitionComplete)
    }

    override fun expand(animate: Boolean, transitionComplete: (() -> Unit)?) {
        if (currentConstraintSetId == R.layout.composer_rich_text_layout_constraint_set_expanded) {
            // ignore we good
            return
        }
        currentConstraintSetId = R.layout.composer_rich_text_layout_constraint_set_expanded
        applyNewConstraintSet(animate, transitionComplete)
    }

    override fun setTextIfDifferent(text: CharSequence?): Boolean {
        return views.composerEditText.setTextIfDifferent(text)
    }

    private fun applyNewConstraintSet(animate: Boolean, transitionComplete: (() -> Unit)?) {
        // val wasSendButtonInvisible = views.sendButton.isInvisible
        if (animate) {
            configureAndBeginTransition(transitionComplete)
        }
        ConstraintSet().also {
            it.clone(context, currentConstraintSetId)
            it.applyTo(this)
        }
        // Might be updated by view state just after, but avoid blinks
        // views.sendButton.isInvisible = wasSendButtonInvisible
    }

    private fun configureAndBeginTransition(transitionComplete: (() -> Unit)? = null) {
        val transition = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_SEQUENTIAL
            addTransition(ChangeBounds())
            addTransition(Fade(Fade.IN))
            duration = animationDuration
            addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition) {
                    transitionComplete?.invoke()
                }

                override fun onTransitionResume(transition: Transition) {}

                override fun onTransitionPause(transition: Transition) {}

                override fun onTransitionCancel(transition: Transition) {}

                override fun onTransitionStart(transition: Transition) {}
            })
        }
        TransitionManager.beginDelayedTransition((parent as? ViewGroup ?: this), transition)
    }

    override fun setInvisible(isInvisible: Boolean) {
        this.isInvisible = isInvisible
    }
}
