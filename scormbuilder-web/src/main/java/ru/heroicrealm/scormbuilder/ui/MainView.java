package ru.heroicrealm.scormbuilder.ui;

import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ImageRenderer;
import ru.heroicrealm.scormbuilder.entities.CatalogEntry;
import ru.heroicrealm.scormbuilder.exceptions.FolderNotFoundException;
import ru.heroicrealm.scormbuilder.service.ICatalogService;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Created by kuran on 30.01.2019.
 */
public class MainView extends VerticalLayout implements View {
    private Button gotoRoot;
    private Button gotoUp;
    private Button newFolder;
    private Button newImage;
    private Button newMovie;
    private Button newText;
    private Button newPresentation;
    private Button newPackage;
    private Button processPackage;
    private Button taskView;

    Grid<CatalogEntry> grid;
    ListDataProvider<CatalogEntry> ldp;
    List<CatalogEntry> folders = new LinkedList<>();
    InitialContext ic = null;
    ICatalogService ics;
    CatalogEntry currentEntry;

    public MainView() {
        HorizontalLayout toolbar = init_toolbar();
        toolbar.setHeight("32px");
        addComponent(toolbar);
        grid = new Grid<>();
        try {
            ic = new InitialContext();
            ics = (ICatalogService) ic.lookup("java:app/scormbuilder-ejb/CatalogServiceImpl");
            folders.addAll(ics.list(1L));
            currentEntry = ics.load(1L);
            ldp = new ListDataProvider<>(folders);
            grid.addColumn(CatalogEntry::getId).setCaption("#");
            Grid.Column<CatalogEntry, ThemeResource> typecol = grid.addColumn(new ValueProvider<CatalogEntry, ThemeResource>() {
                @Override
                public ThemeResource apply(CatalogEntry catalogEntry) {
                    switch (catalogEntry.getOtype()) {
                        case CatalogEntry.FOLDER:
                            return new ThemeResource("ico/folder.png");
                        case CatalogEntry.PRESENTATION:
                            return new ThemeResource("ico/presentation.png");
                        case CatalogEntry.PACKAGE:
                            return new ThemeResource("ico/package.png");
                    }
                    return new ThemeResource("ico/flash.png");
                }
            });
            typecol.setRenderer(new ImageRenderer<>());
            typecol.setCaption("Тип");
            typecol.setWidth(60);

            grid.addColumn(CatalogEntry::getName).setCaption("Имя");
            grid.addColumn(CatalogEntry::getAuthor).setCaption("Автор");
            grid.addColumn(CatalogEntry::getCreatedDate).setCaption("Создано");
            grid.addItemClickListener(itemClick -> {
                if (itemClick.getMouseEventDetails().isDoubleClick()) {
                    CatalogEntry item = itemClick.getItem();
                    switch (item.getOtype()) {
                        case CatalogEntry.FOLDER:
                            openFolder(item.getId());
                            break;
                        case CatalogEntry.PRESENTATION:
                            openPresentation(item.getId());
                            break;
                        case CatalogEntry.PACKAGE:
                            openPackage(item.getId());
                            break;
                    }
                }
            });

            grid.setDataProvider(ldp);
            grid.setWidth("100%");
            grid.setHeightMode(HeightMode.UNDEFINED);
            grid.setSelectionMode(Grid.SelectionMode.MULTI);

            gotoRoot.setEnabled(false);
            gotoUp.setEnabled(false);
            addComponent(grid);
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (FolderNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void openPackage(long id) {
        UI.getCurrent().getNavigator().navigateTo("package-editor/" + currentEntry.getId() + "/" + id);
    }

    private void openPresentation(long id) {
        UI.getCurrent().getNavigator().navigateTo("presentation-editor/" + currentEntry.getId() + "/" + id);
    }

    private void gotoUp() {
        if (currentEntry.getParent() != null) {
            currentEntry = currentEntry.getParent();
            refreshFolder();
        }

    }

    private void gotoRoot() {
        currentEntry = ics.load(1L);
        refreshFolder();
    }

    public void refreshFolder() {
        folders.clear();
        try {
            folders.addAll(ics.list(currentEntry.getId()));
            ldp.refreshAll();
            if (currentEntry.getParent() != null) {
                gotoRoot.setEnabled(true);
                gotoUp.setEnabled(true);
            } else {
                gotoRoot.setEnabled(false);
                gotoUp.setEnabled(false);
            }

        } catch (FolderNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void openFolder(long id) {

        currentEntry = ics.load(id);
        refreshFolder();
    }

    private HorizontalLayout init_toolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        gotoRoot = new Button(new ThemeResource("ico/angle-double-up.png"));
        gotoRoot.addClickListener(clickEvent -> gotoRoot());
        toolbar.addComponent(gotoRoot);
        gotoUp = new Button(new ThemeResource("ico/angle-up.png"));
        gotoUp.addClickListener(clickEvent -> gotoUp());
        toolbar.addComponent(gotoUp);

        newFolder = new Button(new ThemeResource("ico/folder-add.png"));
        newFolder.addClickListener(clickEvent -> createFolder());
        toolbar.addComponent(newFolder);

        newPresentation = new Button(new ThemeResource("ico/file-presentation.png"));
        newPresentation.addClickListener(clickEvent -> createPresentation());
        toolbar.addComponent(newPresentation);

        newPackage = new Button(new ThemeResource("ico/package.png"));
        newPackage.addClickListener(clickEvent -> createPackage());
        toolbar.addComponent(newPackage);

        processPackage = new Button(new ThemeResource("ico/compile.png"));
        processPackage.addClickListener(clickEvent -> compile());
        toolbar.addComponent(processPackage);
        taskView = new Button(new ThemeResource("ico/bullets.png"));
        taskView.addClickListener(clickEvent -> {this.getUI().getNavigator().navigateTo("tasks");});
        toolbar.addComponent(taskView);
        return toolbar;
    }

    private void compile() {
        Set<CatalogEntry> selectedItems = grid.getSelectionModel().getSelectedItems();
        Set<Long> packages = new HashSet<>();
         selectedItems.stream()
                .filter(catalogEntry -> catalogEntry.getOtype() == CatalogEntry.PACKAGE)
                .mapToLong(value -> value.getId()).forEach(value -> packages.add(value));
        if (packages.isEmpty()){
            Notification.show("Select packages", Notification.Type.ERROR_MESSAGE);
            return;
        }
        Connection connection=null;
        try {
            ConnectionFactory cf = (ConnectionFactory) ic.lookup("java:/ConnectionFactory");
            Queue queue = (Queue)ic.lookup("java:/jms/queue/ScormPackerQueue");
            connection = cf.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer publisher = session.createProducer(queue);
            connection.start();
            ObjectMessage task = session.createObjectMessage((Serializable) packages);
            publisher.send(task);
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

    }

    private void createPackage() {
        NewItemWindow niw = new NewItemWindow("Create new package", "Package name");
        niw.setNewItemListener(fldrname -> {
            CatalogEntry ce = null;
            ce = ics.createPackage(currentEntry.getId(), fldrname, "USER");
            folders.add(ce);
            ldp.refreshAll();
        });
        UI.getCurrent().addWindow(niw);
    }



    private void createPresentation() {
        NewItemWindow niw = new NewItemWindow("Create new presentation", "Presentation name");
        niw.setNewItemListener(fldrname -> {
            CatalogEntry ce = null;
            ce = ics.createPresentation(currentEntry.getId(), fldrname, "USER");
            folders.add(ce);
            ldp.refreshAll();
        });
        UI.getCurrent().addWindow(niw);
    }

    private void createFolder() {
        NewItemWindow niw = new NewItemWindow("Create new folder", "Folder name");
        niw.setNewItemListener(fldrname -> {
            CatalogEntry ce = null;
            try {
                ce = ics.createFolder(currentEntry.getId(), fldrname, "USER");
                folders.add(ce);
                ldp.refreshAll();
            } catch (FolderNotFoundException e) {
                e.printStackTrace();
            }

        });
        UI.getCurrent().addWindow(niw);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (event.getParameters() != null && !event.getParameters().isEmpty()) {
            try {
                long l = Long.parseLong(event.getParameters());
                currentEntry = ics.load(l);
                refreshFolder();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }

    }
}
