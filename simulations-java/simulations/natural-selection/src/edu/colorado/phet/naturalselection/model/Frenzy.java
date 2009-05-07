package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

public class Frenzy extends ClockAdapter {

    private double startTime;
    private double duration;
    private NaturalSelectionClock clock;
    private NaturalSelectionModel model;
    private ArrayList wolves;
    private ArrayList targetedBunnies;

    private boolean running = true;

    private ArrayList listeners;

    public Frenzy( NaturalSelectionModel model, double duration ) {
        this.model = model;
        this.duration = duration;

        listeners = new ArrayList();

        clock = model.getClock();
        startTime = clock.getSimulationTime();
        clock.addClockListener( this );

        targetedBunnies = new ArrayList();

    }

    public void init() {
        int numWolves = 4 + model.getPopulation() / 6;
        wolves = new ArrayList();
        for ( int i = 0; i < numWolves; i++ ) {
            Wolf wolf = new Wolf( model, this );
            wolves.add( wolf );
            notifyWolfCreate( wolf );
        }
    }

    public ArrayList getWolves() {
        return wolves;
    }

    /**
     * Get the time left
     *
     * @return Time left for the frenzy in milliseconds
     */
    public double getTimeLeft() {
        return ( startTime + duration - clock.getSimulationTime() ) * 1000 / ( (double) NaturalSelectionDefaults.CLOCK_FRAME_RATE );
    }

    public boolean isRunning() {
        return running;
    }

    public void simulationTimeChanged( ClockEvent event ) {
        notifyFrenzyTimeLeft();

        if ( startTime + duration <= event.getSimulationTime() ) {
            endFrenzy();
        }

    }

    /**
     * Does all of the necessary cleanup to end the frenzy in the model
     */
    public void endFrenzy() {
        if ( !running ) {
            return;
        }

        clock.removeClockListener( this );
        model.endFrenzy();

        running = false;

        Iterator bunnyIter = targetedBunnies.iterator();
        while ( bunnyIter.hasNext() ) {
            ( (Bunny) bunnyIter.next() ).setTargeted( false );
        }

        for ( Iterator iterator = wolves.iterator(); iterator.hasNext(); ) {
            Wolf wolf = (Wolf) iterator.next();
            wolf.disable();
        }

        notifyFrenzyStop();
    }

    //----------------------------------------------------------------------------
    // Notifiers
    //----------------------------------------------------------------------------

    private void notifyFrenzyStop() {
        Iterator iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( (Listener) iter.next() ).onFrenzyStop( this );
        }
    }

    private void notifyFrenzyTimeLeft() {
        Iterator iter = listeners.iterator();
        double timeLeft = getTimeLeft();
        while ( iter.hasNext() ) {
            ( (Listener) iter.next() ).onFrenzyTimeLeft( timeLeft );
        }
    }

    private void notifyWolfCreate( Wolf wolf ) {
        Iterator iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( (Listener) iter.next() ).onWolfCreate( wolf );
        }
    }


    public void addTargetBunny( Bunny bunny ) {
        targetedBunnies.add( bunny );
    }

    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public static interface Listener {
        public void onFrenzyStop( Frenzy frenzy );

        public void onFrenzyTimeLeft( double timeLeft );

        public void onWolfCreate( Wolf wolf );
    }

}
