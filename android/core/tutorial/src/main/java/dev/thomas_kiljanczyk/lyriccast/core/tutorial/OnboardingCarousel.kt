/*
 * Created by Tomasz Kiljanczyk on 7/28/26, 6:42 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 7/28/26, 5:57 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.tutorial

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.ui.testing.TestTags
import dev.thomas_kiljanczyk.lyriccast.core.ui.util.currentWindowSizeClass
import dev.thomas_kiljanczyk.lyriccast.core.ui.util.isWidthExpanded
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

/**
 * Skippable intro carousel shown once, before the guided tour.
 *
 * Slides reference no real UI,
 * so they cover what the tour cannot reach on a fresh install:
 * setlists in use, live presentation controls, and what casting and sessions each mean.
 *
 * @param onSkip ends the whole flow, tour included:
 *   a Skip that only skipped the slides would not be honouring the tap.
 */
@Composable
fun OnboardingCarousel(
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
    slides: ImmutableList<OnboardingSlide> = onboardingSlides,
) {
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val coroutineScope = rememberCoroutineScope()
    val isExpanded = currentWindowSizeClass().isWidthExpanded()

    BackHandler(enabled = pagerState.currentPage > 0) {
        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
    }

    Surface(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.testTag(TestTags.ONBOARDING_SKIP_BUTTON)
                ) {
                    Text(stringResource(R.string.tutorial_skip))
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag(TestTags.ONBOARDING_PAGER)
            ) { page ->
                OnboardingSlideContent(slide = slides[page], isExpanded = isExpanded)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PageIndicator(
                    pageCount = slides.size,
                    progress = {
                        pagerState.currentPage + pagerState.currentPageOffsetFraction
                    }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        enabled = pagerState.currentPage > 0,
                        modifier = Modifier.testTag(TestTags.ONBOARDING_PREVIOUS_BUTTON)
                    ) {
                        Text(stringResource(R.string.tutorial_previous))
                    }

                    val isLast = pagerState.currentPage >= slides.size - 1
                    val labelFadeSpec = MaterialTheme.motionScheme.fastEffectsSpec<Float>()
                    val labelSizeSpec = MaterialTheme.motionScheme.fastSpatialSpec<IntSize>()
                    Button(
                        onClick = {
                            if (isLast) {
                                onComplete()
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        modifier = Modifier.testTag(TestTags.ONBOARDING_NEXT_BUTTON)
                    ) {
                        // Animated so the button width does not snap on the last slide.
                        AnimatedContent(
                            targetState = isLast,
                            transitionSpec = {
                                fadeIn(labelFadeSpec) togetherWith fadeOut(labelFadeSpec) using
                                    SizeTransform { _, _ -> labelSizeSpec }
                            },
                            label = "nextLabel"
                        ) { last ->
                            Text(
                                stringResource(
                                    if (last) {
                                        R.string.tutorial_get_started
                                    } else {
                                        R.string.tutorial_next
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingSlideContent(slide: OnboardingSlide, isExpanded: Boolean) {
    val icon = @Composable {
        Icon(
            imageVector = slide.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(if (isExpanded) 180.dp else 128.dp)
        )
    }
    val text = @Composable {
        Column(
            horizontalAlignment = if (isExpanded) {
                Alignment.Start
            } else {
                Alignment.CenterHorizontally
            }
        ) {
            Text(
                text = stringResource(slide.titleRes),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = if (isExpanded) TextAlign.Start else TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(slide.bodyRes),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = if (isExpanded) TextAlign.Start else TextAlign.Center
            )
        }
    }

    if (isExpanded) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(48.dp, Alignment.CenterHorizontally)
        ) {
            icon()
            Box(Modifier.widthIn(max = 480.dp)) { text() }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(Modifier.height(40.dp))
            text()
        }
    }
}

@PreviewLightDark
@Composable
private fun OnboardingCarouselPreview() {
    LyricCastTheme {
        OnboardingCarousel(onComplete = {}, onSkip = {})
    }
}
