// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.module.MoleculeShapesModule;
import edu.colorado.phet.moleculeshapes.module.MoleculeViewModule;
import edu.umd.cs.piccolo.PNode;

/**
 * Displays a list of options that can be selected
 */
public class OptionsNode extends PNode {
    public OptionsNode( final MoleculeViewModule module ) {
        // enforce the width constraint
        addChild( new Spacer( 0, 0, MoleculeShapesControlPanel.INNER_WIDTH, 10 ) );

        PNode checkboxContainer = new PNode();

        /*---------------------------------------------------------------------------*
        * show lone pairs
        *----------------------------------------------------------------------------*/
        final PNode showLonePairsNode = new PropertyCheckBoxNode( Strings.CONTROL__SHOW_LONE_PAIRS, MoleculeShapesModule.showLonePairs ) {{
            // enabled when there are lone pairs on the molecule
            Runnable updateEnabled = new Runnable() {
                public void run() {
                    setEnabled( !module.getMolecule().getLonePairs().isEmpty() );
                }
            };
            module.getMolecule().onGroupChanged.addUpdateListener( JMEUtils.swingUpdateListener( updateEnabled ), false );

            /*
            * Run this in the current thread. should be in EDT for construction. Needed since the other call
            * is fired off to run in the next JME frame, so we have a flickering initial effect otherwise.
            */
            updateEnabled.run();
        }};
        if ( module.allowTogglingLonePairs() ) {
            checkboxContainer.addChild( showLonePairsNode );
        }

        /*---------------------------------------------------------------------------*
        * show bond angles
        *----------------------------------------------------------------------------*/
        checkboxContainer.addChild( new PropertyCheckBoxNode( Strings.CONTROL__SHOW_BOND_ANGLES, module.showBondAngles ) {{
            // enabled when there are 2 or more bonds (or always)
            Runnable updateEnabled = new Runnable() {
                public void run() {
                    setEnabled( !MoleculeShapesProperties.disableNAShowBondAngles.get()
                                || module.getMolecule().getBondedGroups().size() >= 2 );
                }
            };
            module.getMolecule().onGroupChanged.addUpdateListener( JMEUtils.swingUpdateListener( updateEnabled ), false );
            MoleculeShapesProperties.disableNAShowBondAngles.addObserver( JMEUtils.swingObserver( updateEnabled ) );

            if ( module.allowTogglingLonePairs() ) {
                setOffset( 0, showLonePairsNode.getFullBounds().getMaxY() );
            }
        }} );

        checkboxContainer.setOffset( ( MoleculeShapesControlPanel.INNER_WIDTH - checkboxContainer.getFullBounds().getWidth() ) / 2, 0 );
        addChild( checkboxContainer );

    }
}
