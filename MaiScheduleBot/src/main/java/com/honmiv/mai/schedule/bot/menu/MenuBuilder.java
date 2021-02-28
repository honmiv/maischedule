package com.honmiv.mai.schedule.bot.menu;

import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.honmiv.mai.schedule.utils.OptionLiterals.*;

public class MenuBuilder {

    private static ReplyKeyboardMarkup getReplyKeyboard(List<KeyboardRow> keyboard) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private static void setMenu(SendMessage sendMessage, List<KeyboardRow> keyboard) {
        sendMessage.setReplyMarkup(getReplyKeyboard(keyboard));
    }

    private static void setMenu(SendDocument sendDocument, List<KeyboardRow> keyboard) {
        sendDocument.setReplyMarkup(getReplyKeyboard(keyboard));
    }

    static private List<KeyboardRow> getMainMenuKeyboard() {
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(TODAY.getText()));
        row1.add(new KeyboardButton(NEXT_WORKING_DAY.getText()));

        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton(NEXT_LAB.getText()));
        row4.add(new KeyboardButton(BY_PROFESSOR.getText()));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(NEAREST_WORKING_WEEK.getText()));
        row2.add(new KeyboardButton(NEXT_WEEK.getText()));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton(USEFUL.getText()));
        row3.add(new KeyboardButton(BY_DATE.getText()));
        row3.add(new KeyboardButton(EXAMS.getText()));

        keyboard.add(row1);
        keyboard.add(row4);
        keyboard.add(row2);
        keyboard.add(row3);

        return keyboard;
    }

    static void setMainMenu(SendDocument sendDocument) {
        setMenu(sendDocument, getMainMenuKeyboard());
    }

    public static void setMainMenu(SendMessage sendMessage) {
        setMenu(sendMessage, getMainMenuKeyboard());
    }

    public static void setRegistrationMenu(SendMessage sendMessage) {
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(GROUP_LIST.getText()));

        keyboard.add(row1);

        setMenu(sendMessage, keyboard);
    }

    public static void setResetMenu(SendMessage sendMessage) {
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(YES.getText()));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(NO.getText()));

        keyboard.add(row1);
        keyboard.add(row2);

        setMenu(sendMessage, keyboard);
    }

    public static void setUsefulMenu(SendMessage sendMessage) {

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(MAP.getText()));
        row1.add(new KeyboardButton(WHERE_IS.getText()));
        row1.add(new KeyboardButton(WHERE_AM_I.getText()));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(ADV.getText()));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton(BACK.getText()));
        row3.add(new KeyboardButton(RESET_GROUP.getText()));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        setMenu(sendMessage, keyboard);
    }

    public static InlineKeyboardMarkup setWhereIsMenu() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton().setText("Общежитие").setCallbackData("dorm"));
        rowInline2.add(new InlineKeyboardButton().setText("Корпус").setCallbackData("building"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public static InlineKeyboardMarkup setWhereIsBuildings() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline4 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline5 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline6 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline7 = new ArrayList<>();

        rowInline1.add(new InlineKeyboardButton().setText("1").setCallbackData("building1"));
        rowInline1.add(new InlineKeyboardButton().setText("2").setCallbackData("building2"));
        rowInline1.add(new InlineKeyboardButton().setText("2А").setCallbackData("building2А"));
        rowInline1.add(new InlineKeyboardButton().setText("2Б").setCallbackData("building2Б"));

        rowInline2.add(new InlineKeyboardButton().setText("3").setCallbackData("building3"));
        rowInline2.add(new InlineKeyboardButton().setText("4").setCallbackData("building4"));
        rowInline2.add(new InlineKeyboardButton().setText("5").setCallbackData("building5"));
        rowInline2.add(new InlineKeyboardButton().setText("6").setCallbackData("building6"));

        rowInline3.add(new InlineKeyboardButton().setText("7").setCallbackData("building7"));
        rowInline3.add(new InlineKeyboardButton().setText("9").setCallbackData("building9"));
        rowInline3.add(new InlineKeyboardButton().setText("10").setCallbackData("building10"));
        rowInline3.add(new InlineKeyboardButton().setText("11").setCallbackData("building11"));

        rowInline4.add(new InlineKeyboardButton().setText("16").setCallbackData("building16"));
        rowInline4.add(new InlineKeyboardButton().setText("24А").setCallbackData("building24А"));
        rowInline4.add(new InlineKeyboardButton().setText("24Б").setCallbackData("building24Б"));
        rowInline4.add(new InlineKeyboardButton().setText("24В").setCallbackData("building24В"));


        rowInline5.add(new InlineKeyboardButton().setText("ГАК").setCallbackData("buildingГАК"));
        rowInline5.add(new InlineKeyboardButton().setText("ДК").setCallbackData("buildingДК"));
        rowInline5.add(new InlineKeyboardButton().setText("ЧЕРЕПАХА").setCallbackData("buildingЧЕРЕПАХА"));
        rowInline5.add(new InlineKeyboardButton().setText("12").setCallbackData("building12"));

        rowInline6.add(new InlineKeyboardButton().setText("ГУК А").setCallbackData("buildingГУК А"));
        rowInline6.add(new InlineKeyboardButton().setText("ГУК Б").setCallbackData("buildingГУК Б"));
        rowInline6.add(new InlineKeyboardButton().setText("ГУК В").setCallbackData("buildingГУК В"));
        rowInline6.add(new InlineKeyboardButton().setText("ГУК Г").setCallbackData("buildingГУК Г"));


        rowInline7.add(new InlineKeyboardButton().setText("Назад").setCallbackData("back"));

        // Set the keyboard to the markup
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);
        rowsInline.add(rowInline4);
        rowsInline.add(rowInline5);
        rowsInline.add(rowInline6);
        rowsInline.add(rowInline7);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public static InlineKeyboardMarkup setWhereIsDormitories() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline4 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline5 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline6 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton().setText("№1 Космос").setCallbackData("dorm1"));
        rowInline2.add(new InlineKeyboardButton().setText("№2 Икар").setCallbackData("dorm2"));
        rowInline3.add(new InlineKeyboardButton().setText("№3 Башня").setCallbackData("dorm3"));
        rowInline4.add(new InlineKeyboardButton().setText("№4 Морг").setCallbackData("dorm4"));
        rowInline5.add(new InlineKeyboardButton().setText("№6 Альфа").setCallbackData("dorm6"));
        rowInline6.add(new InlineKeyboardButton().setText("Назад").setCallbackData("back"));
        // Set the keyboard to the markup
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);
        rowsInline.add(rowInline4);
        rowsInline.add(rowInline5);
        rowsInline.add(rowInline6);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }
}
