package com.shengshengman.dao;

import com.shengshengman.model.User;
import com.shengshengman.util.DB;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class UserDao {
    public User insert(String username, String plainPassword) throws SQLException {
        String password = encrypt(plainPassword);

        String sql = "insert into users (username, password) values (?, ?)";

        try (Connection c = DB.getConnection()) {
            try (PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                s.setString(1,username);
                s.setString(2,password);

                s.executeUpdate();

                try (ResultSet r = s.getGeneratedKeys()) {
                    if (!r.next()) {
                        return null;
                    }

                    return new User(r.getInt(1), username);
                }
            }
        }
    }

    public User select(String username, String plainPassword) throws SQLException {
        String password = encrypt(plainPassword);

        String sql = "select uid from users where username = ? and password = ?";

        try (Connection c = DB.getConnection()) {
            try (PreparedStatement s = c.prepareStatement(sql)) {
                s.setString(1, username);
                s.setString(2, password);

                try (ResultSet r = s.executeQuery()) {
                    if (!r.next()) {
                        return null;
                    }

                    return new User(r.getInt(1), username);
                }
            }
        }
    }

    private String encrypt(String plain) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = plain.getBytes();
            byte[] digest = messageDigest.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x",b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /*public static void main(String[] args) {
        String a = "123";
        UserDao userDao = new UserDao();
        String encrypt = userDao.encrypt(a);
        System.out.println(encrypt);
    }*/
}
