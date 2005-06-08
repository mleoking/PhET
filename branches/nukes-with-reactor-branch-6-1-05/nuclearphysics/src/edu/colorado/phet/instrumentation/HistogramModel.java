/**
 * Class: HistogramModel
 * Class: edu.colorado.phet.graphics
 * User: Ron LeMaster
 * Date: Jan 18, 2004
 * Time: 1:16:17 PM
 */
package edu.colorado.phet.instrumentation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class HistogramModel {

    private double lowerBound;
    private double upperBound;
    private TreeMap data = new TreeMap();
    private int numBuckets;
    private double interval;
    private ArrayList buckets = new ArrayList();

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
        for( int i = 0; i < buckets.size(); i++ ) {
            Bucket bucket = (Bucket)buckets.get( i );
            bucket.setCount( 0 );
        }
    }

    public void add( double value ) {
        Bucket bucket = findBucket( value );
        if( bucket != null ) {
            bucket.increment();
        }
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
