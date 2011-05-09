// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.ConstantDensityPropagator;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 8, 2006
 * Time: 9:37:56 PM
 */

public class TimeScaleNode extends PhetPNode {
    private CCKSimulationPanel cckSimulationPanel;
    private CCKModel model;
    private ShadowPText shadowPText;

    public TimeScaleNode(CCKSimulationPanel cckSimulationPanel, final CCKModel model) {
        this.cckSimulationPanel = cckSimulationPanel;
        this.model = model;
        model.getParticleSet().getPropagator().addListener(new ConstantDensityPropagator.Listener() {
            public void timeScaleChanged() {
                update();
            }
        });
        shadowPText = new ShadowPText("Hello!");
        shadowPText.setFont(new PhetFont(Font.BOLD, 16));
        shadowPText.setTextPaint(Color.red);
        shadowPText.setShadowColor(Color.black);
        shadowPText.setShadowOffset(1, 1);
        addChild(shadowPText);
        update();
    }

    private void update() {
        ConstantDensityPropagator propagator = model.getParticleSet().getPropagator();
        String percent = propagator.getPercentString();
        if (!percent.equals(propagator.getDecimalFormat().format(100)) && propagator.getTimeScalingPercentValue() < 95) {
            if (cckSimulationPanel.getElectronsVisible()) {
                if (percent.equals("1")) {
                    percent = "< 1";
                }
                setText(CCKResources.getString("ConstantDensityPropagator.SpeedLimitReached1")
                        + " " + percent + CCKResources.getString("ConstantDensityPropagator.SpeedLimitReached2"));
                setVisible(true);
            } else {
                setVisible(false);
            }
        } else {
            setText("");
            setVisible(false);
        }
    }

    public void setText(String text) {
        shadowPText.setText(text);
    }
}
