// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.tabs.realmolecules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;

import edu.colorado.phet.chemistry.utils.ChemUtils;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.nodes.ComboBoxNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.common.piccolophet.nodes.kit.Kit;
import edu.colorado.phet.common.piccolophet.nodes.kit.KitSelectionNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesApplication;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.UserComponents;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesPanelNode;
import edu.colorado.phet.moleculeshapes.control.OptionsNode;
import edu.colorado.phet.moleculeshapes.control.PropertyCheckBoxNode;
import edu.colorado.phet.moleculeshapes.control.PropertyRadioButtonNode;
import edu.colorado.phet.moleculeshapes.control.TitledControlPanelNode.TitleNode;
import edu.colorado.phet.moleculeshapes.model.RealMoleculeShape;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.moleculeshapes.model.RealMoleculeShape.*;

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

    // used for placement of the molecule selection node
    private Spacer moleculeSelectionSpacer;

    private RealMoleculeShape[][] kitMolecules;

    public RealMoleculesControlPanel( final RealMoleculesTab module, final Function0<Double> getControlPanelXPosition, boolean isBasicsVersion ) {
        if ( isBasicsVersion ) {
            kitMolecules = new RealMoleculeShape[][] {
                    { BORON_TRIFLUORIDE, METHANE, PHOSPHORUS_PENTACHLORIDE, SULFUR_HEXAFLUORIDE } };
        }
        else {
            kitMolecules = new RealMoleculeShape[][] {
                    { BORON_TRIFLUORIDE, METHANE, PHOSPHORUS_PENTACHLORIDE, SULFUR_HEXAFLUORIDE },
                    { CARBON_DIOXIDE, SULFUR_DIOXIDE, WATER, XENON_DIFLUORIDE },
                    { SULFUR_DIOXIDE, AMMONIA, SULFUR_TETRAFLUORIDE, BROMINE_PENTAFLUORIDE },
                    { WATER, CHLORINE_TRIFLUORIDE, XENON_TETRAFLUORIDE } };
        }

        // put it on 0 vertically
        setOffset( 0, 10 );

        RealMoleculeShape[] comboBoxMolecules = isBasicsVersion ? TAB_2_BASIC_MOLECULES : TAB_2_MOLECULES;

        final PNode moleculeSelectionNode = module.shouldUseKit() ? new ZeroOffsetNode( new PNode() {{
            /*---------------------------------------------------------------------------*
            * kit molecule selection
            *----------------------------------------------------------------------------*/
            Dimension anticipatedDimension = getMaximumKitDimensions();

            final MoleculeKit[] kits = new MoleculeKit[kitMolecules.length];
            for ( int i = 0; i < kitMolecules.length; i++ ) {
                kits[i] = new MoleculeKit( module, anticipatedDimension, kitMolecules[i] );
            }

            final Property<Integer> selectedKit = new Property<Integer>( 0 );
            selectedKit.addObserver( new SimpleObserver() {
                public void update() {
                    final RealMoleculeShape currentMolecule = kits[selectedKit.get()].getCurrentMolecule();
                    JMEUtils.invoke( new Runnable() {
                        public void run() {
                            module.switchToMolecule( currentMolecule );
                        }
                    } );
                }
            } );
            addChild( new KitSelectionNode<PNode>( selectedKit, new Spacer( 0, 0, 80, 10 ) {{
                setPickable( false );
                setChildrenPickable( false );
            }}, kits ) {{
                controlHolderNode.setOffset( controlHolderNode.getXOffset(), controlHolderNode.getYOffset() + 100 );
            }} );
        }} ) : new ComboBoxNode<RealMoleculeShape>(
                /*---------------------------------------------------------------------------*
                * combo box molecule selection
                *----------------------------------------------------------------------------*/
                UserComponents.moleculeComboBox,
                new Function1<RealMoleculeShape, String>() {
                    public String apply( RealMoleculeShape realMoleculeShape ) {
                        return realMoleculeShape.getDisplayName();
                    }
                },
                Arrays.asList( comboBoxMolecules ),
                comboBoxMolecules[0],
                new Function1<RealMoleculeShape, PNode>() {
                    public PNode apply( RealMoleculeShape realMoleculeShape ) {
                        return new HTMLNode( ChemUtils.toSubscript( realMoleculeShape.getDisplayName() ) );
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
        final int extraPaddingInsets = 15;
        final PNode moleculePanel = new MoleculeShapesPanelNode( new PNode() {{
            // ensure maximum width, and put it at the top so our panel node doesn't cut away the excess top padding
            addChild( new Spacer( 0, 0, INNER_WIDTH, 20 ) );

            // the spacer holds the place of the molecule combo box
            moleculeSelectionSpacer = new Spacer( moleculeSelectionNode.getFullBounds() ) {{
                // set the correct offset for the spacer
                setOffset( ( INNER_WIDTH - moleculeSelectionNode.getFullBounds().getWidth() ) / 2,
                           dropDownBoxTopPadding );
            }};
            addChild( moleculeSelectionSpacer );

            /*---------------------------------------------------------------------------*
            * real / model radio buttons
            *----------------------------------------------------------------------------*/
            final PNode realRadioNode = new PropertyRadioButtonNode<Boolean>( UserComponents.realViewCheckBox, Strings.CONTROL__REAL_VIEW, module.showRealView, true );

            // visibility handling (and initial adding)
            MoleculeShapesApplication.showRealMoleculeRadioButtons.addObserver( new SimpleObserver() {
                public void update() {
                    if ( MoleculeShapesApplication.showRealMoleculeRadioButtons.get() ) {
                        addChild( realRadioNode );
                    }
                    else {
                        removeChild( realRadioNode );
                    }
                }
            } );

            final PNode modelRadioNode = new PropertyRadioButtonNode<Boolean>( UserComponents.modelViewCheckBox, Strings.CONTROL__MODEL_VIEW, module.showRealView, false );

            // visibility handling (and initial adding)
            MoleculeShapesApplication.showRealMoleculeRadioButtons.addObserver( new SimpleObserver() {
                public void update() {
                    if ( MoleculeShapesApplication.showRealMoleculeRadioButtons.get() ) {
                        addChild( modelRadioNode );
                    }
                    else {
                        removeChild( modelRadioNode );
                    }
                }
            } );

//            if ( module.shouldUseKit() ) {
            // center the radio buttons side-by-side
            double spacer = 10;
            double width = realRadioNode.getFullBounds().getWidth() + spacer + modelRadioNode.getFullBounds().getWidth();
            double x = ( INNER_WIDTH - width ) / 2;
            double y = moleculeSelectionSpacer.getFullBounds().getMaxY();
            realRadioNode.setOffset( x, y );
            modelRadioNode.setOffset( x + realRadioNode.getFullBounds().getWidth() + spacer, y );
//            }
//            else {
//                // center the radio buttons individually, with one on top of the other
//                final double maxWidth = Math.max( realRadioNode.getFullBounds().getWidth(), modelRadioNode.getFullBounds().getWidth() );
//                double radioButtonHorizontalOffset = ( MoleculeShapesConstants.RIGHT_MIN_WIDTH - maxWidth ) / 2;
//
//                realRadioNode.setOffset( radioButtonHorizontalOffset, moleculeSelectionSpacer.getFullBounds().getMaxY() );
//                modelRadioNode.setOffset( radioButtonHorizontalOffset, realRadioNode.getFullBounds().getMaxY() );
//            }
        }}, Strings.CONTROL__MOLECULE, extraPaddingInsets );
        addChild( moleculePanel );

        /*---------------------------------------------------------------------------*
        * options
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode optionsPanel = new MoleculeShapesPanelNode( new OptionsNode( module, INNER_WIDTH + extraPaddingInsets ), Strings.CONTROL__OPTIONS );
        optionsPanel.setOffset( 0, moleculePanel.getFullBounds().getMaxY() + 20 );
        addChild( optionsPanel );

        // set the combo box's offset based on our global full bounds, since the combo box is added to the root
        moleculeSelectionNode.setOffset( moleculeSelectionSpacer.getGlobalFullBounds().getX(), moleculeSelectionSpacer.getGlobalFullBounds().getY() - dropDownBoxTopPadding );
        addChild( moleculeSelectionNode );

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
            maxWidth = Math.max( maxWidth, new PropertyCheckBoxNode( new UserComponent( "dummy" ), checkboxString, new Property<Boolean>( true ) ).getFullBounds().getWidth() );
        }
        return maxWidth;
    }

    public static class MoleculeKit extends Kit<PNode> {
        private Property<RealMoleculeShape> moleculeOptions;
        private final RealMoleculeShape[] shapes;

        public MoleculeKit( final RealMoleculesTab module, final Dimension anticipatedDimension, final RealMoleculeShape... shapes ) {
            // stub, will have content added later
            super( new PNode() );
            this.shapes = shapes;

            moleculeOptions = new Property<RealMoleculeShape>( shapes[0] );
            moleculeOptions.addObserver( new ChangeObserver<RealMoleculeShape>() {
                public void update( RealMoleculeShape newValue, RealMoleculeShape oldValue ) {
                    module.switchToMolecule( newValue );
                }
            } );
            final Property<Double> y = new Property<Double>( 0.0 );
            for ( RealMoleculeShape shape : shapes ) {
                PropertyRadioButtonNode<RealMoleculeShape> radioButtonNode = createRadioButton( shape, moleculeOptions );
                radioButtonNode.setOffset( 0, y.get() );
                y.set( radioButtonNode.getFullBounds().getMaxY() );
                content.addChild( radioButtonNode );
            }

            double padding = 5;
            double round = 10;

//            content.addChild( 0, new PhetPPath( new RoundRectangle2D.Double( -padding, -padding, anticipatedDimension.getWidth() + padding * 2, anticipatedDimension.getHeight() + padding * 2, round, round ), Color.WHITE ) );
            content.addChild( 0, new PhetPPath( new RoundRectangle2D.Double( -padding, -padding, content.getFullBounds().getWidth() + padding * 2, content.getFullBounds().getHeight() + padding * 2, round, round ), Color.WHITE ) );
        }

        public RealMoleculeShape getCurrentMolecule() {
            return moleculeOptions.get();
        }

        public RealMoleculeShape[] getShapes() {
            return shapes;
        }
    }

    public static PropertyRadioButtonNode<RealMoleculeShape> createRadioButton( final RealMoleculeShape shape, Property<RealMoleculeShape> moleculeOptions ) {
        return new PropertyRadioButtonNode<RealMoleculeShape>( UserComponents.valueOf( shape.getDisplayName() ), "<html>" + ChemUtils.toSubscript( shape.getDisplayName() ) + "</html>", moleculeOptions, shape ) {{
            getRadioButton().setForeground( Color.BLACK );
        }};
    }

    public Dimension getMaximumKitDimensions() {
        Dimension result = new Dimension( 0, 0 );

        for ( RealMoleculeShape[] shapes : kitMolecules ) {
            double x = 0;
            double y = 0;
            for ( RealMoleculeShape shape : shapes ) {
                PNode node = createRadioButton( shape, Property.property( shape ) );
                x = Math.max( x, node.getFullBounds().getWidth() );
                y += node.getFullBounds().getHeight();
            }
            result = new Dimension( Math.max( result.width, (int) Math.ceil( x ) ), Math.max( result.height, (int) Math.ceil( y ) ) );
        }
        return result;
    }

}
