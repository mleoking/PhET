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
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Iterator;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.faraday.model.Lightbulb;
import edu.colorado.phet.faraday.model.PickupCoil;
import edu.colorado.phet.faraday.model.Voltmeter;

/**
 * PickupCoilGraphic is the graphical representation of a pickup coil,
 * with devices (lightbulb and voltmeter ) for displaying the effects of induction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PickupCoilGraphic implements SimpleObserver, ICollidable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Component _component;
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
     * @param lightbulbModel the lightbulb model
     * @param voltmeterModel the voltmeter model
     */
    public PickupCoilGraphic( 
            final Component component, 
            BaseModel baseModel,
            PickupCoil pickupCoilModel, 
            Lightbulb lightbulbModel,
            Voltmeter voltmeterModel ) {
        
        assert ( component != null );
        assert ( baseModel != null );
        assert ( pickupCoilModel != null );
        assert ( lightbulbModel != null );
        assert ( voltmeterModel != null );
        
        _collisionDetector = new CollisionDetector( this );
        
        _component = component;
        
        _pickupCoilModel = pickupCoilModel;
        _pickupCoilModel.addObserver( this );
        
        // Graphics components
        _coilGraphic = new CoilGraphic( component, baseModel, pickupCoilModel );
        _lightbulbGraphic = new LightbulbGraphic( component, lightbulbModel, pickupCoilModel.getMagnet() );
        _voltmeterGraphic = new VoltmeterGraphic( component, voltmeterModel, pickupCoilModel.getMagnet() );
        
        // Foreground composition
        _foreground = new CompositePhetGraphic( component );
        _foreground.addGraphic( _coilGraphic.getForeground() );
        _foreground.addGraphic( _lightbulbGraphic );
        _foreground.addGraphic( _voltmeterGraphic );
        
        // Background composition
        _background = new CompositePhetGraphic( component );
        _background.addGraphic( _coilGraphic.getBackground() );
        
        // Interactivity
        _foreground.setCursorHand();
        _background.setCursorHand();
        InteractivityHandler listener = new InteractivityHandler();
        _foreground.addTranslationListener( listener );
        _background.addTranslationListener( listener );
        
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
        _lightbulbGraphic.finalize();
        _voltmeterGraphic.finalize();
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
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * InteractivityHandler is an inner class that handles interactivity.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private class InteractivityHandler implements TranslationListener {
        
        public InteractivityHandler() {}
        
        public void translationOccurred( TranslationEvent e ) {
            int dx = e.getDx();
            int dy = e.getDy();
            boolean collidesNow = _collisionDetector.collidesNow();
            boolean wouldCollide = _collisionDetector.wouldCollide( dx, dy );
            if ( !collidesNow && wouldCollide ) {
                // Ignore the translate if it would result in a collision.
                update();
            } 
            else {
                Component component = _component;
                if ( component instanceof ApparatusPanel2 ) {
                    // Translate if the mouse cursor is inside the canvas.
                    Dimension d = ((ApparatusPanel2)component).getVirtualCanvasSize();
                    Rectangle r = new Rectangle( 0, 0, d.width, d.height );
                    if ( r.contains( e.getMouseEvent().getPoint() ) ) {
                        double x = _pickupCoilModel.getX() + e.getDx();
                        double y = _pickupCoilModel.getY() + e.getDy();
                        _pickupCoilModel.setLocation( x, y );
                    }
                }
                else if ( component.contains( e.getMouseEvent().getPoint() ) ) {
                    // Translate if the mouse cursor is inside the parent component.
                    double x = _pickupCoilModel.getX() + e.getDx();
                    double y = _pickupCoilModel.getY() + e.getDy();
                    _pickupCoilModel.setLocation( x, y );
                }
            }
        }
    }
}