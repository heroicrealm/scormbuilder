package ru.heroicrealm.scormbuilder.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import ru.heroicrealm.scormbuilder.entities.CatalogEntry;
import ru.heroicrealm.scormbuilder.service.ICatalogService;
import ru.heroicrealm.scormbuilder.service.IConfigService;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;

/**
 * Created by kuran on 05.02.2019.
 */
public class ConfigView extends FormLayout implements View {

    TextField tbStoragePath;
    Button btInitDb;
    Button btSaveStoragePath;

    ICatalogService ics;
    IConfigService configService;

    public ConfigView() {
        try {
            InitialContext ic = new InitialContext();
            ics = (ICatalogService) ic.lookup("java:app/scormbuilder-ejb/CatalogServiceImpl");
            configService = (IConfigService) ic.lookup("java:app/scormbuilder-ejb/ConfigServiceImpl");
            btInitDb = new Button();
            btInitDb.setCaption("Init DB");
            this.addComponent( btInitDb);

            HorizontalLayout hl = new HorizontalLayout();
            tbStoragePath = new TextField("Storage Path");
            hl.addComponent(tbStoragePath);
            btSaveStoragePath = new Button("Save");
            hl.addComponent(btSaveStoragePath);
            this.addComponent(hl);



            btInitDb.addClickListener(clickEvent -> initDb());
            btSaveStoragePath.addClickListener(clickEvent ->{ configService.setProperty(IConfigService.FS_BASE_PATH, tbStoragePath.getValue());
            Notification.show("Storage path saved", Notification.Type.HUMANIZED_MESSAGE);
            });

        } catch (NamingException e) {
            e.printStackTrace();
        }

    }

    private void initDb() {
        CatalogEntry ce =new CatalogEntry();
        ce.setOtype(CatalogEntry.FOLDER);
        ce.setName("ROOT");
        ce.setAuthor("SYSTEM");
        ce.setCreatedDate(new Date());
        ics.save(ce);
        Notification.show("Root created", Notification.Type.HUMANIZED_MESSAGE);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        CatalogEntry load = ics.load(1L);
        if(load!=null){
            btInitDb.setEnabled(false);
        }else {
            btInitDb.setEnabled(true);
        }
        String storagePath = configService.getProperty(IConfigService.FS_BASE_PATH);

        tbStoragePath.setValue(storagePath==null?"":storagePath);

    }
}
