package com.costa.luiz.sandbox.model;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AppMain {

    public static void main(String[] args) throws InterruptedException {
        try (SubmissionPublisher<Subscriber> publisher =
                new SubmissionPublisher<>(ForkJoinPool.commonPool(), 8)) {
            Channel firstChannel = new Channel("16 bits", "Tech");
            Subscriber subscriber = new Subscriber("Luiz", "luizcosta@mail.com");
            publisher.subscribe(firstChannel);
            //publisher.offer(f)
            TimeUnit.SECONDS.sleep(1);
        }
        log.info("Finished");
    }
}
