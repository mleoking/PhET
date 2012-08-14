// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
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
    public HomeButton( final VoidFunction0 pressed ) {
        PNode icon = new PNode() {{
            addChild( new VBox( 3, new RowNode(), new RowNode() ) );
        }};
        addChild( new HTMLImageButtonNode( BufferedImageUtils.toBufferedImage( icon.toImage() ) ) {{
            setBackground( RefreshButtonNode.BUTTON_COLOR );
            addActionListener( new ActionListener() {
                @Override public void actionPerformed( final ActionEvent e ) {
                    pressed.apply();
                }
            } );
        }} );
    }

    private class RowNode extends HBox {
        private RowNode() {
            super( 3 );
            for ( int i = 0; i < 3; i++ ) {
                addChild( path() );
            }
        }
    }

    private PhetPPath path() {return new PhetPPath( new Rectangle2D.Double( 0, 0, 6, 10 ), Color.black );}
}