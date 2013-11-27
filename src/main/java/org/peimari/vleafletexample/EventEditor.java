package org.peimari.vleafletexample;

import org.peimari.vleafletexample.domain.EventWithPoint;
import org.peimari.vleafletexample.domain.EventWithRoute;
import org.peimari.vleafletexample.domain.SpatialEvent;
import org.peimari.vleafletexample.jtsfields.JTSField;
import org.peimari.vleafletexample.jtsfields.LineStringField;
import org.peimari.vleafletexample.jtsfields.PointField;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

public class EventEditor extends Window implements ClickListener {
	private final SpatialEvent spatialEvent;

	private Button save = new Button("Save", this);
	private Button cancel = new Button("Cancel", this);

	private TextField title = new TextField("Title");
	private DateField date = new DateField("Date");
	/* Used for EventWithPoint, field used for bean binding */
	private PointField location;
	/* Used for EventWithRoute, field used for bean binding */
	private LineStringField route;
	private JTSField<?> geometryField;

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

		/* Configure components */
		save.setStyleName(Reindeer.BUTTON_DEFAULT);

		/* Build layout */
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setMargin(true);
		verticalLayout.setSpacing(true);
		verticalLayout.addComponents(title, date, geometryField,
				new HorizontalLayout(save, cancel));
		verticalLayout.setExpandRatio(geometryField, 1);
		verticalLayout.setSizeFull();
		setContent(verticalLayout);

		/* Bind data to fields */
		bindFields(spatialEvent);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void bindFields(SpatialEvent e) {
		/* Std. naming convention based Vaadin field binding. */
		BeanFieldGroup group = new BeanFieldGroup(e.getClass());
		group.setItemDataSource(e);
		group.setBuffered(false);
		group.bindMemberFields(this);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == save) {
			JPAUtil.saveOrPersist(spatialEvent);
		} else {
			/* Reset to persisted state */
			JPAUtil.refresh(spatialEvent);
		}
		close();
	}

}
