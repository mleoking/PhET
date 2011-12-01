// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.module.MoleculeViewModule;
import edu.colorado.phet.moleculeshapes.module.moleculeshapes.MoleculeShapesControlPanel;
import edu.colorado.phet.moleculeshapes.module.moleculeshapes.MoleculeShapesModule;
import edu.umd.cs.piccolo.PNode;

/**
 * Displays a list of options that can be selected
 */
public class OptionsNode extends PNode {
    public OptionsNode( final MoleculeViewModule module ) {
        // enforce the width constraint
        addChild( new Spacer( 0, 0, MoleculeShapesControlPanel.INNER_WIDTH, 10 ) );

        final Property<Double> y = new Property<Double>( 0.0 );

        PNode checkboxContainer = new PNode();

        /*---------------------------------------------------------------------------*
        * show lone pairs
        *----------------------------------------------------------------------------*/
        final PNode showLonePairsNode = new PropertyCheckBoxNode( Strings.CONTROL__SHOW_LONE_PAIRS, MoleculeShapesModule.showLonePairs ) {{
            // enabled when there are lone pairs on the molecule
            final Runnable updateEnabled = new Runnable() {
                public void run() {
                    setEnabled( !module.getMolecule().getLonePairs().isEmpty() );
                }
            };
            final UpdateListener updateListener = JMEUtils.swingUpdateListener( updateEnabled );
            module.getMolecule().onGroupChanged.addUpdateListener( updateListener, false );

            module.getMoleculeProperty().addObserver( new ChangeObserver<MoleculeModel>() {
                public void update( MoleculeModel newValue, MoleculeModel oldValue ) {
                    oldValue.onGroupChanged.removeListener( updateListener );
                    newValue.onGroupChanged.addUpdateListener( updateListener, false );
                    updateEnabled.run();
                }
            } );

            /*
            * Run this in the current thread. should be in EDT for construction. Needed since the other call
            * is fired off to run in the next JME frame, so we have a flickering initial effect otherwise.
            */
            updateEnabled.run();
        }};
        if ( module.allowTogglingLonePairs() ) {
            checkboxContainer.addChild( showLonePairsNode );
            y.set( showLonePairsNode.getFullBounds().getMaxY() );
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

            setOffset( 0, y.get() );
        }} );

        checkboxContainer.setOffset( ( MoleculeShapesControlPanel.INNER_WIDTH - checkboxContainer.getFullBounds().getWidth() ) / 2, 0 );
        addChild( checkboxContainer );

    }
}
