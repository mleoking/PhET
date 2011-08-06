// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import edu.colorado.phet.chemistry.molecules.AtomNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.sugarandsaltsolutions.water.model.DefaultParticle;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.toBufferedImage;

/**
 * Draws a single water particle (probably an ion) as a circle
 * TODO: share duplicated code with WaterMoleculeNode
 *
 * @author Sam Reid
 */
public class DefaultParticleNode extends PNode {

    public DefaultParticleNode( final ModelViewTransform transform, final DefaultParticle particle, VoidFunction1<VoidFunction0> addListener, final Element element ) {

        //Get the diameters in view coordinates
        double diameter = transform.modelToViewDeltaX( particle.radius * 2 );

        //Use images from chemistry since they look shaded and good colors
        final PImage image = new PImage( multiScaleToWidth( toBufferedImage( new AtomNode( element.getRadius(), element.getColor() ) {{

            //Show the label inside the particle
            addChild( new PText( element.name ) {{setOffset( -getFullBounds().getWidth() / 2, -getFullBounds().getHeight() / 2 );}} );

            //increase the resolution so when it is rasterized and scaled up it won't be blocky
            scale( 2.0 );
        }}.toImage() ), (int) diameter ) );

        //Update the graphics for the updated model objects
        VoidFunction0 update = new VoidFunction0() {
            public void apply() {
                //Compute angle and position of the molecule
                ImmutableVector2D oxygenPosition = particle.position.get();

                //Set the location of the oxygen atom
                ImmutableVector2D viewPosition = transform.modelToView( oxygenPosition );
                image.setOffset( viewPosition.getX() - image.getFullBounds().getWidth() / 2, viewPosition.getY() - image.getFullBounds().getHeight() / 2 );
            }
        };
        addListener.apply( update );

        //Also update initially
        update.apply();

        addChild( image );

        //Mouse interaction, makes it draggable
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                particle.setGrabbed( true );
            }

            @Override public void mouseDragged( PInputEvent event ) {
                particle.translate( transform.viewToModelDelta( event.getDeltaRelativeTo( DefaultParticleNode.this.getParent() ) ) );
            }

            @Override public void mouseReleased( PInputEvent event ) {
                particle.setGrabbed( false );
            }
        } );
    }
}