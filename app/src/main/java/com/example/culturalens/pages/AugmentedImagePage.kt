package com.example.culturalens.pages

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.culturalens.R
import com.example.culturalens.activity
import com.example.culturalens.components.TopBar
import com.example.culturalens.models
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.addAugmentedImage
import io.github.sceneview.ar.arcore.getUpdatedAugmentedImages
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.math.Position
import io.github.sceneview.model.Model
import io.github.sceneview.node.ImageNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes

@Composable
fun AugmentedImagePage(navController: NavController) {
    Scaffold(
        topBar = { TopBar(navController = navController, title = "Tìm hình ảnh")}
    ) {paddingValues ->
        Column(Modifier.padding(paddingValues)){
            AugmentedImageView()
        }
    }
}

@Composable
fun AugmentedImageView() {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine = engine)
    val materialLoader =  rememberMaterialLoader(engine = engine)
    var frame by remember { mutableStateOf<Frame?>(null) }
    val cameraNode = remember {
        ARSceneView.createARCameraNode(engine)
    }
    val childNodes = rememberNodes()
    val modelCache = remember { HashMap<Int, Model>() }
    val aImgCache = remember { HashMap<String, AugmentedImageNode>() }
    val context = LocalContext.current
    ARScene(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        modelLoader = modelLoader,
        materialLoader = materialLoader,
        cameraNode = cameraNode,
        childNodes = childNodes,
        onSessionUpdated = { session, newFrame ->
            frame = newFrame
            frame!!.getUpdatedAugmentedImages().forEach { aImg ->
                if (!aImgCache.contains(aImg.name)) {
                    val aImgNode = AugmentedImageNode(engine, aImg).apply {
                        var node: Node? = null
                        if (aImg.name.startsWith("3D")) {
                            // load 3D model
                            val idx = aImg.name.substring(2).toInt()
                            node = ModelNode(
                                modelInstance = modelLoader.createInstance(
                                    modelCache.getOrPut(models[aImg.name.toInt()].modelID) {
                                        modelLoader.createModel(
                                            models[idx].modelID
                                        )
                                    }
                                )!!,
                                scaleToUnits = models[idx].scaleToUnits,
                            ).apply {
                                rotation = models[idx].rotation
                            }
                        } else {
                            // load 3D image
                            node = ImageNode(
                                materialLoader = materialLoader,
                                bitmap = BitmapFactory.decodeResource(
                                    activity.resources,
                                    aImg.name.toInt(),
                                ),
                                center = Position(y = 0.2f)
                            )
                        }
                        addChildNode(node)
                        onTrackingStateChanged = {
                            Toast.makeText(context, "Tracking State Change", Toast.LENGTH_LONG).show()
                            if (it != TrackingState.TRACKING) {
                                childNodes.remove(this)
                                node.destroy()
                                this.destroy()
                                aImgCache.remove(aImg.name)
                            }
                        }
                    }
                    aImgCache[aImg.name] = aImgNode
                    childNodes += aImgNode
                } else {
                    if (aImgCache[aImg.name] != null)
                        aImgCache[aImg.name]?.pose = aImg.centerPose
                }
            }

        },
        sessionConfiguration = { session: Session, config: Config ->
            config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            // Config database
            config.addAugmentedImage(
                session,
                (models.size - 1).toString(),
                BitmapFactory.decodeResource(activity.resources, R.drawable.notebook)
            )
            config.addAugmentedImage(
                session,
                R.drawable.trong_dong_descr.toString(),
                BitmapFactory.decodeResource(activity.resources, R.drawable.nb_augmented_img)
            )
            config.planeFindingMode = Config.PlaneFindingMode.DISABLED
        },
        onSessionPaused = {
            childNodes.forEach {
                it.destroy()
            }
            childNodes.clear()
        },
    )
    DisposableEffect(cameraNode) {
        onDispose { }
    }
}
