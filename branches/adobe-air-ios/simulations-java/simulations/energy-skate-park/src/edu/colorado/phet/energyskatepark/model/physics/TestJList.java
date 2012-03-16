// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model.physics;

import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.energyskatepark.util.EnergySkateParkLogging;

/**
 * User: Sam Reid
 * Date: Mar 2, 2007
 * Time: 2:26:20 PM
 */

public class TestJList extends JList {
    final DefaultTestSet defaultTestSet;

    public TestJList( final DefaultTestSet defaultTestSet, final TestPhysics1D testPhysics1D ) {
        this.defaultTestSet = defaultTestSet;
        Vector items = new Vector();
        for ( int i = 0; i < defaultTestSet.getTestCount(); i++ ) {
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
}
