package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.HasCapacitorClip;
import edu.colorado.phet.cck3.circuit.Capacitor3DShapeSet;
import edu.colorado.phet.cck3.circuit.IComponentGraphic;
import edu.colorado.phet.cck3.model.components.Capacitor;
import edu.colorado.phet.cck3.model.components.CircuitComponent;
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
    private Capacitor3DShapeSet capacitor3DShapeSet;
    private ModelViewTransform2D transform;
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
    private ArrayList listeners = new ArrayList();
    private double scale = 1.0 / 0.138;

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
        ModelViewTransform2D transform = getModelViewTransform2D();
        Capacitor component = capacitor;
        Point2D src = transform.getAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dst = transform.getAffineTransform().transform( component.getEndJunction().getPosition(), null );

        capacitor3DShapeSet = new Capacitor3DShapeSet( tiltAngle, getCapacitorWidth(), getCapacitorHeight(), src, dst, distBetweenPlates );

        updateChargeDisplay();
        setBoundsDirty();
        repaint();
        notifyCapacitorBoundsChanged();
    }

    private double getCapacitorHeight() {
//        System.out.println( "capacitor.getCapacitance() = " + capacitor.getCapacitance() );
        return height * Math.sqrt( capacitor.getCapacitance() ) * scale;
    }

    private double getCapacitorWidth() {
        return width * Math.sqrt( capacitor.getCapacitance() ) * scale;
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
//        double insetW = 0.05 * w;
//        double insetL = 0.05 * L;
        double alpha = Math.sqrt( numToShow / w / L );
        int numAcross = (int)( w * alpha );
        int numDown = (int)( L * alpha );
        double dw = w / numAcross;
        double dL = L / numDown;
//        int numShown = numAcross * numDown;
//        System.out.println( "q=" + capacitor.getCharge() + ", numShown = " + numShown );
//        double widthSpan = dw * numAcross;
//        double lengthSpan = dL * numDown;

//        double widthSpanInset = ( w - widthSpan ) / 2.0;
        //        double lengthSpanInset = ( L - lengthSpan ) / 2.0;

        double widthSpanInset = ( dw ) / 2.0;
//        System.out.println( "numAcross="+numAcross+", w="+w+", widthSpan="+widthSpan+", widthSpanInset = " + widthSpanInset );

        double lengthSpanInset = ( dL ) / 2.0;
//        System.out.println( "numDown=" + numDown + ", L=" + L + ", lengthSpan=" + lengthSpan + ", lengthSpanInset= " + lengthSpanInset );
//        System.out.println( "dw = " + dw +" lengthSpanInset="+lengthSpanInset);
        double offsetFromCenterW = 0;
        double offsetFromCenterL = 0;
        if( numAcross == 1 ) {
            offsetFromCenterW = 5;
            offsetFromCenterL = 0;
        }
        for( int i = 0; i < numAcross; i++ ) {
            for( int j = 0; j < numDown; j++ ) {
                double u = -w / 2.0 + i * dw + widthSpanInset + offsetFromCenterW;
                double v = -L / 2.0 + j * dL + lengthSpanInset + offsetFromCenterL;
                Point2D loc = capacitor3DShapeSet.getPlate1Location( u, v );
                Point2D loc2 = capacitor3DShapeSet.getPlate2Location( u, v );
                plate1ChargeGraphic.addGraphic( new PhetShapeGraphic( getComponent(), plate1Graphic.createGraphic( loc ), plate1ChargeColor ) );
                plate2ChargeGraphic.addGraphic( new PhetShapeGraphic( getComponent(), plate2Graphic.createGraphic( loc2 ), plate2ChargeColor ) );
            }
        }
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

    public boolean contains( int x, int y ) {
        return capacitor3DShapeSet.getArea().contains( x, y );
    }
}
