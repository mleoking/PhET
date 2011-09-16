// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.kit;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.nodes.ArrowButtonNode;

/**
 * Button for moving forward through the kits
 *
 * @author Sam Reid
 */
public class ForwardButton extends ArrowButtonNode {
    public ForwardButton() {
        super( Orientation.RIGHT, new ColorScheme( Color.orange ) );
    }
}