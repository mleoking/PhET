// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;

/**
 * The handler for wolf frenzies in the natural selection model
 *
 * @author Jonathan Olson
 */
public class Frenzy implements NaturalSelectionClock.Listener {

    private double startTime;
    private double duration;
    private NaturalSelectionClock clock;
    private NaturalSelectionModel model;
    private ArrayList<Wolf> wolves;

    private boolean running = true;

    private ArrayList<Listener> listeners;

    private List<Bunny> targets;

    public Frenzy( NaturalSelectionModel model, double duration ) {
        this.model = model;
        this.duration = duration;

        listeners = new ArrayList<Listener>();

        clock = model.getClock();
        startTime = clock.getSimulationTime();
        clock.addPhysicalListener( this );

    }

    /**
     * Called after the model has initialized everything else for a frenzy. This creates the wolves, sets wolves up with
     * targets, etc.
     */
    public void init() {
        int wolfBase = NaturalSelectionConstants.getSettings().getWolfBase();
        int bunniesPerWolves = NaturalSelectionConstants.getSettings().getBunniesPerWolves();

        int pop = model.getPopulation();
        int numWolves = wolfBase + pop / bunniesPerWolves;

        initializeTargets();

        wolves = new ArrayList<Wolf>();
        for ( int i = 0; i < numWolves; i++ ) {
            final Wolf wolf = new Wolf( model, this );

            // if no wolves are killed this frenzy, don't let the wolves hunt
            if ( targets.isEmpty() ) {
                wolf.stopHunting();
            }
            wolves.add( wolf );
            notifyWolfCreate( wolf );
            wolf.addListener( new Wolf.Listener() {
                public void onEvent( Wolf.Event event ) {
                    if ( event.type == Wolf.Event.TYPE_KILLED_BUNNY ) {
                        onBunnyKilled( wolf, wolf.getTarget() );
                    }
                }
            } );
        }
    }

    /**
     * Figure out what bunnies the wolves should be hunting
     */
    private void initializeTargets() {
        // pull formula values from settings
        double bunnyOffset = NaturalSelectionConstants.getSettings().getWolfSelectionBunnyOffset();
        double bunnyExponent = NaturalSelectionConstants.getSettings().getWolfSelectionBunnyExponent();
        double scale = NaturalSelectionConstants.getSettings().getWolfSelectionScale();
        double blendScale = NaturalSelectionConstants.getSettings().getWolfSelectionBlendScale();
        double maxKillFraction = NaturalSelectionConstants.getSettings().getMaxKillFraction();

        targets = new LinkedList<Bunny>();

        double baseFraction = ( Math.pow( (double) model.getPopulation() + bunnyOffset, bunnyExponent ) ) * scale;

        for ( Bunny bunny : model.getAliveBunnyList() ) {
            double actualFraction = baseFraction;

            if (
                    ( bunny.getColorPhenotype() == ColorGene.WHITE_ALLELE && model.getClimate() == NaturalSelectionModel.CLIMATE_ARCTIC )
                    || ( bunny.getColorPhenotype() == ColorGene.BROWN_ALLELE && model.getClimate() == NaturalSelectionModel.CLIMATE_EQUATOR )
                    ) {
                actualFraction *= blendScale;
            }

            if ( actualFraction > maxKillFraction ) {
                actualFraction = maxKillFraction;
            }

            if ( Math.random() < actualFraction ) {
                targets.add( bunny );
            }

        }
    }

    private void onBunnyKilled( Wolf wolf, Bunny bunny ) {
        targets.remove( bunny );
        if ( targets.isEmpty() ) {
            for ( Wolf daWolf : wolves ) {
                daWolf.stopHunting();
            }
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
        return ( startTime + duration - clock.getSimulationTime() ) * 1000 / ( (double) NaturalSelectionConstants.getSettings().getClockFrameRate() );
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Get a new bunny as a target for a wolf. This should be called after a wolf kills its main target. Currently the
     * "closest" bunny is found as the wolf's target.
     * <p/>
     * If there are no more bunnies that were decided to be killed in initializeTargets(), then this will return null
     *
     * @param wolf The wolf to find a target for
     * @return A bunny to target, or null if there is no bunny to target
     */
    public Bunny getNewWolfTarget( Wolf wolf ) {
        if ( targets.isEmpty() ) {
            return null;
        }

        Bunny target = null;
        double distance = Double.POSITIVE_INFINITY;

        for ( Bunny bunny : targets ) {
            double pDistance = Point3D.distance( wolf.getPosition(), bunny.getPosition() );
            if ( pDistance < distance ) {
                target = bunny;
                distance = pDistance;
            }
        }

        return target;
    }

    /**
     * Called when time changes
     *
     * @param event The clock event
     */
    public void onTick( ClockEvent event ) {
        notifyFrenzyTimeLeft();

        if ( startTime + duration <= event.getSimulationTime() ) {
            // if the time is up on the frenzy, then end it
            endFrenzy();
        }
        else if ( targets.isEmpty() ) {
            // if there are no more bunnies targeted, and all of the wolves are "off-stage", then we should end early
            boolean onStage = false;
            for ( Wolf wolf : wolves ) {
                Point3D position = wolf.getPosition();
                double mx = model.getLandscape().getMaximumX( position.getZ() );
                if ( position.getX() < mx && position.getX() > -mx ) {
                    onStage = true;
                    break;
                }
            }
            if ( !onStage ) {
                endFrenzy();
            }
        }
    }


    /**
     * Does all of the necessary cleanup to end the frenzy in the model
     */
    public void endFrenzy() {
        if ( !running ) {
            return;
        }

        clock.removePhysicalListener( this );
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
