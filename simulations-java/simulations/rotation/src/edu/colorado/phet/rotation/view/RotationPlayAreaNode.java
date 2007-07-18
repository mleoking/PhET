package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:52:02 AM
 */

public class RotationPlayAreaNode extends PNode {

    private RotationPlatformNode rotationPlatformNode;
    private PNode rotationBodyLayer = new PNode();
    private RotationModel rotationModel;
    private PNode vectorLayer = new PNode();
    private RotationOriginNode originNode;
    private RotationRulerNode rulerNode;

    public static final double SCALE = 3.0 / 200.0;
    private CircleNode circularMotionNode;

    public RotationPlayAreaNode( final RotationModel rotationModel, VectorViewModel vectiorViewModel ) {
        this.rotationModel = rotationModel;
        rotationPlatformNode = new RotationPlatformNode( rotationModel, rotationModel.getRotationPlatform() );
        originNode = new RotationOriginNode( rotationModel.getRotationPlatform() );
//        rulerNode = new RotationRulerNode( rotationPlatformNode.getRadius() * 2, 50 * SCALE, new String[]{"0", "1", "2", "3", "4", "5", "6"}, "m", 3, 14 );
        rulerNode = new RotationRulerNode( rotationPlatformNode.getRadius() * 2, 50 * SCALE, new String[]{"0", "1", "2", "3", "4", "5", "6"}, "m", 4, 14 );
        rulerNode.setVisible( false );

//        addChild( new PImage(rotationPlatformNode.toImage( )));
        addChild( rotationPlatformNode );
        addChild( rotationBodyLayer );
        addChild( vectorLayer );
        addChild( originNode );
        addChild( rulerNode );


        for( int i = 0; i < rotationModel.getNumRotationBodies(); i++ ) {
            addRotationBodyNode( rotationModel.getRotationBody( i ) );
        }
        for( int i = 0; i < rotationModel.getNumRotationBodies(); i++ ) {
            addVectorNode( rotationModel.getRotationBody( i ), vectiorViewModel );
        }
        circularMotionNode = new CircleNode( rotationModel );
        circularMotionNode.setVisible( false );
        addChild( circularMotionNode );
    }

    public CircleNode getCircularMotionNode() {
        return circularMotionNode;
    }

    private void addVectorNode( RotationBody rotationBody, VectorViewModel vectorViewModel ) {
        vectorLayer.addChild( new BodyVectorLayer( rotationModel, rotationBody, vectorViewModel ) );
    }

    public RotationPlatformNode getPlatformNode() {
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

    public void resetAll() {
        rulerNode.setVisible( false );
    }
}
