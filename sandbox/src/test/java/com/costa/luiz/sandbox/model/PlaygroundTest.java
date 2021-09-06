package com.costa.luiz.sandbox.model;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class PlaygroundTest {

    @Test
    void x() throws InterruptedException {
        Flowable.just("Hello world").subscribe(System.out::println);

        Flowable<Integer> flow = Flowable.range(1, 5)
                .map(v -> v * v)
                .filter(v -> v % 3 == 0);
        flow.subscribe(System.out::println);

        Observable.create(emitter -> {
            while (!emitter.isDisposed()) {
                long time = System.currentTimeMillis();
                emitter.onNext(time);
                if (time % 2 != 0) {
                    emitter.onError(new IllegalStateException("Odd millisecond!"));
                    break;
                }
            }
        })
                .subscribe(System.out::println, Throwable::printStackTrace);

        Flowable<String> source = Flowable.fromCallable(() -> {
            Thread.sleep(1000); //  imitate expensive computation
            return "Done";
        });

        Flowable<String> runBackground = source.subscribeOn(Schedulers.io());

        Flowable<String> showForeground = runBackground.observeOn(Schedulers.single());

        showForeground.subscribe(System.out::println, Throwable::printStackTrace);

        Thread.sleep(2000);

    }

    @Test
    void parallelProcessing() {

        Flowable.range(1, 10)
                .flatMap(v ->
                        Flowable.just(v)
                                .subscribeOn(Schedulers.computation())
                                .map(w -> w * w)
                )
                .blockingSubscribe(System.out::println);
    }

    @Test
    void sequential() {
        Flowable.range(1, 10)
                .parallel()
                .runOn(Schedulers.computation())
                .map(v -> v * v)
                .sequential()
                .blockingSubscribe(System.out::println);
    }

    @Test
    void nSubscribers() {
        Flowable<String> regions = Flowable
                .fromArray("Sul", "Sudeste", "Centro-Oeste", "Norte", "Nordeste")
                .map(String::toLowerCase);


        Subscriber<String> subscriber = new FlowableSubscriber<String>() {
            @Override
            public void onSubscribe(@NonNull Subscription s) {
                s.request(2);
            }

            @Override
            public void onNext(String s) {
                System.out.println("from subscriber [" + s + "]");
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        };

        Subscriber<String> otherSubscriber = new FlowableSubscriber<String>() {
            @Override
            public void onSubscribe(@NonNull Subscription s) {
                s.request(10);
            }

            @Override
            public void onNext(String s) {
                System.out.println("from otherSubscriber [" + s + "]");
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        };

        regions.subscribe(subscriber);
        regions.subscribe(otherSubscriber);


    }

    private FlowableSubscriber<? super String> subscriber() {
        return null;
    }

}
