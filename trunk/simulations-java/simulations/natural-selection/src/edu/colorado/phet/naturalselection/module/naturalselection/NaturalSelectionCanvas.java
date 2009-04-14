package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.defaults.ExampleDefaults;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.view.TestVanillaBunnyNode;
import edu.umd.cs.piccolo.PNode;

public class NaturalSelectionCanvas extends PhetPCanvas {

    private NaturalSelectionModel model;

    private PNode rootNode;

    public NaturalSelectionCanvas( NaturalSelectionModel _model ) {

        super( NaturalSelectionDefaults.VIEW_SIZE );
        
        model = _model;

        setBackground( NaturalSelectionConstants.CANVAS_BACKGROUND );

        rootNode = new PNode();
        addWorldChild( rootNode );

        TestVanillaBunnyNode bunny = new TestVanillaBunnyNode();

        bunny.setOffset( 200, 200 );
        
        rootNode.addChild( bunny );

    }

    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( NaturalSelectionConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        //XXX lay out nodes
    }
}
