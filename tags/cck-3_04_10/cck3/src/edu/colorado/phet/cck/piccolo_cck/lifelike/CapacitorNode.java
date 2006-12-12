package edu.colorado.phet.cck.piccolo_cck.lifelike;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Capacitor;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.Capacitor3DShapeSet;
import edu.colorado.phet.cck.piccolo_cck.CircuitInteractionModel;
import edu.colorado.phet.cck.piccolo_cck.ComponentNode;
import edu.colorado.phet.cck.piccolo_cck.PhetPPath;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PAffineTransform;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 9:07:00 AM
 * Copyright (c) Sep 20, 2006 by Sam Reid
 */

public class CapacitorNode extends ComponentNode {
    private CCKModel model;
    private ICCKModule module;
    private Capacitor capacitor;
    private static final Color tan = new Color( 255, 220, 130 );
    private Color plate1Color = tan;
    private Color plate2Color = tan;
    private double tiltAngle = Math.PI / 4;
    private int width = 50;
    private int height = 100;
    private int distBetweenPlates = 20;
    private Capacitor3DShapeSet capacitor3DShapeSet;
    private PlateNode leftPlate;
    private PlateNode rightPlate;
    private CircuitInteractionModel circuitInteractionModel;
    private double scale = 1.0 / 80.0;
    private WireStubNode rightWire;
    private WireStubNode leftWire;
    private int numToShow = 0;
    private SimpleObserver capacitorObserver = new SimpleObserver() {
        public void update() {
            CapacitorNode.this.update();
        }
    };

    public CapacitorNode( CCKModel model, Capacitor capacitor, JComponent component, ICCKModule module ) {
        super( model, capacitor, component, module );
        this.model = model;
        this.module = module;
        circuitInteractionModel = new CircuitInteractionModel( model.getCircuit() );
        this.capacitor = capacitor;
        capacitor.addObserver( capacitorObserver );
        leftPlate = new PlateNode();
        rightPlate = new PlateNode();
        rightWire = new WireStubNode();
        leftWire = new WireStubNode();
        addChild( rightWire );
        addChild( rightPlate );
        addChild( leftPlate );
        addChild( leftWire );
        getHighlightNode().setStroke( new BasicStroke( 0.09f ) );
        update();
    }

    public void delete() {
        super.delete();
        capacitor.removeObserver( capacitorObserver );
    }

    public Shape getClipShape( PNode parent ) {
        Shape leftClip = getPlateClipShape( leftPlate.getPath(), parent );
        Shape rightClip = getPlateClipShape( rightPlate.getPath(), parent );
        Area ax = new Area( leftClip );
        ax.add( new Area( rightClip ) );
        Shape leftWireShape = leftWire.getPathBoundExpanded();
        PAffineTransform a = leftWire.getPath().getLocalToGlobalTransform( null );
        PAffineTransform b = parent.getGlobalToLocalTransform( null );
        leftWireShape = a.createTransformedShape( leftWireShape );
        leftWireShape = b.createTransformedShape( leftWireShape );
        ax.subtract( new Area( leftWireShape ) );
        return ax;
    }

    private Shape getPlateClipShape( PhetPPath platePath, PNode parent ) {
        Shape conductorShape = platePath.getPathReference();
        PAffineTransform a = platePath.getLocalToGlobalTransform( null );
        PAffineTransform b = parent.getGlobalToLocalTransform( null );
        conductorShape = a.createTransformedShape( conductorShape );
        conductorShape = b.createTransformedShape( conductorShape );
        return conductorShape;
    }

    class WireStubNode extends PhetPNode {
        private PhetPPath path;
        private PhetPPath clipPath;

        public WireStubNode() {
            BasicStroke stroke = new BasicStroke( 0.05f * 3.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
            path = new PhetPPath( stroke, Color.black );
            clipPath = new PhetPPath( new BasicStroke( 0.05f * 3.5f * 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), Color.black );
            addChild( path );
        }

        public void setWireStubShape( Shape shape ) {
            path.setPathTo( shape );
            clipPath.setPathTo( shape );
        }

        public PhetPPath getPath() {
            return path;
        }

        public Shape getPathBoundExpanded() {
            return clipPath.getPathBoundsWithStroke();
        }
    }

    public void update() {
        super.update();
        capacitor3DShapeSet = new Capacitor3DShapeSet( tiltAngle, getCapacitorWidth() * scale, getCapacitorHeight() * scale,
                                                       capacitor.getStartPoint(), capacitor.getEndPoint(), distBetweenPlates * scale );
        leftPlate.setPlateShape( capacitor3DShapeSet.getPlate1Shape() );
        rightPlate.setPlateShape( capacitor3DShapeSet.getPlate2Shape() );
        leftWire.setWireStubShape( capacitor3DShapeSet.getPlate1Wire() );
        rightWire.setWireStubShape( capacitor3DShapeSet.getPlate2Wire() );
        getHighlightNode().setPathTo( capacitor3DShapeSet.getPlateArea() );

        updateChargeDisplay();
    }

    class PlateNode extends PhetPNode {
        private PhetPPath path;
        private PhetPNode chargeLayer = new PhetPNode();

        public PlateNode() {
            path = new PhetPPath( tan, new BasicStroke( 1.0f / 60.0f ), Color.black );
            addChild( path );
            addChild( chargeLayer );
        }

        public void setPlateShape( Shape shape ) {
            path.setPathTo( shape );
        }

        public void removeChargeGraphics() {
            chargeLayer.removeAllChildren();
        }

        public void addChargeNode( PNode chargeNode ) {
            chargeLayer.addChild( chargeNode );
        }

        public PhetPPath getPath() {
            return path;
        }
    }

    private double getCapacitorHeight() {
        return height;
    }

    private double getCapacitorWidth() {
        return width;
    }

    public Branch getBranch() {
        return capacitor;
    }

    private interface ChargeGraphic {
        Shape createGraphic( Point2D plus );
    }

    private void updateChargeDisplay() {
        double maxCharge = 1;
        double MAX_NUM_TO_SHOW = 100;
        int numToShow = (int)Math.min( Math.abs( capacitor.getCharge() ) / maxCharge * MAX_NUM_TO_SHOW,
                                       MAX_NUM_TO_SHOW );
        setNumDisplayedCharges( numToShow );
    }

    private void setNumDisplayedCharges( int numToShow ) {
        this.numToShow = numToShow;
        leftPlate.removeChargeGraphics();
        rightPlate.removeChargeGraphics();
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
        double alpha = Math.sqrt( numToShow / w / L );
        int numAcross = (int)( w * alpha );
        int numDown = (int)( L * alpha );
        double dw = w / numAcross;
        double dL = L / numDown;
        double widthSpanInset = ( dw ) / 2.0;
        double lengthSpanInset = ( dL ) / 2.0;
        double offsetFromCenterW = 0;
        double offsetFromCenterL = 0;
        if( numAcross == 1 ) {
            offsetFromCenterW = 5 * SCALE;
            offsetFromCenterL = 0;
        }
        for( int i = 0; i < numAcross; i++ ) {
            for( int j = 0; j < numDown; j++ ) {
                double u = -w / 2.0 + i * dw + widthSpanInset + offsetFromCenterW;
                double v = -L / 2.0 + j * dL + lengthSpanInset + offsetFromCenterL;
                Point2D loc = capacitor3DShapeSet.getPlate1Location( u, v );
                Point2D loc2 = capacitor3DShapeSet.getPlate2Location( u, v );
                leftPlate.addChargeNode( new PhetPPath( plate1Graphic.createGraphic( loc ), plate1ChargeColor ) );
                rightPlate.addChargeNode( new PhetPPath( plate2Graphic.createGraphic( loc2 ), plate2ChargeColor ) );
            }
        }
    }

    double SCALE = 1.0 / 60.0;

    private Shape createPlusGraphic( Point2D loc ) {
        double w = 2 * SCALE;
        Area area = new Area( new Rectangle2D.Double( loc.getX() - w, loc.getY(), w * 2 + 1 * SCALE, 1 * SCALE ) );
        area.add( new Area( new Rectangle2D.Double( loc.getX(), loc.getY() - w, 1 * SCALE, w * 2 + 1 * SCALE ) ) );
        return area;
    }

    private Shape createMinusGraphic( Point2D loc ) {
        double w = 2 * SCALE;
        return new Area( new Rectangle2D.Double( loc.getX() - w, loc.getY(), w * 2 + 1 * SCALE, 1 * SCALE ) );
    }
}
