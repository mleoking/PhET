// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro;

import edu.colorado.phet.sugarandsaltsolutions.common.view.ConcentrationBarChart;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;

/**
 * @author Sam Reid
 */
public class IntroCanvas extends SugarAndSaltSolutionsCanvas {
    public IntroCanvas( final IntroModel model ) {
        super( model );

        addChild( new ConcentrationBarChart( model.saltConcentration, model.sugarConcentration ) {{
            setOffset( getControlPanelMinX() - getFullBoundsReference().width - 20, 20 );
        }} );
    }
}
