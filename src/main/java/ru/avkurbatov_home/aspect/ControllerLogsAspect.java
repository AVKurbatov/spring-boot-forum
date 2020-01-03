package ru.avkurbatov_home.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * Detailed logs for controllers. Following points are logged: requests to controllers methods with all input parameters,
 * answers of methods, time of processing, errors in processing.
 * */
@Slf4j
@Aspect
@Component
public class ControllerLogsAspect {

    @Around("@within(ru.avkurbatov_home.aspect.ControllerLogs) ||" +
            " @annotation(ru.avkurbatov_home.aspect.ControllerLogs)")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String requestName = getRequestName(joinPoint);
        String parametersNamesWithValues = getParametersNamesWithValues(joinPoint);
        String requestInfo = className + " " + requestName + ", " + parametersNamesWithValues;

        log.info("call to {}", requestInfo);
        long start = System.currentTimeMillis();
        try {
            Object proceed = joinPoint.proceed();
            log.info("{} run in {} ms", requestInfo, System.currentTimeMillis() - start);
            return proceed;
        } catch (Exception ex) {
            if( ResponseEntity.class.equals(((MethodSignature)joinPoint.getSignature()).getMethod().getReturnType()) ){
                log.error(requestInfo + " error, message: ", ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            throw ex;
        }
    }

    private String getRequestName(ProceedingJoinPoint joinPoint){
        for (Annotation annotation : ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotations()) {
            if (annotation instanceof RequestMapping) {
                RequestMapping requestMapping = (RequestMapping)annotation;
                if (requestMapping.value().length > 0) {
                    return Arrays.toString(requestMapping.value());
                } else if (requestMapping.path().length > 0) {
                    return Arrays.toString(requestMapping.path());
                }
            }
        }
        return joinPoint.getSignature().getName();
    }

    private String getParametersNamesWithValues(ProceedingJoinPoint joinPoint){
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] values = joinPoint.getArgs();

        if (values == null || values.length == 0) {
            return "(no parameters)";
        }

        StringBuilder result = new StringBuilder("(");
        for (int i = 0; i < values.length; ++i) {
            if (i != 0) {
                result.append(", ");
            }
            result.append(parameters[i].getName());
            result.append(" = ");
            result.append(values[i]);
        }
        result.append(")");

        return result.toString();
    }

}
