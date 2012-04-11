// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Panel that contains crust pieces that can be dragged out into the play area and dropped onto either the left or right side.
 * <p/>
 * IMPORTANT NOTE: the crust pieces are not actually contained inside this panel, since they need to be draggable outside of the panel.
 * They are placed on top, and this class just provides the coordinates for positioning them.
 */
public class CrustChooserPanel extends PNode {
    // size and padding for the icons
    public static final int CRUST_AREA_MAX_WIDTH = 140;
    public static final int CRUST_AREA_MAX_HEIGHT = 100;
    public static final int CRUST_AREA_PADDING = 20;

    // how much horizontal room we need for labeling
    private final int verticalOffset;

    // spacers to enforce the layout
    private final Spacer continentalSpacer;
    private final Spacer youngOceanicSpacer;
    private final Spacer oldOceanicSpacer;

    public CrustChooserPanel() {
        PNode continentalLabel = new PText( Strings.CONTINENTAL_CRUST );
        PNode youngOceanicLabel = new PText( Strings.YOUNG_OCEANIC_CRUST );
        PNode oldOceanicLabel = new PText( Strings.OLD_OCEANIC_CRUST );

        addChild( continentalLabel );
        addChild( youngOceanicLabel );
        addChild( oldOceanicLabel );

        // center the labels above the draggable crust pieces
        continentalLabel.setOffset( ( CRUST_AREA_MAX_WIDTH - continentalLabel.getFullBounds().getWidth() ) / 2, 0 );
        youngOceanicLabel.setOffset( ( CRUST_AREA_MAX_WIDTH - youngOceanicLabel.getFullBounds().getWidth() ) / 2
                                     + CRUST_AREA_PADDING + CRUST_AREA_MAX_WIDTH, 0 );
        oldOceanicLabel.setOffset( ( CRUST_AREA_MAX_WIDTH - oldOceanicLabel.getFullBounds().getWidth() ) / 2
                                   + ( CRUST_AREA_PADDING + CRUST_AREA_MAX_WIDTH ) * 2, 0 );

        // padding between labels and crust pieces
        verticalOffset = (int) ( continentalLabel.getFullBounds().getHeight() + 10 );

        // add spacers so that the ControlPanelNode border accounts for the room
        // the crust pieces are added as separate draggable nodes, and thus are not contained within this panel
        continentalSpacer = new Spacer( 0, verticalOffset, CRUST_AREA_MAX_WIDTH, CRUST_AREA_MAX_HEIGHT );
        addChild( continentalSpacer );

        youngOceanicSpacer = new Spacer( ( CRUST_AREA_PADDING + CRUST_AREA_MAX_WIDTH ), verticalOffset, CRUST_AREA_MAX_WIDTH, CRUST_AREA_MAX_HEIGHT );
        addChild( youngOceanicSpacer );

        oldOceanicSpacer = new Spacer( ( CRUST_AREA_PADDING + CRUST_AREA_MAX_WIDTH ) * 2, verticalOffset, CRUST_AREA_MAX_WIDTH, CRUST_AREA_MAX_HEIGHT );
        addChild( oldOceanicSpacer );
    }

    private ImmutableVector2F getPieceCenter( Spacer node ) {
        Point2D globalPoint = node.localToGlobal( new Point2D.Double(
                node.getRectangle().getX() + CRUST_AREA_MAX_WIDTH / 2,
                node.getRectangle().getY() + CRUST_AREA_MAX_HEIGHT / 2 ) );
        return new ImmutableVector2F( globalPoint.getX(), globalPoint.getY() );
    }

    /*---------------------------------------------------------------------------*
    * coordinates for where the crust pieces should be placed
    *----------------------------------------------------------------------------*/
    public ImmutableVector2F getContinentalCenter() {
        return getPieceCenter( continentalSpacer );
    }

    public ImmutableVector2F getYoungOceanicCenter() {
        return getPieceCenter( youngOceanicSpacer );
    }

    public ImmutableVector2F getOldOceanicCenter() {
        return getPieceCenter( oldOceanicSpacer );
    }
}
