// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import java.util.ArrayList;
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
    private ArrayList<ContainerSet> history = new ArrayList<ContainerSet>();
    private int numerator = 1;
    private int denominator = 1;

    @Override public String toString() {
        return "numerator = " + numerator + ", denominator = " + denominator + ", history = " + history;
    }

    public RandomFill() {
//        history.add( new ContainerState( numerator, denominator, new Container[] { new Container( 1, new int[] { 0 } ) } ) );
    }

    public ContainerSet getCurrentState() {
        if ( history.size() > 0 ) {
            return history.get( history.size() - 1 );
        }
        else {
            return null;
        }
    }

    private void incrementDenominator() {
        denominator = denominator + 1;
        history.clear();
        history.add( createRandomState() );
    }

    private ContainerSet createRandomState() {
        final int numFullContainers = numerator / denominator;
        final int numCellsInLast = numerator % denominator;
        final boolean partiallyFullContainer = numCellsInLast != 0;
//        int numContainers = numFullContainers + ( partiallyFullContainer ? 1 : 0 );
//        return new ContainerState( numerator, denominator, new ArrayList<Container>() {{
//            for ( int i = 0; i < numFullContainers; i++ ) {
//                add( Container.full( denominator ) );
//            }
//            if ( partiallyFullContainer ) {
//                Container c = new Container( denominator, new int[0] );
//                for ( int i = 0; i < numCellsInLast; i++ ) {
//                    c = c.addRandom();
//                }
//                add( c );
//            }
//        }} );
        return null;
    }


//    public ContainerState nextRandomState( ContainerState cs ) {
//        ArrayList<Container> newContainers = new ArrayList<Container>();
//        boolean incrementedCount = false;
//        for ( Container container : cs.containers ) {
//            if ( container.isFull() ) {
//                newContainers.add( container );
//            }
//            else {
//
//                //Assumes things fill to the right and not randomly
//                newContainers.add( container.addRandom() );
//                incrementedCount = true;
//            }
//        }
//
//        //Didn't add one yet, so add a new container now
//        if ( !incrementedCount ) {
//            newContainers.add( new Container( denominator, new int[] { RandomFill.random.nextInt( denominator ) } ) );
//        }
//        return new ContainerState( denominator, newContainers );
//    }

    public static final Random random = new Random();

    private Integer removeRandom( ArrayList<Integer> toChooseFrom ) {
        final int index = random.nextInt( toChooseFrom.size() );
        int value = toChooseFrom.get( index );

        //Note that this method is overloaded because of the generics and there is remove(int) and remove(Integer).  Hopefully this calls the right one (the index based one, not value based)!
        toChooseFrom.remove( index );
        return value;
    }

    public static ArrayList<Integer> listAll( final int denominator ) {
        return new ArrayList<Integer>() {{
            for ( int i = 0; i < denominator; i++ ) {
                add( i );
            }
        }};
    }

//    public void incrementNumerator() {
//        numerator = numerator + 1;
//        if ( getCurrentState() == null ) {
//            history.add( createRandomState() );
//        }
//        else {
//            history.add( nextRandomState( getCurrentState() ) );
//        }
//    }

//    public Container addRandom() {
//        if ( isFull() ) {
//            throw new RuntimeException( "tried to add to full container" );
//        }
//        final HashSet<Integer> empty = new HashSet<Integer>( RandomFill.listAll( numCells ) ) {{
//            removeAll( filledCells );
//        }};
//        final ArrayList<Integer> listOfEmptyCells = new ArrayList<Integer>( empty );
//        HashSet<Integer> newFilledCells = new HashSet<Integer>( filledCells ) {{
//            final int randomIndex = RandomFill.random.nextInt( empty.size() );
//            add( listOfEmptyCells.get( randomIndex ) );
//        }};
//        return new Container( numCells, newFilledCells );
//    }


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

//    public static void main( String[] args ) {
//        RandomFill randomFill = new RandomFill();
//        randomFill.incrementDenominator();
//        System.out.println( randomFill.toString() );
//        randomFill.incrementNumerator();
//        System.out.println( randomFill.toString() );
//        randomFill.incrementNumerator();
//        System.out.println( randomFill.toString() );
//        randomFill.decrementNumerator();
//        System.out.println( randomFill.toString() );
//    }
}