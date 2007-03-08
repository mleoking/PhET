/**
 * Class: TestClock
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: Dec 30, 2003
 */
package edu.colorado.phet.common.examples;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.ThreadedClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.plaf.PlafUtil;
import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Test comment.  Another comment. oudia..
 */
public class TestClock extends PhetApplication {

    public TestClock( ApplicationModel descriptor, Module m, AbstractClock clock ) {
        super( descriptor );
    }

    static class MyModule extends Module {
        protected MyModule( String name, AbstractClock clock ) {
            super( "Test" );
            setApparatusPanel( new ApparatusPanel() );
            setModel( new BaseModel() );
            TestParticle p = new TestParticle( 100, 200 );
            super.add( p, new ParticleGraphic( p ), 10 );
        }

        public void addModelElement( ModelElement modelElement ) {
            super.addModelElement( modelElement );
        }
    }

    static class TestParticle extends Observable implements ModelElement {
        double x;
        double y;

        TestParticle( double x, double y ) {
            this.x = x;
            this.y = y;
        }

        public void stepInTime( double dt ) {
            x = ( x + 10 ) % 100;
            y = ( y + 10 ) % 100;
            setChanged();
            notifyObservers();
        }
    }

    static class ParticleGraphic implements Graphic, Observer {
        private TestParticle p;

        public ParticleGraphic( TestParticle p ) {
            this.p = p;
        }

        public void update( Observable o, Object arg ) {

        }

        public void paint( Graphics2D g ) {
            g.fillRect( (int)p.x, (int)p.y, 10, 10 );
        }
    }


    public static void main( String[] args ) {
        AbstractClock clock = new ThreadedClock( 10, 20, false );
        final MyModule m = new MyModule( "asdf", clock );
        ApplicationModel ad = new ApplicationModel( "appname", "mydescritpion",
                                                    "myversion", new FrameSetup.CenteredWithSize( 400, 400 ) );
        TestClock tc = new TestClock( ad, m, clock );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( AbstractClock c, double dt ) {
                m.getApparatusPanel().repaint();
            }
        } );
        tc.startApplication();
        JFrame frame = tc.getApplicationView().getPhetFrame();

        JMenu menu = new JMenu( "Options" );
        //        JMenuItem plaf=new JMenuItem( );
        JMenuItem[] it = PlafUtil.getLookAndFeelItems();
        for( int i = 0; i < it.length; i++ ) {
            JMenuItem jMenuItem = it[i];
            menu.add( jMenuItem );
        }
        frame.getJMenuBar().add( menu );
        frame.getJMenuBar().validate();

    }
}
