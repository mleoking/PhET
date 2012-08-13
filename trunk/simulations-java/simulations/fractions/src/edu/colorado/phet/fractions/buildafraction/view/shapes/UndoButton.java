// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.fractions.common.view.RefreshButtonNode.BUTTON_COLOR;

/**
 * @author Sam Reid
 */
public class UndoButton extends PNode {

    private final HTMLImageButtonNode buttonNode;

    public UndoButton( final IUserComponent component ) {
        buttonNode = new HTMLImageButtonNode( multiScaleToWidth( Images.UNDO, 26 ) ) {{
            setBackground( BUTTON_COLOR );
            setUserComponent( component );
        }};
        addChild( buttonNode );
    }

    public void addActionListener( final ActionListener listener ) {
        buttonNode.addActionListener( listener );
    }
}