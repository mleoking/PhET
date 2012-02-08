// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.regions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.platetectonics.model.Sample;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.flatten;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.map;

public class Region {
    // boundaries from top to bottom
    private List<Boundary> boundaries = new ArrayList<Boundary>();

    private List<CrossSectionStrip> strips = new ArrayList<CrossSectionStrip>();

    // sample point factory is called with (y (row), x (col))
    public Region( int numStrips, int numXSamples, Function2<Integer, Integer, Sample> samplePointFactory ) {
        int numBoundaries = numStrips + 1;

        for ( int row = 0; row < numBoundaries; row++ ) {
            Boundary boundary = new Boundary();
            boundaries.add( boundary );
            for ( int col = 0; col < numXSamples; col++ ) {
                boundary.samples.add( samplePointFactory.apply( row, col ) );
            }
        }
        for ( int row = 0; row < numBoundaries - 1; row++ ) {
            CrossSectionStrip strip = new CrossSectionStrip( boundaries.get( row ).samples, boundaries.get( row + 1 ).samples );
            strips.add( strip );
        }
    }

    public Boundary getTopBoundary() {
        return boundaries.get( 0 );
    }

    public Boundary getBottomBoundary() {
        return boundaries.get( boundaries.size() - 1 );
    }

    public List<Boundary> getBoundaries() {
        return boundaries;
    }

    public List<CrossSectionStrip> getStrips() {
        return strips;
    }

    public List<Sample> getSamples() {
        return flatten( map( boundaries, new Function1<Boundary, Collection<? extends Sample>>() {
            public Collection<? extends Sample> apply( Boundary boundary ) {
                return boundary.samples;
            }
        } ) );
    }
}
