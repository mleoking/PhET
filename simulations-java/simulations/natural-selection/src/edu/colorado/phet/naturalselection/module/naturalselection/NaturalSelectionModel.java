package edu.colorado.phet.naturalselection.module.naturalselection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.model.NaturalSelectionClock;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;

public class NaturalSelectionModel extends ClockAdapter {

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

    public NaturalSelectionModel( NaturalSelectionClock _clock ) {

        clock = _clock;

        bunnies = new ArrayList();
        listeners = new ArrayList();

        rootFather = new Bunny( null, null );
        rootMother = new Bunny( null, null );
        bunnies.add( rootFather );
        bunnies.add( rootMother );

        clock.addClockListener( this );
    }

    public void reset() {

        currentMonth = 0;
        generation = 0;

        lastMonthTick = 0;

        bunnies = new ArrayList();

        rootFather = new Bunny( null, null );
        rootMother = new Bunny( null, null );
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

    private void nextGeneration() {
        generation++;

        ageBunnies();

        mateBunnies();

        notifyGenerationChange();
    }

    private void mateBunnies() {
        // randomize the bunnies
        Collections.shuffle( bunnies );
        Iterator iter = bunnies.iterator();

        ArrayList newBunnies = new ArrayList();

        while( iter.hasNext() ) {
            // get the next two parents
            Bunny father = (Bunny) iter.next();
            if( father.getAge() >= NaturalSelectionConstants.BUNNIES_STERILE_WHEN_THIS_OLD ) { continue; }

            while( iter.hasNext() ) {
                Bunny mother = (Bunny) iter.next();
                if( mother.getAge() >= NaturalSelectionConstants.BUNNIES_STERILE_WHEN_THIS_OLD ) { continue; }

                Bunny[] bunnyArray = Bunny.mateBunnies( father, mother );

                for( int i = 0; i < bunnyArray.length; i++ ) {
                    newBunnies.add( bunnyArray[i] );
                }
            }
        }



        Iterator newIter = newBunnies.iterator();
        while( newIter.hasNext() ) {
            Bunny bunny = (Bunny) newIter.next();
            bunnies.add( bunny );
            notifyNewBunny( bunny );
        }
    }

    private void ageBunnies() {
        Iterator iter = bunnies.iterator();
        while( iter.hasNext() ) {
            ( (Bunny) iter.next() ).ageMe();
        }
    }

    private void nextMonth() {
        currentMonth++;
        if ( currentMonth >= 12 ) {
            currentMonth = 0;
        }
        notifyMonthChange();
        if( getCurrentMonth().equals( "August" ) ) {
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


    // listeners

    public void addListener( NaturalSelectionModelListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( NaturalSelectionModelListener listener ) {
        listeners.remove( listener );
    }

    public interface NaturalSelectionModelListener {
        public void onMonthChange( String monthName );
        public void onGenerationChange( int generation );
        public void onNewBunny( Bunny bunny );
    }

}
