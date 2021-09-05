package com.costa.luiz.sandbox.model;

import java.util.concurrent.Executors;
import java.util.concurrent.SubmissionPublisher;

public class VideoStreamServer extends SubmissionPublisher<VideoFrame> {

    public VideoStreamServer() {
        super(Executors.newSingleThreadExecutor(), 5);
    }
}
