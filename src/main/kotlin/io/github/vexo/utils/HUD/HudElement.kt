package io.github.vexo.utils.HUD

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.WorldRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color
import net.minecraft.client.Minecraft
import org.lwjgl.input.Mouse


open class HudElement(
    var x: Float,
    var y: Float,
    var scale: Float,
    var enabled: Boolean = true,
    val render: (Boolean) -> Pair<Number, Number> = { _ -> 0f to 0f }
) {
    var width = 0f
        private set
    var height = 0f
        private set

    fun draw(example: Boolean) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, 1f)
        GlStateManager.scale(scale, scale, 1f)
        val (width, height) = render(example).let { (w, h) -> w.toFloat() to h.toFloat() }

        if (example) hollowRect(0f, 0f, width, height, 1 / scale + if (isHovered()) 2f else 0f, Color.WHITE)

        GlStateManager.popMatrix()

        this.width = width
        this.height = height
    }

    fun isHovered(): Boolean = isAreaHovered(x, y, width * scale, height * scale)
}

val tessellator: Tessellator = Tessellator.getInstance()
val worldRenderer: WorldRenderer = tessellator.worldRenderer
val mc = Minecraft.getMinecraft()


inline operator fun WorldRenderer.invoke(block: WorldRenderer.() -> Unit) {
    block(this)
}

fun hollowRect(x: Float, y: Float, width: Float, height: Float, thickness: Float, color: Color) {
    val left = x
    val right = x + width
    val top = y
    val bottom = y + height

    GlStateManager.enableBlend()
    GlStateManager.disableTexture2D()
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
    color.bind()

    GL11.glLineWidth(thickness)
    worldRenderer {
        begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION)
        pos(left.toDouble(),  bottom.toDouble(), 0.0).endVertex()
        pos(right.toDouble(), bottom.toDouble(), 0.0).endVertex()
        pos(right.toDouble(), top.toDouble(),    0.0).endVertex()
        pos(left.toDouble(),  top.toDouble(),    0.0).endVertex()
    }
    tessellator.draw()

    GlStateManager.enableTexture2D()
    GlStateManager.disableBlend()
}

fun Color.bind() {
    GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f)
}

inline val mouseX: Float get() =
    Mouse.getX().toFloat()

inline val mouseY: Float get() =
    mc.displayHeight - Mouse.getY() - 1f

fun isAreaHovered(x: Float, y: Float, w: Float, h: Float): Boolean =
mouseX in x..x + w && mouseY in y..y + h

fun isAreaHovered(x: Float, y: Float, w: Float): Boolean =
    mouseX in x..x + w && mouseY >= y


