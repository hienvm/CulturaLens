package com.example.culturalens

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import com.example.culturalens.ui.theme.CulturaLensTheme
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Session
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.addAugmentedImage
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedAugmentedImages
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.math.Rotation
import io.github.sceneview.model.Model
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

//val models = HashMap<Int, ModelWrapper>
//
class ModelWrapper(val modelID: Int, val imgID: Int,val scaleToUnits: Float = 1f,val rotation: Rotation = Rotation(),) {}

val models = listOfNotNull(
    ModelWrapper(modelID = R.raw.trong_dong_4k, imgID = R.drawable.trong_dong_4k, scaleToUnits = 0.2f,),
    ModelWrapper(modelID = R.raw.damaged_helmet, imgID = R.drawable.img, scaleToUnits = 0.5f),
    ModelWrapper(modelID = R.raw.velociraptor, imgID = R.drawable.chuong, scaleToUnits = 100f),
    ModelWrapper(modelID = R.raw.halloween, imgID = R.drawable.dep_to_ong, scaleToUnits = 0.02f),
    ModelWrapper(modelID = R.raw.trong_dong_4k, imgID = R.drawable.trong_dong_4k, scaleToUnits = 0.015f, rotation = Rotation(x = -90f),),
    ModelWrapper(modelID = R.raw.trong_dong_4k, imgID = R.drawable.trong_dong_4k, scaleToUnits = 0.4f,),
    ModelWrapper(modelID = R.raw.trong_dong_4k, imgID = R.drawable.trong_dong_4k, scaleToUnits = 0.4f,),
    ModelWrapper(modelID = R.raw.trong_dong_4k, imgID = R.drawable.trong_dong_4k, scaleToUnits = 0.4f,),
    ModelWrapper(modelID = R.raw.trong_dong_4k, imgID = R.drawable.trong_dong_4k, scaleToUnits = 0.4f,),
    ModelWrapper(modelID = R.raw.trong_dong_4k, imgID = R.drawable.trong_dong_4k, scaleToUnits = 0.4f,),
)

var curModel: ModelWrapper? = null

lateinit var activity: ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            Home()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text(text = "Chuyên đề cn") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    )
}

@Composable
private fun Home() {
    CulturaLensTheme {
        Scaffold(topBar = { TopBar() }, bottomBar = { Bottom() }) {
            Column(
                modifier = Modifier
                    .padding(it)
            ) {
                ArView()
            }
        }
    }
}

@Preview(device = "id:small_phone", showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE
)
@Composable
fun Preview() {
    Home()
}

@Composable
fun Bottom() {
    LazyRow(Modifier.height(100.dp)) {
        for (e in models) {
            item {
                Image(
                    painter = painterResource(id = e.imgID),
                    contentDescription = e.modelID.toString(),
                    Modifier
                        .clickable(enabled = true) {
                            curModel = e
                        }
                )
            }
        }
    }
}

@Composable
fun ArView() {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine = engine)
    val materialLoader = rememberMaterialLoader(engine = engine)
    val cameraNode = rememberARCameraNode(engine = engine)
    var frame by remember { mutableStateOf<Frame?>(null) }
    val view = rememberView(engine = engine)
    val collisionSystem = rememberCollisionSystem(view = view)
    val viewNodeWindowManager = rememberViewNodeManager()
    var planeRenderer by rememberSaveable { mutableStateOf(true) }
    val mPlayer = MediaPlayer.create(LocalContext.current, R.raw.wood_snare_2)

    val childNodes = rememberNodes()
    val modelCache = remember { HashMap<Int, Model>() }
    val aImgCache = remember { HashMap<String, AugmentedImageNode>() }
    ARScene(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        modelLoader = modelLoader,
        materialLoader = materialLoader,
        cameraNode = cameraNode,
        onSessionUpdated = { session, newFrame ->
            frame = newFrame
            frame!!.getUpdatedAugmentedImages().forEach {aImg ->
                if (!aImgCache.contains(aImg.name)) {
                    val aImgNode = AugmentedImageNode(engine, aImg).apply {
                        if (aImg.name == "trong_dong") {
                            curModel = models[0]
                            addChildNode(
                                ModelNode(
                                    modelInstance = modelLoader.createInstance(
                                        modelCache.getOrPut(curModel!!.modelID){ modelLoader.createModel(curModel!!.modelID) }
                                    )!!,
                                    scaleToUnits = curModel!!.scaleToUnits,
                                ).apply {
                                    rotation = curModel!!.rotation
                                }
                            )
                        }
                    }
                    aImgCache[aImg.name] = aImgNode
                    childNodes += aImgNode
                } else {
                    aImgCache[aImg.name]!!.pose = aImg.centerPose
                }
            }
        },
        planeRenderer = planeRenderer,
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
                if (anchor != null && curModel != null && modelCache[curModel!!.modelID] == null) {
                    planeRenderer = false
                    val anchorNode = AnchorNode(engine = engine, anchor = anchor)
                    anchorNode.addChildNode(
                        ModelNode(
                            modelInstance = modelLoader.createInstance(
                                modelCache.getOrPut(curModel!!.modelID){ modelLoader.createModel(curModel!!.modelID) }
                            )!!,
                            scaleToUnits = curModel!!.scaleToUnits,
                        ).apply {
                            rotation = curModel!!.rotation
                            isEditable = true
                            if(curModel!!.modelID == R.raw.trong_dong_4k) {
                                onSingleTapConfirmed = {
                                    mPlayer.start()
                                    true
                                }
                            }
                        }
                    )
//                    anchorNode.addChildNode(
//                        ViewNode(engine, viewNodeWindowManager, materialLoader, R.layout.test).apply {
//                            scale = Scale(z = .3f, y = .3f)
//                            position = Position(z = 1f)
//                            lookAt(cameraNode)
//                        }
//                    )
                    childNodes += anchorNode
                }
            }
        ),
        sessionConfiguration = { session: Session, config: Config ->
            config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            config.addAugmentedImage(
                session,
                "trong_dong",
                BitmapFactory.decodeResource(activity.resources, R.drawable.aimg)
            )
//            config.depthMode = Config.DepthMode.AUTOMATIC
//            config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
//            config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL

        },
//        cameraStream = ARCameraStream(materialLoader = rememberMaterialLoader(engine = engine)).apply {
//            isDepthOcclusionEnabled = true
//        },
        onViewCreated = {
            this.planeRenderer.isShadowReceiver = false
        }
    )
}