package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.HasCapacitorClip;
import edu.colorado.phet.cck3.circuit.Capacitor3DShapeSet;
import edu.colorado.phet.cck3.circuit.IComponentGraphic;
import edu.colorado.phet.common_cck.math.AbstractVector2D;
import edu.colorado.phet.common_cck.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class SchematicCapacitor3DGraphic extends PhetGraphic implements HasCapacitorClip, IComponentGraphic {
    private Capacitor capacitor;
    private CompositePhetGraphic plate1ChargeGraphic;
    private CompositePhetGraphic plate2ChargeGraphic;
    private Color plusMinusColor = Color.black;
    private Capacitor3DShapeSet capacitor3DShapeSet;
    private ModelViewTransform2D transform;
    private double fracDistToPlate = 0.325;
    private static final Color tan = new Color( 255, 220, 130 );
    private Color plate1Color = tan;
    private Color plate2Color = tan;
    private double tiltAngle = Math.PI / 4;
    private int width = 50;
    private int height = 100;
    private int distBetweenPlates = 20;
    private TransformListener transformListener;
    private SimpleObserver componentObserver;
    private Capacitor.Listener componentListener;

    public SchematicCapacitor3DGraphic( Component parent, Capacitor component,
                                        ModelViewTransform2D transform, double wireThickness ) {
        super( parent );
        this.transform = transform;
        componentListener = new Capacitor.Listener() {
            public void chargeChanged() {
                update();
            }
        };
        component.addListener( componentListener );
        componentObserver = new SimpleObserver() {
            public void update() {
                SchematicCapacitor3DGraphic.this.update();
            }
        };
        component.addObserver( componentObserver );
        this.capacitor = component;
        plate1ChargeGraphic = new CompositePhetGraphic( parent );
        plate2ChargeGraphic = new CompositePhetGraphic( parent );
        transformListener = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                update();
            }
        };
        transform.addTransformListener( transformListener );
        update();
    }

    public void setWidth( int width ) {
        this.width = width;
        update();
    }

    public void setHeight( int height ) {
        this.height = height;
        update();
    }

    public void setDistBetweenPlates( int distBetweenPlates ) {
        this.distBetweenPlates = distBetweenPlates;
        update();
    }

    public void paint( Graphics2D g ) {
        if( capacitor.isSelected() ) {
            g.setColor( Color.yellow );
            g.setStroke( getHighlightStroke() );
            g.draw( capacitor3DShapeSet.getArea() );
            g.draw( getWireStroke().createStrokedShape( capacitor3DShapeSet.getPlate1Wire() ) );
            g.draw( getWireStroke().createStrokedShape( capacitor3DShapeSet.getPlate2Wire() ) );
        }
        g.setColor( Color.black );
        g.setStroke( getWireStroke() );
        g.draw( capacitor3DShapeSet.getPlate2Wire() );
        g.setStroke( new BasicStroke() );

        g.setColor( plate1Color );
        g.fill( capacitor3DShapeSet.getPlate2Shape() );
        g.setColor( Color.black );
        g.draw( capacitor3DShapeSet.getPlate2Shape() );
        plate2ChargeGraphic.paint( g );

        g.setColor( plate2Color );
        g.fill( capacitor3DShapeSet.getPlate1Shape() );
        g.setColor( Color.black );
        g.draw( capacitor3DShapeSet.getPlate1Shape() );
        plate1ChargeGraphic.paint( g );

        g.setColor( Color.black );
        g.setStroke( getWireStroke() );
        g.draw( capacitor3DShapeSet.getPlate1Wire() );
        g.setStroke( new BasicStroke() );
    }

    private BasicStroke getHighlightStroke() {
        return new BasicStroke( 8, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND );
    }

    private BasicStroke getWireStroke() {
        return new BasicStroke( 15, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
    }

    protected void update() {
        if( capacitor == null ) {
            return;
        }
//        double charge = capacitor.getCharge();
//        System.out.println( "charge = " + charge );
        ModelViewTransform2D transform = getModelViewTransform2D();
        Capacitor component = capacitor;
        Point2D src = transform.getAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dst = transform.getAffineTransform().transform( component.getEndJunction().getPosition(), null );
//        double viewThickness = transform.modelToViewDifferentialY( getWireThickness() );

        ImmutableVector2D vector = new ImmutableVector2D.Double( src, dst );
        Point2D cat = vector.getScaledInstance( getFracDistToPlate() ).getDestination( src );
        Point2D ano = vector.getScaledInstance( 1 - getFracDistToPlate() ).getDestination( src );
        AbstractVector2D east = vector.getInstanceOfMagnitude( 1 );
        AbstractVector2D north = east.getNormalVector();
        capacitor3DShapeSet = new Capacitor3DShapeSet( tiltAngle, width, height, src, dst, distBetweenPlates );

        updateChargeDisplay();
        setBoundsDirty();
        repaint();
        notifyCapacitorBoundsChanged();
    }

    private void updateChargeDisplay() {
        double maxCharge = 1;
        double MAX_NUM_TO_SHOW = 100;
        int numToShow = (int)Math.min( Math.abs( capacitor.getCharge() ) / maxCharge * MAX_NUM_TO_SHOW,
                                       MAX_NUM_TO_SHOW );
        plate1ChargeGraphic.clear();
        plate2ChargeGraphic.clear();
        ChargeGraphic plate1Graphic = new ChargeGraphic() {
            public Shape createGraphic( Point2D center ) {
                return !( capacitor.getCharge() > 0 ) ? createPlusGraphic( center ) : createMinusGraphic( center );
            }
        };
        ChargeGraphic plate2Graphic = new ChargeGraphic() {
            public Shape createGraphic( Point2D center ) {
                return ( capacitor.getCharge() > 0 ) ? createPlusGraphic( center ) : createMinusGraphic( center );
            }
        };

        Color plate1ChargeColor = !( capacitor.getCharge() > 0 ) ? Color.red : Color.blue;
        Color plate2ChargeColor = ( capacitor.getCharge() > 0 ) ? Color.red : Color.blue;

        double w = capacitor3DShapeSet.getWidth();
        double L = capacitor3DShapeSet.getLength();
        double insetW = w * 0.03;
        double insetL = L * 0.03;
        double u = -w / 2.0 + insetW;
        double v = -L / 2.0 + insetL;
        double dw = w / Math.sqrt( numToShow );
        double dL = L / Math.sqrt( numToShow );
        for( int i = 0; i < numToShow * 1000; i++ ) {
            Point2D loc = capacitor3DShapeSet.getPlate1Location( u, v );
            Point2D loc2 = capacitor3DShapeSet.getPlate2Location( u, v );
            plate1ChargeGraphic.addGraphic( new PhetShapeGraphic( getComponent(), plate1Graphic.createGraphic( loc ), plate1ChargeColor ) );
            plate2ChargeGraphic.addGraphic( new PhetShapeGraphic( getComponent(), plate2Graphic.createGraphic( loc2 ), plate2ChargeColor ) );
            u += dw;
            if( u >= w / 2 - insetW ) {
                u -= w;
                v += dL;
                if( i >= numToShow || v >= L - insetL ) {
                    break;
                }
            }
        }
    }

    private double getFracDistToPlate() {
        return fracDistToPlate;
    }

    public ModelViewTransform2D getModelViewTransform2D() {
        return transform;
    }

    public CircuitComponent getCircuitComponent() {
        return capacitor;
    }

    public void delete() {
        transform.removeTransformListener( transformListener );
        capacitor.removeObserver( componentObserver );
        capacitor.removeListener( componentListener );
    }

    public Shape getCapacitorClip() {
        Line2D.Double line = new Line2D.Double( capacitor3DShapeSet.getPlate1Point(), capacitor3DShapeSet.getPlate2EdgePoint() );
        Stroke str = new BasicStroke( 30, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
        return str.createStrokedShape( line );
    }

    public void addListener( SchematicPlatedGraphic.Listener listener ) {
        listeners.add( listener );
    }

    private ArrayList listeners = new ArrayList();

    public void notifyCapacitorBoundsChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            SchematicPlatedGraphic.Listener listener = (SchematicPlatedGraphic.Listener)listeners.get( i );
            listener.areaChanged();
        }
    }

    private interface ChargeGraphic {
        Shape createGraphic( Point2D plus );
    }

    protected Rectangle determineBounds() {
        if( capacitor.isSelected() ) {
            Area a = new Area( getHighlightStroke().createStrokedShape( capacitor3DShapeSet.getArea() ) );
            a.add( new Area( getWireStroke().createStrokedShape( capacitor3DShapeSet.getPlate1Wire() ) ) );
            a.add( new Area( getWireStroke().createStrokedShape( capacitor3DShapeSet.getPlate2Wire() ) ) );
//            g.draw( getWireStroke().createStrokedShape( capacitor3DShapeSet.getPlate2Wire() ) );
            return a.getBounds();
        }
        else {
            return capacitor3DShapeSet.getBounds();
        }
    }

    private Shape createPlusGraphic( Point2D loc ) {
        double w = 2;
        Area area = new Area( new Rectangle2D.Double( loc.getX() - w, loc.getY(), w * 2 + 1, 1 ) );
        area.add( new Area( new Rectangle2D.Double( loc.getX(), loc.getY() - w, 1, w * 2 + 1 ) ) );
        return area;
    }

    private Shape createMinusGraphic( Point2D loc ) {
        double w = 2;
//        area.add( new Area( new Rectangle2D.Double( loc.getX(), loc.getY() - w, 1, w * 2 + 1 ) ) );
        return new Area( new Rectangle2D.Double( loc.getX() - w, loc.getY(), w * 2 + 1, 1 ) );
    }
}
