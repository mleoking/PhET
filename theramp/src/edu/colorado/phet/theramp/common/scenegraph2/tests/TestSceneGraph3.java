/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph2.tests;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.theramp.common.scenegraph2.*;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jun 2, 2005
 * Time: 3:33:20 AM
 * Copyright (c) Jun 2, 2005 by Sam Reid
 */

public class TestSceneGraph3 {

    public static void main( String[] args ) {
        final SceneGraphPanel sceneGraphPanel = new SceneGraphPanel();
        sceneGraphPanel.getGraphic().setName( "root" );
        sceneGraphPanel.getGraphic().setAntialias( true );

        AbstractGraphic rect = new TextGraphic( "MYTEXT!" );
//        AbstractGraphic rect = new FillGraphic( new Rectangle( 0, 0, 100, 100 ) );
        rect.setName( "Rectangle!" );
        rect.setCursorHand();
        rect.addMouseListener( new Translator() );
        rect.addMouseListener( new Rotator() );
        sceneGraphPanel.addGraphic( rect );

        JFrame frame = new JFrame( "Test" );

        frame.setContentPane( sceneGraphPanel );
        frame.setSize( 800, 800 );
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        SwingTimerClock clock = new SwingTimerClock( 1, 30 );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                DirtyRegionSet regions = sceneGraphPanel.collectDirtyRegions();
                sceneGraphPanel.repaintDirtyRegions( regions );
                sceneGraphPanel.repaint();
            }
        } );
        clock.start();
    }

}
