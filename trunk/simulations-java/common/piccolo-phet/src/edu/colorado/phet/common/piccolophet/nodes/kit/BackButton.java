// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.kit;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.nodes.ArrowButtonNode;

/**
 * Button for moving backwards through the kits
 *
 * @author Sam Reid
 */
public class BackButton extends ArrowButtonNode {
    public BackButton( Color buttonColor ) {
        super( Orientation.LEFT, new ColorScheme( buttonColor ) );
    }
}