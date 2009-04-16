package edu.colorado.phet.naturalselection.module.naturalselection;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.model.NaturalSelectionClock;

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
        notifyGenerationChange();
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
    }

}
