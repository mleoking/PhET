package edu.colorado.phet.fractions.research_november_2013;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.*;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionScreenType;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by Sam on 10/21/13.
 */
public interface ResearchApplication {
    ObservableProperty<Boolean> windowNotIconified();

    ObservableProperty<Boolean> windowActive();

    ObservableProperty<String> module();

    ObservableProperty<String> introRepresentation();

    ObservableProperty<Integer> introDenominator();

    ObservableProperty<Integer> introNumerator();

    ObservableProperty<Integer> introMaximum();

    ObservableProperty<Integer> totalClicks();

    ObservableProperty<BuildAFractionScreenType> bafScreenType();

    void addBAFLevelStartedListener( VoidFunction1<PNode> listener );

    Function0<Long> time();

    Function0<Long> endTime();
}
