package com.umd.sched_gen.Scheduler;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class Scheduler implements ApplicationListener<ApplicationReadyEvent> {

    /* Explicit dependencies */
    public Scheduler() {}

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Once data is loaded, you can start the scheduling part
        // This might be triggered by user action instead
    }
}