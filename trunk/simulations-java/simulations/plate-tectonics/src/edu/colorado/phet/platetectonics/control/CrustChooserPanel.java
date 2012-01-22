// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Shows crust pieces that can be dragged out into the
 */
public class CrustChooserPanel extends PNode {
    public static final int CRUST_AREA_MAX_WIDTH = 140;
    public static final int CRUST_AREA_MAX_HEIGHT = 100;
    public static final int CRUST_AREA_PADDING = 20;
    private final int verticalOffset;
    private final Spacer continentalSpacer;
    private final Spacer youngOceanicSpacer;
    private final Spacer oldOceanicSpacer;

    public CrustChooserPanel() {
        PNode continentalLabel = new PText( "Continental Crust" );
        PNode youngOceanicLabel = new PText( "Young Oceanic Crust" );
        PNode oldOceanicLabel = new PText( "Old Oceanic Crust" );

        addChild( continentalLabel );
        addChild( youngOceanicLabel );
        addChild( oldOceanicLabel );

        continentalLabel.setOffset( ( CRUST_AREA_MAX_WIDTH - continentalLabel.getFullBounds().getWidth() ) / 2, 0 );
        youngOceanicLabel.setOffset( ( CRUST_AREA_MAX_WIDTH - youngOceanicLabel.getFullBounds().getWidth() ) / 2
                                     + CRUST_AREA_PADDING + CRUST_AREA_MAX_WIDTH, 0 );
        oldOceanicLabel.setOffset( ( CRUST_AREA_MAX_WIDTH - oldOceanicLabel.getFullBounds().getWidth() ) / 2
                                   + ( CRUST_AREA_PADDING + CRUST_AREA_MAX_WIDTH ) * 2, 0 );

        verticalOffset = (int) ( continentalLabel.getFullBounds().getHeight() + 10 );

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
