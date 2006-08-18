/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.view.phetcomponents;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A slider that we can constrain values of, and that will scale properly.
 * <p/>
 * - where functionality is similar to JSlider, use the same method signature
 * - change the background and knob
 * - optional major/min tick marks
 *
 * @deprecated Use PhetJComponent with a slider.
 */

public class PhetSlider extends GraphicLayerSet {
    private ThumbGraphic thumbGraphic;
    private PhetGraphic trackGraphic;
    private double min;
    private double max;
    private double value = Double.NaN;//to guarantee a set on the init.
    private ModelViewTransform1D transform;//model values to pixel values.
    private Rectangle rect;
    private BackgroundGraphic backgroundGraphic;
    private ArrayList changeListeners = new ArrayList();
    private TickSetGraphic tickSetGraphic;
    private int trackWidth = 6;
    private int numTicks;
    private boolean vertical = false;
    private SliderOrientation orientation;

    public PhetSlider( Component component, double min, double max, double value, boolean vertical ) {
        super( component );

        this.vertical = vertical;

        if( vertical ) {
            orientation = new Vertical();
        }
        else {
            orientation = new Horizontal();
        }
        this.rect = orientation.createRectangle();
        this.numTicks = 6;
        this.min = min;
        this.max = max;
        this.trackGraphic = new TrackGraphic( component );
        this.thumbGraphic = new ThumbGraphic( component );
        this.backgroundGraphic = new BackgroundGraphic( component );
        addGraphic( backgroundGraphic );
        addGraphic( trackGraphic );
        addGraphic( thumbGraphic );
        transform = orientation.createTransform();
        tickSetGraphic = new TickSetGraphic( component );
        addGraphic( tickSetGraphic );
        setValue( value );
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
        return value;
    }

    private void setValue( double value ) {
        if( value != this.value ) {
            this.value = value;
            orientation.setThumbValue( value );
            thumbGraphic.thumbIcon.setTransform( thumbGraphic.transformBuilder.toAffineTransform() );
            fireChangeEvent();
        }
    }


    public class TickSetGraphic extends CompositePhetGraphic {

        public TickSetGraphic( Component component ) {
            super( component );

            double dx = ( max - min ) / ( numTicks - 1 );
            for( int i = 0; i < numTicks; i++ ) {
                double value = min + dx * i;
                TickGraphic tickGraphic = new TickGraphic( component, value );
                addGraphic( tickGraphic );
                orientation.center( tickGraphic, value );
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

            Line2D.Double tick = orientation.createTickShape( textGraphic );
            tickGraphic = new PhetShapeGraphic( component, tick, new BasicStroke( 3 ), Color.black );
            addGraphic( textGraphic );
            addGraphic( tickGraphic );
        }

        public void setCenterX( int x ) {
            setLocation( x - textGraphic.getWidth() / 2, (int)( rect.getHeight() / 2 - 2 ) );
        }

        public void setCenterY( int y ) {
            int xVal = (int)( rect.getWidth() / 2 - 2 );
            int yVal = y - textGraphic.getHeight() / 2;
            System.out.println( "xVal=" + xVal + ", yVal = " + yVal );
            setLocation( xVal, yVal );
        }
    }

    public void setBackgroundColor( Color color ) {
        backgroundGraphic.shape.setColor( color );
    }

    public BackgroundGraphic getBackgroundGraphic() {
        return backgroundGraphic;
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
                    PhetSlider.this.setLocation( me.getX(), me.getY() );
                }
            } );
        }

        public PhetShapeGraphic getShapeGraphic() {
            return shape;
        }
    }

    public class ThumbGraphic extends CompositePhetGraphic {
        private PhetGraphic thumbIcon;
        private AffineTransformBuilder transformBuilder;

        public ThumbGraphic( Component component ) {
            super( component );
            this.thumbIcon = orientation.createThumbIcon();
            transformBuilder = new AffineTransformBuilder( thumbIcon.getBounds() );
            addGraphic( thumbIcon );
            addTranslationListener( new TranslationListener() {
                public void translationOccurred( TranslationEvent translationEvent ) {
                    MouseEvent me = translationEvent.getMouseEvent();
                    AffineTransform net = getNetTransform();
                    Point2D dst = new Point2D.Double();

                    //TODO to avoid this kind of code, maybe parents should transform mouse event before passing it along
                    //TODO so that children handle events in their own coordinate frame
                    try {
                        net.inverseTransform( me.getPoint(), dst );
                    }
                    catch( NoninvertibleTransformException e ) {
                        e.printStackTrace();
                    }
                    double newModelValue = orientation.getNewModelValue( dst, ThumbGraphic.this );
                    setValue( newModelValue );
                }
            } );
            setCursorHand();
        }

        public int getCenterX() {
            return (int)( transformBuilder.getX() + thumbIcon.getWidth() / 2 );
        }

        public int getCenterY() {
            return (int)( transformBuilder.getY() + thumbIcon.getHeight() / 2 );
        }
    }

    public class TrackGraphic extends CompositePhetGraphic {
        private PhetShapeGraphic shape;

        public TrackGraphic( Component component ) {
            super( component );
            int strokeSize = trackWidth;
            Stroke stroke = new BasicStroke( strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1 );
            Line2D.Double line = orientation.createLine( strokeSize );
            Shape track = stroke.createStrokedShape( line );
            Stroke outline = new BasicStroke( 1 );
            shape = new PhetShapeGraphic( component, track, outline, Color.black );
            addGraphic( shape );
        }

    }

    private interface SliderOrientation {

        Rectangle createRectangle();

        ModelViewTransform1D createTransform();

        Line2D.Double createLine( int strokeSize );

        PhetGraphic createThumbIcon();

        double getModelValue();

        void center( TickGraphic tickGraphic, double value );

        Line2D.Double createTickShape( PhetGraphic textGraphic );

        void setThumbValue( double value );

        double getNewModelValue( Point2D dst, ThumbGraphic thumbGraphic );
    }

    private class Horizontal implements SliderOrientation {

        public Rectangle createRectangle() {
            return new Rectangle( 0, 0, 200, 50 );
        }

        public ModelViewTransform1D createTransform() {
            return new ModelViewTransform1D( min, max, rect.x, rect.x + rect.width );
        }

        public Line2D.Double createLine( int strokeSize ) {
            return new Line2D.Double( rect.x + strokeSize / 2, rect.y + rect.height / 2,
                                      rect.x + rect.width - strokeSize / 2, rect.y + rect.height / 2 );
        }

        public PhetGraphic createThumbIcon() {
            return new PhetImageGraphic( getComponent(), "images/arrow-down.png" );
        }

        public void setThumbValue( double modelValue ) {
            int viewX = transform.modelToView( modelValue );
            thumbGraphic.transformBuilder.setX( viewX - thumbGraphic.transformBuilder.getOrigRect().width / 2 );
        }

        public double getNewModelValue( Point2D dst, ThumbGraphic thumbGraphic ) {
            double dest = dst.getX();

            if( dest > rect.x + rect.width ) {
                dest = rect.x + rect.width;
            }
            else if( dest < rect.x ) {
                dest = rect.x;
            }
            int viewX = (int)( dest );
            double val = transform.viewToModel( viewX );
            return val;
        }

        public double getModelValue() {
            return transform.viewToModel( thumbGraphic.getCenterX() );
        }

        public void center( TickGraphic tickGraphic, double value ) {
            int x = transform.modelToView( value );
            tickGraphic.setCenterX( x );
        }

        public Line2D.Double createTickShape( PhetGraphic textGraphic ) {
            return new Line2D.Double( textGraphic.getWidth() / 2, 0, textGraphic.getWidth() / 2, -trackWidth );
        }
    }

    private class Vertical implements SliderOrientation {

        public Rectangle createRectangle() {
            return new Rectangle( 0, 0, 50, 200 );
        }

        public ModelViewTransform1D createTransform() {
            return new ModelViewTransform1D( min, max, rect.x + rect.height, rect.y );
        }

        public Line2D.Double createLine( int strokeSize ) {
            return new Line2D.Double( rect.x + rect.width / 2, rect.y + strokeSize / 2,
                                      rect.x + rect.width / 2, rect.y + rect.height - strokeSize / 2 );
        }

        public PhetGraphic createThumbIcon() {
            try {
                BufferedImage bufferedImage = ImageLoader.loadBufferedImage( "images/arrow-right.gif" );
                return new PhetImageGraphic( getComponent(), bufferedImage );
            }
            catch( IOException e ) {
                throw new RuntimeException( e );
            }
        }

        public void setThumbValue( double modelValue ) {

            int viewY = transform.modelToView( modelValue );
            thumbGraphic.transformBuilder.setY( viewY - thumbGraphic.transformBuilder.getOrigRect().height / 2 );
        }

        public double getNewModelValue( Point2D dst, ThumbGraphic thumbGraphic ) {
            double dest = dst.getY();

            if( dest > rect.y + rect.height ) {
                dest = rect.y + rect.height;
            }
            else if( dest < rect.y ) {
                dest = rect.y;
            }
            int viewY = (int)( dest );
            double val = transform.viewToModel( viewY );
            return val;
        }

        public double getModelValue() {
            return transform.viewToModel( thumbGraphic.getCenterY() );
        }

        public void center( TickGraphic tickGraphic, double value ) {
            int y = transform.modelToView( value );
            tickGraphic.setCenterY( y );
        }

        public Line2D.Double createTickShape( PhetGraphic textGraphic ) {
            return new Line2D.Double( 0, textGraphic.getHeight() / 2, -trackWidth, textGraphic.getHeight() / 2 );
        }
    }

}
