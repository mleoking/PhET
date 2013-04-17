// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forces1d.common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.forces1d.view.Arrow;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 28, 2004
 * Time: 7:56:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class WiggleMe extends CompositePhetGraphic {
    private long t0 = System.currentTimeMillis();
    //    private double frequency = 0.0025;//in Hertz.
    private double frequency = 2.5;//in Hertz.
    private double amplitude = 30;//In pixels.
    private MutableVector2D oscillationAxis;//axis along which the wiggle me moves.
    private Target target;
    private PhetShapeGraphic phetShapeGraphic;
    private ShadowHTMLGraphic textGraphic;
    private ClockListener tickListener;
    private IClock clock;

    public WiggleMe( Component component, IClock clock, String text, PhetGraphic phetGraphic ) {
        this( component, clock, text, new PhetGraphicTarget( phetGraphic ) );
    }

    public WiggleMe( final Component component, IClock clock, String text, Target t ) {
        this( component, clock, text, t, new Font( PhetFont.getDefaultFontName(), Font.BOLD, 20 ), 1, 1 );
    }

    public WiggleMe( final Component component, IClock clock, String text, Target t, Font font, int dx, int dy ) {
        super( component );
        this.target = t;
        this.clock = clock;
        this.oscillationAxis = new MutableVector2D( 0, 1 );
        Color foreGroundColor = new Color( 39, 27, 184 );
        Color shadowColor = new Color( 6, 0, 44 );
        textGraphic = new ShadowHTMLGraphic( component, text, font, foreGroundColor, dx, dy, shadowColor );
        addGraphic( textGraphic );

        textGraphic.setLocation( 0, 0 );
        tickListener = new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                tick();
            }
        };
        setVisible( true );//attaches as a clock tick listener.
//        clock.addClockListener( tickListener );
        Arrow arrow = new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( 50, 0 ), 20, 20, 10 );
        phetShapeGraphic = new PhetShapeGraphic( component, arrow.getShape(), Color.blue, new BasicStroke( 2 ), Color.black );

        addGraphic( phetShapeGraphic );
//        phetShapeGraphic.setLocation( 0, textGraphic.getHeight() + 5 );
        setRegistrationPoint( getBounds().x, getBounds().y );
        tick();
        setIgnoreMouse( true );
        phetShapeGraphic.setLocation( textGraphic.getWidth() - phetShapeGraphic.getWidth() - 5, textGraphic.getHeight() + 5 );
    }

    public void setOscillationAxis( MutableVector2D oscillationAxis ) {
        this.oscillationAxis = oscillationAxis;
    }

    public void setFrequency( double frequency ) {
        this.frequency = frequency;
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            if ( !clock.containsClockListener( tickListener ) ) {
                clock.addClockListener( tickListener );
            }
        }
        else {
            while ( clock.containsClockListener( tickListener ) ) {
                clock.removeClockListener( tickListener );
            }
        }
        t0 = System.currentTimeMillis();
    }

    public void setAmplitude( double amplitude ) {
        this.amplitude = amplitude;
    }

    public void setFont( Font font ) {
        textGraphic.setFont( font );
    }

    public void setArrow( double dx, double dy ) {
        Arrow arrow = new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( dx, dy ), 20, 20, 10 );
        phetShapeGraphic.setShape( arrow.getShape() );
    }

    private void tick() {
        double time = ( System.currentTimeMillis() - t0 ) / 1000.0;
        double period = 2 * Math.PI / frequency;
        if ( isVisible() && getComponent().isShowing() && time < 3 * period ) {

            Point targetLoc = target.getLocation();

            Point oscillationCenter = new Point( targetLoc.x - getWidth() - 5, targetLoc.y );
            double distAlongAxis = Math.sin( frequency * time ) * amplitude;
            Vector2D norm = oscillationAxis.getInstanceOfMagnitude( distAlongAxis );
            Point2D dest = norm.getDestination( oscillationCenter );

            setLocation( (int) dest.getX(), (int) dest.getY() );

        }
    }

    public void setArrowColor( Color color ) {
        phetShapeGraphic.setColor( color );
    }

    public static interface Target {

        Point getLocation();

        int getHeight();
    }

    public static class PhetGraphicTarget implements Target {
        private PhetGraphic target;

        public PhetGraphicTarget( PhetGraphic t ) {
            this.target = t;
        }

        public Point getLocation() {
            Point targetLoc = target.getLocation();
            return targetLoc;
        }

        public int getHeight() {
            return target.getHeight();
        }
    }

    public static class SwingComponentTarget implements Target {
        JComponent jComponent;

        public SwingComponentTarget( JComponent jComponent ) {
            this.jComponent = jComponent;
        }

        public Point getLocation() {
            return jComponent.getLocation();
        }

        public int getHeight() {
            return jComponent.getHeight();
        }
    }
}
