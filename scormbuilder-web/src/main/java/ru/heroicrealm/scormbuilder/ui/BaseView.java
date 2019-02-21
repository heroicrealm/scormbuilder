package ru.heroicrealm.scormbuilder.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;

/**
 * Created by kuran on 10.02.2019.
 */
public class BaseView extends VerticalLayout implements View {
    HorizontalLayout toolbar;
    Button back;
    long folderId;
    Panel panel;

   public BaseView(String title){
        toolbar = new HorizontalLayout();
        back = new Button(new ThemeResource("ico/backwards.png"));
        back.addClickListener(clickEvent -> {
            UI.getCurrent().getNavigator().navigateTo("/"+folderId);
        });
        toolbar.addComponent(back);
        panel = new Panel(title);
        toolbar.setHeight(64,Unit.PIXELS);
        this.addComponents(toolbar,panel);
        panel.setSizeFull();
        this.setExpandRatio(panel,1);
        setSpacing(false);
        setMargin(false);
        setSizeFull();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
