package com.github.raystatic.commitgeniedevfesttemp

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class CommitMessageAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        Messages.showInfoMessage(project, "Initial setup", "CommitGenie")
    }

}