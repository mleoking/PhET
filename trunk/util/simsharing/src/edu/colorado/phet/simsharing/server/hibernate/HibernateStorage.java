//// Copyright 2002-2011, University of Colorado
//package edu.colorado.phet.simsharing.server.hibernate;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
//
//import edu.colorado.phet.common.phetcommon.simsharing.SimState;
//import edu.colorado.phet.simsharing.messages.AddSamples;
//import edu.colorado.phet.simsharing.messages.SampleBatch;
//import edu.colorado.phet.simsharing.messages.SessionID;
//import edu.colorado.phet.simsharing.messages.SessionRecord;
//import edu.colorado.phet.simsharing.messages.StudentSummary;
//import edu.colorado.phet.simsharing.server.Storage;
//import edu.colorado.phet.simsharing.teacher.SessionList;
//import edu.colorado.phet.simsharing.teacher.StudentList;
//
//import static edu.colorado.phet.simsharing.server.hibernate.HibernateUtils.resultTransaction;
//import static edu.colorado.phet.simsharing.server.hibernate.HibernateUtils.wrapTransaction;
//
///**
// * @author Sam Reid
// */
//public class HibernateStorage implements Storage {
//    private SessionFactory sessionFactory;
//    private Session session;
//
//    public HibernateStorage() {
//        try {
//            setUp();
//        }
//        catch ( Exception e ) {
//            e.printStackTrace();
//        }
//    }
//
//    protected synchronized void setUp() throws Exception {
//        // A SessionFactory is set up once for an application
//        sessionFactory = new Configuration()
//                .configure() // configures settings from hibernate.cfg.xml
//                .buildSessionFactory();
//
//        session = sessionFactory.openSession();
//    }
//
//    public static void main( String[] args ) throws Exception {
//        new HibernateStorage().run();
//    }
//
//    private synchronized void run() throws Exception {
//        setUp();
//
//        wrapTransaction( session, new HibernateTask() {
//            public boolean run( org.hibernate.Session session ) {
//                session.save( new Event( "Our very first event!", new Date() ) );
//                session.save( new Event( "A follow up event", new Date() ) );
//                return false;
//            }
//        } );
//
//        List events = listEvents();
//        for ( int i = 0; i < events.size(); i++ ) {
//            Event theEvent = (Event) events.get( i );
//            System.out.println( "Event: " + theEvent.getName() + " Time: " + theEvent.getDate() );
//        }
//    }
//
//    private List listEvents() {
//        Result<List> list = resultTransaction( session, new Task<List>() {
//            public List run( Session session ) {
//                return session.createQuery( "from Event" ).list();
//            }
//        } );
//        return list.value;
//    }
//
//    public SimState getSample( SessionID sessionID, int index ) {
//        return null;
//    }
//
//    public int getNumberSamples( SessionID sessionID ) {
//        return 0;
//    }
//
//    public int getNumberSessions() {
//        return 0;
//    }
//
//    public StudentList getActiveStudentList() {
//        return new StudentList( new ArrayList<StudentSummary>() );
//    }
//
//    public SessionList listAllSessions() {
//        return new SessionList( new ArrayList<SessionRecord>() {{
//            wrapTransaction( session, new HibernateTask() {
//                public boolean run( Session session ) {
//                    for ( Object o : session.createQuery( "from HibernateStudentSession" ).list() ) {
//                        HibernateStudentSession s = (HibernateStudentSession) o;
//                        add( new SessionRecord( s.getSessionID(), s.getTime().getTime() ) );
//                    }
//                    return true;
//                }
//            } );
//        }} );
//    }
//
//    public void startSession( final SessionID sessionID ) {
//        wrapTransaction( session, new HibernateTask() {
//            public boolean run( Session session ) {
//                session.save( new HibernateStudentSession( sessionID ) );
//                return true;
//            }
//        } );
//    }
//
//    public void endSession( SessionID sessionID ) {
//    }
//
//    public SampleBatch getSamplesAfter( SessionID id, int index ) {
////        session.createQuery( "from Sample as s select s where s.sessionIndex = :index and s.index > :otherIndex" ).setLong( "index", sessionIndex ).setInteger( "otherIndex", index ).list();
//        return null;
//    }
//
//    public void storeAll( SessionID sessionID, AddSamples data ) {
//        for ( SimState simState : data.data ) {
//            wrapTransaction( session, new HibernateTask() {
//                public boolean run( Session session ) {
//
//                    return false;
//                }
//            } );
//        }
//    }
//
//    public void clear() {
//    }
//}
