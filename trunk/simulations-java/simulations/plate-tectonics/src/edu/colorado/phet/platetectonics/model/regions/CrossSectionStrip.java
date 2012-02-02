// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.regions;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import edu.colorado.phet.common.phetcommon.model.event.ValueNotifier;
import edu.colorado.phet.platetectonics.model.SamplePoint;

public class CrossSectionStrip {
    public final List<SamplePoint> topPoints = new LinkedList<SamplePoint>();
    public final List<SamplePoint> bottomPoints = new LinkedList<SamplePoint>();

    public final ValueNotifier<CrossSectionStrip> changed = new ValueNotifier<CrossSectionStrip>( this );

    public CrossSectionStrip() {
    }

    public CrossSectionStrip( List<SamplePoint> topPoints, List<SamplePoint> bottomPoints ) {
        ListIterator<SamplePoint> topIter = topPoints.listIterator();
        ListIterator<SamplePoint> bottomIter = bottomPoints.listIterator();

        while ( topIter.hasNext() ) {
            addRightPatch( topIter.next(), bottomIter.next() );
        }
    }

    public int getLength() {
        return topPoints.size();
    }

    public int getNumberOfVertices() {
        return topPoints.size() * 2;
    }

    public void addLeftPatch( SamplePoint top, SamplePoint bottom ) {
        topPoints.add( 0, top );
        bottomPoints.add( 0, bottom );
    }

    public void addRightPatch( SamplePoint top, SamplePoint bottom ) {
        topPoints.add( top );
        bottomPoints.add( bottom );
    }

    public void removeLeftPatch() {
        topPoints.remove( 0 );
        bottomPoints.remove( 0 );
    }

    public void removeRightPatch() {
        topPoints.remove( topPoints.size() - 1 );
        bottomPoints.remove( bottomPoints.size() - 1 );
    }

    public void update() {
        changed.updateListeners();
    }
}
