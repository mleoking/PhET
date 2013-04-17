// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetgraphics.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetButton;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;

/**
 * User: Sam Reid
 * Date: Mar 2, 2005
 * Time: 2:26:36 PM
 */

public class EmbeddedControlTest {
    JFrame frame = new JFrame();
    ApparatusPanel apparatusPanel = new ApparatusPanel();

    public EmbeddedControlTest() {
        final GraphicLayerSet root = new GraphicLayerSet( apparatusPanel );
        TranslationListener translationListener = new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                root.translate( translationEvent.getDx(), translationEvent.getDy() );
            }
        };
        BattGraphic battGraphic = new BattGraphic( apparatusPanel, translationListener );
        PhetShapeGraphic background = new PhetShapeGraphic( apparatusPanel,
                                                            RectangleUtils.expand( new Rectangle( battGraphic.getBounds() ), 15, 15 ), Color.green, new BasicStroke( 1 ), Color.black );
        background.addTranslationListener( translationListener );
        root.addGraphic( background );
        root.addGraphic( battGraphic );

        frame.setContentPane( apparatusPanel );
        frame.setSize( 800, 800 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        apparatusPanel.addGraphic( root );
    }

    static class BattGraphic extends GraphicLayerSet {
        public BattGraphic( Component component, TranslationListener dragHandler ) {
            super( component );
            PhetShapeGraphic phetShapeGraphic = new PhetShapeGraphic( component,
                                                                      new Rectangle( 100, 100 ), Color.blue, new BasicStroke( 1 ), Color.black );
            addGraphic( phetShapeGraphic );
            PhetButton phetButton = new PhetButton( component, "Hello" );
            addGraphic( phetButton );
            phetButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    System.out.println( "e = " + e );
                }
            } );
            phetShapeGraphic.addTranslationListener( dragHandler );
        }
    }

    public static void main( String[] args ) {
        new EmbeddedControlTest().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}