package com.honmiv.mai.schedule.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.objects.Update;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//TODO jpa?
@Slf4j
public class UsersDB {

    //TODO параметризовать
    private static final String url = "jdbc:mysql://localhost:3306/users?useUnicode=yes&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false";
    private static final String user = "";
    private static final String password = "";

    private static Connection getConnection() {
        Connection connection;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            log.error("sql exception: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return connection;
    }

    public static List<String> getAllUsersChatId() {
        List<String> chatIds = new ArrayList<>();
        try (Connection conn = getConnection()) {
            PreparedStatement st = conn.prepareStatement("select chat_id from users.users");
            ResultSet res = st.executeQuery();
            while (res.next()) {
                chatIds.add(res.getString(1));
            }
        } catch (SQLException e) {
            log.error("sql exception: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return chatIds;
    }

    public static boolean isUserAdmin(Update update) {
        int recordCount = 0;
        try (Connection conn = getConnection()) {
            PreparedStatement st = conn.prepareStatement("select count(*) from users.users where users.chat_id = ? AND users.admin = ?");
            st.setLong(1, update.getMessage().getChatId());
            st.setBoolean(2, true);
            ResultSet res = st.executeQuery();
            while (res.next()) {
                recordCount = res.getInt(1);
            }
        } catch (SQLException e) {
            log.error("sql exception: {} for chatId {} with messageId {} and text {}", e.getMessage(), update.getMessage().getChatId(), update.getMessage().getMessageId(), update.getMessage().getText());
            throw new RuntimeException(e);
        }
        return recordCount != 0;
    }

    public static boolean isUserRegistered(Long chatId) {
        if (isUserExist(chatId)) {
            int recordCount = 0;
            try (Connection conn = getConnection()) {
                PreparedStatement st = conn.prepareStatement("select count(*) from users.users where not users.group is null and users.chat_id = ?");
                st.setLong(1, chatId);
                ResultSet res = st.executeQuery();
                while (res.next()) {
                    recordCount = res.getInt(1);
                }
            } catch (SQLException e) {
                log.error("sql exception: {} for chatId {}", e.getMessage(), chatId);
                throw new RuntimeException(e);
            }
            return recordCount != 0;
        } else {
            return false;
        }
    }

    public static boolean isUserRegistered(Update update) {
        return isUserRegistered(update.getMessage().getChatId());
    }

    private static boolean isUserExist(Long chatId) {
        Connection conn = null;
        int recordCount = 0;
        try {
            conn = getConnection();
            PreparedStatement st = conn.prepareStatement("select count(*) from users.users where users.chat_id = ?");
            st.setLong(1, chatId);
            ResultSet res = st.executeQuery();
            while (res.next()) {
                recordCount = res.getInt(1);
            }
        } catch (SQLException e) {
            log.error("sql exception: {} for chatId {}", e.getMessage(), chatId);
            throw new RuntimeException(e);
        }
        return recordCount != 0;
    }

    public static boolean isUserExist(Update update) {
        return isUserExist(update.getMessage().getChatId());
    }

    private static void addUser(Long chatId, String group) {
        try (Connection conn = getConnection()) {
            PreparedStatement st;
            if (!isUserExist(chatId)) {
                st = conn.prepareStatement("insert into users.users (users.chat_id, users.group) values (?, ?)");
                st.setLong(1, chatId);
                st.setString(2, group);
            } else {
                st = conn.prepareStatement("update users.users set users.group = ? where users.chat_id = ?");
                st.setString(1, group);
                st.setLong(2, chatId);
            }
            st.execute();
        } catch (SQLException e) {
            log.error("sql exception: {} for chatId {}", e.getMessage(), chatId);
            throw new RuntimeException(e);
        }
    }

    public static void addUser(Update update) {
        addUser(update.getMessage().getChatId(), update.getMessage().getText());
    }

    private static void deleteUser(Long chatId) {
        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement("update users.users set users.group = ? where users.chat_id = ?")) {
            st.setString(1, null);
            st.setLong(2, chatId);
            st.execute();
        } catch (SQLException e) {
            log.error("sql exception: {} for chatId {}", e.getMessage(), chatId);
            throw new RuntimeException(e);
        }
    }

    public static void deleteUser(Update update) {
        deleteUser(update.getMessage().getChatId());
    }

    public static String getUserGroup(Update update) {
        return getUserGroup(update.getMessage().getChatId());
    }

    private static String getUserGroup(long chatId) {
        String group = "";
        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement("select users.group from users.users where users.chat_id = ?")) {
            st.setLong(1, chatId);
            ResultSet res = st.executeQuery();
            while (res.next()) {
                group = res.getString(1);
            }
        } catch (SQLException e) {
            log.error("sql exception: {} for chatId {}", e.getMessage(), chatId);
            throw new RuntimeException(e);
        }
        return group;
    }

    public static void updateUserInfo(Update update) {

        PreparedStatement st;
        try (Connection conn = getConnection()) {
            try {
                st = conn.prepareStatement("update users.users set users.lastAccess=? where chat_id=?");
                st.setString(1, new Timestamp(new Date().getTime()).toString());
                st.setLong(2, update.getMessage().getChatId());
                st.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // отдельные экзекьюты для каждого поля, потому что, напирмер смайлик в char mysql'я не вписывается
            try {
                st = conn.prepareStatement("update users.users set users.firstName=? where chat_id=?");
                st.setString(1, update.getMessage().getFrom().getFirstName());
                st.setLong(2, update.getMessage().getChatId());
                st.execute();
            } catch (SQLException e) {
                log.warn("firstName db parsing error: {chatId = {}, messageId = {}, firstName ={} }", update.getMessage().getChatId(), update.getMessage().getMessageId(), update.getMessage().getChat().getFirstName());
            }
            try {
                st = conn.prepareStatement("update users.users set users.lastName=? where chat_id=?");
                st.setString(1, update.getMessage().getFrom().getLastName());
                st.setLong(2, update.getMessage().getChatId());
                st.execute();
            } catch (SQLException e) {
                log.warn("lastName db parsing error: {chatId = {}, messageId = {}, lastName ={} }", update.getMessage().getChatId(), update.getMessage().getMessageId(), update.getMessage().getChat().getLastName());
            }
            try {
                st = conn.prepareStatement("update users.users set users.userName=? where chat_id=?");
                st.setString(1, update.getMessage().getFrom().getUserName());
                st.setLong(2, update.getMessage().getChatId());
                st.execute();
            } catch (SQLException e) {
                log.warn("userName db parsing error: {chatId = {}, messageId = {}, userName ={} }", update.getMessage().getChatId(), update.getMessage().getMessageId(), update.getMessage().getChat().getUserName());
            }
        } catch (SQLException e) {
            log.error("sql exception: {} for chatId {} with messageId {} and text {}", e.getMessage(), update.getMessage().getChatId(), update.getMessage().getMessageId(), update.getMessage().getText());
            throw new RuntimeException(e);
        }
    }

    public static void incADVClick(Update update) {
        int clickCount = 0;
        try (Connection conn = getConnection()) {
            PreparedStatement st = conn.prepareStatement("select users.pechatClick from users.users where users.chat_id = ?");
            st.setLong(1, update.getMessage().getChatId());
            ResultSet res = st.executeQuery();
            while (res.next()) {
                clickCount = res.getInt(1);
            }
            st = conn.prepareStatement("update users.users set pechatClick = ? where chat_id = ?");
            st.setInt(1, clickCount + 1);
            st.setLong(2, update.getMessage().getChatId());
            st.execute();
        } catch (SQLException e) {
            log.error("sql exception: {} for chatId {} with messageId {} and text {}", e.getMessage(), update.getMessage().getChatId(), update.getMessage().getMessageId(), update.getMessage().getText());
            throw new RuntimeException(e);
        }
    }
}
