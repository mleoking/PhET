//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.BuildAMoleculeResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Displays a pair of scissors that can be "open" or "closed"
 */
public class ScissorsNode extends PNode {

    private PImage scissorsOpenImage;
    private PImage scissorsClosedImage;

    public ScissorsNode() {
        scissorsOpenImage = new PImage( BuildAMoleculeResources.getImage( BuildAMoleculeConstants.IMAGE_SCISSORS_ICON ) ) {{
            centerFullBoundsOnPoint( 0, 0 );
        }};
        addChild( scissorsOpenImage );
        scissorsClosedImage = new PImage( BuildAMoleculeResources.getImage( BuildAMoleculeConstants.IMAGE_SCISSORS_CLOSED_ICON ) ) {{
            centerFullBoundsOnPoint( 0, 0 );
        }};
        addChild( scissorsClosedImage );

        // default to "open"
        scissorsClosedImage.setVisible( false );
    }

    public void setClosed( boolean closed ) {
        scissorsOpenImage.setVisible( !closed );
        scissorsClosedImage.setVisible( closed );
    }

    @Override public void setPickable( boolean isPickable ) {
        super.setPickable( isPickable );
        scissorsOpenImage.setPickable( isPickable );
        scissorsClosedImage.setPickable( isPickable );
    }
}
