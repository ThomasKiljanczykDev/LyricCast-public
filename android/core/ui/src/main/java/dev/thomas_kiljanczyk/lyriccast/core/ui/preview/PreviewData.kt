package dev.thomas_kiljanczyk.lyriccast.core.ui.preview

import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.color.BaseColors
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.model.SetlistItem
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList

/**
 * Centralized preview data constants to avoid duplication across Compose preview functions
 */
object PreviewData {

    val hymnsCategory = CategoryItem(Category(name = "Hymns", color = BaseColors.NavajoWhite))
    val contemporaryCategory =
        CategoryItem(Category(name = "Contemporary", color = BaseColors.LightSteelBlue))
    val popCategory = CategoryItem(Category(name = "Pop", color = BaseColors.Tomato))
    val rockCategory = CategoryItem(Category(name = "Rock", color = BaseColors.RoyalBlue))
    val jazzCategory = CategoryItem(Category(name = "Jazz", color = BaseColors.SlateGray))

    val sampleCategories = listOf(
        hymnsCategory,
        contemporaryCategory,
        popCategory,
        rockCategory,
        jazzCategory
    ).toImmutableList()

    val sampleCategoriesWithNull = listOf(
        null,
        *sampleCategories.toTypedArray()
    ).toImmutableList()

    val amazingGrace = SongItem(
        id = UUIDv7.randomUUID(),
        title = "Amazing Grace",
        lyricsMap = persistentMapOf(
            "Verse 1" to "Amazing grace how sweet the sound\nThat saved a wretch like me\n" +
                "I once was lost but now am found\nWas blind but now I see",
            "Verse 2" to "'Twas grace that taught my heart to fear\nAnd grace my fears relieved\n" +
                "How precious did that grace appear\nThe hour I first believed",
            "Chorus" to "Amazing grace how sweet the sound\nThat saved a wretch like me"
        ),
        presentation = persistentListOf("Verse 1", "Chorus", "Verse 2", "Chorus"),
        category = hymnsCategory
    )

    val howGreatThouArt = SongItem(
        id = UUIDv7.randomUUID(),
        title = "How Great Thou Art",
        lyricsMap = persistentMapOf(
            "Verse 1" to "O Lord my God when I in awesome wonder\n" +
                "Consider all the worlds thy hands have made\n" +
                "I see the stars I hear the rolling thunder\nThy power throughout the universe displayed",
            "Chorus" to "Then sings my soul my Savior God to thee\nHow great thou art how great thou art\n" +
                "Then sings my soul my Savior God to thee\nHow great thou art how great thou art",
            "Verse 2" to "When through the woods and forest glades I wander\n" +
                "And hear the birds sing sweetly in the trees\n" +
                "When I look down from lofty mountain grandeur\nAnd hear the brook and feel the gentle breeze"
        ),
        presentation = persistentListOf("Verse 1", "Chorus", "Verse 2", "Chorus"),
        category = hymnsCategory
    )

    val beStillMySoul = SongItem(
        id = UUIDv7.randomUUID(),
        title = "Be Still My Soul",
        lyricsMap = persistentMapOf(
            "Verse 1" to "Be still my soul the Lord is on thy side\n" +
                "Bear patiently the cross of grief or pain\n" +
                "Leave to thy God to order and provide\nIn every change He faithful will remain",
            "Verse 2" to "Be still my soul thy God doth undertake\nTo guide the future as He has the past\n" +
                "Thy hope thy confidence let nothing shake\nAll now mysterious shall be bright at last",
            "Chorus" to "Be still my soul when dearest friends depart\n" +
                "And all is darkened in the vale of tears\n" +
                "Then shalt thou better know His love His heart\nWho comes to soothe thy sorrow and thy fears"
        ),
        presentation = persistentListOf("Verse 1", "Chorus", "Verse 2", "Chorus"),
        category = hymnsCategory
    )

    val sampleSong1 = SongItem(
        id = UUIDv7.randomUUID(),
        title = "Sample Song 1",
        lyricsMap = persistentMapOf(),
        presentation = persistentListOf(),
        category = null
    )

    val sampleSong2 = SongItem(
        id = UUIDv7.randomUUID(),
        title = "Sample Song 2",
        lyricsMap = persistentMapOf(),
        presentation = persistentListOf(),
        category = hymnsCategory
    )

    val sampleSong3 = SongItem(
        id = UUIDv7.randomUUID(),
        title = "Sample Song 3",
        lyricsMap = persistentMapOf(),
        presentation = persistentListOf(),
        category = null
    )

    val sampleSongs = listOf(
        amazingGrace,
        howGreatThouArt,
        beStillMySoul,
        sampleSong1,
        sampleSong2,
        sampleSong3
    ).toImmutableList()

    val sampleSetlists = listOf(
        SetlistItem(
            id = UUIDv7.randomUUID(),
            name = "Sunday Service",
            presentation = listOf(
                amazingGrace,
                howGreatThouArt,
                beStillMySoul,
                sampleSong1,
                sampleSong2
            ).toImmutableList()
        ),
        SetlistItem(
            id = UUIDv7.randomUUID(),
            name = "Youth Night",
            presentation = listOf(sampleSong1, sampleSong2, sampleSong3).toImmutableList()
        ),
        SetlistItem(
            id = UUIDv7.randomUUID(),
            name = "Empty Setlist",
            presentation = persistentListOf()
        )
    ).toImmutableList()

    val categoryColors = listOf(
        BaseColors.Maroon,
        BaseColors.Tomato,
        BaseColors.GoldenRod,
        BaseColors.OliveDrab
    )

    fun createSampleCategoriesForManager(count: Int): List<CategoryItem> = List(count) { index ->
        CategoryItem(
            name = "Category $index",
            color = categoryColors[index % categoryColors.size]
        )
    }

    val sampleSongsWithLyrics = listOf(
        amazingGrace,
        howGreatThouArt.copy(isSelected = true),
        beStillMySoul
    ).toImmutableList()

    // Demo data for the rendered README screenshots. Kept apart from the preview fixtures above:
    // those carry placeholder rows and uncategorised songs, which are fine in the IDE and wrong in
    // a README. Lyrics are Lorem ipsum so they read the same in every locale.
    const val sampleLyrics: String =
        "Lorem ipsum\ndolor sit amet\nconsectetur adipiscing elit\n" +
            "sed do eiusmod tempor incididunt\nut labore et dolore"

    val goodStuffCategory = CategoryItem(Category(name = "GOOD STUFF", color = -12490271))
    val favoritesCategory = CategoryItem(Category(name = "FAVORITES", color = -2448096))
    val mehCategory = CategoryItem(Category(name = "MEH", color = -40121))

    val screenshotCategories = listOf(
        goodStuffCategory,
        favoritesCategory,
        mehCategory
    ).toImmutableList()

    val screenshotCategoriesWithNull = listOf(
        null,
        *screenshotCategories.toTypedArray()
    ).toImmutableList()

    private fun screenshotSong(title: String, category: CategoryItem?) = SongItem(
        id = UUIDv7.randomUUID(),
        title = title,
        lyricsMap = persistentMapOf("Verse 1" to sampleLyrics, "Chorus" to sampleLyrics),
        presentation = persistentListOf("Verse 1", "Chorus"),
        category = category
    )

    val aSong = screenshotSong("A Song", goodStuffCategory)
    val awesomeSong = screenshotSong("Awesome Song", favoritesCategory)
    val greatSong = screenshotSong("Great Song", favoritesCategory)
    val magnificentSong = screenshotSong("Magnificent Song", mehCategory)
    val theSong = screenshotSong("THE Song", goodStuffCategory)

    val screenshotSongs = listOf(
        aSong, awesomeSong, greatSong, magnificentSong, theSong
    ).toImmutableList()

    const val screenshotSetlistName: String = "A Setlist"
}
