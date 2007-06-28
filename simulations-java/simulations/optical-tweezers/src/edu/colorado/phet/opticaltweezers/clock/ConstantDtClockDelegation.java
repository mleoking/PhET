/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.clock;

import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.*;

/**
 * ConstantDtClockDelegation is a clock with a constant dt.
 * This implementation delegates most of its work to SwingClock.
 * Changes to delay and dt are handled via a new listener/event pair.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConstantDtClockDelegation implements IClock {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final SwingClock swingClock;
    private EventListenerList listenerList;
    private ClockEventRemapper eventRemapper;
    
    //----------------------------------------------------------------------------
    //  Constructors
    //----------------------------------------------------------------------------
    
    public ConstantDtClockDelegation( int delay, double dt ) {
        super();
        swingClock = new SwingClock( delay, new TimingStrategy.Constant( dt ) );
        listenerList = new EventListenerList();
        eventRemapper = new ClockEventRemapper( this );
        swingClock.addClockListener( eventRemapper );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setDelay( int delay ) {
        if ( delay != getDelay() ) {
            swingClock.setDelay( delay );
            fireDelayChanged( new ConstantDtClockEvent( this ) );
        }
    }
    
    public int getDelay() {
        return swingClock.getDelay();
    }
    
    public void setDet( double dt ) {
        if ( dt != getDt() ) {
            swingClock.setTimingStrategy( new TimingStrategy.Constant( dt ) );
            fireDtChanged( new ConstantDtClockEvent( this ) );
        }
    }
    
    public double getDt() {
        return ((TimingStrategy.Constant) swingClock.getTimingStrategy()).getSimulationTimeChange( 0, 0 );
    }
    
    public void setRunning( boolean running ) {
        if ( running ) {
            start();
        }
        else {
            pause();
        }
    }

    public void setPaused( boolean paused ) {
        setRunning( !paused );
    }
    
    //----------------------------------------------------------------------------
    // ConstantDtClockListener
    //----------------------------------------------------------------------------
    
    public interface ConstantDtClockListener extends EventListener {
        
        public void delayChanged( ConstantDtClockEvent event );
        
        public void dtChanged( ConstantDtClockEvent event );
    }
    
    public static class ConstantDtClockAdapter implements ConstantDtClockListener {
        
        public void delayChanged( ConstantDtClockEvent event ) {};
        
        public void dtChanged( ConstantDtClockEvent event ) {};
    }
    
    public static class ConstantDtClockEvent extends EventObject {

        public ConstantDtClockEvent( ConstantDtClockDelegation source ) {
            super( source );
        }
        
        public ConstantDtClockDelegation getClock() {
            return (ConstantDtClockDelegation) getSource();
        }
    }
    
    public void addConstantDtClockListener( ConstantDtClockListener listener ) {
        listenerList.add( ConstantDtClockListener.class, listener );
    }
    
    public void removeConstantDtClockListener( ConstantDtClockListener listener ) {
        listenerList.remove( ConstantDtClockListener.class, listener );
    }
    
    private void fireDelayChanged( ConstantDtClockEvent event ) {
        Object[] listeners = listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ConstantDtClockListener.class ) {
                ( (ConstantDtClockListener)listeners[i + 1] ).delayChanged( event );
            }
        }
    }
    
    private void fireDtChanged( ConstantDtClockEvent event ) {
        Object[] listeners = listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ConstantDtClockListener.class ) {
                ( (ConstantDtClockListener)listeners[i + 1] ).dtChanged( event );
            }
        }
    }

    //----------------------------------------------------------------------------
    // IClock implementation, delegated to SwingClock
    //----------------------------------------------------------------------------
    
    public void start() {
        swingClock.start();
    }
    
    public void pause() {
        swingClock.pause();
    }

    public boolean isPaused() {
        return swingClock.isPaused();
    }

    public boolean isRunning() {
        return swingClock.isRunning();
    }

    public double getSimulationTime() {
        return swingClock.getSimulationTime();
    }

    public double getSimulationTimeChange() {
        return swingClock.getSimulationTimeChange();
    }

    public long getWallTime() {
        return swingClock.getWallTime();
    }

    public long getWallTimeChange() {
        return swingClock.getWallTimeChange();
    }

    public void resetSimulationTime() {
        swingClock.resetSimulationTime();
    }

    public void setSimulationTime( double simulationTime ) {
        swingClock.setSimulationTime( simulationTime );
    }

    public void stepClockWhilePaused() {
        swingClock.stepClockWhilePaused();
    }
    
    public void addClockListener( ClockListener clockListener ) {
        eventRemapper.addClockListener( clockListener );
    }
    
    public void removeClockListener( ClockListener clockListener ) {
        eventRemapper.removeClockListener( clockListener );
    }
    
    //----------------------------------------------------------------------------
    // IClock implementation, delegated to SwingClock
    //----------------------------------------------------------------------------
    
    /*
     * Listens for changes to the swingClock delegate,
     * then remaps the event's clock to the actual clock.
     */
    private static class ClockEventRemapper implements ClockListener {
        
        private ConstantDtClockDelegation newClock;
        private EventListenerList listenerList;

        public ClockEventRemapper( ConstantDtClockDelegation newClock ) {
            super();
            this.newClock = newClock;
        }
        
        public void clockPaused( ClockEvent clockEvent ) {
            fireClockPaused( new ClockEvent( newClock ) );
        }

        public void clockStarted( ClockEvent clockEvent ) {
            fireClockStarted( new ClockEvent( newClock ) );
        }

        public void clockTicked( ClockEvent clockEvent ) {
            fireClockTicked( new ClockEvent( newClock ) );
        }

        public void simulationTimeChanged( ClockEvent clockEvent ) {
            fireSimulationTimeChanged( new ClockEvent( newClock ) );
        }

        public void simulationTimeReset( ClockEvent clockEvent ) {
            fireSimulationTimeReset( new ClockEvent( newClock ) );
        }
        
        public void addClockListener( ClockListener clockListener ) {
            listenerList.add( ClockListener.class, clockListener );
        }
        
        public void removeClockListener( ClockListener clockListener ) {
            listenerList.remove( ClockListener.class, clockListener );
        }
        
        private void fireClockPaused( ClockEvent event ) {
            Object[] listeners = listenerList.getListenerList();
            for( int i = 0; i < listeners.length; i += 2 ) {
                if( listeners[i] == ClockListener.class ) {
                    ( (ClockListener)listeners[i + 1] ).clockPaused( event );
                }
            }
        }
        
        private void fireClockStarted( ClockEvent event ) {
            Object[] listeners = listenerList.getListenerList();
            for( int i = 0; i < listeners.length; i += 2 ) {
                if( listeners[i] == ClockListener.class ) {
                    ( (ClockListener)listeners[i + 1] ).clockStarted( event );
                }
            }
        }
        
        private void fireClockTicked( ClockEvent event ) {
            Object[] listeners = listenerList.getListenerList();
            for( int i = 0; i < listeners.length; i += 2 ) {
                if( listeners[i] == ClockListener.class ) {
                    ( (ClockListener)listeners[i + 1] ).clockTicked( event );
                }
            }
        }
        
        private void fireSimulationTimeChanged( ClockEvent event ) {
            Object[] listeners = listenerList.getListenerList();
            for( int i = 0; i < listeners.length; i += 2 ) {
                if( listeners[i] == ClockListener.class ) {
                    ( (ClockListener)listeners[i + 1] ).simulationTimeChanged( event );
                }
            }
        }
        
        private void fireSimulationTimeReset( ClockEvent event ) {
            Object[] listeners = listenerList.getListenerList();
            for( int i = 0; i < listeners.length; i += 2 ) {
                if( listeners[i] == ClockListener.class ) {
                    ( (ClockListener)listeners[i + 1] ).simulationTimeReset( event );
                }
            }
        }
    }
}
