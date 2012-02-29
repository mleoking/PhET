// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.equalitylab.model;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

/**
 * Model for the Equality Lab tab
 *
 * @author Sam Reid
 */
public class EqualityLabModel {
    private final FractionsIntroModel model = new FractionsIntroModel( IntroState.IntroState( 6 ) );
    public final SettableProperty<Representation> representation = model.representation;
    public final SettableProperty<PieSet> pieSet = model.pieSet;
    public final SettableProperty<PieSet> horizontalBarSet = model.horizontalBarSet;
    public final Clock clock = model.clock;

    public void resetAll() {
        model.resetAll();
    }
}