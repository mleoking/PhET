package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.view.PopulationGraphNode;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.umd.cs.piccolo.PNode;

public class PopulationCanvas extends PhetPCanvas {

    private PNode rootNode;

    public static Dimension canvasSize = new Dimension( 80, 200 );

    public PopulationCanvas() {
        super( canvasSize );

        rootNode = new PNode();
        addWorldChild( rootNode );

        rootNode.addChild( new PopulationGraphNode( 23 ) );

        setPreferredSize( canvasSize );

        setBorder( null );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
    }

    public void updateLayout() {
        
    }
}
