/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.model.Compass;

/**
 * CompassGraphic is the graphical representation of a compass.
 * It can be dragged around, and will indicate the direction of the magnetic field.
 * Its registration point is at the center of the needle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CompassGraphic extends CompositePhetGraphic
    implements SimpleObserver, ICollidable, ApparatusPanel2.ChangeListener {
  
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Sizes
    private static final int RING_DIAMETER = 70;
    private static final int RING_WIDTH = 10;
    private static final int INDICATOR_DIAMETER = 6;
    private static final int ANCHOR_DIAMETER = 6;
    private static final Dimension NEEDLE_SIZE = new Dimension( 55, 15 );
    
    // Colors
    private static final Color RING_COLOR = new Color( 153, 153, 153 );
    private static final Color LENS_COLOR = new Color( 255, 255, 255, 15 ); // alpha!
    private static final Color INDICATOR_COLOR = Color.BLACK;
    private static final Color ANCHOR_COLOR = Color.BLACK;
    
    // misc.
    private static final int INDICATOR_INCREMENT = 45; // degrees

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Rectangle _parentBounds;
    private Compass _compassModel;
    private CompassNeedleGraphic _needle;
    private CollisionDetector _collisionDetector;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * The registration point is at the rotation point of the compass needle.
     * 
     * @param component the parent Component
     * @param magnetModel the magnet that the compass is observing
     */
    public CompassGraphic( Component component, Compass compassModel ) {
        super( component );
        assert( component != null );
        assert( compassModel != null );
        
        _collisionDetector = new CollisionDetector( this );
        
        _parentBounds = new Rectangle( 0, 0, component.getWidth(), component.getHeight() );
        
        _compassModel = compassModel;
        _compassModel.addObserver( this );
        
        setRenderingHints(new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Body of the compass
        BodyGraphic body = new BodyGraphic( component);
        body.centerRegistrationPoint();
        addGraphic( body );
        
        // Needle
        _needle = new CompassNeedleGraphic( component );
        _needle.setSize( NEEDLE_SIZE );
        _needle.setDirection( _compassModel.getDirection() );
        addGraphic( _needle );
        
        // Thing that anchors the needle to the body.
        Shape anchorShape = new Ellipse2D.Double( -(ANCHOR_DIAMETER/2), -(ANCHOR_DIAMETER/2), ANCHOR_DIAMETER, ANCHOR_DIAMETER );
        PhetShapeGraphic anchor = new PhetShapeGraphic( component);
        anchor.setShape( anchorShape );
        anchor.setPaint( ANCHOR_COLOR );
        addGraphic( anchor );
        
        // Setup interactivity.
        InteractivityListener listener = new InteractivityListener();
        super.setCursorHand();
        super.addTranslationListener( listener );
        super.addMouseInputListener( listener );
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _compassModel.removeObserver( this );
        _compassModel = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {
        super.setVisible( _compassModel.isEnabled() );
        if( isVisible() ) {
            // Rotation of the needle
            _needle.setDirection( _compassModel.getDirection() );
            // Location
            setLocation( (int) _compassModel.getX(), (int) _compassModel.getY() );
        }
    }

    //----------------------------------------------------------------------------
    // ICollidable implementation
    //----------------------------------------------------------------------------

    /*
     * @see edu.colorado.phet.faraday.view.ICollidable#getCollisionDetector()
     */
    public CollisionDetector getCollisionDetector() {
        return _collisionDetector;
    }

    /*
     * @see edu.colorado.phet.faraday.view.ICollidable#getCollisionBounds()
     */
    public Rectangle[] getCollisionBounds() {
        Rectangle[] bounds = null;
        if ( isVisible() ) {
            bounds = new Rectangle[1];
            bounds[0] = getBounds();
        }
        return bounds;
    }
    
    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.view.ApparatusPanel2.ChangeListener#canvasSizeChanged(edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent)
     */
    public void canvasSizeChanged( ChangeEvent event ) {
        _parentBounds.setBounds( 0, 0, event.getCanvasSize().width, event.getCanvasSize().height );   
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * InteractivityListener is an inner class that handles interactivity.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private class InteractivityListener extends MouseInputAdapter implements TranslationListener {
        
        private boolean _stopDragging;
        
        public InteractivityListener() {
            super();
            _stopDragging = false;
        }
        
        public void translationOccurred( TranslationEvent e ) {
            int dx = e.getDx();
            int dy = e.getDy();
            boolean collidesNow = _collisionDetector.collidesNow();
            boolean wouldCollide = _collisionDetector.wouldCollide( dx, dy );
            if ( !collidesNow && wouldCollide ) {
                // Ignore the translate if it would result in a collision.
                _stopDragging = true;
                update();
            }
            else if ( !_stopDragging && _parentBounds.contains( e.getMouseEvent().getPoint() ) ) {
                // Translate if the mouse cursor is inside the parent component.
                double x = _compassModel.getX() + e.getDx();
                double y = _compassModel.getY() + e.getDy();
                _compassModel.setLocation( x, y );
            }
        }
        
        public void mouseDragged( MouseEvent event ) {
            if ( _stopDragging && contains( event.getPoint() ) ) {
                _stopDragging = false;
            }
        }
        
        public void mouseReleased( MouseEvent event ) {
            _stopDragging = false;
        }
    }
    
    /**
     * BodyGraphic creates a the compass body from a bunch of static graphic components.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private static class BodyGraphic extends PhetImageGraphic {
        public BodyGraphic( Component component ) {
            super( component );
            
            // This will be flattened after we've added graphics to it.
            GraphicLayerSet graphicLayerSet = new GraphicLayerSet( component );
            RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            graphicLayerSet.setRenderingHints( hints );
            
            // Ring & lens, center at (0,0)
            Shape ringShape = new Ellipse2D.Double( -( RING_DIAMETER/2), -( RING_DIAMETER/2), RING_DIAMETER, RING_DIAMETER );
            PhetShapeGraphic ring = new PhetShapeGraphic( component );
            ring.setShape( ringShape );
            ring.setPaint( LENS_COLOR );
            ring.setBorderColor( RING_COLOR );
            ring.setStroke( new BasicStroke( RING_WIDTH ) );
            graphicLayerSet.addGraphic( ring );
            
            // Indicators at evenly-spaced increments around the ring.
            int angle = 0; // degrees
            PhetShapeGraphic indicatorGraphic = null;
            Shape indicatorShape = new Ellipse2D.Double( -(INDICATOR_DIAMETER/2), -(INDICATOR_DIAMETER/2), INDICATOR_DIAMETER, INDICATOR_DIAMETER ); 
            while ( angle < 360 ) {
                indicatorGraphic = new PhetShapeGraphic( component );
                indicatorGraphic.setShape( indicatorShape );
                indicatorGraphic.setPaint( INDICATOR_COLOR );
                AbstractVector2D v = ImmutableVector2D.Double.parseAngleAndMagnitude( RING_DIAMETER/2, Math.toRadians( angle ) );
                int rx = (int) v.getX();
                int ry = (int) v.getY();
                indicatorGraphic.setRegistrationPoint( rx, ry );
                graphicLayerSet.addGraphic( indicatorGraphic );
                angle += INDICATOR_INCREMENT;
            }
            
            // Flatten the graphic layer set.
            {
                Dimension size = graphicLayerSet.getSize();
                BufferedImage bufferedImage = new BufferedImage( size.width, size.height, BufferedImage.TYPE_INT_ARGB );
                Graphics2D g2 = bufferedImage.createGraphics();
                graphicLayerSet.translate( size.width/2, size.height/2 );
                graphicLayerSet.paint( g2 );
                setImage( bufferedImage );
            }
        }
    }
}
