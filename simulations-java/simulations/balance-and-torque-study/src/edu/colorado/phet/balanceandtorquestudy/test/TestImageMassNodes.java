// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.test;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.balanceandtorquestudy.balancelab.view.ImageMassNode;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Barrel;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.BigRock;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Boy;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.CinderBlock;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.FireHydrant;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.FlowerPot;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Girl;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.ImageMass;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.LargeBucket;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.LargeTrashCan;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Man;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.MediumBucket;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.MediumRock;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.MediumTrashCan;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.PottedPlant;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.SmallBucket;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.SmallRock;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.SodaBottle;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Television;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.TinyRock;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Tire;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Woman;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class puts all image mass nodes on a canvas so that they can be
 * checked for reasonable relative size and so that any warning messages
 * related to scaling can be checked.
 *
 * @author John Blanco
 */
public class TestImageMassNodes {

    private static final Dimension2D STAGE_SIZE = new PDimension( 800, 700 );
    private static final double INTER_NODE_SPACING_X = 100;
    private static final double INTER_NODE_SPACING_Y = 300;
    private static final BooleanProperty LABEL_VISIBILITY_PROPERTY = new BooleanProperty( true );
    private static double xOffset = INTER_NODE_SPACING_X / 2;
    private static double yOffset = INTER_NODE_SPACING_Y / 4;

    private static void addNextImageMassNode( ImageMass imageMass, PhetPCanvas canvas, ModelViewTransform mvt ) {
        PNode pnode = new ImageMassNode( mvt, imageMass, canvas, LABEL_VISIBILITY_PROPERTY );
        pnode.setOffset( xOffset, yOffset );
        canvas.addWorldChild( pnode );

        xOffset += INTER_NODE_SPACING_X;
        if ( xOffset > STAGE_SIZE.getWidth() - INTER_NODE_SPACING_X / 2 ) {
            xOffset = INTER_NODE_SPACING_X / 2;
            yOffset += INTER_NODE_SPACING_Y;
        }
    }

    /**
     * Main routine that constructs a PhET Piccolo canvas in a window.
     *
     * @param args
     */
    public static void main( String[] args ) {


        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, STAGE_SIZE ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.50 ) ),
                150 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        canvas.getLayer().addChild( new PhetPPath( new Rectangle2D.Double( -5, -5, 10, 10 ), Color.PINK ) );

        addNextImageMassNode( new TinyRock( false ), canvas, mvt );
        addNextImageMassNode( new SmallRock( false ), canvas, mvt );
        addNextImageMassNode( new MediumRock( false ), canvas, mvt );
        addNextImageMassNode( new BigRock( false ), canvas, mvt );
        addNextImageMassNode( new Boy(), canvas, mvt );
        addNextImageMassNode( new Girl(), canvas, mvt );
        addNextImageMassNode( new Man(), canvas, mvt );
        addNextImageMassNode( new Woman(), canvas, mvt );

        addNextImageMassNode( new Barrel( false ), canvas, mvt );
        addNextImageMassNode( new CinderBlock( false ), canvas, mvt );

        addNextImageMassNode( new FireHydrant( false ), canvas, mvt );
        addNextImageMassNode( new Television( false ), canvas, mvt );
        addNextImageMassNode( new LargeTrashCan( false ), canvas, mvt );
        addNextImageMassNode( new MediumTrashCan( false ), canvas, mvt );
        addNextImageMassNode( new FlowerPot( false ), canvas, mvt );
        addNextImageMassNode( new SmallBucket( false ), canvas, mvt );
        addNextImageMassNode( new MediumBucket( false ), canvas, mvt );
        addNextImageMassNode( new LargeBucket( false ), canvas, mvt );
        addNextImageMassNode( new PottedPlant( false ), canvas, mvt );
        addNextImageMassNode( new SodaBottle( false ), canvas, mvt );
        addNextImageMassNode( new Tire( false ), canvas, mvt );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) STAGE_SIZE.getWidth(), (int) STAGE_SIZE.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }
}
