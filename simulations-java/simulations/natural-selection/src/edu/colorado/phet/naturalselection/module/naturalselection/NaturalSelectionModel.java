/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.module.naturalselection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.*;

/**
 * The model itself for the Natural Selection simulation
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionModel extends ClockAdapter {

    // types of climates
    public static final int CLIMATE_EQUATOR = 0;
    public static final int CLIMATE_ARCTIC = 1;

    // types of selection factors
    public static final int SELECTION_NONE = 0;
    public static final int SELECTION_FOOD = 1;
    public static final int SELECTION_WOLVES = 2;

    /**
     * The simulation clock
     */
    private NaturalSelectionClock clock;

    /**
     * A list of all of the (model) bunnies.
     * WARNING: do NOT change the order of this list
     */
    private ArrayList bunnies;

    // starting bunnies
    private Bunny rootFather;
    private Bunny rootMother;

    private ArrayList listeners;

    /**
     * the last tick at which a year changed. When the difference between this and the actual tick is large enough,
     * another year will change
     */
    private double lastYearTick = 0;

    /**
     * The current generation
     */
    private int generation = 0;

    /**
     * Whether the user has pressed "Add a friend" yet
     */
    private boolean friendAdded = false;

    /**
     * The current climate (CLIMATE_EQUATOR or CLIMATE_ARCTIC)
     */
    private int climate = CLIMATE_EQUATOR;

    /**
     * The current selection factor (SELECTION_NONE, SELECTION_FOOD, or SELECTION_WOLVES)
     */
    private int selectionFactor = SELECTION_NONE;

    private static final Random random = new Random( System.currentTimeMillis() );

    /**
     * Constructor
     *
     * @param clock The simulation clock
     */
    public NaturalSelectionModel( NaturalSelectionClock clock ) {

        this.clock = clock;

        bunnies = new ArrayList();
        listeners = new ArrayList();

        // set up the genes to this module
        ColorGene.getInstance().setModel( this );
        TeethGene.getInstance().setModel( this );
        TailGene.getInstance().setModel( this );

        // create the starting bunnies
        rootFather = new Bunny( this, null, null, 0 );
        rootFather.notifyInit();
        bunnies.add( rootFather );
        clock.addClockListener( rootFather );

        this.clock.addClockListener( this );
    }

    /**
     * Reset the entire model
     */
    public void reset() {

        climate = NaturalSelectionDefaults.DEFAULT_CLIMATE;

        selectionFactor = NaturalSelectionDefaults.DEFAULT_SELECTION_FACTOR;

        friendAdded = false;

        generation = 0;

        lastYearTick = 0;

        bunnies = new ArrayList();

        ColorGene.getInstance().reset();
        TailGene.getInstance().reset();
        TeethGene.getInstance().reset();

        if ( NaturalSelectionDefaults.DEFAULT_NUMBER_OF_BUNNIES != 1 ) {
            throw new RuntimeException( "Number of starting bunnies must be 1, or this part should be changed" );
        }

        rootFather = new Bunny( this, null, null, 0 );
        rootFather.notifyInit();
        bunnies.add( rootFather );
        clock.addClockListener( rootFather );

        clock.resetSimulationTime();
        clock.start();

        notifyGenerationChange();

        initialize();
    }

    public void initialize() {
        notifyNewBunny( rootFather );
    }

    public void addFriend() {
        friendAdded = true;
        rootMother = new Bunny( this, null, null, 0 );
        rootMother.notifyInit();
        rootFather.setPotentialMate( rootMother );
        rootMother.setPotentialMate( rootFather );
        bunnies.add( rootMother );
        clock.addClockListener( rootMother );
        notifyNewBunny( rootMother );
    }

    /**
     * Causes another generation to be born (and everything else that happens when a new generation occurs)
     */
    private void nextGeneration() {
        ageBunnies();

        if ( !friendAdded ) {
            System.out.println( "Nothing to do, friend has not been added" );

            return;
        }

        mateBunnies();

        generation++;
        notifyGenerationChange();

        // make sure genes won't mutate anymore
        ColorGene.getInstance().setMutatable( false );
        TailGene.getInstance().setMutatable( false );
        TeethGene.getInstance().setMutatable( false );
    }

    /**
     * Causes all bunnies that can reproduce to do so
     */
    private void mateBunnies() {
        Iterator iter = bunnies.iterator();

        // temporarily store the new bunnies that we are creating
        ArrayList newBunnies = new ArrayList();

        while ( iter.hasNext() ) {
            Bunny bunny = (Bunny) iter.next();

            if ( bunny.canMate() ) {
                Bunny[] offspring = Bunny.mateBunnies( bunny, bunny.getPotentialMate() );
                for ( int i = 0; i < offspring.length; i++ ) {
                    newBunnies.add( offspring[i] );
                }
            }
        }

        // attempt to mutate a single bunny
        if ( newBunnies.size() != 0 ) {
            Bunny mutant = (Bunny) newBunnies.get( random.nextInt( newBunnies.size() ) );
            mutant.mutateMe();
        }

        Iterator newIter = newBunnies.iterator();
        while ( newIter.hasNext() ) {
            Bunny bunny = (Bunny) newIter.next();
            bunny.notifyInit();
            bunnies.add( bunny );
            clock.addClockListener( bunny );
            // TODO: possibly notify at the end for potential performance issues?
            notifyNewBunny( bunny );
        }
    }

    /**
     * Make all of the bunnies older
     */
    private void ageBunnies() {
        Iterator iter = bunnies.iterator();
        while ( iter.hasNext() ) {
            ( (Bunny) iter.next() ).ageMe();
        }
    }

    /**
     * Bunnies will run out of food if the selection factor is food
     */
    private void bunnyFamine() {
        Iterator iter = bunnies.iterator();

        double baseFraction = ( Math.sqrt( (double) getPopulation() ) - 3 ) / ( NaturalSelectionDefaults.TICKS_PER_YEAR * 15 );

        while ( iter.hasNext() ) {
            Bunny bunny = (Bunny) iter.next();

            if ( !bunny.isAlive() ) {
                continue;
            }

            double actualFraction = baseFraction;

            if ( bunny.getTeethPhenotype() == TeethGene.TEETH_HUGE_ALLELE ) {
                actualFraction /= 2;
            }

            if ( Math.random() < actualFraction ) {
                bunny.die();
            }

        }
    }

    private void invisibleWolfAttack() {
        Iterator iter = bunnies.iterator();

        double baseFraction = ( Math.sqrt( (double) getPopulation() ) - 3 ) / ( NaturalSelectionDefaults.TICKS_PER_YEAR * 10 );

        while ( iter.hasNext() ) {
            Bunny bunny = (Bunny) iter.next();

            if ( !bunny.isAlive() ) {
                continue;
            }

            double actualFraction = baseFraction;

            if (
                    ( bunny.getColorPhenotype() == ColorGene.WHITE_ALLELE && climate == CLIMATE_ARCTIC )
                    || ( bunny.getColorPhenotype() == ColorGene.BROWN_ALLELE && climate == CLIMATE_EQUATOR )
                    ) {
                actualFraction /= 4;
            }

            if ( Math.random() < actualFraction ) {
                bunny.die();
            }

        }
    }

    //----------------------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------------------

    public NaturalSelectionClock getClock() {
        return clock;
    }

    public int getPopulation() {
        // TODO: easier way? maybe count a filtered ArrayList?
        int ret = 0;
        for ( int i = 0; i < bunnies.size(); i++ ) {
            Bunny bunny = (Bunny) bunnies.get( i );
            if ( bunny.isAlive() ) {
                ret++;
            }
        }
        return ret;
    }

    public int getGeneration() {
        return generation;
    }

    public ArrayList getBunnyList() {
        return bunnies;
    }

    /**
     * Get only alive bunnies
     *
     * @return A list of bunnies that are alive
     */
    public ArrayList getAliveBunnyList() {
        ArrayList ret = new ArrayList();
        Iterator iter = bunnies.iterator();
        while ( iter.hasNext() ) {
            Bunny bunny = (Bunny) iter.next();
            if ( bunny.isAlive() ) {
                ret.add( bunny );
            }
        }
        return ret;
    }

    /**
     * Get a list of all bunnies that were born in the desired generation
     *
     * @param desiredGeneration The generation that bunnies were born in.
     * @return A list of all bunnies (alive or dead) born in desiredGeneration
     */
    public ArrayList getBunnyGenerationList( int desiredGeneration ) {
        ArrayList ret = new ArrayList();
        Iterator iter = bunnies.iterator();
        while ( iter.hasNext() ) {
            Bunny bunny = (Bunny) iter.next();
            if ( bunny.getGeneration() == desiredGeneration ) {
                ret.add( bunny );
            }
        }
        return ret;
    }

    public int getClimate() {
        return climate;
    }

    public void setClimate( int _climate ) {
        if ( climate == _climate ) {
            return;
        }

        climate = _climate;

        notifyClimateChange();
    }

    public int getSelectionFactor() {
        return selectionFactor;
    }

    public void setSelectionFactor( int _selectionFactor ) {
        if ( selectionFactor == _selectionFactor ) {
            return;
        }

        selectionFactor = _selectionFactor;

        notifySelectionFactorChange();
    }

    public Bunny getRootFather() {
        return rootFather;
    }

    public Bunny getRootMother() {
        return rootMother;
    }

    public boolean isFriendAdded() {
        return friendAdded;
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    public void simulationTimeChanged( ClockEvent event ) {
        if ( selectionFactor == SELECTION_FOOD ) {
            bunnyFamine();
        }

        if ( selectionFactor == SELECTION_WOLVES ) {
            invisibleWolfAttack();
        }

        while ( event.getSimulationTime() - lastYearTick > NaturalSelectionDefaults.TICKS_PER_YEAR ) {
            lastYearTick += NaturalSelectionDefaults.TICKS_PER_YEAR;
            nextGeneration();
        }
    }


    //----------------------------------------------------------------------------
    // Notifications
    //----------------------------------------------------------------------------

    private void notifyGenerationChange() {
        Iterator iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( (NaturalSelectionModelListener) iter.next() ).onGenerationChange( generation );
        }
    }

    private void notifyNewBunny( Bunny bunny ) {
        Iterator iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( (NaturalSelectionModelListener) iter.next() ).onNewBunny( bunny );
        }
    }

    private void notifyClimateChange() {
        Iterator iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( (NaturalSelectionModelListener) iter.next() ).onClimateChange( climate );
        }
    }

    private void notifySelectionFactorChange() {
        Iterator iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( (NaturalSelectionModelListener) iter.next() ).onSelectionFactorChange( selectionFactor );
        }
    }

    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------

    public void addListener( NaturalSelectionModelListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( NaturalSelectionModelListener listener ) {
        listeners.remove( listener );
    }

    /**
     * The interface for objects that wish to receive model change events
     */
    public interface NaturalSelectionModelListener {
        // shortcut: implemented in SpritesNode, NaturalSelectionBackgroundNode, PedigreeNode

        /**
         * Called when the generation changes
         *
         * @param generation The new generation number
         */
        public void onGenerationChange( int generation );

        /**
         * Called when a new bunny is born
         *
         * @param bunny The bunny
         */
        public void onNewBunny( Bunny bunny );

        /**
         * Called when the climate changes
         *
         * @param climate The new climate
         */
        public void onClimateChange( int climate );

        /**
         * Called when the selection factor changes
         *
         * @param selectionFactor The new selection factor
         */
        public void onSelectionFactorChange( int selectionFactor );
    }

}
