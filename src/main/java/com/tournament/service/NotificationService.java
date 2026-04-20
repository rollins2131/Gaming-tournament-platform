package com.tournament.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tournament.model.Notification;
import com.tournament.model.enums.NotificationStatus;
import com.tournament.repository.NotificationRepository;

@Service // Singleton pattern via Spring container
@Transactional
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> getUserNotifications(Integer userId) {
        return notificationRepository.findByUser_UserIdOrderBySentTimeDesc(userId);
    }

    @Override
    public List<Notification> getUnreadNotifications(Integer userId) {
        return notificationRepository.findByUser_UserIdAndStatusNotOrderBySentTimeDesc(
                userId, NotificationStatus.READ);
    }

    @Override
    public long getUnreadCount(Integer userId) {
        return notificationRepository.countByUser_UserIdAndStatusNot(userId, NotificationStatus.READ);
    }

    @Override
    public void markAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setStatus(NotificationStatus.READ);
        notification.setReadTime(java.time.LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Integer userId) {
        List<Notification> unread = notificationRepository
                .findByUser_UserIdAndStatusNotOrderBySentTimeDesc(userId, NotificationStatus.READ);
        unread.forEach(n -> {
            n.setStatus(NotificationStatus.READ);
            n.setReadTime(java.time.LocalDateTime.now());
        });
        notificationRepository.saveAll(unread);
    }

    @Override
    public void retryFailedNotification(Integer notificationId, Integer userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (notification.getUser() == null || !notification.getUser().getUserId().equals(userId)) {
            throw new SecurityException("You cannot retry this notification");
        }
        if (notification.getStatus() != NotificationStatus.FAILED) {
            throw new IllegalStateException("Only FAILED notifications can be retried");
        }
        sendNotification(notification);
    }

    @Override
    public void sendNotification(Notification notification) {
        // Protected Variations: failures in delivery do not propagate to core flows.
        try {
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentTime(java.time.LocalDateTime.now());
            notificationRepository.save(notification);

            // Simulate successful delivery in the current synchronous flow.
            notification.setStatus(NotificationStatus.DELIVERED);
            notification.setDeliveredTime(java.time.LocalDateTime.now());
            notificationRepository.save(notification);
        } catch (RuntimeException ex) {
            try {
                notification.setStatus(NotificationStatus.FAILED);
                notificationRepository.save(notification);
            } catch (RuntimeException ignored) {
                // Keep resilience behavior: notification errors never break core flow.
            }
            // Intentionally swallow to keep core system resilient
        }
    }
}
