package com.honmiv.mai.schedule.utils;

import com.honmiv.mai.schedule.model.Day;
import com.honmiv.mai.schedule.model.Week;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

@Slf4j
public class PageParser {

    private static final String SCHEDULE_URL = "https://mai.ru/education/schedule/";

    private static final String LESSONS_POSTFIX = "/detail.php?group=";
    private static final HashMap<String, String> PAGE_CONTENT_MAP = new HashMap<>();
    private static int lastPageContentMapDayUpdate = new GregorianCalendar().get(GregorianCalendar.DAY_OF_MONTH);

    public static String _UTIL_getGroupList() {
        String pageContent = getPageContent("https://mai.ru/education/schedule/");
        Document parsedPage = Jsoup.parse(pageContent);
        Elements groups = parsedPage.getElementsByClass("sc-group-item");
        String[] groupArr = groups.text().split(" ");
        StringBuilder res = new StringBuilder();
        for (String group : groupArr) {
            res.append(group).append("\n");
        }
        return res.toString();
    }

    static String getPageContent(String urlString) {
        String requestDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String key = urlString + requestDate;
        String pageContent;
        if (!PAGE_CONTENT_MAP.containsKey(key)) {
            StringBuilder read = new StringBuilder();
            try {
                URLConnection urlConnection = null;
                URL url = new URL(urlString);
                while (urlConnection == null) {
                    urlConnection = url.openConnection();
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    read.append(inputLine);
                }
            } catch (IOException e) {
                log.error("exception was thrown during parsing schedule from url {} e={}", urlString, e.getMessage());
                throw new RuntimeException(e);
            }
            pageContent = read.toString();
            PAGE_CONTENT_MAP.put(key, pageContent);
        } else {
            int curDayOfMonth = (new GregorianCalendar()).get(GregorianCalendar.DAY_OF_MONTH);
            if (lastPageContentMapDayUpdate != curDayOfMonth) {
                PAGE_CONTENT_MAP.clear();
                lastPageContentMapDayUpdate = curDayOfMonth;
            }
            pageContent = PAGE_CONTENT_MAP.get(key);
        }
        return pageContent;
    }

    static Week parseWeek(String group, String postfix) {

        String pageContent = getPageContent(SCHEDULE_URL + postfix + group);
        Document parsedPage = Jsoup.parse(pageContent);

        Week week = new Week();

        Element scheduleContent = parsedPage.body().getElementById("schedule-content");
        Elements days = scheduleContent.getElementsByClass("sc-day-header");
        for (int i = 0; i < days.size(); i++) {
            String dayheader = days.get(i).text();
            week.addDay(new Day(dayheader));
            Elements daySchedule = scheduleContent.getElementsByClass("sc-table sc-table-detail");
            Elements lessons = daySchedule.get(i).getElementsByClass("sc-table-row");
            for (Element lesson : lessons) {
                String time = lesson.getElementsByClass("sc-item-time").text();
                String type = lesson.getElementsByClass("sc-item-type").text();
                String location = lesson.getElementsByClass("sc-item-location").text();
                String subject = lesson.getElementsByClass("sc-title").text();
                String[] lecturers = lesson.getElementsByClass("sc-lecturer").text().split(", ");
                week.getDay(i).addLesson(time, type, location, subject, lecturers);
            }
        }
        return week;
    }

    public static boolean checkIfGroupIsCorrect(String group) {
        String pageContent = getPageContent(SCHEDULE_URL);
        Document parsedPage = Jsoup.parse(pageContent);
        Elements groups = parsedPage.getElementsByClass("sc-group-item");
        String[] groupArr = groups.text().split(" ");
        for (String a : groupArr) {
            if (a.equals(group))
                return true;
        }
        return false;
    }

    static int findWeek(Date date, String group) {

        Week week = new Week();

        Calendar dateToSearch = new GregorianCalendar();
        dateToSearch.setTime(date);
        String pageContent = getPageContent(SCHEDULE_URL + LESSONS_POSTFIX + group);
        Document parsedPage = Jsoup.parse(pageContent);
        Elements weeks = parsedPage.getElementsByTag("tr");

        boolean weekIsPresent = false;

        for (Element element : weeks) {
            week = new Week(element.text());
            if (dateToSearch.getTimeInMillis() >= week.getAlpha() && dateToSearch.getTimeInMillis() <= week.getOmega()) {
                weekIsPresent = true;
                break;
            }
        }

        return weekIsPresent ? week.getNum() : 0;
    }
}
