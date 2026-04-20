package com.tournament.service;

import java.util.List;

import com.tournament.model.Notification;

public interface INotificationService {

    List<Notification> getUserNotifications(Integer userId);

    List<Notification> getUnreadNotifications(Integer userId);

    long getUnreadCount(Integer userId);

    void markAsRead(Integer notificationId);

    void markAllAsRead(Integer userId);

    void retryFailedNotification(Integer notificationId, Integer userId);

    void sendNotification(Notification notification);
}
