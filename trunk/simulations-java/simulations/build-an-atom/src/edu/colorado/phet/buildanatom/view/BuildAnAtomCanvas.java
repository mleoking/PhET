/* Copyright 2007, University of Colorado */

package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.module.BuildAnAtomDefaults;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the tab where the user builds an atom.
 */
public class BuildAnAtomCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private final BuildAnAtomModel model;

    // View
    private final PNode rootNode;

    // Transform.
    private final ModelViewTransform2D mvt;


    // Stroke for drawing the electron shells.
    private final Stroke ELECTRON_SHELL_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
            new float[] {3,3}, 0 );

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildAnAtomCanvas( BuildAnAtomModel model ) {

        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this, BuildAnAtomDefaults.STAGE_SIZE ) );

//        mvt = new ModelViewTransform2D( model.getModelViewport(),
//                new Rectangle2D.Double( 0, BuildAnAtomDefaults.STAGE_SIZE.getHeight() * ( 1 - 0.8 ),
//                BuildAnAtomDefaults.STAGE_SIZE.getWidth() * 0.7, BuildAnAtomDefaults.STAGE_SIZE.getHeight() * 0.7 ) );

        // Set up the model-canvas transform.  IMPORTANT NOTES: The multiplier
        // factors for the point in the view can be adjusted to shift the
        // center right or left, and the scale factor can be adjusted to zoom
        // in or out (smaller numbers zoom out, larger ones zoom in).
        mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.25 ), (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.45 ) ),
                2.0,
                true );

        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Put up the bounds of the model.
//        rootNode.addChild( new PhetPPath( mvt.createTransformedShape( model.getModelViewport() ), Color.PINK, new BasicStroke( 3f ), Color.BLACK ) );

        // Add the atom's nucleus location to the canvas.
        Shape nucleusOutlineShape = mvt.createTransformedShape( new Ellipse2D.Double(
                -model.getAtom().getNucleusRadius(),
                -model.getAtom().getNucleusRadius(),
                model.getAtom().getNucleusRadius() * 2,
                model.getAtom().getNucleusRadius() * 2 ) );
        PNode nucleusOutlineNode = new PhetPPath( nucleusOutlineShape, new BasicStroke(1f), Color.RED );
//        nucleusOutlineNode.setOffset( model.getAtom().getPosition() );
        rootNode.addChild( nucleusOutlineNode );

        // Add the atom's electron shells to the canvas.
        for (Double shellRadius : model.getAtom().getElectronShellRadii()){
            Shape electronShellShape = mvt.createTransformedShape( new Ellipse2D.Double(
                    -shellRadius,
                    -shellRadius,
                    shellRadius * 2,
                    shellRadius * 2));
            PNode electronShellNode = new PhetPPath( electronShellShape, ELECTRON_SHELL_STROKE, Color.BLUE );
            electronShellNode.setOffset( model.getAtom().getPosition() );
            rootNode.addChild( electronShellNode );
        }

        // Add the buckets for holding the sub-atomic particles to the canvas.
        BucketNode electronBucketNode = new BucketNode( model.getElectronBucket(), mvt );
        electronBucketNode.setOffset( mvt.modelToViewDouble( model.getElectronBucket().getPosition() ) );
        rootNode.addChild( electronBucketNode );
        BucketNode protonBucketNode = new BucketNode( model.getProtonBucket(), mvt );
        protonBucketNode.setOffset( mvt.modelToViewDouble( model.getProtonBucket().getPosition() ) );
        rootNode.addChild( protonBucketNode );
        BucketNode neutronBucketNode = new BucketNode( model.getNeutronBucket(), mvt );
        neutronBucketNode.setOffset( mvt.modelToViewDouble( model.getNeutronBucket().getPosition() ) );
        rootNode.addChild( neutronBucketNode );

        // TODO: Temp - put a sketch of the tab up as a very early prototype.
        //        PImage image = new PImage( BuildAnAtomResources.getImage( "tab-1-sketch-01.png" ));
        //        image.scale( 2.05 );
        //        PImage image = new PImage( BuildAnAtomResources.getImage( "first-tab-of-build-an-atom-sim-with-color.png" ));
        //        image.scale( 1.2 );
        //        image.setOffset( 50, 0 );
        //        rootNode.addChild(image);

        for ( int i = 0; i < model.numElectrons(); i++ ) {
            rootNode.addChild( new ElectronNode( mvt, model.getElectron( i ) ) );
        }

        for ( int i = 0; i < model.numProtons(); i++ ) {
            rootNode.addChild( new ProtonNode( mvt, model.getProton( i ) ) );
        }

        for ( int i = 0; i < model.numNeutrons(); i++ ) {
            rootNode.addChild( new NeutronNode( mvt, model.getNeutron( i ) ) );
        }
    }


    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------

    /*
     * Updates the layout of stuff on the canvas.
     */
    @Override
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( BuildAnAtomConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        //XXX lay out nodes
    }
}
