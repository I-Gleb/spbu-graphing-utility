import java.io.File
import kotlin.test.*

internal class Test1 {

    @Test
    fun testParseInput() {
        assertEquals(
            parseInput(arrayOf("barChart", "assets/case1/data.txt", "out.png")),
            ChartData(chartType = ChartType.BarChart,
                data = listOf(Element(60, "projects"), Element(10, "practise"), Element(10, "essay"), Element(20, "other")), File("out.png"))
        )
        assertEquals(
            parseInput(arrayOf("barChart", "assets/case2/data.txt", "out.png")),
            ChartData(chartType = ChartType.BarChart,
            data = listOf(Element(15, "wine"), Element(20, "rum"), Element(10, "soda")), File("out.png")
            )
        )
    }
}
