V-Leaflet example project
=========================

This is a small usage example of [V-Leaflet](https://github.com/mstahv/v-leaflet/) in a small [Vaadin](http://vaadin.com/) application. It is built to show how easy it can be to add some GIS features into typical Vaadin application - even without setting up a separate [geoserver](http://geoserver.org) or its various commercial competitors. The app uses simple "domain driven JPA backend". The UI itself is rather rude and e.g. error handling is minimal, but the code should be easy to understand as a Leaflet usage example.

This example app is not trying to demonstrate all V-Leaflet features, but just to give an overall picture. For relevant V-Leaflet specific code examples, see at least these classes:

 * GisUI : The main UI that contains basic map, OSM base layer and data queried from the JPA backend
 * JTSField and its subclasses PointField and LineStringField
   * These implement Vaadin Field interface to edit JTS data types that are used in model objects. They demonstrate usage of [Leaflet.Draw](https://github.com/Leaflet/Leaflet.draw) features. In EventEditor form these fields are used to do normal Vaadin bean binding to edit JPA entities. Field implementations might be useful in your own apps as well, feel free to use them.

The app uses work in progress version of Hibernates spatial extensions, but that is by no means necessary with v-leaflet. The JPA session is used in J2SE style, but should be easy to adapt to e.g jboss, just use extended persistence context. This is fine for at least apps with moderate concurrent users. The used database is h2 (for portability), but I'd bet using PostgreSQL/PostGIS is a more tested combination for production usage.

To play with the project you just need to import it into your favorite IDE, execute "mvn package" and deploy it into server (or use "mvn jetty:run").

Things planned to improve the demo:

 * JTS support (and built in Field implementations) to v-leaflet add-on
 * Example of spatial query so that only relevant entities are selected from database to maps viewport.

Links to projects that "make this possible"
-------------------------------------------
 
 * [Vaadin](http://vaadin.com/) - The server side Java library that makes web programming as easy as swing
 * [LeafletJS](http://leafletjs.com) - The leading slippy map for web
 * [V-Leaflet](https://github.com/mstahv/v-leaflet) - Vaadin API for LeafletJS
 * [JTS Topology Suite](http://tsusiatsoftware.net/jts/main.html) - "Industry standard" geospatial library for Java. See also [geotools](http://www.geotools.org) that uses JTS Topology Suite.
 * [Hibernate Spatial](http://www.hibernatespatial.org) - Spatial extension to Hibernate

 