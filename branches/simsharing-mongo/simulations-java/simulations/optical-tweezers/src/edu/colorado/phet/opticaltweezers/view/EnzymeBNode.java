// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.model.EnzymeB;
import edu.colorado.phet.opticaltweezers.model.OTModelViewTransform;

/**
 * EnzymeBNode is the visual representation of an EnzymeB enzyme.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EnzymeBNode extends AbstractEnzymeNode {

    public EnzymeBNode( EnzymeB enzyme, OTModelViewTransform modelViewTransform ) {
        super( enzyme, modelViewTransform, OTConstants.ENZYME_B_OUTER_COLOR, OTConstants.ENZYME_B_INNER_COLOR, OTConstants.ENZYME_B_TICK_COLOR );
    }
}
