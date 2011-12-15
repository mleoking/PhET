// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Instead of filling up in-order, the user can choose to randomly fill the containers.
 * This is a complex feature, and will take some explaining:
 * 1. When the numerator increases, a new piece is added randomly to an open site
 * 2. When the numerator decreases, the previously added piece is removed (or if not in history, then a random piece is removed from the rightmost container)
 * 3. Changing the denominator re-randomizes everything.
 * 4. Containers still fill up from the left, it is only the rightmost one that gets randomized (SR not sure he likes this one).
 * <p/>
 * The best analogy of how it should be have is that it's like a browser history, when you go back it is always where you came from (even for a long path), but when going forward (adding a piece) you can always go somewhere different.
 *
 * @author Sam Reid
 */
public class RandomFill {
    private ArrayList<State> history = new ArrayList<State>();
    private int numerator = 1;
    private int denominator = 1;

    @Override public String toString() {
        return "numerator = " + numerator + ", denominator = " + denominator + ", history = " + history;
    }

    public RandomFill() {
        history.add( new State( numerator, denominator, new Container[] { new Container( 1, new int[] { 0 } ) } ) );
    }

    public State getCurrentState() {
        if ( history.size() > 0 ) {
            return history.get( history.size() - 1 );
        }
        else {
            return null;
        }
    }

    //The entire state of what is filled and empty.
    static class State {
        public final List<Container> containers;
        private final int denominator;
        private final int numerator;

        State( int numerator, int denominator, Container[] containers ) {
            this( numerator, denominator, Arrays.asList( containers ) );
        }

        State( int numerator, int denominator, List<Container> containers ) {
            this.containers = containers;
            this.denominator = denominator;
            this.numerator = numerator;
        }

        public State nextRandomState() {
            ArrayList<Container> newContainers = new ArrayList<Container>();
            boolean incrementedCount = false;
            for ( Container container : containers ) {
                if ( container.isFull() ) {
                    newContainers.add( container );
                }
                else {

                    //Assumes things fill to the right and not randomly
                    newContainers.add( container.addRandom() );
                    incrementedCount = true;
                }
            }

            //Didn't add one yet, so add a new container now
            if ( !incrementedCount ) {
                newContainers.add( new Container( denominator, new int[] { random.nextInt( denominator ) } ) );
            }
            return new State( numerator, denominator, newContainers );
        }

        @Override public String toString() {
            return containers.toString();
        }
    }

    //Do not mutate, other code relies on this class being immutable.
    static class Container {
        public final int numCells;
        public final ArrayList<Integer> filledCells;

        Container( int numCells, int[] filledCells ) {
            this( numCells, toList( filledCells ) );
        }

        private static ArrayList<Integer> toList( int[] filledCells ) {
            ArrayList<Integer> list = new ArrayList<Integer>();
            for ( int cell : filledCells ) {
                list.add( cell );
            }
            return list;
        }

        @Override public String toString() {
            return filledCells.toString();
        }

        Container( int numCells, ArrayList<Integer> filledCells ) {
            this.numCells = numCells;
            this.filledCells = filledCells;
        }

        public static Container full( final int denominator ) {
            return new Container( denominator, new ArrayList<Integer>() {{
                for ( int i = 0; i < denominator; i++ ) {
                    add( i );
                }
            }} );
        }

        public boolean isFull() {
            return new HashSet<Integer>( filledCells ).equals( new HashSet<Integer>( listAll( numCells ) ) );
        }

        public Container addRandom() {
            if ( isFull() ) {
                throw new RuntimeException( "tried to add to full container" );
            }
            final HashSet<Integer> empty = new HashSet<Integer>( listAll( numCells ) ) {{
                removeAll( filledCells );
            }};
            final ArrayList<Integer> listOfEmptyCells = new ArrayList<Integer>( empty );
            ArrayList<Integer> newFilledCells = new ArrayList<Integer>( filledCells ) {{
                final int randomIndex = random.nextInt( empty.size() );
                add( listOfEmptyCells.get( randomIndex ) );
            }};
            return new Container( numCells, newFilledCells );
        }
    }

    private void incrementDenominator() {
        denominator = denominator + 1;
        history.clear();
        history.add( createRandomState() );
    }

    private State createRandomState() {
        final int numFullContainers = numerator / denominator;
        final int numCellsInLast = numerator % denominator;
        final boolean partiallyFullContainer = numCellsInLast != 0;
        int numContainers = numFullContainers + ( partiallyFullContainer ? 1 : 0 );
        return new State( numerator, denominator, new ArrayList<Container>() {{
            for ( int i = 0; i < numFullContainers; i++ ) {
                add( Container.full( denominator ) );
            }
            if ( partiallyFullContainer ) {
                Container c = new Container( denominator, new int[0] );
                for ( int i = 0; i < numCellsInLast; i++ ) {
                    c = c.addRandom();
                }
                add( c );
            }
        }} );
    }

    public static final Random random = new Random();

    private Integer removeRandom( ArrayList<Integer> toChooseFrom ) {
        final int index = random.nextInt( toChooseFrom.size() );
        int value = toChooseFrom.get( index );

        //Note that this method is overloaded because of the generics and there is remove(int) and remove(Integer).  Hopefully this calls the right one (the index based one, not value based)!
        toChooseFrom.remove( index );
        return value;
    }

    private static ArrayList<Integer> listAll( final int denominator ) {
        return new ArrayList<Integer>() {{
            for ( int i = 0; i < denominator; i++ ) {
                add( i );
            }
        }};
    }

    public void incrementNumerator() {
        numerator = numerator + 1;
        if ( getCurrentState() == null ) {
            history.add( createRandomState() );
        }
        else {
            history.add( getCurrentState().nextRandomState() );
        }
    }

    public void decrementNumerator() {
        numerator = numerator - 1;

        //If there was an item in the history, it must be for the right denominator (otherwise would have been cleared)
        //Hence we can just go back to the previous history
        if ( history.size() > 0 ) {
            history.remove( history.size() - 1 );
            //TODO: notify that state changed.

        }

        //todo: what if there wasn't enough history to clear?  Then remove one piece randomly from the last container.
        else {

        }
    }

    public static void main( String[] args ) {
        RandomFill randomFill = new RandomFill();
        randomFill.incrementDenominator();
        System.out.println( randomFill.toString() );
        randomFill.incrementNumerator();
        System.out.println( randomFill.toString() );
        randomFill.incrementNumerator();
        System.out.println( randomFill.toString() );
        randomFill.decrementNumerator();
        System.out.println( randomFill.toString() );
    }
}