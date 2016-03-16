package org.peimari.vleafletexample;

import com.vaadin.addon.contextmenu.ContextMenu;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.annotation.WebServlet;

import org.peimari.vleafletexample.domain.EventWithPoint;
import org.peimari.vleafletexample.domain.EventWithRoute;
import org.peimari.vleafletexample.domain.SpatialEvent;
import org.vaadin.addon.leaflet.AbstractLeafletLayer;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.util.JTSUtil;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import org.vaadin.addon.leaflet.LOpenStreetMapLayer;
import org.vaadin.addon.leaflet.shared.Point;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

@Theme("valo")
@SuppressWarnings("serial")
public class GisUI extends UI implements ClickListener, CloseListener {

    private Point lastContextMenuPosition;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = GisUI.class, widgetset = "org.peimari.vleafletexample.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    private EntityManager em;

    private RichText infoText = new RichText().withMarkDown(
            "###V-Leaflet example\n\n"
            + "This is small example app to demonstrate how to add simple GIS "
            + "features to your Vaadin apps. "
            + "[Check out the sources!](https://github.com/mstahv/vleafletexample)");
    private MTable<SpatialEvent> table;
    private Button addNew = new Button("Add event with a location", this);
    private Button addNewWithRoute = new Button("Add event with a route", this);
    private LMap map = new LMap();
    private LTileLayer osmTiles = new LOpenStreetMapLayer();

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

        table = new MTable<>(SpatialEvent.class);
        table.setWidth("100%");

        table.withGeneratedColumn("Actions", spatialEvent -> {
            Button edit = new MButton(FontAwesome.EDIT, e -> {
                EventEditor eventEditor = new EventEditor(spatialEvent);
                addWindow(eventEditor);
            });
            Button delete = new MButton(FontAwesome.TRASH, e -> {
                JPAUtil.remove(spatialEvent);
                loadEvents();
            });
            return new MHorizontalLayout(edit, delete);
        });
        table.withProperties("id", "title", "date", "Actions");

        loadEvents();

        osmTiles.setAttributionString("© OpenStreetMap Contributors");

        HorizontalLayout actions = new MHorizontalLayout(addNew, addNewWithRoute);
        setContent(new MVerticalLayout(infoText, actions).expand(map, table));

        // You can also use ContextMenu Add-on with Vaadin
        // Give "false" as a second parameter, we'll open context menu programmatically
        ContextMenu contextMenu = new ContextMenu(map, false);
        contextMenu.addItem("Add new event here", e -> {
            EventWithPoint eventWithPoint = new EventWithPoint();
            eventWithPoint.setLocation(JTSUtil.toPoint(lastContextMenuPosition));
            addWindow(new EventEditor(eventWithPoint));
        });

        map.addContextMenuListener(event -> {
            // save the point to be used by listener
            lastContextMenuPosition = event.getPoint();
            // you could here also configure what to show in the menu
            contextMenu.open((int) event.getClientX(), (int) event.getClientY());
        });

    }

    private void loadEvents() {

        List<SpatialEvent> events = JPAUtil.listAllSpatialEvents();

        /* Populate table... */
        table.setBeans(events);

        /* ... and map */
        map.removeAllComponents();
        map.addBaseLayer(osmTiles, "OSM");
        for (final SpatialEvent spatialEvent : events) {
            if (spatialEvent.getGeom() != null) {
                /* 
                 * JTSUtil wil make LMarker for point event, 
                 * LPolyline for events with route 
                 */
                AbstractLeafletLayer layer = (AbstractLeafletLayer) JTSUtil.
                        toLayer(spatialEvent.getGeom());

                /* Add click listener to open event editor */
                layer.addClickListener(event -> {
                    EventEditor eventEditor = new EventEditor(spatialEvent);
                    addWindow(eventEditor);
                });
                map.addLayer(layer);
            }
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
