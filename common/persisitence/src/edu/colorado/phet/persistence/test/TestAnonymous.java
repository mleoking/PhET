/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.persistence.test;

import edu.colorado.phet.common.util.persistence.PersistentPoint2D;

import java.util.ArrayList;
import java.util.EventListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.beans.XMLEncoder;
import java.io.Serializable;

/**
 * TestAnonymous
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestAnonymous implements Serializable {

    ArrayList listeners;
    String foo = "FOO";
    private static ActionListener lala;

    public TestAnonymous( ) {
        this.listeners = new ArrayList();
        listeners.add( "ASDDFASDFASD");
    }

    public void addListener( EventListener l ) {
        listeners.add( l );
    }

    public ArrayList getListeners() {
        return listeners;
    }

    public void setListeners( ArrayList listeners ) {
        this.listeners = listeners;
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo( String foo ) {
        this.foo = foo;
    }
    public class MyListener implements ActionListener{
//        TestAnonymous ta;

        public MyListener( TestAnonymous ta ) {
//            this.ta = ta;
        }

        public MyListener() {
        }

        public void actionPerformed( ActionEvent e ) {

//            System.out.println( "foo = " + ta.foo );
        }

//        public TestAnonymous getTa() {
//            return ta;
//        }
//
//        public void setTa( TestAnonymous ta ) {
//            this.ta = ta;
//        }
    }
    public static void main( String[] args ) {
        TestAnonymous ta = new TestAnonymous( );
//        lala = new ActionListener() {
//                    public void actionPerformed( ActionEvent e ) {
//                        System.out.println( "TestAnonymous.actionPerformed" );
//                    }
//                };
//        ta.addListener( lala);
        ta.addtest();
        ta.setFoo( "BAR" );
//        PersistentPoint2D p = new PersistentPoint2D( new Point2D.Double( 10, 20 ));
        XMLEncoder e = new XMLEncoder( System.out );
        e.writeObject( ta );
        e.close();
    }

    private void addtest() {
        addListener( new MyListener() );

    }
}
