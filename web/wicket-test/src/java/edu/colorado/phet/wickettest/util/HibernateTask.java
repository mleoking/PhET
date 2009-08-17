package edu.colorado.phet.wickettest.util;

import org.hibernate.Session;

public interface HibernateTask {
    public boolean run( Session session );
}
