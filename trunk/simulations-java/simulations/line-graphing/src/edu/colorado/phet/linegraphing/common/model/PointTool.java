// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Model of the point tool. Highlights when it is placed on one of the lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointTool implements Resettable {

    public static enum Orientation {UP, DOWN} // direction that the tool points

    public final Property<Vector2D> location;
    public final Orientation orientation;
    public final Property<Line> onLine; // line that the tool is on, possibly null

    private final ObservableList<Line> lines;

    /**
     * Constructor
     *
     * @param location location of the tool, in model coordinate frame
     * @param lines    lines that the tool might intersect, provided in the order that they would be rendered
     */
    public PointTool( Vector2D location, Orientation orientation, ObservableList<Line> lines ) {

        this.location = new Property<Vector2D>( location );
        this.orientation = orientation;
        this.onLine = new Property<Line>( null );

        this.lines = lines;

        // When location or lines change, update highlighting
        {
            // location
            this.location.addObserver( new SimpleObserver() {
                public void update() {
                    updateOnLine();
                }
            } );

            // saved & standard lines
            final VoidFunction1<Line> linesChanged = new VoidFunction1<Line>() {
                public void apply( Line line ) {
                    updateOnLine();
                }
            };
            lines.addElementAddedObserver( linesChanged );
            lines.addElementRemovedObserver( linesChanged );
        }
    }

    // Determine which line (if any) the tool is placed on.
    private void updateOnLine() {
        // Lines are in rendering order, reverse iterate so we get the one that's on top.
        for ( int i = lines.size() - 1; i >= 0; i-- ) {
            Line line = lines.get( i );
            if ( isOnLine( line ) ) {
                onLine.set( line );
                return;
            }
        }
        onLine.set( null );
    }

    // Is the point tool on this line?
    private boolean isOnLine( Line line ) {
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
