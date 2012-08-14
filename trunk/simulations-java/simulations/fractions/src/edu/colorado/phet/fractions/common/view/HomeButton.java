// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;

/**
 * Button that allows the user to navigate back.
 *
 * @author Sam Reid
 */
public class HomeButton extends PNode {
    public HomeButton() {
        PNode icon = new PNode() {{
            addChild( new VBox( new RowNode(), new RowNode() ) );
        }};
        addChild( new HTMLImageButtonNode( BufferedImageUtils.toBufferedImage( icon.toImage() ) ) );
    }

    private class RowNode extends HBox {
        private RowNode() {
            addChild( path() );
            addChild( path() );
            addChild( path() );
            addChild( path() );
            addChild( path() );
        }
    }

    private PhetPPath path() {return new PhetPPath( new Rectangle2D.Double( 0, 0, 10, 5 ) );}
}