package ru.nsu.vorona;

/**
 * Вид контроля по дисциплине.
 */
public enum ControlType {
    EXAM,                 // экзамен
    DIFF_PASS,            // дифференцированный зачет
    PASS,                 // зачет без оценки
    TASK,                 // задание, лабораторные и пр.
    TEST,                 // контрольная, коллоквиум
    PRACTICE_REPORT,      // отчет по практике
    THESIS_DEFENSE        // защита ВКР
}
