package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;

/**
 * Author: Sam Reid
 * May 25, 2007, 5:47:33 PM
 */
public class VectorNode extends PNode {
    private RotationModel rotationModel;
    private RotationBody rotationBody;
    private PhetPPath accelArrow;
    private PhetPPath velocityArrow;
    private double accelScale = 1E4 * 10;
    private double velScale = 1000;

    //todo: factor out required interface to rotationmodel
    public VectorNode( RotationModel rotationModel, RotationBody rotationBody ) {
        this.rotationModel = rotationModel;
        this.rotationBody = rotationBody;
        accelArrow = new PhetPPath( new Rectangle( -2, -2, 4, 4 ), Color.blue ,getStroke(),getStrokePaint());
        addChild( accelArrow );

        velocityArrow = new PhetPPath( Color.red ,getStroke(), getStrokePaint( ));
        addChild( velocityArrow );

        rotationBody.addListener( new RotationBody.Listener() {
            public void positionChanged() {
                update();
            }
        } );
        update();
    }

    private Paint getStrokePaint() {
        return Color.black;
    }

    private Stroke getStroke() {
        return new BasicStroke( 1.0f);
    }

    private void update() {
        AbstractVector2D accelerationVector = new Vector2D.Double( rotationBody.getPosition(), rotationModel.getRotationPlatform().getCenter() ).getInstanceOfMagnitude( Math.abs( rotationModel.getRotationPlatform().getAcceleration() ) ).getScaledInstance( accelScale );
        Arrow accelArrow = new Arrow( rotationBody.getPosition(), accelerationVector, 20, 20, 3, 0.75, true );
        this.accelArrow.setPathTo( accelArrow.getShape() );

//        AbstractVector2D velocityArrow = rotationBody.getVelocity().getScaledInstance( velScale );
        AbstractVector2D velocityArrow = new Vector2D.Double( rotationBody.getPosition(), rotationModel.getRotationPlatform().getCenter() ).getInstanceOfMagnitude( rotationModel.getRotationPlatform().getVelocity() ).getScaledInstance( -velScale ).getRotatedInstance( Math.PI / 2 );
        Arrow velArrow = new Arrow( rotationBody.getPosition(), velocityArrow, 20, 20, 3, 0.75, true );
        this.velocityArrow.setPathTo( velArrow.getShape() );
    }
}
