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
    private final Glacier _glacier;
    private final ArrayList _tools; // array of AbstractTool
    private final ArrayList _toolProducerListeners;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractModel( GlaciersClock clock, Glacier glacier ) {
        super();
        _clock = clock;
        _glacier = glacier;
        _clock.addClockListener( glacier );
        _tools = new ArrayList();
        _toolProducerListeners = new ArrayList();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public GlaciersClock getClock() {
        return _clock;
    }
    
    public Glacier getGlacier() {
        return _glacier;
    }
    
    public Valley getValley() {
        return _glacier.getValley();
    }
    
    public Climate getClimate() {
        return _glacier.getClimate();
    }
    
    //----------------------------------------------------------------------------
    // IToolProducer
    //----------------------------------------------------------------------------
    
    public BoreholeDrill addBoreholeDrill( Point2D position ) {
        BoreholeDrill tool = new BoreholeDrill( position, getGlacier() );
        addTool( tool );
        return tool;
    }
    
    public GlacialBudgetMeter addGlacialBudgetMeter( Point2D position ) {
        GlacialBudgetMeter tool = new GlacialBudgetMeter( position, getClimate() );
        addTool( tool );
        return tool;
    }
    
    public GPSReceiver createGPSReceiver( Point2D position ) {
        GPSReceiver tool = new GPSReceiver( position );
        addTool( tool );
        return tool;
    }
    
    public IceThicknessTool addIceThicknessTool( Point2D position ) {
        IceThicknessTool tool = new IceThicknessTool( position, getGlacier() );
        addTool( tool );
        return tool;
    }
    
    public Thermometer addThermometer( Point2D position ) {
        Thermometer tool = new Thermometer( position, getGlacier() );
        addTool( tool );
        return tool;
    }
    
    public TracerFlag addTracerFlag( Point2D position ) {
        TracerFlag tool = new TracerFlag( position, getGlacier() );
        addTool( tool );
        return tool;
    }
    
    public void addToolProducerListener( ToolProducerListener listener ) {
        _toolProducerListeners.add( listener );
    }
    
    public void removeToolProducerListener( ToolProducerListener listener ) {
        _toolProducerListeners.remove( listener );
    }
    
    private void addTool( AbstractTool tool ) {
        if ( _tools.contains( tool ) ) {
            throw new IllegalStateException( "attempted to add tool twice: " + tool.getClass().getName() );
        }
        _tools.add( tool );
        _clock.addClockListener( tool );
        notifyToolAdded( tool );
    }
    
    public void removeTool( AbstractTool tool ) {
        if ( !_tools.contains( tool ) ) {
            throw new IllegalStateException( "attempted to remove a tool that doesn't exist: " + tool.getClass().getName() );
        }
        _tools.remove( tool );
        _clock.removeClockListener( tool );
        notifyToolRemoved( tool );
        tool.cleanup();
    }
    
    public void removeAllTools() {
        ArrayList toolsCopy = new ArrayList( _tools ); // iterate on a copy of the array
        Iterator i = toolsCopy.iterator();
        while ( i.hasNext() ) {
            removeTool( (AbstractTool) i.next() );
        }
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
