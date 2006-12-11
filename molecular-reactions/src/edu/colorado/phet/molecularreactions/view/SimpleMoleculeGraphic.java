/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.Profiles;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.IOException;
import java.util.HashMap;

/**
 * SimpleMoleculeGraphic
 * <p/>
 * Base class used in the spatial and energy views for the graphics for simple molecules
 * <p/>
 * The buffered images used for the PImage nodes are shared between all instances for each
 * type of molecule. This is the flyweight GOF design pattern.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class SimpleMoleculeGraphic extends PNode implements SimpleObserver, SimpleMolecule.ChangeListener {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static double BOND_OFFSET = 0;
    private static HashMap moleculeTypeToColor = new HashMap();
    private static HashMap moleculeTypeToAnnotation = new HashMap();
    private static Color moleculeAColor = new Color( 240, 240, 0 );
    private static Color moleculeBColor = new Color( 0, 200, 0 );
    private static Color moleculeCColor = new Color( 100, 100, 240 );
    private static Color defaultMoleculeColor = new Color( 100, 100, 100 );
    private static Stroke defaultStroke = new BasicStroke( 0f );
//    private static Stroke defaultStroke = new BasicStroke( 1f );
    private static Stroke selectedStroke = new BasicStroke( 2 );
    private static Stroke nearestToSelectedStroke = new BasicStroke( 2 );
    private static Paint defaultStrokePaint = Color.black;
    private static Paint selectedStrokePaint = Color.magenta;
    private static Paint cmPaint = new Color( 60, 180, 00);
//    private static Paint cmPaint = Color.magenta;
    private static Paint nearestToSelectedStrokePaint = new Color( 255, 0, 255 );
//    private static boolean MARK_SELECTED_MOLECULE = true;
    private static boolean MARK_SELECTED_MOLECULE = false;

    private static BufferedImage moleculeAImg;
    private static BufferedImage moleculeBImg;
    private static BufferedImage moleculeCImg;

    static {
        try {
            moleculeAImg = ImageLoader.loadBufferedImage( "images/glass-molecule-A.png" );
            moleculeBImg = ImageLoader.loadBufferedImage( "images/glass-molecule-B.png" );
            moleculeCImg = ImageLoader.loadBufferedImage( "images/glass-molecule-C.png" );

            RenderingHints hints = new RenderingHints( RenderingHints.KEY_INTERPOLATION,
                                                       RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
            double scaleA = new MoleculeA().getRadius() * 2 / moleculeAImg.getWidth();
            moleculeAImg = scaleImage( moleculeAImg, scaleA );

            double scaleB = new MoleculeB().getRadius() * 2 / moleculeBImg.getWidth();
            moleculeBImg = scaleImage( moleculeBImg, scaleB );

            double scaleC = new MoleculeC().getRadius() * 2 / moleculeCImg.getWidth();
            moleculeCImg = scaleImage( moleculeCImg, scaleC );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private static BufferedImage scaleImage( BufferedImage img, double scale) {
        AffineTransform atx = AffineTransform.getScaleInstance( scale, scale );
        BufferedImageOp op = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
        return op.filter( img, null );
    }

    static {
        SimpleMoleculeGraphic.moleculeTypeToColor.put( MoleculeA.class, SimpleMoleculeGraphic.moleculeAColor );
        SimpleMoleculeGraphic.moleculeTypeToColor.put( MoleculeB.class, SimpleMoleculeGraphic.moleculeBColor );
        SimpleMoleculeGraphic.moleculeTypeToColor.put( MoleculeC.class, SimpleMoleculeGraphic.moleculeCColor );

        SimpleMoleculeGraphic.moleculeTypeToAnnotation.put( MoleculeA.class, "A" );
        SimpleMoleculeGraphic.moleculeTypeToAnnotation.put( MoleculeB.class, "B" );
        SimpleMoleculeGraphic.moleculeTypeToAnnotation.put( MoleculeC.class, "C" );
    }

    private static Color getColor( SimpleMolecule molecule ) {
        Color color = (Color)moleculeTypeToColor.get( molecule.getClass() );
        if( color == null ) {
            color = defaultMoleculeColor;
        }
        return color;
    }

    private static String getAnnotation( SimpleMolecule molecule ) {
        String annotation = (String)moleculeTypeToAnnotation.get( molecule.getClass() );
        if( annotation == null ) {
            annotation = "";
        }
        return annotation;
    }

    public static void setMarkSelectedMolecule( boolean mark ) {
        SimpleMoleculeGraphic.MARK_SELECTED_MOLECULE = mark;
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private SimpleMolecule molecule;
    private PPath pPath;
    private PPath cmNode;
    private double cmRad = 4;
    private boolean glassMolecules = true;

    /**
     * @param molecule
     * @param profile
     */
    public SimpleMoleculeGraphic( SimpleMolecule molecule, EnergyProfile profile ) {
        this( molecule, profile, false );
    }

    /**
     * Constructor that provides option of the graphic being annotated with a letter that
     * indicates the type of molecule.
     *
     * @param molecule
     * @param profile
     * @param annotate
     */
    public SimpleMoleculeGraphic( SimpleMolecule molecule, EnergyProfile profile, boolean annotate ) {
        this.molecule = molecule;
        molecule.addObserver( this );
        molecule.addListener( this );

        if( glassMolecules ) {
            PImage moleculeNode = null;
            PImage labelNode = null;
            if( molecule instanceof MoleculeA ) {
                moleculeNode = new PImage( moleculeAImg );
                if( annotate ) {
                    labelNode = PImageFactory.create( "images/molecule-label-A.png" );
                }
            }
            if( molecule instanceof MoleculeB ) {
                moleculeNode = new PImage( moleculeBImg );
                if( annotate ) {
                    labelNode = PImageFactory.create( "images/molecule-label-B.png" );
                }
            }
            if( molecule instanceof MoleculeC ) {
                moleculeNode = new PImage( moleculeCImg );
                if( annotate ) {
                    labelNode = PImageFactory.create( "images/molecule-label-C.png" );
                }
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
                Color duotoneHue = MoleculePaints.getColor( molecule, profile);
                MakeDuotoneImageOp imgOp = new MakeDuotoneImageOp( duotoneHue );
                BufferedImage bi = imgOp.filter( (BufferedImage)moleculeNode.getImage(), null );
                moleculeNode.setImage( bi );
            }

            // Halo and CM mark for use when showing the "selected molecule"
            double radius = moleculeNode.getWidth() / 2;
            Shape s = new Ellipse2D.Double( -radius,
                                            -radius,
                                            radius * 2,
                                            radius * 2 );
            pPath = new PPath( s, selectedStroke );
            pPath.setStrokePaint( selectedStrokePaint );
            addChild( pPath );
            pPath.setVisible( false );

            // The CM marker
            cmNode = new PPath( new Ellipse2D.Double( -cmRad, -cmRad, cmRad * 2, cmRad * 2 ) );
            cmNode.setStrokePaint( null );
            cmNode.setPaint( cmPaint );
            cmNode.setVisible( false );
            addChild( cmNode );


        }
        else {
            double radius = molecule.getRadius() - BOND_OFFSET;
            Shape s = new Ellipse2D.Double( -radius,
                                            -radius,
                                            radius * 2,
                                            radius * 2 );
            pPath = new PPath( s, SimpleMoleculeGraphic.defaultStroke );
            pPath.setPaint( SimpleMoleculeGraphic.getColor( molecule ) );
            pPath.setStrokePaint( SimpleMoleculeGraphic.defaultStrokePaint );
            addChild( pPath );

            // The CM marker
            cmNode = new PPath( new Ellipse2D.Double( -cmRad, -cmRad, cmRad * 2, cmRad * 2 ) );
            cmNode.setPaint( cmPaint );
            cmNode.setVisible( false );
            addChild( cmNode );

            // Add annotation, if required
            if( annotate ) {
                PText annotation = new PText( getAnnotation( molecule ) );
                RegisterablePNode rNode = new RegisterablePNode( annotation );
                rNode.setRegistrationPoint( annotation.getWidth() / 2,
                                            annotation.getHeight() / 2 );
                addChild( rNode );
            }
        }

        update();
    }

    public SimpleMolecule getMolecule() {
        return molecule;
    }

    public void update() {
        // noop
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SimpleMolecule.Listener
    //--------------------------------------------------------------------------------------------------

    /**
     * Highlights the selected molecule and the nearest one to it that can
     * react with it
     *
     * @param molecule
     */
    public void selectionStatusChanged( SimpleMolecule molecule ) {

        if( MARK_SELECTED_MOLECULE ) {
            if( molecule.getSelectionStatus() == Selectable.SELECTED ) {
                if( glassMolecules ) {
                    pPath.setVisible( true );
                }
                else {
                    pPath.setStroke( SimpleMoleculeGraphic.selectedStroke );
                    pPath.setStrokePaint( SimpleMoleculeGraphic.selectedStrokePaint );
                }
            }
            else if( molecule.getSelectionStatus() == Selectable.NEAREST_TO_SELECTED ) {
                cmNode.setVisible( true );
//            pPath.setStroke( AbstractSimpleMoleculeGraphic.nearestToSelectedStroke );
//            pPath.setStrokePaint( AbstractSimpleMoleculeGraphic.nearestToSelectedStrokePaint );
            }
            else {
                cmNode.setVisible( false );
                if( glassMolecules ) {
                    pPath.setVisible( false );
                }
                else {
                    pPath.setStroke( SimpleMoleculeGraphic.defaultStroke );
                    pPath.setStrokePaint( SimpleMoleculeGraphic.defaultStrokePaint );
                }
            }
        }

        // for debugging
//        if( cmNode != null ) {
//            cmNode.setVisible( DebugFlags.SHOW_CM );
//        }

    }
}
