// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that is only visible when the indicated representation is selected
 *
 * @author Sam Reid
 */
public class RepresentationNode extends PNode {
    public RepresentationNode( ObservableProperty<Representation> chosenRepresentation, final Representation representation, final PNode... children ) {
        chosenRepresentation.valueEquals( representation ).addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );

                //Send message to children so they can avoid updating when possible
                for ( PNode child : children ) {
                    child.setVisible( visible );
                }
            }
        } );
        for ( PNode child : children ) {
            addChild( child );
        }
    }
}