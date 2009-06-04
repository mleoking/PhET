package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;

public class Frenzy extends ClockAdapter {

    private double startTime;
    private double duration;
    private NaturalSelectionClock clock;
    private NaturalSelectionModel model;
    private ArrayList<Wolf> wolves;
    private ArrayList<Bunny> targetedBunnies;

    private boolean running = true;

    private ArrayList<Listener> listeners;

    private static final Random random = new Random( System.currentTimeMillis() );

    public Frenzy( NaturalSelectionModel model, double duration ) {
        this.model = model;
        this.duration = duration;

        listeners = new ArrayList<Listener>();

        clock = model.getClock();
        startTime = clock.getSimulationTime();
        clock.addClockListener( this );

        targetedBunnies = new ArrayList<Bunny>();

    }

    public void init() {
        int numWolves = 4 + model.getPopulation() / 6;
        wolves = new ArrayList<Wolf>();
        for ( int i = 0; i < numWolves; i++ ) {
            Wolf wolf = new Wolf( model, this );
            wolves.add( wolf );
            notifyWolfCreate( wolf );
        }
    }

    public ArrayList<Wolf> getWolves() {
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

    public Bunny getNewWolfTarget( Wolf wolf ) {
        List<Bunny> bunnies = model.getAliveBunnyList();
        if ( bunnies.isEmpty() ) {
            return null;
        }
        int index = 0;
        for ( int i = 0; i < 10; i++ ) {
            index = random.nextInt( bunnies.size() );
            Allele color = bunnies.get( index ).getColorPhenotype();
            if ( color == ColorGene.WHITE_ALLELE && model.getClimate() == NaturalSelectionModel.CLIMATE_EQUATOR ) {
                break;
            }
            else if ( color == ColorGene.BROWN_ALLELE && model.getClimate() == NaturalSelectionModel.CLIMATE_ARCTIC ) {
                break;
            }
        }
        return bunnies.get( index );
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

        for ( Iterator<Wolf> iterator = wolves.iterator(); iterator.hasNext(); ) {
            Wolf wolf = iterator.next();
            wolf.disable();
        }

        notifyFrenzyStop();
    }

    //----------------------------------------------------------------------------
    // Notifiers
    //----------------------------------------------------------------------------

    private void notifyFrenzyStop() {
        Iterator<Listener> iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( iter.next() ).onFrenzyStop( this );
        }
    }

    private void notifyFrenzyTimeLeft() {
        Iterator<Listener> iter = listeners.iterator();
        double timeLeft = getTimeLeft();
        while ( iter.hasNext() ) {
            ( iter.next() ).onFrenzyTimeLeft( timeLeft );
        }
    }

    private void notifyWolfCreate( Wolf wolf ) {
        Iterator<Listener> iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( iter.next() ).onWolfCreate( wolf );
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
