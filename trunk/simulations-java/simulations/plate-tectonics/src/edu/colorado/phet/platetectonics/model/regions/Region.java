// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.regions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.TextureStrategy;
import edu.colorado.phet.platetectonics.util.Side;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.flatten;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.map;

/**
 * A region is cross-sectional area of earth made up of strips separated by multiple boundaries (usually top-to-bottom). Given a list of multiple
 * boundaries (top-to-bottom with the same number of samples in each), the region is made up of strips of earth made from each adjacent pair
 * of boundaries.
 */
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

    public void addColumn( Side side, List<Sample> samples ) {
        assert samples.size() == boundaries.size();

        // insert samples into the start of the boundaries
        for ( int i = 0; i < samples.size(); i++ ) {
            boundaries.get( i ).addSample( side, samples.get( i ) );
        }

        for ( int i = 0; i < strips.size(); i++ ) {
            strips.get( i ).addPatch( side, samples.get( i ), samples.get( i + 1 ) );
        }
    }

    public void removeColumn( Side side ) {
        for ( Boundary boundary : boundaries ) {
            boundary.removeSample( side );
        }

        for ( CrossSectionStrip strip : strips ) {
            strip.removePatch( side );
        }
    }

    public float getTopElevation( int columnIndex ) {
        return getTopBoundary().samples.get( columnIndex ).getPosition().y;
    }

    public float getBottomElevation( int columnIndex ) {
        return getBottomBoundary().samples.get( columnIndex ).getPosition().y;
    }

    // re-spaces the heights in a column evenly between top and bottom
    // TODO: stop having to pass in this strategy everywhere!
    public void layoutColumn( int columnIndex, float top, float bottom, TextureStrategy textureStrategy, boolean updateTextures ) {
        for ( int i = 0; i < boundaries.size(); i++ ) {
            final Sample sample = boundaries.get( i ).samples.get( columnIndex );
            final ImmutableVector3F oldPosition = sample.getPosition();
            float ratio = ( (float) i ) / ( (float) ( boundaries.size() - 1 ) );
            final float oldY = oldPosition.y;
            final float newY = ( 1 - ratio ) * top + ( ratio ) * bottom;
            sample.setPosition( new ImmutableVector3F( oldPosition.x,
                                                       newY,
                                                       oldPosition.z ) );
            if ( updateTextures ) {
                sample.setTextureCoordinates( sample.getTextureCoordinates().plus( textureStrategy.mapFrontDelta( new ImmutableVector2F( 0, newY - oldY ) ) ) );
            }
        }
    }

    public void moveToFront() {
        for ( CrossSectionStrip strip : strips ) {
            strip.moveToFrontNotifier.updateListeners();
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

    public void setAllAlphas( float alpha ) {
        for ( CrossSectionStrip strip : strips ) {
            strip.alpha.set( alpha );
        }
    }
}
