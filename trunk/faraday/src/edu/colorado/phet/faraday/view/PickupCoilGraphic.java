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
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.collision.CollisionDetector;
import edu.colorado.phet.faraday.collision.ICollidable;
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.model.Lightbulb;
import edu.colorado.phet.faraday.model.PickupCoil;
import edu.colorado.phet.faraday.model.Voltmeter;

/**
 * PickupCoilGraphic is the graphical representation of a pickup coil,
 * with indicators (lightbulb and voltmeter ) for displaying the effect
 * of electromagnetic induction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PickupCoilGraphic extends GraphicLayerSet
    implements SimpleObserver, ICollidable, ApparatusPanel2.ChangeListener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static boolean _displayFluxEnabled = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PickupCoil _pickupCoilModel;
    private CoilGraphic _coilGraphic;
    private LightbulbGraphic _lightbulbGraphic;
    private VoltmeterGraphic _voltmeterGraphic;
    private CompositePhetGraphic _foreground, _background;
    private CollisionDetector _collisionDetector;
    private PhetTextGraphic _fluxValue, _deltaFluxValue, _emfValue;
    private DecimalFormat _fluxFormatter;
    private SamplePointsGraphic _samplePointsGraphic;
    private FaradayMouseHandler _mouseHandler;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param baseModel the base model
     * @param pickupCoilModel
     * @param lightbulbModel
     * @param voltmeterModel
     * @param magnetModel
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
        
        _pickupCoilModel = pickupCoilModel;
        _pickupCoilModel.addObserver( this );
        
        _collisionDetector = new CollisionDetector( this );
        
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
        
        /* =================================================================
         * NOTE! --
         * Do NOT add the foreground and background to this GraphicLayerSet.
         * They will be added directly to the apparatus panel in the module,
         * so that objects can pass between the foreground and background.
         * =================================================================
         */
        
        // Interactivity
        setDraggingEnabled( true );
        
        // Points on the coil where the magnetic field is sampled to compute flux.
        if ( FaradayConfig.DEBUG_DRAW_PICKUP_SAMPLE_POINTS ) {
            _samplePointsGraphic = new SamplePointsGraphic( component );           
            _foreground.addGraphic( _samplePointsGraphic );
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
     * Enables and disables dragging of the coil.
     * 
     * @param enabled true or false
     */
    public void setDraggingEnabled( boolean enabled ) {
        _foreground.setIgnoreMouse( !enabled );
        _background.setIgnoreMouse( !enabled );
        if ( enabled ) {
            // Interactivity
            _mouseHandler = new FaradayMouseHandler( _pickupCoilModel, this );
            _mouseHandler.setCollisionDetector( _collisionDetector );

            _foreground.setCursorHand();
            _foreground.addMouseInputListener( _mouseHandler );
            
            _background.setCursorHand();
            _background.addMouseInputListener( _mouseHandler );
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
    // GraphicLayerSet overrides
    //----------------------------------------------------------------------------
    
    /**
     * Is a specified point inside the graphic?
     * 
     * @param x
     * @param y
     * @return true or false
     */
    public boolean contains( int x, int y ) {
        return _foreground.contains( x, y ) || _background.contains( x, y );
    }
    
    /**
     * Is a specified point inside the graphic?
     * 
     * @param p the point
     * @return true or false
     */
    public boolean contains( Point p ) {
        return contains( p.x, p.y );
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
            int x = -10;
            int y = -( _coilGraphic.getForeground().getHeight() / 2 );
            _lightbulbGraphic.setLocation( x, y );
            _voltmeterGraphic.setLocation( x + 5, y + 5 );

            // Direction (do this *after* positioning lightbulb and voltmeter!)
            _foreground.rotate( _pickupCoilModel.getDirection() );
            _background.rotate( _pickupCoilModel.getDirection() );

            // Sample points
            if ( _samplePointsGraphic != null ) {
                _samplePointsGraphic.setPointLocations( (int)_pickupCoilModel.getRadius() );
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
     * Informs the mouse handler of changes to the apparatus panel size.
     */
    public void canvasSizeChanged( ChangeEvent event ) {
        _mouseHandler.setDragBounds( 0, 0, event.getCanvasSize().width, event.getCanvasSize().height );   
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * SamplePointsGraphic is the graphical representation of the
     * points on the coil where the magnetic field is sampled.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    public static class SamplePointsGraphic extends CompositePhetGraphic {
        
        private static final int POINT_RADIUS = 2;
        private static final Color POINT_COLOR = Color.YELLOW;
        private static final Color CENTER_COLOR = Color.RED;
        
        private PhetShapeGraphic _centerPoint;
        private PhetShapeGraphic[] _abovePoints;
        private PhetShapeGraphic[] _belowPoints;
        
        /**
         * Sole constructor.
         * Creates a set of graphics that represent the sample points.
         * 
         * @param component
         */
        public SamplePointsGraphic( Component component ) {
            super( component );

            int r = POINT_RADIUS;
            Shape pointShape = new Ellipse2D.Double( -r, r, r * 2, r * 2 );

            _centerPoint = new PhetShapeGraphic( component );
            _centerPoint.setShape( pointShape );
            _centerPoint.setColor( CENTER_COLOR );
            _centerPoint.centerRegistrationPoint();
            addGraphic( _centerPoint );

            _abovePoints = new PhetShapeGraphic[ PickupCoil.SAMPLE_POINTS_ABOVE ];
            for ( int i = 0; i < _abovePoints.length; i++ ) {
                _abovePoints[i] = new PhetShapeGraphic( component );
                _abovePoints[i].setShape( pointShape );
                _abovePoints[i].setColor( POINT_COLOR );
                _abovePoints[i].centerRegistrationPoint();
                addGraphic( _abovePoints[i] );
            }

            _belowPoints = new PhetShapeGraphic[ PickupCoil.SAMPLE_POINTS_BELOW ];
            for ( int i = 0; i < _belowPoints.length; i++ ) {
                _belowPoints[i] = new PhetShapeGraphic( component );
                _belowPoints[i].setShape( pointShape );
                _belowPoints[i].setColor( POINT_COLOR );
                _belowPoints[i].centerRegistrationPoint();
                addGraphic( _belowPoints[i] );
            }
        }
        
        /**
         * Sets the location of the sample points,
         * typically called when the coil's radius is changed.
         * 
         * @param radius
         */
        public void setPointLocations( int radius ) {
            // Center point
            _centerPoint.setLocation( 0, 0 );
            // Points above the center
            for ( int i = 0; i < _abovePoints.length; i++ ) {
                int y = ( i + 1 ) * ( radius / _abovePoints.length );
                _abovePoints[i].setLocation( 0, -y );
            }
            // Points below the center
            for ( int i = 0; i < _belowPoints.length; i++ ) {
                int y = ( i + 1 ) * ( radius / _belowPoints.length );
                _belowPoints[i].setLocation( 0, y );
            }
        }
    }
}