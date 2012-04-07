// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.imperative;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class ImperativeModelNode extends PNode {
    public ImperativeModelNode( final ImperativeModel model ) {
        for ( final Circle o : model.getCircles() ) {
            addChild( new CircleNode( o ) {{
                o.dragging.addObserver( new VoidFunction1<Boolean>() {
                    @Override public void apply( final Boolean dragging ) {
                        if ( dragging ) { moveToFront(); }
                    }
                } );
            }} );
        }
        addChild( new ReadoutNode( model ) );
    }
}