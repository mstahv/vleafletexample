package org.peimari.vleafletexample;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.annotation.WebServlet;

import org.peimari.vleafletexample.domain.EventWithPoint;
import org.peimari.vleafletexample.domain.EventWithRoute;
import org.peimari.vleafletexample.domain.SpatialEvent;
import org.vaadin.addon.leaflet.AbstractLeafletLayer;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.util.JTSUtil;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

@Theme("mytheme")
@SuppressWarnings("serial")
public class GisUI extends UI implements ClickListener, CloseListener {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = GisUI.class, widgetset = "org.peimari.vleafletexample.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    private EntityManager em;

    private Label infoText = new Label("<h1>V-Leaflet example</h1>"
            + "<p>This is small example app to demonstrate how to "
            + "add simple GIS features to your Vaadin apps. "
            + "<a href='https://github.com/mstahv/vleafletexample'>"
            + "Check out sources</a></p>",
            ContentMode.HTML);
    private Table table;
    private Button addNew = new Button("new event with location", this);
    private Button addNewWithRoute = new Button("new event with route", this);
    private LMap map = new LMap();
    private LTileLayer osmTiles = new LTileLayer(
            "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png");

    public static GisUI get() {
        return (GisUI) UI.getCurrent();
    }

    /**
     * This demo app uses "session per application(~http session)". Returns an
     * entity manager that should be used by this UI/user/"application
     * instance".
     *
     * @return
     */
    public EntityManager getEntityManager() {
        if (em == null || !em.isOpen()) {
            em = JPAUtil.createEntityManager();
        }
        return em;
    }

    @Override
    protected void init(VaadinRequest request) {

        table = new Table();
        table.setWidth("100%");

        table.addGeneratedColumn("Actions", new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object spatialEvent,
                    Object columnId) {
                Button edit = new Button("Edit", new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        SpatialEvent pojo = (SpatialEvent) spatialEvent;
                        EventEditor eventEditor = new EventEditor(pojo);
                        addWindow(eventEditor);
                    }
                });
                Button delete = new Button("Delete", new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        JPAUtil.remove(spatialEvent);
                        table.removeItem(spatialEvent);
                    }
                });
                HorizontalLayout rowActions = new HorizontalLayout(edit, delete);
                rowActions.setSpacing(true);
                return rowActions;
            }
        });

        loadEvents();

        osmTiles.setAttributionString("© OpenStreetMap Contributors");

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        HorizontalLayout actions = new HorizontalLayout(addNew, addNewWithRoute);
        actions.setSpacing(true);
        layout.addComponents(infoText, actions, map, table);
        layout.setExpandRatio(map, 1);
        layout.setExpandRatio(table, 1);
        table.setSizeFull();
        layout.setSizeFull();
        setContent(layout);
    }

    private void loadEvents() {

        List<SpatialEvent> events = JPAUtil.listAllSpatialEvents();

        /* Populate table... */
        BeanItemContainer<SpatialEvent> container = new BeanItemContainer<SpatialEvent>(
                SpatialEvent.class);
        container.addAll(events);
        table.setContainerDataSource(container);
        table.setVisibleColumns("id", "title", "date", "Actions");

        /* ... and map */
        map.removeAllComponents();
        map.addBaseLayer(osmTiles, "OSM");
        for (final SpatialEvent spatialEvent : events) {
            /* 
             * JTSUtil wil make LMarker for point event, 
             * LPolyline for events with route 
             */
            AbstractLeafletLayer layer = (AbstractLeafletLayer) JTSUtil.toLayer(spatialEvent.getGeom());

            /* Add click listener to open event editor */
            layer.addClickListener(new LeafletClickListener() {
                @Override
                public void onClick(LeafletClickEvent event) {
                    EventEditor eventEditor = new EventEditor(spatialEvent);
                    addWindow(eventEditor);
                }
            });
            map.addLayer(layer);
        }
        map.zoomToContent();

    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == addNew) {
            addWindow(new EventEditor(new EventWithPoint()));
        } else if (event.getButton() == addNewWithRoute) {
            addWindow(new EventEditor(new EventWithRoute()));
        }
    }

    @Override
    public void addWindow(Window window) throws IllegalArgumentException,
            NullPointerException {
        super.addWindow(window);
        window.addCloseListener(this);
    }

    @Override
    public void windowClose(CloseEvent e) {
        // refresh table after edit
        loadEvents();
    }

}
