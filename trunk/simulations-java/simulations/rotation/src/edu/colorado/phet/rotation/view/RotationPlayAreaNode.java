package edu.colorado.phet.rotation.view;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;

import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.RotationResources;
import edu.colorado.phet.rotation.RotationStrings;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:52:02 AM
 */

public class RotationPlayAreaNode extends PNode {

    private PNode rotationPlatformNode;
    private PNode rotationBodyLayer = new PNode();
    private RotationModel rotationModel;
    private PNode vectorLayer = new PNode();
    private RotationOriginNode originNode;
    private RotationRulerNode rulerNode;
//    private PauseNode pauseNode = new PauseNode();

    public static final double SCALE = 3.0 / 200.0;
    private CircleNode circularMotionNode;

    PInputEventListener startSimWhenInteracting = new PBasicInputEventHandler() {
        public void mouseDragged( PInputEvent event ) {
            rotationModel.startRecording();
        }
    };

    public RotationPlayAreaNode( final RotationModel rotationModel, VectorViewModel vectorViewModel, AngleUnitModel angleUnitModel ) {
        this.rotationModel = rotationModel;
        rotationPlatformNode = createRotationPlatformNode( rotationModel.getRotationPlatform() );
//        new Timer(30,new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                System.out.println( "rotationPlatformNode.getGlobalFullBounds() = " + rotationPlatformNode.getGlobalFullBounds() );
//            }
//        } ).start();
//        rotationPlatformNode = new BufferedRotationPlatformNode( rotationModel.getRotationPlatform() );
        originNode = new RotationOriginNode( rotationModel.getRotationPlatform(), angleUnitModel );
        rulerNode = new RotationRulerNode( rotationModel.getRotationPlatform().getRadius() * 2, 50 * SCALE, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8"}, RotationStrings.getString( "units.mm" ), 4, 14 );
        rulerNode.setTransform( AffineTransform.getScaleInstance( 1, -1 ) );
//        rulerNode.setOffset( -RotationPlatform.DEFAULT_OUTER_RADIUS, 0 );
        rulerNode.setOffset( -rulerNode.getFullBounds().getWidth() / 2, 0 );
        rulerNode.setVisible( false );

        addChild( rotationPlatformNode );


        for ( int i = 0; i < rotationModel.getNumRotationBodies(); i++ ) {
            addRotationBodyNode( rotationModel.getRotationBody( i ) );
        }
        for ( int i = 0; i < rotationModel.getNumRotationBodies(); i++ ) {
            addVectorNode( rotationModel.getRotationBody( i ), vectorViewModel );
        }
        circularMotionNode = new CircleNode( rotationModel );
        circularMotionNode.setVisible( false );


        setTransform( AffineTransform.getScaleInstance( 1, -1 ) );

//        addFlowerNodes( rotationModel );

        addChild( rotationBodyLayer );
        addChild( vectorLayer );
        addChild( originNode );
        addChild( rulerNode );

        addChild( circularMotionNode );
//        pauseNode.setScale( SCALE * 3.5 * 2.5 );
//        pauseNode.setOffset( -RotationPlatform.MAX_RADIUS, RotationPlatform.MAX_RADIUS
//                                                           - pauseNode.getFullBounds().getHeight()
//        );
//        rotationModel.getClock().addClockListener( new ClockAdapter() {
//            public void clockPaused( ClockEvent clockEvent ) {
//                updatePauseNode();
//            }
//
//            public void clockStarted( ClockEvent clockEvent ) {
//                updatePauseNode();
//            }
//        } );
//        updatePauseNode();
//        addChild( pauseNode );

        rotationPlatformNode.addInputEventListener( startSimWhenInteracting );
    }

//    private void updatePauseNode() {
//        pauseNode.setVisible( rotationModel.getClock().isPaused() );
//    }

    private void addFlowerNodes( RotationModel rotationModel ) {
        addChild( new FlowerNode( "flower1.gif", 0.5, -rotationModel.getRotationPlatform().getRadius(), rotationModel.getRotationPlatform().getRadius() * 0.8 ) );
        addChild( new FlowerNode( "flower2.gif", 0.32, -rotationModel.getRotationPlatform().getRadius(), -rotationModel.getRotationPlatform().getRadius() * 0.8 ) );
        addChild( new FlowerNode( "flower2.gif", 0.39, rotationModel.getRotationPlatform().getRadius() * 0.7, rotationModel.getRotationPlatform().getRadius() * 1.1 ) );
    }

    static class FlowerNode extends PNode {
        public FlowerNode( String image, double scale, double x, double y ) {
            try {
                BufferedImage bufferedImage = RotationResources.loadBufferedImage( image );
                bufferedImage = filterFlower( bufferedImage );
                PImage flower1 = new PImage( bufferedImage );
                final double flowerscale1 = SCALE * scale;
                flower1.scale( flowerscale1 );
                flower1.translate( -flower1.getFullBounds().getWidth() / flowerscale1 / 2, -flower1.getFullBounds().getHeight() / flowerscale1 / 2 );
                flower1.translate( x / flowerscale1, y / flowerscale1 );
                addChild( flower1 );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }

        private BufferedImage filterFlower( BufferedImage bufferedImage ) {
            BufferedImage bim = new BufferedImage( bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB );
            bim.createGraphics().drawRenderedImage( bufferedImage, new AffineTransform() );
//                System.out.println( "bufferedImage.getType() = " + bufferedImage.getType() );

            /* Create a rescale filter op that makes the image 50% opaque */
//                float[] scales = {1f, 1f, 0.9f, 0.5f};
            float colorscale = 1.75f;
            float[] scales = {colorscale, colorscale, colorscale, 0.275f};
            float[] offsets = new float[]{0.1f, 0.1f, 0.1f, 0.0f};
            RescaleOp rop = new RescaleOp( scales, offsets, null );

//                PImage flower1 = new PImage( bufferedImage );
            BufferedImage c = rop.filter( bim, null );
            return c;
        }
    }

    protected PNode createRotationPlatformNode( RotationPlatform rotationPlatform ) {
        return new RotationPlatformNodeWithHandle( rotationPlatform );
    }

    public CircleNode getCircularMotionNode() {
        return circularMotionNode;
    }

    private void addVectorNode( RotationBody rotationBody, VectorViewModel vectorViewModel ) {
        vectorLayer.addChild( new BodyVectorLayer( rotationModel, rotationBody, vectorViewModel ) );
    }

    public PNode getPlatformNode() {
        return rotationPlatformNode;
    }

    private void addRotationBodyNode( RotationBody rotationBody ) {
        final RotationBodyNode child = new RotationBodyNode( rotationModel, rotationBody );
        child.addInputEventListener( startSimWhenInteracting );
        rotationBodyLayer.addChild( child );
    }

    public RulerNode getRulerNode() {
        return rulerNode;
    }

    public PNode getOriginNode() {
        return originNode;
    }

    public void setOriginNodeVisible( boolean visible ) {
        originNode.setVisible( visible );
    }

    public void resetAll() {
        rulerNode.setVisible( false );
    }
}
