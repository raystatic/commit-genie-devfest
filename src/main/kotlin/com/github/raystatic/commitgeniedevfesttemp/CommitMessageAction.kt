package com.github.raystatic.commitgeniedevfesttemp

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.ProjectLevelVcsManager

class CommitMessageAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return


        // fetch git root
        val gitRoot = ProjectLevelVcsManager.getInstance(project).allVcsRoots.firstOrNull()?.path?.path
        if (gitRoot.isNullOrEmpty()) {
            Messages.showErrorDialog(project, "Could not determine Git root.", "Commit Genie")
            return
        }

    }

}