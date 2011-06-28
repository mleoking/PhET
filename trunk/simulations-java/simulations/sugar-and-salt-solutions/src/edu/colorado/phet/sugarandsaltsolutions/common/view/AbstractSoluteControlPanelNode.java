// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author John Blanco
 */
public class AbstractSoluteControlPanelNode extends WhiteControlPanelNode {
    public AbstractSoluteControlPanelNode( PNode soluteSelector ) {
        super( new VBox(
                new PText( SugarAndSaltSolutionsResources.Strings.SOLUTE ) {{setFont( SugarAndSaltSolutionsCanvas.TITLE_FONT );}},
                new PhetPPath( new Rectangle( 0, 0, 0, 0 ), new Color( 0, 0, 0, 0 ) ),//spacer
                soluteSelector
        ) );
    }
}
