/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.dvm;

import edu.colorado.phet.cck.common.SimpleObservable;
import edu.colorado.phet.cck.common.SimpleObserver;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.coreadditions.graphics.DifferentialDragHandler;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Oct 26, 2003
 * Time: 2:05:39 AM
 * Copyright (c) Oct 26, 2003 by Sam Reid
 */
public class VoltmeterUnitGraphic extends SimpleObservable implements InteractiveGraphic {

    private BufferedImage dvmImage;
    private ModelViewTransform2d transform;
    VoltmeterUnit vm;
    private AffineTransform trf;
    int x;
    int y;
    private DecimalFormat df = new DecimalFormat("0.0#");
    private String voltageText;
    private Font font = new Font("Dialog", 0, 20);

    public VoltmeterUnitGraphic(VoltmeterUnit vm, BufferedImage dvmImage, ModelViewTransform2d transform) {
        this.vm = vm;
        this.dvmImage = dvmImage;
        this.transform = transform;
        this.trf = new AffineTransform();
        vm.addObserver(new SimpleObserver() {
            public void update() {
                changed();
            }
        });
        changed();
        transform.addTransformListener(new TransformListener() {
            public void transformChanged(ModelViewTransform2d modelViewTransform2d) {
                changed();
            }
        });
    }

    private void changed() {
        Point loc = transform.modelToView(vm.getX(), vm.getY());
        this.x = loc.x;
        this.y = loc.y;
        trf.setToTranslation(loc.x, loc.y);
        updateObservers();
    }

    public boolean canHandleMousePress(MouseEvent event) {
        Rectangle2D.Double rect = new Rectangle2D.Double(x, y, dvmImage.getWidth(), dvmImage.getHeight());
        return rect.contains(event.getPoint());
    }

    public void mousePressed(MouseEvent event) {
        ddh = new DifferentialDragHandler(event.getPoint());
    }

    DifferentialDragHandler ddh;

    public void mouseDragged(MouseEvent event) {
        Point dx = ddh.getDifferentialLocationAndReset(event.getPoint());
        Point2D.Double modelDX = transform.viewToModelDifferential(dx);
        vm.translate(modelDX.x, modelDX.y);
    }

    public void mouseReleased(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent event) {
        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void paint(Graphics2D g) {
        g.drawRenderedImage(dvmImage, trf);
        g.setFont(font);
        g.setColor(Color.black);
        if (voltageText != null)
            g.drawString(voltageText, x + 15, y + 40);
    }

    public Point getBlackWirePoint() {
        return new Point(x + 88, y + 210);
    }

    public Point getRedWirePoint() {
        return new Point(x + 12, y + 210);
    }

    public void setVoltage(double voltage) {
        if (Double.isNaN(voltage)) {
            this.voltageText = "???";
        } else
            this.voltageText = df.format(voltage) + " V";
    }
}
