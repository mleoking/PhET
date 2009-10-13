package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;

/**
 * The handler for food famines in the natural selection model
 *
 * @author Jonathan Olson
 */
public class Famine implements NaturalSelectionClock.Listener {

    private double startTime;
    private double duration;
    private NaturalSelectionClock clock;
    private NaturalSelectionModel model;

    private boolean running = true;

    private ArrayList<Listener> listeners;

    private List<Bunny> targets = new LinkedList<Bunny>();
    private int totalTargets;

    public Famine( NaturalSelectionModel model, double duration ) {
        this.model = model;
        this.duration = duration;

        listeners = new ArrayList<Listener>();

        clock = model.getClock();
        startTime = clock.getSimulationTime();
        clock.addPhysicalListener( this );

    }

    /**
     * Called after the model has initialized everything else for a famine.
     */
    public void init() {
        initializeTargets();
    }

    /**
     * Figure out what bunnies the wolves should be hunting
     */
    private void initializeTargets() {
        // load formula variables
        double bunnyOffset = NaturalSelectionConstants.getSettings().getFoodSelectionBunnyOffset();
        double bunnyExponent = NaturalSelectionConstants.getSettings().getFoodSelectionBunnyExponent();
        double scale = NaturalSelectionConstants.getSettings().getFoodSelectionScale();
        double blendScale = NaturalSelectionConstants.getSettings().getFoodSelectionBlendScale();
        double maxKillFraction = NaturalSelectionConstants.getSettings().getMaxKillFraction();

        Iterator<Bunny> iter = model.getBunnyList().iterator();

        double baseFraction = ( Math.pow( (double) model.getPopulation() + bunnyOffset, bunnyExponent ) ) * scale;

        while ( iter.hasNext() ) {
            Bunny bunny = iter.next();

            if ( !bunny.isAlive() ) {
                continue;
            }

            double actualFraction = baseFraction;

            if ( bunny.getTeethPhenotype() == TeethGene.TEETH_LONG_ALLELE ) {
                actualFraction *= blendScale;
            }

            if ( actualFraction > maxKillFraction ) {
                actualFraction = maxKillFraction;
            }

            if ( Math.random() < actualFraction ) {
                targets.add( bunny );
            }

        }

        totalTargets = targets.size();
    }

    public double getTimeLeft() {
        return ( startTime + duration - clock.getSimulationTime() ) * 1000 / ( (double) NaturalSelectionConstants.getSettings().getClockFrameRate() );
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Called when time changes
     *
     * @param event The clock event
     */
    public void onTick( ClockEvent event ) {
        notifyFamineTimeLeft();

        if ( startTime + duration <= event.getSimulationTime() ) {
            // if the time is up on the frenzy, then end it
            endFamine();
        }
        else if ( targets.isEmpty() ) {
            endFamine();
        }

        double ratioDone = 1 - ( duration - ( event.getSimulationTime() - startTime ) ) / duration;

        int bunniesDead = ( (int) ( ratioDone * totalTargets ) ) + 1;
        int bunniesAlive = totalTargets - bunniesDead;

        while ( !targets.isEmpty() && targets.size() > bunniesAlive ) {
            Bunny bunny = targets.get( 0 );
            bunny.die();
            targets.remove( bunny );
        }
    }


    /**
     * Does all of the necessary cleanup to end the famine in the model
     */
    public void endFamine() {
        if ( !running ) {
            return;
        }

        clock.removePhysicalListener( this );
        model.endFamine();

        running = false;

        notifyFamineStop();
    }

    //----------------------------------------------------------------------------
    // Notifiers
    //----------------------------------------------------------------------------

    private void notifyFamineStop() {
        Iterator<Listener> iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( iter.next() ).onFamineStop( this );
        }
    }

    private void notifyFamineTimeLeft() {
        Iterator<Listener> iter = listeners.iterator();
        double timeLeft = getTimeLeft();
        while ( iter.hasNext() ) {
            ( iter.next() ).onFamineTimeLeft( timeLeft );
        }
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
        public void onFamineStop( Famine frenzy );

        public void onFamineTimeLeft( double timeLeft );
    }

}