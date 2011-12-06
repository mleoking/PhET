// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.module.realmolecules;

import java.util.Arrays;

import edu.colorado.phet.chemistry.utils.ChemUtils;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.nodes.ComboBoxNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesPanelNode;
import edu.colorado.phet.moleculeshapes.control.OptionsNode;
import edu.colorado.phet.moleculeshapes.control.PropertyCheckBoxNode;
import edu.colorado.phet.moleculeshapes.control.PropertyRadioButtonNode;
import edu.colorado.phet.moleculeshapes.control.TitledControlPanelNode.TitleNode;
import edu.colorado.phet.moleculeshapes.model.RealMoleculeShape;
import edu.umd.cs.piccolo.PNode;

public class RealMoleculesControlPanel extends PNode {
    private static final double PANEL_SPACER = 20; // space between text and bond lines

    /*---------------------------------------------------------------------------*
    * information for width computations
    *----------------------------------------------------------------------------*/

    private static final double TITLE_PADDING = 5;
    private static final String[] TITLE_STRINGS = new String[] {
            Strings.CONTROL__BONDING, Strings.CONTROL__LONE_PAIR, Strings.CONTROL__OPTIONS, Strings.REAL_EXAMPLES__TITLE
    };
    private static final String[] CHECKBOX_STRINGS = new String[] {
            Strings.CONTROL__SHOW_BOND_ANGLES, Strings.CONTROL__SHOW_LONE_PAIRS
    };
    public static final double INNER_WIDTH = Math.ceil( getRequiredInnerWidth() );

    // used for placement of the molecule combo box
    private Spacer comboBoxSpacer;

    public RealMoleculesControlPanel( final RealMoleculesModule module, final Function0<Double> getControlPanelXPosition ) {

        // put it on 0 vertically
        setOffset( 0, 10 );

        final PNode comboBoxNode = new ComboBoxNode<RealMoleculeShape>(
                Arrays.asList( RealMoleculeShape.TAB_2_MOLECULES ),
                RealMoleculeShape.TAB_2_MOLECULES[0],
                new Function1<RealMoleculeShape, PNode>() {
                    public PNode apply( RealMoleculeShape realMoleculeShape ) {
                        return new HTMLNode( ChemUtils.toSubscript( realMoleculeShape.getDisplayName() ) );
                    }
                },
                "real molecule selection",
                new Function1<RealMoleculeShape, String>() {
                    public String apply( RealMoleculeShape realMoleculeShape ) {
                        return realMoleculeShape.getDisplayName();
                    }
                }
        ) {{
            selectedItem.addObserver( new ChangeObserver<RealMoleculeShape>() {
                public void update( final RealMoleculeShape newValue, RealMoleculeShape oldValue ) {
                    JMEUtils.invoke( new Runnable() {
                        public void run() {
                            module.switchToMolecule( newValue );
                        }
                    } );
                }
            } );
        }};

        final int dropDownBoxTopPadding = 10;

        /*---------------------------------------------------------------------------*
        * molecule panel
        *----------------------------------------------------------------------------*/
        final PNode moleculePanel = new MoleculeShapesPanelNode( new PNode() {{
            // ensure maximum width, and put it at the top so our panel node doesn't cut away the excess top padding
            addChild( new Spacer( 0, 0, INNER_WIDTH, 20 ) );

            // the spacer holds the place of the molecule combo box
            comboBoxSpacer = new Spacer( comboBoxNode.getFullBounds() ) {{
                // set the correct offset for the spacer
                setOffset( ( INNER_WIDTH - comboBoxNode.getFullBounds().getWidth() ) / 2,
                           dropDownBoxTopPadding );
            }};
            addChild( comboBoxSpacer );

            /*---------------------------------------------------------------------------*
            * real / model radio buttons
            *----------------------------------------------------------------------------*/
            final PNode realRadioNode = new PropertyRadioButtonNode<Boolean>( Strings.CONTROL__REAL_VIEW, module.showRealView, true ) {{
                setOffset( 0, comboBoxSpacer.getFullBounds().getMaxY() );
            }};
            addChild( realRadioNode );

            final PNode modelRadioNode = new PropertyRadioButtonNode<Boolean>( Strings.CONTROL__MODEL_VIEW, module.showRealView, false ) {{
                setOffset( 0, realRadioNode.getFullBounds().getMaxY() );
            }};
            addChild( modelRadioNode );

            // center the radio buttons
            final double maxWidth = Math.max( realRadioNode.getFullBounds().getWidth(), modelRadioNode.getFullBounds().getWidth() );
            double radioButtonHorizontalOffset = ( MoleculeShapesConstants.RIGHT_MIN_WIDTH - maxWidth ) / 2;
            realRadioNode.setOffset( radioButtonHorizontalOffset, realRadioNode.getYOffset() );
            modelRadioNode.setOffset( radioButtonHorizontalOffset, modelRadioNode.getYOffset() );
        }}, Strings.CONTROL__MOLECULE );
        addChild( moleculePanel );

        /*---------------------------------------------------------------------------*
        * options
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode optionsPanel = new MoleculeShapesPanelNode( new OptionsNode( module, INNER_WIDTH ), Strings.CONTROL__OPTIONS );
        optionsPanel.setOffset( 0, moleculePanel.getFullBounds().getMaxY() + 20 );
        addChild( optionsPanel );

        // set the combo box's offset based on our global full bounds, since the combo box is added to the root
        comboBoxNode.setOffset( comboBoxSpacer.getGlobalFullBounds().getX(), comboBoxSpacer.getGlobalFullBounds().getY() - dropDownBoxTopPadding );
        addChild( comboBoxNode );

        // spacer takes up as much vertical room as the combo box, so we prevent sub-pixel issues
        addChild( new Spacer( 0, 0, 10, 800 ) );
    }

    /*---------------------------------------------------------------------------*
    * computation of required width
    *----------------------------------------------------------------------------*/

    public static double getRequiredInnerWidth() {
        double maxWidth = MoleculeShapesConstants.RIGHT_MIN_WIDTH;
        for ( String titleString : TITLE_STRINGS ) {
            double width = new TitleNode( titleString ).getFullBounds().getWidth();
            if ( titleString.equals( Strings.REAL_EXAMPLES__TITLE ) ) {
                width += 45;
            }
            else {
                width += 25;
            }
            maxWidth = Math.max( maxWidth, width );
        }
        for ( String checkboxString : CHECKBOX_STRINGS ) {
            maxWidth = Math.max( maxWidth, new PropertyCheckBoxNode( checkboxString, new Property<Boolean>( true ) ).getFullBounds().getWidth() );
        }
        return maxWidth;
    }

}
