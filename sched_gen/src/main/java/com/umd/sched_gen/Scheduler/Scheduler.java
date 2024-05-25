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
        /* From here, we should see if we can deploy some sort of service that continually listens
         * for HTTP pings which request a viable course to be added to the schedule given the
         * courses already added. This will probably require the use of a second API service for
         * the scheduler, event listener, and an algorithm that can return which course(s) are
         * recommended.
         */
    }
}