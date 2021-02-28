package com.honmiv.mai.schedule.bot;

import com.honmiv.mai.schedule.bot.menu.MenuBuilder;
import com.honmiv.mai.schedule.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.ForwardMessage;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import static com.honmiv.mai.schedule.utils.OptionLiterals.*;
import static com.honmiv.mai.schedule.utils.PageParser._UTIL_getGroupList;

//TODO расписание препода - пока невозможно, нет в открытом доступе (только клик по имени препода в расписании на день)
//TODO разобраться с ГрупЧатами - пока не ясно, почему боту нельзя отправить сообещния

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final Map<Long, String> previousUpdate = new HashMap<>();

    private static boolean MAI_WEBSITE_DOES_NOT_WORK = false;
    private static boolean TECHNICAL_WORKS = false;
    private static boolean SUMMER = false;


    public static void main(String[] args) {

        Locale.setDefault(new Locale("ru", "RU"));

        if (args.length != 0 && args[0].equals("-groups")) {
            log.info("trying to get groups...");
            System.out.println(_UTIL_getGroupList());
        } else {

            if (args.length != 0) {
                switch (args[0]) {
                    case "-mai":
                        MAI_WEBSITE_DOES_NOT_WORK = true;
                        break;
                    case "-works":
                        TECHNICAL_WORKS = true;
                        break;
                    case "-summer":
                        SUMMER = true;
                        break;
                }
            }

            /// Initialize Api Context
            ApiContextInitializer.init();

            // Instantiate Telegram Bots API
            TelegramBotsApi botsApi = new TelegramBotsApi();

            Bot bot = new Bot();

            // Register
            try {
                botsApi.registerBot(bot);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendToAll(String msgText) {
        List<String> chatIds = UsersDB.getAllUsersChatId();
        for (String chatId : chatIds) {
            SendMessage msg = new SendMessage().setText(msgText).setChatId(chatId);
            if (UsersDB.isUserRegistered(Long.valueOf(chatId)))
                MenuBuilder.setMainMenu(msg);
            else {
                MenuBuilder.setRegistrationMenu(msg);
            }
            log.info("sending message broadcast message {} to user with chatId={}", msg.getText(), msg.getChatId());
            try {
                sendMsg(msg);
                log.info("broadcast message {} to user with chatId={} sent successfully", msg.getText(), msg.getChatId());
            } catch (RuntimeException e) {
                log.warn("broadcast message {} to user with chatId={} wasn't sent due to exception {} with cause {}", msg.getText(), msg.getChatId(), e.getMessage(), e.getCause());
            }
        }
    }

    @Override
    public String getBotUsername() {
        //TODO параметризовать
        return "bot_username";
        //return "test_bot_username";
    }

    @Override
    public String getBotToken() {
        //TODO параметризовать
        return "BOT_TOKEN";
        //return "TEST_BOT_TOKEN";
    }

    @Override
    public void onUpdateReceived(Update update) {
        new Thread(() -> {
            if (update.hasMessage()) {
                if (update.getMessage().hasLocation())
                    sendLocationResponse(update);
                if (update.getMessage().hasText())
                    parseMsg(update);
            } else if (update.hasCallbackQuery()) {
                parseWhereIsMenu(update);
            }
        }).start();
    }

    private void parseWhereIsMenu(Update update) {
        // Set variables
        String callData = update.getCallbackQuery().getData();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (callData) {
            case "back": {
                EditMessageText new_message = new EditMessageText()
                        .setChatId(chatId)
                        .setMessageId((int) messageId)
                        .setText("Что вас интересует?")
                        .setReplyMarkup(MenuBuilder.setWhereIsMenu());
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            break;
            case "dorm": {
                EditMessageText new_message = new EditMessageText()
                        .setChatId(chatId)
                        .setMessageId((int) messageId)
                        .setText("Что вас интересует?")
                        .setReplyMarkup(MenuBuilder.setWhereIsDormitories());
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            break;
            case "dorm1": {
                sendMaiMapWithMarker(update, 545, 330);
            }
            break;
            case "dorm2": {
                sendMaiMapWithMarker(update, 525, 255);
            }
            break;
            case "dorm3": {
                sendMaiMapWithMarker(update, 330, 225);
            }
            break;
            case "dorm4": {
                sendMaiMapWithMarker(update, 415, 245);
            }
            break;
            case "dorm6": {
                sendMaiMapWithMarker(update, 460, 460);
            }
            break;
            case "building": {
                EditMessageText new_message = new EditMessageText()
                        .setChatId(chatId)
                        .setMessageId((int) messageId)
                        .setText("Что вас интересует?")
                        .setReplyMarkup(MenuBuilder.setWhereIsBuildings());
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            break;
            case "building1": {
                sendMaiMapWithMarker(update, 666, 216);
            }
            break;
            case "building2": {
                sendMaiMapWithMarker(update, 840, 549);
            }
            break;
            case "building2А": {
                sendMaiMapWithMarker(update, 827, 444);
            }
            break;
            case "building2Б": {
                sendMaiMapWithMarker(update, 785, 520);
            }
            break;

            case "building3": {
                sendMaiMapWithMarker(update, 660, 440);
            }
            break;
            case "building4": {
                sendMaiMapWithMarker(update, 725, 575);
            }
            break;
            case "building5": {
                sendMaiMapWithMarker(update, 855, 750);
            }
            break;
            case "building6": {
                sendMaiMapWithMarker(update, 274, 787);
            }
            break;

            case "building7": {
                sendMaiMapWithMarker(update, 900, 495);
            }
            break;
            case "building9": {
                sendMaiMapWithMarker(update, 632, 315);
            }
            break;
            case "building10": {
                sendMaiMapWithMarker(update, 672, 298);
            }
            break;
            case "building11": {
                sendMaiMapWithMarker(update, 648, 140);
            }
            break;

            case "building16": {
                sendMaiMapWithMarker(update, 715, 280);
            }
            break;
            case "building24А": {
                sendMaiMapWithMarker(update, 760, 240);
            }
            break;
            case "building24Б": {
                sendMaiMapWithMarker(update, 630, 108);
            }
            break;
            case "building24В": {
                sendMaiMapWithMarker(update, 610, 160);
            }
            break;

            case "buildingГАК": {
                sendMaiMapWithMarker(update, 800, 355);
            }
            break;
            case "buildingДК": {
                sendMaiMapWithMarker(update, 577, 439);
            }
            break;
            case "buildingЧЕРЕПАХА": {
                sendMaiMapWithMarker(update, 620, 600);
            }
            break;
            case "building12": {
                sendMaiMapWithMarker(update, 620, 600);
            }
            break;

            case "buildingГУК А": {
                sendMaiMapWithMarker(update, 553, 503);
            }
            break;
            case "buildingГУК Б": {
                sendMaiMapWithMarker(update, 650, 588);
            }
            break;
            case "buildingГУК В": {
                sendMaiMapWithMarker(update, 588, 562);
            }
            break;
            case "buildingГУК Г": {
                sendMaiMapWithMarker(update, 675, 555);
            }
            break;
        }
    }

    private void parseMsg(Update update) {

        /*if (update.getMessage().getText().equals("/reset")) {
            UsersDB.deleteUser(update);
            sendResetResult(update);
            return;
        }*/

        /*String msgText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        if (UsersDB.isUserAdmin(update)) {
            if (previousUpdate.containsKey(chatId)) {
                if (previousUpdate.get(chatId).startsWith("/all"))
                    sendToAll(msgText);
                if (previousUpdate.get(chatId).startsWith("/to_"))
                    adminReply(previousUpdate.get(chatId), update);
            }
        } else {
            sendToAdmin(update);
        }
        previousUpdate.put(chatId, msgText);*/

        if (MAI_WEBSITE_DOES_NOT_WORK || TECHNICAL_WORKS || SUMMER) {
            log.info("send bot doesn't work due to params MAI_WEBSITE_DOES_NOT_WORK={} and TECHNICAL_WORKS={} and SUMMER={}",
                    MAI_WEBSITE_DOES_NOT_WORK,
                    TECHNICAL_WORKS,
                    SUMMER);
            sendBotDoesntWork(update);
            return;
        }

        String userName = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();
        String updateMsgText = update.getMessage().getText();
        Integer messageId = update.getMessage().getMessageId();

        log.info("[chatId={}, messageId={}, userName={}, text={}] start parsing msg",
                chatId,
                messageId,
                userName,
                updateMsgText
        );

        String msgText = update.getMessage().getText();

        if (UsersDB.isUserRegistered(update)) {
            log.info("[chatId={}, messageId={}, userName={}, text={}] user is registered", chatId, messageId, userName, updateMsgText);
            UsersDB.updateUserInfo(update);
            log.info("[chatId={}, messageId={}, userName={}, text={}] db info updated successfully", chatId, messageId, userName, updateMsgText);

            switch (OptionLiterals.getByText(msgText)) {
                case START: {
                    logSendingMainMenu(chatId, messageId, userName, updateMsgText, START);
                    SendMessage msg = new SendMessage().setChatId(chatId).setText("Настройки группы:\n" +
                            UsersDB.getUserGroup(update));
                    MenuBuilder.setMainMenu(msg);
                    logSendingSendMessage(update, msg);
                    sendMsg(msg);
                    logSentSendMessage(update, msg);
                    logSentMainMenu(chatId, messageId, userName, updateMsgText, START);
                }
                case TODAY: {
                    logSendingSchedule(chatId, messageId, userName, updateMsgText, TODAY);
                    sendTodaySchedule(update);
                    logSentSchedule(chatId, messageId, userName, updateMsgText, TODAY);
                }
                break;
                case NEXT_WORKING_DAY: {
                    logSendingSchedule(chatId, messageId, userName, updateMsgText, NEXT_WORKING_DAY);
                    sendNextStudyDaySchedule(update);
                    logSentSchedule(chatId, messageId, userName, updateMsgText, NEXT_WORKING_DAY);
                }
                break;
                case NEAREST_WORKING_WEEK: {
                    logSendingSchedule(chatId, messageId, userName, updateMsgText, NEAREST_WORKING_WEEK);
                    sendWeekSchedule(update);
                    logSentSchedule(chatId, messageId, userName, updateMsgText, NEAREST_WORKING_WEEK);
                }
                break;
                case NEXT_WEEK: {
                    logSendingSchedule(chatId, messageId, userName, updateMsgText, NEXT_WEEK);
                    sendNextWeekSchedule(update);
                    logSentSchedule(chatId, messageId, userName, updateMsgText, NEXT_WEEK);
                }
                break;
                case BY_DATE: {
                    sendDateRequest(update);
                }
                break;
                case EXAMS: {
                    logSendingSchedule(chatId, messageId, userName, updateMsgText, EXAMS);
                    sendExamsSchedule(update);
                    logSentSchedule(chatId, messageId, userName, updateMsgText, EXAMS);
                }
                break;
                case NEXT_LESSON: {
                    logSendingSchedule(chatId, messageId, userName, updateMsgText, NEXT_LESSON);
                    sendNextLesson(update);
                    logSentSchedule(chatId, messageId, userName, updateMsgText, NEXT_LESSON);
                }
                break;
                case CURR_LESSON: {
                    logSendingSchedule(chatId, messageId, userName, updateMsgText, CURR_LESSON);
                    sendCurLesson(update);
                    logSentSchedule(chatId, messageId, userName, updateMsgText, CURR_LESSON);
                }
                break;
                case BACK: {
                    logSendingMainMenu(chatId, messageId, userName, updateMsgText, BACK);
                    sendMainMenu(update);
                    logSentMainMenu(chatId, messageId, userName, updateMsgText, BACK);
                }
                break;
                case USEFUL: {
                    sendUsefulMenu(update);
                }
                break;
                case ADV: {
                    UsersDB.incADVClick(update);
                    sendADV(update);
                }
                break;
                case MAP: {
                    sendMaiMap(update);
                }
                break;
                case RESET_GROUP: {
                    sendResetMenu(update);
                }
                break;
                case NEXT_LAB: {
                    logSendingSchedule(chatId, messageId, userName, updateMsgText, NEXT_LAB);
                    sendNextLaba(update);
                    logSentSchedule(chatId, messageId, userName, updateMsgText, NEXT_LAB);
                }
                break;
                case BY_PROFESSOR: {
                    sendNextPrepodRequest(update);
                }
                break;
                case WHERE_AM_I: {
                    sendLocationRequest(update);
                }
                break;
                case WHERE_IS: {
                    sendWhereIsMenu(update);
                }
                break;
                default: {
                    if (UsersDB.isUserAdmin(update)) {
                        if (previousUpdate.containsKey(chatId)) {
                            if (previousUpdate.get(chatId).startsWith("/all")) {
                                sendToAll(msgText);
                            }
                            if (previousUpdate.get(chatId).startsWith("/to_")) {
                                adminReply(previousUpdate.get(chatId), update);
                            }
                            saveState(chatId, msgText);
                        }
                    }

                    if (previousUpdate.get(chatId) != null && previousUpdate.get(chatId).equals(BY_PROFESSOR.getText())) {
                        sendNextPrepodResponse(update);
                    } else if (previousUpdate.get(chatId) != null && previousUpdate.get(chatId).equals(RESET_GROUP.getText())) {
                        switch (getByText(msgText)) {
                            case YES: {
                                UsersDB.deleteUser(update);
                                sendResetResult(update);
                            }
                            break;
                            case NO: {
                                sendMainMenu(update);
                            }
                            break;
                        }
                    } else {
                        Pattern datePattern = Pattern.compile("\\d\\d[.|\\-|/|\\\\]\\d\\d[.|\\-|/|\\\\](?:\\d{4}|\\d{2})");

                        if (datePattern.matcher(msgText).matches()) {
                            sendScheduleByDate(update);
                            saveState(chatId, msgText);
                            return;
                        }
                        sendUnknownRequest(update);
                    }
                    if (!(previousUpdate.containsKey(chatId) &&
                            previousUpdate.get(chatId).equals(BY_PROFESSOR.getText())))
                        sendToAdmin(update);
                }
                break;
            }
        } else {
            if (msgText.equals(GROUP_LIST.getText())) {
                sendGroupList(update);
            } else if (PageParser.checkIfGroupIsCorrect(msgText)) {
                UsersDB.addUser(update);
                if (UsersDB.isUserExist(update)) {
                    UsersDB.updateUserInfo(update);
                    sendRegSuccesResult(update);
                } else {
                    sendRegFailureResult(update);
                }
            } else {
                sendToAdmin(update);
                if (!msgText.equals("/start"))
                    sendGroupUnrecognized(update);
                sendRegistrationRequest(update);
            }
        }
        saveState(chatId, msgText);
    }

    private void saveState(Long chatId, String msgText) {
        previousUpdate.put(chatId, msgText);
    }

    private void sendBotDoesntWork(Update update) {
        SendMessage msg = new SendMessage()
                .setChatId(update.getMessage().getChatId());
        if (SUMMER)
            msg.setText("Работа бота будет восстановлена после появления расписания на сайте МАИ.\nStay Tuned \uD83D\uDCFB");
        if (MAI_WEBSITE_DOES_NOT_WORK)
            msg.setText("Страница с расписанием на сайте МАИ работает некорректно, ждем пока починят. \nStay Tuned \uD83D\uDCFB");
        if (TECHNICAL_WORKS)
            msg.setText("Ведутся технические работы. \nStay Tuned \uD83D\uDCFB");

        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendUnknownRequest(Update update) {
        SendMessage msg = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Команда не распознана, воспользуйтесь меню");
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendWhereIsMenu(Update update) {
        SendMessage msg = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Что вас интересует?")
                .setReplyMarkup(MenuBuilder.setWhereIsMenu());
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendMainMenu(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId()).setText(CHOOSE_OPTION.getText());
        MenuBuilder.setMainMenu(msg);
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendUsefulMenu(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId()).setText(CHOOSE_OPTION.getText());
        MenuBuilder.setUsefulMenu(msg);
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendResetMenu(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId()).setText(ARE_YOU_SURE.getText());
        MenuBuilder.setResetMenu(msg);
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendADV(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId());
        msg.setText(ADV_MESSAGE_TEXT.getText());
        MenuBuilder.setMainMenu(msg);
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendResetResult(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId());
        msg.setText("Настройки группы успешно сброшены");
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
        sendRegistrationRequest(update);
    }

    private void sendToAdmin(Update update) {
        //TODO: захардкоженная фигня, можно искать admin=1 в бд
        String adminChatId = "245814836";
        String msgText = new StringBuilder()
                .append("\n/to_")
                .append(update.getMessage().getChatId())
                .append("_")
                .append(update.getMessage().getMessageId()).toString();
        SendMessage msg = new SendMessage().setChatId(adminChatId).setText(msgText);
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
        ForwardMessage fmsg = new ForwardMessage().setMessageId(update.getMessage().getMessageId()).setChatId(adminChatId).setFromChatId(update.getMessage().getChatId());
        try {
            execute(fmsg);
        } catch (TelegramApiException e) {
            log.error("Exception was thrown while forwarding message {} from user with chatId {} to admin {}", msg.getText(), fmsg.getFromChatId(), fmsg.getChatId());
            throw new RuntimeException(e);
        }
    }

    private void adminReply(String to, Update update) {
        if (update.getMessage().isReply()) {
            String[] splitted = to.split("_");
            String msgText = update.getMessage().getText();
            if (!to.equals(msgText)) {
                String chatId = splitted[1];
                int replyMessageId = Integer.parseInt(splitted[2]);
                SendMessage msg = new SendMessage().
                        setReplyToMessageId(replyMessageId).setChatId(chatId).setText(msgText);
                logSendingSendMessage(update, msg);
                sendMsg(msg);
                logSentSendMessage(update, msg);
            }
        }
    }

    private void sendDoc(SendDocument doc) {
        try {
            sendDocument(doc);
        } catch (TelegramApiException e) {
            log.error("Exception was thrown while sending doc to user with chatId {}", doc.getChatId());
            throw new RuntimeException(e);
        }
    }

    private void sendMsg(SendMessage msg) {
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error("Exception was thrown while sending message {} to user with chatId {}", msg.getText(), msg.getChatId());
            throw new RuntimeException(e);
        }
    }

    private void sendGroupList(Update update) {
        File groupsFile = new File("groups.txt");
        SendDocument groups = new SendDocument()
                .setChatId(update.getMessage().getChatId())
                .setNewDocument(groupsFile);
        sendDoc(groups);
    }

    private void sendRegSuccesResult(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText("Регистрация прошла успешно\n\nВыбрана группа: " + update.getMessage().getText());
        MenuBuilder.setMainMenu(msg);
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendRegFailureResult(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText("Регистрация не выполнена");
        MenuBuilder.setRegistrationMenu(msg);
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendRegistrationRequest(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText("Введите номер группы, скопировав из списка (пока что обновляется, не все группы присутсвуют на сайте):\n\n");
        MenuBuilder.setRegistrationMenu(msg);
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
        File groupsFile = new File("groups.txt");
        SendDocument groups = new SendDocument()
                .setChatId(update.getMessage().getChatId())
                .setNewDocument(groupsFile);
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendNextLesson(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText(Schedule.getNextLesson(UsersDB.getUserGroup(update)));
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendCurLesson(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText(Schedule.getCurLesson(UsersDB.getUserGroup(update)));
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendWeekSchedule(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText(Schedule.getNearestWeekSchedule(UsersDB.getUserGroup(update)));
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendNextWeekSchedule(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText(Schedule.getNextWeekSchedule(UsersDB.getUserGroup(update)));
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendTodaySchedule(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText(Schedule.getTodaySchedule(UsersDB.getUserGroup(update)));
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendNextStudyDaySchedule(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText(Schedule.getNextStudyDaySchedule(UsersDB.getUserGroup(update)));
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendExamsSchedule(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText(Schedule.getExamsSchedule(UsersDB.getUserGroup(update)));
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendDateRequest(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText("Введите дату в формате dd.mm.yy");
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendScheduleByDate(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId());
        String msgText = update.getMessage().getText();
        try {
            // SimpleDateFormat хуёво парсит, без ошибок, т.е. 32.12.2012 -> 01.01.2013
            //Pattern.compile("\\d\\d[.|\\-|/|\\\\]\\d\\d[.|\\-|/|\\\\](?:\\d{4}|\\d{2})");
            String[] numbers = msgText.split("[.|\\-|/|\\\\]");
            String year = numbers[2].length() == 4 ? numbers[2].substring(2, 4) : numbers[2].substring(0, 2);
            msgText = numbers[0] + "." + numbers[1] + "." + year;
            LocalDate date = LocalDate.parse(msgText, DateTimeFormatter.ofPattern("dd.MM.yy"));
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd");

            msg.setText(Schedule.getScheduleByDate(f.parse(date.toString()), UsersDB.getUserGroup(update)));
        } catch (Exception e) {
            msg.setText("Дата введена неверно");
        }
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendNextLaba(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText(Schedule.getNextLaba(UsersDB.getUserGroup(update)));
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendNextPrepodRequest(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText("Введите фамилию, имя или\nотчество преподавателя");
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendNextPrepodResponse(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText(Schedule.getNextPrepod(UsersDB.getUserGroup(update), update.getMessage().getText()));
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendGroupUnrecognized(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText("Код группы не распознан");
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendMaiMapWithMarker(Update update, double x, double y) {
        File file = new File(Objects.requireNonNull(MapDrawer.drawAndGetPath(x, y, update.getCallbackQuery().getFrom().getId().toString())));
        SendDocument doc = new SendDocument()
                .setChatId((long) update.getCallbackQuery().getFrom().getId())
                .setNewDocument(file);
        sendDoc(doc);
        file.delete();
    }

    private void sendMaiMap(Update update) {
        SendDocument doc = new SendDocument()
                .setChatId(update.getMessage().getChatId())
                .setNewDocument(new File(MapDrawer.getMaiMapPath()));
        sendDoc(doc);
    }

    private void sendLocationRequest(Update update) {
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText("Пришлите мне свою геопозицию \uD83D\uDCCE\uD83D\uDCCD и я отмечу вас на карте МАИ");
        MenuBuilder.setMainMenu(msg);
        logSendingSendMessage(update, msg);
        sendMsg(msg);
        logSentSendMessage(update, msg);
    }

    private void sendLocationResponse(Update update) {
        String filePath = MapDrawer.drawLocation(update);
        if (filePath == null) {
            SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                    .setText("Похоже, вы находитесь не на территории МАИ");
            sendMsg(msg);
            return;
        }
        File file = new File(filePath);
        SendDocument doc = new SendDocument()
                .setChatId(update.getMessage().getChatId())
                .setNewDocument(file);
        sendDoc(doc);
        file.delete();
        SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId())
                .setText("\uD83D\uDCCD - Вы находитесь здесь");
        MenuBuilder.setMainMenu(msg);
        sendMsg(msg);
    }

    private void logSendingSendMessage(Update update, SendMessage msg) {
        log.info("[chatId={}, messageId={}, userName={}, text={}] sending message with text {}",
                update.getMessage().getChatId(),
                update.getMessage().getMessageId(),
                update.getMessage().getChat().getUserName(),
                update.getMessage().getText(),
                msg.getText());
    }

    private void logSentSendMessage(Update update, SendMessage msg) {
        log.info("[chatId={}, messageId={}, userName={}, text={}] message with text {} sent successfully",
                update.getMessage().getChatId(),
                update.getMessage().getMessageId(),
                update.getMessage().getChat().getUserName(),
                update.getMessage().getText(),
                msg.getText());
    }

    private void logSendingSchedule(Long chatId, Integer messageId, String userName, String updateMsgText, OptionLiterals literal) {
        log.info("[chatId={}, messageId={}, userName={}, text={}] sending schedule for {} message", chatId, messageId, userName, updateMsgText, literal);
    }

    private void logSentSchedule(Long chatId, Integer messageId, String userName, String updateMsgText, OptionLiterals literal) {
        log.info("[chatId={}, messageId={}, userName={}, text={}] schedule sent successfully for {} message", chatId, messageId, userName, updateMsgText, literal);
    }

    private void logSendingMainMenu(Long chatId, Integer messageId, String userName, String updateMsgText, OptionLiterals literal) {
        log.info("[chatId={}, messageId={}, userName={}, text={}] sending main menu for {} message", chatId, messageId, userName, updateMsgText, literal);
    }

    private void logSentMainMenu(Long chatId, Integer messageId, String userName, String updateMsgText, OptionLiterals literal) {
        log.info("[chatId={}, messageId={}, userName={}, text={}] main menu for {} message sent successfully", chatId, messageId, userName, updateMsgText, literal);
    }
}