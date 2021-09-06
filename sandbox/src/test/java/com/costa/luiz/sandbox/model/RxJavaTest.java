package com.costa.luiz.sandbox.model;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RxJavaTest implements WithAssertions {

    @Nested
    class Basic {
        @Test
        void createAnObservable() {
            String oneItem = Observable
                    .just("One item")
                    .map(value -> value + " has " + value.length() + " characters")
                    .blockingFirst();

            assertThat(oneItem).isEqualTo("One item has 8 characters");
        }

        @Test
        void fromIterable() {
            List<String> cities = List.of("Barcelona", "Madrid", "Porto", "Paris", "London");
            Observable<String> observable = Observable.fromIterable(cities)
                    .map(String::toUpperCase);
//                    .filter(city -> city.startsWith("P"));

            TestObserver<String> testObserver = observable.test();
            testObserver.hasSubscription();
            testObserver.assertComplete();
            testObserver.assertValueCount(5);
            testObserver.assertResult("BARCELONA", "MADRID", "PORTO", "PARIS", "LONDON");
        }

        @Test
        void timeBased() {
            List<String> letters = Arrays.asList("A", "B", "C", "D", "E");
            TestScheduler scheduler = new TestScheduler();
            TestSubscriber<String> subscriber = new TestSubscriber<>();
            Observable<Long> tick = Observable.interval(1, TimeUnit.SECONDS);

            Observable<String> observable = Observable.fromIterable(letters)
                    .zipWith(tick, (string, index) -> index + "-" + string);


            TestObserver<String> test = observable.subscribeOn(scheduler)
                    .test();
            test.assertNoValues();
            test.assertNotComplete();

        }
    }

}
