package com.movievortex.android.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object VortexIcons {

    private fun buildIcon(name: String, pathData: String): ImageVector {
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                // parse SVG path data
                // For safety and compatibility with Compose Vector DSL, we define helper builders below
            }
        }.build()
    }

    val StaggeredMenu: ImageVector by lazy {
        ImageVector.Builder("StaggeredMenu", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(4f, 6.5f)
                horizontalLineToRelative(16f)
                curveToRelative(0.55f, 0f, 1f, 0.45f, 1f, 1f)
                reflectiveCurveToRelative(-0.45f, 1f, -1f, 1f)
                horizontalLineTo(4f)
                curveToRelative(-0.55f, 0f, -1f, -0.45f, -1f, -1f)
                reflectiveCurveToRelative(0.45f, -1f, 1f, -1f)
                close()
                moveTo(4f, 11f)
                horizontalLineToRelative(11f)
                curveToRelative(0.55f, 0f, 1f, 0.45f, 1f, 1f)
                reflectiveCurveToRelative(-0.45f, 1f, -1f, 1f)
                horizontalLineTo(4f)
                curveToRelative(-0.55f, 0f, -1f, -0.45f, -1f, -1f)
                reflectiveCurveToRelative(0.45f, -1f, 1f, -1f)
                close()
                moveTo(4f, 15.5f)
                horizontalLineToRelative(7f)
                curveToRelative(0.55f, 0f, 1f, 0.45f, 1f, 1f)
                reflectiveCurveToRelative(-0.45f, 1f, -1f, 1f)
                horizontalLineTo(4f)
                curveToRelative(-0.55f, 0f, -1f, -0.45f, -1f, -1f)
                reflectiveCurveToRelative(0.45f, -1f, 1f, -1f)
                close()
            }
        }.build()
    }

    val VideoHome: ImageVector by lazy {
        ImageVector.Builder("VideoHome", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12.0f, 3.2f)
                curveToRelative(0.53f, 0f, 1.04f, 0.22f, 1.4f, 0.6f)
                lineToRelative(6.8f, 6.2f)
                curveToRelative(0.5f, 0.46f, 0.8f, 1.12f, 0.8f, 1.8f)
                verticalLineTo(18.5f)
                curveToRelative(0f, 1.38f, -1.12f, 2.5f, -2.5f, 2.5f)
                horizontalLineTo(15f)
                curveToRelative(-0.55f, 0f, -1f, -0.45f, -1f, -1f)
                verticalLineToRelative(-4.5f)
                curveToRelative(0f, -0.83f, -0.67f, -1.5f, -1.5f, -1.5f)
                reflectiveCurveTo(11f, 14.67f, 11f, 15.5f)
                verticalLineTo(20f)
                curveToRelative(0f, 0.55f, -0.45f, 1f, -1f, 1f)
                horizontalLineTo(5.5f)
                curveTo(4.12f, 21f, 3f, 19.88f, 3f, 18.5f)
                verticalLineToRelative(-6.7f)
                curveToRelative(0f, -0.68f, 0.3f, -1.34f, 0.8f, -1.8f)
                lineToRelative(6.8f, -6.2f)
                curveToRelative(0.36f, -0.38f, 0.87f, -0.6f, 1.4f, -0.6f)
                close()
            }
        }.build()
    }

    val VideoSearch: ImageVector by lazy {
        ImageVector.Builder("VideoSearch", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(10.5f, 3f)
                curveToRelative(4.14f, 0f, 7.5f, 3.36f, 7.5f, 7.5f)
                curveToRelative(0f, 1.61f, -0.51f, 3.1f, -1.38f, 4.32f)
                lineToRelative(4.09f, 4.09f)
                curveToRelative(0.39f, 0.39f, 0.39f, 1.02f, 0f, 1.41f)
                curveToRelative(-0.39f, 0.39f, -1.02f, 0.39f, -1.41f, 0f)
                lineToRelative(-4.09f, -4.09f)
                curveTo(13.99f, 17.15f, 12.35f, 18f, 10.5f, 18f)
                curveTo(6.36f, 18f, 3f, 14.64f, 3f, 10.5f)
                reflectiveCurveTo(6.36f, 3f, 10.5f, 3f)
                close()
            }
        }.build()
    }

    val VideoHeart: ImageVector by lazy {
        ImageVector.Builder("VideoHeart", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 21.35f)
                lineToRelative(-1.45f, -1.32f)
                curveTo(5.4f, 15.36f, 2f, 12.28f, 2f, 8.5f)
                curveTo(2f, 5.42f, 4.42f, 3f, 7.5f, 3f)
                curveToRelative(1.74f, 0f, 3.41f, 0.81f, 4.5f, 2.09f)
                curveTo(13.09f, 3.81f, 14.76f, 3f, 16.5f, 3f)
                curveTo(19.58f, 3f, 22f, 5.42f, 22f, 8.5f)
                curveToRelative(0f, 3.78f, -3.4f, 6.86f, -8.55f, 11.54f)
                lineTo(12f, 21.35f)
                close()
            }
        }.build()
    }

    val VideoProfile: ImageVector by lazy {
        ImageVector.Builder("VideoProfile", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 2.5f)
                curveToRelative(2.48f, 0f, 4.5f, 2.02f, 4.5f, 4.5f)
                reflectiveCurveToRelative(-2.02f, 4.5f, -4.5f, 4.5f)
                reflectiveCurveToRelative(-4.5f, -2.02f, -4.5f, -4.5f)
                reflectiveCurveToRelative(2.02f, -4.5f, 4.5f, -4.5f)
                close()
                moveTo(12f, 13.5f)
                curveToRelative(4.42f, 0f, 8f, 2.69f, 8f, 6f)
                curveToRelative(0f, 0.83f, -0.67f, 1.5f, -1.5f, 1.5f)
                horizontalLineTo(5.5f)
                curveToRelative(-0.83f, 0f, -1.5f, -0.67f, -1.5f, -1.5f)
                curveToRelative(0f, -3.31f, 3.58f, -6f, 8f, -6f)
                close()
            }
        }.build()
    }

    val History: ImageVector by lazy {
        ImageVector.Builder("History", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(13f, 3f)
                curveToRelative(-4.97f, 0f, -9f, 4.03f, -9f, 9f)
                horizontalLineTo(1f)
                lineToRelative(3.89f, 3.89f)
                lineToRelative(0.07f, 0.14f)
                lineTo(9f, 12f)
                horizontalLineTo(6f)
                curveToRelative(0f, -3.87f, 3.13f, -7f, 7f, -7f)
                reflectiveCurveToRelative(7f, 3.13f, 7f, 7f)
                reflectiveCurveToRelative(-3.13f, 7f, -7f, 7f)
                curveToRelative(-1.93f, 0f, -3.68f, -0.79f, -4.94f, -2.06f)
                lineToRelative(-1.42f, 1.42f)
                curveTo(8.27f, 19.99f, 10.51f, 21f, 13f, 21f)
                curveToRelative(4.97f, 0f, 9f, -4.03f, 9f, -9f)
                reflectiveCurveToRelative(-4.03f, -9f, -9f, -9f)
                close()
                moveTo(12f, 8f)
                verticalLineToRelative(5f)
                lineToRelative(4.28f, 2.54f)
                lineToRelative(0.72f, -1.21f)
                lineToRelative(-3.5f, -2.08f)
                verticalLineTo(8f)
                horizontalLineTo(12f)
                close()
            }
        }.build()
    }

    val Home: ImageVector by lazy {
        ImageVector.Builder("Home", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(10.0f, 20.0f)
                verticalLineToRelative(-6.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(6.0f)
                horizontalLineToRelative(5.0f)
                verticalLineToRelative(-8.0f)
                horizontalLineToRelative(3.0f)
                lineTo(12.0f, 3.0f)
                lineTo(2.0f, 12.0f)
                horizontalLineToRelative(3.0f)
                verticalLineToRelative(8.0f)
                close()
            }
        }.build()
    }

    val HomeOutline: ImageVector by lazy {
        ImageVector.Builder("HomeOutline", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 5.69f)
                lineToRelative(5f, 4.5f)
                verticalLineTo(18f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(-6f)
                horizontalLineTo(9f)
                verticalLineToRelative(6f)
                horizontalLineTo(7f)
                verticalLineToRelative(-7.81f)
                lineToRelative(5f, -4.5f)
                moveTo(12f, 3f)
                lineTo(2f, 12f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(8f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(-6f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(-8f)
                horizontalLineToRelative(3f)
                lineTo(12f, 3f)
                close()
            }
        }.build()
    }

    val Heart: ImageVector by lazy {
        ImageVector.Builder("Heart", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 21.35f)
                lineToRelative(-1.45f, -1.32f)
                curveTo(5.4f, 15.36f, 2f, 12.28f, 2f, 8.5f)
                curveTo(2f, 5.42f, 4.42f, 3f, 7.5f, 3f)
                curveToRelative(1.74f, 0f, 3.41f, 0.81f, 4.5f, 2.09f)
                curveTo(13.09f, 3.81f, 14.76f, 3f, 16.5f, 3f)
                curveTo(19.58f, 3f, 22f, 5.42f, 22f, 8.5f)
                curveToRelative(0f, 3.78f, -3.4f, 6.86f, -8.55f, 11.54f)
                lineTo(12f, 21.35f)
                close()
            }
        }.build()
    }

    val HeartBorder: ImageVector by lazy {
        ImageVector.Builder("HeartBorder", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(16.5f, 3f)
                curveToRelative(-1.74f, 0f, -3.41f, 0.81f, -4.5f, 2.09f)
                curveTo(10.91f, 3.81f, 9.24f, 3f, 7.5f, 3f)
                curveTo(4.42f, 3f, 2f, 5.42f, 2f, 8.5f)
                curveToRelative(0f, 3.78f, 3.4f, 6.86f, 8.55f, 11.54f)
                lineTo(12f, 21.35f)
                lineToRelative(1.45f, -1.32f)
                curveTo(18.6f, 15.36f, 22f, 12.28f, 22f, 8.5f)
                curveTo(22f, 5.42f, 19.58f, 3f, 16.5f, 3f)
                close()
                moveTo(12.1f, 18.55f)
                lineToRelative(-0.1f, 0.1f)
                lineToRelative(-0.1f, -0.1f)
                curveTo(7.14f, 14.24f, 4f, 11.39f, 4f, 8.5f)
                curveTo(4f, 6.5f, 5.5f, 5f, 7.5f, 5f)
                curveToRelative(1.54f, 0f, 3.04f, 0.99f, 3.57f, 2.36f)
                horizontalLineToRelative(1.87f)
                curveTo(13.46f, 5.99f, 14.96f, 5f, 16.5f, 5f)
                curveToRelative(2f, 0f, 3.5f, 1.5f, 3.5f, 3.5f)
                curveToRelative(0f, 2.89f, -3.14f, 5.74f, -7.9f, 10.05f)
                close()
            }
        }.build()
    }

    val MoreVert: ImageVector by lazy {
        ImageVector.Builder("MoreVert", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 8f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                reflectiveCurveToRelative(-0.9f, -2f, -2f, -2f)
                reflectiveCurveToRelative(-2f, 0.9f, -2f, 2f)
                reflectiveCurveToRelative(0.9f, 2f, 2f, 2f)
                close()
                moveTo(12f, 10f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                reflectiveCurveToRelative(0.9f, 2f, 2f, 2f)
                reflectiveCurveToRelative(2f, -0.9f, 2f, -2f)
                reflectiveCurveToRelative(-0.9f, -2f, -2f, -2f)
                close()
                moveTo(12f, 16f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                reflectiveCurveToRelative(0.9f, 2f, 2f, 2f)
                reflectiveCurveToRelative(2f, -0.9f, 2f, -2f)
                reflectiveCurveToRelative(-0.9f, -2f, -2f, -2f)
                close()
            }
        }.build()
    }

    val ThumbUp: ImageVector by lazy {
        ImageVector.Builder("ThumbUp", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(1f, 21f)
                horizontalLineToRelative(4f)
                verticalLineTo(9f)
                horizontalLineTo(1f)
                verticalLineToRelative(12f)
                close()
                moveTo(23f, 10f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                horizontalLineToRelative(-6.31f)
                lineToRelative(0.95f, -4.57f)
                lineToRelative(0.03f, -0.32f)
                curveToRelative(0f, -0.41f, -0.17f, -0.79f, -0.44f, -1.06f)
                lineTo(14.17f, 1f)
                lineTo(7.59f, 7.59f)
                curveTo(7.22f, 7.95f, 7f, 8.45f, 7f, 9f)
                verticalLineToRelative(10f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(9f)
                curveToRelative(0.83f, 0f, 1.54f, -0.5f, 1.84f, -1.22f)
                lineToRelative(3.02f, -7.05f)
                curveToRelative(0.09f, -0.23f, 0.14f, -0.47f, 0.14f, -0.73f)
                verticalLineToRelative(-2f)
                close()
            }
        }.build()
    }

    val Search: ImageVector by lazy {
        ImageVector.Builder("Search", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(15.5f, 14f)
                horizontalLineToRelative(-0.79f)
                lineToRelative(-0.28f, -0.27f)
                curveTo(15.41f, 12.59f, 16f, 11.11f, 16f, 9.5f)
                curveTo(16f, 5.91f, 13.09f, 3f, 9.5f, 3f)
                reflectiveCurveTo(3f, 5.91f, 3f, 9.5f)
                reflectiveCurveTo(5.91f, 16f, 9.5f, 16f)
                curveToRelative(1.61f, 0f, 3.09f, -0.59f, 4.23f, -1.57f)
                lineToRelative(0.27f, 0.28f)
                verticalLineToRelative(0.79f)
                lineToRelative(5f, 4.99f)
                lineTo(20.49f, 19f)
                lineToRelative(-4.99f, -5f)
                close()
                moveTo(9.5f, 14f)
                curveTo(7.01f, 14f, 5f, 11.99f, 5f, 9.5f)
                reflectiveCurveTo(7.01f, 5f, 9.5f, 5f)
                reflectiveCurveTo(14f, 7.01f, 14f, 9.5f)
                reflectiveCurveTo(11.99f, 14f, 9.5f, 14f)
                close()
            }
        }.build()
    }

    val ArrowBack: ImageVector by lazy {
        ImageVector.Builder("ArrowBack", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(20f, 11f)
                horizontalLineTo(7.83f)
                lineToRelative(5.59f, -5.59f)
                lineTo(12f, 4f)
                lineToRelative(-8f, 8f)
                lineToRelative(8f, 8f)
                lineToRelative(1.41f, -1.41f)
                lineTo(7.83f, 13f)
                horizontalLineTo(20f)
                verticalLineToRelative(-2f)
                close()
            }
        }.build()
    }

    val Close: ImageVector by lazy {
        ImageVector.Builder("Close", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(19f, 6.41f)
                lineTo(17.59f, 5f)
                lineTo(12f, 10.59f)
                lineTo(6.41f, 5f)
                lineTo(5f, 6.41f)
                lineTo(10.59f, 12f)
                lineTo(5f, 17.59f)
                lineTo(6.41f, 19f)
                lineTo(12f, 13.41f)
                lineTo(17.59f, 19f)
                lineTo(19f, 17.59f)
                lineTo(13.41f, 12f)
                close()
            }
        }.build()
    }

    val Check: ImageVector by lazy {
        ImageVector.Builder("Check", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(9f, 16.17f)
                lineTo(4.83f, 12f)
                lineToRelative(-1.42f, 1.41f)
                lineTo(9f, 19f)
                lineTo(21f, 7f)
                lineToRelative(-1.41f, -1.41f)
                close()
            }
        }.build()
    }

    val Play: ImageVector by lazy {
        ImageVector.Builder("Play", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(8f, 5f)
                verticalLineToRelative(14f)
                lineToRelative(11f, -7f)
                close()
            }
        }.build()
    }

    val Pause: ImageVector by lazy {
        ImageVector.Builder("Pause", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(6f, 19f)
                horizontalLineToRelative(4f)
                verticalLineTo(5f)
                horizontalLineTo(6f)
                verticalLineToRelative(14f)
                close()
                moveTo(14f, 5f)
                verticalLineToRelative(14f)
                horizontalLineToRelative(4f)
                verticalLineTo(5f)
                horizontalLineToRelative(-4f)
                close()
            }
        }.build()
    }

    val Replay10: ImageVector by lazy {
        ImageVector.Builder("Replay10", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 5f)
                verticalLineTo(1f)
                lineTo(7f, 6f)
                lineToRelative(5f, 5f)
                verticalLineTo(7f)
                curveToRelative(3.31f, 0f, 6f, 2.69f, 6f, 6f)
                reflectiveCurveToRelative(-2.69f, 6f, -6f, 6f)
                reflectiveCurveToRelative(-6f, -2.69f, -6f, -6f)
                horizontalLineTo(4f)
                curveToRelative(0f, 4.42f, 3.58f, 8f, 8f, 8f)
                reflectiveCurveToRelative(8f, -3.58f, 8f, -8f)
                reflectiveCurveToRelative(-3.58f, -8f, -8f, -8f)
                close()
            }
        }.build()
    }

    val Forward10: ImageVector by lazy {
        ImageVector.Builder("Forward10", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 5f)
                verticalLineTo(1f)
                lineToRelative(5f, 5f)
                lineToRelative(-5f, 5f)
                verticalLineTo(7f)
                curveToRelative(-3.31f, 0f, -6f, 2.69f, -6f, 6f)
                reflectiveCurveToRelative(2.69f, 6f, 6f, 6f)
                reflectiveCurveToRelative(6f, -2.69f, 6f, -6f)
                horizontalLineToRelative(2f)
                curveToRelative(0f, 4.42f, -3.58f, 8f, -8f, 8f)
                reflectiveCurveToRelative(-8f, -3.58f, -8f, -8f)
                reflectiveCurveToRelative(3.58f, -8f, 8f, -8f)
                close()
            }
        }.build()
    }

    val HighQuality: ImageVector by lazy {
        ImageVector.Builder("HighQuality", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(19f, 4f)
                horizontalLineTo(5f)
                curveToRelative(-1.11f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(12f)
                curveToRelative(0f, 1.1f, 0.89f, 2f, 2f, 2f)
                horizontalLineToRelative(14f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(6f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(11f, 15f)
                horizontalLineTo(9.5f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(2f)
                horizontalLineTo(6f)
                verticalLineTo(9f)
                horizontalLineToRelative(1.5f)
                verticalLineToRelative(2.5f)
                horizontalLineToRelative(2f)
                verticalLineTo(9f)
                horizontalLineTo(11f)
                verticalLineToRelative(6f)
                close()
                moveTo(18f, 14f)
                curveToRelative(0f, 0.55f, -0.45f, 1f, -1f, 1f)
                horizontalLineToRelative(-4f)
                curveToRelative(-0.55f, 0f, -1f, -0.45f, -1f, -1f)
                verticalLineToRelative(-4f)
                curveToRelative(0f, -0.55f, 0.45f, -1f, 1f, -1f)
                horizontalLineToRelative(4f)
                curveToRelative(0.55f, 0f, 1f, 0.45f, 1f, 1f)
                verticalLineToRelative(4f)
                close()
            }
        }.build()
    }

    val Subtitles: ImageVector by lazy {
        ImageVector.Builder("Subtitles", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(20f, 4f)
                horizontalLineTo(4f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(12f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(16f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(6f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(4f, 12f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(2f)
                horizontalLineTo(4f)
                verticalLineToRelative(-2f)
                close()
                moveTo(14f, 18f)
                horizontalLineTo(4f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(10f)
                verticalLineToRelative(2f)
                close()
                moveTo(20f, 18f)
                horizontalLineToRelative(-4f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(2f)
                close()
                moveTo(20f, 14f)
                horizontalLineToRelative(-8f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(8f)
                verticalLineToRelative(2f)
                close()
            }
        }.build()
    }

    val Movie: ImageVector by lazy {
        ImageVector.Builder("Movie", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(18f, 4f)
                lineToRelative(2f, 4f)
                horizontalLineToRelative(-3f)
                lineToRelative(-2f, -4f)
                horizontalLineToRelative(-2f)
                lineToRelative(2f, 4f)
                horizontalLineToRelative(-3f)
                lineToRelative(-2f, -4f)
                horizontalLineTo(8f)
                lineToRelative(2f, 4f)
                horizontalLineTo(7f)
                lineTo(5f, 4f)
                horizontalLineTo(4f)
                curveToRelative(-1.1f, 0f, -1.99f, 0.9f, -1.99f, 2f)
                lineTo(2f, 18f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(16f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(4f)
                horizontalLineToRelative(-4f)
                close()
            }
        }.build()
    }

    val Tv: ImageVector by lazy {
        ImageVector.Builder("Tv", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(21f, 3f)
                horizontalLineTo(3f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(12f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(5f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(8f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(5f)
                curveToRelative(1.1f, 0f, 1.99f, -0.9f, 1.99f, -2f)
                lineTo(23f, 5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(21f, 17f)
                horizontalLineTo(3f)
                verticalLineTo(5f)
                horizontalLineToRelative(18f)
                verticalLineToRelative(12f)
                close()
            }
        }.build()
    }

    val Download: ImageVector by lazy {
        ImageVector.Builder("Download", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(19.35f, 10.04f)
                curveTo(18.67f, 6.59f, 15.64f, 4f, 12f, 4f)
                curveTo(9.11f, 4f, 6.6f, 5.64f, 5.35f, 8.04f)
                curveTo(2.34f, 8.36f, 0f, 10.91f, 0f, 14f)
                curveToRelative(0f, 3.31f, 2.69f, 6f, 6f, 6f)
                horizontalLineToRelative(13f)
                curveToRelative(2.76f, 0f, 5f, -2.24f, 5f, -5f)
                curveToRelative(0f, -2.64f, -2.05f, -4.78f, -4.65f, -4.96f)
                close()
                moveTo(17f, 13f)
                lineToRelative(-5f, 5f)
                lineToRelative(-5f, -5f)
                horizontalLineToRelative(3f)
                verticalLineTo(9f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                horizontalLineToRelative(3f)
                close()
            }
        }.build()
    }

    val Settings: ImageVector by lazy {
        ImageVector.Builder("Settings", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(19.14f, 12.94f)
                curveToRelative(0.04f, -0.3f, 0.06f, -0.61f, 0.06f, -0.94f)
                curveToRelative(0f, -0.32f, -0.02f, -0.64f, -0.07f, -0.94f)
                lineToRelative(2.03f, -1.58f)
                curveToRelative(0.18f, -0.14f, 0.23f, -0.41f, 0.12f, -0.61f)
                lineToRelative(-1.92f, -3.32f)
                curveToRelative(-0.12f, -0.22f, -0.37f, -0.29f, -0.59f, -0.22f)
                lineToRelative(-2.39f, 0.96f)
                curveToRelative(-0.5f, -0.38f, -1.03f, -0.7f, -1.62f, -0.94f)
                lineTo(14.4f, 2.81f)
                curveToRelative(-0.04f, -0.24f, -0.24f, -0.41f, -0.48f, -0.41f)
                horizontalLineToRelative(-3.84f)
                curveToRelative(-0.24f, 0f, -0.43f, 0.17f, -0.47f, 0.41f)
                lineTo(9.25f, 5.35f)
                curveToRelative(-0.59f, 0.24f, -1.13f, 0.56f, -1.62f, 0.94f)
                lineToRelative(-2.39f, -0.96f)
                curveToRelative(-0.22f, -0.08f, -0.47f, 0f, -0.59f, 0.22f)
                lineTo(2.74f, 8.87f)
                curveToRelative(-0.12f, 0.21f, -0.08f, 0.47f, 0.12f, 0.61f)
                lineToRelative(2.03f, 1.58f)
                curveToRelative(-0.05f, 0.3f, -0.09f, 0.63f, -0.09f, 0.94f)
                reflectiveCurveToRelative(0.02f, 0.64f, 0.07f, 0.94f)
                lineToRelative(-2.03f, 1.58f)
                curveToRelative(-0.18f, 0.14f, -0.23f, 0.41f, -0.12f, 0.61f)
                lineToRelative(1.92f, 3.32f)
                curveToRelative(0.12f, 0.22f, 0.37f, 0.29f, 0.59f, 0.22f)
                lineToRelative(2.39f, -0.96f)
                curveToRelative(0.5f, 0.38f, 1.03f, 0.7f, 1.62f, 0.94f)
                lineToRelative(0.36f, 2.54f)
                curveToRelative(0.05f, 0.24f, 0.24f, 0.41f, 0.48f, 0.41f)
                horizontalLineToRelative(3.84f)
                curveToRelative(0.24f, 0f, 0.44f, -0.17f, 0.47f, -0.41f)
                lineToRelative(0.36f, -2.54f)
                curveToRelative(0.59f, -0.24f, 1.13f, -0.56f, 1.62f, -0.94f)
                lineToRelative(2.39f, 0.96f)
                curveToRelative(0.22f, 0.08f, 0.47f, 0f, 0.59f, -0.22f)
                lineToRelative(1.92f, -3.32f)
                curveToRelative(0.12f, -0.22f, 0.07f, -0.47f, -0.12f, -0.61f)
                lineToRelative(-2.01f, -1.58f)
                close()
                moveTo(12f, 15.6f)
                curveToRelative(-1.98f, 0f, -3.6f, -1.62f, -3.6f, -3.6f)
                reflectiveCurveToRelative(1.62f, -3.6f, 3.6f, -3.6f)
                reflectiveCurveToRelative(3.6f, 1.62f, 3.6f, 3.6f)
                reflectiveCurveToRelative(-1.62f, 3.6f, -3.6f, 3.6f)
                close()
            }
        }.build()
    }

    val Bookmark: ImageVector by lazy {
        ImageVector.Builder("Bookmark", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(17f, 3f)
                horizontalLineTo(7f)
                curveToRelative(-1.1f, 0f, -1.99f, 0.9f, -1.99f, 2f)
                lineTo(5f, 21f)
                lineToRelative(7f, -3f)
                lineToRelative(7f, 3f)
                verticalLineTo(5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
            }
        }.build()
    }

    val BookmarkBorder: ImageVector by lazy {
        ImageVector.Builder("BookmarkBorder", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(17f, 3f)
                horizontalLineTo(7f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(16f)
                lineToRelative(7f, -3f)
                lineToRelative(7f, 3f)
                verticalLineTo(5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(17f, 18f)
                lineToRelative(-5f, -2.18f)
                lineTo(7f, 18f)
                verticalLineTo(5f)
                horizontalLineToRelative(10f)
                verticalLineToRelative(13f)
                close()
            }
        }.build()
    }

    val Add: ImageVector by lazy {
        ImageVector.Builder("Add", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(19f, 13f)
                horizontalLineToRelative(-6f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(-6f)
                horizontalLineTo(5f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(6f)
                verticalLineTo(5f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(2f)
                close()
            }
        }.build()
    }

    val Security: ImageVector by lazy {
        ImageVector.Builder("Security", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 1f)
                lineTo(3f, 5f)
                verticalLineToRelative(6f)
                curveToRelative(0f, 5.55f, 3.84f, 10.74f, 9f, 12f)
                curveToRelative(5.16f, -1.26f, 9f, -6.45f, 9f, -12f)
                verticalLineTo(5f)
                lineToRelative(-9f, -4f)
                close()
            }
        }.build()
    }

    val Person: ImageVector by lazy {
        ImageVector.Builder("Person", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 12f)
                curveToRelative(2.21f, 0f, 4f, -1.79f, 4f, -4f)
                reflectiveCurveToRelative(-1.79f, -4f, -4f, -4f)
                reflectiveCurveToRelative(-4f, 1.79f, -4f, 4f)
                reflectiveCurveToRelative(1.79f, 4f, 4f, 4f)
                close()
                moveTo(12f, 14f)
                curveToRelative(-2.67f, 0f, -8f, 1.34f, -8f, 4f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(16f)
                verticalLineToRelative(-2f)
                curveToRelative(0f, -2.66f, -5.33f, -4f, -8f, -4f)
                close()
            }
        }.build()
    }

    val PersonOutline: ImageVector by lazy {
        ImageVector.Builder("PersonOutline", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 5.9f)
                curveToRelative(1.16f, 0f, 2.1f, 0.94f, 2.1f, 2.1f)
                reflectiveCurveToRelative(-0.94f, 2.1f, -2.1f, 2.1f)
                reflectiveCurveTo(9.9f, 9.16f, 9.9f, 8f)
                reflectiveCurveTo(10.84f, 5.9f, 12f, 5.9f)
                moveTo(12f, 14.9f)
                curveToRelative(2.97f, 0f, 6.1f, 1.46f, 6.1f, 2.1f)
                verticalLineToRelative(1.1f)
                horizontalLineTo(5.9f)
                verticalLineTo(17f)
                curveToRelative(0f, -0.64f, 3.13f, -2.1f, 6.1f, -2.1f)
                moveTo(12f, 4f)
                curveTo(9.79f, 4f, 8f, 5.79f, 8f, 8f)
                reflectiveCurveToRelative(1.79f, 4f, 4f, 4f)
                reflectiveCurveToRelative(4f, -1.79f, 4f, -4f)
                reflectiveCurveToRelative(-1.79f, -4f, -4f, -4f)
                close()
                moveTo(12f, 13f)
                curveToRelative(-2.67f, 0f, -8f, 1.34f, -8f, 4f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(16f)
                verticalLineToRelative(-3f)
                curveToRelative(0f, -2.66f, -5.33f, -4f, -8f, -4f)
                close()
            }
        }.build()
    }

    val Wifi: ImageVector by lazy {
        ImageVector.Builder("Wifi", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 3f)
                curveTo(7.3f, 3f, 3.07f, 4.9f, 0f, 7.98f)
                lineTo(12f, 21f)
                lineTo(24f, 7.98f)
                curveTo(20.93f, 4.9f, 16.7f, 3f, 12f, 3f)
                close()
            }
        }.build()
    }

    val WifiOff: ImageVector by lazy {
        ImageVector.Builder("WifiOff", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(23.64f, 7f)
                curveToRelative(-0.45f, -0.34f, -4.93f, -4f, -11.64f, -4f)
                curveToRelative(-1.5f, 0f, -2.8f, 0.19f, -3.99f, 0.51f)
                lineTo(19.86f, 15.36f)
                lineTo(23.64f, 7f)
                close()
                moveTo(3.27f, 1.44f)
                lineTo(2f, 2.72f)
                lineToRelative(2.05f, 2.06f)
                curveTo(1.91f, 5.76f, 0.59f, 6.82f, 0.36f, 7f)
                lineToRelative(11.63f, 14.49f)
                lineToRelative(0.01f, 0.01f)
                lineToRelative(0.01f, -0.01f)
                lineToRelative(3.9f, -4.86f)
                lineToRelative(3.32f, 3.32f)
                lineToRelative(1.27f, -1.27f)
                lineTo(3.27f, 1.44f)
                close()
            }
        }.build()
    }

    val Delete: ImageVector by lazy {
        ImageVector.Builder("Delete", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(6f, 19f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(8f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(7f)
                horizontalLineTo(6f)
                verticalLineToRelative(12f)
                close()
                moveTo(19f, 4f)
                horizontalLineToRelative(-3.5f)
                lineToRelative(-1f, -1f)
                horizontalLineToRelative(-5f)
                lineToRelative(-1f, 1f)
                horizontalLineTo(5f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(14f)
                verticalLineTo(4f)
                close()
            }
        }.build()
    }

    val ChevronDown: ImageVector by lazy {
        ImageVector.Builder("ChevronDown", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(7.41f, 8.59f)
                lineTo(12f, 13.17f)
                lineToRelative(4.59f, -4.58f)
                lineTo(18f, 10f)
                lineToRelative(-6f, 6f)
                lineToRelative(-6f, -6f)
                lineToRelative(1.41f, -1.41f)
                close()
            }
        }.build()
    }

    val ChevronUp: ImageVector by lazy {
        ImageVector.Builder("ChevronUp", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(7.41f, 15.41f)
                lineTo(12f, 10.83f)
                lineToRelative(4.59f, 4.58f)
                lineTo(18f, 14f)
                lineToRelative(-6f, -6f)
                lineToRelative(-6f, 6f)
                lineToRelative(1.41f, 1.41f)
                close()
            }
        }.build()
    }

    val SearchOff: ImageVector by lazy {
        ImageVector.Builder("SearchOff", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(15.5f, 14f)
                horizontalLineToRelative(-0.79f)
                lineToRelative(-0.28f, -0.27f)
                curveTo(15.41f, 12.59f, 16f, 11.11f, 16f, 9.5f)
                curveTo(16f, 5.91f, 13.09f, 3f, 9.5f, 3f)
                curveTo(7.89f, 3f, 6.41f, 3.59f, 5.27f, 4.57f)
                lineTo(19.78f, 19.08f)
                lineTo(21.19f, 17.67f)
                lineTo(15.5f, 11.98f)
                verticalLineTo(14f)
                close()
                moveTo(2.81f, 2.81f)
                lineTo(1.39f, 4.22f)
                lineToRelative(3.52f, 3.52f)
                curveTo(4.34f, 8.44f, 4f, 9.42f, 4f, 10.5f)
                curveTo(4f, 14.09f, 6.91f, 17f, 10.5f, 17f)
                curveToRelative(1.08f, 0f, 2.06f, -0.34f, 2.76f, -0.91f)
                lineToRelative(4.99f, 4.99f)
                lineToRelative(1.41f, -1.41f)
                lineTo(2.81f, 2.81f)
                close()
            }
        }.build()
    }

    val DownloadOff: ImageVector by lazy {
        ImageVector.Builder("DownloadOff", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(21.19f, 21.19f)
                lineTo(2.81f, 2.81f)
                lineTo(1.39f, 4.22f)
                lineTo(4.45f, 7.28f)
                curveTo(2.13f, 8.35f, 0.5f, 10.74f, 0.5f, 13.5f)
                curveTo(0.5f, 17.09f, 3.41f, 20f, 7f, 20f)
                horizontalLineToRelative(10.18f)
                lineToRelative(2.61f, 2.61f)
                lineToRelative(1.4f, -1.42f)
                close()
                moveTo(19.35f, 10.04f)
                curveTo(18.67f, 6.59f, 15.64f, 4f, 12f, 4f)
                curveToRelative(-1.78f, 0f, -3.41f, 0.63f, -4.71f, 1.68f)
                lineToRelative(1.46f, 1.46f)
                curveTo(9.66f, 6.44f, 10.79f, 6f, 12f, 6f)
                curveToRelative(2.76f, 0f, 5f, 2.24f, 5f, 5f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(2f)
                curveToRelative(1.66f, 0f, 3f, 1.34f, 3f, 3f)
                curveToRelative(0f, 0.83f, -0.34f, 1.58f, -0.88f, 2.12f)
                lineToRelative(1.43f, 1.43f)
                curveTo(23.41f, 17.52f, 24f, 16.08f, 24f, 14.5f)
                curveToRelative(0f, -3.04f, -2.09f, -5.6f, -4.65f, -6.46f)
                close()
            }
        }.build()
    }

    val Dns: ImageVector by lazy {
        ImageVector.Builder("Dns", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(20f, 13f)
                horizontalLineTo(4f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(4f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(16f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineToRelative(-4f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(7f, 19f)
                curveToRelative(-1.1f, 0f, -2f, -0.9f, -2f, -2f)
                reflectiveCurveToRelative(0.9f, -2f, 2f, -2f)
                reflectiveCurveToRelative(2f, 0.9f, 2f, 2f)
                reflectiveCurveToRelative(-0.9f, 2f, -2f, 2f)
                close()
                moveTo(20f, 3f)
                horizontalLineTo(4f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(4f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(16f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(7f, 9f)
                curveToRelative(-1.1f, 0f, -2f, -0.9f, -2f, -2f)
                reflectiveCurveToRelative(0.9f, -2f, 2f, -2f)
                reflectiveCurveToRelative(2f, 0.9f, 2f, 2f)
                reflectiveCurveToRelative(-0.9f, 2f, -2f, 2f)
                close()
            }
        }.build()
    }

    val Storage: ImageVector by lazy {
        ImageVector.Builder("Storage", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(2f, 20f)
                horizontalLineToRelative(20f)
                verticalLineToRelative(-4f)
                horizontalLineTo(2f)
                verticalLineToRelative(4f)
                close()
                moveTo(4f, 17f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(2f)
                horizontalLineTo(4f)
                verticalLineToRelative(-2f)
                close()
                moveTo(2f, 4f)
                verticalLineToRelative(4f)
                horizontalLineToRelative(20f)
                verticalLineTo(4f)
                horizontalLineTo(2f)
                close()
                moveTo(6f, 7f)
                horizontalLineTo(4f)
                verticalLineTo(5f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(2f)
                close()
                moveTo(2f, 14f)
                horizontalLineToRelative(20f)
                verticalLineToRelative(-4f)
                horizontalLineTo(2f)
                verticalLineToRelative(4f)
                close()
                moveTo(4f, 11f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(2f)
                horizontalLineTo(4f)
                verticalLineToRelative(-2f)
                close()
            }
        }.build()
    }

    val DeleteSweep: ImageVector by lazy {
        ImageVector.Builder("DeleteSweep", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(15f, 16f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(-4f)
                close()
                moveTo(15f, 8f)
                horizontalLineToRelative(7f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(-7f)
                close()
                moveTo(15f, 12f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(-6f)
                close()
                moveTo(3f, 18f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(6f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(8f)
                horizontalLineTo(3f)
                verticalLineToRelative(10f)
                close()
                moveTo(13f, 5f)
                horizontalLineToRelative(-2.5f)
                lineToRelative(-1f, -1f)
                horizontalLineToRelative(-3f)
                lineToRelative(-1f, 1f)
                horizontalLineTo(3f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(10f)
                verticalLineTo(5f)
                close()
            }
        }.build()
    }

    val Share: ImageVector by lazy {
        ImageVector.Builder("Share", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(18f, 16.08f)
                curveToRelative(-0.76f, 0f, -1.44f, 0.3f, -1.96f, 0.77f)
                lineTo(8.91f, 12.7f)
                curveToRelative(0.05f, -0.23f, 0.09f, -0.46f, 0.09f, -0.7f)
                reflectiveCurveToRelative(-0.04f, -0.47f, -0.09f, -0.7f)
                lineToRelative(7.05f, -4.11f)
                curveToRelative(0.54f, 0.5f, 1.25f, 0.81f, 2.04f, 0.81f)
                curveToRelative(1.66f, 0f, 3f, -1.34f, 3f, -3f)
                reflectiveCurveToRelative(-1.34f, -3f, -3f, -3f)
                reflectiveCurveToRelative(-3f, 1.34f, -3f, 3f)
                curveToRelative(0f, 0.24f, 0.04f, 0.47f, 0.09f, 0.7f)
                lineTo(8.04f, 9.81f)
                curveTo(7.5f, 9.31f, 6.79f, 9f, 6f, 9f)
                curveToRelative(-1.66f, 0f, -3f, 1.34f, -3f, 3f)
                reflectiveCurveToRelative(1.34f, 3f, 3f, 3f)
                curveToRelative(0.79f, 0f, 1.5f, -0.31f, 2.04f, -0.81f)
                lineToRelative(7.12f, 4.16f)
                curveToRelative(-0.05f, 0.21f, -0.08f, 0.43f, -0.08f, 0.65f)
                curveToRelative(0f, 1.61f, 1.31f, 2.92f, 2.92f, 2.92f)
                reflectiveCurveToRelative(2.92f, -1.31f, 2.92f, -2.92f)
                curveToRelative(0f, -1.61f, -1.31f, -2.92f, -2.92f, -2.92f)
                close()
            }
        }.build()
    }

    val Report: ImageVector by lazy {
        ImageVector.Builder("Report", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(14.4f, 6f)
                lineTo(14f, 4f)
                horizontalLineTo(5f)
                verticalLineToRelative(17f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-7f)
                horizontalLineToRelative(5.6f)
                lineToRelative(0.4f, 2f)
                horizontalLineToRelative(7f)
                verticalLineTo(6f)
                horizontalLineToRelative(-5.6f)
                close()
            }
        }.build()
    }

    val Fullscreen: ImageVector by lazy {
        ImageVector.Builder("Fullscreen", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(7f, 14f)
                horizontalLineTo(5f)
                verticalLineToRelative(5f)
                horizontalLineToRelative(5f)
                verticalLineToRelative(-2f)
                horizontalLineTo(7f)
                verticalLineToRelative(-3f)
                close()
                moveTo(5f, 10f)
                horizontalLineToRelative(2f)
                verticalLineTo(7f)
                horizontalLineToRelative(3f)
                verticalLineTo(5f)
                horizontalLineTo(5f)
                verticalLineToRelative(5f)
                close()
                moveTo(19f, 19f)
                verticalLineToRelative(-5f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(-3f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(5f)
                close()
                moveTo(17f, 7f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(2f)
                verticalLineTo(5f)
                horizontalLineToRelative(-5f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(3f)
                close()
            }
        }.build()
    }

    val FullscreenExit: ImageVector by lazy {
        ImageVector.Builder("FullscreenExit", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(5f, 16f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-5f)
                horizontalLineTo(5f)
                verticalLineToRelative(2f)
                close()
                moveTo(8f, 8f)
                horizontalLineTo(5f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(5f)
                verticalLineTo(5f)
                horizontalLineTo(8f)
                verticalLineToRelative(3f)
                close()
                moveTo(14f, 19f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-3f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(-5f)
                verticalLineToRelative(5f)
                close()
                moveTo(16f, 8f)
                verticalLineTo(5f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(5f)
                horizontalLineToRelative(5f)
                verticalLineTo(8f)
                horizontalLineToRelative(-3f)
                close()
            }
        }.build()
    }

    val PictureInPicture: ImageVector by lazy {
        ImageVector.Builder("PictureInPicture", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(19f, 7f)
                horizontalLineToRelative(-8f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(8f)
                verticalLineTo(7f)
                close()
                moveTo(21f, 3f)
                horizontalLineTo(3f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(14f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(18f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(21f, 19f)
                horizontalLineTo(3f)
                verticalLineTo(5f)
                horizontalLineToRelative(18f)
                verticalLineToRelative(14f)
                close()
            }
        }.build()
    }

    val SkipNext: ImageVector by lazy {
        ImageVector.Builder("SkipNext", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(6f, 18f)
                lineToRelative(8.5f, -6f)
                lineTo(6f, 6f)
                verticalLineToRelative(12f)
                close()
                moveTo(16f, 6f)
                verticalLineToRelative(12f)
                horizontalLineToRelative(2f)
                verticalLineTo(6f)
                horizontalLineToRelative(-2f)
                close()
            }
        }.build()
    }

    val SkipPrevious: ImageVector by lazy {
        ImageVector.Builder("SkipPrevious", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(6f, 6f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(12f)
                horizontalLineTo(6f)
                close()
                moveTo(9.5f, 12f)
                lineToRelative(8.5f, 6f)
                verticalLineTo(6f)
                lineToRelative(-8.5f, 6f)
                close()
            }
        }.build()
    }

    val Tune: ImageVector by lazy {
        ImageVector.Builder("Tune", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(3f, 17f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(-2f)
                horizontalLineTo(3f)
                close()
                moveTo(3f, 5f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(10f)
                verticalLineTo(5f)
                horizontalLineTo(3f)
                close()
                moveTo(13f, 21f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(8f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(-8f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(2f)
                close()
                moveTo(7f, 9f)
                verticalLineToRelative(2f)
                horizontalLineTo(3f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(2f)
                verticalLineTo(9f)
                horizontalLineTo(7f)
                close()
                moveTo(21f, 13f)
                verticalLineToRelative(-2f)
                horizontalLineTo(11f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(10f)
                close()
                moveTo(17f, 9f)
                horizontalLineToRelative(2f)
                verticalLineTo(7f)
                horizontalLineToRelative(2f)
                verticalLineTo(5f)
                horizontalLineToRelative(-2f)
                verticalLineTo(3f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(6f)
                close()
            }
        }.build()
    }

    val ChevronRight: ImageVector by lazy {
        ImageVector.Builder("ChevronRight", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(10f, 6f)
                lineTo(8.59f, 7.41f)
                lineTo(13.17f, 12f)
                lineToRelative(-4.58f, 4.59f)
                lineTo(10f, 18f)
                lineToRelative(6f, -6f)
                close()
            }
        }.build()
    }

    val Cast: ImageVector by lazy {
        ImageVector.Builder("Cast", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(1f, 18f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(3f)
                curveToRelative(0f, -1.66f, -1.34f, -3f, -3f, -3f)
                close()
                moveTo(1f, 14f)
                verticalLineToRelative(2f)
                curveToRelative(2.76f, 0f, 5f, 2.24f, 5f, 5f)
                horizontalLineToRelative(2f)
                curveToRelative(0f, -3.87f, -3.13f, -7f, -7f, -7f)
                close()
                moveTo(1f, 10f)
                verticalLineToRelative(2f)
                curveToRelative(4.97f, 0f, 9f, 4.03f, 9f, 9f)
                horizontalLineToRelative(2f)
                curveToRelative(0f, -6.08f, -4.92f, -11f, -11f, -11f)
                close()
                moveTo(21f, 3f)
                horizontalLineTo(3f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(2f)
                verticalLineTo(5f)
                horizontalLineToRelative(18f)
                verticalLineToRelative(14f)
                horizontalLineToRelative(-7f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(7f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
            }
        }.build()
    }

    val GridView: ImageVector by lazy {
        ImageVector.Builder("GridView", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(3f, 3f)
                verticalLineToRelative(8f)
                horizontalLineToRelative(8f)
                lineTo(11f, 3f)
                lineTo(3f, 3f)
                close()
                moveTo(9f, 9f)
                lineTo(5f, 9f)
                lineTo(5f, 5f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                close()
                moveTo(3f, 13f)
                verticalLineToRelative(8f)
                horizontalLineToRelative(8f)
                verticalLineToRelative(-8f)
                lineTo(3f, 13f)
                close()
                moveTo(9f, 19f)
                lineTo(5f, 19f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                close()
                moveTo(13f, 3f)
                verticalLineToRelative(8f)
                horizontalLineToRelative(8f)
                lineTo(21f, 3f)
                horizontalLineToRelative(-8f)
                close()
                moveTo(19f, 9f)
                horizontalLineToRelative(-4f)
                lineTo(15f, 5f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                close()
                moveTo(13f, 13f)
                verticalLineToRelative(8f)
                horizontalLineToRelative(8f)
                verticalLineToRelative(-8f)
                horizontalLineToRelative(-8f)
                close()
                moveTo(19f, 19f)
                horizontalLineToRelative(-4f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                close()
            }
        }.build()
    }

    val Notifications: ImageVector by lazy {
        ImageVector.Builder("Notifications", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 22f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                horizontalLineToRelative(-4f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                close()
                moveTo(18f, 16f)
                verticalLineToRelative(-5f)
                curveToRelative(0f, -3.07f, -1.63f, -5.64f, -4.5f, -6.32f)
                lineTo(13.5f, 4f)
                curveToRelative(0f, -0.83f, -0.67f, -1.5f, -1.5f, -1.5f)
                reflectiveCurveToRelative(-1.5f, 0.67f, -1.5f, 1.5f)
                verticalLineToRelative(0.68f)
                curveTo(7.64f, 5.36f, 6f, 7.92f, 6f, 11f)
                verticalLineToRelative(5f)
                lineToRelative(-2f, 2f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(16f)
                verticalLineToRelative(-1f)
                lineToRelative(-2f, -2f)
                close()
            }
        }.build()
    }

    val Refresh: ImageVector by lazy {
        ImageVector.Builder("Refresh", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(17.65f, 6.35f)
                curveTo(16.2f, 4.9f, 14.21f, 4f, 12f, 4f)
                curveToRelative(-4.42f, 0f, -7.99f, 3.58f, -7.99f, 8f)
                reflectiveCurveToRelative(3.57f, 8f, 7.99f, 8f)
                curveToRelative(3.73f, 0f, 6.84f, -2.55f, 7.73f, -6f)
                horizontalLineToRelative(-2.08f)
                curveToRelative(-0.82f, 2.33f, -3.04f, 4f, -5.65f, 4f)
                curveToRelative(-3.31f, 0f, -6f, -2.69f, -6f, -6f)
                reflectiveCurveToRelative(2.69f, -6f, 6f, -6f)
                curveToRelative(1.66f, 0f, 3.14f, 0.69f, 4.22f, 1.78f)
                lineTo(13f, 11f)
                horizontalLineToRelative(7f)
                verticalLineTo(4f)
                lineToRelative(-2.35f, 2.35f)
                close()
            }
        }.build()
    }

    val TrendingUp: ImageVector by lazy {
        ImageVector.Builder("TrendingUp", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(16f, 6f)
                lineToRelative(2.29f, 2.29f)
                lineToRelative(-4.88f, 4.88f)
                lineToRelative(-4f, -4f)
                lineTo(2f, 16.59f)
                lineTo(3.41f, 18f)
                lineToRelative(6f, -6f)
                lineToRelative(4f, 4f)
                lineToRelative(6.3f, -6.29f)
                lineTo(22f, 12f)
                verticalLineTo(6f)
                close()
            }
        }.build()
    }
}

