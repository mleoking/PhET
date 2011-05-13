// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro;

import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsConfig;
import edu.colorado.phet.sugarandsaltsolutions.common.view.ConcentrationBarChart;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;

/**
 * @author Sam Reid
 */
public class IntroCanvas extends SugarAndSaltSolutionsCanvas {
    public IntroCanvas( final IntroModel model, SugarAndSaltSolutionsConfig config ) {
        super( model, model.anySolutes, config );

        ConcentrationBarChart concentrationBarChart = new ConcentrationBarChart( model.saltConcentration, model.sugarConcentration ) {{
            setOffset( stageSize.getWidth() - getFullBoundsReference().width - INSET, INSET );
        }};
        addChild( concentrationBarChart );

        soluteControlPanelNode.setOffset( concentrationBarChart.getFullBounds().getX() - soluteControlPanelNode.getFullBounds().getWidth() - INSET, concentrationBarChart.getFullBounds().getY() );
    }
}
