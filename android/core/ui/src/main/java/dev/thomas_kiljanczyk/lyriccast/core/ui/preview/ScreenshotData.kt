package dev.thomas_kiljanczyk.lyriccast.core.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import dev.thomas_kiljanczyk.lyriccast.core.ui.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * The demo dataset behind the rendered README screenshots (`tools:readme-screenshots`). It lives
 * here, next to [PreviewData], because a `screenshotTest` source set is not a dependable target:
 * nothing outside that module can depend on it, so the one dataset has to sit in a normal one.
 *
 * Titles and category names come from resources so a `@Preview(locale = "pl")` render translates
 * the *data*, not just the UI chrome. Lyrics stay as [PreviewData.sampleLyrics] (Lorem ipsum is
 * language-neutral).
 */
class ScreenshotData(
    val categories: ImmutableList<CategoryItem?>,
    val songs: ImmutableList<SongItem>,
    val setlistName: String
) {
    val aSong get() = songs[0]
    val awesomeSong get() = songs[1]
    val greatSong get() = songs[2]
}

@Composable
fun rememberScreenshotData(): ScreenshotData {
    val goodStuff = PreviewData.goodStuffCategory.copy(
        name = stringResource(R.string.demo_category_good_stuff)
    )
    val favorites = PreviewData.favoritesCategory.copy(
        name = stringResource(R.string.demo_category_favorites)
    )
    val meh = PreviewData.mehCategory.copy(name = stringResource(R.string.demo_category_meh))

    val songs = listOf(
        PreviewData.aSong.copy(title = stringResource(R.string.demo_song_a), category = goodStuff),
        PreviewData.awesomeSong.copy(
            title = stringResource(R.string.demo_song_awesome),
            category = favorites
        ),
        PreviewData.greatSong.copy(
            title = stringResource(R.string.demo_song_great),
            category = favorites
        ),
        PreviewData.magnificentSong.copy(
            title = stringResource(R.string.demo_song_magnificent),
            category = meh
        ),
        PreviewData.theSong.copy(
            title = stringResource(R.string.demo_song_the),
            category = goodStuff
        )
    ).toImmutableList()

    return ScreenshotData(
        categories = listOf(null, goodStuff, favorites, meh).toImmutableList(),
        songs = songs,
        setlistName = stringResource(R.string.demo_setlist_name)
    )
}
