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

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.BarMagnet;


/**
 * BarMagnetGraphic is the graphical representation of a bar magnet.
 * The registration point is at the center of the image.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetGraphic extends PhetImageGraphic
    implements SimpleObserver, ICollidable, ApparatusPanel2.ChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Composite COMPOSITE = 
        AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.9f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Rectangle _parentBounds;
    private BarMagnet _magnetModel;
    private boolean _transparencyEnabled;
    private CollisionDetector _collisionDetector;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param barMagnetModel model of the bar magnet
     */
    public BarMagnetGraphic( Component component, BarMagnet magnetModel ) {
        super( component, FaradayConfig.BAR_MAGNET_IMAGE );
        
        assert( component != null );
        assert( magnetModel != null );
        
        _collisionDetector = new CollisionDetector( this );
        
        _parentBounds = new Rectangle( 0, 0, component.getWidth(), component.getHeight() );
        
        // Save a reference to the model.
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
        
        // Set the model's size to match the image.
        _magnetModel.setSize( getWidth()/2, getHeight()/2 );
        
        // Registration point is the center of the image.
        centerRegistrationPoint();

        // Setup interactivity.
        InteractivityListener listener = new InteractivityListener();
        super.setCursorHand();
        super.addTranslationListener( listener );
        super.addMouseInputListener( listener );
        
        // Use the opaque image by default.
        setTransparencyEnabled( false );
        
        // Synchronize view with model.
        update();
    }

    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _magnetModel.removeObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Override inherited methods
    //----------------------------------------------------------------------------
    
    /**
     * Updates when we become visible.
     * 
     * @param visible true for visible, false for invisible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        update();
    }
    
    /** 
     * Enabled and disables transparency of the magnet graphic.
     * 
     * @param enabled true for transparency, false for opaque
     */
    public void setTransparencyEnabled( boolean enabled ) {
        _transparencyEnabled = enabled;
        repaint();
    }
    
    /**
     * Gets the current state of the magnet graphic transparency.
     * 
     * @return true if transparency, false if opaque
     */
    public boolean isTransparencyEnabled() {
        return _transparencyEnabled;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {
        
        if ( isVisible() ) {
            
            clearTransform();

            // Rotation
            if ( _magnetModel.getDirection() != 0 ) {
                rotate( _magnetModel.getDirection() );
            }
            
            // Location
            setLocation( (int) _magnetModel.getX(), (int) _magnetModel.getY() );
            
            repaint();
        }
    }
    
    //----------------------------------------------------------------------------
    // PhetImageGraphic overrides
    //----------------------------------------------------------------------------

    /**
     * Draws the magnet.  
     * If transparency is enabled, use alpha compositing
     * to make the magnet slightly transparent.
     * 
     * @param g2 the graphics context
     */
    public void paint( Graphics2D g2 ) {
        if ( isVisible() ) {
            if ( _transparencyEnabled ) {
                Composite oldComposite = g2.getComposite(); // save
                g2.setComposite( COMPOSITE );
                super.paint( g2 );
                g2.setComposite( oldComposite ); // restore
            }
            else {
                super.paint( g2 );
            }
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
                double x = _magnetModel.getX() + e.getDx();
                double y = _magnetModel.getY() + e.getDy();
                _magnetModel.setLocation( x, y );
            }
        }
        
        public void mouseDragged( MouseEvent event ) {
            if ( _stopDragging && getBounds().contains( event.getPoint() ) ) {
                _stopDragging = false;
            }
        }
        
        public void mouseReleased( MouseEvent event ) {
            _stopDragging = false;
        }
    }
}
