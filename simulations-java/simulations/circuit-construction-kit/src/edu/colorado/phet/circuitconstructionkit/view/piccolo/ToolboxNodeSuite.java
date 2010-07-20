package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.common.phetcommon.model.MutableBoolean;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 6, 2006
 * Time: 1:57:39 AM
 */

public class ToolboxNodeSuite extends PhetPNode {
    private ToolboxNode lifelikeToolbox;
    private ToolboxNode schematicToolbox;
    private BranchNodeFactory branchNodeFactory;

    public ToolboxNodeSuite(CCKModel model, CCKModule module, CCKSimulationPanel cckSimulationPanel, final BranchNodeFactory branchNodeFactory, final MutableBoolean lifelikeProperty) {
        this.branchNodeFactory = branchNodeFactory;
        lifelikeToolbox = new ToolboxNode(cckSimulationPanel, model, module, new BranchNodeFactory(model, cckSimulationPanel, module, new MutableBoolean(true)), cckSimulationPanel,lifelikeProperty);
        lifelikeToolbox.scale(1.0 / 80.0);
        addChild(lifelikeToolbox);

        schematicToolbox = new ToolboxNode(cckSimulationPanel, model, module, new BranchNodeFactory(model, cckSimulationPanel, module, new MutableBoolean(false)), cckSimulationPanel,lifelikeProperty);
        schematicToolbox.scale(1.0 / 80.0);
        addChild(schematicToolbox);

        SimpleObserver updateLifelike = new SimpleObserver() {
            public void update() {
                lifelikeToolbox.setVisible(lifelikeProperty.getValue());
                schematicToolbox.setVisible(!lifelikeProperty.getValue());
            }
        };
        lifelikeProperty.addObserver(updateLifelike);
        updateLifelike.update();
    }

    public void setBackground(Color color) {
        lifelikeToolbox.setBackground(color);
        schematicToolbox.setBackground(color);
    }

    public Color getBackgroundColor() {
        return lifelikeToolbox.getBackgroundColor();
    }

    public void setSeriesAmmeterVisible(boolean selected) {
        lifelikeToolbox.setSeriesAmmeterVisible(selected);
        schematicToolbox.setSeriesAmmeterVisible(selected);
    }

    public PNode getWireMaker() {
        return lifelikeToolbox.getWireMaker();
    }
}
