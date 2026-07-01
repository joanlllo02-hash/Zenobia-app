/*
 * Copyright (c) 2025 7X. All rights reserved.
 * فخر برمجة سورية — شركة 7X
 */

package com.zenobia.app.features.login.impl.screens.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    val radius: Float,
    val alpha: Float,
)

private data class Connection(
    val from: Int,
    val to: Int,
    val alpha: Float,
)

@Composable
fun ParticleNetworkBackground(
    modifier: Modifier = Modifier,
    nodeCount: Int = 24,
    connectionDistance: Float = 200f,
    primaryColor: Color = Color(0xFF0DBDA8),
    secondaryColor: Color = Color(0xFF0D5CBD),
    accentColor: Color = Color(0xFF8B5CF6),
) {
    val infiniteTransition = rememberInfiniteTransition()
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
    )

    val particles = remember {
        List(nodeCount) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                vx = (Random.nextFloat() - 0.5f) * 0.008f,
                vy = (Random.nextFloat() - 0.5f) * 0.008f,
                radius = (Random.nextFloat() * 3.5f + 1.5f),
                alpha = Random.nextFloat() * 0.6f + 0.4f,
            )
        }
    }

    val connections = remember {
        val list = mutableListOf<Connection>()
        for (i in 0 until nodeCount) {
            for (j in i + 1 until nodeCount) {
                val dist = kotlin.math.sqrt(
                    (particles[i].x - particles[j].x).let { it * it } +
                        (particles[i].y - particles[j].y).let { it * it }
                )
                if (dist < 0.35f && Random.nextFloat() < 0.3f) {
                    list.add(
                        Connection(
                            from = i,
                            to = j,
                            alpha = Random.nextFloat() * 0.3f + 0.1f,
                        )
                    )
                }
            }
        }
        list
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val phaseRad = Math.toRadians(phase.toDouble())

        for (i in particles.indices) {
            val p = particles[i]
            p.x += p.vx * (1f + 0.3f * sin((phaseRad + i * 0.5)).toFloat())
            p.y += p.vy * (1f + 0.3f * cos((phaseRad + i * 0.5)).toFloat())
            if (p.x < 0f || p.x > 1f) {
                val flip = Particle(
                    x = if (p.x < 0f) 0f else 1f,
                    y = p.y,
                    vx = -p.vx,
                    vy = p.vy,
                    radius = p.radius,
                    alpha = p.alpha,
                )
                particles[i] = flip
            }
            if (p.y < 0f || p.y > 1f) {
                val flip = Particle(
                    x = p.x,
                    y = if (p.y < 0f) 0f else 1f,
                    vx = p.vx,
                    vy = -p.vy,
                    radius = p.radius,
                    alpha = p.alpha,
                )
                particles[i] = flip
            }
        }

        for (conn in connections) {
            val p1 = particles[conn.from]
            val p2 = particles[conn.to]
            val dx = (p1.x - p2.x) * w
            val dy = (p1.y - p2.y) * h
            val dist = kotlin.math.sqrt(dx * dx + dy * dy)
            if (dist < connectionDistance) {
                val lineAlpha = conn.alpha * (1f - dist / connectionDistance)
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = lineAlpha * 0.3f),
                            secondaryColor.copy(alpha = lineAlpha * 0.3f),
                        ),
                    ),
                    start = Offset(p1.x * w, p1.y * h),
                    end = Offset(p2.x * w, p2.y * h),
                    strokeWidth = 0.8f,
                )
            }
        }

        for (i in particles.indices) {
            val p = particles[i]
            val pulse = (sin(phaseRad * 1.5 + i * 0.7) * 0.3f + 0.7f).toFloat()
            val color = when (i % 3) {
                0 -> primaryColor
                1 -> secondaryColor
                else -> accentColor
            }
            drawCircle(
                color = color.copy(alpha = p.alpha * pulse),
                radius = p.radius * (1f + 0.3f * (1f - pulse)),
                center = Offset(p.x * w, p.y * h),
            )
        }
    }
}
