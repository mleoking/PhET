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

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.model.Lightbulb;
import edu.colorado.phet.faraday.model.PickupCoil;
import edu.colorado.phet.faraday.model.Voltmeter;
import edu.colorado.phet.faraday.util.ElectronSpeedRescaler;
import edu.colorado.phet.faraday.util.LightbulbRescaler;
import edu.colorado.phet.faraday.util.VoltmeterRescaler;

/**
 * PickupCoilGraphic is the graphical representation of a pickup coil,
 * with devices (lightbulb and voltmeter ) for displaying the effects of induction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PickupCoilGraphic 
    implements SimpleObserver, ICollidable, ApparatusPanel2.ChangeListener {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Rectangle _parentBounds;
    private PickupCoil _pickupCoilModel;
    private CoilGraphic _coilGraphic;
    private LightbulbGraphic _lightbulbGraphic;
    private VoltmeterGraphic _voltmeterGraphic;
    private CompositePhetGraphic _foreground, _background;
    private CollisionDetector _collisionDetector;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param BaseModel the base model
     * @param pickupCoilModel the pickup coil model
     * @param lightbulbModel
     * @param voltmeterModel
     */
    public PickupCoilGraphic( 
            final Component component, 
            BaseModel baseModel,
            PickupCoil pickupCoilModel, 
            Lightbulb lightbulbModel,
            Voltmeter voltmeterModel,
            AbstractMagnet magnetModel ) {
        
        assert ( component != null );
        assert ( baseModel != null );
        assert ( pickupCoilModel != null );
        assert ( lightbulbModel != null );
        assert ( voltmeterModel != null );
        
        _collisionDetector = new CollisionDetector( this );
        
        _parentBounds = new Rectangle( 0, 0, component.getWidth(), component.getHeight() );
        
        _pickupCoilModel = pickupCoilModel;
        _pickupCoilModel.addObserver( this );
        
        // Graphics components
        _coilGraphic = new CoilGraphic( component, pickupCoilModel, baseModel );
        _lightbulbGraphic = new LightbulbGraphic( component, lightbulbModel );;
        _voltmeterGraphic = new VoltmeterGraphic( component, voltmeterModel );
        
        // Rescalers
        _coilGraphic.setRescaler( new ElectronSpeedRescaler( magnetModel ) );
        _lightbulbGraphic.setRescaler( new LightbulbRescaler( magnetModel ) );
        _voltmeterGraphic.setRescaler( new VoltmeterRescaler( magnetModel) );
        
        // Foreground composition
        _foreground = new CompositePhetGraphic( component );
        _foreground.addGraphic( _coilGraphic.getForeground() );
        _foreground.addGraphic( _lightbulbGraphic );
        _foreground.addGraphic( _voltmeterGraphic );
        
        // Background composition
        _background = new CompositePhetGraphic( component );
        _background.addGraphic( _coilGraphic.getBackground() );
        
        // Interactivity
        setDraggingEnabled( true );
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _pickupCoilModel.removeObserver( this );
        _pickupCoilModel = null;
        _coilGraphic.finalize();
    }
 
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the PhetGraphic that contains the foreground elements of the coil.
     * 
     * @return the foreground graphics
     */
    public PhetGraphic getForeground() {
        return _foreground;
    }
    
    /**
     * Gets the PhetGraphic that contains the background elements of the coil.
     * 
     * @return the background graphics
     */
    public PhetGraphic getBackground() {
        return _background;
    }
    
    /**
     * Gets the coil graphic.
     * This is intended for use in debugging, or for connecting a control panel.
     */
    public CoilGraphic getCoilGraphic() {
        return _coilGraphic;
    }
 
    /**
     * Is a specified point inside the graphic?
     * 
     * @param p the point
     * @return true or false
     */
    public boolean contains( Point p ) {
        return _foreground.contains( p ) || _background.contains( p );
    }
    
    /**
     * Enables and disables dragging of the coil.
     * 
     * @param enabled true or false
     */
    public void setDraggingEnabled( boolean enabled ) {
        if ( enabled ) {
            // Interactivity
            _foreground.setCursorHand();
            _background.setCursorHand();
            InteractivityListener listener = new InteractivityListener();
            _foreground.addTranslationListener( listener );
            _foreground.addMouseInputListener( listener );
            _background.addTranslationListener( listener );
            _background.addMouseInputListener( listener );
        }
        else {
            _foreground.removeAllMouseInputListeners();
            _background.removeAllMouseInputListeners();
        }
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {

        // Location
        _foreground.setLocation( (int) _pickupCoilModel.getX(), (int) _pickupCoilModel.getY() );
        _background.setLocation( (int) _pickupCoilModel.getX(), (int) _pickupCoilModel.getY() );
        
        // Position the lightbulb and voltmeter at the top of the coil.
        _foreground.clearTransform();
        _background.clearTransform();
        Rectangle bounds = new Rectangle( _coilGraphic.getForeground().getBounds() );
        bounds.union( _coilGraphic.getBackground().getBounds() );
        int x = -10;
        int y = -( bounds.height / 2 ) - 5;
        _lightbulbGraphic.setLocation( x, y );
        _voltmeterGraphic.setLocation( x, y );
        
        // Direction (do this *after* positioning lightbulb and voltmeter!)
        _foreground.rotate( _pickupCoilModel.getDirection() );
        _background.rotate( _pickupCoilModel.getDirection() );
        
        _foreground.repaint();
        _background.repaint();
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
       return _coilGraphic.getCollisionBounds();
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
                double x = _pickupCoilModel.getX() + e.getDx();
                double y = _pickupCoilModel.getY() + e.getDy();
                _pickupCoilModel.setLocation( x, y );
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
}