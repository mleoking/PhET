/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A slider that we can constrain values of, and that will scale properly.
 */

public class SliderPhetGraphic extends GraphicLayerSet {
    private ThumbGraphic thumbGraphic;
    private PhetGraphic trackGraphic;
    private double min;
    private double max;
    private double value;
    private ModelViewTransform1D transform;//model values to pixel values.
    private Rectangle rect;
    private BackgroundGraphic backgroundGraphic;
    private ArrayList changeListeners = new ArrayList();
    private TickSetGraphic tickSetGraphic;
    private int trackWidth = 6;
    private int numTicks;

    public SliderPhetGraphic( Component component, double min, double max, double value ) {
        super( component );
        this.rect = new Rectangle( 0, 0, 200, 50 );
        this.numTicks = 6;
        this.min = min;
        this.max = max;
        this.value = value;
        this.trackGraphic = new TrackGraphic( component );
        this.thumbGraphic = new ThumbGraphic( component );
        this.backgroundGraphic = new BackgroundGraphic( component );
        addGraphic( backgroundGraphic );
        addGraphic( trackGraphic );
        addGraphic( thumbGraphic );
        transform = new ModelViewTransform1D( min, max, rect.x, rect.x + rect.width );
        tickSetGraphic = new TickSetGraphic( component );
        addGraphic( tickSetGraphic );
    }

    public class TickSetGraphic extends CompositePhetGraphic {

        public TickSetGraphic( Component component ) {
            super( component );

            double dx = ( max - min ) / ( numTicks - 1 );
            for( int i = 0; i < numTicks; i++ ) {
                double value = min + dx * i;
                TickGraphic tickGraphic = new TickGraphic( component, value );
                addGraphic( tickGraphic );
                int x = transform.modelToView( value );
                tickGraphic.setCenterX( x );
            }
        }
    }

    public class TickGraphic extends CompositePhetGraphic {
        private PhetTextGraphic textGraphic;
        private PhetShapeGraphic tickGraphic;

        public TickGraphic( Component component, double value ) {

            super( component );
            DecimalFormat df = new DecimalFormat( "#.#" );
            textGraphic = new PhetTextGraphic( component, new Font( "Lucida Sans", 0, 12 ), df.format( value ), Color.black, 0, 2 );

            Shape tick = new Line2D.Double( textGraphic.getWidth() / 2, 0, textGraphic.getWidth() / 2, -trackWidth );
            tickGraphic = new PhetShapeGraphic( component, tick, new BasicStroke( 3 ), Color.black );
            addGraphic( textGraphic );
            addGraphic( tickGraphic );
        }

        public void setCenterX( int x ) {
            setLocation( x - textGraphic.getWidth() / 2, (int)( rect.getHeight() / 2 - 2 ) );
        }
    }

    private void fireChangeEvent() {
        ChangeEvent ce = new ChangeEvent( this );
        for( int i = 0; i < changeListeners.size(); i++ ) {
            ChangeListener changeListener = (ChangeListener)changeListeners.get( i );
            changeListener.stateChanged( ce );
        }
    }

    public void addChangeListener( ChangeListener changeListener ) {
        changeListeners.add( changeListener );
    }

    public double getValue() {
        return transform.viewToModel( thumbGraphic.getCenterX() );
    }

    public class BackgroundGraphic extends CompositePhetGraphic {
        private PhetShapeGraphic shape;

        public BackgroundGraphic( Component component ) {
            super( component );
            shape = new PhetShapeGraphic( component, rect, Color.white, new BasicStroke( 1.0f ), Color.gray );
            addGraphic( shape );
            addTranslationListener( new TranslationListener() {
                public void translationOccurred( TranslationEvent translationEvent ) {
                    MouseEvent me = translationEvent.getMouseEvent();
                    SliderPhetGraphic.this.setLocation( me.getX(), me.getY() );
                }
            } );
        }
    }

    public class ThumbGraphic extends CompositePhetGraphic {
        private PhetGraphic thumbIcon;
        private AffineTransformBuilder transformBuilder;

        public ThumbGraphic( Component component ) {
            super( component );
            thumbIcon = new PhetImageGraphic( component, "images/arrow-down.png" );

            transformBuilder = new AffineTransformBuilder( thumbIcon.getBounds() );
            addGraphic( thumbIcon );
            addTranslationListener( new TranslationListener() {
                public void translationOccurred( TranslationEvent translationEvent ) {
                    double oldValue = getValue();
                    MouseEvent me = translationEvent.getMouseEvent();
                    AffineTransform net = getNetTransform();
                    Point2D dst = new Point2D.Double();
                    try {
                        net.inverseTransform( me.getPoint(), dst );
                    }
                    catch( NoninvertibleTransformException e ) {
                        e.printStackTrace();
                    }
                    double dest = dst.getX();

                    if( dest > rect.x + rect.width ) {
                        dest = rect.x + rect.width;
                    }
                    else if( dest < rect.x ) {
                        dest = rect.x;
                    }
                    transformBuilder.setX( dest - transformBuilder.getOrigRect().width / 2 );
                    thumbIcon.setTransform( transformBuilder.toAffineTransform() );
                    if( getValue() != oldValue ) {
                        fireChangeEvent();
                    }
                }
            } );
            setCursorHand();
        }

        public int getCenterX() {
            return (int)( transformBuilder.getX() + transformBuilder.getOrigRect().width / 2 );
        }
    }

    public class TrackGraphic extends CompositePhetGraphic {
        private PhetShapeGraphic shape;

        public TrackGraphic( Component component ) {
            super( component );
            int strokeSize = trackWidth;
            Stroke stroke = new BasicStroke( strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1 );
            Line2D.Double line = new Line2D.Double( rect.x + strokeSize / 2, rect.y + rect.height / 2, rect.x + rect.width - strokeSize / 2, rect.y + rect.height / 2 );
            Shape track = stroke.createStrokedShape( line );
            Stroke outline = new BasicStroke( 1 );
            shape = new PhetShapeGraphic( component, track, outline, Color.black );
            addGraphic( shape );
        }
    }
}
