import kotlin.test.*

internal class Test1 {

    @Test
    fun testParseInput() {
        assertEquals(
            parseInput(arrayOf("barChart", "projects:60", "practise:10", "other:30")),
            ChartData(chartType = ChartType.BarChart,
                data = listOf(Element(60, "projects"), Element(10, "practise"), Element(30, "other")),
                title = "",
                xLegend = "",
                yLegend = "")
        )
        assertEquals(
            parseInput(arrayOf("barChart", "--title", "CocktailChart", "wine:15", "rum:20", "soda:10")),
            ChartData(chartType = ChartType.BarChart,
            data = listOf(Element(15, "wine"), Element(20, "rum"), Element(10, "soda")),
            title = "CocktailChart",
            xLegend = "",
            yLegend = "")
        )
    }
}
