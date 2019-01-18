package com.miva.manager.service;

import com.miva.manager.bean.MessageJson;
import com.miva.manager.controller.MessageController;
import com.miva.manager.repository.model.Message;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class MessageResourceAssembler implements ResourceAssembler<Message, Resource<MessageJson>> {

    @Override
    public Resource<MessageJson> toResource(Message message) {

        return new Resource<>(new MessageJson(message),
                linkTo(methodOn(MessageController.class).getMessage(message.getId())).withSelfRel(),
                linkTo(methodOn(MessageController.class).getMessages(null)).withRel("messages"));
    }
}
