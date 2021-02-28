package com.honmiv.mai.schedule.model;

import lombok.Value;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Value
public class Lesson {

    Calendar alpha = new GregorianCalendar();

    Calendar omega = new GregorianCalendar();

    String type;

    String location;

    String subject;

    List<String> lecturers = new ArrayList<>();

    Lesson(String time, String type, String location, String subject, String[] lecturers, Calendar date) {
        DateFormat format = new SimpleDateFormat("HH:mm");
        try {
            alpha.setTime(format.parse(time.split(" – ")[0]));
            omega.setTime(format.parse(time.split(" – ")[1]));
            alpha.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
            omega.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.location = location;
        this.type = type;
        this.subject = subject;
        Collections.addAll(this.lecturers, lecturers);
    }

    public long getAlpha() {
        return alpha.getTimeInMillis();
    }

    public long getOmega() {
        return omega.getTimeInMillis();
    }

    @Override
    public String toString() {
        DateFormat format = new SimpleDateFormat("HH.mm");
        StringBuilder lesson = new StringBuilder()
                .append("\uD83D\uDD59 ")
                .append(format.format(alpha.getTime()))
                .append(" – ")
                .append(format.format(omega.getTime()))
                .append(" - ")
                .append(type)
                .append("\n\uD83C\uDFE2 ")
                .append(location)
                .append("\n\uD83D\uDCD6 ")
                .append(subject)
                .append("\n");
        if (!lecturers.get(0).equals("")) {
            for (String lecturer : lecturers)
                lesson.append("\uD83D\uDC64 ").append(lecturer).append("\n");
            lesson.append("\n");
        } else
            lesson.append("\n");
        lesson.append("\n");

        return lesson.toString();
    }
}
