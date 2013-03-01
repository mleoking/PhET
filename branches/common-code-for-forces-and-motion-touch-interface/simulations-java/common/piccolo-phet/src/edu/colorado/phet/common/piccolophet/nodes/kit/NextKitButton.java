// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.kit;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.nodes.ArrowButtonNode;

/**
 * Button for moving to the next kit.
 *
 * @author Sam Reid
 */
public class NextKitButton extends ArrowButtonNode {
    public NextKitButton( Color buttonColor ) {
        super( Orientation.RIGHT, new ColorScheme( buttonColor ) );
    }
}