package com.revpay.db;

import com.revpay.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class UserDao {

    public void save(User user) throws Exception {

        String sql = """
            INSERT INTO users
            (full_name, email, phone, password_hash, pin_hash, user_type, failed_attempts, locked_until, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getPinHash());
            ps.setString(6, user.getUserType());
            ps.setInt(7, user.getFailedAttempts());
            ps.setTimestamp(8, user.getLockedUntil() == null ? null : Timestamp.valueOf(user.getLockedUntil()));
            ps.setTimestamp(9, Timestamp.valueOf(user.getCreatedAt()));

            ps.executeUpdate();
        }
    }
    public User findByEmailOrPhone(String input) throws Exception {

        String sql = """
        SELECT id, full_name, email, phone, password_hash, pin_hash,
               user_type, failed_attempts, locked_until, created_at
        FROM users
        WHERE email = ? OR phone = ?
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, input);
            ps.setString(2, input);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            User user = new User();
            user.setId(rs.getLong("id"));
            user.setFullName(rs.getString("full_name"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setPinHash(rs.getString("pin_hash"));
            user.setUserType(rs.getString("user_type"));
            user.setFailedAttempts(rs.getInt("failed_attempts"));
            user.setLockedUntil(rs.getTimestamp("locked_until") == null
                    ? null
                    : rs.getTimestamp("locked_until").toLocalDateTime());
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            return user;
        }
    }

    public void updateFailedAttempts(long userId, int attempts, LocalDateTime lockedUntil) throws Exception {

        String sql = """
        UPDATE users
        SET failed_attempts = ?, locked_until = ?
        WHERE id = ?
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, attempts);
            ps.setTimestamp(2, lockedUntil == null ? null : Timestamp.valueOf(lockedUntil));
            ps.setLong(3, userId);

            ps.executeUpdate();
        }
    }

    public User findById(long id) throws Exception {

        String sql = """
        SELECT id, full_name, email, phone, password_hash, pin_hash,
               user_type, failed_attempts, locked_until, created_at
        FROM users
        WHERE id = ?
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            User user = new User();
            user.setId(rs.getLong("id"));
            user.setFullName(rs.getString("full_name"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setPinHash(rs.getString("pin_hash"));
            user.setUserType(rs.getString("user_type"));
            user.setFailedAttempts(rs.getInt("failed_attempts"));
            user.setLockedUntil(rs.getTimestamp("locked_until") == null
                    ? null
                    : rs.getTimestamp("locked_until").toLocalDateTime());
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            return user;
        }
    }



}
