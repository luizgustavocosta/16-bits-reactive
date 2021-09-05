package com.costa.luiz.sandbox.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Channel implements Flow.Subscriber<Subscriber>{

    private String name;
    private String theme;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        log.info("New subscriber to channel");
    }

    @Override
    public void onNext(Subscriber subscriber) {
        log.info("Hello {}. Check your inbox.: {}", subscriber.getName(), subscriber.getEmail());
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }

    @Override
    public void onComplete() {
        log.error("Transmission completed");
    }
}
