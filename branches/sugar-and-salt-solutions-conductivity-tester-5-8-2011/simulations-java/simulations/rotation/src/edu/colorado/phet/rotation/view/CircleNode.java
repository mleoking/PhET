// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.view;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Author: Sam Reid
 * Jul 17, 2007, 3:49:30 PM
 */
public class CircleNode extends PNode {
    private PhetPPath circlePath;
    private RotationModel rotationModel;
    private PhetPPath pointPath;

    public CircleNode( RotationModel rotationModel ) {
        this.rotationModel = rotationModel;
        rotationModel.getRotationBody( 1 ).addListener( new RotationBody.Adapter() {

            public void positionChanged() {
                update();
            }

            public void speedAndAccelerationUpdated() {
                update();
            }

        } );
        pointPath = new PhetPPath( new BasicStroke( (float) ( 2 * RotationPlayAreaNode.SCALE ) ), Color.green );
        circlePath = new PhetPPath( new BasicStroke( (float) ( 2 * RotationPlayAreaNode.SCALE ) ), Color.blue );
//        circlePath = new PhetPPath( Color.blue );
        update();
        addChild( circlePath );
        addChild( pointPath );
    }

    private void update() {
        if ( getVisible() ) {
            if ( rotationModel.getRotationBody( 1 ).getCircle() != null ) {
                circlePath.setPathTo( rotationModel.getRotationBody( 1 ).getCircle().toEllipse() );
                pointPath.setPathToPolyline( rotationModel.getRotationBody( 1 ).getPointHistory( 25 ) );
//            circlePath.setPathTo( new Ellipse2D.Double( rotationModel.getRotationBody( 1 ).getX(), rotationModel.getRotationBody( 1).getY(),1,1));
//            System.out.println( "circlePath.getGlobalFullBounds() = " + circlePath.getGlobalFullBounds() );
            }
        }
//        setScale( RotationPlayAreaNode.SCALE );
    }

    public void setVisible( boolean isVisible ) {
        super.setVisible( isVisible );
        if ( isVisible ) {
            update();
        }
    }
}
