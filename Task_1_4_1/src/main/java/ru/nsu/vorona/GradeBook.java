package ru.nsu.vorona;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Электронная зачетная книжка студента.
 */
public final class GradeBook {

    private final String studentName;
    private boolean paidEducation;
    private final List<CourseRecord> records = new ArrayList<>();

    public GradeBook(String studentName, boolean paidEducation) {
        this.studentName = Objects.requireNonNull(studentName);
        this.paidEducation = paidEducation;
    }

    public String getStudentName() {
        return studentName;
    }

    public boolean isPaidEducation() {
        return paidEducation;
    }

    public void setPaidEducation(boolean paidEducation) {
        this.paidEducation = paidEducation;
    }

    public void addRecord(CourseRecord record) {
        records.add(Objects.requireNonNull(record));
    }

    public List<CourseRecord> getRecords() {
        return List.copyOf(records);
    }

    /**
     * 1. Текущий средний балл за все время обучения.
     * В расчёт входят только записи с выставленной оценкой
     * по экзаменам и диффзачётам.
     */
    public double getCurrentAverageGrade() {
        int sum = 0;
        int count = 0;

        for (CourseRecord r : records) {
            if (!r.hasGrade()) {
                continue;
            }
            if (r.getControlType() == ControlType.EXAM ||
                    r.getControlType() == ControlType.DIFF_PASS) {

                sum += r.getGrade().getNumeric();
                count++;
            }
        }

        return count == 0 ? 0.0 : (double) sum / count;
    }

    /**
     * 2. Можно ли перевестись с платного на бюджет.
     * Условие: в последних двух сессиях НЕТ "3" по ЭКЗАМЕНАМ.
     * "3" по диффзачётам допустимы.
     */
    public boolean canTransferToBudget() {
        if (!paidEducation) {
            return false; // уже бюджет
        }
        if (records.isEmpty()) {
            return false;
        }

        int maxSemester = records.stream()
                .mapToInt(CourseRecord::getSemester)
                .max()
                .orElse(1);

        int lastSemester = maxSemester;
        int prevSemester = Math.max(1, maxSemester - 1);

        for (CourseRecord r : records) {
            if (!r.hasGrade()) {
                continue;
            }
            int sem = r.getSemester();
            if (sem == lastSemester || sem == prevSemester) {
                if (r.getControlType() == ControlType.EXAM &&
                        r.getGrade().isSatisfactory()) { // "3"
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 3. Можно ли получить "красный" диплом.
     * Условия:
     *  - >= 75% оценок по экзаменам и диффзачётам — "5"
     *  - нет "3" и "2" по экзаменам и диффзачётам
     *  - ВКР (если уже есть запись THESIS_DEFENSE) должна быть на "5"
     * Учитывается текущая ситуация: если часть предметов не закрыта,
     * проверяем, не нарушены ли критерии И есть ли шанс добрать 75% пятёрок.
     */
    public boolean canGetRedDiploma() {
        int examsWithGrade = 0;
        int excellentCount = 0;

        for (CourseRecord r : records) {
            if (!r.hasGrade()) {
                continue;
            }

            ControlType t = r.getControlType();
            Grade g = r.getGrade();

            // Проверка ВКР
            if (t == ControlType.THESIS_DEFENSE) {
                if (!g.isExcellent()) {
                    return false;
                }
            }

            // Экзамены и дифзачёты
            if (t == ControlType.EXAM || t == ControlType.DIFF_PASS) {
                // Нельзя иметь "3" или "2"
                if (g.isBad() || g.isSatisfactory()) {
                    return false;
                }

                examsWithGrade++;
                if (g.isExcellent()) {
                    excellentCount++;
                }
            }
        }

        // Если пока нет ни одного экзамена/дифзачёта с оценкой —
        if (examsWithGrade == 0) {
            return true;
        }

        // уже набранный процент пятёрок
        double currentRatio = (double) excellentCount / examsWithGrade;

        if (currentRatio >= 0.75) {
            return true;
        }

        // Смотрим незакрытые экзамены/дифзачёты:
        int remaining = 0;
        for (CourseRecord r : records) {
            if (!r.hasGrade() &&
                    (r.getControlType() == ControlType.EXAM ||
                            r.getControlType() == ControlType.DIFF_PASS)) {
                remaining++;
            }
        }

        // Максимально возможное количество пятёрок,
        // если по всем оставшимся будут "5"
        int maxExcellent = excellentCount + remaining;
        int total = examsWithGrade + remaining;

        return total > 0 && (double) maxExcellent / total >= 0.75;
    }

    /**
     * 4. Возможность получения повышенной стипендии в данном семестре.
     * Примерное правило (уточняется под требования):
     *  - в семестре нет "3" и "2" по экзаменам и дифзачётам
     *  - не более двух "4"
     *  - есть хотя бы одна "5" по экзамену или дифзачёту
     */
    public boolean canGetIncreasedScholarship(int semester) {
        boolean hasExcellent = false;
        int foursCount = 0;

        for (CourseRecord r : records) {
            if (r.getSemester() != semester || !r.hasGrade()) {
                continue;
            }
            ControlType t = r.getControlType();
            Grade g = r.getGrade();

            if (t == ControlType.EXAM || t == ControlType.DIFF_PASS) {
                // 2 или 3 — сразу нет
                if (g.isBad() || g.isSatisfactory()) {
                    return false;
                }
                if (g == Grade.FOUR) {
                    foursCount++;
                    // больше двух 4 — нельзя
                    if (foursCount > 2) {
                        return false;
                    }
                }
                if (g.isExcellent()) {
                    hasExcellent = true;
                }
            }
        }

        // Нужна хотя бы одна 5 и не более двух 4
        return hasExcellent;
    }
}
