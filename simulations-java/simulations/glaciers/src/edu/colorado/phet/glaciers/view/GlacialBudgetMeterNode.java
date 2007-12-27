/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.GlacialBudgetMeter;
import edu.umd.cs.piccolo.nodes.PImage;


public class GlacialBudgetMeterNode extends AbstractToolNode {

    public GlacialBudgetMeterNode( GlacialBudgetMeter glacialBudgetMeter ) {
        super( glacialBudgetMeter );
        PImage imageNode = new PImage( GlaciersImages.GLACIAL_BUDGET_METER );
        addChild( imageNode );
    }
}
