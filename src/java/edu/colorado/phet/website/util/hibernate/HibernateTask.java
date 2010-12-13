package edu.colorado.phet.website.util.hibernate;

import org.hibernate.Session;

public interface HibernateTask {
    public boolean run( Session session );
}
