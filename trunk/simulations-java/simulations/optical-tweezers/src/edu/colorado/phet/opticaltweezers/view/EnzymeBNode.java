/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.model.EnzymeB;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;


public class EnzymeBNode extends AbstractEnzymeNode {

    public EnzymeBNode( EnzymeB enzyme, ModelViewTransform modelViewTransform ) {
        super( enzyme, modelViewTransform, OTConstants.ENZYME_B_OUTER_COLOR, OTConstants.ENZYME_B_INNER_COLOR, OTConstants.ENZYME_B_TICK_COLOR );
    }
}
