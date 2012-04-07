// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.imperative;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class ReadoutNode extends PNode {
    public ReadoutNode( final ImperativeModel model ) {
        addChild( new PhetPText( "" ) {{
            for ( Circle circle : model.getCircles() ) {
                SimpleObserver listener = new SimpleObserver() {
                    @Override public void update() {
                        Circle c = getDragging( model );
                        setText( c == null ? "None" : c.toString() );
                    }
                };
                circle.dragging.addObserver( listener );
                circle.position.addObserver( listener );
            }
        }} );
    }

    private Circle getDragging( final ImperativeModel model ) {
        for ( Circle c : model.getCircles() ) {
            if ( c.dragging.get() ) {
                return c;
            }
        }
        return null;
    }
}