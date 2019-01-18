package com.miva.manager.bean;

import com.miva.manager.repository.model.Message;
import lombok.Data;

@Data
public class MessageJson {

    private Long id;
    private String author;
    private String text;

    public MessageJson(Long id, String author, String text) {
        this.id = id;
        this.author = author;
        this.text = text;
    }

    protected MessageJson() {
    }

    public MessageJson(Message message) {
        this.id = message.getId();
        this.author = message.getAuthor();
        this.text = message.getText();
    }

}
