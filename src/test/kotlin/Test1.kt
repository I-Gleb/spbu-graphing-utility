import java.io.File
import kotlin.test.*

internal class Test1 {

    @Test
    fun testParseInput() {
        assertEquals(
            parseInput(arrayOf("barChart", "projects:60", "practise:10", "other:30", "out.png")),
            ChartData(chartType = ChartType.BarChart,
                data = listOf(Element(60, "projects"), Element(10, "practise"), Element(30, "other")), File("out.png"))
        )
        assertEquals(
            parseInput(arrayOf("barChart", "wine:15", "rum:20", "soda:10", "out.png")),
            ChartData(chartType = ChartType.BarChart,
            data = listOf(Element(15, "wine"), Element(20, "rum"), Element(10, "soda")), File("out.png")
            )
        )
    }
}
