/**
 * Class: TestPhetApplication
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: May 12, 2004
 */
package edu.colorado.phet.common_cck.examples;

import edu.colorado.phet.common_cck.application.ApplicationModel;
import edu.colorado.phet.common_cck.application.Module;
import edu.colorado.phet.common_cck.application.PhetApplication;
import edu.colorado.phet.common_cck.model.BaseModel;
import edu.colorado.phet.common_cck.model.ModelElement;
import edu.colorado.phet.common_cck.model.clock.AbstractClock;
import edu.colorado.phet.common_cck.model.clock.SwingTimerClock;
import edu.colorado.phet.common_cck.util.SimpleObservable;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.graphics.Graphic;
import edu.colorado.phet.common_cck.view.graphics.ShapeGraphic;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class TestPhetApplication {
    static class MyModule extends Module {

        public MyModule( String name, AbstractClock clock, Color color ) {
            super( name );
            setApparatusPanel( new ApparatusPanel() );
            setModel( new BaseModel() );
            JTextArea ctrl = new JTextArea( 20, 20 );
            JPanel controls = new JPanel();
            controls.add( ctrl );
            getApparatusPanel().addGraphic( new ShapeGraphic( new Rectangle( 200, 100, 300, 100 ), color ) );
            setControlPanel( controls );
        }

        public void activate( PhetApplication app ) {
            super.activate( app );
        }
    }

    static class MyModule2 extends Module {

        public MyModule2( String name, AbstractClock clock, Color color ) {
            super( name );
            setApparatusPanel( new ApparatusPanel() );
            setModel( new BaseModel() );
            JButton ctrl = new JButton( "Click Me" );
            JPanel controls = new JPanel();
            controls.add( ctrl );
            getApparatusPanel().addGraphic( new ShapeGraphic( new Rectangle( 200, 100, 300, 100 ), color ) );
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

        Random rand = new Random();

        public Photon( double x, double y ) {
            this.x = x;
            this.y = y;
        }

        public void stepInTime( double dt ) {
            //            x += ( rand.nextDouble() - .5 ) * 5;
            //            y += ( rand.nextDouble() - .5 ) * 5;
            x = ++x % 600;
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

    static class PhotonGraphic implements Graphic {
        private Photon ph;

        public PhotonGraphic( Photon ph ) {
            this.ph = ph;
        }

        public void paint( Graphics2D g ) {
            g.setColor( Color.blue );
            g.fillRect( (int)ph.x, (int)ph.y, 2, 2 );
        }

    }

    static class MyModule3 extends Module {
        public MyModule3() {
            super( "Test Module" );
            setApparatusPanel( new ApparatusPanel() );
            setModel( new BaseModel() );

            Photon ph = new Photon( 100, 100 );
            addModelElement( ph );

            Graphic g = new PhotonGraphic( ph );
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

        MyModule3 modulePhotons = new MyModule3();


        Module[] m = new Module[]{module, module2, module3, modulePhotons};

        ApplicationModel applicationModel = new ApplicationModel( "Test app", "My Test", ".10" );
        applicationModel.setName( "apptest" );
        applicationModel.setClock( clock );
        applicationModel.setModules( m );
        applicationModel.setInitialModule( modulePhotons );
        applicationModel.setUseClockControlPanel( false );

        PhetApplication app = new PhetApplication( applicationModel );
        app.startApplication();
    }

}
