// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.functional;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.functionaltest.functional.model.FunctionalModel;
import edu.colorado.phet.functionaltest.functional.model.FunctionalState;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class FunctionalModelNode extends PNode {
    public FunctionalModelNode( final FunctionalModel model ) {
        model.model.addObserver( new VoidFunction1<FunctionalState>() {
            @Override public void apply( final FunctionalState functionalState ) {
                removeAllChildren();
                addChild( new FunctionalStateNode( model.model ) );
            }
        } );
    }
}