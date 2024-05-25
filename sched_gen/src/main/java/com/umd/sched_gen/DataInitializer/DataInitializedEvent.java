package com.umd.sched_gen.DataInitializer;

import org.springframework.context.ApplicationEvent;

/** Used to indicate the database table courses has been populated */
public class DataInitializedEvent extends ApplicationEvent {
    public DataInitializedEvent(Object source) {
        super(source);
    }
}
