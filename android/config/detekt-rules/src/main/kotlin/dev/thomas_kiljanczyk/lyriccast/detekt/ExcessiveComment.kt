package dev.thomas_kiljanczyk.lyriccast.detekt

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtFile

class ExcessiveComment(config: Config) :
    Rule(
        config,
        "Comment blocks should stay short. A block this long narrates the code instead of " +
            "explaining a non-obvious constraint - trim it to the essential reason or delete it.",
    ) {

    @Configuration("maximum number of lines a single contiguous comment block may span")
    private val maxLines: Int by config(4)

    override fun visitComment(comment: PsiComment) {
        if (!comment.startsCommentBlock() || comment.isLeadingFileHeader()) return

        val lineCount = comment.commentBlockLineCount()
        if (lineCount > maxLines) {
            report(
                Finding(
                    Entity.from(comment),
                    "This comment block spans $lineCount lines (max $maxLines).",
                ),
            )
        }
    }

    private fun PsiComment.startsCommentBlock() = previousNonWhitespaceSibling() !is PsiComment

    private fun PsiComment.isLeadingFileHeader() = parent is KtFile && prevSibling == null

    private fun PsiComment.commentBlockLineCount(): Int {
        var lines = text.lineCount()
        var sibling: PsiElement? = nextSibling
        while (sibling != null) {
            when (sibling) {
                is PsiWhiteSpace -> if (sibling.text.count { it == '\n' } > 1) break
                is PsiComment -> lines += sibling.text.lineCount()
                else -> break
            }
            sibling = sibling.nextSibling
        }
        return lines
    }

    private fun PsiComment.previousNonWhitespaceSibling() =
        generateSequence(prevSibling) { it.prevSibling }.firstOrNull { it !is PsiWhiteSpace }

    private fun String.lineCount() = count { it == '\n' } + 1
}
