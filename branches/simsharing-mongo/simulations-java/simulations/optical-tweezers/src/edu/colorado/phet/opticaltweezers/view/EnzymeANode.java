// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.model.EnzymeA;
import edu.colorado.phet.opticaltweezers.model.OTModelViewTransform;

/**
 * EnzymeANode is the visual representation of an EnzymeA enzyme.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EnzymeANode extends AbstractEnzymeNode {

    public EnzymeANode( EnzymeA enzyme, OTModelViewTransform modelViewTransform ) {
        super( enzyme, modelViewTransform, OTConstants.ENZYME_A_OUTER_COLOR, OTConstants.ENZYME_A_INNER_COLOR, OTConstants.ENZYME_A_TICK_COLOR );
    }

}
