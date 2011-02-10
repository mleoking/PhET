// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class ToolboxNode extends PNode {
    public ToolboxNode() {
        addChild( new PText( "Hello" ) {{
            setFont( ControlPanelNode.labelFont );
        }} );
    }
}