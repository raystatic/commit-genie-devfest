package com.github.raystatic.commitgeniedevfesttemp

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.changes.ChangeListManager
import java.io.File

class CommitMessageAction : AnAction() {

    private val TAG = "Commit Genie"

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return


        // fetch git root
        val gitRoot = ProjectLevelVcsManager.getInstance(project).allVcsRoots.firstOrNull()?.path?.path
        if (gitRoot.isNullOrEmpty()) {
            Messages.showErrorDialog(project, "Could not determine Git root.", TAG)
            return
        }

        // get git diff
        val diffSummary = getGitDiff(gitRoot)
        println("diff: $diffSummary")
        if (diffSummary.isEmpty()) {
            Messages.showErrorDialog(project, "No staged changes found", TAG)
            return
        }

    }

    private fun getGitDiff(gitRoot: String): String {
        return try {
            val changes = ProcessBuilder("git", "diff")
                .directory(File(gitRoot))
                .start()
                .inputStream.bufferedReader().readText()

            val cachedChanges = ProcessBuilder("git", "diff", "--cached")
                .directory(File(gitRoot))
                .start()
                .inputStream.bufferedReader().readText()

            changes + "\n" + cachedChanges
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

}