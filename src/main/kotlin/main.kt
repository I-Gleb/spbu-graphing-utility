import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.file
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skija.*
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaRenderer
import org.jetbrains.skiko.SkiaWindow
import org.jetbrains.skiko.toBufferedImage
import java.awt.Dimension
import java.io.File
import java.lang.Float.min
import javax.imageio.ImageIO
import javax.swing.WindowConstants
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

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

data class ChartData(val chartType: ChartType, val data: List<Element>, val fileOut: File)

class ChartOptionsParser : CliktCommand() {
    val chartType by argument().enum<ChartType>(ignoreCase = true)
    val data by argument().file(canBeDir = false, mustExist = true, mustBeReadable = true)
    val fileOut by argument().file(canBeDir = false)
    override fun run() = Unit
}

fun parseInput(args: Array<String>): ChartData {
    val myParser = ChartOptionsParser()
    myParser.main(args)
    return ChartData(myParser.chartType, myParser.data.readLines().map { myParser.chartType.parser(it) }, myParser.fileOut)
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

    //saving chart to file
    val image = window.layer.screenshot()?.toBufferedImage()
    ImageIO.write(image, "png", chartInfo.fileOut)
}

class Renderer(val layer: SkiaLayer, val chartInfo: ChartData): SkiaRenderer {
    val typeface = Typeface.makeFromFile("fonts/JetBrainsMono-Regular.ttf")
    val signFont = Font(typeface, 20f)
    val blackColor = 0xff000000L.toInt()
    val blackTextPaint = Paint().apply { color = blackColor; mode = PaintMode.STROKE_AND_FILL }


    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        val contentScale = layer.contentScale
        canvas.scale(contentScale, contentScale)
        val w = width / contentScale
        val h = height / contentScale

        when (chartInfo.chartType) {
            ChartType.BarChart -> drawBarChart(canvas, w, h)
            ChartType.PieChart -> drawPieChart(canvas, w, h)
        }

        layer.needRedraw()
    }

    private fun randomFillPaint(seed: Int): Paint {
        val randomColor = 0x66000000L.toInt() + (0 until 256 * 256 * 256).random(Random(seed))
        return Paint().apply { color = randomColor; mode = PaintMode.STROKE_AND_FILL}
    }

    private fun randomTextPaint(seed: Int): Paint {
        val randomColor = 0xFF000000L.toInt() + (0 until 256 * 256 * 256).random(Random(seed))
        return Paint().apply { color = randomColor; mode = PaintMode.STROKE_AND_FILL}
    }

    private fun drawBarChart(canvas: Canvas, width: Float, height: Float) {

        val smallIndent = 10f

        val left = smallIndent
        val right = width - smallIndent
        val top = smallIndent
        val bottom = height - smallIndent

        val numberOfBars = chartInfo.data.size
        val bigIndent = 30f
        val barWidth = (right - left - bigIndent) / numberOfBars - bigIndent
        val k = (bottom - top - 2 * signFont.size - smallIndent) / chartInfo.data.maxOf { it.value }

        // draw bars and signs
        var currStart = left
        chartInfo.data.forEach {
            currStart += bigIndent

            canvas.drawRect(Rect(currStart, bottom - signFont.size - k * it.value, currStart + barWidth, bottom - signFont.size),
                randomFillPaint(currStart.toInt()))

            canvas.drawString(it.group_name!!, currStart + (barWidth - signFont.measureTextWidth(it.group_name)) / 2, bottom, signFont,
                randomTextPaint(currStart.toInt()))

            canvas.drawString(it.value.toString(), currStart + (barWidth - signFont.measureTextWidth(it.value.toString())) / 2, bottom - signFont.size - smallIndent - k * it.value, signFont,
                randomTextPaint(currStart.toInt()))

            currStart += barWidth
        }
    }

    private fun drawPieChart(canvas: Canvas, width: Float, height: Float) {

        fun degreesToRadians(x: Float): Double {
            return x * PI / 180
        }

        val indent = 5f
        val centerX = width / 2
        val centerY = height / 2
        val radius = min(width, height) / 2 - indent

        val left = centerX - radius
        val right = centerX + radius
        val top = centerY - radius
        val bottom = centerY + radius

        fun drawSector(startAng: Float, deltaAng: Float) {
            canvas.drawArc(left, top, right, bottom, startAng, deltaAng, true,
                randomFillPaint(startAng.toInt()))
        }

        fun drawSign(startAng: Float, deltaAng: Float, name: String, value: String) {
            val signAng = degreesToRadians(startAng + deltaAng / 2)

            val signCenterX = centerX + cos(-signAng).toFloat() * radius / 2
            val signCenterY = centerY - sin(-signAng).toFloat() * radius / 2

            val nameX = signCenterX - signFont.measureText(name).width / 2
            val nameY = signCenterY
            canvas.drawString(name, nameX, nameY, signFont, randomTextPaint(startAng.toInt()))

            val valueX = signCenterX - signFont.measureText(value).width / 2
            val valueY = signCenterY + signFont.size
            canvas.drawString(value, valueX, valueY, signFont, randomTextPaint(startAng.toInt()))
        }

        val sum = chartInfo.data.sumOf { it.value }
        var currStart = 0f

        chartInfo.data.forEach {
            val deltaAng = it.value * 360f / sum

            drawSector(currStart, deltaAng)
            drawSign(currStart, deltaAng, it.group_name!!, it.value.toString())

            currStart += deltaAng
        }
    }
}