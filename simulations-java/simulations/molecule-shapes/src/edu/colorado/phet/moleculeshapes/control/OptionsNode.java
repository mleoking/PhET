// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.UserComponents;
import edu.colorado.phet.moleculeshapes.model.Molecule;
import edu.colorado.phet.moleculeshapes.tabs.MoleculeViewTab;
import edu.umd.cs.piccolo.PNode;

/**
 * Displays a list of options that can be selected
 */
public class OptionsNode extends PNode {

//    private static final String[] CHECKBOX_STRINGS = new String[] {
//            Strings.CONTROL__SHOW_BOND_ANGLES, Strings.CONTROL__SHOW_LONE_PAIRS
//    };

    public OptionsNode( final MoleculeViewTab module, double INNER_WIDTH ) {
        System.out.println( module + ", " + INNER_WIDTH );
        // enforce the width constraint

//        double INNER_WIDTH = 0;
//        for ( String checkboxString : CHECKBOX_STRINGS ) {
//            INNER_WIDTH = Math.max( INNER_WIDTH, new PropertyCheckBoxNode( new UserComponent( "dummy" ), checkboxString, new Property<Boolean>( true ) ).getFullBounds().getWidth() );
//        }

        addChild( new Spacer( 0, 0, INNER_WIDTH, 10 ) );

        final Property<Double> y = new Property<Double>( 0.0 );

        PNode checkboxContainer = new PNode();

        /*---------------------------------------------------------------------------*
        * show lone pairs
        *----------------------------------------------------------------------------*/
        final PNode showLonePairsNode = new PropertyCheckBoxNode( UserComponents.showLonePairsCheckBox, Strings.CONTROL__SHOW_LONE_PAIRS, module.showLonePairs ) {{
            // enabled when there are lone pairs on the molecule
            final Runnable updateEnabled = new Runnable() {
                public void run() {
                    setEnabled( !module.getMolecule().getAllLonePairs().isEmpty() );
                }
            };
            final UpdateListener updateListener = JMEUtils.swingUpdateListener( updateEnabled );
            module.getMolecule().onBondChanged.addUpdateListener( updateListener, false );

            module.getMoleculeProperty().addObserver( new ChangeObserver<Molecule>() {
                public void update( Molecule newValue, Molecule oldValue ) {
                    oldValue.onBondChanged.removeListener( updateListener );
                    newValue.onBondChanged.addUpdateListener( updateListener, false );
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
        * show all lone pairs
        *----------------------------------------------------------------------------*/
        final PNode showAllLonePairsNode = new PropertyCheckBoxNode( UserComponents.showAllLonePairsCheckBox, Strings.CONTROL__SHOW_ALL_LONE_PAIRS, module.showAllLonePairs ) {{
            // enabled when there are terminal lone pairs on the molecule
            final Runnable updateEnabled = new Runnable() {
                public void run() {
                    setEnabled( module.showLonePairs.get() && !module.getMolecule().getDistantLonePairs().isEmpty() );
                }
            };
            final UpdateListener updateListener = JMEUtils.swingUpdateListener( updateEnabled );
            module.getMolecule().onBondChanged.addUpdateListener( updateListener, false );
            module.showLonePairs.addObserver( updateListener, false );

            module.getMoleculeProperty().addObserver( new ChangeObserver<Molecule>() {
                public void update( Molecule newValue, Molecule oldValue ) {
                    oldValue.onBondChanged.removeListener( updateListener );
                    newValue.onBondChanged.addUpdateListener( updateListener, false );
                    updateEnabled.run();
                }
            } );

            /*
            * Run this in the current thread. should be in EDT for construction. Needed since the other call
            * is fired off to run in the next JME frame, so we have a flickering initial effect otherwise.
            */
            updateEnabled.run();

            setOffset( 0, y.get() );
        }};
        if ( module.allowTogglingAllLonePairs() ) {
            System.out.println( "OptionsNode.OptionsNode" );
            checkboxContainer.addChild( showAllLonePairsNode );
            y.set( showAllLonePairsNode.getFullBounds().getMaxY() );
        }

        /*---------------------------------------------------------------------------*
        * show bond angles
        *----------------------------------------------------------------------------*/
        checkboxContainer.addChild( new PropertyCheckBoxNode( UserComponents.showBondAnglesCheckBox, Strings.CONTROL__SHOW_BOND_ANGLES, module.showBondAngles ) {{
            // enabled when there are 2 or more bonds (or always)
            Runnable updateEnabled = new Runnable() {
                public void run() {
                    setEnabled( !MoleculeShapesProperties.disableNAShowBondAngles.get()
                                || module.getMolecule().getRadialAtoms().size() >= 2 );
                }
            };
            module.getMolecule().onBondChanged.addUpdateListener( JMEUtils.swingUpdateListener( updateEnabled ), false );
            MoleculeShapesProperties.disableNAShowBondAngles.addObserver( JMEUtils.swingObserver( updateEnabled ) );

            setOffset( 0, y.get() );
        }} );

        checkboxContainer.setOffset( ( INNER_WIDTH - checkboxContainer.getFullBounds().getWidth() ) / 2, 0 );
        addChild( checkboxContainer );

    }
}
