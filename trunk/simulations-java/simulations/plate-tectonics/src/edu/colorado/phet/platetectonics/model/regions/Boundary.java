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

    public void borrowPosition( Boundary other ) {
        assert other.samples.size() == samples.size();

        for ( int i = 0; i < samples.size(); i++ ) {
            final Sample mySample = samples.get( i );
            final Sample otherSample = other.samples.get( i );

            mySample.setPosition( otherSample.getPosition() );
        }
    }

    public void borrowPositionAndTexture( Boundary other ) {
        assert other.samples.size() == samples.size();

        for ( int i = 0; i < samples.size(); i++ ) {
            final Sample mySample = samples.get( i );
            final Sample otherSample = other.samples.get( i );

            mySample.setPosition( otherSample.getPosition() );
            mySample.setTextureCoordinates( otherSample.getTextureCoordinates() );
        }
    }

    public void borrowPositionTemperatureAndTexture( Boundary other ) {
        assert other.samples.size() == samples.size();

        for ( int i = 0; i < samples.size(); i++ ) {
            final Sample mySample = samples.get( i );
            final Sample otherSample = other.samples.get( i );

            mySample.setPosition( otherSample.getPosition() );
            mySample.setTextureCoordinates( otherSample.getTextureCoordinates() );
            mySample.setTemperature( otherSample.getTemperature() );
        }
    }

    public Sample getEdgeSample( Side side ) {
        return side.getEnd( samples );
    }

    public Sample getEdgeSample( Side side, int offset ) {
        return side.getFromEnd( samples, offset );
    }

}