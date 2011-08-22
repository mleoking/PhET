// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;

/**
 * Defines the look and behavior of the "Reset All" button used throughout this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPResetAllButtonNode extends ResetAllButtonNode {

    public MPResetAllButtonNode( Resettable[] resettables, Frame parentFrame ) {
        super( resettables, parentFrame, 16, Color.BLACK, Color.WHITE );
        setConfirmationEnabled( false );
    }
}
