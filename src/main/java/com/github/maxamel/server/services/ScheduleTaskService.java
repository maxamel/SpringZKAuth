package com.github.maxamel.server.services;

import java.util.List;
import java.util.Timer;

import com.github.maxamel.server.domain.model.User;

public interface ScheduleTaskService {

    public void publishChallenge(User user) throws InterruptedException;
    
    public void handleActivity(User user, List<Timer> timers) throws InterruptedException;
}
