/* Copyright 2005, University of Colorado */

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
import edu.colorado.phet.faraday.model.ACSource;
import edu.colorado.phet.faraday.model.Battery;
import edu.colorado.phet.faraday.model.Electromagnet;
import edu.colorado.phet.faraday.model.SourceCoil;
import edu.colorado.phet.faraday.util.IRescaler;


/**
 * ElectromagnetGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ElectromagnetGraphic 
implements SimpleObserver, ICollidable, ApparatusPanel2.ChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Rectangle _parentBounds;
    private Electromagnet _electromagnetModel;
    private CoilGraphic _coilGraphic;
    private BatteryGraphic _batteryGraphic;
    private ACSourceGraphic _acSourceGraphic;
    private CompositePhetGraphic _foreground, _background;
    private CollisionDetector _collisionDetector;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    public ElectromagnetGraphic(
            Component component,
            BaseModel baseModel,
            Electromagnet electromagnetModel,
            SourceCoil sourceCoilModel,
            Battery batteryModel,
            ACSource acSourceModel ) {
        
        assert ( component != null );
        assert ( baseModel != null );
        assert ( electromagnetModel != null );
        
        _collisionDetector = new CollisionDetector( this );
        
        _parentBounds = new Rectangle( 0, 0, component.getWidth(), component.getHeight() );
        
        _electromagnetModel = electromagnetModel;
        _electromagnetModel.addObserver( this );
        
        // Graphics components
        _coilGraphic = new CoilGraphic( component, baseModel, sourceCoilModel );
        _batteryGraphic = new BatteryGraphic( component, batteryModel );
        _acSourceGraphic = new ACSourceGraphic( component, acSourceModel );
        
        // Foreground composition
        _foreground = new CompositePhetGraphic( component );
        _foreground.addGraphic( _coilGraphic.getForeground() );
        _foreground.addGraphic( _batteryGraphic );
        _foreground.addGraphic( _acSourceGraphic );
        
        // Background composition
        _background = new CompositePhetGraphic( component );
        _background.addGraphic( _coilGraphic.getBackground() );
        
        // Interactivity
        _foreground.setCursorHand();
        _background.setCursorHand();
        InteractivityListener listener = new InteractivityListener();
        _foreground.addTranslationListener( listener );
        _foreground.addMouseInputListener( listener );
        _background.addTranslationListener( listener );
        _background.addMouseInputListener( listener );
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _electromagnetModel.removeObserver( this );
        _electromagnetModel = null;
        _coilGraphic.finalize();
        _batteryGraphic.finalize();
        _acSourceGraphic.finalize();
    }
    

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Set the rescaler, used to make values look better when displayed.
     * 
     * @param rescaler
     */
    public void setRescaler( IRescaler rescaler ) {
        _coilGraphic.setRescaler( rescaler );
    }
    
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
    
    public void setVisible( boolean visible ) {
        _foreground.setVisible( visible );
        _background.setVisible( visible );
        update();
    }
    
    public boolean isVisible() {
        return _foreground.isVisible() || _background.isVisible();
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        if ( isVisible() ) {
            // Location
            _foreground.setLocation( (int) _electromagnetModel.getX(), (int) _electromagnetModel.getY() );
            _background.setLocation( (int) _electromagnetModel.getX(), (int) _electromagnetModel.getY() );
            
            // Position the voltage sources at the top of the coil.
            _foreground.clearTransform();
            _background.clearTransform();
            Rectangle bounds = new Rectangle( _coilGraphic.getForeground().getBounds() );
            bounds.union( _coilGraphic.getBackground().getBounds() );
            int x = 0;
            int y = -( bounds.height / 2 ) - 10;
            _batteryGraphic.setLocation( x, y );
            _acSourceGraphic.setLocation( x, y );
            
            // Direction (do this *after* positioning voltage sources!)
            _foreground.rotate( _electromagnetModel.getDirection() );
            _background.rotate( _electromagnetModel.getDirection() );
            
            _foreground.repaint();
            _background.repaint();
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
                double x = _electromagnetModel.getX() + e.getDx();
                double y = _electromagnetModel.getY() + e.getDy();
                _electromagnetModel.setLocation( x, y );
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
