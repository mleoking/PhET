package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.view.PlatformNode2;

/**
 * Created by: Sam
 * Dec 3, 2007 at 11:52:49 PM
 */
public class RotationPlatformNodeWithMassDisplay extends PlatformNode2 {
//    private EdgeGraphic edgeGraphic;

    public RotationPlatformNodeWithMassDisplay( RotationPlatform rotationPlatform ) {
        super( rotationPlatform );
//        rotationPlatform.addListener( new RotationPlatform.Adapter() {
//            public void massChanged() {
//                update();
//            }
//
//            public void radiusChanged() {
//                update();
//            }
//
//            public void innerRadiusChanged() {
//                update();
//            }
//        } );
//
//        edgeGraphic = new EdgeGraphic();
//        addChild( 0, edgeGraphic );
////        addChild( edgeGraphic );
//        update();
    }

    //
//    class EdgeGraphic extends PhetPPath {
//        public EdgeGraphic() {
//            super( Color.gray, new BasicStroke( (float) ( 2.0 * RotationPlayAreaNode.SCALE ) ), Color.darkGray );
//        }
//    }
//
//    private void update() {
////        final double MAX_THICKNESS = 0.15;
//        final double MAX_THICKNESS = 0.4;
//        Function.LinearFunction linearFunction = new Function.LinearFunction( RotationPlatform.MIN_MASS, RotationPlatform.MAX_MASS, 0, MAX_THICKNESS );
//        double dy = linearFunction.evaluate( getRotationPlatform().getMass() );
//        DoubleGeneralPath doubleGeneralPath = new DoubleGeneralPath( getRotationPlatform().getCenter().getX() - getRadius(), getRotationPlatform().getCenter().getY() );
//        doubleGeneralPath.lineToRelative( 0, -dy );
//        Arc2D.Double arc = new Arc2D.Double( getRotationPlatform().getCenter().getX() - getRadius(), getRotationPlatform().getCenter().getY() - getRadius() - dy,
//                                             getRadius() * 2, getRadius() * 2, 0, 180, Arc2D.Double.OPEN );
//        doubleGeneralPath.append( arc, false );
//
//        doubleGeneralPath.lineToRelative( 0, +dy );
//        doubleGeneralPath.lineTo( getRotationPlatform().getCenter().getX() + getRadius(), getRotationPlatform().getCenter().getY() );
//        doubleGeneralPath.closePath();
//        edgeGraphic.setPathTo( doubleGeneralPath.getGeneralPath() );
//        edgeGraphic.setTransform( new AffineTransform( ) );
//        edgeGraphic.rotateAboutPoint( -Math.PI/2-Math.PI/4,0,0);
//    }
}
