package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;

/**
 * Author: Sam Reid
 * May 25, 2007, 5:47:33 PM
 */
public class BodyVectorLayer extends PNode {
    private RotationModel rotationModel;
    private RotationBody rotationBody;
    private VectorNode accelArrow;
    private VectorNode velocityArrow;
    private double accelScale = 250 * 0.8 * 0.03 * 0.03 / 2.0;
    private double velScale = 10 * 0.8 * 0.03;
//    private static final double SCALE = 1.0 / 200.0 * 3.0;

    //todo: factor out required interface to rotationmodel

    public BodyVectorLayer( final RotationModel rotationModel, final RotationBody rotationBody, final VectorViewModel vectorViewModel ) {
        this.rotationModel = rotationModel;
        this.rotationBody = rotationBody;
        accelArrow = new VectorNode( "a", Color.blue, new VectorFunction() {
            public AbstractVector2D getVector() {
                return rotationBody.getAcceleration().getScaledInstance( accelScale );
            }
        } );
        addChild( accelArrow );

        velocityArrow = new VectorNode( "v", Color.red, new VectorFunction() {
            public AbstractVector2D getVector() {
                return rotationBody.getVelocity().getScaledInstance( velScale );
            }
        } );
        addChild( velocityArrow );

        rotationBody.addListener( new RotationBody.Listener() {
            public void positionChanged() {
//                update();//todo: this call was causing acceleration to be non-centripetal during circular motion
            }

            public void speedAndAccelerationUpdated() {
                update();
            }
        } );
        vectorViewModel.addListener( new VectorViewModel.Listener() {
            public void visibilityChanged() {
                updateVisibility( vectorViewModel );
            }
        } );
        update();
        updateVisibility( vectorViewModel );

        setPickable( false );
        setChildrenPickable( false );
    }

    private void updateVisibility( VectorViewModel vectorViewModel ) {
        accelArrow.setVisible( vectorViewModel.isAccelerationVisible() );
        velocityArrow.setVisible( vectorViewModel.isVelocityVisible() );
    }

    class VectorNode extends PNode {
        private PhetPPath arrowNode;
        private VectorFunction vectorFunction;
        private ShadowPText labelNode;
        private double VISIBLE_THRESHOLD = 0.1;

        public VectorNode( String label, Color color, VectorFunction vectorFunction ) {
            this.vectorFunction = vectorFunction;
            arrowNode = new PhetPPath( color, getStroke(), getStrokePaint() );
            addChild( arrowNode );
            labelNode = new ShadowPText( label );
            labelNode.setFont( new Font( "Lucida Sans", Font.BOLD, 18 ) );
            labelNode.setTextPaint( color );
            addChild( labelNode );
        }

        public void update() {
            arrowNode.setPathTo( new Arrow( rotationBody.getPosition(), vectorFunction.getVector(), 20 * RotationPlayAreaNode.SCALE, 20 * RotationPlayAreaNode.SCALE, 3 * RotationPlayAreaNode.SCALE, 0.75, true ).getShape() );
            labelNode.setScale( 1.0 );
            labelNode.scale( 1.0 * RotationPlayAreaNode.SCALE );
            labelNode.setOffset( increase( vectorFunction.getVector(), 20 * RotationPlayAreaNode.SCALE ).getDestination( rotationBody.getPosition() ) );
            labelNode.translate( -labelNode.getFullBounds().getWidth() / 2, -labelNode.getFullBounds().getHeight() / 2 );
            labelNode.setVisible( vectorFunction.getVector().getMagnitude() > VISIBLE_THRESHOLD );

        }

        private AbstractVector2D increase( AbstractVector2D orig, double dx ) {
            double mag = orig.getMagnitude();
            return Math.abs( mag ) < VISIBLE_THRESHOLD ? orig : orig.getInstanceOfMagnitude( mag + dx );
        }
    }

    static interface VectorFunction {
        AbstractVector2D getVector();
    }

    private static Paint getStrokePaint() {
        return Color.black;
    }

    private static Stroke getStroke() {
        return new BasicStroke( (float)( 1 * RotationPlayAreaNode.SCALE ) );
    }

    private void update() {
        accelArrow.update();
        velocityArrow.update();
        rotationBody.checkCentripetalAccel();
    }
}
