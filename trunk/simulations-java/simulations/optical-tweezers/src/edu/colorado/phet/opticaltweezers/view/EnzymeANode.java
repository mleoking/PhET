/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.Color;
import java.awt.Paint;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.model.EnzymeA;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;


public class EnzymeANode extends AbstractEnzymeNode {

    public EnzymeANode( EnzymeA enzyme, ModelViewTransform modelViewTransform ) {
        super( enzyme, modelViewTransform, OTConstants.ENZYME_A_OUTER_COLOR, OTConstants.ENZYME_A_INNER_COLOR, OTConstants.ENZYME_A_TICK_COLOR );
    }

}
