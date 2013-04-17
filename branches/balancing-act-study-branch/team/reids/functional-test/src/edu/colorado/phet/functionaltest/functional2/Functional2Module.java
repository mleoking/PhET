// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.functional2;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class Functional2Module extends Module {
    public Functional2Module() {
        super( "Functional 2", new ConstantDtClock() );
        final Functional2Model model = new Functional2Model( getClock() );
        setSimulationPanel( new PhetPCanvas() {{
            addScreenChild( new PNode() {{
                model.state.addObserver( new VoidFunction1<State2>() {
                    @Override public void apply( final State2 state2 ) {
                        removeAllChildren();
                        addChild( new StateNode( model.state, state2 ) );
                    }
                } );

            }} );
        }} );
    }
}