import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.types.enum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skija.*
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaRenderer
import org.jetbrains.skiko.SkiaWindow
import java.awt.Dimension
import java.lang.Integer.min
import javax.swing.WindowConstants
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class ChartType(val parser: (String) -> Element) {
    BarChart({s ->
        try {
            val args = s.split(":")
            Element(args[1].toInt(), args[0])
        }
        catch (e: Exception) {
            throw TODO()
        }
    }),
    PieChart({s ->
        try {
            val args = s.split(":")
            Element(args[1].toInt(), args[0])
        }
        catch (e: Exception) {
            throw TODO()
        }
    })
}

data class Element(val value: Int, val group_name: String? = null, val x: Int? = null)

data class ChartData(val chartType: ChartType, val data: List<Element>)

class ChartOptionsParser : CliktCommand() {
    val chartType by argument().enum<ChartType>(ignoreCase = true)
    val data by argument().multiple(required = true)
    override fun run() = Unit
}

fun parseInput(args: Array<String>): ChartData {
    val myParser = ChartOptionsParser()
    myParser.main(args)
    return ChartData(myParser.chartType, myParser.data.map { myParser.chartType.parser(it) })
}

fun main(args: Array<String>) {
    val input = parseInput(args)
    createWindow("pf-2021-viz", input)
}

fun createWindow(title: String, chartInfo: ChartData) = runBlocking(Dispatchers.Swing) {
    val window = SkiaWindow()
    window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    window.title = title

    window.layer.renderer = Renderer(window.layer, chartInfo)

    window.preferredSize = Dimension(800, 600)
    window.minimumSize = Dimension(100,100)
    window.pack()
    window.layer.awaitRedraw()
    window.isVisible = true
}

class Renderer(val layer: SkiaLayer, val chartInfo: ChartData): SkiaRenderer {
    val typeface = Typeface.makeFromFile("fonts/JetBrainsMono-Regular.ttf")
    val indent = 5
    val blackColor = 0xff000000L.toInt()

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        val contentScale = layer.contentScale
        canvas.scale(contentScale, contentScale)
        val w = (width / contentScale).toInt()
        val h = (height / contentScale).toInt()

        when (chartInfo.chartType) {
            ChartType.BarChart -> drawBarChart(canvas, w, h)
            ChartType.PieChart -> drawPieChart(canvas, w, h)
        }

        layer.needRedraw()
    }

    private fun drawBarChart(canvas: Canvas, width: Int, height: Int) {
        TODO()
    }

    private fun drawPieChart(canvas: Canvas, width: Int, height: Int) {

        fun degreesToRadians(x: Float): Double {
            return x * PI / 180
        }

        val signFont = Font(typeface, 20f)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(width, height) / 2f - indent

        val left = centerX - radius
        val right = centerX + radius
        val top = centerY - radius
        val bottom = centerY + radius

        fun drawSector(startAng: Float, deltaAng: Float) {
            canvas.drawArc(left, top, right, bottom, startAng, deltaAng, true,
                Paint().apply { color = blackColor; mode = PaintMode.STROKE })
        }

        fun drawSign(startAng: Float, deltaAng: Float, s: String) {
            val signAng = degreesToRadians(startAng + deltaAng / 2)
            val signX = centerX + cos(-signAng).toFloat() * radius / 2 - signFont.measureTextWidth(s) / 2
            val signY = centerY - sin(-signAng).toFloat() * radius / 2 + signFont.size / 2f
            canvas.drawString(s, signX, signY, signFont,
                Paint().apply { color = blackColor; mode = PaintMode.STROKE_AND_FILL })
        }

        val sum = chartInfo.data.sumOf { it.value }
        var currStart = 0f

        chartInfo.data.forEach {
            val deltaAng = it.value * 360f / sum

            drawSector(currStart, deltaAng)
            drawSign(currStart, deltaAng, it.group_name!!)

            currStart += deltaAng
        }
    }
}