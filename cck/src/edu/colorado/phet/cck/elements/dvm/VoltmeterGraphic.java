/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.dvm;

import edu.colorado.phet.cck.common.SimpleObserver;
import edu.colorado.phet.cck.elements.circuit.CircuitGraphic;
import edu.colorado.phet.cck.elements.circuit.LeadWireConnection;
import edu.colorado.phet.cck.elements.junction.Junction;
import edu.colorado.phet.cck.util.ImageLoader;
import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Oct 26, 2003
 * Time: 1:59:41 AM
 * Copyright (c) Oct 26, 2003 by Sam Reid
 */
public class VoltmeterGraphic extends CompositeGraphic {
    private Voltmeter vm;
    private ModelViewTransform2d transform;
    private ImageLoader imageLoader;
    private LeadGraphic redLeadGraphic;
    private LeadGraphic blackLeadGraphic;
    private CableGraphic blackCableGraphic;
    private CableGraphic redCableGraphic;
    private VoltmeterUnitGraphic voltmeterUnitGraphic;

    public VoltmeterUnitGraphic getVoltmeterUnitGraphic() {
        return voltmeterUnitGraphic;
    }

    public VoltmeterGraphic(Voltmeter vm, ModelViewTransform2d transform, ImageLoader imageLoader) throws IOException {
        this.vm = vm;
        this.transform = transform;
        this.imageLoader = imageLoader;

        BufferedImage dvmImage = imageLoader.loadBufferedImage("images/dvm/dvm-volts-t2-100.gif");
        voltmeterUnitGraphic = new VoltmeterUnitGraphic(vm.getVoltmeterUnit(), dvmImage, transform);
        addGraphic(voltmeterUnitGraphic, 1);

        BufferedImage black = imageLoader.loadBufferedImage("images/dvm/probeBlack.gif");
        BufferedImage red = imageLoader.loadBufferedImage("images/dvm/probeRed.gif");
        double redAngle = Math.PI / 8;
        double blackAngle = -redAngle;
        redLeadGraphic = new LeadGraphic(vm.getRedLead(), red, transform, redAngle);
        blackLeadGraphic = new LeadGraphic(vm.getBlackLead(), black, transform, blackAngle);

        addGraphic(redLeadGraphic, 1);
        addGraphic(blackLeadGraphic, 2);

        blackCableGraphic = new CableGraphic(transform, Color.black, blackLeadGraphic, new HasLocation() {
            public Point getLocation() {
                return voltmeterUnitGraphic.getBlackWirePoint();
            }
        });

        redCableGraphic = new CableGraphic(transform, Color.red, redLeadGraphic, new HasLocation() {
            public Point getLocation() {
                return voltmeterUnitGraphic.getRedWirePoint();
            }
        });
        voltmeterUnitGraphic.addObserver(new SimpleObserver() {
            public void update() {
                redCableGraphic.changed();
                blackCableGraphic.changed();
            }
        });
        redLeadGraphic.addObserver(new SimpleObserver() {
            public void update() {
                redCableGraphic.changed();
            }
        });
        blackLeadGraphic.addObserver(new SimpleObserver() {
            public void update() {
                blackCableGraphic.changed();
            }
        });

        addGraphic(blackCableGraphic, 3);
        addGraphic(redCableGraphic, 4);
    }

    public LeadGraphic getRedLeadGraphic() {
        return redLeadGraphic;
    }

    public LeadGraphic getBlackLeadGraphic() {
        return blackLeadGraphic;
    }

    boolean tipsIntersect() {
        Area a = new Area(redLeadGraphic.getTipShape());
        Area b = new Area(blackLeadGraphic.getTipShape());
        a.intersect(b);
        return !a.isEmpty();
    }

    public void updateVoltageReading(CircuitGraphic circuitGraphic) {
        if (tipsIntersect()) {
            voltmeterUnitGraphic.setVoltage(0);
            return;
        }
        if (redLeadGraphic == null || blackLeadGraphic == null)
            return;
        LeadWireConnection red = circuitGraphic.getVoltageVertex(redLeadGraphic.getTipShape(), null);
        if (red == null) {
            voltmeterUnitGraphic.setVoltage(Double.NaN);
            return;
        }
        LeadWireConnection black = circuitGraphic.getVoltageVertex(blackLeadGraphic.getTipShape(), null);
//        System.out.println("red=" + red + ", black=" + black);
        if (red == null || black == null) {
            voltmeterUnitGraphic.setVoltage(Double.NaN);
            return;
        } else {
            Junction start = red.getEquivalentJunction();//red is the start.
            Junction end = black.getEquivalentJunction();
            if (start == end || start.hasConnection(end)) {
                if (red.getDistFromVertex() < black.getDistFromVertex()) {
                    //red keeps his vertex
                    black = circuitGraphic.getVoltageVertex(blackLeadGraphic.getTipShape(), red.getEquivalentJunction());
                    end = black.getEquivalentJunction();
                } else if (red.getDistFromVertex() >= black.getDistFromVertex()) {
                    red = circuitGraphic.getVoltageVertex(redLeadGraphic.getTipShape(), black.getEquivalentJunction());
                    start = red.getEquivalentJunction();
                }
            }

            double voltageDropBetweenVertices = circuitGraphic.getCircuit().getVoltageDrop(start, end);
            double dred = red.getVoltageDropToVertex();
            double dblack = black.getVoltageDropToVertex();
            double tot = voltageDropBetweenVertices + dred - dblack;
//            if (start == end || start.hasConnection(end)) {
//                System.out.println("End=Start");
//                tot = dred - dblack;
//            }
//            if (voltageDropBetweenVertices<0){
//                tot=voltageDropBetweenVertices+dred+dblack;
//            }
            System.out.println("voltageDropBetweenVertices=" + voltageDropBetweenVertices + ", dred=" + dred + ", dblack=" + dblack + ", tot=" + tot);

//            voltageDropBetweenVertices=voltageDropBetweenVertices-dred-dblack;
            voltmeterUnitGraphic.setVoltage(tot);
        }
    }
}
