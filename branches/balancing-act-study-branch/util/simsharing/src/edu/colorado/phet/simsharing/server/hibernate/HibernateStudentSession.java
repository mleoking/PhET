//// Copyright 2002-2011, University of Colorado
//package edu.colorado.phet.simsharing.server.hibernate;
//
//import java.util.Date;
//
//import edu.colorado.phet.simsharing.messages.SessionID;
//
///**
// * @author Sam Reid
// */
//public class HibernateStudentSession {
//    String name;
//    String sim;
//    long index;
//    Date time;
//
//    public Date getTime() {
//        return time;
//    }
//
//    public void setTime( Date time ) {
//        this.time = time;
//    }
//
//    public HibernateStudentSession() {
//    }
//
//    public HibernateStudentSession( SessionID sessionID ) {
//        this.name = sessionID.getName();
//        this.sim = sessionID.sim;
//        this.index = sessionID.getIndex();
//        time = new Date();
//    }
//
//    public SessionID getSessionID() {
//        return new SessionID( (int) index, name, sim );
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName( String name ) {
//        this.name = name;
//    }
//
//    public String getSim() {
//        return sim;
//    }
//
//    public void setSim( String sim ) {
//        this.sim = sim;
//    }
//
//    public long getIndex() {
//        return index;
//    }
//
//    public void setIndex( long index ) {
//        this.index = index;
//    }
//}
