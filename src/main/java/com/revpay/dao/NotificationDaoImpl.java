package com.revpay.dao;

import com.revpay.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDaoImpl implements NotificationDao {

    private final Connection con;

    public NotificationDaoImpl(Connection con) {
        this.con = con;
    }

    @Override
    public void save(Notification n) {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO notifications (user_id, message) VALUES (?, ?)")) {
            ps.setLong(1, n.getUserId());
            ps.setString(2, n.getMessage());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Notification> findUnreadByUserId(Long userId) {
        List<Notification> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM notifications WHERE user_id=? AND is_read=false")) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getLong("id"));
                n.setUserId(rs.getLong("user_id"));
                n.setMessage(rs.getString("message"));
                list.add(n);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public void markAsRead(Long notificationId) {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE notifications SET is_read=true WHERE id=?")) {
            ps.setLong(1, notificationId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
