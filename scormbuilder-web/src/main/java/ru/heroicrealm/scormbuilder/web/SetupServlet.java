package ru.heroicrealm.scormbuilder.web;

import ru.heroicrealm.scormbuilder.entities.CatalogEntry;
import ru.heroicrealm.scormbuilder.entities.ObjType;
import ru.heroicrealm.scormbuilder.entities.Task;
import ru.heroicrealm.scormbuilder.exceptions.FolderNotFoundException;
import ru.heroicrealm.scormbuilder.service.ICatalogService;
import ru.heroicrealm.scormbuilder.service.ITaskService;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by kuran on 19.01.2019.
 */
@WebServlet("/setup")
public class SetupServlet extends javax.servlet.http.HttpServlet {

    @EJB
    ITaskService its;

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

     //   try {

       /* } catch (FolderNotFoundException e) {
            e.printStackTrace();
        }*/
        List<Task> user = its.listTasksForUser("USER");
        System.out.println("User tasks:");
        user.stream().forEach(task -> System.out.println(task.getGuid()+":"+task.getOwner()));

        user = its.listTasksForUser("USER2");
        System.out.println("User2 tasks:");
        user.stream().forEach(task -> System.out.println(task.getGuid()+":"+task.getOwner()));

    }
}
