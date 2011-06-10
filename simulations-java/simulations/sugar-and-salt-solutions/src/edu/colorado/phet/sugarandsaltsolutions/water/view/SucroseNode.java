// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.chemistry.molecules.AtomNode;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.water.model.Atom;
import edu.colorado.phet.sugarandsaltsolutions.water.model.Sucrose;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.chemistry.model.Element.H;
import static edu.colorado.phet.chemistry.model.Element.O;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.toBufferedImage;
import static edu.colorado.phet.sugarandsaltsolutions.water.model.WaterMolecule.hydrogenRadius;
import static edu.colorado.phet.sugarandsaltsolutions.water.model.WaterMolecule.oxygenRadius;

/**
 * Draws a single water molecule, including a circle for each of the O, H, H
 *
 * @author Sam Reid
 */
public class SucroseNode extends PNode {

    //Preload the images statically to save processor time during startup.  Use high resolution images here, and scale them down so they'll have good quality
    private static final BufferedImage OXYGEN_IMAGE = toBufferedImage( new AtomNode( 1000, O.getColor() ).toImage() );
    private static final BufferedImage HYDROGEN_IMAGE = toBufferedImage( new AtomNode( 1000, H.getColor() ).toImage() );
    private static final BufferedImage CARBON_IMAGE = toBufferedImage( new AtomNode( 1000, Color.gray ).toImage() );

    public SucroseNode( final ModelViewTransform transform, final Sucrose sucrose, VoidFunction1<VoidFunction0> addListener ) {

        //Get the diameters in view coordinates
        double oxygenDiameter = transform.modelToViewDeltaX( oxygenRadius * 2 );
        double hydrogenDiameter = transform.modelToViewDeltaX( hydrogenRadius * 2 );

        ArrayList<PNode> children = new ArrayList<PNode>();
        //Create shapes for O, H, H
        //Use images from chemistry since they look shaded and good colors
        for ( Atom oxygen : sucrose.getOxygens() ) {
            children.add( new AtomImage( OXYGEN_IMAGE, oxygenDiameter, oxygen, addListener, transform ) );
        }
        for ( Atom hydrogen : sucrose.getHydrogens() ) {
            children.add( new AtomImage( HYDROGEN_IMAGE, hydrogenDiameter, hydrogen, addListener, transform ) );
        }
        for ( Atom carbon : sucrose.getCarbons() ) {
            children.add( new AtomImage( CARBON_IMAGE, oxygenDiameter, carbon, addListener, transform ) );
        }

        //Put in random order so that all atoms of one type are not in same layer (like all hydrogens being at the front)
        //We may want to be able to specify the z-ordering later, but we'll see if this is good enough first.
        Collections.shuffle( children );
        for ( PNode child : children ) {
            addChild( child );
        }
    }
}