// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;

import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class WhiteControlPanelNode extends edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode {
    public WhiteControlPanelNode( final PNode content ) {
        super( content, Color.white );
    }
}
