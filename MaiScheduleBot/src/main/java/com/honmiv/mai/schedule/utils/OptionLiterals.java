package com.honmiv.mai.schedule.utils;

import java.util.Arrays;

public enum OptionLiterals {
    USEFUL("Полезное"),
    RESET_GROUP("Сброс настроек"),
    GROUP_LIST("Список групп"),
    YES("Да"),
    NO("Нет"),
    TODAY("Сегодня"),
    NEXT_WORKING_DAY("Следующий\nучебный день"),
    NEAREST_WORKING_WEEK("Ближайшая\nучебная неделя"),
    NEXT_WEEK("Следующая\nнеделя"),
    BACK("Назад"),
    BY_DATE("По дате"),
    EXAMS("Сессия"),
    MAP("Карта МАИ"),
    WHERE_AM_I("Где я?"),
    ADV("Место для рекламы"),
    ADV_MESSAGE_TEXT("Подробности @honmiv"),
    NEXT_LESSON("Следующая пара"),
    CURR_LESSON("Текущая пара"),
    CHOOSE_OPTION("Выберите опцию \u2611"),
    ARE_YOU_SURE("Вы уверены \u2049 "),
    NEXT_LAB("Следующая лаба"),
    BY_PROFESSOR("По преподавателю"),
    WHERE_IS("Где находится?"),
    START("/start"),
    UNKNOWN("default");

    private final String text;

    OptionLiterals(String text) {
        this.text = text;
    }

    public static OptionLiterals getByText(String text) {
        return Arrays.stream(values())
                .filter(value -> value.getText().equals(text))
                .findAny()
                .orElse(UNKNOWN);
    }

    public String getText() {
        return text;
    }
}