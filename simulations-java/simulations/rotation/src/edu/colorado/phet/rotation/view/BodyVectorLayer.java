package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
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
    public BodyVectorLayer( final RotationModel rotationModel, final RotationBody rotationBody ) {
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
        update();
    }

    class VectorNode extends PNode {
        private PhetPPath arrow;
        private VectorFunction vectorFunction;
        private ShadowPText labelNode;

        public VectorNode( String label, Color color, VectorFunction vectorFunction ) {
            this.vectorFunction = vectorFunction;
            arrow = new PhetPPath( color, getStroke(), getStrokePaint() );
            addChild( arrow );
            labelNode = new ShadowPText( label );
            labelNode.setFont( new Font( "Lucida Sans", Font.BOLD, 18 ) );
            labelNode.setTextPaint( color );
            addChild( labelNode );
        }

        public void update() {
            Arrow a = new Arrow( rotationBody.getPosition(), vectorFunction.getVector(), 20, 20, 3, 0.75, true );
            arrow.setPathTo( a.getShape() );
            labelNode.setOffset( increase( vectorFunction.getVector(), 20 ).getDestination( rotationBody.getPosition() ) );
            labelNode.translate( -labelNode.getFullBounds().getWidth() / 2, -labelNode.getFullBounds().getHeight() / 2 );
        }

    }

    private AbstractVector2D increase( AbstractVector2D vector, double dx ) {
        double mag = vector.getMagnitude();
        AbstractVector2D vec=vector.getInstanceOfMagnitude( mag  + dx );
//        return vec;
        return vector;

//        return
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
