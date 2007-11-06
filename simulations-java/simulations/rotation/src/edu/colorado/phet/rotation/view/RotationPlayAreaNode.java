package edu.colorado.phet.rotation.view;

import java.awt.geom.AffineTransform;
import java.io.IOException;

import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.AngleUnitModel;
import edu.colorado.phet.rotation.RotationResources;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PNode;
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

    public static final double SCALE = 3.0 / 200.0;
    private CircleNode circularMotionNode;

    public RotationPlayAreaNode( final RotationModel rotationModel, VectorViewModel vectiorViewModel, AngleUnitModel angleUnitModel ) {
        this.rotationModel = rotationModel;
        rotationPlatformNode = createRotationPlatformNode( rotationModel.getRotationPlatform() );
//        rotationPlatformNode = new BufferedRotationPlatformNode( rotationModel.getRotationPlatform() );
        originNode = new RotationOriginNode( rotationModel.getRotationPlatform(), angleUnitModel );
        rulerNode = new RotationRulerNode( rotationModel.getRotationPlatform().getRadius() * 2, 50 * SCALE, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8"}, "m", 4, 14 );
        rulerNode.setTransform( AffineTransform.getScaleInstance( 1, -1 ) );
        rulerNode.setVisible( false );

        addChild( rotationPlatformNode );


        for ( int i = 0; i < rotationModel.getNumRotationBodies(); i++ ) {
            addRotationBodyNode( rotationModel.getRotationBody( i ) );
        }
        for ( int i = 0; i < rotationModel.getNumRotationBodies(); i++ ) {
            addVectorNode( rotationModel.getRotationBody( i ), vectiorViewModel );
        }
        circularMotionNode = new CircleNode( rotationModel );
        circularMotionNode.setVisible( false );


        setTransform( AffineTransform.getScaleInstance( 1, -1 ) );

        addChild( new FlowerNode( "flower1.gif", 0.5, -rotationModel.getRotationPlatform().getRadius(), rotationModel.getRotationPlatform().getRadius() * 0.8 ) );
        addChild( new FlowerNode( "flower2.gif", 0.32, -rotationModel.getRotationPlatform().getRadius(), -rotationModel.getRotationPlatform().getRadius() * 0.8 ) );
        addChild( new FlowerNode( "flower2.gif", 0.39, rotationModel.getRotationPlatform().getRadius()*0.6, -rotationModel.getRotationPlatform().getRadius() * 1.1 ) );

        addChild( rotationBodyLayer );
        addChild( vectorLayer );
        addChild( originNode );
        addChild( rulerNode );

        addChild( circularMotionNode );
    }

    static class FlowerNode extends PNode {

        public FlowerNode( String image, double scale, double x, double y ) {
            PImage flower1 = null;
            try {
                flower1 = new PImage( RotationResources.loadBufferedImage( image ) );
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
        rotationBodyLayer.addChild( new RotationBodyNode( rotationModel, rotationBody ) );
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
