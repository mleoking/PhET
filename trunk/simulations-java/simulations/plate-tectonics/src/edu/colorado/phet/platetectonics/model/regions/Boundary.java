// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.regions;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.util.Side;

public class Boundary {
    public List<Sample> samples = new ArrayList<Sample>();

    public void addSample( Side side, Sample sample ) {
        side.addToList( samples, sample );
    }

    public void removeSample( Side side ) {
        side.removeFromList( samples );
    }

    public Sample getSample( int index ) {
        return samples.get( index );
    }

    public Sample getEdgeSample( Side side ) {
        return side.getEnd( samples );
    }

    public Sample getEdgeSample( Side side, int offset ) {
        return side.getFromEnd( samples, offset );
    }

    public float getApproximateYFromX( float x ) {
        // if we are outside of our normal bounds, just set to nearest endpoint y
        if ( x < getEdgeSample( Side.LEFT ).getPosition().x ) {
            return getEdgeSample( Side.LEFT ).getPosition().y;
        }
        if ( x > getEdgeSample( Side.RIGHT ).getPosition().x ) {
            return getEdgeSample( Side.RIGHT ).getPosition().y;
        }

        // scan through linearly (easiest for now, but this could definitely be sped up)
        for ( int i = 1; i < samples.size(); i++ ) {
            Sample right = samples.get( i );
            if ( x > right.getPosition().x ) {
                continue;
            }
            Sample left = samples.get( i - 1 );
            float ratio = ( x - left.getPosition().x ) / ( right.getPosition().x - left.getPosition().x );
            return ratio * right.getPosition().y + ( 1 - ratio ) * left.getPosition().y;
        }

        throw new RuntimeException( "should never reach here" );
    }

}