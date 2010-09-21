package edu.colorado.phet.website.util;

import org.hibernate.Session;

public interface HibernateTask {
    public boolean run( Session session );
}
