package com.costa.luiz.sandbox.model;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;

@Slf4j
public class VideoPlayer implements Flow.Subscriber<VideoFrame> {

    Flow.Subscription subscription = null;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(VideoFrame item) {
        log.info("play #{}" , item.getNumber());
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("There is an error in video streaming:{}" , throwable.getMessage());

    }

    @Override
    public void onComplete() {
        log.error("Video has ended");
    }
}
