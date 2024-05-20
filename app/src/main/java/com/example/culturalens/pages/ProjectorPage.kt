package com.example.culturalens.pages

import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.culturalens.ModelWrapper
import com.example.culturalens.R
import com.example.culturalens.components.TopBar
import com.example.culturalens.activity
import com.example.culturalens.components.ModelChoiceBar
import com.example.culturalens.components.OFFSET
import com.example.culturalens.ui.theme.CulturaLensTheme
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Session
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.camera.ARCameraStream
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.model.Model
import io.github.sceneview.node.ImageNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import io.github.sceneview.rememberViewNodeManager

@Composable
fun ProjectorPage(navController: NavController) {
    val context = LocalContext.current
    var curModel: ModelWrapper? by rememberSaveable{ mutableStateOf(null) }
    var planeRenderer by rememberSaveable { mutableStateOf(true) }
    var occlusion by rememberSaveable { mutableStateOf(false) }
    var expandDropDown by remember { mutableStateOf(false) }
    CulturaLensTheme {
        val childNodes = rememberNodes()
        var showChoices by rememberSaveable { mutableStateOf(true) }
        Scaffold(
            topBar = {
                TopBar(
                    navController = navController,
                    title = if (curModel == null) "Chọn đồ vật" else "Đặt đồ vật",
                    actions = {
                        IconToggleButton(
                            checked = expandDropDown,
                            onCheckedChange = {
                                expandDropDown = it
                            },
                            Modifier.size(OFFSET)
                        ) {
                            Icon(Icons.Default.Settings, "Settings", modifier = Modifier.size(OFFSET),)
                            DropdownMenu(
                                expanded = expandDropDown,
                                onDismissRequest = { expandDropDown = false },
                                offset = DpOffset(20.dp,12.dp),
                                modifier = Modifier.background(color = Color.DarkGray)
                            ) {
                                DropdownMenuItem(onClick = { planeRenderer = !planeRenderer }) {
                                    Checkbox(checked = planeRenderer, onCheckedChange = {
                                        planeRenderer = it
                                    } )
                                    Text(text = "Plane Renderer", color = Color.White)
                                }
                                DropdownMenuItem(onClick = { occlusion = !occlusion }) {
                                    Checkbox(checked = occlusion, onCheckedChange = {
                                        occlusion = it
                                    } )
                                    Text(text = "Occlusion", color = Color.White)
                                }
                            }
                        }
                    }
                )
            },
            bottomBar = {
                ModelChoiceBar(showChoices, getCurModel = { curModel }, setCurModel = { curModel = it})
            },
            floatingActionButton = {
                key(showChoices) {
                    Column (verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        FloatingActionButton(
                            onClick = {
                                for (node in childNodes) {
                                    node.destroy()
                                }
                                childNodes.clear()
                            },
                            backgroundColor = Color.Red,
                            contentColor = Color.White,
                        ) {
                            Icon(Icons.Default.Delete, "Clear models")
                        }
                        FloatingActionButton(
                            onClick = { showChoices = !showChoices },
                            backgroundColor = MaterialTheme.colors.secondary,
                            contentColor = MaterialTheme.colors.onSecondary,
                        ) {
                            if (showChoices) {
                                Icon(Icons.Default.Close, "Close choice bar")
                            } else {
                                Icon(Icons.Default.Add, "Add a model")
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                ProjectorView(
                    getCurModel = { curModel },
                    setCurModel = { curModel = it },
                    childNodes = childNodes,
                    getPlaneRenderer = { planeRenderer },
                    getOcclusion = {occlusion},
                )
            }
        }
    }
}

@Composable
fun ProjectorView(
    getCurModel: () -> ModelWrapper?,
    setCurModel: (ModelWrapper?) -> Unit,
    childNodes: SnapshotStateList<Node>,
    getPlaneRenderer: () -> Boolean, getOcclusion: () -> Boolean,
) {
    val context = LocalContext.current
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine = engine)
    val materialLoader = rememberMaterialLoader(engine = engine)
    val cameraNode = remember {
        ARSceneView.createARCameraNode(engine)
    }
    var frame by remember { mutableStateOf<Frame?>(null) }
    val view = rememberView(engine = engine)
    val collisionSystem = rememberCollisionSystem(view = view)
    val viewNodeWindowManager = rememberViewNodeManager()
    val mPlayer = MediaPlayer.create(context, R.raw.wood_snare_2)
    val modelCache = rememberSaveable { HashMap<Int, Model>() }

    ARScene(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        modelLoader = modelLoader,
        materialLoader = materialLoader,
        cameraNode = cameraNode,
        onSessionUpdated = { session, newFrame ->
            frame = newFrame
        },
        planeRenderer = getPlaneRenderer(),
        collisionSystem = collisionSystem,
        view = view,
        childNodes = childNodes,
        viewNodeWindowManager = viewNodeWindowManager,
        onGestureListener = rememberOnGestureListener(
            onSingleTapConfirmed = { e: MotionEvent, node: Node? ->
                val hitResults = frame?.hitTest(e.x, e.y)

                val anchor = hitResults?.firstOrNull {
                    it.isValid(depthPoint = false, point = false)
                }?.createAnchorOrNull()
                if (anchor != null && getCurModel() != null) {
                    Toast.makeText(context, "Đặt đồ vật: " + getCurModel()!!.name, Toast.LENGTH_LONG).show()
                    val anchorNode = AnchorNode(engine = engine, anchor = anchor)
//                    anchorNode.position = Position(
//                        x = anchorNode.position.x + getCurModel()!!.position.x,
//                        y = anchorNode.position.y + getCurModel()!!.position.y,
//                        z = anchorNode.position.z + getCurModel()!!.position.z,
//                    )
                    // place descr
                    val imgNode = ImageNode(
                        materialLoader = materialLoader,
                        bitmap = BitmapFactory.decodeResource(
                            activity.resources,
                            getCurModel()!!.descrImgID,
                        ),
                        center = Position(y = 1.2f)
                    )
                    anchorNode.addChildNode(
                        ModelNode(
                            modelInstance = modelLoader.createInstance(
                                modelCache.getOrPut(getCurModel()!!.modelID) {
                                    modelLoader.createModel(
                                        getCurModel()!!.modelID
                                    )
                                }
                            )!!,
                            scaleToUnits = getCurModel()!!.scaleToUnits,
                            centerOrigin = getCurModel()!!.position,
                        ).apply {
                            rotation = getCurModel()!!.rotation
                            isEditable = true
                            if (getCurModel()!!.modelID == R.raw.trong_dong_4k) {
                                onSingleTapConfirmed = {
                                    mPlayer.start()
                                    true
                                }
                            }
                            onDoubleTap = {
                                imgNode.isVisible = !imgNode.isVisible
                                true
                            }
                        }
                    )
                    anchorNode.addChildNode(imgNode)
                    childNodes += anchorNode
                    setCurModel(null)
                }
            }
        ),
        sessionConfiguration = { session: Session, config: Config ->
            config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            config.depthMode = if (getOcclusion()) Config.DepthMode.AUTOMATIC else Config.DepthMode.DISABLED
//            config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
            config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
        },
        cameraStream = ARCameraStream(materialLoader = rememberMaterialLoader(engine = engine)).apply {
            isDepthOcclusionEnabled = getOcclusion()
        },
        onViewCreated = {
            this.planeRenderer.isShadowReceiver = false
        },
        onSessionPaused = {
            childNodes.forEach {
                it.destroy()
            }
            childNodes.clear()
        }
    )
    DisposableEffect(cameraNode) {
        onDispose { }
    }
}

//    LazyRow(
//        Modifier
//            .height(100.dp)) {
//        models.forEach() {
//            item {
//                Image(
//                    painter = painterResource(id = it.imgID),
//                    contentDescription = it.modelID.toString(),
//                    Modifier
//                        .clickable(enabled = true) {
//                            curModel = it
//                        }
//                )
//            }
//        }
//    }
