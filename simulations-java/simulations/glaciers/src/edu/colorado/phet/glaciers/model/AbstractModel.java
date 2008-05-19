/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.glaciers.model.AbstractTool.ToolAdapter;
import edu.colorado.phet.glaciers.model.AbstractTool.ToolListener;
import edu.colorado.phet.glaciers.model.Borehole.BoreholeAdapter;
import edu.colorado.phet.glaciers.model.Borehole.BoreholeListener;
import edu.colorado.phet.glaciers.model.BoreholeDrill.BoreholeDrillListener;
import edu.colorado.phet.glaciers.model.Debris.DebrisListener;


/**
 * AbstractModel is the base class for all models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractModel implements IToolProducer, IBoreholeProducer, IDebrisProducer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean ENABLE_DEBUG_OUTPUT = true;
    private static final int YEARS_PER_DEBRIS_GENERATED = 10; // debris is generated this many years apart
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GlaciersClock _clock;
    private final Glacier _glacier;
    
    private final ArrayList _tools; // array of AbstractTool
    private final ArrayList _toolProducerListeners; // array of ToolProducerListener
    private final ToolListener _toolSelfDeletionListener;
    private final BoreholeDrillListener _boreholeDrillListener;
    private final BoreholeListener _boreholeSelfDeletionListener;
    
    private final ArrayList _boreholes; // array of Borehole
    private final ArrayList _boreholeProducerListeners; // array of BoreholeProducerListener
    
    private final ArrayList _debris; // array of Debris
    private final ArrayList _debrisProducerListeners; // array of DebrisProducerListener
    private final DebrisListener _debrisSelfDeletionListener;

    private int _timeSinceLastDebrisGenerated;
    private Random _randomDebrisX, _randomDebrisY;
    
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
        _debris = new ArrayList();
        _debrisProducerListeners = new ArrayList();
        
        _timeSinceLastDebrisGenerated = 0;
        _randomDebrisX = new Random();
        _randomDebrisY = new Random();
        
        // create a borehole when the drill is pressed
        _boreholeDrillListener = new BoreholeDrillListener() {
            public void drillAt( Point2D position ) {
                addBorehole( position );
            }
        };
        
        // borehole deletes itself
        _boreholeSelfDeletionListener = new BoreholeAdapter() {
            public void deleteMe( Borehole borehole ) {
                removeBorehole( borehole );
            }
        };
        
        // tool deletes itself
        _toolSelfDeletionListener = new ToolAdapter() {
            public void deleteMe( AbstractTool tool ) {
                removeTool( tool );
            }
        };
        
        // debris deletes itself
        _debrisSelfDeletionListener = new DebrisListener() {
            public void deleteMe( Debris debris ) {
                removeDebris( debris );
            }
        };
        
        _clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                _timeSinceLastDebrisGenerated += clockEvent.getSimulationTimeChange();
                if ( _timeSinceLastDebrisGenerated >= YEARS_PER_DEBRIS_GENERATED ) {
                    generateRandomDebris();
                    _timeSinceLastDebrisGenerated = 0;
                }
            }
        });
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
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( "AbstractModel.addTool " + tool.getClass().getName() );
        }
        if ( _tools.contains( tool ) ) {
            throw new IllegalStateException( "attempted to add tool twice: " + tool.getClass().getName() );
        }
        tool.addToolListener( _toolSelfDeletionListener );
        _tools.add( tool );
        _clock.addClockListener( tool );
        notifyToolAdded( tool );
    }
    
    public void removeTool( AbstractTool tool ) {
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( "AbstractModel.removeTool " + tool.getClass().getName() );
        }
        if ( !_tools.contains( tool ) ) {
            throw new IllegalStateException( "attempted to remove a tool that doesn't exist: " + tool.getClass().getName() );
        }
        if ( tool instanceof BoreholeDrill ) { //XXX
            ( (BoreholeDrill) tool ).removeBoreholeDrillListener( _boreholeDrillListener );
        }
        tool.removeToolListener( _toolSelfDeletionListener );
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
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( "AbstractModel.addBorehole" );
        }
        Borehole borehole = new Borehole( _glacier, position );
        borehole.addBoreholeListener( _boreholeSelfDeletionListener );
        _boreholes.add( borehole );
        _clock.addClockListener( borehole );
        notifyBoreholeAdded( borehole );
        return borehole;
    }
    
    public void removeBorehole( Borehole borehole ) {
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( "AbstractModel.removeBorehole" );
        }
        if ( !_boreholes.contains( borehole ) ) {
            throw new IllegalStateException( "attempted to remove a borehole that doesn't exist" );
        }
        borehole.removeBoreholeListener( _boreholeSelfDeletionListener );
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
    
    //----------------------------------------------------------------------------
    // IDebrisProducer
    //----------------------------------------------------------------------------
    
    public Debris addDebris( Point2D position ) {
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( "AbstractModel.addDebris" );
        }
        Debris debris = new Debris( position, _glacier );
        debris.addDebrisListener( _debrisSelfDeletionListener );
        _debris.add( debris );
        _clock.addClockListener( debris );
        notifyDebrisAdded( debris );
        return debris;
    }
    
    public void removeDebris( Debris debris ) {
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( "AbstractModel.removeDebris" );
        }
        if ( !_debris.contains( debris ) ) {
            throw new IllegalStateException( "attempted to remove debris that doesn't exist" );
        }
        debris.removeDebrisListener( _debrisSelfDeletionListener );
        _debris.remove( debris );
        _clock.removeClockListener( debris );
        notifyDebrisRemoved( debris );
        debris.cleanup();
    }
    
    public void removeAllDebris() {
        ArrayList debrisCopy = new ArrayList( _debris ); // iterate on a copy of the array
        Iterator i = debrisCopy.iterator();
        while ( i.hasNext() ) {
            removeDebris( (Debris) i.next() );
        }
    }
    
    public void addDebrisProducerListener( IDebrisProducerListener listener ) {
        _debrisProducerListeners.add( listener );
    }
    
    public void removeDebrisProducerListener( IDebrisProducerListener listener ) {
        _debrisProducerListeners.remove( listener );
    }
    
    private void notifyDebrisAdded( Debris debris ) {
        Iterator i = _debrisProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((IDebrisProducerListener)i.next()).debrisAdded( debris );
        }
    }
    
    private void notifyDebrisRemoved( Debris debris ) {
        Iterator i = _debrisProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((IDebrisProducerListener)i.next()).debrisRemoved( debris );
        }
    }
    
    private void generateRandomDebris() {
        if ( _glacier.getLength() > 0 ) {
            
            final double minX = _glacier.getValley().getHeadwallPositionReference().getX();
            Point2D surfaceAtSteadyStateELA = _glacier.getSurfaceAtSteadyStateELAReference();
            double maxX = 0;
            if ( surfaceAtSteadyStateELA != null ) {
                maxX = _glacier.getSurfaceAtSteadyStateELAReference().getX();
            }
            else {
                maxX = _glacier.getTerminusX();
            }
            final double x = minX + _randomDebrisX.nextDouble() * ( maxX - minX );
            
            final double minY = _glacier.getValley().getElevation( x ) + 1;
            final double maxY = _glacier.getSurfaceElevation( x ) - 1;
            final double y = minY + _randomDebrisY.nextDouble() * ( maxY - minY );
            
            addDebris( new Point2D.Double( x, y ) );
        }
    }

}
