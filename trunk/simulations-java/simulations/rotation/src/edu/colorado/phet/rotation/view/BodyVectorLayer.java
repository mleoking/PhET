package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
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
    private double accelScale = 100000;
    private double velScale = 1000;

    //todo: factor out required interface to rotationmodel
    public BodyVectorLayer( final RotationModel rotationModel, final RotationBody rotationBody, final VectorViewModel vectorViewModel ) {
        this.rotationModel = rotationModel;
        this.rotationBody = rotationBody;
        accelArrow = new VectorNode( "a", Color.blue, new VectorFunction() {
            public AbstractVector2D getVector() {
                return new Vector2D.Double( rotationBody.getPosition(), rotationModel.getRotationPlatform().getCenter() ).getInstanceOfMagnitude( Math.abs( rotationModel.getRotationPlatform().getAcceleration() ) ).getScaledInstance( accelScale );
            }
        } );
        addChild( accelArrow );

        velocityArrow = new VectorNode( "v", Color.red, new VectorFunction() {
            public AbstractVector2D getVector() {
                return new Vector2D.Double( rotationBody.getPosition(), rotationModel.getRotationPlatform().getCenter() ).getInstanceOfMagnitude( rotationModel.getRotationPlatform().getVelocity() ).getScaledInstance( -velScale ).getRotatedInstance( Math.PI / 2 );
            }
        } );
        addChild( velocityArrow );

        rotationBody.addListener( new RotationBody.Listener() {
            public void positionChanged() {
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
            arrowNode.setPathTo( new Arrow( rotationBody.getPosition(), vectorFunction.getVector(), 20, 20, 3, 0.75, true ).getShape() );
            labelNode.setOffset( increase( vectorFunction.getVector(), 20 ).getDestination( rotationBody.getPosition() ) );
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
        return new BasicStroke( 1.0f );
    }

    private void update() {
        accelArrow.update();
        velocityArrow.update();
    }
}
