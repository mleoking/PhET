// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.regions;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import edu.colorado.phet.common.phetcommon.model.event.ValueNotifier;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.util.Side;

/**
 * Describes a (usually horizontal) strip of earth cross-section delineated by a top and bottom list of points. It is essentially the sum
 * of quadrilaterals made up by top[i],top[i+1],bottom[i+1],bottom[i] for all applicable i.
 */
public class CrossSectionStrip {
    public final List<Sample> topPoints = new LinkedList<Sample>();
    public final List<Sample> bottomPoints = new LinkedList<Sample>();

    public final ValueNotifier<CrossSectionStrip> changed = new ValueNotifier<CrossSectionStrip>( this );

    // fires when this strip should be moved to the front
    public final ValueNotifier<CrossSectionStrip> moveToFrontNotifier = new ValueNotifier<CrossSectionStrip>( this );

    public final Property<Float> alpha = new Property<Float>( 1f );

    public CrossSectionStrip() {
        alpha.addObserver( new SimpleObserver() {
            public void update() {
                changed.updateListeners();
            }
        } );
    }

    public CrossSectionStrip( List<Sample> topPoints, List<Sample> bottomPoints ) {
        ListIterator<Sample> topIter = topPoints.listIterator();
        ListIterator<Sample> bottomIter = bottomPoints.listIterator();

        while ( topIter.hasNext() ) {
            addPatch( Side.RIGHT, topIter.next(), bottomIter.next() );
        }
    }

    public int getLength() {
        return topPoints.size();
    }

    public int getNumberOfVertices() {
        return topPoints.size() * 2;
    }

    public void addPatch( Side side, Sample top, Sample bottom ) {
        side.addToList( topPoints, top );
        side.addToList( bottomPoints, bottom );
    }

    public void removePatch( Side side ) {
        side.removeFromList( topPoints );
        side.removeFromList( bottomPoints );
    }

    public void update() {
        changed.updateListeners();
    }
}
