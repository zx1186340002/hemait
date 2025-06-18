package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthOption;
import org.aspectj.apache.bcel.classfile.Signature;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import java.time.LocalDateTime;

/**
 * 自定义切面类
 */
@Aspect
@Component
@Slf4j //日志
public class AutoFillAspect {
    /**
     *切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    //第一个表示的是切点表达式，第二个表示拦截指定注解。
    //下面的方法表示的就是切点表达式
    public void autoFillPointCut() {}
    //前置通知。
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段填充");
        //获取类型。
        MethodSignature  signature  = (MethodSignature)joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); //获取标签上对应的注解
        OperationType operationType = autoFill.value(); //获取数据库操作类型
        //获取被拦截方法的参数--实体对象。
        Object[] args = joinPoint.getArgs(); //表示获取当前的所有参数，因此我们做一个约定即就定义第一个为Employee类型
        if (args == null || args.length == 0) {
            return ;
        }

        Object entity = args[0];
        //准备赋值的数据。
        LocalDateTime now = LocalDateTime.now();
        Long currenId = BaseContext.getCurrentId();
        //根据当前操作类型进行赋值。
        if(operationType == OperationType.INSERT){
            //4个公共字段
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                //通过反射赋值
                        setCreateTime.invoke(entity,now);
                        setCreateUser.invoke(entity,currenId);
                        setUpdateTime.invoke(entity,now);
                        setUpdateUser.invoke(entity,currenId);
            } catch (Exception e) {
               e.printStackTrace();
            }
        }else if(operationType == OperationType.UPDATE){
            //2个
                   try {
                      Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                      Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                    setUpdateTime.invoke(entity,now);
                    setUpdateUser.invoke(entity,currenId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  }
