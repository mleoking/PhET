/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitListener;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.phetgraphics_cck.CCKCompositePhetGraphic;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetTextGraphic;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 2:05:05 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class JunctionGraphic extends CCKCompositePhetGraphic {
    private PhetShapeGraphic shapeGraphic;
    private Junction junction;
    private ModelViewTransform2D transform;
    private double radius;
    private Circuit circuit;
    private double strokeWidthModelCoords = CCKModel.JUNCTION_GRAPHIC_STROKE_WIDTH;
    private SimpleObserver observer;
    private TransformListener transformListener;
    private PhetShapeGraphic highlightGraphic;
    private CircuitListener circuitListener;
    private static int instanceCount = 0;
    private PhetTextGraphic debugGraphic;
//    private boolean showLabel = false;
//<<<<<<< JunctionGraphic.java
    private boolean showLabel = CircuitGraphic.GRAPHICAL_DEBUG;
//=======
//    private boolean showLabel = false;
//>>>>>>> 1.13

    public JunctionGraphic( Component parent, Junction junction, ModelViewTransform2D transform, double radius, Circuit circuit ) {
        super( parent );

        shapeGraphic = new PhetShapeGraphic( parent, new Area(), new BasicStroke( 2 ), Color.black );
        highlightGraphic = new PhetShapeGraphic( parent, new Area(), new BasicStroke( 4 ), Color.yellow );
        addGraphic( highlightGraphic );
        addGraphic( shapeGraphic );
        if( showLabel ) {
            debugGraphic = new PhetTextGraphic( getComponent(), new Font( "dialog", 0, 12 ), "", Color.black, 0, 0 );
            addGraphic( debugGraphic );
        }
        this.junction = junction;
        this.transform = transform;
        this.radius = radius;
        this.circuit = circuit;
        observer = new SimpleObserver() {
            public void update() {
                changed();
            }
        };
        junction.addObserver( observer );
        circuitListener = new CircuitListener() {

            public void junctionRemoved( Junction junction ) {
                changed();
            }

            public void branchRemoved( Branch branch ) {
                changed();
            }

            public void junctionsMoved() {
            }

            public void branchesMoved( Branch[] branches ) {
            }

            public void junctionAdded( Junction junction ) {
            }

            public void junctionsConnected( Junction a, Junction b, Junction newTarget ) {
            }

            public void junctionsSplit( Junction old, Junction[] j ) {
            }

            public void branchAdded( Branch branch ) {
            }

        };
        circuit.addCircuitListener( this.circuitListener );

        transformListener = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                changed();
            }
        };
        transform.addTransformListener( transformListener );
        changed();
        setVisible( true );
        instanceCount++;
//        new Exception("Created JG # "+instanceCount+"").printStackTrace();
//        System.out.println( "instanceCount = " + instanceCount );
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        highlightGraphic.setVisible( visible && junction.isSelected() );
    }

    public double getRadius() {
        return radius;
    }

    private Stroke createStroke( double strokeWidth ) {
        float[] dash = new float[]{3, 6};
//        float strokeWidthView = (float)transform.getAffineTransform().transform( new Point2D.Double( strokeWidth, 0 ), null ).getX();
        float strokeWidthView = (float)transform.getAffineTransform().deltaTransform( new Point2D.Double( strokeWidth, 0 ), null ).getX();
        if( strokeWidthView <= 0 ) {
            throw new RuntimeException( "negative stroke width=" + strokeWidthView );
        }
        Stroke s = new BasicStroke( strokeWidthView, BasicStroke.CAP_SQUARE, BasicStroke.CAP_BUTT, 3, dash, 0 );
        return s;
    }

    private void changed() {
        boolean selected = junction.isSelected();
        highlightGraphic.setVisible( selected );
        Ellipse2D.Double ellipse = new Ellipse2D.Double();
        ellipse.setFrameFromCenter( junction.getX(), junction.getY(), junction.getX() + radius, junction.getY() + radius );
        shapeGraphic.setShape( transform.createTransformedShape( ellipse ) );

        Ellipse2D.Double highlightEllipse = new Ellipse2D.Double();
        highlightEllipse.setFrameFromCenter( junction.getX(), junction.getY(), junction.getX() + radius * 1.33, junction.getY() + radius * 1.33 );
        highlightGraphic.setShape( transform.createTransformedShape( highlightEllipse ) );

        int numConnections = circuit.getNeighbors( getJunction() ).length;
        if( numConnections == 1 ) {
            shapeGraphic.setBorderColor( Color.red );//setOutlinePaint( Color.red );
            shapeGraphic.setStroke( createStroke( strokeWidthModelCoords * 1.5 ) );
        }
        else {
            shapeGraphic.setBorderColor( Color.black );
            shapeGraphic.setStroke( createStroke( strokeWidthModelCoords ) );
        }
        if( showLabel ) {
//            debugGraphic.setText( junction.getLabel() + ", "+junction.hashCode() );
            debugGraphic.setText( junction.getLabel() + "" );//+ ", "+junction.hashCode() );
            debugGraphic.setPosition( shapeGraphic.getShape().getBounds().x, shapeGraphic.getShape().getBounds().y );
        }
        super.setBoundsDirty();

    }

    public Junction getJunction() {
        return junction;
    }

    public ModelViewTransform2D getTransform() {
        return transform;
    }

    public void delete() {
        junction.removeObserver( observer );
        transform.removeTransformListener( transformListener );
        circuit.removeCircuitListener( circuitListener );
        instanceCount--;
    }

    public Shape getShape() {
        return this.shapeGraphic.getShape();
    }
}
