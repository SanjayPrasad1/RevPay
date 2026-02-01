package com.revpay.dao;

import com.revpay.model.Notification;

import java.util.List;

public interface NotificationDao {
    void save(Notification notification);
    List<Notification> findUnreadByUserId(Long userId);
    void markAsRead(Long notificationId);
}
