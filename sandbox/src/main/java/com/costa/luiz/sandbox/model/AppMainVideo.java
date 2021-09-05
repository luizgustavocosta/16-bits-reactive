package com.costa.luiz.sandbox.model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Thread.sleep;

public class AppMainVideo {

    public static void main(String[] args) throws InterruptedException {
        VideoStreamServer streamServer = new VideoStreamServer();
        streamServer.subscribe(new VideoPlayer());

// submit video frames
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        AtomicLong frameNumber = new AtomicLong();
        executor.scheduleWithFixedDelay(() -> {
            streamServer.offer(new VideoFrame(frameNumber.getAndIncrement()), (subscriber, videoFrame) -> {
                subscriber.onError(new RuntimeException("Frame#" + videoFrame.getNumber()
                        + " droped because of backpressure"));
                return true;
            });
        }, 0, 1, TimeUnit.MILLISECONDS);

        sleep(1000);
    }
}
