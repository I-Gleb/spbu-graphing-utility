# Data visualization
## Project made for programming basics course at Department of Mathematics and Computer Science, St. Petersburg State University

[Task definition](./TASK.md)

### Documentation

This console utility creates charts and graphs based on data provided by the user as a file, displays them in a separate window and saves them in png format.

#### Supported chart types

* BarChart - bar chart
* PieChart - pie chart
* ScatterPlot - point distribution

#### Input data format

As an input utility takes following values separated by a space: the chart type (in any case), the name of the data file, in which the values are stored in separate lines in format "name:value" (for *ScatterPlot*, the name is the *x* coordinate), and the name of output file.
If output file does not exist, it will be created.

##### Example

     $vis pieChart data.txt output.png
