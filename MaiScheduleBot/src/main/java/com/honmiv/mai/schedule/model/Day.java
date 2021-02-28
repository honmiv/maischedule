package com.honmiv.mai.schedule.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Day {

    private final Calendar dayDate = new GregorianCalendar();
    private final List<Lesson> lessons = new ArrayList<>();
    private String dayOfWeek;

    public Day(String header) {
        dayDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(header.substring(0, 2)));
        dayDate.set(Calendar.MONTH, Integer.parseInt(header.substring(3, 5)) - 1);
        dayDate.set(Calendar.HOUR, 0);
        dayDate.set(Calendar.AM_PM, Calendar.AM);
        dayDate.set(Calendar.MINUTE, 0);
        dayDate.set(Calendar.SECOND, 0);
        dayDate.set(Calendar.MILLISECOND, 0);

        try {
            dayOfWeek = header.substring(5, 7);
        } catch (Exception e) {
            switch (dayDate.get(Calendar.DAY_OF_WEEK)) {
                case 2:
                    dayOfWeek = "Пн";
                    break;
                case 3:
                    dayOfWeek = "Вт";
                    break;
                case 4:
                    dayOfWeek = "Ср";
                    break;
                case 5:
                    dayOfWeek = "Чт";
                    break;
                case 6:
                    dayOfWeek = "Пт";
                    break;
                case 7:
                    dayOfWeek = "Сб";
                    break;
                case 1:
                    dayOfWeek = "Вс";
                    break;
                default:
                    dayOfWeek = "";
                    break;
            }
        }
    }

    public Lesson getLesson(int i) {
        return lessons.get(i);
    }

    public int getLessonSize() {
        return lessons.size();
    }

    public void addLesson(String time, String type, String location, String subject, String[] lecturers) {
        lessons.add(new Lesson(time, type, location, subject, lecturers, dayDate));
    }

    public Calendar getDayDate() {
        return dayDate;
    }

    public String getNextLesson() {
        Calendar curTime = new GregorianCalendar();
        curTime.setTime(new Date());
        if (curTime.getTimeInMillis() > lessons.get(lessons.size() - 1).getOmega())
            return "Пары закончились";
        for (Lesson lesson : lessons) {
            if (curTime.getTimeInMillis() < lesson.getAlpha())
                return lesson.toString();
        }
        return "Ошибка";
    }

    public String getCurLesson() {
        Calendar curTime = new GregorianCalendar();
        curTime.setTime(new Date());
        if (curTime.getTimeInMillis() < lessons.get(0).getAlpha())
            return "Пары еще не начались";
        if (curTime.getTimeInMillis() > lessons.get(lessons.size() - 1).getOmega())
            return "Пары закончились";
        for (Lesson lesson : lessons) {
            if (curTime.getTimeInMillis() >= lesson.getAlpha() && curTime.getTimeInMillis() <= lesson.getOmega())
                return lesson.toString();
        }
        return "Сейчас перерыв";
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        DateFormat format = new SimpleDateFormat("dd.MM.YY - " + dayOfWeek);
        result.append("\uD83D\uDCC5 ").append(format.format(dayDate.getTime())).append("\n\n\n");
        for (Lesson lesson : lessons) {
            if (lesson.toString().contains("Военная подготовка")) {
                result.append("\uD83C\uDDF7\uD83C\uDDFA Военная кафедра\n\n\n");
                break;
            }
            result.append(lesson.toString());
        }
        return result.toString();
    }
}