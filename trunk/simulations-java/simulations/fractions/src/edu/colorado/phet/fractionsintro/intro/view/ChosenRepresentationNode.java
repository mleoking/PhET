// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class ChosenRepresentationNode extends PNode {
    public ChosenRepresentationNode( ObservableProperty<ChosenRepresentation> chosenRepresentation, final ChosenRepresentation value ) {
        chosenRepresentation.valueEquals( value ).addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
    }
}
