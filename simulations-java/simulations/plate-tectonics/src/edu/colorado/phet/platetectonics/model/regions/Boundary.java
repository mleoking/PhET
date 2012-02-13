// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.regions;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.platetectonics.model.Sample;

public class Boundary {
    public List<Sample> samples = new ArrayList<Sample>();

    public void addLeftSample( Sample sample ) {
        samples.add( 0, sample );
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

    public Sample getFirstSample() {
        return samples.get( 0 );
    }

    public Sample getLastSample() {
        return samples.get( samples.size() - 1 );
    }
}
