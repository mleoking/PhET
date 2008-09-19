/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.collision.CollisionDetector;
import edu.colorado.phet.faraday.collision.ICollidable;
import edu.colorado.phet.faraday.model.Compass;

/**
 * CompassGraphic is the graphical representation of a compass.
 * It can be dragged around, and will indicate the direction of the magnetic field.
 * Its registration point is at the center of the needle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
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
    
    private Compass _compassModel;
    private PhetShapeGraphic _needleNorthGraphic, _needleSouthGraphic;
    private CompassNeedleCache _needleCache;
    private CollisionDetector _collisionDetector;
    private Rectangle[] _collisionBounds;
    private FaradayMouseHandler _mouseHandler;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * The registration point is at the rotation point of the compass needle.
     * 
     * @param component the parent Component
     * @param compassModel the compass model
     */
    public CompassGraphic( Component component, Compass compassModel ) {
        super( component );
        assert( component != null );
        assert( compassModel != null );
        
        _compassModel = compassModel;
        _compassModel.addObserver( this );
        
        setRenderingHints(new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Body of the compass
        BodyGraphic body = new BodyGraphic( component);
        body.centerRegistrationPoint();
        addGraphic( body );
        
        // Needle
        {
            // Cache
            _needleCache = new CompassNeedleCache( NEEDLE_SIZE );
            
            // North tip
            Color northColor = _needleCache.getNorthColor( 1.0 ); // opaque
            Shape northShape = _needleCache.getNorthShape( _compassModel.getDirection() );
            _needleNorthGraphic = new PhetShapeGraphic( component );
            _needleNorthGraphic.setColor( northColor );
            _needleNorthGraphic.setShape( northShape );
            addGraphic( _needleNorthGraphic );
            
            // South tip
            Color southColor = _needleCache.getSouthColor( 1.0 ); // opaque
            Shape southShape = _needleCache.getSouthShape( _compassModel.getDirection() );
            _needleSouthGraphic = new PhetShapeGraphic( component );
            _needleSouthGraphic.setColor( southColor );
            _needleSouthGraphic.setShape( southShape );
            addGraphic( _needleSouthGraphic );
        }
        
        // Thing that anchors the needle to the body.
        Shape anchorShape = new Ellipse2D.Double( -(ANCHOR_DIAMETER/2), -(ANCHOR_DIAMETER/2), ANCHOR_DIAMETER, ANCHOR_DIAMETER );
        PhetShapeGraphic anchor = new PhetShapeGraphic( component);
        anchor.setShape( anchorShape );
        anchor.setPaint( ANCHOR_COLOR );
        addGraphic( anchor );
        
        // Setup interactivity.
        _mouseHandler = new FaradayMouseHandler( _compassModel, this );
        _collisionDetector = new CollisionDetector( this );
        _mouseHandler.setCollisionDetector( _collisionDetector );
        super.setCursorHand();
        super.addMouseInputListener( _mouseHandler );
        
        update();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
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
            
            // Needle rotation
            double direction = _compassModel.getDirection();
            Shape northShape = _needleCache.getNorthShape( direction );
            Shape southShape = _needleCache.getSouthShape( direction );
            _needleNorthGraphic.setShape( northShape );
            _needleSouthGraphic.setShape( southShape );
            
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
        if ( isVisible() ) {
            if ( _collisionBounds == null ) {
                _collisionBounds = new Rectangle[1];
                _collisionBounds[0] = new Rectangle();
            }
            _collisionBounds[0].setBounds( getBounds() );
            return _collisionBounds;
        }
        else {
            return null;
        }
    }
    
    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /*
     * Informs the mouse handler of changes to the apparatus panel size.
     */
    public void canvasSizeChanged( ChangeEvent event ) {
        _mouseHandler.setDragBounds( 0, 0, event.getCanvasSize().width, event.getCanvasSize().height );   
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * SkaterNode creates a the compass body from a bunch of static graphic components.
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
