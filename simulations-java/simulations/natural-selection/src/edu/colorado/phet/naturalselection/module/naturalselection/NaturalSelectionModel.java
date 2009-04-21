package edu.colorado.phet.naturalselection.module.naturalselection;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.model.*;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;

public class NaturalSelectionModel extends ClockAdapter {

    public static final int CLIMATE_EQUATOR = 0;
    public static final int CLIMATE_ARCTIC = 1;

    public static final int SELECTION_NONE = 0;
    public static final int SELECTION_FOOD = 1;
    public static final int SELECTION_WOLVES = 2;

    private NaturalSelectionClock clock;

    private ArrayList bunnies;
    private Bunny rootFather;
    private Bunny rootMother;

    private ArrayList listeners;

    public static double TICKS_PER_MONTH = 25.0;

    private double lastMonthTick = 0;

    private int currentMonth = 0;
    private static String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    private int generation = 0;

    private int climate = CLIMATE_EQUATOR;
    private int selectionFactor = SELECTION_NONE;

    public NaturalSelectionModel( NaturalSelectionClock _clock ) {

        clock = _clock;

        bunnies = new ArrayList();
        listeners = new ArrayList();

        ColorGene.getInstance().setModel( this );
        TeethGene.getInstance().setModel( this );
        TailGene.getInstance().setModel( this );

        rootFather = new Bunny( null, null, 0 );
        rootMother = new Bunny( null, null, 0 );
        rootFather.setPotentialMate( rootMother );
        rootMother.setPotentialMate( rootFather );
        bunnies.add( rootFather );
        bunnies.add( rootMother );

        clock.addClockListener( this );
    }

    public void reset() {

        climate = CLIMATE_EQUATOR;

        currentMonth = 0;
        generation = 0;

        lastMonthTick = 0;

        bunnies = new ArrayList();

        ColorGene.getInstance().reset();
        TailGene.getInstance().reset();
        TeethGene.getInstance().reset();

        if( NaturalSelectionDefaults.DEFAULT_NUMBER_OF_BUNNIES != 2 ) {
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

    private void nextGeneration() {
        System.out.println( "***** Mating season, creating next generation" );

        generation++;

        ageBunnies();

        mateBunnies();

        notifyGenerationChange();

        System.out.println( "***** End mating season, stats to follow:" );

        System.out.println( "\tPopulation: " + getPopulation() );
    }

    private void mateBunnies() {
        Iterator iter = bunnies.iterator();

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

    private void ageBunnies() {
        Iterator iter = bunnies.iterator();
        while ( iter.hasNext() ) {
            ( (Bunny) iter.next() ).ageMe();
        }
    }

    private void nextMonth() {
        currentMonth++;
        if ( currentMonth >= 12 ) {
            currentMonth = 0;
        }
        notifyMonthChange();
        if ( getCurrentMonth().equals( "August" ) ) {
            nextGeneration();
        }
    }

    public void simulationTimeChanged( ClockEvent event ) {
        //System.out.println( event.getSimulationTime() - lastMonthTick );
        while ( event.getSimulationTime() - lastMonthTick > TICKS_PER_MONTH ) {
            lastMonthTick += TICKS_PER_MONTH;
            nextMonth();
        }
    }

    public String getCurrentMonth() {
        return monthNames[currentMonth];
    }

    public ArrayList getBunnyList() {
        return bunnies;
    }

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


    // notification

    private void notifyMonthChange() {
        Iterator iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( (NaturalSelectionModelListener) iter.next() ).onMonthChange( monthNames[currentMonth] );
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

    // listeners

    public void addListener( NaturalSelectionModelListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( NaturalSelectionModelListener listener ) {
        listeners.remove( listener );
    }

    public interface NaturalSelectionModelListener {
        // shortcut: implemented in PopulationCanvas, TimeDisplayPanel, SpritesNode, NaturalSelectionBackgroundNode

        public void onMonthChange( String monthName );

        public void onGenerationChange( int generation );

        public void onNewBunny( Bunny bunny );

        public void onClimateChange( int climate );

        public void onSelectionFactorChange( int selectionFactor );
    }

}
