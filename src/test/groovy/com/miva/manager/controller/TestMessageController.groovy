package com.miva.manager.controller

import com.miva.manager.bean.MessageJson
import com.miva.manager.exception.MessageManagerException
import com.miva.manager.repository.MessageRepository
import com.miva.manager.repository.model.Message
import com.miva.manager.service.MessageResourceAssembler
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.hateoas.Resource
import org.springframework.hateoas.Resources
import spock.lang.Specification

@SpringBootTest
class TestMessageController extends Specification {

    MessageController controller

    def setup() {
        controller = new MessageController()
        controller.repository = Mock(MessageRepository)
        controller.assembler = new MessageResourceAssembler()
    }

    void "test get message by id"() {
        setup:
        Message message = new Message("jan", "bla")
        when:
        Resource<MessageJson> response = controller.getMessage(1)
        then:
        1 * controller.repository.findById(1) >> Optional.ofNullable(message)
        response != null
        response.content.getAuthor() == message.getAuthor()
        response.content.getText() == message.getText()
    }

    void "test get message by id doe not exist"() {
        setup:
        when:
        controller.getMessage(1)
        then:
        1 * controller.repository.findById(1) >> Optional.ofNullable(null)
        MessageManagerException exception = thrown()
        exception.statusCode == 404
    }

    void "test get all messages"() {
        setup:
        def messages = [new Message("jan", "bla"), new Message("karel", "bla")]
        when:
        Resources<Resource<MessageJson>> response = controller.getMessages(null)
        then:
        1 * controller.repository.findAll() >> messages
        response != null
        response.size() == 2
    }

    void "test get all messages by author"() {
        setup:
        def messages = [new Message("jan", "bla")]
        when:
        Resources<Resource<MessageJson>> response = controller.getMessages(messages[0].getAuthor())
        then:
        1 * controller.repository.findByAuthor(messages[0].getAuthor()) >> messages
        response != null
        response.size() == 1
        response[0].content.author == messages[0].author
        response[0].content.text == messages[0].text
    }

    void "test create message"() {
        setup:
        def message = new Message("jan", "bla")
        def messageJson = new MessageJson(message)
        when:
        Resource<MessageJson> response = controller.createMessage(messageJson)
        then:
        1 * controller.repository.save(message) >> message
        response != null
        response.content.getAuthor() == message.getAuthor()
        response.content.getText() == message.getText()
    }

    void "test update message"() {
        setup:
        def message = new Message("jan", "bla")
        message.id = 1
        def updatedMessage = new Message("jan", "blabla")
        updatedMessage.id = 1
        def messageJson = new MessageJson(updatedMessage)
        when:
        Resource<MessageJson> response = controller.updateMessage(1, messageJson)
        then:
        1 * controller.repository.findById(1) >> Optional.ofNullable(message)
        1 * controller.repository.save(_ as Message) >> message
        response != null
        response.content.getId() == message.getId()
        response.content.getAuthor() == message.getAuthor()
        response.content.getText() == message.getText()
    }

    void "test update message id mismatch"() {
        setup:

        def messageJson = new MessageJson(1, "jan", "blabla")
        when:
        controller.updateMessage(2, messageJson)
        then:
        MessageManagerException exception = thrown()
        exception.statusCode == 400
    }

    void "test delete message by id"() {
        setup:
        when:
        controller.deleteMessage(1)
        then:
        1 * controller.repository.deleteById(1)
    }
}
