/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.cck.common.LineSegment;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.cck.model.components.Switch;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 19, 2004
 * Time: 12:26:09 AM
 * Copyright (c) Jun 19, 2004 by Sam Reid
 */
public class SchematicLeverGraphic extends PhetShapeGraphic {
    private SchematicSwitchGraphic schematicSwitchGraphic;
    private ApparatusPanel apparatusPanel;
    private ModelViewTransform2D transform;
    private double wireThickness;
    private double length;
    private Switch aSwitch;
    public static final double OPEN_ANGLE = 4.19;//open
    public static final double CLOSED_ANGLE = Math.PI * 2;//closed

    double relativeAngle = OPEN_ANGLE;
    private SimpleObserver simpleObserver;
    private TransformListener transformListener;

    public SchematicLeverGraphic( SchematicSwitchGraphic schematicSwitchGraphic, ApparatusPanel apparatusPanel, ModelViewTransform2D transform, double wireThickness, double length ) {
        super( apparatusPanel, new Area(), Color.black, new BasicStroke( 2 ), Color.gray );
        this.schematicSwitchGraphic = schematicSwitchGraphic;
        this.apparatusPanel = apparatusPanel;
        this.transform = transform;
        this.wireThickness = wireThickness;
        this.length = length;
        this.aSwitch = (Switch)schematicSwitchGraphic.getCircuitComponent();
        simpleObserver = new SimpleObserver() {
            public void update() {
                changed();
            }
        };
        this.aSwitch.addObserver( simpleObserver );
        transformListener = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                changed();
            }
        };
        transform.addTransformListener( transformListener );

        Switch swit = schematicSwitchGraphic.getSwitch();
        if( swit.isClosed() ) {
            relativeAngle = CLOSED_ANGLE;
        }
        else {
            relativeAngle = LeverGraphic.CLOSED_ANGLE - Math.PI * ( 45.0 / 180.0 );
        }
        changed();
        setVisible( true );
    }

    private void changed() {
        Point2D baseSrc = aSwitch.getStartJunction().getPosition();
        Point2D baseDst = aSwitch.getEndJunction().getPosition();

        AbstractVector2D baseVector = new Vector2D.Double( baseSrc, baseDst );
        double angle = baseVector.getAngle();
        double totalAngle = -angle - relativeAngle;
        double viewLength = transform.modelToViewDifferentialX( length );
        AbstractVector2D leverDir = Vector2D.Double.parseAngleAndMagnitude( viewLength, totalAngle );
        double viewThickness = Math.abs( transform.modelToViewDifferentialY( wireThickness ) );
        Point2D viewSrc = schematicSwitchGraphic.getPivot();
        Point2D viewDst = leverDir.getDestination( viewSrc );
        Shape shape = LineSegment.getSegment( viewSrc, viewDst, viewThickness );
        super.setShape( shape );
    }

    public SchematicSwitchGraphic getBaseGraphic() {
        return schematicSwitchGraphic;
    }

//    public Point2D getPivotViewLocation() {
//        return viewPivot;
//    }

    public void setRelativeAngle( double angle ) {
        while( angle < 0 ) {
            angle += Math.PI * 2;
        }
        while( angle > Math.PI * 2 ) {
            angle -= Math.PI * 2;
        }
        if( angle < 2.53 ) {
            angle = CLOSED_ANGLE;
        }
        else if( angle < 4.1 ) {
            angle = OPEN_ANGLE;
        }
//        System.out.println( "angle = " + angle );
        if( angle >= OPEN_ANGLE && angle <= CLOSED_ANGLE ) {
            double oldAngle = relativeAngle;
            if( oldAngle != angle ) {
                this.relativeAngle = angle;
                changed();
                if( angle == CLOSED_ANGLE && !aSwitch.isClosed() ) {
                    aSwitch.setClosed( true );
                }
                else if( angle != CLOSED_ANGLE && aSwitch.isClosed() ) {
                    aSwitch.setClosed( false );
                }
            }
        }
    }

    public CircuitComponent getCircuitComponent() {
        return aSwitch;
    }

    public Point2D getPivotViewLocation() {
        return this.getBaseGraphic().getPivot();
    }

    public void delete() {
        transform.removeTransformListener( transformListener );
        aSwitch.removeObserver( simpleObserver );
    }
}
