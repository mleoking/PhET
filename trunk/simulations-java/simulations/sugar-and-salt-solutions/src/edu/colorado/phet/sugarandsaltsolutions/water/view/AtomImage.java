// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.water.model.Atom;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;

/**
 * Image for a single atom, used in a MoleculeNode. Use images from chemistry since they look shaded and good colors
 *
 * @author Sam Reid
 */
public class AtomImage extends PNode {
    public AtomImage( BufferedImage image, double diameter, final Atom atom, VoidFunction1<VoidFunction0> addListener, final ModelViewTransform transform ) {
        addChild( new PImage( multiScaleToWidth( image, (int) diameter ) ) );
        addListener.apply( new VoidFunction0() {
            public void apply() {
                //Set the location of the oxygen atom
                ImmutableVector2D position = transform.modelToView( atom.position.get() );
                setOffset( position.getX() - getFullBounds().getWidth() / 2, position.getY() - getFullBounds().getHeight() / 2 );
            }
        } );
    }
}