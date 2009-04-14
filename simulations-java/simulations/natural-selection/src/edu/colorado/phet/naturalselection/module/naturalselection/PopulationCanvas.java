package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.view.PopulationGraphNode;
import edu.umd.cs.piccolo.PNode;

public class PopulationCanvas extends PhetPCanvas {

    private PNode rootNode;

    public PopulationCanvas() {
        super( new Dimension( 80, 200 ) );

        rootNode = new PNode();
        addWorldChild( rootNode );

        rootNode.addChild( new PopulationGraphNode() );

        setPreferredSize( new Dimension( 80, 200 ) );

        setBorder( null );

        setBackground( new Color( 0xC9E5C6 ) );
    }

    public void updateLayout() {
        
    }
}
