// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactionsandrates.view;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.reactionsandrates.model.EnergyProfile;
import edu.colorado.phet.reactionsandrates.model.MoleculeA;
import edu.colorado.phet.reactionsandrates.model.MoleculeB;
import edu.colorado.phet.reactionsandrates.model.MoleculeC;
import edu.colorado.phet.reactionsandrates.model.reactions.Profiles;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.IOException;

public class SimpleMoleculeGraphicNode extends PNode {
    private static BufferedImage A_IMAGE;
    private static BufferedImage B_IMAGE;
    private static BufferedImage C_IMAGE;
    private static BufferedImage A_LABEL_IMAGE;
    private static BufferedImage B_LABEL_IMAGE;
    private static BufferedImage C_LABEL_IMAGE;

    static {
        try {
            A_IMAGE = ImageLoader.loadBufferedImage( "reactions-and-rates/images/glass-molecule-A.png" );
            B_IMAGE = ImageLoader.loadBufferedImage( "reactions-and-rates/images/glass-molecule-B.png" );
            C_IMAGE = ImageLoader.loadBufferedImage( "reactions-and-rates/images/glass-molecule-C.png" );

            double scaleA = new MoleculeA().getRadius() * 2 / A_IMAGE.getWidth();
            A_IMAGE = scaleImage( A_IMAGE, scaleA );

            double scaleB = new MoleculeB().getRadius() * 2 / B_IMAGE.getWidth();
            B_IMAGE = scaleImage( B_IMAGE, scaleB );

            double scaleC = new MoleculeC().getRadius() * 2 / C_IMAGE.getWidth();
            C_IMAGE = scaleImage( C_IMAGE, scaleC );

            A_LABEL_IMAGE = ImageLoader.loadBufferedImage( "reactions-and-rates/images/molecule-label-A.png" );
            B_LABEL_IMAGE = ImageLoader.loadBufferedImage( "reactions-and-rates/images/molecule-label-B.png" );
            C_LABEL_IMAGE = ImageLoader.loadBufferedImage( "reactions-and-rates/images/molecule-label-C.png" );

        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private static BufferedImage scaleImage( BufferedImage img, double scale ) {
        AffineTransform atx = AffineTransform.getScaleInstance( scale, scale );
        BufferedImageOp op = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
        return op.filter( img, null );
    }

    public SimpleMoleculeGraphicNode( Class moleculeClass, EnergyProfile profile ) {
        this( moleculeClass, profile, false );
    }

    /*
     * Constructor that provides option of the graphic being annotated with a letter that
     * indicates the type of molecule.
     */
    public SimpleMoleculeGraphicNode( Class moleculeClass, EnergyProfile profile, boolean annotate ) {
        createGraphics( moleculeClass, annotate, profile );
    }

    private void createGraphics( Class moleculeClass, boolean annotate, EnergyProfile profile ) {
        PImage moleculeNode;

        PImage labelNode = null;

        if( moleculeClass == MoleculeA.class ) {
            moleculeNode = new PImage( A_IMAGE );

            if( annotate ) {
                labelNode = new PImage( A_LABEL_IMAGE );
            }
        }
        else if( moleculeClass == MoleculeB.class ) {
            moleculeNode = new PImage( B_IMAGE );

            if( annotate ) {
                labelNode = new PImage( B_LABEL_IMAGE );
            }
        }
        else if( moleculeClass == MoleculeC.class ) {
            moleculeNode = new PImage( C_IMAGE );

            if( annotate ) {
                labelNode = new PImage( C_LABEL_IMAGE );
            }
        }
        else {
            throw new InternalError();
        }

        moleculeNode.setOffset( -moleculeNode.getImage().getWidth( null ) / 2, -moleculeNode.getImage().getHeight( null ) / 2 );

        addChild( moleculeNode );

        if( labelNode != null ) {
            labelNode.setOffset( -labelNode.getFullBounds().getWidth() / 2,
                                 -labelNode.getFullBounds().getHeight() / 2 );
            addChild( labelNode );
        }

        // If we aren't using the default or "design your own" energy profile, we need to
        // change the color of the image
        if( profile != Profiles.DEFAULT && profile != Profiles.DYO ) {
            Color duotoneHue = MoleculePaints.getDuotoneHue( moleculeClass, profile );
            MakeDuotoneImageOp imgOp = new MakeDuotoneImageOp( duotoneHue );
            BufferedImage bi = imgOp.filter( (BufferedImage)moleculeNode.getImage(), null );
            moleculeNode.setImage( bi );
        }
    }

    public void update() {
        // noop
    }
}
