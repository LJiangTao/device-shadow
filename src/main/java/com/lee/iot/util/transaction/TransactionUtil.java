package com.lee.iot.util.transaction;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TransactionUtil {

    public static void doAfterTransaction(Runnable runnable) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new DoTransactionCompletion(runnable));
        }
    }


    /**
     * 事务包装工具
     *
     * @param manager           事务管理服务
     * @param definition        事务隔离范围 {@link TransactionDefinition}
     * @param runnable          执行事务内容
     * @param throwableConsumer 异常处理
     */
    public static void doTransaction(PlatformTransactionManager manager,
                                     int definition,
                                     Runnable runnable,
                                     Consumer<Throwable> throwableConsumer) {
        DefaultTransactionDefinition dtd = new DefaultTransactionDefinition(definition);
        TransactionStatus transaction = manager.getTransaction(dtd);

        try {
            runnable.run();
            manager.commit(transaction);
        } catch (Exception e) {
            throwableConsumer.accept(e);
            manager.rollback(transaction);
            throw e;
        } finally {
            if (!transaction.isCompleted()) {
                manager.rollback(transaction);
            }
        }
    }


    public static <T> T doTransaction(PlatformTransactionManager manager,
                                      int definition,
                                      Supplier<T> runnable,
                                      Consumer<Throwable> throwableConsumer) {
        DefaultTransactionDefinition dtd = new DefaultTransactionDefinition(definition);
        TransactionStatus transaction = manager.getTransaction(dtd);

        try {
            T res = runnable.get();
            manager.commit(transaction);
            return res;
        } catch (Exception e) {
            throwableConsumer.accept(e);
            manager.rollback(transaction);
            throw e;
        } finally {
            if (!transaction.isCompleted()) {
                manager.rollback(transaction);
            }
        }
    }


}
