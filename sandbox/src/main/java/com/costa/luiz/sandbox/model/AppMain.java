package com.costa.luiz.sandbox.model;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Thread.sleep;

@Slf4j
public class AppMain {

    public static void main(String[] args) throws InterruptedException {
        try (SubmissionPublisher<Subscriber> publisher =
                     new SubmissionPublisher<>(ForkJoinPool.commonPool(), 8)) {
            Channel firstChannel = new Channel("16 bits", "Tech");
            var subscriber = new Subscriber("Luiz", "luizcosta@mail.com");
            publisher.subscribe(firstChannel);
            //publisher.offer(f)
            TimeUnit.SECONDS.sleep(1);
        }
        log.info("Finished");
    }

    void anotherTest() throws InterruptedException {
        try (VideoStreamServer streamServer = new VideoStreamServer()) {
            streamServer.subscribe(new VideoPlayer());
            // submit video frames
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            AtomicLong frameNumber = new AtomicLong();
            executor.scheduleWithFixedDelay(() ->
                    streamServer.offer(new VideoFrame(frameNumber.getAndIncrement()), (subscriber, videoFrame) -> {
                        subscriber.onError(new RuntimeException("Frame#" + videoFrame.getNumber()
                                + " dropped because of backpressure"));
                        return true;
                    }), 0, 1, TimeUnit.MILLISECONDS);
            sleep(1000);
        }
    }
}
