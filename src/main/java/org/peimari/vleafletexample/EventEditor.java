package org.peimari.vleafletexample;

import org.peimari.vleafletexample.domain.EventWithPoint;
import org.peimari.vleafletexample.domain.EventWithRoute;
import org.peimari.vleafletexample.domain.SpatialEvent;
import org.vaadin.addon.leaflet.util.AbstractJTSField;
import org.vaadin.addon.leaflet.util.PointField;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import org.apache.log4j.Logger;
import org.vaadin.addon.leaflet.editable.LineStringField;
import org.vaadin.viritin.button.PrimaryButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MVerticalLayout;

public class EventEditor extends Window implements ClickListener {
	private final SpatialEvent spatialEvent;

	private Button save = new PrimaryButton("Save", this);
	private Button cancel = new Button("Cancel", this);

	private TextField title = new MTextField("Title");
	private DateField date = new DateField("Date");
	/* Used for EventWithPoint, field used for bean binding */
	@SuppressWarnings("unused")
	private PointField location;
	/* Used for EventWithRoute, field used for bean binding */
	@SuppressWarnings("unused")
	private LineStringField route;
	private AbstractJTSField<?> geometryField;

	public EventEditor(SpatialEvent spatialEvent) {
		this.spatialEvent = spatialEvent;

		/* Choose suitable custom field for geometry */
		if (spatialEvent instanceof EventWithPoint) {
			geometryField = location = new PointField();
		} else if (spatialEvent instanceof EventWithRoute) {
			geometryField = route = new LineStringField();
		}

		/* Configure the sub window editing the pojo */
		setCaption("Edit event");
		setHeight("80%");
		setWidth("80%");
		setModal(true);
		setClosable(false); // only via save/cancel

		/* Build layout */
		MVerticalLayout verticalLayout = new MVerticalLayout(title, date, geometryField,
				new HorizontalLayout(save, cancel)).expand(geometryField);
		setContent(verticalLayout);

		/* Bind data to fields */
		bindFields(spatialEvent);
        title.focus();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void bindFields(SpatialEvent e) {
		/* Std. naming convention based Vaadin field binding. */
        BeanFieldGroup.bindFieldsUnbuffered(e, this);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == save) {
			try {
				JPAUtil.saveOrPersist(spatialEvent);
			} catch (Exception e) {
				// Most likely a concurrent modification
				Notification.show("Saving entity failed due to concurrent modification",
						Notification.Type.ERROR_MESSAGE);
				Logger.getLogger(EventEditor.class).info("JPA Exception", e);
				JPAUtil.refresh(spatialEvent);
			}
		} else {
			/* Reset to persisted state */
			JPAUtil.refresh(spatialEvent);
		}
		close();
	}

}
