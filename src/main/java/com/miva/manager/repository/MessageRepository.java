package com.miva.manager.repository;

import com.miva.manager.repository.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByAuthor(String author);
}
