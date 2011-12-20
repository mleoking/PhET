// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.model;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractions.intro.common.model.SingleFractionModel;
import edu.colorado.phet.fractions.intro.intro.view.Fill;
import edu.colorado.phet.fractions.intro.intro.view.Visualization;

/**
 * Model for the Fractions Intro sim.
 *
 * @author Sam Reid
 */
public class FractionsIntroModel extends SingleFractionModel {
    public final Property<Fill> fill = new Property<Fill>( Fill.SEQUENTIAL );
    public final Property<Visualization> visualization = new Property<Visualization>( Visualization.NONE );
    public final Property<ContainerState> containerState = new Property<ContainerState>( new ContainerState( numerator.get(), denominator.get(), new Container[] { new Container( 1, new int[] { 1 } ) } ) );

    public FractionsIntroModel() {
        //synchronize the container state with the numerator and denominator for when the user uses the spinners
        numerator.addObserver( new ChangeObserver<Integer>() {
            public void update( Integer newValue, Integer oldValue ) {

                //TODO: not debugged yet
//                int delta = newValue - oldValue;
//                containerState.set( containerState.get().addPieces( delta ) );
            }
        } );
    }

    public void resetAll() {
        super.resetAll();
        fill.reset();
        visualization.reset();
    }
}