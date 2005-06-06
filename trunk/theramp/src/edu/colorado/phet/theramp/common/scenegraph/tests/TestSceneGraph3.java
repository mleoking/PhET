/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph.tests;

import edu.colorado.phet.theramp.common.scenegraph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

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

        AbstractGraphic userRoot = new TextGraphic( "MYTEXT!" );
        userRoot.setName( "MyText!" );
        userRoot.setFontLucidaSansBold( 32 );
        userRoot.setCursorHand();
        userRoot.addMouseListener( new Translator() );
        userRoot.addMouseListener( new Rotator() );
        sceneGraphPanel.addGraphic( userRoot );

        sceneGraphPanel.addMouseMotionListener( new MouseMotionAdapter() {
            // implements java.awt.event.MouseMotionListener
            public void mouseDragged( MouseEvent e ) {
                if( e.isShiftDown() ) {
                    sceneGraphPanel.getGraphic().rotate( Math.PI / 32, sceneGraphPanel.getWidth() / 2, sceneGraphPanel.getHeight() / 2 );
                }
            }
        } );

        JFrame frame = new JFrame( "Test" );

        SceneGraphJComponent.init( frame );
        JButton jButton = new JButton( "text" );
        AbstractGraphic sceneGraphJComponent = SceneGraphJComponent.newInstance( jButton );
        sceneGraphPanel.addGraphic( sceneGraphJComponent );
//        sceneGraphJComponent.addMouseListener( new Repaint() );
        sceneGraphJComponent.translate( 400, 400 );
        sceneGraphJComponent.setAntialias( true );
        sceneGraphJComponent.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        sceneGraphJComponent.setCursorHand();
        sceneGraphJComponent.addMouseListener( new Translator() );
        sceneGraphJComponent.addMouseListener( new Rotator() );

        frame.setContentPane( sceneGraphPanel );
        frame.setSize( 800, 800 );
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

}
