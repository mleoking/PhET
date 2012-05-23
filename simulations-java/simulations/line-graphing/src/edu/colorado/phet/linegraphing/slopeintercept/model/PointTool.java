// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.model.StraightLine;

/**
 * Model of the point tool. Highlights when it is placed on one of the lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointTool implements Resettable {

    public final Property<ImmutableVector2D> location;
    public final Property<StraightLine> onLine; // line that the tool is on, possibly null

    private final Property<StraightLine> interactiveLine;
    private final ObservableList<StraightLine> savedLines;
    private final ObservableList<StraightLine> standardLines;

    public PointTool( ImmutableVector2D location, Property<StraightLine> interactiveLine, ObservableList<StraightLine> savedLines, ObservableList<StraightLine> standardLines ) {

        this.location = new Property<ImmutableVector2D>( location );
        this.onLine = new Property<StraightLine>( null );

        this.interactiveLine = interactiveLine;
        this.savedLines = savedLines;
        this.standardLines = standardLines;

        // When location or lines change, update highlighting
        {
            // interactive line
            final RichSimpleObserver observer = new RichSimpleObserver() {
                public void update() {
                    updateOnLine();
                }
            };
            observer.observe( this.location, interactiveLine );

            // saved & standard lines
            final VoidFunction1<StraightLine> linesChanged = new VoidFunction1<StraightLine>() {
                public void apply( StraightLine line ) {
                    updateOnLine();
                }
            };
            savedLines.addElementAddedObserver( linesChanged );
            savedLines.addElementRemovedObserver( linesChanged );
            standardLines.addElementAddedObserver( linesChanged );
            standardLines.addElementRemovedObserver( linesChanged );
        }
    }

    // Determine which line (if any) the tool is placed on.
    private void updateOnLine() {

        ArrayList<StraightLine> lines = new ArrayList<StraightLine>();
        lines.addAll( savedLines );
        lines.addAll( standardLines );
        lines.add( interactiveLine.get() );

        for ( StraightLine line : lines ) {
            if ( isOnLine( line ) ) {
                onLine.set( line );
                return;
            }
        }

        onLine.set( null );
    }

    // Is the point tool on this line?
    private boolean isOnLine( StraightLine line ) {
        if ( line.run == 0 && location.get().getX() == line.x1 ) {
            // slope is undefined, tool is on the line
            return true;
        }
        else if ( line.rise == 0 && location.get().getY() == line.y1 ) {
            // slope is zero, tool is on the line
            return true;
        }
        else if ( line.run != 0 && line.rise != 0 && location.get().getY() == line.solveY( location.get().getX() ) ) {
            // not one of the 2 special cases above, and the tool is on the line
            return true;
        }
        return false;
    }

    public void reset() {
        location.reset();
        onLine.reset();
    }
}
