package ru.heroicrealm.scormbuilder.ui;

import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.AbstractRenderer;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.ImageRenderer;
import ru.heroicrealm.scormbuilder.entities.Task;
import ru.heroicrealm.scormbuilder.service.IConfigService;
import ru.heroicrealm.scormbuilder.service.ITaskService;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kuran on 06.02.2019.
 */
public class TaskView extends BaseView {



    InitialContext ic;
    ITaskService its;
    IConfigService configService;

    List<Task> tasks = new LinkedList<>();
    ListDataProvider<Task> ldp = new ListDataProvider<>(tasks);

    Grid<Task> taskGrid;

    public TaskView() {
        super("Task view");

        try {
            ic = new InitialContext();
            its = (ITaskService) ic.lookup("java:app/scormbuilder-ejb/TaskServiceImpl");
            configService = (IConfigService) ic.lookup("java:app/scormbuilder-ejb/ConfigServiceImpl");
            taskGrid = new Grid<>();
            taskGrid.addColumn(Task::getGuid).setCaption("ID");
            taskGrid.addColumn(Task::getName).setCaption("Имя");
            taskGrid.addColumn(Task::getStart).setCaption("Дата начала");
            taskGrid.addColumn(Task::getEnd).setCaption("Дата окончания");
            taskGrid.addColumn(new ValueProvider<Task, ThemeResource>() {
                @Override
                public ThemeResource apply(Task task) {
                    switch (task.getTaskType()){
                        case PACKAGE:return new ThemeResource("ico/package.png");
                    };
                    return new ThemeResource("ico/diamonf.png");
                }
            }).setRenderer(new ImageRenderer<>()).setCaption("Тип");

            taskGrid.addColumn(new ValueProvider<Task, Component>() {
                @Override
                public Component apply(Task task) {
                    switch (task.getStatus()){
                        case STARTED:return new Image("",new ThemeResource("ico/cogs.png"));
                        case ABORTED:return   new Image("",new ThemeResource("ico/close-circle.png"));
                        case FINISHED:{
                            Button download = new Button("Finish");
                            StreamResource myResource = createResource(task);
                            FileDownloader fileDownloader = new FileDownloader(myResource);
                            fileDownloader.extend(download);
                            return download;
                        }//ThemeResource("ico/check-circle.png");
                    }
                    return new Label("Error");// ThemeResource("ico/diamonf.png");
                }
            }).setRenderer(new ComponentRenderer()).setCaption("Статус");




            taskGrid.setSizeFull();
            taskGrid.setDataProvider(ldp);


            panel.setContent(taskGrid);




        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private StreamResource createResource(Task task) {
        return new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                String storagePath = configService.getProperty(IConfigService.FS_BASE_PATH);
                try {
                    return new FileInputStream(storagePath+ File.separator+"out"+File.separator+task.getGuid()+".zip");
                } catch (FileNotFoundException e) {
                    return null;
                }
            }
        }, task.getName()+".zip");

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        super.enter(event);
        tasks.clear();
        tasks.addAll(its.listTasksForUser("USER"));
        ldp.refreshAll();
    }
}
