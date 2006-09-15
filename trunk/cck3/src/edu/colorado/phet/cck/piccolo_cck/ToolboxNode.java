package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKLookAndFeel;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Wire;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:12:28 PM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class ToolboxNode extends PhetPNode {
    private PPath toolboxBounds;
    private CCKModel model;

    public ToolboxNode( CCKModel model ) {
        this.model = model;
        this.toolboxBounds = new PPath( new Rectangle( 100, 600 ) );
        toolboxBounds.setStroke( new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL ) );
        toolboxBounds.setPaint( CCKLookAndFeel.toolboxColor );
        addChild( toolboxBounds );

        WireMaker wireMaker = new WireMaker();
        addBranchMaker( wireMaker );
    }

    private void addBranchMaker( BranchMaker branchMaker ) {
        branchMaker.setOffset( toolboxBounds.getFullBounds().getWidth() / 2 - branchMaker.getFullBounds().getWidth() / 2, 50 );
        addChild( branchMaker );
    }

    class BranchMaker extends PhetPNode {
        private PText label;

        public BranchMaker( String name ) {
            label = new PText( name );
            label.setFont( new Font( "Lucida Sans", Font.PLAIN, 8 ) );
        }

        public void setDisplayGraphic( PNode child ) {
            addChild( child );
            addChild( label );
            double labelInsetDY = 4;
            label.setOffset( child.getFullBounds().getWidth() / 2 - label.getFullBounds().getWidth() / 2, child.getFullBounds().getMaxY() + labelInsetDY );
        }
    }

    class WireMaker extends BranchMaker {
        public WireMaker() {
            super( "Wire" );
            WireNode child = new WireNode( new Wire( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1.5, 0 ) ) );
            child.scale( 30 );
            setDisplayGraphic( child );
        }
    }
}
