package com.teethcure.demo

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.Locale
import java.util.concurrent.Executors
import kotlinx.coroutines.delay

@Composable
fun TeethCureApp(viewModel: TeethCureViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.brushing.resultMessage) {
        uiState.brushing.resultMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.resetResultMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.selectedTab != AppTab.Brushing) {
                NavigationBar {
                    AppTab.entries
                        .filter { it != AppTab.Brushing }
                        .forEach { tab ->
                            NavigationBarItem(
                                selected = tab == uiState.selectedTab,
                                onClick = { viewModel.selectTab(tab) },
                                icon = { Box(modifier = Modifier.size(0.dp)) },
                                label = { Text(tab.label) },
                            )
                        }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFEAF4FF)),
        ) {
            when (uiState.selectedTab) {
                AppTab.WorldMap -> WorldMapScreen(
                    uiState = uiState,
                    onStartBrushing = viewModel::startSession,
                )

                AppTab.Brushing -> BrushingScreen(viewModel = viewModel, uiState = uiState)
                AppTab.Collection -> CollectionScreen(uiState = uiState)
            }
        }
    }
}

@Composable
private fun WorldMapScreen(
    uiState: AppUiState,
    onStartBrushing: () -> Unit,
) {
    val backgroundBitmap = rememberAssetBitmap("world_map/WorldBackground.png")
    val toothBitmap = rememberAssetBitmap("world_map/tooth.png")
    val regionBitmaps = GameCatalog.regions.map { region ->
        region to rememberAssetBitmap(region.assetPath)
    }
    val worldPlacements = listOf(
        WorldPlacement(regionIndex = 0, widthFraction = 0.36f, xFraction = 0.08f, yFraction = 0.64f),
        // WorldPlacement(regionIndex = 1, widthFraction = 0.36f, xFraction = 0.02f, yFraction = 0.31f),
        WorldPlacement(regionIndex = 1, widthFraction = 0.39f, xFraction = 0.03f, yFraction = 0.30f),
        WorldPlacement(regionIndex = 2, widthFraction = 0.42f, xFraction = 0.28f, yFraction = 0.05f),
        WorldPlacement(regionIndex = 3, widthFraction = 0.39f, xFraction = 0.56f, yFraction = 0.30f),
        WorldPlacement(regionIndex = 4, widthFraction = 0.40f, xFraction = 0.56f, yFraction = 0.60f),
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
    ) {
        Text(
            text = "TeethKeeth Kingdom",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Regions stay grayscale until every character in that region is unlocked.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3E5E7A),
        )
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFB8D3EB), RoundedCornerShape(16.dp))
                .background(Color.White),
        ) {
            if (backgroundBitmap == null || toothBitmap == null || regionBitmaps.any { (_, bitmap) -> bitmap == null }) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading map...")
                }
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val grayMatrix = ColorMatrix().apply { setToSaturation(0f) }

                    Image(
                        bitmap = backgroundBitmap,
                        contentDescription = "World background",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )

                    worldPlacements.forEach { placement ->
                        val region = GameCatalog.regions[placement.regionIndex]
                        val bitmap = regionBitmaps[placement.regionIndex].second!!
                        val isRegionUnlocked = region.characterIds.all { it in uiState.unlockedCharacterIds }

                        Image(
                            bitmap = bitmap,
                            contentDescription = region.name,
                            modifier = Modifier
                                .fillMaxWidth(placement.widthFraction)
                                .offset(
                                    x = maxWidth * placement.xFraction,
                                    y = maxHeight * placement.yFraction,
                                ),
                            colorFilter = if (isRegionUnlocked) null else ColorFilter.colorMatrix(grayMatrix),
                        )
                    }

                    Image(
                        bitmap = toothBitmap,
                        contentDescription = "Center tooth",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(0.11f),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Progress: ${uiState.unlockedCharacterIds.size}/${GameCatalog.characters.size}")
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onStartBrushing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("양치 시작")
        }
    }
}

private data class WorldPlacement(
    val regionIndex: Int,
    val widthFraction: Float,
    val xFraction: Float,
    val yFraction: Float,
)

@Composable
private fun BrushingScreen(
    viewModel: TeethCureViewModel,
    uiState: AppUiState,
) {
    val brushing = uiState.brushing
    val target = uiState.targetCharacter
    val targetBitmap = rememberAssetBitmap(target.assetPath)
    var cameraGranted by rememberCameraPermissionState()
    var debugSkeleton by remember { mutableStateOf(SkeletonFrame.Empty) }
    var trackerStatus by remember { mutableStateOf("Initializing") }
    val context = LocalContext.current

    val tracker = remember(context) { MediaPipeHandMouthTracker(context) }

    DisposableEffect(brushing.active, tracker) {
        if (brushing.active) {
            tracker.start(
                onStrokeDetected = { zone -> viewModel.applyStroke(zone) },
                onSkeletonDetected = { frame -> debugSkeleton = frame },
                onStatusChanged = { status -> trackerStatus = status },
            )
        } else {
            tracker.stop()
            debugSkeleton = SkeletonFrame.Empty
        }
        onDispose {
            tracker.stop()
            debugSkeleton = SkeletonFrame.Empty
        }
    }

    LaunchedEffect(brushing.active) {
        while (brushing.active) {
            delay(1000L)
            viewModel.tickSession()
        }
    }

    val elapsed = brushing.elapsedSec
    val total = GameCatalog.SESSION_DURATION_SEC

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "Elapsed: ${formatSec(elapsed)} / ${formatSec(total)}",
                fontWeight = FontWeight.Bold,
            )
            LinearProgressIndicator(
                progress = { elapsed / total.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFD8EBFF)),
                contentAlignment = Alignment.Center,
            ) {
                if (targetBitmap != null) {
                    Image(
                        bitmap = targetBitmap,
                        contentDescription = target.name,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                FoamOverlay(
                    leftProgress = brushing.leftStrokes / 50f,
                    centerProgress = brushing.centerStrokes / 50f,
                    rightProgress = brushing.rightStrokes / 50f,
                )
            }

            Text("Purification: ${brushing.purification}/3")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ZonePill("L ${brushing.leftStrokes}/50", brushing.leftDone)
                ZonePill("C ${brushing.centerStrokes}/50", brushing.centerDone)
                ZonePill("R ${brushing.rightStrokes}/50", brushing.rightDone)
            }

            Text(
                text = "MediaPipe status: $trackerStatus",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF4C5F72),
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (!brushing.active) {
                    Button(onClick = { viewModel.startSession() }) {
                        Text("Restart")
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(0.8f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF102033)),
        ) {
            if (cameraGranted) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onFrame = { imageProxy ->
                        tracker.processFrame(imageProxy, isFrontCamera = true)
                    },
                )
                SkeletonOverlay(
                    frame = debugSkeleton,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Camera permission required.", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SkeletonOverlay(
    frame: SkeletonFrame,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        fun toOffset(p: SkeletonPoint): Offset = Offset(p.x * size.width, p.y * size.height)

        val face = frame.facePoints
        val hand = frame.handPoints
        val lineColor = Color(0xFF74FFCC)
        val pointColor = Color(0xFF1BE2FF)

        if (face.size >= 7) {
            val facePairs = listOf(
                0 to 2,
                1 to 2,
                3 to 4,
                4 to 5,
                4 to 6,
            )
            facePairs.forEach { (a, b) ->
                drawLine(
                    color = lineColor,
                    start = toOffset(face[a]),
                    end = toOffset(face[b]),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round,
                )
            }
        }

        if (hand.size >= 2) {
            (0 until hand.lastIndex).forEach { i ->
                drawLine(
                    color = lineColor,
                    start = toOffset(hand[i]),
                    end = toOffset(hand[i + 1]),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round,
                )
            }
        }

        face.forEach { point ->
            drawCircle(color = pointColor, radius = 6f, center = toOffset(point))
        }
        hand.forEach { point ->
            drawCircle(color = pointColor, radius = 6f, center = toOffset(point))
        }
    }
}

private fun formatSec(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return String.format(Locale.US, "%02d:%02d", m, s)
}

@Composable
private fun rememberCameraPermissionState(): androidx.compose.runtime.MutableState<Boolean> {
    val context = LocalContext.current
    val granted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        granted.value = it
    }
    LaunchedEffect(Unit) {
        if (!granted.value) launcher.launch(Manifest.permission.CAMERA)
    }
    return granted
}

@Composable
private fun ZonePill(text: String, done: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (done) Color(0xFFB5F5C8) else Color(0xFFE5ECF3))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(text)
    }
}

@Composable
private fun FoamOverlay(
    leftProgress: Float,
    centerProgress: Float,
    rightProgress: Float,
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val maxRadius = size.minDimension * 0.28f
        val alphaBase = 0.18f
        if (leftProgress > 0f) {
            drawCircle(
                color = Color.White.copy(alpha = alphaBase + leftProgress * 0.55f),
                radius = maxRadius * leftProgress.coerceIn(0f, 1f),
                center = Offset(size.width * 0.23f, size.height * 0.56f),
            )
        }
        if (centerProgress > 0f) {
            drawCircle(
                color = Color.White.copy(alpha = alphaBase + centerProgress * 0.55f),
                radius = maxRadius * centerProgress.coerceIn(0f, 1f),
                center = Offset(size.width * 0.5f, size.height * 0.58f),
            )
        }
        if (rightProgress > 0f) {
            drawCircle(
                color = Color.White.copy(alpha = alphaBase + rightProgress * 0.55f),
                radius = maxRadius * rightProgress.coerceIn(0f, 1f),
                center = Offset(size.width * 0.76f, size.height * 0.56f),
            )
        }
    }
}

@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    onFrame: (ImageProxy) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val analyzerExecutor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose { analyzerExecutor.shutdown() }
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener(
                {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build()
                    preview.surfaceProvider = previewView.surfaceProvider

                    val analysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .build()
                    analysis.setAnalyzer(analyzerExecutor) { imageProxy ->
                        runCatching {
                            onFrame(imageProxy)
                        }.onFailure {
                            imageProxy.close()
                        }
                    }

                    runCatching {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_FRONT_CAMERA,
                            preview,
                            analysis,
                        )
                    }
                },
                ContextCompat.getMainExecutor(ctx),
            )
            previewView
        },
        update = {},
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CollectionScreen(uiState: AppUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
    ) {
        Text(
            text = "Character Collection",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text("Unlocked: ${uiState.unlockedCharacterIds.size}/${GameCatalog.characters.size}")
        Spacer(modifier = Modifier.height(10.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(GameCatalog.characters, key = { it.id }) { character ->
                val bitmap = rememberAssetBitmap(character.assetPath)
                val unlocked = character.id in uiState.unlockedCharacterIds
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.8f)
                        .clickable(enabled = unlocked) {},
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = character.name,
                                    colorFilter = if (unlocked) null else ColorFilter.colorMatrix(
                                        ColorMatrix().apply { setToSaturation(0f) },
                                    ),
                                )
                            } else {
                                Text("...")
                            }
                        }
                        Text(
                            text = if (unlocked) character.name else "Locked",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}
