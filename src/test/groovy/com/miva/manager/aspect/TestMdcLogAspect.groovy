package com.miva.manager.aspect

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.Signature
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class TestMdcLogAspect extends Specification {

    MdcLogAspect mdcLogAspect
    def appenderMock
    def captures

    def setup() {
        mdcLogAspect = new MdcLogAspect()
        appenderMock = Mock(Appender)
        final Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
        root.detachAndStopAllAppenders()
        root.addAppender(appenderMock)
        captures = new ArrayList<LoggingEvent>()
    }

    def cleanup() {
        final Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
        root.detachAndStopAllAppenders()
    }

    def "test call hierarchy and logging"() {
        setup:
        String payload = "payload"
        def proceedingJoinPointMock = Mock(ProceedingJoinPoint)
        proceedingJoinPointMock.getTarget() >> mdcLogAspect
        def signature = Mock(Signature)
        signature.getName() >> "log"
        proceedingJoinPointMock.getSignature() >> signature
        proceedingJoinPointMock.getArgs() >> [payload]
        proceedingJoinPointMock.proceed() >> payload

        when: "aspect is triggered"
        Object result = mdcLogAspect.log(proceedingJoinPointMock)

        then: "check that log contains request and response with MDC values"
        result instanceof String
        payload == result
        2 * appenderMock.doAppend({ captures << it })
        captures.size() == 2
        String.format("[target: MdcLogAspect.log] params: [%s]", payload) == captures[0].getFormattedMessage()
        captures[1].getFormattedMessage().matches(String.format("\\[target: MdcLogAspect.log\\] response: %s in \\d+ms", payload)) == true
    }
}
