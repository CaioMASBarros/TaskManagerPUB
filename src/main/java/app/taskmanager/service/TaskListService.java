package app.taskmanager.service;

import app.taskmanager.entities.TaskList;
import app.taskmanager.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskListService {

    List<TaskList> listTaskLists();
    TaskList createTasklist(TaskList taskList, User user);
    Optional<TaskList> getTaskList(UUID id);
    TaskList updateTaskList(UUID taskListId, TaskList taskList);
    void deleteTaskList(UUID taskListId);
}
