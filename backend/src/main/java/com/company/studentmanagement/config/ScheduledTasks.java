package com.company.studentmanagement.config;

import com.company.studentmanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final TaskService taskService;

    // Runs once a day at 00:05 to flip PENDING/IN_PROGRESS tasks past their due date to OVERDUE.
    @Scheduled(cron = "0 5 0 * * *")
    public void markOverdueTasks() {
        taskService.markOverdueTasks();
    }
}
