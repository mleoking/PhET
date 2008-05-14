/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.glaciers.model.Borehole.BoreholeAdapter;
import edu.colorado.phet.glaciers.model.Borehole.BoreholeListener;
import edu.colorado.phet.glaciers.model.BoreholeDrill.BoreholeDrillListener;


/**
 * AbstractModel is the base class for all models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractModel implements IToolProducer, IBoreholeProducer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GlaciersClock _clock;
    private final Glacier _glacier;
    private final ArrayList _tools; // array of AbstractTool
    private final ArrayList _toolProducerListeners; // array of ToolProducerListener
    private final ArrayList _boreholes; // array of Borehole
    private final ArrayList _boreholeProducerListeners; // array of BoreholeProducerListener

    private final BoreholeDrillListener _boreholeDrillListener;
    private final BoreholeListener _boreholeListener;
    
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
        _boreholes = new ArrayList();
        _boreholeProducerListeners = new ArrayList();
        
        // create a borehole when the drill is pressed
        _boreholeDrillListener = new BoreholeDrillListener() {
            public void drillAt( Point2D position ) {
                addBorehole( position );
            }
        };
        
        // deletes a borehole
        _boreholeListener = new BoreholeAdapter() {
            public void deleteMe( Borehole borehole ) {
                removeBorehole( borehole );
            }
        };
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
        tool.addBoreholeDrillListener( _boreholeDrillListener );
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
    
    public void addToolProducerListener( IToolProducerListener listener ) {
        _toolProducerListeners.add( listener );
    }
    
    public void removeToolProducerListener( IToolProducerListener listener ) {
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
        if ( tool instanceof BoreholeDrill ) { //XXX
            ( (BoreholeDrill) tool ).removeBoreholeDrillListener( _boreholeDrillListener );
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
            ((IToolProducerListener)i.next()).toolAdded( tool );
        }
    }
    
    private void notifyToolRemoved( AbstractTool tool ) {
        Iterator i = _toolProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((IToolProducerListener)i.next()).toolRemoved( tool );
        }
    }
    
    //----------------------------------------------------------------------------
    // IBoreholeProducer
    //----------------------------------------------------------------------------
    
    public Borehole addBorehole( Point2D position ) {
        Borehole borehole = new Borehole( _glacier, position );
        borehole.addBoreholeListener( _boreholeListener );
        _boreholes.add( borehole );
        _clock.addClockListener( borehole );
        notifyBoreholeAdded( borehole );
        return borehole;
    }
    
    public void removeBorehole( Borehole borehole ) {
        if ( !_boreholes.contains( borehole ) ) {
            throw new IllegalStateException( "attempted to remove a borehole that doesn't exist" );
        }
        borehole.removeBoreholeListener( _boreholeListener );
        _boreholes.remove( borehole );
        _clock.removeClockListener( borehole );
        notifyBoreholeRemoved( borehole );
        borehole.cleanup();
    }
    
    public void removeAllBoreholes() {
        ArrayList boreholesCopy = new ArrayList( _boreholes ); // iterate on a copy of the array
        Iterator i = boreholesCopy.iterator();
        while ( i.hasNext() ) {
            removeBorehole( (Borehole) i.next() );
        }
    }
    
    public void addBoreholeProducerListener( IBoreholeProducerListener listener ) {
        _boreholeProducerListeners.add( listener );
    }
    
    public void removeBoreholeProducerListener( IBoreholeProducerListener listener ) {
        _boreholeProducerListeners.remove( listener );
    }
    
    private void notifyBoreholeAdded( Borehole borehole ) {
        Iterator i = _boreholeProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((IBoreholeProducerListener)i.next()).boreholeAdded( borehole );
        }
    }
    
    private void notifyBoreholeRemoved( Borehole borehole ) {
        Iterator i = _boreholeProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((IBoreholeProducerListener)i.next()).boreholeRemoved( borehole );
        }
    }
}
