# Курс основ программирования на МКН СПбГУ
## Проект 3: визуализация данных

[Постановка задачи](./TASK.md)

### Документация

Данная консольная утилита создаёт диаграммы и графики на основе данных, передоставленных пользователем ввиде файла, показывает в отдельном окне и сохраняет их в формате png.

#### Поддерживаемые типы диаграм

* BarChart - столбчатая диаграмма
* PieChart - круговая диаграмма
* ScatterPlot - точечное распределение

#### Формат входных данных

На вход программе нужно через пробел передать тип диаграммы(в любом регистре), имя файла с данными, в котором в отдельных строках хранятся значения в формате "имя:значение"(для *ScatterPlot* имя - это координата *x*), и имя файла, в который надо сохранить диаграмму.
Если файла, в который надо сохранить, не существует, то он создастся.

##### Пример

    $vis pieChart data.txt output.png