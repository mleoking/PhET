/**
 * Class: Photon
 * Package: edu.colorado.phet.colorvision
 * Author: Another Guy
 * Date: Feb 22, 2004
 */
package edu.colorado.phet.colorvision;

import java.awt.*;
import java.util.ArrayList;

public class Photon {
    private boolean isVisible;
    private int xLoc;
    private int yLoc;
    private double ds = 10;
    private double s = 15;
    private int rgb;
    private Color color;
    private double wavelength;
    private double theta;


    private static ArrayList instances = new ArrayList();
//        private static boolean init = false;
    private static Stroke stroke = new BasicStroke(1f);

    static void stepInstances() {
        for (int i = 0; i < Photon.instances.size(); i++) {
            ((Photon) instances.get(i)).stepInTime();
        }
    }

    static void deleteInstance(Photon p) {
        for (int i = 0; i < Photon.instances.size(); i++) {
            // NOTE!!! If Photon.instances[i] doesn't work!!!
//                p2 = instances[i];
//			if (Photon.instances[i] == p) {
            if (instances.get(i) == p) {
                Photon.instances.remove(i);
            }
        }
    }

    static ArrayList getInstances() {
        return instances;
    }

    static ArrayList getPhotonsWithinBounds(int x, int y, int w, int h) {
        ArrayList result = new ArrayList();
        for (int i = 0; i < Photon.instances.size(); i++) {
            Photon p = (Photon) Photon.instances.get(i);
            if (p.xLoc >= x && p.xLoc <= x + w && p.yLoc >= y && p.yLoc <= y + h) {
                result.add(p);
            }
        }
        return result;
    }

    public Photon(int x, int y, double theta, double wavelength) {
        this.xLoc = x;
        this.yLoc = y;
        this.theta = theta;
        setWavelength(wavelength);
//		this.rgb = ColorUtil.getColor(wavelength);
        this.isVisible = true;
        Photon.instances.add(this);
    }

    public int getX() {
        return xLoc;
    }

    public void setLocation(int xLoc, int yLoc) {
        this.xLoc = xLoc;
        this.yLoc = yLoc;
    }

    public double getWavelength() {
        return this.wavelength;
    }

    public void setWavelength(double wavelength) {
        this.wavelength = wavelength;
        this.rgb = ColorUtil.getColor(wavelength);
    }

    public void setRgb(int rgb) {
        this.rgb = rgb;
        ColorUtil.ColorTransform ctx = ColorUtil.colorToCtx( rgb );
        this.color = new Color( ctx.rb,  ctx.gb, ctx.bb );
    }

    public int getRgb() {
        return rgb;
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void stepInTime() {
        this.xLoc += ds * Math.cos(this.theta);
        this.yLoc += ds * Math.sin(this.theta);
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        this.stepInTime();
        if (this.isVisible) {
            g2.setStroke(stroke);
            g2.setColor(this.color);
            g2.drawLine(this.xLoc, this.yLoc,
                    (int) (this.xLoc - s * Math.cos(this.theta)),
                    (int) (this.yLoc - s * Math.sin(this.theta)));
        }
    }
}
