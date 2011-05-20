// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import org.jbox2d.common.Vec2;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.chemistry.molecules.AtomNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.toBufferedImage;

/**
 * Draws a single water molecule, including a circle for each of the O, H, H
 * TODO: share duplicated code with WaterMoleculeNode
 *
 * @author Sam Reid
 */
public class SodiumIonNode extends PNode {

    public SodiumIonNode( final ModelViewTransform transform, final SodiumIon sodiumIon, VoidFunction1<VoidFunction0> addListener ) {

        //Get the diameters in view coordinates
        double sodiumDiameter = transform.modelToViewDeltaX( sodiumIon.radius * 2 );

        //Use images from chemistry since they look shaded and good colors
        final PImage sodium = new PImage( multiScaleToWidth( toBufferedImage( new AtomNode( Element.N ).toImage() ), (int) sodiumDiameter ) );

        //Update the graphics for the updated model objects
        VoidFunction0 update = new VoidFunction0() {
            public void apply() {
                //Compute angle and position of the molecule
                ImmutableVector2D oxygenPosition = sodiumIon.position.get();

                //Set the location of the oxygen atom
                ImmutableVector2D viewPosition = transform.modelToView( oxygenPosition );
                sodium.setOffset( viewPosition.getX() - sodium.getFullBounds().getWidth() / 2, viewPosition.getY() - sodium.getFullBounds().getHeight() / 2 );
            }
        };
        addListener.apply( update );

        //Also update initially
        update.apply();

        addChild( sodium );
    }

    public static ImmutableVector2D toImmutableVector2D( Vec2 from ) {
        return new ImmutableVector2D( from.x, from.y );
    }
}
