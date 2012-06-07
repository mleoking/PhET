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
        if ( x <= getEdgeSample( Side.LEFT ).getPosition().x ) {
            return getEdgeSample( Side.LEFT ).getPosition().y;
        }
        if ( x >= getEdgeSample( Side.RIGHT ).getPosition().x ) {
            return getEdgeSample( Side.RIGHT ).getPosition().y;
        }

        // basically find this with bisection. assumes increasing y values
        int lowGuess = 0;
        int highGuess = samples.size() - 1;

        assert lowGuess < highGuess;

        while ( lowGuess + 1 != highGuess ) {
            assert lowGuess != highGuess;

            int middleGuess = ( lowGuess + highGuess ) / 2;
            Sample sample = samples.get( middleGuess );
            if ( x >= sample.getPosition().x ) {
                lowGuess = middleGuess;
            }
            else {
                highGuess = middleGuess;
            }
        }

        Sample left = samples.get( lowGuess );
        Sample right = samples.get( highGuess );

        float ratio = ( x - left.getPosition().x ) / ( right.getPosition().x - left.getPosition().x );
        return ratio * right.getPosition().y + ( 1 - ratio ) * left.getPosition().y;
    }

}