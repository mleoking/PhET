package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 6, 2006
 * Time: 1:57:39 AM
 * Copyright (c) Oct 6, 2006 by Sam Reid
 */

public class ToolboxNodeSuite extends PhetPNode {
    private ToolboxNode lifelikeToolbox;
    private ToolboxNode schematicToolbox;
    private BranchNodeFactory branchNodeFactory;

    public ToolboxNodeSuite( CCKModel model, ICCKModule module, CCKSimulationPanel cckSimulationPanel, BranchNodeFactory branchNodeFactory ) {
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
