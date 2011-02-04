// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.colorado.phet.lightreflectionandrefraction.view.LightRayNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class LightReflectionAndRefractionCanvas extends PhetPCanvas {
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );
    private PNode rootNode;

    public LightReflectionAndRefractionCanvas() {
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        setBackground( Color.black );
        double redWavelength = 650E-9;

        double modelWidth = redWavelength * 50;
        double modelHeight = STAGE_SIZE.getHeight() / STAGE_SIZE.getWidth() * modelWidth;
        ModelViewTransform transform = ModelViewTransform.createRectangleInvertedYMapping(
                new Rectangle2D.Double( -modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight ),
                new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ) );

        LightRay ray = new LightRay( new Property<ImmutableVector2D>( new ImmutableVector2D() ), new Property<ImmutableVector2D>( new ImmutableVector2D( modelWidth, modelHeight ) ) );
        LightRayNode rayNode = new LightRayNode( transform, ray );
        addChild( rayNode );
    }

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }
}
