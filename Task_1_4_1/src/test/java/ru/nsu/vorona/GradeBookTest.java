package ru.nsu.vorona;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Тесты для GradeBook.
 */
class GradeBookTest {

    @Test
    void testAverageGradeEmpty() {
        GradeBook book = new GradeBook("Student", true);
        assertEquals(0.0, book.getCurrentAverageGrade(), 1e-9);
    }

    @Test
    void testAverageGradeOnlyExamsAndDiffPass() {
        GradeBook book = new GradeBook("Student", true);

        CourseRecord exam1 = new CourseRecord("Math", 1, ControlType.EXAM);
        exam1.setGrade(Grade.FIVE);
        book.addRecord(exam1);

        CourseRecord diff1 = new CourseRecord("Physics", 1, ControlType.DIFF_PASS);
        diff1.setGrade(Grade.FOUR);
        book.addRecord(diff1);

        CourseRecord pass = new CourseRecord("PE", 1, ControlType.PASS);
        pass.setGrade(null); // не должно влиять
        book.addRecord(pass);

        double avg = book.getCurrentAverageGrade();
        // (5 + 4) / 2 = 4.5
        assertEquals(4.5, avg, 1e-9);
    }

    @Test
    void testCanTransferToBudgetAlreadyBudget() {
        GradeBook book = new GradeBook("Student", false);
        assertFalse(book.canTransferToBudget());
    }

    @Test
    void testCanTransferToBudgetNoRecords() {
        GradeBook book = new GradeBook("Student", true);
        assertFalse(book.canTransferToBudget());
    }

    @Test
    void testCanTransferToBudgetWithoutThreesInLastTwoSemesters() {
        GradeBook book = new GradeBook("Student", true);

        // 1 семестр — есть тройка, но он старый, не входит в последние два.
        CourseRecord oldExam = new CourseRecord("OldExam", 1, ControlType.EXAM);
        oldExam.setGrade(Grade.THREE);
        book.addRecord(oldExam);

        // 2 семестр
        CourseRecord exam2 = new CourseRecord("Math2", 2, ControlType.EXAM);
        exam2.setGrade(Grade.FOUR);
        book.addRecord(exam2);

        CourseRecord diff2 = new CourseRecord("Phys2", 2, ControlType.DIFF_PASS);
        diff2.setGrade(Grade.THREE); // допустимо
        book.addRecord(diff2);

        // 3 семестр (последний)
        CourseRecord exam3 = new CourseRecord("Prog", 3, ControlType.EXAM);
        exam3.setGrade(Grade.FIVE);
        book.addRecord(exam3);

        assertTrue(book.canTransferToBudget());
    }

    @Test
    void testCannotTransferToBudgetWithThreeInLastSemesterExam() {
        GradeBook book = new GradeBook("Student", true);

        CourseRecord exam2 = new CourseRecord("Math2", 2, ControlType.EXAM);
        exam2.setGrade(Grade.FOUR);
        book.addRecord(exam2);

        CourseRecord exam3 = new CourseRecord("Prog", 3, ControlType.EXAM);
        exam3.setGrade(Grade.THREE); // тройка в последней сессии
        book.addRecord(exam3);

        assertFalse(book.canTransferToBudget());
    }

    @Test
    void testCannotTransferToBudgetWithThreeInPreviousSemesterExam() {
        GradeBook book = new GradeBook("Student", true);

        CourseRecord exam2 = new CourseRecord("Math2", 2, ControlType.EXAM);
        exam2.setGrade(Grade.THREE); // тройка в предпоследней сессии
        book.addRecord(exam2);

        CourseRecord exam3 = new CourseRecord("Prog", 3, ControlType.EXAM);
        exam3.setGrade(Grade.FIVE);
        book.addRecord(exam3);

        assertFalse(book.canTransferToBudget());
    }

    @Test
    void testRedDiplomaSimplePositive() {
        GradeBook book = new GradeBook("Student", true);

        // 4 экзамена, 1 дифзачет — все на 5
        addExamWithGrade(book, "Math", 1, Grade.FIVE);
        addExamWithGrade(book, "Phys", 2, Grade.FIVE);
        addExamWithGrade(book, "Prog", 3, Grade.FIVE);
        addDiffWithGrade(book, "History", 3, Grade.FIVE);
        addExamWithGrade(book, "Algo", 4, Grade.FIVE);

        // ВКР на 5
        CourseRecord thesis = new CourseRecord("Thesis", 8, ControlType.THESIS_DEFENSE);
        thesis.setGrade(Grade.FIVE);
        book.addRecord(thesis);

        assertTrue(book.canGetRedDiploma());
    }

    @Test
    void testRedDiplomaFailsWithThreeOnExam() {
        GradeBook book = new GradeBook("Student", true);

        addExamWithGrade(book, "Math", 1, Grade.FIVE);
        addExamWithGrade(book, "Phys", 2, Grade.THREE); // тройка ломает
        addDiffWithGrade(book, "History", 3, Grade.FIVE);

        CourseRecord thesis = new CourseRecord("Thesis", 8, ControlType.THESIS_DEFENSE);
        thesis.setGrade(Grade.FIVE);
        book.addRecord(thesis);

        assertFalse(book.canGetRedDiploma());
    }

    @Test
    void testRedDiplomaFailsWithBadThesis() {
        GradeBook book = new GradeBook("Student", true);

        addExamWithGrade(book, "Math", 1, Grade.FIVE);
        addExamWithGrade(book, "Phys", 2, Grade.FIVE);
        addDiffWithGrade(book, "History", 3, Grade.FIVE);

        CourseRecord thesis = new CourseRecord("Thesis", 8, ControlType.THESIS_DEFENSE);
        thesis.setGrade(Grade.FOUR); // ВКР не на 5
        book.addRecord(thesis);

        assertFalse(book.canGetRedDiploma());
    }

    @Test
    void testRedDiplomaPotentialWhileNotAllCoursesClosed() {
        GradeBook book = new GradeBook("Student", true);

        // Уже есть 2 экзамена на 5, 1 дифзачёт на 4.
        addExamWithGrade(book, "Math", 1, Grade.FIVE);
        addExamWithGrade(book, "Phys", 2, Grade.FIVE);
        addDiffWithGrade(book, "History", 3, Grade.FOUR);

        // Пара ещё не закрытых экзаменов
        CourseRecord futureExam1 = new CourseRecord("Prog", 4, ControlType.EXAM);
        book.addRecord(futureExam1);
        CourseRecord futureExam2 = new CourseRecord("Algo", 5, ControlType.EXAM);
        book.addRecord(futureExam2);

        // ВКР пока нет — считаем, что теоретически можно на 5
        assertTrue(book.canGetRedDiploma());
    }

    @Test
    void testCanGetIncreasedScholarshipPositive() {
        GradeBook book = new GradeBook("Student", true);

        addExamWithGrade(book, "Math", 3, Grade.FIVE);
        addExamWithGrade(book, "Phys", 3, Grade.FOUR);
        addDiffWithGrade(book, "History", 3, Grade.FIVE);

        assertTrue(book.canGetIncreasedScholarship(3));
    }

    @Test
    void testCannotGetScholarshipWithThree() {
        GradeBook book = new GradeBook("Student", true);

        addExamWithGrade(book, "Math", 3, Grade.FIVE);
        addExamWithGrade(book, "Phys", 3, Grade.THREE);

        assertFalse(book.canGetIncreasedScholarship(3));
    }

    @Test
    void testCannotGetScholarshipWithoutExcellent() {
        GradeBook book = new GradeBook("Student", true);

        addExamWithGrade(book, "Math", 3, Grade.FOUR);
        addDiffWithGrade(book, "History", 3, Grade.FOUR);

        assertFalse(book.canGetIncreasedScholarship(3));
    }

    // --- Вспомогательные методы для сокращения кода ---

    private static void addExamWithGrade(GradeBook book, String name, int sem, Grade grade) {
        CourseRecord r = new CourseRecord(name, sem, ControlType.EXAM);
        r.setGrade(grade);
        book.addRecord(r);
    }

    private static void addDiffWithGrade(GradeBook book, String name, int sem, Grade grade) {
        CourseRecord r = new CourseRecord(name, sem, ControlType.DIFF_PASS);
        r.setGrade(grade);
        book.addRecord(r);
    }
}
