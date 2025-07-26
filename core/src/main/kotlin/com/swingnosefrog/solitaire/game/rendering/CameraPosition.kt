package com.swingnosefrog.solitaire.game.rendering

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils


data class CameraPosition(
    val x: Float,
    val y: Float,
    val zoom: Float,
) {
    
    fun lerp(other: CameraPosition, alpha: Float): CameraPosition {
        return CameraPosition(MathUtils.lerp(this.x, other.x, alpha), MathUtils.lerp(this.y, other.y, alpha), MathUtils.lerp(this.zoom, other.zoom, alpha))
    }
    
    fun applyToCamera(camera: OrthographicCamera) {
        camera.zoom = this.zoom
        camera.position.x = this.x
        camera.position.y = this.y
    }
}
