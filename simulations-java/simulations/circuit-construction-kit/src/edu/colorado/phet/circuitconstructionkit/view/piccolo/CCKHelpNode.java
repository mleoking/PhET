package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;

/**
 * User: Sam Reid
 * Date: Oct 5, 2006
 * Time: 1:07:29 AM
 */

public class CCKHelpNode extends PhetPNode {
    private CCKSimulationPanel cckSimulationPanel;
    private CCKModule module;

    public CCKHelpNode( CCKSimulationPanel cckSimulationPanel, CCKModule module ) {
        this.cckSimulationPanel = cckSimulationPanel;
        this.module = module;
        JunctionHelpNode junctionHelpNode = new JunctionHelpNode( cckSimulationPanel, module, CCKStrings.getHTML( "CCKHelp.JunctionHelp" ) );
        addChild( junctionHelpNode );

        BranchHelpNode branchHelpNode = new BranchHelpNode( cckSimulationPanel, module, CCKStrings.getHTML( "CCKHelp.ComponentHelp" ) );
        addChild( branchHelpNode );

        ToolboxHelpNode toolboxHelpNode = new ToolboxHelpNode( cckSimulationPanel, module );
        addChild( toolboxHelpNode );
    }

    static class ToolboxHelpNode extends HelpBalloon {
        private CCKSimulationPanel cckSimulationPanel;
        private CCKModule module;

        public ToolboxHelpNode( CCKSimulationPanel cckSimulationPanel, CCKModule module ) {
            super( cckSimulationPanel, CCKStrings.getHTML( "CCKHelp.ToolboxHelp" ), RIGHT_BOTTOM, 30 );
            this.cckSimulationPanel = cckSimulationPanel;
            this.module = module;
            pointAt( cckSimulationPanel.getToolboxNodeSuite(), cckSimulationPanel );
        }
    }

}
