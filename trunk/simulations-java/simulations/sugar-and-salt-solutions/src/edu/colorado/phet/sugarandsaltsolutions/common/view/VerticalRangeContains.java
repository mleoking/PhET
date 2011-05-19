// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * This property is used to determine if any fluid can flow out through the output pipe, it works by observing the bounds of the solution and seeing if any part of that overlaps any part of the open pipe.
 *
 * @author Sam Reid
 */
public class VerticalRangeContains extends CompositeProperty<Boolean> {
    public VerticalRangeContains( final ObservableProperty<Shape> shape, final double minY, final double maxY ) {
        super( new Function0<Boolean>() {
                   public Boolean apply() {

                       //A simple way to determine whether any part of range A overlaps with any part of range B is to intersect rectangles
                       //the X regions of the rectangles are the same so they can be safely ignored
                       Rectangle2D parentBounds = shape.get().getBounds2D();
                       Rectangle2D pseudoBounds = new Rectangle2D.Double( parentBounds.getX(), minY, parentBounds.getWidth(), maxY );
                       return parentBounds.intersects( pseudoBounds );
                   }
               }, shape );
    }
}
