package ru.heroicrealm.scormbuilder.service;

import ru.heroicrealm.scormbuilder.entities.Task;

import java.util.List;

/**
 * Created by kuran on 03.02.2019.
 */
public interface ITaskService {
    Task createTask(Task.TaskType type, String name, String owner);

    List<Task> listTasksForUser(String user);

    void finishTask(Task task);
    void abortTask(Task task);
}
