/**
 * Class: HistogramModel
 * Class: edu.colorado.phet.graphics
 * User: Ron LeMaster
 * Date: Jan 18, 2004
 * Time: 1:16:17 PM
 */
package edu.colorado.phet.idealgas.instrumentation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class HistogramModel {

    public static final int IN_RANGE = 1;
    public static final int BELOW_RANGE = 2;
    public static final int ABOVE_RANGE = 3;

    private double lowerBound;
    private double upperBound;
    private TreeMap data = new TreeMap();
    private int numBuckets;
    private double interval;
    private ArrayList buckets = new ArrayList();
    private boolean dataOutOfRange;

    public void setBounds( double lowerBound, double upperBound ) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public void setInterval( double interval ) {
        this.interval = interval;
        this.numBuckets = (int)( ( upperBound - lowerBound ) / interval );
        createBuckets();
    }

    public void setNumIntervals( int numBuckets ) {
        this.numBuckets = numBuckets;
        this.interval = ( upperBound - lowerBound ) / numBuckets;
        createBuckets();
    }

    private void createBuckets() {
        buckets.clear();
        for( double lb = lowerBound; lb < upperBound; lb += interval ) {
            buckets.add( new Bucket( lb, lb + interval ) );
        }
    }

    public void clear() {
        dataOutOfRange = false;
        for( int i = 0; i < buckets.size(); i++ ) {
            Bucket bucket = (Bucket)buckets.get( i );
            bucket.setCount( 0 );
        }
    }

    /**
     * Adds a datum to the histogram. If the datum is above or below the range
     * of the histogram, it is not added.
     *
     * @param value
     * @return One of the following values depending on where the value falls in
     *         relation to the range of the histogram:
     *         <ul>
     *         <li>IN_RANGE
     *         <li>BELOW_RANGE
     *         <li>ABOVE_RANGE
     *         </ul>
     */
    public int add( double value ) {
        int returnValue = IN_RANGE;
        Bucket bucket = findBucket( value );
        if( bucket != null ) {
            bucket.increment();
            returnValue = IN_RANGE;
        }
        else if( value < ( (Bucket)buckets.get( 0 ) ).lowerBound ) {
            returnValue = BELOW_RANGE;
        }
        else if( value >= ( (Bucket)buckets.get( buckets.size() - 1 ) ).upperBound ) {
            dataOutOfRange = true;
        }
        return returnValue;
    }

    private Bucket findBucket( double value ) {
        Bucket result = null;
        for( int i = 0; result == null && i < buckets.size(); i++ ) {
            Bucket bucket = (Bucket)buckets.get( i );
            if( value >= bucket.lowerBound && value < bucket.upperBound ) {
                result = bucket;
            }
        }
        return result;
    }

    public Iterator iterator() {
        return buckets.iterator();
    }

    public int valueAt( int i ) {
        return ( (Bucket)buckets.get( i ) ).getCount();
    }

    public int getNumIntervals() {
        return numBuckets;
    }

    public boolean isDataOutOfRange() {
        return dataOutOfRange;
    }

    //
    // Inner classes
    //
    private static class Bucket {
        private double lowerBound;
        private double upperBound;
        private int count;
        private static ArrayList s_instances = new ArrayList();

        Bucket( double lowerBound, double upperBound ) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            s_instances.add( this );
        }

        void increment() {
            count++;
        }

        int getCount() {
            return count;
        }

        void setCount( int i ) {
            count = 0;
        }
    }


    //
    // CollisionGod code
    //
    public static void main( String[] args ) {
        HistogramModel hm = new HistogramModel();
        hm.setBounds( 0, 10 );
        hm.setNumIntervals( 2 );
        hm.add( -1 );
        hm.add( 50 );
        hm.add( 99 );

        for( int i = 0; i < hm.numBuckets; i++ ) {
            System.out.println( i + ": " + ( (Bucket)hm.buckets.get( i ) ).getCount() );
        }
    }
}
