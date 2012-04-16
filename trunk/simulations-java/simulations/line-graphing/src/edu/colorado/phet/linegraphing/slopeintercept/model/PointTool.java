// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Model of the point tool. Highlights when it is placed on one of the lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointTool implements Resettable {

    public final Property<ImmutableVector2D> location;
    public final Property<SlopeInterceptLine> onLine; // line that the tool is on, possibly null

    private final Property<SlopeInterceptLine> interactiveLine;
    private final ObservableList<SlopeInterceptLine> savedLines;
    private final ObservableList<SlopeInterceptLine> standardLines;

    public PointTool( ImmutableVector2D location, Property<SlopeInterceptLine> interactiveLine, ObservableList<SlopeInterceptLine> savedLines, ObservableList<SlopeInterceptLine> standardLines ) {

        this.location = new Property<ImmutableVector2D>( location );
        this.onLine = new Property<SlopeInterceptLine>( null );

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
            final VoidFunction1<SlopeInterceptLine> linesChanged = new VoidFunction1<SlopeInterceptLine>() {
                public void apply( SlopeInterceptLine line ) {
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

        ArrayList<SlopeInterceptLine> lines = new ArrayList<SlopeInterceptLine>();
        lines.addAll( savedLines );
        lines.addAll( standardLines );
        lines.add( interactiveLine.get() );

        for ( SlopeInterceptLine line : lines ) {
            if ( isOnLine( line ) ) {
                onLine.set( line );
                return;
            }
        }

        onLine.set( null );
    }

    // Is the point tool on this line?
    private boolean isOnLine( SlopeInterceptLine line ) {
        if ( line.run == 0 && location.get().getX() == 0 ) {
            // undefined slope, tool is on the y axis
            return true;
        }
        else if ( line.rise == 0 && location.get().getY() == line.intercept ) {
            // slope is zero and point tool in at intercept
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
