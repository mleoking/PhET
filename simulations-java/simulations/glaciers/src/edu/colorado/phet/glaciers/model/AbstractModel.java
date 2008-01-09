/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * AbstractModel is the base class for all models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractModel implements IToolProducer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GlaciersClock _clock;
    private final Valley _valley;
    private final Glacier _glacier;
    private final Climate _climate;
    private final ModelViewTransform _modelViewTransform;
    private final ArrayList _tools; // array of AbstractTool
    private final ArrayList _toolProducerListeners;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractModel( GlaciersClock clock, Valley valley, Glacier glacier, Climate climate, ModelViewTransform modelViewTransform ) {
        super();
        _clock = clock;
        _valley = valley;
        _glacier = glacier;
        _clock.addClockListener( glacier );
        _climate = climate;
        _clock.addClockListener( climate );
        _modelViewTransform = modelViewTransform;
        _tools = new ArrayList();
        _toolProducerListeners = new ArrayList();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public GlaciersClock getClock() {
        return _clock;
    }
    
    public Valley getValley() {
        return _valley;
    }
    
    public Glacier getGlacier() {
        return _glacier;
    }
    
    public Climate getClimate() {
        return _climate;
    }
    
    public ModelViewTransform getModelViewTransform() {
        return _modelViewTransform;
    }
    
    private void addTool( AbstractTool tool ) {
        if ( _tools.contains( tool ) ) {
            throw new IllegalStateException( "attempted to add tool twice: " + tool.getClass().getName() );
        }
        _tools.add( tool );
        _clock.addClockListener( tool );
        notifyToolAdded( tool );
    }
    
    private void removeTool( AbstractTool tool ) {
        if ( !_tools.contains( tool ) ) {
            throw new IllegalStateException( "attempted to remove a tool that doesn't exist: " + tool.getClass().getName() );
        }
        _clock.removeClockListener( tool );
        _tools.remove( tool );
        notifyToolRemoved( tool );
    }
    
    //----------------------------------------------------------------------------
    // IToolProducer
    //----------------------------------------------------------------------------
    
    public BoreholeDrill addBoreholeDrill( Point2D position ) {
        BoreholeDrill tool = new BoreholeDrill( position, _glacier );
        addTool( tool );
        return tool;
    }
    
    public GlacialBudgetMeter addGlacialBudgetMeter( Point2D position ) {
        GlacialBudgetMeter tool = new GlacialBudgetMeter( position, _glacier );
        addTool( tool );
        return tool;
    }
    
    public GPSReceiver createGPSReceiver( Point2D position ) {
        GPSReceiver tool = new GPSReceiver( position );
        addTool( tool );
        return tool;
    }
    
    public IceThicknessTool addIceThicknessTool( Point2D position ) {
        IceThicknessTool tool = new IceThicknessTool( position, _glacier );
        addTool( tool );
        return tool;
    }
    
    public Thermometer addThermometer( Point2D position ) {
        Thermometer tool = new Thermometer( position, _climate );
        addTool( tool );
        return tool;
    }
    
    public TracerFlag addTracerFlag( Point2D position ) {
        TracerFlag tool = new TracerFlag( position, _glacier );
        addTool( tool );
        return tool;
    }
    
    public void remove( AbstractTool tool ) {
        removeTool( tool );
    }
    
    public void addListener( ToolProducerListener listener ) {
        _toolProducerListeners.add( listener );
    }
    
    public void removeListener( ToolProducerListener listener ) {
        _toolProducerListeners.remove( listener );
    }
    
    private void notifyToolAdded( AbstractTool tool ) {
        Iterator i = _toolProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((ToolProducerListener)i.next()).toolAdded( tool );
        }
    }
    
    private void notifyToolRemoved( AbstractTool tool ) {
        Iterator i = _toolProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((ToolProducerListener)i.next()).toolRemoved( tool );
        }
    }
}
