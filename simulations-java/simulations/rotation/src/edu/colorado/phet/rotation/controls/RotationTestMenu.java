// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.TestDynamicJFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.TestDynamicJFreeChartNodeTree;
import edu.colorado.phet.common.motion.tests.TestControlGraph;
import edu.colorado.phet.rotation.tests.*;

/**
 * Author: Sam Reid
 * May 10, 2007, 10:34:39 PM
 */
public class RotationTestMenu extends JMenu {
    public RotationTestMenu() {
        super( "Tests" );
        setMnemonic( 't' );

        addTest( "Dynamic Plot", new Runnable() {
            public void run() {
                TestDynamicJFreeChartNode.main( new String[0] );
            }
        } );
        addTest( "Dynamic Plot 2", new Runnable() {
            public void run() {
                TestDynamicJFreeChartNodeTree.main( new String[0] );
            }
        } );
        addTest( "Platform Graphics", new Runnable() {
            public void run() {
                TestRotationPlatformNode.main( new String[0] );
            }
        } );
        addTest( "Graphs", new Runnable() {
            public void run() {
                TestGraphs.main( new String[0] );
            }
        } );

        addTest( "Graph Control", new Runnable() {
            public void run() {
                TestControlGraph.main( new String[0] );
            }
        } );
        addTest( "Graph Selection", new Runnable() {
            public void run() {
                TestGraphSelectionControl.main( new String[0] );
            }
        } );
        addTest( "Graph View", new Runnable() {
            public void run() {
                TestGraphSelectionView.main( new String[0] );
            }
        } );
        addTest( "Vector Control", new Runnable() {
            public void run() {
                TestShowVectorsControl.main( new String[0] );
            }
        } );
        addTest( "Symbol Key", new Runnable() {
            public void run() {
                TestSymbolKey.main( new String[0] );
            }
        } );
    }

    private void addTest( String name, final Runnable runnable ) {
        JMenuItem item = new JMenuItem( name );
        item.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                runnable.run();
            }
        } );
        add( item );
    }
}

