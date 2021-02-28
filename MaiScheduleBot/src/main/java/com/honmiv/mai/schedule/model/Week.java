package com.honmiv.mai.schedule.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Week {
    private final Calendar alpha = new GregorianCalendar();
    private final Calendar omega = new GregorianCalendar();
    private final List<Day> days = new ArrayList<>();
    private int num;

    public Week() {
        alpha.setTime(new Date());
        omega.setTime(alpha.getTime());
    }

    public Week(String weekInfo) {
        String[] info = weekInfo.split(" +-+ +| +");
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        num = Integer.valueOf(info[0]);
        try {
            alpha.setTimeInMillis(format.parse(info[1]).getTime());
            omega.setTimeInMillis(format.parse(info[2]).getTime() + 24 * 60 * 60 * 1000 - 1);
            // + 23 часа 59 минут 59 секунд 999 милисекунд, т.к. последний день недели парсится в начало дня
            // и неделя не охватывает последний день
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public long getAlpha() {
        return omega.getTimeInMillis() - 8 * 24 * 60 * 60 * 1000 + 1;
    }

    public long getOmega() {
        return omega.getTimeInMillis();
    }

    public int getNum() {
        return num;
    }

    public int getDaySize() {
        return days.size();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Day day : days)
            result.append(day);
        return result.toString();
    }

    public void addDay(Day day) {
        day.getDayDate().set(Calendar.YEAR, alpha.get(Calendar.YEAR));
        days.add(day);
    }

    public Day getDay(int i) {
        return days.get(i);
    }
}
