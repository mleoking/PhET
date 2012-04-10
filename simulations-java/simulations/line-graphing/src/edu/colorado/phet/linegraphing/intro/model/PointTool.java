// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine.InteractiveLine;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine.SavedLine;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine.StandardLine;

/**
 * Model of the point tool. Highlights when it is placed on one of the lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointTool implements Resettable {

    public final Property<ImmutableVector2D> location;
    public final Property<Boolean> highlighted;

    private final Property<InteractiveLine> interactiveLine;
    private final ObservableList<SavedLine> savedLines;
    private final ObservableList<StandardLine> standardLines;

    public PointTool( ImmutableVector2D location, Property<InteractiveLine> interactiveLine, ObservableList<SavedLine> savedLines, ObservableList<StandardLine> standardLines ) {

        this.location = new Property<ImmutableVector2D>( location );
        this.highlighted = new Property<Boolean>( false );

        this.interactiveLine = interactiveLine;
        this.savedLines = savedLines;
        this.standardLines = standardLines;

        // When location or lines change, update highlighting
        {
            // interactive line
            final RichSimpleObserver observer = new RichSimpleObserver() {
                public void update() {
                    updateHighlight();
                }
            };
            observer.observe( this.location, interactiveLine );

            // saved lines
            final VoidFunction1<SavedLine> savedLinesChanged = new VoidFunction1<SavedLine>() {
                public void apply( SavedLine line ) {
                    updateHighlight();
                }
            };
            savedLines.addElementAddedObserver( savedLinesChanged );
            savedLines.addElementRemovedObserver( savedLinesChanged );

            // standard lines
            final VoidFunction1<StandardLine> standardLinesChanged = new VoidFunction1<StandardLine>() {
                public void apply( StandardLine line ) {
                    updateHighlight();
                }
            };
            standardLines.addElementAddedObserver( standardLinesChanged );
            standardLines.addElementRemovedObserver( standardLinesChanged );
        }
    }

    // Highlight the tool if its place on one of the lines
    private void updateHighlight() {
        highlighted.set( isOnLine( interactiveLine.get() ) || isOnSavedLines( savedLines ) || isOnStandardLines( standardLines ) );
    }

    // Is the point tool on this line?
    private boolean isOnLine( SlopeInterceptLine line ) {
        if ( Math.round( line.run ) == 0 && Math.round( location.get().getX() ) == 0 ) {
            return true;
        }
        else if ( Math.round( line.solveX( location.get().getY() ) ) == Math.round( location.get().getX() ) ) {
            return true;
        }
        return false;
    }

    private boolean isOnSavedLines( ObservableList<SavedLine> lines ) {
         for ( SavedLine line : lines ) {
             if ( isOnLine( line ) ) {
                 return true;
             }
         }
         return false;
    }

    private boolean isOnStandardLines( ObservableList<StandardLine> lines ) {
         for ( StandardLine line : lines ) {
             if ( isOnLine( line ) ) {
                 return true;
             }
         }
         return false;
    }

    public void reset() {
        location.reset();
        highlighted.reset();
    }
}
