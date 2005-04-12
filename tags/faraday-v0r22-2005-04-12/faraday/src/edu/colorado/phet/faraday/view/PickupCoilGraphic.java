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
import java.text.DecimalFormat;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.collision.CollisionDetector;
import edu.colorado.phet.faraday.collision.ICollidable;
import edu.colorado.phet.faraday.model.AbstractMagnet;
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
public class PickupCoilGraphic 
    implements SimpleObserver, ICollidable, ApparatusPanel2.ChangeListener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static boolean _displayFluxEnabled = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Rectangle _parentBounds;
    private Rectangle _bounds;
    private PickupCoil _pickupCoilModel;
    private CoilGraphic _coilGraphic;
    private LightbulbGraphic _lightbulbGraphic;
    private VoltmeterGraphic _voltmeterGraphic;
    private CompositePhetGraphic _foreground, _background;
    private CollisionDetector _collisionDetector;
    private PhetTextGraphic _fluxValue, _deltaFluxValue, _emfValue;
    private DecimalFormat _fluxFormatter;
    private PhetShapeGraphic _centerPointGraphic, _topPointGraphic, _bottomPointGraphic;
    
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
        _bounds = new Rectangle();
        
        _pickupCoilModel = pickupCoilModel;
        _pickupCoilModel.addObserver( this );
        
        // Graphics components
        _coilGraphic = new CoilGraphic( component, pickupCoilModel, baseModel );
        _coilGraphic.setEndsConnected( true );
        _lightbulbGraphic = new LightbulbGraphic( component, lightbulbModel );;
        _voltmeterGraphic = new VoltmeterGraphic( component, voltmeterModel );
        
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
        
        // Points on the coil where the magnetic field is sampled to compute flux.
        if ( FaradayConfig.DEBUG_DRAW_PICKUP_SAMPLE_POINTS ) {
            int r = 3; // point radius
            Shape pointShape = new Ellipse2D.Double( -r, r, r * 2, r * 2 );
            
            _centerPointGraphic = new PhetShapeGraphic( component );
            _centerPointGraphic.setShape( pointShape );
            _centerPointGraphic.setColor( Color.YELLOW );
            _centerPointGraphic.centerRegistrationPoint();
            
            _topPointGraphic = new PhetShapeGraphic( component );
            _topPointGraphic.setShape( pointShape );
            _topPointGraphic.setColor( Color.YELLOW );
            _topPointGraphic.centerRegistrationPoint();
            
            _bottomPointGraphic = new PhetShapeGraphic( component );
            _bottomPointGraphic.setShape( pointShape );
            _bottomPointGraphic.setColor( Color.YELLOW );
            _bottomPointGraphic.centerRegistrationPoint();
            
            _foreground.addGraphic( _centerPointGraphic );
            _foreground.addGraphic( _topPointGraphic );
            _foreground.addGraphic( _bottomPointGraphic );
        }
        
        // Area & flux display
        {
            _fluxFormatter = new DecimalFormat( "###0.00" );
            Font font = new Font( "SansSerif", Font.PLAIN, 15 );
            
            _fluxValue = new PhetTextGraphic( component, font, "XXX", Color.YELLOW, 70, -25 );
            _fluxValue.setVisible( _displayFluxEnabled );
            _deltaFluxValue = new PhetTextGraphic( component, font, "YYY", Color.YELLOW, 70, 0 );
            _deltaFluxValue.setVisible( _displayFluxEnabled );
            _emfValue = new PhetTextGraphic( component, font, "WWW", Color.YELLOW, 70, 25 );
            _emfValue.setVisible( _displayFluxEnabled );
            
            _foreground.addGraphic( _fluxValue );
            _foreground.addGraphic( _deltaFluxValue );
            _foreground.addGraphic( _emfValue );
        }
        
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
            MouseHandler mouseHandler = new MouseHandler();
            _foreground.addMouseInputListener( mouseHandler );
            _background.addMouseInputListener( mouseHandler );
        }
        else {
            _foreground.removeAllMouseInputListeners();
            _background.removeAllMouseInputListeners();
        }
    }
    
    /**
     * Enables or disabled the display of debugging info.
     * 
     * @param displayFluxEnabled true or false
     */
    public static void setDisplayFluxEnabled( boolean displayFluxEnabled ) {
        _displayFluxEnabled = displayFluxEnabled;
    }
    
    /**
     * Is the display of debugging info enabled?
     * 
     * @return true or false
     */
    public static boolean isDisplayFluxEnabled() {
        return _displayFluxEnabled;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {

        if ( _foreground.isVisible() ) {
            
            // Location
            _foreground.setLocation( (int) _pickupCoilModel.getX(), (int) _pickupCoilModel.getY() );
            _background.setLocation( (int) _pickupCoilModel.getX(), (int) _pickupCoilModel.getY() );

            // Position the lightbulb and voltmeter at the top of the coil.
            _foreground.clearTransform();
            _background.clearTransform();
            _bounds.setBounds( _coilGraphic.getForeground().getBounds() );
            _bounds.union( _coilGraphic.getBackground().getBounds() );
            int x = -10;
            int y = -( _bounds.height / 2 );
            _lightbulbGraphic.setLocation( x, y );
            _voltmeterGraphic.setLocation( x + 5, y + 5 );

            // Direction (do this *after* positioning lightbulb and voltmeter!)
            _foreground.rotate( _pickupCoilModel.getDirection() );
            _background.rotate( _pickupCoilModel.getDirection() );

            // Sample points
            if ( FaradayConfig.DEBUG_DRAW_PICKUP_SAMPLE_POINTS ) {
                _centerPointGraphic.setLocation( 0, 0 );
                _topPointGraphic.setLocation( 0, (int) -_pickupCoilModel.getRadius() );
                _bottomPointGraphic.setLocation( 0, (int) _pickupCoilModel.getRadius() );
            }
            
            // Flux display
            {
                _fluxValue.setVisible( _displayFluxEnabled );
                _deltaFluxValue.setVisible( _displayFluxEnabled );
                _emfValue.setVisible( _displayFluxEnabled );
                
                if ( _displayFluxEnabled ) {
                    double flux = _pickupCoilModel.getFlux();
                    double deltaFlux = _pickupCoilModel.getDeltaFlux();
                    double emf = _pickupCoilModel.getEmf();

                    _fluxValue.setText( "Flux = " + _fluxFormatter.format( flux ) + " W" );
                    _deltaFluxValue.setText( "Delta Flux = " + _fluxFormatter.format( deltaFlux ) + " W" );
                    _emfValue.setText( "EMF = " + _fluxFormatter.format( emf ) + " V" );
                }
            }
            
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
     * MouseHandler handles mouse events.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private class MouseHandler extends MouseInputAdapter {
        
        private boolean _dragEnabled;
        private Point _previousPoint;
        
        public MouseHandler() {
            super();
            _dragEnabled = true;
            _previousPoint = new Point();
        }
        
        public void mousePressed( MouseEvent event ) {
            _dragEnabled = true;
            _previousPoint.setLocation( event.getPoint() );
        }
        
        public void mouseDragged( MouseEvent event ) {

            if ( !_dragEnabled && contains( event.getPoint() ) ) {
                _dragEnabled = true;
            }
            
            if ( _dragEnabled ) {

                int dx = event.getX() - _previousPoint.x;
                int dy = event.getY() - _previousPoint.y;
                
                boolean inApparatusPanel = _parentBounds.contains( event.getPoint() );
                boolean collidesNow = _collisionDetector.collidesNow();
                boolean wouldCollide = _collisionDetector.wouldCollide( dx, dy );
                
                if ( !inApparatusPanel || ( !collidesNow && wouldCollide ) ) {
                    // Ignore the translate if the mouse is outside the apparatus panel or 
                    // if the tanslate would result in a collision.
                    _dragEnabled = false;
                }
                else {
                    // Translate if the mouse cursor is inside the parent component.
                    double x = _pickupCoilModel.getX() + dx;
                    double y = _pickupCoilModel.getY() + dy;
                    _pickupCoilModel.setLocation( x, y );
                }
            }
            
            _previousPoint.setLocation( event.getPoint() );
        }
    }
}