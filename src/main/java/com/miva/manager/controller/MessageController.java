package com.miva.manager.controller;


import com.miva.manager.bean.ErrorMessageJson;
import com.miva.manager.bean.MessageJson;
import com.miva.manager.exception.MessageManagerException;
import com.miva.manager.repository.MessageRepository;
import com.miva.manager.repository.model.Message;
import com.miva.manager.service.MessageResourceAssembler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@ControllerAdvice
@RequestMapping(value = "/v1")
@Slf4j
public class MessageController {

    @Autowired
    private MessageRepository repository;
    @Autowired
    private MessageResourceAssembler assembler;

    @RequestMapping(method = RequestMethod.GET, value = "/messages")
    @Transactional(readOnly = true)
    public @ResponseBody
    Resources<Resource<MessageJson>> getMessages(@RequestParam(required = false) String author) {
        List<Resource<MessageJson>> messages;
        if (author != null) {
            messages = repository.findByAuthor(author).stream().map(assembler::toResource).collect(Collectors.toList());
        } else {
            messages = repository.findAll().stream().map(assembler::toResource).collect(Collectors.toList());
        }
        return new Resources<>(messages,
                linkTo(methodOn(MessageController.class).getMessages(author)).withSelfRel());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/messages/{id}")
    @Transactional(readOnly = true)
    public @ResponseBody
    Resource<MessageJson> getMessage(@PathVariable Long id) {
        Message message = repository.findById(id).orElseThrow(() -> getNotFoundException(id));
        return assembler.toResource(message);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/messages")
    @Transactional
    public @ResponseBody
    Resource<MessageJson> createMessage(@RequestBody MessageJson messageJson) {
        Message message = repository.save(new Message(messageJson));
        return assembler.toResource(message);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/messages/{id}")
    @Transactional
    public @ResponseBody
    Resource<MessageJson> updateMessage(@PathVariable Long id, @RequestBody MessageJson messageJson) {
        if (messageJson.getId() != null && !id.equals(messageJson.getId())) {
            throw new MessageManagerException(HttpServletResponse.SC_BAD_REQUEST, "Message Id mismatch");
        }
        Message message = repository.findById(id).orElseThrow(() -> getNotFoundException(id));
        message.setAuthor(messageJson.getAuthor());
        message.setText(messageJson.getText());
        return assembler.toResource(repository.save(message));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/messages/{id}")
    @Transactional
    public @ResponseStatus(value = HttpStatus.OK)
    void deleteMessage(@PathVariable Long id) {
        repository.deleteById(id);
    }

    private MessageManagerException getNotFoundException(Long id) {
        return new MessageManagerException(HttpServletResponse.SC_NOT_FOUND, "Not found message: " + id);
    }

    @ExceptionHandler(MessageManagerException.class)
    public @ResponseBody
    ErrorMessageJson handleException(MessageManagerException ex, WebRequest request, HttpServletResponse response) {
        response.setStatus(ex.getStatusCode());
        return new ErrorMessageJson(ex, request.getDescription(false));
    }
}
