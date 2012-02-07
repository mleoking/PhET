// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.regions;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.platetectonics.model.SamplePoint;

public class Boundary {
    public List<SamplePoint> samples = new ArrayList<SamplePoint>();

    public void borrowPositionAndTexture( Boundary other ) {
        assert other.samples.size() == samples.size();

        for ( int i = 0; i < samples.size(); i++ ) {
            final SamplePoint mySample = samples.get( i );
            final SamplePoint otherSample = other.samples.get( i );

            mySample.setPosition( otherSample.getPosition() );
            mySample.setTextureCoordinates( otherSample.getTextureCoordinates() );
        }
    }
}
