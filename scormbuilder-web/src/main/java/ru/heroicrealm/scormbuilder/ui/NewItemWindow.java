package ru.heroicrealm.scormbuilder.ui;

import com.vaadin.ui.*;
import ru.heroicrealm.scormbuilder.util.IEvtNewFolder;

import java.util.Optional;

/**
 * Created by kuran on 30.01.2019.
 */
public class NewItemWindow extends Window {
    TextField tbItemName;

    Button btOk;
    Button btCancel;
    IEvtNewFolder newItemListener;

    NewItemWindow(String title,String propname){
        newItemListener = null;
        VerticalLayout vl = new VerticalLayout();
        tbItemName =new TextField(propname);
        tbItemName.setWidth("100%");
        vl.addComponent(tbItemName);
        HorizontalLayout hl = new HorizontalLayout();
        btOk = new Button("Ok");
        btOk.addClickListener(clickEvent -> {
            if(newItemListener != null) {
                newItemListener.onNewFolderEvent(tbItemName.getValue());
            }
            close();
        });
        btCancel = new Button("Cancel");
        btCancel.addClickListener(clickEvent -> close());
        hl.addComponents(btOk,btCancel);
        vl.addComponent(hl);
        this.setCaption(title);
        this.setContent(vl);
        this.setModal(true);
        this.setWidth("300px");
    }

    public void setNewItemListener(IEvtNewFolder newItemListener) {
        this.newItemListener = newItemListener;
    }
}
