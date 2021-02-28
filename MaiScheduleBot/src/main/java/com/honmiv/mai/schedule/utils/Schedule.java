package com.honmiv.mai.schedule.utils;

import com.honmiv.mai.schedule.model.Day;
import com.honmiv.mai.schedule.model.Lesson;
import com.honmiv.mai.schedule.model.Week;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Schedule {

    private static final String LESSONS_URL = "/detail.php?group=";

    private static final String EXAMS_URL = "/session.php?group=";

    public static String getScheduleByDate(Date targetDate, String group) {
        Week week = getWeekByDate(targetDate, group);
        if (week == null) return "Неучебная неделя";
        for (int i = 0; i < week.getDaySize(); i++) {
            if (targetDate.getTime() == week.getDay(i).getDayDate().getTimeInMillis()) {
                return week.getDay(i).toString();
            }
        }
        DateFormat f = new SimpleDateFormat("dd.MM.yy");
        return f.format(targetDate) + " - выходной";
    }

    private static Week getWeekByDate(Date targetDate, String group) {
        Calendar targetCalendar = new GregorianCalendar();
        targetCalendar.setTime(targetDate);
        targetCalendar.set(Calendar.HOUR, 0);
        targetCalendar.set(Calendar.MINUTE, 0);
        targetCalendar.set(Calendar.SECOND, 0);
        targetCalendar.set(Calendar.MILLISECOND, 0);
        targetDate.setTime(targetCalendar.getTimeInMillis());
        int weekNum = PageParser.findWeek(targetDate, group);
        if (weekNum == 0)
            return null;
        String postfix = "detail.php?week=" + weekNum + "&group=";
        return PageParser.parseWeek(group, postfix);
    }

    private static String printWeek(Week week) {
        if (week != null && !week.toString().equals("")) return week.toString();
        else return "Неучебная неделя";
    }

    public static String getNearestWeekSchedule(String group) {
        Date targetDate = new Date();
        Week nearestWeek = getWeekByDate(targetDate, group);
        while (printWeek(nearestWeek).equals("Неучебная неделя")) {
            targetDate.setTime(targetDate.getTime() + 7 * 24 * 60 * 60 * 1000);
            nearestWeek = getWeekByDate(targetDate, group);
        }
        if (printWeek(nearestWeek).equals("Неучебная неделя"))
            return "Расписание отсутствует";
        return printWeek(nearestWeek);
    }

    public static String getNextWeekSchedule(String group) {
        Date targetDate = new Date();
        targetDate.setTime(targetDate.getTime() + 7 * 24 * 60 * 60 * 1000);
        return printWeek(getWeekByDate(targetDate, group));
    }

    public static String getExamsSchedule(String group) {
        String exams = PageParser.parseWeek(group, EXAMS_URL).toString();
        if (exams == null || exams.equals(""))
            return "Расписание экзаменов отсутствует";
        else return exams;
    }

    private static boolean is0IsToday(Week week) {
        Calendar today = new GregorianCalendar();
        today.setTime(new Date());
        return (today.get(Calendar.YEAR) == week.getDay(0).getDayDate().get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == week.getDay(0).getDayDate().get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == week.getDay(0).getDayDate().get(Calendar.DAY_OF_MONTH));
    }

    public static String getTodaySchedule(String group) {
        Week week = PageParser.parseWeek(group, LESSONS_URL);
        if (week.getDaySize() == 0)
            return "Расписание отсутствует";
        if (is0IsToday(week))
            return week.getDay(0).toString();
        else
            return "Сегодня выходной";
    }

    public static String getNextStudyDaySchedule(String group) {
        Week week = PageParser.parseWeek(group, LESSONS_URL);
        if (week.getDaySize() == 0)
            return "Расписания на сайте нет";
        if (is0IsToday(week)) {
            String nextDay = week.getDay(1).toString();
            if (nextDay.contains("Военная")) {
                return nextDay + week.getDay(2).toString();
            } else
                return nextDay;
        } else
            return week.getDay(0).toString();
    }

    public static String getCurLesson(String group) {
        Week week = PageParser.parseWeek(group, LESSONS_URL);
        if (is0IsToday(week))
            return week.getDay(0).getCurLesson();
        else
            return "Сегодня выходной";
    }

    public static String getNextLesson(String group) {
        Week week = PageParser.parseWeek(group, LESSONS_URL);
        if (is0IsToday(week))
            return week.getDay(0).getNextLesson();
        else
            return "Сегодня выходной";
    }

    public static String getNextLaba(String group) {
        Date date = new Date();
        Week week;
        while ((week = getWeekByDate(date, group)) == null) {
            date.setTime(date.getTime() + 7 * 24 * 60 * 60 * 1000);
            week = getWeekByDate(date, group);
        }
        while ((week = getWeekByDate(date, group)) != null) {
            for (int i = 0; i < week.getDaySize(); i++) {
                Day day = week.getDay(i);
                Calendar dayDate = day.getDayDate();
                Calendar today = new GregorianCalendar();
                //Если день уже прошел, пропускаем его
                if (dayDate.get(Calendar.YEAR) <= today.get(Calendar.YEAR)
                        && dayDate.get(Calendar.MONTH) <= today.get(Calendar.MONTH)
                        && dayDate.get(Calendar.DAY_OF_MONTH) < today.get(Calendar.DAY_OF_MONTH))
                    continue;
                for (int j = 0; j < day.getLessonSize(); j++) {
                    Lesson lesson = day.getLesson(j);
                    if (dayDate.get(Calendar.YEAR) >= today.get(Calendar.YEAR)
                            && dayDate.get(Calendar.MONTH) >= today.get(Calendar.MONTH)) {
                        if (dayDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                            if (today.getTimeInMillis() > lesson.getOmega()) {
                                if (lesson.toString().contains("ЛР")) {
                                    return day.toString();
                                }
                            }
                        } else {
                            if (lesson.toString().contains("ЛР")) {
                                return day.toString();
                            }
                        }
                    }
                }
            }
            date.setTime(date.getTime() + 7 * 24 * 60 * 60 * 1000);
        }
        return "Лабы кончились";
    }

    public static String getNextPrepod(String group, String name) {
        Date date = new Date();
        Week week;
        while ((week = getWeekByDate(date, group)) == null) {
            date.setTime(date.getTime() + 7 * 24 * 60 * 60 * 1000);
            week = getWeekByDate(date, group);
        }
        while ((week = getWeekByDate(date, group)) != null) {
            for (int i = 0; i < week.getDaySize(); i++) {
                Day day = week.getDay(i);
                Calendar dayDate = day.getDayDate();
                Calendar today = new GregorianCalendar();
                //Если день уже прошел, пропускаем его
                if (dayDate.get(Calendar.YEAR) <= today.get(Calendar.YEAR)
                        && dayDate.get(Calendar.MONTH) <= today.get(Calendar.MONTH)
                        && dayDate.get(Calendar.DAY_OF_MONTH) < today.get(Calendar.DAY_OF_MONTH))
                    continue;
                for (int j = 0; j < day.getLessonSize(); j++) {
                    Lesson lesson = day.getLesson(j);
                    if (dayDate.get(Calendar.YEAR) >= today.get(Calendar.YEAR)
                            && dayDate.get(Calendar.MONTH) >= today.get(Calendar.MONTH)) {
                        Pattern p = Pattern.compile("(?s).*" + name + "(?s).*");
                        Matcher m = p.matcher(lesson.toString());
                        if (dayDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                            if (today.getTimeInMillis() > lesson.getOmega()) {
                                if (m.matches()) {
                                    return day.toString();
                                }
                            }
                        } else {
                            if (m.matches()) {
                                return day.toString();
                            }
                        }
                    }
                }
            }
            date.setTime(date.getTime() + 7 * 24 * 60 * 60 * 1000);
        }
        return "Пар с этим преподавателем не найдено, возможно, имя введено неверно";
    }
}