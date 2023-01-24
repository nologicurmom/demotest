package com.example.demo.interfaces;
import com.example.demo.models.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, Integer> {

    List<Notification> findByidEnchere(int idEnchere);

}