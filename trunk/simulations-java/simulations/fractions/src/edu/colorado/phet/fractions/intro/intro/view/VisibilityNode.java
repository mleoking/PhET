// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.intro.intro.model.CellPointer;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that sets its visibility (and pickability) based on an observable flag
 *
 * @author Sam Reid
 */
public abstract class VisibilityNode extends PNode {
    public final ObservableProperty<Boolean> visible;

    public VisibilityNode( final ObservableProperty<Boolean> visible ) {
        this.visible = visible;
        visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
                setPickable( visible );
                setChildrenPickable( visible );
            }
        } );
    }

    public abstract CellPointer getClosestOpenCell( Shape globalShape, Point2D center2D );
}