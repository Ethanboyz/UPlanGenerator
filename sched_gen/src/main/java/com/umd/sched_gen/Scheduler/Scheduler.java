package com.umd.sched_gen.Scheduler;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import com.umd.sched_gen.DataInitializer.DataInitializedEvent;

@Component
public class Scheduler implements ApplicationListener<DataInitializedEvent> {

    /* Explicit dependencies */
    public Scheduler() {}

    @Override
    public void onApplicationEvent(@NonNull DataInitializedEvent event) {
        System.out.println("Success!");
    }
}