/**
 * Class: TestPhetApplication
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: May 12, 2004
 */
package edu.colorado.phet.common.examples;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.clockgui.ClockParamSetterPanel;
import edu.colorado.phet.common.view.help.HelpItem;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class TestPhetApplication {
    static class MyModule extends Module {

        public MyModule( String name, AbstractClock clock, Color color ) {
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

            controlPanel.add( ctrl );
            final JButton button1 = new JButton( "YO!" );
            controlPanel.add( button1 );
            JButton button2 = new JButton( "Y'ALL!" );
            controlPanel.addFullWidth( button2 );
            button2.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    controlPanel.remove( button1 );
                }
            } );
        }

        public void activate( PhetApplication app ) {
            super.activate( app );
        }
    }

    static class MyModule2 extends Module {

        public MyModule2( String name, AbstractClock clock, Color color ) {
            super( name, clock );
            setApparatusPanel( new ApparatusPanel() );
            setModel( new BaseModel() );
            JButton ctrl = new JButton( "Click Me" );
            JPanel controls = new JPanel();
            controls.add( ctrl );
            getApparatusPanel().addGraphic( new PhetShapeGraphic( getApparatusPanel(), new Rectangle( 200, 100, 300, 100 ), color ) );
            setControlPanel( controls );
            JPanel monitorPanel = new JPanel();
            monitorPanel.add( new JCheckBox( "yes/no" ) );
            setMonitorPanel( monitorPanel );


        }

        public void activate( PhetApplication app ) {
            super.activate( app );
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
            if( x > 600 ) {
                x = 0;
            }
//                    x = ++x % 600;
            //            if( x > 100 ) {
            //                x = 100;
            //            }
            if( y > 100 ) {
                y = 100;
            }
            if( x < 0 ) {
                x = 0;
            }
            if( y < 0 ) {
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
            g.fillRect( (int)ph.x, (int)ph.y, 2, 2 );
        }

        protected Rectangle determineBounds() {
            return new Rectangle( (int)ph.x, (int)ph.y, 2, 2 );
        }
    }

    static class MyModule3 extends Module {
        public MyModule3( AbstractClock clock ) {
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

        SwingTimerClock clock = new SwingTimerClock( 1, 30, true );
        Module module = new MyModule( "Testing", clock, Color.blue );
        Module module2 = new MyModule( "1ntht", clock, Color.red );
        Module module3 = new MyModule2( "Button", clock, Color.red );

        MyModule3 modulePhotons = new MyModule3( clock );


        Module[] m = new Module[]{module, module2, module3, modulePhotons};

        ApplicationModel applicationModel = new ApplicationModel( "Test app", "My Test", ".10" );
        applicationModel.setName( "phetcommon" );
        applicationModel.setClock( clock );
        applicationModel.setModules( m );
        applicationModel.setInitialModule( module );
        applicationModel.setUseClockControlPanel( true );

        PhetApplication app = new PhetApplication( applicationModel, args );
        app.startApplication();

        ClockParamSetterPanel ccp = new ClockParamSetterPanel( clock );
        JFrame jeff = new JFrame( "CCP" );
        jeff.setContentPane( ccp );
        jeff.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jeff.pack();
        jeff.show();
    }

}
