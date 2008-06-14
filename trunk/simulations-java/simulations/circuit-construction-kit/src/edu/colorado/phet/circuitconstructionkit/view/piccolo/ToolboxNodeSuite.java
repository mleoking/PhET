package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Oct 6, 2006
 * Time: 1:57:39 AM
 */

public class ToolboxNodeSuite extends PhetPNode {
    private ToolboxNode lifelikeToolbox;
    private ToolboxNode schematicToolbox;
    private BranchNodeFactory branchNodeFactory;

    public ToolboxNodeSuite( CCKModel model, CCKModule module, CCKSimulationPanel cckSimulationPanel, BranchNodeFactory branchNodeFactory ) {
        this.branchNodeFactory = branchNodeFactory;
        lifelikeToolbox = new ToolboxNode( cckSimulationPanel, model, module, new BranchNodeFactory( model, cckSimulationPanel, module, true ), cckSimulationPanel );
        lifelikeToolbox.scale( 1.0 / 80.0 );
        addChild( lifelikeToolbox );

        schematicToolbox = new ToolboxNode( cckSimulationPanel, model, module, new BranchNodeFactory( model, cckSimulationPanel, module, false ), cckSimulationPanel );
        schematicToolbox.scale( 1.0 / 80.0 );
        addChild( schematicToolbox );

        branchNodeFactory.addListener( new BranchNodeFactory.Listener() {
            public void displayStyleChanged() {
                update();
            }
        } );
        update();
    }

    private void update() {
        lifelikeToolbox.setVisible( branchNodeFactory.isLifelike() );
        schematicToolbox.setVisible( !branchNodeFactory.isLifelike() );
    }

    public void setBackground( Color color ) {
        lifelikeToolbox.setBackground( color );
        schematicToolbox.setBackground( color );
    }

    public Color getBackgroundColor() {
        return lifelikeToolbox.getBackgroundColor();
    }

    public void setSeriesAmmeterVisible( boolean selected ) {
        lifelikeToolbox.setSeriesAmmeterVisible( selected );
        schematicToolbox.setSeriesAmmeterVisible( selected );
    }

    public PNode getWireMaker() {
        return lifelikeToolbox.getWireMaker();
    }
}
