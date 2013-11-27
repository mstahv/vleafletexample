package org.peimari.vleafletexample;

import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.shared.Point;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

/**
 * Helper methods to convert between JTS - v-leaflet objects.
 * 
 * TODO: This class should be made obsolete by implementing
 * https://github.com/mstahv/v-leaflet/issues/18
 */
public class JTSUtil {

	public static org.vaadin.addon.leaflet.shared.Point[] toLeafletPointArray(
			LineString path) {
		Coordinate[] coordinates = path.getCoordinates();
		org.vaadin.addon.leaflet.shared.Point[] points = new org.vaadin.addon.leaflet.shared.Point[coordinates.length];
		for (int i = 0; i < points.length; i++) {
			Coordinate coordinate = coordinates[i];
			points[i] = new org.vaadin.addon.leaflet.shared.Point(coordinate.y,
					coordinate.x);
		}
		return points;
	}

	public static LineString toLineString(
			org.vaadin.addon.leaflet.shared.Point[] points) {
		GeometryFactory factory = new GeometryFactory();
		Coordinate[] coordinates = new Coordinate[points.length];
		for (int i = 0; i < coordinates.length; i++) {
			Point p = points[i];
			coordinates[i] = new Coordinate(p.getLon(), p.getLat());
		}
		return factory.createLineString(coordinates);
	}

	public static LineString toLineString(LPolyline polyline) {
		Point[] points = polyline.getPoints();
		return toLineString(points);
	}

	public static com.vividsolutions.jts.geom.Point toPoint(
			org.vaadin.addon.leaflet.shared.Point p) {
		com.vividsolutions.jts.geom.Point point = new GeometryFactory()
				.createPoint(new Coordinate(p.getLon(), p.getLat()));
		return point;
	}

	public static com.vividsolutions.jts.geom.Point toPoint(LMarker lMarker) {
		return toPoint(lMarker.getPoint());
	}

	public static org.vaadin.addon.leaflet.shared.Point toLeafletPoint(
			com.vividsolutions.jts.geom.Point location) {
		org.vaadin.addon.leaflet.shared.Point p = new org.vaadin.addon.leaflet.shared.Point(
				location.getY(), location.getX());
		return p;
	}

}
