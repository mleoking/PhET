package edu.colorado.phet.wickettest.util;

import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.hibernate.Session;

public class PhetRequestCycle extends WebRequestCycle {

    private Session session;
    private Long start;

    public PhetRequestCycle( WebApplication webApplication, WebRequest webRequest, Response response ) {
        super( webApplication, webRequest, response );
    }

    @Override
    protected void onBeginRequest() {
//        System.out.println( "onBeginRequest" );
        start = System.currentTimeMillis();
        System.out.println( "----------" );
        session = HibernateUtils.getInstance().openSession();
        super.onBeginRequest();
    }

    @Override
    protected void onEndRequest() {
//        System.out.println( "onEndRequest" );
        System.out.println( "Request cycle: " + ( System.currentTimeMillis() - start ) + " ms" );
        session.close();
        super.onEndRequest();
    }

    public Session getHibernateSession() {
        return session;
    }
}
