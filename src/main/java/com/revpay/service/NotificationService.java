package com.revpay.service;

import com.revpay.dao.NotificationDao;
import com.revpay.model.Notification;

import java.util.List;

public class NotificationService {

    private final NotificationDao notificationDao;

    public NotificationService(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    public void notify(Long userId, String message) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setMessage(message);
        notificationDao.save(n);
    }

    public List<Notification> getUnread(Long userId) {
        return notificationDao.findUnreadByUserId(userId);
    }

    public void markRead(Long notificationId) {
        notificationDao.markAsRead(notificationId);
    }
}
