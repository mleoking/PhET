package edu.colorado.phet.wickettest.util;

import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.hibernate.Session;

public class PhetRequestCycle extends WebRequestCycle {

    private Session session;

    public PhetRequestCycle( WebApplication webApplication, WebRequest webRequest, Response response ) {
        super( webApplication, webRequest, response );
    }

    @Override
    protected void onBeginRequest() {
        System.out.println( "onBeginRequest" );
        session = HibernateUtils.getInstance().openSession();
        super.onBeginRequest();
    }

    @Override
    protected void onEndRequest() {
        System.out.println( "onEndRequest" );
        session.close();
        super.onEndRequest();
    }

    public Session getHibernateSession() {
        return session;
    }
}
