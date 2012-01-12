// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.rotation.RotationStrings;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Author: Sam Reid
 * May 25, 2007, 5:47:33 PM
 */
public class BodyVectorLayer extends PNode {
    private RotationModel rotationModel;
    private RotationBody rotationBody;
    private VectorNode accelArrow;
    private VectorNode velocityArrow;
    private double accelScale = 0.09 ;//in m/sec/sec, see #2077
    private double velScale = 0.24 ;//in m/sec, see #2077

    //todo: factor out required interface to rotationmodel

    public BodyVectorLayer( final RotationModel rotationModel, final RotationBody rotationBody, final VectorViewModel vectorViewModel ) {
        this.rotationModel = rotationModel;
        this.rotationBody = rotationBody;
        accelArrow = new VectorNode( RotationStrings.getString( "variable.a" ), RotationColorScheme.ACCELERATION_COLOR, new VectorFunction() {
            public ImmutableVector2D getVector() {
                return rotationBody.getAcceleration().getScaledInstance( accelScale );
            }
        } );
        addChild( accelArrow );

        velocityArrow = new VectorNode( RotationStrings.getString( "variable.v" ), RotationColorScheme.VELOCITY_COLOR, new VectorFunction() {
            public ImmutableVector2D getVector() {
                return rotationBody.getVelocity().getScaledInstance( velScale );
            }
        } );
        addChild( velocityArrow );

        rotationBody.addListener( new RotationBody.Adapter() {
            public void positionChanged() {
                //todo: this call was causing acceleration to be non-centripetal during circular motion
                update();
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
            labelNode.setFont( new PhetFont( Font.BOLD, 18 ) );
            labelNode.setTextPaint( color );
            addChild( labelNode );
        }

        public void update() {
            Point2D position = rotationBody.getPosition();
            ImmutableVector2D vector = vectorFunction.getVector();
            arrowNode.setPathTo( new Arrow( position, vector, 20 * RotationPlayAreaNode.SCALE, 20 * RotationPlayAreaNode.SCALE, 3 * RotationPlayAreaNode.SCALE, 0.75, true ).getShape() );
            labelNode.setVisible( vector.getMagnitude() > VISIBLE_THRESHOLD );
            if ( labelNode.getVisible() ) {
                labelNode.setTransform( AffineTransform.getScaleInstance( RotationPlayAreaNode.SCALE, -1.0 * RotationPlayAreaNode.SCALE ) );
                labelNode.setOffset( increase( vector, 20 * RotationPlayAreaNode.SCALE ).getDestination( position ) );
                labelNode.translate( -labelNode.getFullBounds().getWidth() / 2 / RotationPlayAreaNode.SCALE, -labelNode.getFullBounds().getHeight() / 2 / RotationPlayAreaNode.SCALE );
            }
        }

        private ImmutableVector2D increase( ImmutableVector2D orig, double dx ) {
            return Math.abs( orig.getMagnitude() ) < VISIBLE_THRESHOLD ? orig : orig.getInstanceOfMagnitude( orig.getMagnitude() + dx );
        }
    }

    static interface VectorFunction {
        ImmutableVector2D getVector();
    }

    private static Paint getStrokePaint() {
        return Color.black;
    }

    private static Stroke getStroke() {
        return new BasicStroke( (float) ( 1 * RotationPlayAreaNode.SCALE ) );
    }

    private void update() {
        accelArrow.update();
        velocityArrow.update();
        rotationBody.checkCentripetalAccel();
    }
}
