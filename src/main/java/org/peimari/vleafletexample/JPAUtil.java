package org.peimari.vleafletexample;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.peimari.vleafletexample.domain.EventWithPoint;
import org.peimari.vleafletexample.domain.EventWithRoute;
import org.peimari.vleafletexample.domain.SpatialEvent;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class JPAUtil {

	private static final EntityManagerFactory emFactory;

	static {
		try {
			emFactory = Persistence
					.createEntityManagerFactory("org.peimari.vleafletexample");
		} catch (Throwable ex) {
			System.err.println("Cannot create EntityManagerFactory.");
			throw new ExceptionInInitializerError(ex);
		}

		/* Add some example data when jvm starts */
		EntityManager em = createEntityManager();
		em.getTransaction().begin();

		EventWithPoint theEvent = new EventWithPoint();
		theEvent.setTitle("Example event");
		theEvent.setDate(new Date());
		GeometryFactory factory = new GeometryFactory();
		theEvent.setLocation(factory.createPoint(new Coordinate(23, 61)));
		em.persist(theEvent);

		EventWithRoute eventWithPath = new EventWithRoute();
		Coordinate[] coords = new Coordinate[]{new Coordinate(22, 60),
			new Coordinate(23, 61), new Coordinate(22, 63)};
		eventWithPath.setRoute(factory.createLineString(coords));
		eventWithPath.setDate(new Date());
		eventWithPath.setTitle("MTB cup 1/10");
		em.persist(eventWithPath);

		em.getTransaction().commit();
		em.close();

	}

	public static EntityManager createEntityManager() {
		return emFactory.createEntityManager();
	}

	public static void close() {
		emFactory.close();
	}

	/* Static helper methods that use entity manager from active GisUI */
	public static void saveOrPersist(SpatialEvent entity) {
		EntityManager em = GisUI.get().getEntityManager();
		/* Save or persist the edited JPA entity */
		em.getTransaction().begin();
		if (entity.getId() == null) {
			em.persist(entity);
		} else {
			em.merge(entity);
		}
		em.getTransaction().commit();
	}

	public static void refresh(Object entity) {
		GisUI.get().getEntityManager().refresh(entity);
	}

	public static List<SpatialEvent> listAllSpatialEvents() {
		EntityManager em = GisUI.get().getEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SpatialEvent> cq = cb.createQuery(SpatialEvent.class);
		Root<SpatialEvent> pet = cq.from(SpatialEvent.class);
		cq.select(pet);
		TypedQuery<SpatialEvent> q = em.createQuery(cq);
		List<SpatialEvent> events = q.getResultList();
		return events;
	}

	public static void remove(Object spatialEvent) {
		EntityManager em = GisUI.get().getEntityManager();
		em.getTransaction().begin();
		try {
			em.remove(spatialEvent);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw new RuntimeException(e);
		}
	}

}
