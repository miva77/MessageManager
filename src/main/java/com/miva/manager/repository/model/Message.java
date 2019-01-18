package com.miva.manager.repository.model;


import com.miva.manager.bean.MessageJson;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Message {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String author;
    @Column()
    private String text;

    protected Message() {

    }

    public Message(String author, String text) {
        this.author = author;
        this.text = text;
    }

    public Message(MessageJson messageJson) {
        this.author = messageJson.getAuthor();
        this.text = messageJson.getText();
    }
}
