// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.kit;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.nodes.ArrowButtonNode;

/**
 * Button for moving to the previous kit.
 *
 * @author Sam Reid
 */
public class PreviousKitButton extends ArrowButtonNode {
    public PreviousKitButton( Color buttonColor ) {
        super( Orientation.LEFT, new ColorScheme( buttonColor ) );
    }
}