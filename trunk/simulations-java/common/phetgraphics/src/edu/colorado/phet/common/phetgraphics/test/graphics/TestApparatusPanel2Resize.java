// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetgraphics.test.graphics;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 30, 2004
 * Time: 11:18:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestApparatusPanel2Resize {
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test AP2" );
        final BaseModel model = new BaseModel();
        SwingClock clock = new SwingClock( 30, 1.0 );
        ApparatusPanel2 panel = new ApparatusPanel2( clock );
        JLabel comp = new JLabel( "Label" );
        comp.reshape( 250, 250, comp.getPreferredSize().width, comp.getPreferredSize().height );
        panel.add( comp );
        frame.setContentPane( panel );
        frame.setSize( 600, 600 );
        frame.show();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        Rectangle rectangle = new Rectangle( 0, 0, 200, 200 );
        final PhetShapeGraphic phetShapeGraphic = new PhetShapeGraphic( panel, rectangle, Color.blue );
        phetShapeGraphic.setLocation( 50, 50 );

        panel.addGraphic( phetShapeGraphic );
        phetShapeGraphic.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                phetShapeGraphic.setLocation( translationEvent.getX(), translationEvent.getY() );
            }
        } );

        PhetTextGraphic textGraphic = new PhetTextGraphic( panel, new PhetFont( 0, 24 ), "Hello", Color.black, 0, 0 );
        textGraphic.setLocation( 400, 100 );
        panel.addGraphic( textGraphic );

        JButton button = new JButton( "Test button" );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "Pressed" );
            }
        } );
        panel.setLayout( null );

        button.reshape( 100, 100, button.getPreferredSize().width, button.getPreferredSize().height );
        panel.add( button );


        phetShapeGraphic.setCursorHand();
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                model.update( clockEvent );
            }
        } );
        clock.start();
    }
}
