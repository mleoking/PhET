/**
 * Class: FisionModule
 * Package: edu.colorado.phet.nuclearphysics.modules
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.NucleusGraphic;
import edu.colorado.phet.nuclearphysics.view.PotentialProfileGraphic;
import edu.colorado.phet.nuclearphysics.view.PotentialProfilePanel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Point2D;

public class FisionModule extends Module {
    private ApparatusPanel apparatusPanel;
    private PotentialProfile potentialProfile;
    private PotentialProfilePanel potentialProfilePanel;
    private ApparatusPanel physicalPanel;
    private Uranium2235 uraniumNucleus;

    public FisionModule(AbstractClock clock) {
        super("Fision");

        apparatusPanel = new ApparatusPanel();
        super.setApparatusPanel(apparatusPanel);

        potentialProfilePanel = new PotentialProfilePanel();
        BevelBorder baseBorder = (BevelBorder) BorderFactory.createRaisedBevelBorder();
        Border titledBorder = BorderFactory.createTitledBorder(baseBorder, "Potential Profile");
        potentialProfilePanel.setBorder(titledBorder);

        physicalPanel = new ApparatusPanel();
        BevelBorder baseBorder2 = (BevelBorder) BorderFactory.createRaisedBevelBorder();
        Border titledBorder2 = BorderFactory.createTitledBorder(baseBorder2, "Physical System");
        physicalPanel.setBorder(titledBorder2);


        apparatusPanel.setLayout(new GridLayout(1, 2));
        apparatusPanel.add(potentialProfilePanel);
        apparatusPanel.add(physicalPanel);



        // Start the model
        this.setModel(new FisionModel(clock));
        this.getModel().addModelElement(new ModelElement() {
            public void stepInTime(double dt) {
                apparatusPanel.repaint();
            }
        });
        boolean b = clock.hasStarted();
        b = clock.isRunning();


        potentialProfile = new PotentialProfile(250, 400, 75);
        PotentialProfileGraphic ppg = new PotentialProfileGraphic(potentialProfile);

        potentialProfilePanel.addPotentialProfile(ppg);

        uraniumNucleus = new Uranium2235(new Point2D.Double(200, 400));
        addNeucleus(uraniumNucleus);

        JPanel controlPanel = new FisionControlPanel(this);
        super.setControlPanel(controlPanel);

    }

    public void activate(PhetApplication app) {
    }

    public void deactivate(PhetApplication app) {
    }


    private void addNeucleus(Nucleus nucleus) {
        this.getModel().addModelElement(nucleus);
        NucleusGraphic ng = new NucleusGraphic(nucleus);
        physicalPanel.addGraphic(ng);
        potentialProfilePanel.addNucleus(ng);
    }

    public void setProfileMaxHeight(double modelValue) {
        potentialProfile.setMaxPotential(modelValue);
        potentialProfilePanel.repaint();
    }

    public void setProfileWellDepth(double wellDepth) {
        potentialProfile.setWellDepth(wellDepth);
        potentialProfilePanel.repaint();
    }

    public void setProfileWidth(double profileWidth) {
        potentialProfile.setWidth(profileWidth);
        potentialProfilePanel.repaint();
    }

    public PotentialProfile getPotentialProfile() {
        return this.potentialProfile;
    }

    public void testDecay() {
        DecayProducts dp = uraniumNucleus.decay();
        getModel().removeModelElement(dp.getN0());
        getModel().addModelElement(dp.getN1());
        getModel().addModelElement(dp.getN2());
        physicalPanel.removeGraphic(NucleusGraphic.getGraphic(dp.getN0()));
        NucleusGraphic n1g = new NucleusGraphic(dp.getN1());
        physicalPanel.addGraphic(n1g);
        NucleusGraphic n2g = new NucleusGraphic(dp.getN2());
        physicalPanel.addGraphic(n2g);
    }
}
