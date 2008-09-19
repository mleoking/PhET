/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.collision.CollisionDetector;
import edu.colorado.phet.faraday.collision.ICollidable;
import edu.colorado.phet.faraday.model.ACPowerSupply;
import edu.colorado.phet.faraday.model.Battery;
import edu.colorado.phet.faraday.model.Electromagnet;
import edu.colorado.phet.faraday.model.SourceCoil;


/**
 * ElectromagnetGraphic is the graphical representation of an electromagnet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ElectromagnetGraphic extends GraphicLayerSet
implements SimpleObserver, ICollidable, ApparatusPanel2.ChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Electromagnet _electromagnetModel;
    private CoilGraphic _coilGraphic;
    private BatteryGraphic _batteryGraphic;
    private ACPowerSupplyGraphic _acPowerSupplyGraphic;
    private GraphicLayerSet _foreground, _background;
    private CollisionDetector _collisionDetector;
    private PhetShapeGraphic _modelShapeGraphic;
    private FaradayMouseHandler _mouseHandler;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ElectromagnetGraphic(
            Component component,
            BaseModel baseModel,
            Electromagnet electromagnetModel,
            SourceCoil sourceCoilModel,
            Battery batteryModel,
            ACPowerSupply acPowerSupplyModel ) {
        
        assert ( component != null );
        assert ( baseModel != null );
        assert ( electromagnetModel != null );
        
        _electromagnetModel = electromagnetModel;
        _electromagnetModel.addObserver( this );
        
        // Graphics components
        _coilGraphic = new CoilGraphic( component, sourceCoilModel, baseModel );
        _batteryGraphic = new BatteryGraphic( component, batteryModel );
        _acPowerSupplyGraphic = new ACPowerSupplyGraphic( component, acPowerSupplyModel );
        
        // Foreground composition
        _foreground = new GraphicLayerSet( component );
        _foreground.addGraphic( _coilGraphic.getForeground() );
        _foreground.addGraphic( _batteryGraphic );
        _foreground.addGraphic( _acPowerSupplyGraphic );
        
        // Background composition
        _background = new GraphicLayerSet( component );
        _background.addGraphic( _coilGraphic.getBackground() );
        
        /* =================================================================
         * NOTE! --
         * Do NOT add the foreground and background to this GraphicLayerSet.
         * They will be added directly to the apparatus panel in the module,
         * so that objects can pass between the foreground and background.
         * =================================================================
         */
        
        // Graphic that represents the magnet's bounds.
        {
            _modelShapeGraphic = new PhetShapeGraphic( component );
            _modelShapeGraphic.setShape( _electromagnetModel.getShape() );
            _modelShapeGraphic.setVisible( false );
            _modelShapeGraphic.setBorderColor( Color.YELLOW );
            _modelShapeGraphic.setStroke( new BasicStroke( 1f ) );
            _foreground.addGraphic( _modelShapeGraphic );
            _foreground.setRenderingHints(new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        }
        
        // Interactivity
        {
            _mouseHandler = new FaradayMouseHandler( _electromagnetModel, this );
            _collisionDetector = new CollisionDetector( this );
            _mouseHandler.setCollisionDetector( _collisionDetector );
            
            _foreground.setCursorHand();
            _foreground.addMouseInputListener( _mouseHandler );
          
            _background.setCursorHand();
            _background.addMouseInputListener( _mouseHandler );
        }
        
        update();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _electromagnetModel.removeObserver( this );
        _electromagnetModel = null;
        _coilGraphic.cleanup();
        _batteryGraphic.cleanup();
        _acPowerSupplyGraphic.cleanup();
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
 
    public void setModelShapeVisible( boolean visible ) {
        _modelShapeGraphic.setVisible( visible );
    }
    
    public boolean isModelShapeVisible() {
        return _modelShapeGraphic.isVisible();
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
    
    /**
     * Sets the visibility.
     * 
     * @param visible true or false
     */
    public void setVisible( boolean visible ) {
        _foreground.setVisible( visible );
        _background.setVisible( visible );
        update();
    }
    
    /**
     * Gets the visibility.
     * 
     * @return true or false
     */
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
            int x = 0;
            int y = -( _coilGraphic.getForeground().getHeight() / 2 ) - 14;
            _batteryGraphic.setLocation( x, y );
            _acPowerSupplyGraphic.setLocation( x, y );
            
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
}
