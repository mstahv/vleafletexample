package org.peimari.vleafletexample.domain;

import javax.persistence.Entity;

import com.vividsolutions.jts.geom.LineString;

@Entity
public class EventWithRoute extends SpatialEvent {

	public LineString getRoute() {
		return (LineString) getGeom();
	}

	public void setRoute(LineString route) {
		setGeom(route);
	}

}