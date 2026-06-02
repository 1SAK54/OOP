include 'tasks.groovy'
include 'students.groovy'

settings {
    workDir '.oop-checker-work'
    timeoutSeconds 120
    compileTask 'compileJava'
    docsTask 'javadoc'
    styleTask 'checkstyleMain'
    testTask 'test'
    allowMissingStyleTask true

    grade 5, from: 85
    grade 4, from: 70
    grade 3, from: 55
}

check {
    tasks 'Task_2_1_1', 'Task_2_2_1', 'Task_2_3_1'
    groups '24215'
}

checkpoint('КТ-1') {
    date '2026-04-30'
}

checkpoint('Итог') {
    date '2026-06-01'
}

bonus github: 'Artem351', task: 'Task_2_1_1', points: 1, reason: 'Дополнительное задание'
bonus github: 'Rasymptote', task: 'Task_2_2_1', points: 1, reason: 'Дополнительное задание'
