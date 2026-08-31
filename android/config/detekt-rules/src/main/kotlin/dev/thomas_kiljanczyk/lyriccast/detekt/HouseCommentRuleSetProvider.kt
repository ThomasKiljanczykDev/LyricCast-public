package dev.thomas_kiljanczyk.lyriccast.detekt

import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

class HouseCommentRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("comments-house")

    override fun instance(): RuleSet =
        RuleSet(
            ruleSetId,
            listOf(::ExcessiveComment),
        )
}
