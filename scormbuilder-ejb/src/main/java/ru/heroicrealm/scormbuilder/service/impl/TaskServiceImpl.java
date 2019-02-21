package ru.heroicrealm.scormbuilder.service.impl;

import ru.heroicrealm.scormbuilder.entities.Task;
import ru.heroicrealm.scormbuilder.service.ITaskService;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by kuran on 03.02.2019.
 */
@Stateless
public class TaskServiceImpl implements ITaskService{

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public Task createTask(Task.TaskType type, String name, String owner) {
        Task task = new Task();
        task.setGuid(UUID.randomUUID().toString());
        task.setStart(Date.from(Instant.now()));
        task.setName(name);
        task.setOwner(owner);
        task.setTaskType(Task.TaskType.PACKAGE);
        task.setStatus(Task.Status.STARTED);
        entityManager.persist(task);
        return task;
    }

    @Override
    public List<Task> listTasksForUser(String user) {

        return entityManager.createQuery("FROM Task WHERE owner=:owner",Task.class).setParameter("owner",user).getResultList();

    }

    @Override
    public void finishTask(Task task) {
        task.setStatus(Task.Status.FINISHED);
        entityManager.merge(task);
    }

    @Override
    public void abortTask(Task task) {
        task.setStatus(Task.Status.ABORTED);
        entityManager.merge(task);
    }
}
