/**
 * Class: PotentialProfilePanel
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 6:03:01 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.ApparatusPanel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class PotentialProfilePanel extends ApparatusPanel {
    private NucleusGraphic nucleusGraphic;
    private PotentialProfileGraphic profileGraphic;
    private Point2D.Double origin;

    public PotentialProfilePanel() {
        origin = new Point2D.Double(250, 600);
    }

    public void addNucleus(NucleusGraphic nucleusGraphic) {
        this.nucleusGraphic = nucleusGraphic;
    }

    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        double x = origin.getX();
        double y = origin.getY() - profileGraphic.getProfile().getWellPotential() - 10;
        AffineTransform atx = scaleInPlaceTx(.3, x, y);

        Graphics2D g2 = (Graphics2D) graphics;
        AffineTransform orgTx = g2.getTransform();
        g2.setTransform(atx);
        nucleusGraphic.paint(g2, (int) x, (int) y);
        g2.setTransform(orgTx);
    }

    public void addPotentialProfile(PotentialProfileGraphic profileGraphic) {
        this.profileGraphic = profileGraphic;
        this.profileGraphic.setOrigin(origin);
        super.addGraphic(profileGraphic);
    }


    public static AffineTransform scaleInPlaceTx(double scale, double x, double y) {
        AffineTransform atx = new AffineTransform();
        atx.translate(x, y);
        atx.scale(scale, scale);
        atx.translate(-x, -y);
        return atx;
    }
}
