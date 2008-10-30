package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.piccolophet.PhetPNode;

public class RightClickHelpNode extends PhetPNode {
    private boolean userRightClicked = false;
    private CCKModule module;

    public RightClickHelpNode( CCKSimulationPanel cckSimulationPanel, CCKModule module ) {
        this.module = module;
        String text = PhetUtilities.isMacintosh() ? CCKStrings.getString( "CCKHelp.right-click-help-mac" ): CCKStrings.getString( "CCKHelp.right-click-help" );
        BranchHelpNode branchHelpNode = new BranchHelpNode( cckSimulationPanel, module, text );
        addChild( branchHelpNode );

        setVisible( false );
        cckSimulationPanel.addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                updateVisibility();
            }

            public void mousePressed( MouseEvent e ) {
                if ( e.isMetaDown() ) {//check for right click
                    userRightClicked = true;
                    updateVisibility();
                }
            }
        } );
    }

    private void updateVisibility() {
        setVisible( !userRightClicked && isNonWireSelected() );
    }

    private boolean isNonWireSelected() {
        Branch[] b = module.getCircuit().getSelectedBranches();
        for ( int i = 0; i < b.length; i++ ) {
            Branch branch = b[i];
            if ( !( branch instanceof Wire ) ) {
                return true;
            }
        }
        return false;
    }
}
