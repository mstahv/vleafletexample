package org.peimari.vleafletexample.domain;

import javax.persistence.Entity;

import com.vividsolutions.jts.geom.Point;

@Entity
public class EventWithPoint extends SpatialEvent {

	public Point getLocation() {
		return (Point) getGeom();
	}

	public void setLocation(Point location) {
		setGeom(location);
	}
	
}