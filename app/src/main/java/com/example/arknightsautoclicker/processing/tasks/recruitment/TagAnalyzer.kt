package com.example.arknightsautoclicker.processing.tasks.recruitment

import com.example.arknightsautoclicker.processing.ext.norm

class TagAnalyzer(
    private val data: Set<CharTag> = TagData().characters
) {
    val availableTagsNorm: Set<String>
        = data.flatMapTo(mutableSetOf()) { it.tags_norm }
    data class TagCombination(
        val tags: List<String>,
        val characters: List<CharTag>
    ) {
        val minRarity = characters.minOf { it.rarity }
        val minProprity = characters.minOf { it.priority }
        val minRarityCount = characters.count { it.rarity == minRarity }
        // maximize minRarity first, then minimize minRarityCount
        // if equal, minimize number of tags
        private val score
            get() = minProprity * minRarity * 10000 - minRarityCount * 100 - tags.size
        fun max(a: TagCombination) =
            if (score >= a.score) this else a
    }

    private fun getBestCombination(
        tags_norm: List<String>,
        comb: TagCombination,
        ind: Int = 0
    ): TagCombination {
        if (comb.tags.size >= 3) return comb
        var best = comb
        for (i in ind until tags_norm.size) {
            val tag = tags_norm[i]
            val newChars = comb.characters.filter { it.tags_norm.contains(tag) }
            if (newChars.isEmpty()) continue
            var newComb = TagCombination(
                comb.tags + tag,
                newChars
            )
            newComb = getBestCombination(tags_norm, newComb, i + 1)
            best = best.max(newComb)
        }
        return best
    }

    /**
     * @return the best combination of tags
     * @return null if a tag combination guarantees 5* + operator
     * where the user should manually select the operator with a calculator
     */
    fun getBestCombination(inputTag: List<String>): TagCombination {
        val tags = inputTag.map { it.norm }
        val comb = if (tags.any { it == "Top Operator".norm }) {
            val chars = data.filter { it.rarity == 6 }
            val comb = TagCombination(listOf("Top Operator".norm), chars)
            getBestCombination(tags, comb)
        } else {
            val chars = data.filter { it.rarity in 3..5 }
            getBestCombination(tags, TagCombination(listOf(), chars))
        }
        val origTags = comb.tags.map { tag ->
            inputTag.find { it.norm == tag } ?: throw Exception("Should not happen")
        }
        return TagCombination(origTags, comb.characters)
    }
}

