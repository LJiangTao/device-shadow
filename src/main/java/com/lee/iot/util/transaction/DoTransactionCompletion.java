package com.lee.iot.util.transaction;

import org.springframework.transaction.support.TransactionSynchronization;

public class DoTransactionCompletion implements TransactionSynchronization {

    private final Runnable afterCommitRunnable;

    public DoTransactionCompletion(Runnable afterCommitRunnable) {
        this.afterCommitRunnable = afterCommitRunnable;
    }

    @Override
    public void afterCompletion(int status) {
        if (STATUS_COMMITTED == status) {
            afterCommitRunnable.run();
        }
    }
}