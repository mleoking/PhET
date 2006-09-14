/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.model.CCKModel;
import edu.colorado.phet.cck3.model.Circuit;
import edu.colorado.phet.cck3.model.CircuitListener;
import edu.colorado.phet.cck3.model.Junction;
import edu.colorado.phet.cck3.model.components.Branch;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jul 17, 2004
 * Time: 10:21:39 PM
 * Copyright (c) Jul 17, 2004 by Sam Reid
 */
public class SolderGraphic extends PhetShapeGraphic {
    private Junction junction;
    private ModelViewTransform2D transform;
    private Circuit circuit;
    private CircuitGraphic circuitGraphic;
    private SimpleObserver simpleObserver;
    private CircuitListener circuitListener;
    private TransformListener transformListener;

    public SolderGraphic( Component parent, final Junction junction, ModelViewTransform2D transform, Circuit circuit, CircuitGraphic circuitGraphic ) {
        super( parent, new Area(), CircuitGraphic.SILVER );
        this.junction = junction;
        this.transform = transform;
        this.circuit = circuit;
        this.circuitGraphic = circuitGraphic;
        simpleObserver = new SimpleObserver() {
            public void update() {
                changed();
            }
        };
        junction.addObserver( simpleObserver );
        circuitListener = new CircuitListener() {

            public void junctionRemoved( Junction junction ) {
            }

            public void branchRemoved( Branch branch ) {
                if( branch.hasJunction( junction ) ) {
                    changeVisiblity();
                }
            }

            public void junctionsMoved() {

            }

            public void branchesMoved( Branch[] branches ) {

            }

            public void junctionAdded( Junction junction ) {
            }

            public void junctionsConnected( Junction a, Junction b, Junction newTarget ) {
                if( a == junction || b == junction || newTarget == junction ) {
                    changeVisiblity();
                }
            }

            public void junctionsSplit( Junction old, Junction[] j ) {
                if( junction == old || Arrays.asList( j ).contains( junction ) ) {
                    changeVisiblity();
                }
            }

        };
        circuit.addCircuitListener( circuitListener );
        transformListener = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                changed();
            }
        };
        transform.addTransformListener( transformListener );
        changed();
        changeVisiblity();
    }

    private void changeVisiblity() {
        Branch[] n = circuit.getAdjacentBranches( junction );
        boolean solderOn = n.length > 1;
        if( solderOn ) {
            setVisible( true );
            changed();
        }
        else {
            setVisible( false );
        }
    }

    private void changed() {
        if( !isVisible() ) {
            return;
        }
        double radius = CCKModel.JUNCTION_RADIUS * 1.34;
        Ellipse2D.Double ellipse = new Ellipse2D.Double();
        Point ctr = transform.modelToView( junction.getPosition() );
        double viewRadius = transform.modelToViewDifferentialX( radius );
        ellipse.setFrameFromCenter( ctr.x, ctr.y, ctr.x + viewRadius, ctr.y + viewRadius );
        super.setShape( ellipse );
    }

    public Junction getJunction() {
        return junction;
    }

    public void delete() {
        transform.removeTransformListener( transformListener );
        circuit.removeCircuitListener( circuitListener );
        junction.removeObserver( simpleObserver );
    }
}
