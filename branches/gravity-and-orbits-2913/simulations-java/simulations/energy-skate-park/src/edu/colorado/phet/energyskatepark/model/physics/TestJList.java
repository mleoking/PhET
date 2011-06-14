// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.energyskatepark.util.EnergySkateParkLogging;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.Vector;

/**
 * User: Sam Reid
 * Date: Mar 2, 2007
 * Time: 2:26:20 PM
 */

public class TestJList extends JList {
    DefaultTestSet defaultTestSet;

    public TestJList( final DefaultTestSet defaultTestSet, final TestPhysics1D testPhysics1D ) {
        this.defaultTestSet = defaultTestSet;
        Vector items = new Vector();
        for( int i = 0; i < defaultTestSet.getTestCount(); i++ ) {
            items.add( defaultTestSet.getTest( i ).getName() );
        }
        setListData( items );

        setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                int index = getSelectedIndex();
                EnergySkateParkLogging.println( "index = " + index );
                TestState testState = defaultTestSet.getTest( index );
                testPhysics1D.setTestState( testState );
            }
        } );
    }

//    private static Object[] toList( DefaultTestSet defaultTestSet ) {
//        Object[] items = new Object[defaultTestSet.getTestCount()];
//        for( int i = 0; i < defaultTestSet.getTestCount(); i++ ) {
//            items[i] = defaultTestSet.getTest( i ).getName();
//        }
//        return items;
//    }
}
