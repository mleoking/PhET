// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.tabs.moleculeshapes;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.jmephet.JMEActionListener;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.control.BondTypeControlNode;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesPanelNode;
import edu.colorado.phet.moleculeshapes.control.OptionsNode;
import edu.colorado.phet.moleculeshapes.control.PropertyCheckBoxNode;
import edu.colorado.phet.moleculeshapes.control.TitledControlPanelNode.TitleNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.moleculeshapes.control.BondTypeControlNode.systemResponseForGeometries;

/**
 * The main Molecule Shapes control panel on the right hand side. It is composed of multiple sub-panels,
 * namely "bonding", "lone pair", "options" and "real examples".
 */
public class MoleculeShapesControlPanel extends PNode {
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
    private BondTypeControlNode singleBondNode;
    private BondTypeControlNode doubleBondNode;
    private BondTypeControlNode tripleBondNode;
    private BondTypeControlNode lonePairNode;

    public MoleculeShapesControlPanel( final MoleculeShapesTab module ) {

        /*---------------------------------------------------------------------------*
        * bonding panel
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode bondingPanel = new MoleculeShapesPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new Spacer( 0, 0, INNER_WIDTH, 10 ) );

            final double spaceBetweenTypes = 8;

            singleBondNode = new BondTypeControlNode( module, new Spacer( 0, 0, 100, 33 ), 1, module.addSingleBondEnabled ) {{
                setOffset( 0, 10 );
            }};
            addChild( singleBondNode );
            doubleBondNode = new BondTypeControlNode( module, new Spacer( 0, 0, 100, 33 ), 2, module.addDoubleBondEnabled ) {{
                setOffset( 0, singleBondNode.getFullBounds().getMaxY() + spaceBetweenTypes );
            }};
            addChild( doubleBondNode );
            tripleBondNode = new BondTypeControlNode( module, new Spacer( 0, 0, 100, 33 ), 3, module.addTripleBondEnabled ) {{
                setOffset( 0, doubleBondNode.getFullBounds().getMaxY() + spaceBetweenTypes );   // TODO: remove the images that used to be here
            }};
            addChild( tripleBondNode );
        }}, Strings.CONTROL__BONDING );
        addChild( bondingPanel );

        // put it on 0 vertically
        setOffset( 0, 10 );

        /*---------------------------------------------------------------------------*
        * lone pair panel
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode nonBondingPanel = new MoleculeShapesPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new Spacer( 0, 0, INNER_WIDTH, 10 ) );

            /*---------------------------------------------------------------------------*
            * lone pair control
            *----------------------------------------------------------------------------*/
            lonePairNode = new BondTypeControlNode( module, new Spacer( 0, 0, 61, 42 ), 0, module.addLonePairEnabled ) {
                {
                    setOffset( 0, 10 );

                    // make sure to update our state when "show lone pairs" changes
                    module.showLonePairs.addObserver( JMEUtils.swingObserver( new Runnable() {
                        public void run() {
                            updateState();
                        }
                    } ), false );
                }

                @Override protected boolean isEnabled() {
                    // add the extra constraint on visibility
                    return super.isEnabled() && module.showLonePairs.get();
                }
            };
            addChild( lonePairNode );
        }}, Strings.CONTROL__LONE_PAIR ) {{
            setOffset( 0, bondingPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        if ( !module.isBasicsVersion() ) {
            addChild( nonBondingPanel );
        }

        final PBounds lastBounds = module.isBasicsVersion() ? bondingPanel.getFullBounds() : nonBondingPanel.getFullBounds();

        final PNode removeAllButtonNode = new TextButtonNode( Strings.CONTROL__REMOVE_ALL,
                                                              MoleculeShapesConstants.REMOVE_BUTTON_FONT,
                                                              MoleculeShapesColor.REMOVE_BUTTON_BACKGROUND.get() ) {{
            addActionListener( new JMEActionListener( new Runnable() {
                public void run() {
                    module.getMolecule().removeAllGroups();

                    //System response for electron and molecule geometry names, copied from code in GeometryNameNode
                    systemResponseForGeometries( module.getMolecule() );
                }
            } ) );

            MoleculeShapesColor.REMOVE_BUTTON_BACKGROUND.addColorObserver( new VoidFunction1<Color>() {
                public void apply( Color color ) {
                    setBackground( color );
                }
            } );

            MoleculeShapesColor.REMOVE_BUTTON_TEXT.addColorObserver( new VoidFunction1<Color>() {
                public void apply( Color color ) {
                    setEnabledTextColor( color );
                }
            } );

            setOffset( ( lastBounds.getWidth() - getFullBounds().getWidth() ) / 2,
                       lastBounds.getMaxY() + PANEL_SPACER - 4 );
        }};
        addChild( removeAllButtonNode );

        /*---------------------------------------------------------------------------*
        * options
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode optionsPanel = new MoleculeShapesPanelNode( new OptionsNode( module, INNER_WIDTH ), Strings.CONTROL__OPTIONS );
        optionsPanel.setOffset( 0, removeAllButtonNode.getFullBounds().getMaxY() + PANEL_SPACER * 1.5 );
        addChild( optionsPanel );
    }

    public PBounds getSingleBondTargetBounds() {
        return singleBondNode.getGraphic().getGlobalFullBounds();
    }

    public PBounds getDoubleBondTargetBounds() {
        return doubleBondNode.getGraphic().getGlobalFullBounds();
    }

    public PBounds getTripleBondTargetBounds() {
        return tripleBondNode.getGraphic().getGlobalFullBounds();
    }

    public PBounds getLonePairTargetBounds() {
        return lonePairNode.getGraphic().getGlobalFullBounds();
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

}
