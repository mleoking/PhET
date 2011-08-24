// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.STRING_RESET_ALL;
import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.getInstance;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.*;

/**
 * Reset all button used in all tabs, shown in the bottom right corner
 *
 * @author Sam Reid
 */
public class ResetAllButtonNode extends PNode {
    public ResetAllButtonNode( final double stageWidth, final double stageHeight, final VoidFunction0 reset ) {
        addChild( new HTMLImageButtonNode( getInstance().getLocalizedString( STRING_RESET_ALL ), BUTTON_COLOR ) {{
            setFont( CONTROL_FONT );

            //Have to set the offset after changing the font since it changes the size of the node
            setOffset( stageWidth - getFullBounds().getWidth() - INSET, stageHeight - getFullBounds().getHeight() - INSET );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    reset.apply();
                }
            } );
        }} );
    }
}