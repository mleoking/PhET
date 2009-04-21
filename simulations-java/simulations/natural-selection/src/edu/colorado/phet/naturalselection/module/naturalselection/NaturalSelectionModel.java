/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.module.naturalselection;

import java.util.ArrayList;
import java.util.Iterator;

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
     * the last tick at which a month changed. When the difference between this and the actual tick is large enough,
     * another month will change
     */
    private double lastMonthTick = 0;

    /**
     * The current month name, as an index into MONTH_NAMES
     */
    private int currentMonth = 0;
    public static final String[] MONTH_NAMES = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    /**
     * The current generation
     */
    private int generation = 0;

    /**
     * The current climate (CLIMATE_EQUATOR or CLIMATE_ARCTIC)
     */
    private int climate = CLIMATE_EQUATOR;

    /**
     * The current selection factor (SELECTION_NONE, SELECTION_FOOD, or SELECTION_WOLVES)
     */
    private int selectionFactor = SELECTION_NONE;

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
        rootFather = new Bunny( null, null, 0 );
        rootMother = new Bunny( null, null, 0 );
        rootFather.setPotentialMate( rootMother );
        rootMother.setPotentialMate( rootFather );
        bunnies.add( rootFather );
        bunnies.add( rootMother );

        this.clock.addClockListener( this );
    }

    /**
     * Reset the entire model
     */
    public void reset() {

        climate = CLIMATE_EQUATOR;

        currentMonth = 0;
        generation = 0;

        lastMonthTick = 0;

        bunnies = new ArrayList();

        ColorGene.getInstance().reset();
        TailGene.getInstance().reset();
        TeethGene.getInstance().reset();

        if ( NaturalSelectionDefaults.DEFAULT_NUMBER_OF_BUNNIES != 2 ) {
            throw new RuntimeException( "Number of starting bunnies must be 2, or this part should be changed" );
        }

        rootFather = new Bunny( null, null, 0 );
        rootMother = new Bunny( null, null, 0 );
        rootFather.setPotentialMate( rootMother );
        rootMother.setPotentialMate( rootFather );
        bunnies.add( rootFather );
        bunnies.add( rootMother );

        clock.resetSimulationTime();
        clock.start();

        notifyMonthChange();
        notifyGenerationChange();

        initialize();
    }

    public void initialize() {
        notifyNewBunny( rootFather );
        notifyNewBunny( rootMother );
    }


    /**
     * Causes another generation to be born (and everything else that happens when a new generation occurs)
     */
    private void nextGeneration() {
        System.out.println( "***** Mating season, creating next generation" );

        generation++;

        ageBunnies();

        mateBunnies();

        notifyGenerationChange();

        System.out.println( "***** End mating season, stats to follow:" );

        System.out.println( "\tPopulation: " + getPopulation() );
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

        Iterator newIter = newBunnies.iterator();
        while ( newIter.hasNext() ) {
            Bunny bunny = (Bunny) newIter.next();
            bunnies.add( bunny );
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
     * Increment a month. If applicable, start a new generation.
     */
    private void nextMonth() {
        currentMonth++;
        if ( currentMonth >= 12 ) {
            currentMonth = 0;
        }
        notifyMonthChange();
        if ( currentMonth == NaturalSelectionDefaults.GENERATION_MONTH ) {
            nextGeneration();
        }
    }

    //----------------------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------------------

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

    public String getCurrentMonth() {
        return MONTH_NAMES[currentMonth];
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


    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    public void simulationTimeChanged( ClockEvent event ) {
        while ( event.getSimulationTime() - lastMonthTick > NaturalSelectionDefaults.TICKS_PER_MONTH ) {
            lastMonthTick += NaturalSelectionDefaults.TICKS_PER_MONTH;
            nextMonth();
        }
    }


    //----------------------------------------------------------------------------
    // Notifications
    //----------------------------------------------------------------------------

    private void notifyMonthChange() {
        Iterator iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( (NaturalSelectionModelListener) iter.next() ).onMonthChange( MONTH_NAMES[currentMonth] );
        }
    }

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
        // shortcut: implemented in PopulationCanvas, TimeDisplayPanel, SpritesNode, NaturalSelectionBackgroundNode

        /**
         * Called when the month changes
         *
         * @param monthName The new month name
         */
        public void onMonthChange( String monthName );

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
