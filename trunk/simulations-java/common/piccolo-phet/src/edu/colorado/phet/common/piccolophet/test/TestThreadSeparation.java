/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14676 $
 * Date modified : $Date:2007-04-17 02:58:50 -0500 (Tue, 17 Apr 2007) $
 */
package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.DeprecatedPhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.*;
import edu.colorado.phet.common.phetcommon.util.ModelEventChannel;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.util.SwingThreadModelListener;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * TestThreadSeparation
 *
 * @author Ron LeMaster
 * @version $Revision:14676 $
 */
public class TestThreadSeparation {

    /**
     * The model. It has an orbiter that travels in a circle, and notifies listeners when the orbiter's
     * position changes
     */
    static class TestModel {
        Clock clock;
        Point2D center = new Point2D.Double( 200, 200 );
        double radius = 50;
        Point2D.Double orbiter = new Point2D.Double( center.getX() + radius, center.getY() );
        ModelEventChannel eventChannel = new ModelEventChannel( TestModel.ChangeListener.class );
        TestModel.ChangeListener listenerProxy = (TestModel.ChangeListener) eventChannel.getListenerProxy();

        public TestModel( IClock clock ) {
            clock.addClockListener( new ClockAdapter() {
                public void clockTicked( ClockEvent clockEvent ) {

                    double t = clockEvent.getSimulationTime();
                    orbiter.setLocation( center.getX() + radius * Math.cos( t * 0.05 ),
                                         center.getY() + radius * Math.sin( t * 0.05 ) );
                    listenerProxy.stateChanged( orbiter );
                }
            } );
        }

        public void addListener( TestModel.ChangeListener listener ) {
            eventChannel.addListener( listener );
        }

        public void setRadius( double radius ) {
            this.radius = radius;
        }

        /**
         * An interface for ChangeListeners on the TestModel
         */
        public interface ChangeListener extends EventListener {
            void stateChanged( Point2D.Double location );
        }
    }

    /**
     * The only module
     */
    static class TestModule extends PiccoloModule {
        public TestModule() {
            super( "Module A", new ModelClock( 40, 10 ) );

            TestModel model = new TestModel( getClock() );

            TestCanvas canvas = new TestCanvas( model );
            setSimulationPanel( canvas );

            setControlPanel( new ControlPanel( model, (ModelClock) getClock() ) );
        }
    }

    /**
     * A panel with a JSlider that controls the radius of the model's orbiter
     */
    static class ControlPanel extends JPanel {
        public ControlPanel( final TestModel model, final ModelClock clock ) {
            final JSlider radiusSlider = new JSlider( 20, 100, 50 );
            radiusSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    PhetUtilities.invokeLater( new Runnable() {
                        public void run() {
                            model.setRadius( radiusSlider.getValue() );
                        }
                    } );
                }
            } );
            add( radiusSlider );
        }
    }

    /**
     * The drawing canvas. It listens for changes in the orbiter, and updates the position of its
     * representation in the view when they occur. By implementing the marker interface
     * SwingThreadModelListener, we are guaranteed to get events in the Swing dispatch thread
     */
    static class TestCanvas extends PhetPCanvas implements SwingThreadModelListener, TestModel.ChangeListener {
        private Ellipse2D orbiterRep = new Ellipse2D.Double();

        public TestCanvas( TestModel model ) {
            model.addListener( this );
        }

        /**
         * Draws a graphic representation of the orbiter
         *
         * @param g
         */
        public void paint( Graphics g ) {
            super.paint( g );
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor( Color.red );
            g2.fill( orbiterRep );
        }

        /**
         * Update the position of the orbiter's representation in the view, then repaint
         *
         * @param position
         */
        public void stateChanged( Point2D.Double position ) {
            orbiterRep.setFrame( position, new Dimension( 10, 10 ) );
            TestCanvas.this.invalidate();
            TestCanvas.this.repaint();
        }
    }

    /**
     * Test driver
     *
     * @param args
     */
    public static void main( String[] args ) {

        DeprecatedPhetApplicationLauncher app = new DeprecatedPhetApplicationLauncher( args, "Thread Separation Test", "", "",
                                                                       new FrameSetup.CenteredWithSize( 600, 500 ) );
        app.addModule( new TestModule() );
        app.startApplication();
    }

}
