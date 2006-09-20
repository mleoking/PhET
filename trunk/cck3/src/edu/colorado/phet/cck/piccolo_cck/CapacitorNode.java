package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Capacitor;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.Capacitor3DShapeSet;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 9:07:00 AM
 * Copyright (c) Sep 20, 2006 by Sam Reid
 */

public class CapacitorNode extends ComponentNode {
    private CCKModel model;
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

    public CapacitorNode( CCKModel model, Capacitor capacitor ) {
        super( model, capacitor );
        this.model = model;
        circuitInteractionModel = new CircuitInteractionModel( model.getCircuit() );
        this.capacitor = capacitor;
        capacitor.addObserver( new SimpleObserver() {
            public void update() {
                CapacitorNode.this.update();
            }
        } );
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

    class WireStubNode extends PhetPNode {
        private PhetPPath path;

        public WireStubNode() {
            path = new PhetPPath( new BasicStroke( 0.05f * 3.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), Color.black );
            addChild( path );
        }

        public void setWireStubShape( Shape shape ) {
            path.setPathTo( shape );
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
    }

    class PlateNode extends PhetPNode {
        private PhetPPath path;

        public PlateNode() {
            path = new PhetPPath( tan, new BasicStroke( 1.0f / 60.0f ), Color.black );
            addChild( path );
        }

        public void setPlateShape( Shape shape ) {
            path.setPathTo( shape );
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
}
