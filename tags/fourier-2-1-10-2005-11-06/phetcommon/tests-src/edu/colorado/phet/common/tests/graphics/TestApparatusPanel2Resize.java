package edu.colorado.phet.common.tests.graphics;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        BaseModel model = new BaseModel();
        SwingTimerClock clock = new SwingTimerClock( 1, 30 );
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

        PhetTextGraphic textGraphic = new PhetTextGraphic( panel, new Font( "Lucida Sans", 0, 24 ), "Hello", Color.black, 0, 0 );
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
        clock.addClockTickListener( model );
        clock.start();
    }
}
