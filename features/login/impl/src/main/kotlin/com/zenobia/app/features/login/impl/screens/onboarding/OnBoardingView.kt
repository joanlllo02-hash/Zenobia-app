/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.login.impl.R
import com.zenobia.app.features.login.impl.login.LoginModeView
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.theme.components.Button
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.IconSource
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.designsystem.theme.components.TextButton
import com.zenobia.app.libraries.matrix.api.auth.OAuthDetails
import com.zenobia.app.libraries.testtags.TestTags
import com.zenobia.app.libraries.testtags.testTag
import com.zenobia.app.libraries.ui.strings.CommonStrings
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnBoardingView(
    state: OnBoardingState,
    onBackClick: () -> Unit,
    onDeveloperSettingsClick: () -> Unit,
    onSignInWithQrCode: () -> Unit,
    onSignIn: (mustChooseAccountProvider: Boolean) -> Unit,
    onCreateAccount: () -> Unit,
    onOAuthDetails: (OAuthDetails) -> Unit,
    onNeedLoginPassword: () -> Unit,
    onLearnMoreClick: () -> Unit,
    onCreateAccountContinue: (url: String) -> Unit,
    onReportProblem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = state.step,
        modifier = modifier,
        transitionSpec = {
            when {
                targetState == OnBoardingStep.WELCOME && initialState == OnBoardingStep.INTRO_CAROUSEL -> {
                    fadeIn(animationSpec = tween(600, easing = FastOutSlowInEasing)) togetherWith
                        fadeOut(animationSpec = tween(300))
                }
                targetState == OnBoardingStep.INTRO_CAROUSEL -> {
                    fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
                }
                else -> {
                    fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
                }
            }
        },
    ) { step ->
        when (step) {
            OnBoardingStep.INTRO_CAROUSEL -> {
                IntroCarouselView(
                    state = state,
                    onSkip = { state.eventSink(OnBoardingEvents.OnSkipCarousel) },
                    onGetStarted = { state.eventSink(OnBoardingEvents.OnGetStarted) },
                    onPageChange = { state.eventSink(OnBoardingEvents.OnCarouselSlideChange(it)) },
                )
            }
            OnBoardingStep.WELCOME -> {
                PremiumWelcomeView(
                    state = state,
                    onBackClick = onBackClick,
                    onDeveloperSettingsClick = onDeveloperSettingsClick,
                    onSignInWithQrCode = onSignInWithQrCode,
                    onSignIn = onSignIn,
                    onCreateAccount = onCreateAccount,
                    onOAuthDetails = onOAuthDetails,
                    onNeedLoginPassword = onNeedLoginPassword,
                    onLearnMoreClick = onLearnMoreClick,
                    onCreateAccountContinue = onCreateAccountContinue,
                    onReportProblem = onReportProblem,
                )
            }
        }
    }
}

@Composable
private fun IntroCarouselView(
    state: OnBoardingState,
    onSkip: () -> Unit,
    onGetStarted: () -> Unit,
    onPageChange: (Int) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = state.currentSlideIndex,
        pageCount = { state.slideCount },
    )

    LaunchedEffect(pagerState.currentPage) {
        onPageChange(pagerState.currentPage)
    }

    val isLastSlide = pagerState.currentPage == state.slideCount - 1
    val currentSlide = state.slides.getOrNull(pagerState.currentPage)
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(ZenobiaTheme.colors.bgCanvasDefault)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.TopEnd,
            ) {
                if (!isLastSlide) {
                    TextButton(
                        text = "تخطي",
                        onClick = onSkip,
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                pageSpacing = 0.dp,
            ) { page ->
                val slide = state.slides[page]
                IntroSlidePage(
                    slide = slide,
                    pageIndex = page,
                    currentPage = pagerState.currentPage,
                    pageOffset = pagerState.currentPageOffsetFraction,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PageIndicator(
                    currentPage = pagerState.currentPage,
                    totalPages = state.slideCount,
                    activeColor = Color(state.slides.getOrNull(pagerState.currentPage)?.accentColor ?: 0xFF0DBDA8),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    text = if (isLastSlide) "ابدأ الآن" else "التالي",
                    leadingIcon = IconSource.Vector(CompoundIcons.ChevronLeft()),
                    onClick = {
                        if (isLastSlide) {
                            onGetStarted()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                )
            }
        }
    }
}

@Composable
private fun IntroSlidePage(
    slide: IntroSlide,
    pageIndex: Int,
    currentPage: Int,
    pageOffset: Float,
) {
    val animatedProgress by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                val alpha = 1f - abs(currentPage - pageIndex) * 0.15f
                this.alpha = alpha.coerceIn(0f, 1f)
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(
                        Color(slide.accentColor).copy(alpha = 0.12f),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val radius = size.minDimension / 2.5f
                    for (i in 0 until 3) {
                        val angle = animatedProgress * 360 + i * 120
                        val r = radius + 20f
                        val px = cx + r * cos(Math.toRadians(angle.toDouble())).toFloat()
                        val py = cy + r * sin(Math.toRadians(angle.toDouble())).toFloat()
                        drawCircle(
                            color = Color(slide.accentColor).copy(alpha = 0.2f),
                            radius = 8f,
                            center = Offset(px, py),
                        )
                    }
                    drawCircle(
                        color = Color(slide.accentColor).copy(alpha = 0.15f),
                        radius = radius + 30f,
                        style = Stroke(width = 2f),
                    )
                    drawCircle(
                        color = Color(slide.accentColor).copy(alpha = 0.08f),
                        radius = radius + 50f,
                        style = Stroke(width = 1.5f),
                    )
                }
                Icon(
                    imageVector = slide.icon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color(slide.accentColor),
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = slide.title,
                style = ZenobiaTheme.typography.fontHeadingXlBold,
                color = ZenobiaTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = slide.description,
                style = ZenobiaTheme.typography.fontBodyLgRegular,
                color = ZenobiaTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
            )
        }
    }
}

@Composable
private fun PageIndicator(
    currentPage: Int,
    totalPages: Int,
    activeColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalPages) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .then(
                        when {
                            isSelected -> Modifier
                                .width(28.dp)
                                .height(8.dp)
                            else -> Modifier
                                .size(8.dp)
                        }
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when {
                            isSelected -> activeColor
                            else -> ZenobiaTheme.colors.textSecondary.copy(alpha = 0.3f)
                        }
                    ),
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PremiumWelcomeView(
    state: OnBoardingState,
    onBackClick: () -> Unit,
    onDeveloperSettingsClick: () -> Unit,
    onSignInWithQrCode: () -> Unit,
    onSignIn: (mustChooseAccountProvider: Boolean) -> Unit,
    onCreateAccount: () -> Unit,
    onOAuthDetails: (OAuthDetails) -> Unit,
    onNeedLoginPassword: () -> Unit,
    onLearnMoreClick: () -> Unit,
    onCreateAccountContinue: (url: String) -> Unit,
    onReportProblem: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedGradientBackground()

        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(800, delayMillis = 200, easing = FastOutSlowInEasing),
            ) + fadeIn(animationSpec = tween(800, delayMillis = 200)),
            exit = fadeOut(),
        ) {
            GlassMorphismLogoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 48.dp)
                    .padding(horizontal = 32.dp),
                state = state,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 340.dp),
        ) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(600, delayMillis = 400, easing = FastOutSlowInEasing),
                ) + fadeIn(animationSpec = tween(600, delayMillis = 400)),
                exit = fadeOut(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                ) {
                    val loginView = @Composable {
                        LoginModeView(
                            loginMode = state.loginMode,
                            onClearError = {
                                state.eventSink(OnBoardingEvents.ClearError)
                            },
                            onLearnMoreClick = onLearnMoreClick,
                            onOAuthDetails = onOAuthDetails,
                            onNeedLoginPassword = onNeedLoginPassword,
                            onCreateAccountContinue = onCreateAccountContinue,
                        )
                    }

                    if (state.loginMode !is AsyncData.Uninitialized) {
                        loginView()
                    }

                    WelcomeActionButtons(
                        state = state,
                        onSignInWithQrCode = onSignInWithQrCode,
                        onSignIn = onSignIn,
                        onCreateAccount = onCreateAccount,
                        onReportProblem = onReportProblem,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            if (state.showBackButton) {
                TextButton(
                    text = stringResource(CommonStrings.action_back),
                    leadingIcon = IconSource.Vector(CompoundIcons.ChevronLeft()),
                    onClick = { state.eventSink(OnBoardingEvents.OnBackToCarousel) },
                    modifier = Modifier.align(Alignment.TopStart),
                )
            }
            if (state.showDeveloperSettings) {
                TextButton(
                    text = stringResource(CommonStrings.common_developer_options),
                    leadingIcon = IconSource.Vector(CompoundIcons.SettingsSolid()),
                    onClick = onDeveloperSettingsClick,
                    modifier = Modifier.align(Alignment.TopEnd),
                )
            }
        }
    }
}

@Composable
private fun AnimatedGradientBackground() {
    val infiniteTransition = rememberInfiniteTransition()
    val anim1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gradientBrush = ShaderBrush(
                LinearGradientShader(
                    from = Offset(size.width * anim1, 0f),
                    to = Offset(size.width * (1f - anim1), size.height),
                    colors = listOf(
                        Color(0xFF0D5CBD).copy(alpha = 0.25f),
                        Color(0xFF0DBDA8).copy(alpha = 0.15f),
                        Color(0xFF8B5CF6).copy(alpha = 0.1f),
                        Color(0xFF0D5CBD).copy(alpha = 0.2f),
                    ),
                )
            )
            drawRect(brush = gradientBrush, size = size)
        }

        ParticleNetworkBackground(
            modifier = Modifier.fillMaxSize(),
            nodeCount = 20,
            connectionDistance = 180f,
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val overlayBrush = ShaderBrush(
                LinearGradientShader(
                    from = Offset(0f, size.height * 0.7f),
                    to = Offset(0f, size.height),
                    colors = listOf(
                        Color.Transparent,
                        ZenobiaTheme.colors.bgCanvasDefault,
                    ),
                )
            )
            drawRect(brush = overlayBrush, size = size)
        }
    }
}

@Composable
private fun GlassMorphismLogoCard(
    modifier: Modifier,
    state: OnBoardingState,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                ZenobiaTheme.colors.bgCanvasDefault.copy(alpha = 0.7f),
                RoundedCornerShape(28.dp),
            )
            .padding(1.dp)
            .clip(RoundedCornerShape(27.dp))
            .background(
                ZenobiaTheme.colors.bgCanvasDefault.copy(alpha = 0.5f),
                RoundedCornerShape(27.dp),
            )
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state.onBoardingLogoResId != null) {
                Image(
                    painter = painterResource(id = state.onBoardingLogoResId),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit,
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF0DBDA8).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Z",
                        style = ZenobiaTheme.typography.fontHeadingXlBold,
                        fontSize = 36.sp,
                        color = Color(0xFF0DBDA8),
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "زنوبيا",
                style = ZenobiaTheme.typography.fontHeadingXlBold,
                color = ZenobiaTheme.colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "التواصل اللامركزي — بأيدينا",
                style = ZenobiaTheme.typography.fontBodyMdRegular,
                color = ZenobiaTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.version,
                style = ZenobiaTheme.typography.fontBodySmRegular,
                color = ZenobiaTheme.colors.textSecondary.copy(alpha = 0.5f),
            )
        }
    }
}

@Composable
private fun WelcomeActionButtons(
    state: OnBoardingState,
    onSignInWithQrCode: () -> Unit,
    onSignIn: (mustChooseAccountProvider: Boolean) -> Unit,
    onCreateAccount: () -> Unit,
    onReportProblem: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        val isLoading by remember(state.loginMode) {
            derivedStateOf {
                state.loginMode is AsyncData.Loading
            }
        }

        val signInButtonStringRes = if (state.canLoginWithQrCode || state.canCreateAccount) {
            R.string.screen_onboarding_sign_in_manually
        } else {
            CommonStrings.action_continue
        }

        if (state.canLoginWithQrCode) {
            Button(
                text = stringResource(id = R.string.screen_onboarding_sign_in_with_qr_code),
                leadingIcon = IconSource.Vector(CompoundIcons.QrCode()),
                onClick = onSignInWithQrCode,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(52.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        val defaultAccountProvider = state.defaultAccountProvider
        if (defaultAccountProvider == null) {
            Button(
                text = stringResource(id = signInButtonStringRes),
                onClick = { onSignIn(state.mustChooseAccountProvider) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(52.dp)
                    .testTag(TestTags.onBoardingSignIn),
            )
        } else {
            Button(
                text = stringResource(id = R.string.screen_onboarding_sign_in_to, defaultAccountProvider),
                showProgress = isLoading,
                onClick = { state.eventSink(OnBoardingEvents.OnSignIn(defaultAccountProvider)) },
                enabled = state.submitEnabled || isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(52.dp),
            )
        }

        if (state.canCreateAccount) {
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                text = stringResource(id = R.string.screen_onboarding_sign_up),
                onClick = onCreateAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(48.dp),
            )
        }

        if (state.showDevLogin) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                text = "Dev Login (joan@joan.com)",
                onClick = { state.eventSink(OnBoardingEvents.OnDevSignIn) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "باستخدام زنوبيا، أنت توافق على",
            style = ZenobiaTheme.typography.fontBodySmRegular,
            color = ZenobiaTheme.colors.textSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
        Row(
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "شروط الخدمة",
                style = ZenobiaTheme.typography.fontBodySmRegular,
                color = Color(0xFF0DBDA8),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onLearnMoreClick() },
            )
            Text(
                text = " و ",
                style = ZenobiaTheme.typography.fontBodySmRegular,
                color = ZenobiaTheme.colors.textSecondary.copy(alpha = 0.7f),
            )
            Text(
                text = "سياسة الخصوصية",
                style = ZenobiaTheme.typography.fontBodySmRegular,
                color = Color(0xFF0DBDA8),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onLearnMoreClick() },
            )
        }

        if (state.isAddingAccount.not()) {
            Spacer(modifier = Modifier.height(12.dp))
            if (state.canReportBug) {
                Text(
                    modifier = Modifier
                        .clickable(onClick = onReportProblem)
                        .padding(16.dp),
                    text = stringResource(id = CommonStrings.common_report_a_problem),
                    style = ZenobiaTheme.typography.fontBodySmRegular,
                    color = ZenobiaTheme.colors.textSecondary,
                )
            } else {
                Text(
                    modifier = Modifier
                        .clickable { state.eventSink(OnBoardingEvents.OnVersionClick) }
                        .padding(16.dp),
                    text = stringResource(id = R.string.screen_onboarding_app_version, state.version),
                    style = ZenobiaTheme.typography.fontBodySmRegular,
                    color = ZenobiaTheme.colors.textSecondary,
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun OnBoardingViewPreview(
    @PreviewParameter(OnBoardingStateProvider::class) state: OnBoardingState,
) = ZenobiaPreview {
    OnBoardingView(
        state = state,
        onBackClick = {},
        onDeveloperSettingsClick = {},
        onSignInWithQrCode = {},
        onSignIn = {},
        onCreateAccount = {},
        onReportProblem = {},
        onOAuthDetails = {},
        onNeedLoginPassword = {},
        onLearnMoreClick = {},
        onCreateAccountContinue = {},
    )
}
