// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.GradientPaint;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkLayer;
import edu.colorado.phet.energyformsandchanges.energysystems.model.FaucetAndWater;
import edu.colorado.phet.energyformsandchanges.energysystems.model.WaterDrop;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Piccolo node that represents a drop of water in the view.
 *
 * @author John Blanco
 */
public class WaterDropNode extends PositionableFadableModelElementNode {

    public WaterDropNode( WaterDrop waterDrop, final ModelViewTransform mvt ) {
        super( waterDrop, mvt );

        final PPath waterDropNode = new PhetPPath( EFACConstants.WATER_COLOR );
        waterDrop.size.addObserver( new VoidFunction1<Dimension2D>() {
            public void apply( Dimension2D dropSize ) {
                double dropWidth = mvt.modelToViewDeltaX( dropSize.getWidth() );
                double dropHeight = -mvt.modelToViewDeltaY( dropSize.getHeight() );
                waterDropNode.setPathTo( new Ellipse2D.Double( -dropWidth / 2, -dropHeight / 2, dropWidth, dropHeight ) );
            }
        } );
        addChild( waterDropNode );
   }
}