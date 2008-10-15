/**
 * Class: TestPhetApplication
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: May 12, 2004
 */
package edu.colorado.phet.common.phetgraphics.test.examples;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.help.HelpItem;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetcommon.application.DeprecatedPhetApplicationLauncher;

public class TestPhetApplication {
    static class MyModule extends PhetGraphicsModule {

        public MyModule( String name, IClock clock, Color color ) {
            super( name, clock );

//            final ControlPanel controlPanel = new ControlPanel( this );
//            setControlPanel( controlPanel );

            setApparatusPanel( new ApparatusPanel() );
            setModel( new BaseModel() );
            JTextArea ctrl = new JTextArea( 5, 20 );
            getApparatusPanel().addGraphic( new PhetShapeGraphic( getApparatusPanel(), new Rectangle( 200, 100, 300, 100 ), color ) );

            final ControlPanel controlPanel = new ControlPanel( this );
            setControlPanel( controlPanel );

            addHelpItem( new HelpItem( getApparatusPanel(), "HELP!!!", 300, 200 ) );

            controlPanel.addControl( ctrl );
            final JButton button1 = new JButton( "YO!" );
            controlPanel.addControl( button1 );
            JButton button2 = new JButton( "Y'ALL!" );
            controlPanel.addControlFullWidth( button2 );
            button2.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    controlPanel.removeControl( button1 );
                }
            } );
        }

        public void activate() {
            super.activate();
        }
    }

    static class MyModule2 extends PhetGraphicsModule {

        public MyModule2( String name, IClock clock, Color color ) {
            super( name, clock );
            setApparatusPanel( new ApparatusPanel() );
            setModel( new BaseModel() );
            JButton ctrl = new JButton( "Click Me" );
            ControlPanel controls = new ControlPanel( this );
            controls.addControl( ctrl );
            getApparatusPanel().addGraphic( new PhetShapeGraphic( getApparatusPanel(), new Rectangle( 200, 100, 300, 100 ), color ) );
            setControlPanel( controls );
            JPanel monitorPanel = new JPanel();
            monitorPanel.add( new JCheckBox( "yes/no" ) );
            setMonitorPanel( monitorPanel );
        }

        public void activate() {
            super.activate();
        }
    }

    static class Photon extends SimpleObservable implements ModelElement {
        double x;
        double y;
        double speed = 1.0;
        Random rand = new Random();

        public Photon( double x, double y ) {
            this.x = x;
            this.y = y;
        }

        public void stepInTime( double dt ) {
            //            x += ( rand.nextDouble() - .5 ) * 5;
            //            y += ( rand.nextDouble() - .5 ) * 5;
            x = x + speed * dt;
            if ( x > 600 ) {
                x = 0;
            }
//                    x = ++x % 600;
            //            if( x > 100 ) {
            //                x = 100;
            //            }
            if ( y > 100 ) {
                y = 100;
            }
            if ( x < 0 ) {
                x = 0;
            }
            if ( y < 0 ) {
                y = 0;
            }
            notifyObservers();

        }
    }

    static class PhotonGraphic extends PhetGraphic {
        private Photon ph;

        public PhotonGraphic( ApparatusPanel ap, Photon ph ) {
            super( ap );
            this.ph = ph;
        }

        public void paint( Graphics2D g ) {
            g.setColor( Color.blue );
            g.fillRect( (int) ph.x, (int) ph.y, 2, 2 );
        }

        protected Rectangle determineBounds() {
            return new Rectangle( (int) ph.x, (int) ph.y, 2, 2 );
        }
    }

    static class MyModule3 extends PhetGraphicsModule {
        public MyModule3( IClock clock ) {
            super( "Test Module", clock );
            setApparatusPanel( new ApparatusPanel() );
            setModel( new BaseModel() );

            Photon ph = new Photon( 100, 100 );
            addModelElement( ph );

            PhetGraphic g = new PhotonGraphic( getApparatusPanel(), ph );
            addGraphic( g, 0 );

            ph.addObserver( new SimpleObserver() {
                public void update() {
                    getApparatusPanel().repaint();
                }
            } );
        }

    }

    public static void main( String[] args ) {
        SwingClock clock = new SwingClock( 30, 1.0 );
        PhetGraphicsModule module = new MyModule( "Testing", clock, Color.blue );
        PhetGraphicsModule module2 = new MyModule( "1ntht", clock, Color.red );
        PhetGraphicsModule module3 = new MyModule2( "Button", clock, Color.red );

        MyModule3 modulePhotons = new MyModule3( clock );
        PhetGraphicsModule[] m = new PhetGraphicsModule[]{module, module2, module3, modulePhotons};

        DeprecatedPhetApplicationLauncher app = new DeprecatedPhetApplicationLauncher( args, "title", "description", "version" );
        for ( int i = 0; i < m.length; i++ ) {
            app.addModule( m[i] );
        }
        app.startApplication();
    }

}
