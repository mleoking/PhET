// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that sets its visibility (and pickability) based on an observable flag
 *
 * @author Sam Reid
 */
public class VisibilityNode extends PNode {
    public VisibilityNode( final ObservableProperty<Boolean> visible, PNode... children ) {
        visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
                setPickable( visible );
                setChildrenPickable( visible );
            }
        } );

        addChildren( Arrays.asList( children ) );
    }
}