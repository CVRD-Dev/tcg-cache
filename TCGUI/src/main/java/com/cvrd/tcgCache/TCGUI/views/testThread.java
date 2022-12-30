package com.cvrd.tcgCache.TCGUI.views;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class testThread {
    @Async
    public ListenableFuture<String> longRunningTask() {
        try {
            // Simulate a long running task
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return AsyncResult.forValue("Some result");
    }
}
