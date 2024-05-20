package com.example.culturalens.pages

import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.culturalens.ModelWrapper
import com.example.culturalens.R
import com.example.culturalens.components.Header
import com.example.culturalens.components.ModelChoiceBar
import com.example.culturalens.components.TopBar
import com.example.culturalens.models
import com.google.android.filament.Engine
import io.github.sceneview.Scene
import io.github.sceneview.animation.Transition.animateRotation
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.model.Model
import io.github.sceneview.node.CameraNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.github.sceneview.rememberNodes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@Composable
fun GalleryPage(navController: NavController) {
    var curModel: ModelWrapper? by remember { mutableStateOf(null) }
    val childNodes = rememberNodes()
    val modelCache = rememberSaveable { HashMap<Int, Model>() }
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val cameraNode = remember {
        ARSceneView.createARCameraNode(engine).apply {
            position = Position(z = 4.0f)
        }
    }
    Scaffold(
        topBar = { TopBar(navController = navController, title = "Chọn đồ vật để trình chiếu") },
        bottomBar = {
            ModelChoiceBar(showChoices = true, getCurModel = { curModel }) {
                curModel = it
                childNodes.forEach {
                    it.destroy()
                }
                childNodes.clear()

                val model = ModelNode(
                    modelInstance = modelLoader.createInstance(
                        modelCache.getOrPut(curModel!!.modelID) {
                            modelLoader.createModel(
                                curModel!!.modelID
                            )
                        }
                    )!!,
                    scaleToUnits = curModel!!.scaleToUnits,
                ).apply {
                    rotation = curModel!!.rotation
                    isEditable = true
                }
                cameraNode.position = Position(
                    x = 0f - curModel!!.position.x,
                    y = 0f - curModel!!.position.y,
                    z = 4f - curModel!!.position.z,
                )
                childNodes.add(model)
            }
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(paddingValues),
        ) {
            Header()
            Divider(
                color = MaterialTheme.colors.primaryVariant,
                thickness = 1.dp,
                modifier = Modifier,
            )
            GalleryView(
                engine = engine,
                modelLoader = modelLoader,
                childNodes = childNodes,
                cameraNode = cameraNode,
            )
        }
    }

}

@Composable
fun GalleryView(engine: Engine, modelLoader: ModelLoader, childNodes: SnapshotStateList<Node>, cameraNode: CameraNode) {
    val environmentLoader = rememberEnvironmentLoader(engine)


    Scene(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        modelLoader = modelLoader,
        cameraNode = cameraNode,
        childNodes = childNodes,
        environment = environmentLoader.createHDREnvironment(R.raw.sky_2k)!!,
    )
    DisposableEffect(cameraNode) {
        onDispose { }
    }
}
