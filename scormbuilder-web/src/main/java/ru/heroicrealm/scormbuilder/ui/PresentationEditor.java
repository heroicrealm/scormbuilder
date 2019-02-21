package ru.heroicrealm.scormbuilder.ui;


import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ImageRenderer;
import ru.heroicrealm.scormbuilder.entities.Presentation;
import ru.heroicrealm.scormbuilder.entities.PresentationPage;
import ru.heroicrealm.scormbuilder.service.IPresentationService;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by kuran on 30.01.2019.
 */
public class PresentationEditor extends BaseView{

    Button save;
    Button dependencies;
    Label label;

    long presentationId;

    Presentation presentation;
    InitialContext ic;
    IPresentationService ips;
    Grid<PresentationPage> pageGrid;
    TextField tbTitle;
    TextField tbNo;
    RichTextArea rta;
    ListDataProvider<PresentationPage> ldp;

    PresentationPage currentPage;
    Binder<PresentationPage> pageBinder;
    List<PresentationPage> deletedPages;

    public PresentationEditor() {
        super ("Presentation editor");
        currentPage = null;
        deletedPages = new LinkedList<>();
        try {
            ic = new InitialContext();
            ips = (IPresentationService) ic.lookup("java:app/scormbuilder-ejb/PresentationServiceImpl");
        } catch (NamingException e) {
            e.printStackTrace();
        }

        save = new Button(new ThemeResource("ico/database.png"));
        dependencies = new Button(new ThemeResource("ico/file-tree.png"));
        back.addClickListener(clickEvent -> {
            UI.getCurrent().getNavigator().navigateTo("/"+folderId);
        });
        save.addClickListener(clickEvent -> savePresentation());
        toolbar.addComponents(save,dependencies);

        label=new Label();
        HorizontalLayout hl = new HorizontalLayout();
        VerticalLayout pageList = new VerticalLayout();
        HorizontalLayout smallToolbar = new HorizontalLayout();
        Button addPage = new Button(new ThemeResource("ico/file-add.png"));
        addPage.addClickListener(clickEvent -> addPage());
        Button delPage = new Button(new ThemeResource("ico/trash.png"));
        delPage.addClickListener(clickEvent -> deletePage());
        Button moveUp = new Button(new ThemeResource("ico/arrow-circle-up-o.png"));
        Button moveDown = new Button(new ThemeResource("ico/arrow-circle-down-o.png"));
        smallToolbar.addComponents(addPage,delPage,moveUp,moveDown);
        pageList.addComponent(smallToolbar);
        pageList.setWidth(500,Unit.PIXELS);
        pageGrid = new Grid<>();
        pageGrid.addColumn(PresentationPage::getPageNo).setCaption("№");
        pageGrid.addColumn(PresentationPage::getTitle).setCaption("Заголовок");
        pageList.addComponent(pageGrid);
        pageGrid.setWidthUndefined();
        pageGrid.addItemClickListener(itemClick -> openPage(itemClick));
        hl.addComponent(pageList);


        VerticalLayout pcontent = new VerticalLayout();
        HorizontalLayout info = new HorizontalLayout();
        tbTitle = new TextField("Page title");
        tbNo = new TextField("Page No");
        tbNo.setReadOnly(true);
        info.addComponents(tbNo,tbTitle);
        pcontent.addComponent(info);
        rta = new RichTextArea();
        rta.setHeight(600,Unit.PIXELS);
        rta.setWidth(1200,Unit.PIXELS);

        pcontent.addComponent(rta);
        hl.addComponent(pcontent);

        panel.setContent(hl);



        pageBinder = new Binder<>();
        pageBinder.bind(tbTitle,PresentationPage::getTitle,PresentationPage::setTitle);
        pageBinder.bind(rta,PresentationPage::getContent,PresentationPage::setContent);
        pageBinder.bind(tbNo,PresentationPage::getPageNos,PresentationPage::setPageNos);
        pageGrid.setSelectionMode(Grid.SelectionMode.MULTI);
    }

    private void deletePage() {

        Set<PresentationPage> selectedItems = pageGrid.getSelectionModel().getSelectedItems();
        System.out.println(selectedItems.size());
        deletedPages.addAll(selectedItems);
        List<PresentationPage> pages = presentation.getPages();
        pages.removeAll(selectedItems);

        for(int i = 0;i<pages.size();i++){
            pages.get(i).setPageNo(i+1);
        }
        ldp.refreshAll();

    }

    private void savePresentation() {
        try {
            if (currentPage!=null && pageBinder!=null) {
                pageBinder.writeBean(currentPage);
            }
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        ips.deletePages(deletedPages);
        deletedPages.clear();
        ips.savePresentation(presentation);
        Notification.show("Presentation saved", Notification.Type.HUMANIZED_MESSAGE);
    }

    private void addPage() {
        PresentationPage pp = new PresentationPage();
        Optional<PresentationPage> max = presentation.getPages().stream().max(Comparator.comparingInt(PresentationPage::getPageNo));
        if (max.isPresent()){
            pp.setPageNo(max.get().getPageNo()+1);
        }else{
            pp.setPageNo(1);
        }
        pp.setPresentation(presentation);
        presentation.getPages().add(pp);
        ldp.refreshAll();
    }

    private void openPage(Grid.ItemClick<PresentationPage> itemClick) {
        if(itemClick.getMouseEventDetails().isDoubleClick()) {
            if (currentPage == null) {
                currentPage = itemClick.getItem();
                pageBinder.readBean(currentPage);
            } else {
                try {
                    pageBinder.writeBean(currentPage);
                    currentPage = itemClick.getItem();
                    pageBinder.readBean(currentPage);

                } catch (ValidationException e) {
                    e.printStackTrace();
                }
            }
            ldp.refreshAll();
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        super.enter(event);
        if (event.getParameters()==null || event.getParameters().isEmpty()){
            label.setValue("No params");
        }else{
            String[] split = event.getParameters().split("/");
            if (split == null || split.length != 2){
                UI.getCurrent().getNavigator().navigateTo("/1");
            }else {
                try{
                    folderId = Long.parseLong(split[0]);
                    presentationId = Long.parseLong(split[1]);
                }catch (NumberFormatException e){
                    UI.getCurrent().getNavigator().navigateTo("/1");
                }
                presentation = ips.load(presentationId);
                panel.setCaption(presentation.getName());
                ldp = new ListDataProvider<>(presentation.getPages());
                pageGrid.setDataProvider(ldp);
            }
            label.setValue(event.getParameters());
            for (String key :event.getParameterMap().keySet()){
                Label l = new Label("K:"+key+" V:"+event.getParameterMap().get(key));
            }
        }
    }
}
