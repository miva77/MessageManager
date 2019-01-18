package com.miva.manager.aspect;

import com.miva.manager.exception.MessageManagerException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class MdcLogAspect {

    final public static String MDC_ID = "ID";

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void beanAnnotatedWithRestController() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void methodAnnotatedWithRequestMapping() {
    }

    @Pointcut("methodAnnotatedWithRequestMapping() && beanAnnotatedWithRestController()")
    public void publicMethodInsideAClassMarkedWithAtRestController() {
    }

    @Around("publicMethodInsideAClassMarkedWithAtRestController()")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.nanoTime();
        try {
            MDC.put(MDC_ID, UUID.randomUUID().toString());
            String target = pjp.getTarget().getClass().getSimpleName() + "." + pjp.getSignature().getName();
            log.info("[target: {}] params: {}", target, Arrays.asList(pjp.getArgs()));
            Object result = pjp.proceed();
            log.info("[target: {}] response: {} in {}ms", target, result, (System.nanoTime() - startTime) / 1000000);
            return result;
        } catch (MessageManagerException ex) {
            log.warn("Business Exception caught: {}", ex.getLogMessage());
            throw ex;
        } catch (Exception e) {
            MessageManagerException ex = new MessageManagerException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unknown error:", e);
            log.error("Exception caught: {}", e);
            throw ex;
        } finally {
            MDC.remove(MDC_ID);
        }
    }
}
