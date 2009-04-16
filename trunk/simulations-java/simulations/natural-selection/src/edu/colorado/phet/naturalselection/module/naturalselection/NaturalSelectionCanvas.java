package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.view.TestVanillaBunnyNode;
import edu.colorado.phet.naturalselection.view.BunniesNode;
import edu.umd.cs.piccolo.PNode;

public class NaturalSelectionCanvas extends PhetPCanvas {

    private NaturalSelectionModel model;

    private PNode rootNode;
    public BunniesNode bunnies;

    public NaturalSelectionCanvas( NaturalSelectionModel _model ) {

        super( NaturalSelectionDefaults.VIEW_SIZE );

        model = _model;

        setBackground( NaturalSelectionConstants.CANVAS_BACKGROUND );

        rootNode = new PNode();
        addWorldChild( rootNode );

        bunnies = new BunniesNode();
        rootNode.addChild( bunnies );

        /*
        TestVanillaBunnyNode bunny = new TestVanillaBunnyNode();
        bunny.setOffset( 200, 200 );
        rootNode.addChild( bunny );
        */

    }

    public void reset() {
        bunnies.reset();
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
