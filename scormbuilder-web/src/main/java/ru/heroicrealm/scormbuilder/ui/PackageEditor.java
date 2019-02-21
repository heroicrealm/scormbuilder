package ru.heroicrealm.scormbuilder.ui;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Setter;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ImageRenderer;
import ru.heroicrealm.scormbuilder.entities.*;
import ru.heroicrealm.scormbuilder.service.ICatalogService;
import ru.heroicrealm.scormbuilder.service.IPackageService;
import ru.heroicrealm.scormbuilder.util.CatalogDataProvider;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by kuran on 31.01.2019.
 */
public class PackageEditor extends VerticalLayout implements View {
    HorizontalLayout toolbar;
    Button back;
    Button save;
    Button dependencies;

    Panel panel;
    TabSheet tabSheet;


    long folderId;
    long packageId;

    //region PROP TAB
    TextField tbPropId;
    TextField tbPropName;
    TextField tbPropAuthor;
    DateField tbPropCreated;
    TextField tbPropChangedBy;
    DateField tbPropChanged;
    DateField tbPropBegda;
    DateField tbPropEndda;
    TextArea tbPropComment;
    //endregion

    Grid<ScormRef> refs;

    ICatalogService ics;
    IPackageService ips;

    CatalogEntry ce;
    ScormPackage pack;

    Binder<CatalogEntry> ceBinder;
    Binder<ScormPackage> paBinder;
    ListDataProvider<ScormRef> refDataProvider;
    List<ScormRef> deletedRefs = new LinkedList<>();

    public PackageEditor() {
        try {
            InitialContext ic =new InitialContext();
            ics = (ICatalogService) ic.lookup("java:app/scormbuilder-ejb/CatalogServiceImpl");
            ips = (IPackageService) ic.lookup("java:app/scormbuilder-ejb/PackageServiceImpl");

        } catch (NamingException e) {
            e.printStackTrace();
        }
        toolbar = new HorizontalLayout();
        back = new Button(new ThemeResource("ico/backwards.png"));
        save = new Button(new ThemeResource("ico/database.png"));
        save.addClickListener(clickEvent -> onSave());
        dependencies = new Button(new ThemeResource("ico/file-tree.png"));
        back.addClickListener(clickEvent -> {
            UI.getCurrent().getNavigator().navigateTo("/" + folderId);
        });
        toolbar.addComponents(back, save, dependencies);
        toolbar.setSizeUndefined();

        panel = new Panel();

        panel.setCaption("Package-editor");
        tabSheet = new TabSheet();
        preparePropertiesTab();
        prepareContentTab();
        panel.setContent(tabSheet);
        panel.setSizeFull();
        this.addComponents(toolbar,panel);

      //  this.setSizeFull();
    }

    private void onSave() {
        try {
            ceBinder.writeBean(ce);
            paBinder.writeBean(pack);
            ips.deleteRefs(deletedRefs);
            deletedRefs.clear();
            ics.save(ce);
            ips.save(pack);
            Notification.show("Package saved", Notification.Type.HUMANIZED_MESSAGE);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    private void prepareContentTab() {
        HorizontalLayout hl = new HorizontalLayout();
     //   Panel p = new Panel();
        Tree<CatalogEntry> tree = new Tree<>();

        CatalogDataProvider cdp = new CatalogDataProvider(ics);
        tree.setDataProvider(cdp);
        tree.setItemIconGenerator(catalogEntry -> {
            switch (catalogEntry.getOtype()) {
                case CatalogEntry.FOLDER:
                    return new ThemeResource("ico/folder.png");
                case CatalogEntry.PRESENTATION:
                    return new ThemeResource("ico/presentation.png");
                case CatalogEntry.PACKAGE:
                    return new ThemeResource("ico/package.png");
            }
            return new ThemeResource("ico/flash.png");
        });

        tree.addItemClickListener(itemClick -> itemClicked(itemClick));
        tree.setCaption("Выбор материала:");
    //    p.setContent(tree);
      //  p.setSizeUndefined();
        tree.setSizeUndefined();
    //    p.setWidth("50%");
        hl.addComponent(tree);


        Panel p2 = new Panel();
        refs = new Grid<>();

        refs.addColumn(ScormRef::getSeqnr).setCaption("№");
        refs.addColumn(new ValueProvider<ScormRef, ThemeResource>() {
            @Override
            public ThemeResource apply(ScormRef scormRef) {
                switch (scormRef.getTargetType()) {
                    case FOLDER:
                        return new ThemeResource("ico/folder.png");
                    case PRESENTATION:
                        return new ThemeResource("ico/presentation.png");
                    case PACKAGE:
                        return new ThemeResource("ico/package.png");
                }
                return new ThemeResource("ico/flash.png");
            }
        }).setRenderer(new ImageRenderer<>()).setCaption("Тип");
        refs.addColumn(ScormRef::getName).setCaption("Название");
        refs.setSizeUndefined();
        refs.setSelectionMode(Grid.SelectionMode.MULTI);
        HorizontalLayout minitoolbar = new HorizontalLayout();
        Button delBt = new Button(new ThemeResource("ico/trash.png"));
        delBt.addClickListener(clickEvent -> delRef());
        minitoolbar.addComponent(delBt);
        VerticalLayout vl = new VerticalLayout();
        vl.addComponents(minitoolbar,refs);
        hl.addComponent(vl);
        hl.setSizeFull();
        p2.setCaption("p2");
        tabSheet.addTab(hl,"Содержимое",new ThemeResource("ico/file-tree.png") );

    }

    private void delRef() {
        Set<ScormRef> selectedItems = refs.getSelectionModel().getSelectedItems();
        deletedRefs.addAll(selectedItems);
        List<ScormRef>  srefs = pack.getComponents();
        srefs.removeAll(selectedItems);
        for(int i = 0;i<srefs.size();i++){
            srefs.get(i).setSeqnr(i+1);
        }
        refDataProvider.refreshAll();
    }

    private void itemClicked(Tree.ItemClick<CatalogEntry> itemClick) {
        if(itemClick.getMouseEventDetails().isDoubleClick()){
            ScormRef sref = new ScormRef();
            sref.setName(itemClick.getItem().getName());
            sref.setTargetId(itemClick.getItem().getId());
            sref.setTargetType(ObjType.PRESENTATION);
            Optional<ScormRef> max = pack.getComponents().stream().max(Comparator.comparingInt(ScormRef::getSeqnr));
            if (max.isPresent()){
                sref.setSeqnr(max.get().getSeqnr()+1);
            }else{
                sref.setSeqnr(1);
            }
            pack.getComponents().add(sref);
            refDataProvider.refreshAll();

        }
    }

    private void preparePropertiesTab() {
        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        tbPropId = new TextField("Ид");
        tbPropName = new TextField("Название");
        formLayout.addComponents(tbPropId, tbPropName);
        HorizontalLayout hl = new HorizontalLayout();
        tbPropAuthor = new TextField("Автор");
        tbPropCreated = new DateField("Дата создания");
        hl.addComponents(tbPropAuthor, tbPropCreated);
        formLayout.addComponent(hl);
        hl = new HorizontalLayout();
        tbPropChangedBy = new TextField("Изменил");
        tbPropChanged = new DateField("Дата изменения");
        hl.addComponents(tbPropChangedBy, tbPropChanged);
        formLayout.addComponent(hl);
        hl = new HorizontalLayout();
        tbPropBegda = new DateField("Принят",LocalDate.now());
        tbPropEndda = new DateField("Актуален",LocalDate.MAX);
        hl.addComponents(tbPropBegda,tbPropEndda);
        formLayout.addComponent(hl);
        tbPropComment = new TextArea("Комментарии");
        tbPropComment.setRows(20);
        tbPropComment.setWidth(50,Unit.PERCENTAGE);
        formLayout.addComponent(tbPropComment);
        tabSheet.addTab(formLayout,"Свойства",new ThemeResource("ico/cog.png"));

        ceBinder = new Binder<>();
        paBinder = new Binder<>();
        ceBinder.forField(tbPropId)
                .withConverter(new StringToLongConverter("Must be long"))
                .bind(CatalogEntry::getId,CatalogEntry::setId);

        ceBinder.bind(tbPropName,CatalogEntry::getName,CatalogEntry::setName);
        ceBinder.bind(tbPropAuthor,CatalogEntry::getAuthor,CatalogEntry::setAuthor);
        ceBinder.bind(tbPropChangedBy,CatalogEntry::getChangedBy,CatalogEntry::setChangedBy);

       ceBinder.bind(tbPropCreated, new ValueProvider<CatalogEntry, LocalDate>() {
           @Override
           public LocalDate apply(CatalogEntry catalogEntry) {
               return catalogEntry.getCreatedDate() == null ? null :
                       catalogEntry.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
           }
       }, new Setter<CatalogEntry, LocalDate>() {
           @Override
           public void accept(CatalogEntry catalogEntry, LocalDate localDate) {
               catalogEntry.setCreatedDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
           }
       });

        ceBinder.bind(tbPropChanged, new ValueProvider<CatalogEntry, LocalDate>() {
            @Override
            public LocalDate apply(CatalogEntry catalogEntry) {
                return catalogEntry.getChangedDate() == null ? null :
                        catalogEntry.getChangedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
        }, new Setter<CatalogEntry, LocalDate>() {
            @Override
            public void accept(CatalogEntry catalogEntry, LocalDate localDate) {
                catalogEntry.setChangedDate(null);
                if (localDate!=null) {
                    catalogEntry.setChangedDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                }
            }
        });
        paBinder.bind(tbPropComment,ScormPackage::getComment,ScormPackage::setComment);

        paBinder.bind(tbPropBegda, new ValueProvider<ScormPackage, LocalDate>() {
            @Override
            public LocalDate apply(ScormPackage pk) {
                return pk.getBegda() == null ? null :
                        pk.getBegda().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
        }, new Setter<ScormPackage, LocalDate>() {
            @Override
            public void accept(ScormPackage pk, LocalDate localDate) {
                pk.setBegda(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
        });

        paBinder.bind(tbPropEndda, new ValueProvider<ScormPackage, LocalDate>() {
            @Override
            public LocalDate apply(ScormPackage pk) {
                return pk.getEndda() == null ? null :
                        pk.getEndda().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
        }, new Setter<ScormPackage, LocalDate>() {
            @Override
            public void accept(ScormPackage pk, LocalDate localDate) {
                pk.setEndda(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
        });

        tbPropCreated.setReadOnly(true);
        tbPropAuthor.setReadOnly(true);
        tbPropChanged.setReadOnly(true);
        tbPropChangedBy.setReadOnly(true);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (event.getParameters() == null || event.getParameters().isEmpty()) {
            UI.getCurrent().getNavigator().navigateTo("/1");
        } else {
            String[] split = event.getParameters().split("/");
            if (split == null || split.length != 2) {
                UI.getCurrent().getNavigator().navigateTo("/1");
            } else {
                try {
                    folderId = Long.parseLong(split[0]);
                    packageId = Long.parseLong(split[1]);
                    ce = ics.load(packageId);
                    pack = ips.load(packageId);
                    ceBinder.readBean(ce);
                    paBinder.readBean(pack);
                    refDataProvider = new ListDataProvider<>(pack.getComponents());
                    refs.setDataProvider(refDataProvider);
                    refDataProvider.refreshAll();
                } catch (NumberFormatException e) {
                    UI.getCurrent().getNavigator().navigateTo("/1");
                }

            }

        }
    }
}

