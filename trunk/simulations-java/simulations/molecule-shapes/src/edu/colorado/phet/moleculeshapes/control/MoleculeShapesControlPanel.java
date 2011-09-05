// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Images;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.jme.JmeUtils;
import edu.colorado.phet.moleculeshapes.util.SimpleTarget;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * The main Molecule Shapes control panel on the right hand side. It is composed of multiple sub-panels,
 * namely "bonding", "lone pair", "options" and "real examples".
 */
public class MoleculeShapesControlPanel extends PNode {
    private static final double PANEL_SPACER = 20; // space between text and bond lines

    private final MoleculeShapesPanelNode realMoleculePanel;
    private RealMoleculePanelNode realMoleculeNode;

    public MoleculeShapesControlPanel( final MoleculeJMEApplication app, RealMoleculeOverlayNode overlayNode ) {

        /*---------------------------------------------------------------------------*
        * bonding panel
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode bondingPanel = new MoleculeShapesPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new Spacer( 0, 0, MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, 10 ) );

            final double spaceBetweenTypes = 15;

            final PNode singleNode = new BondTypeControlNode( app, new PImage( Images.SINGLE_BOND_SMALL ), 1 ) {{
                setOffset( 0, 10 );
            }};
            addChild( singleNode );
            final PNode doubleNode = new BondTypeControlNode( app, new PImage( Images.DOUBLE_BOND_SMALL ), 2 ) {{
                setOffset( 0, singleNode.getFullBounds().getMaxY() + spaceBetweenTypes );
            }};
            addChild( doubleNode );
            final PNode tripleNode = new BondTypeControlNode( app, new PImage( Images.TRIPLE_BOND_SMALL ), 3 ) {{
                setOffset( 0, doubleNode.getFullBounds().getMaxY() + spaceBetweenTypes );
            }};
            addChild( tripleNode );
        }}, "Bonding" );
        addChild( bondingPanel );

        // put it on 0 vertically
        setOffset( 0, 10 );

        /*---------------------------------------------------------------------------*
        * lone pair panel
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode nonBondingPanel = new MoleculeShapesPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new Spacer( 0, 0, MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, 10 ) );

            /*---------------------------------------------------------------------------*
            * lone pair control
            *----------------------------------------------------------------------------*/
            final BondTypeControlNode lonePairNode = new BondTypeControlNode( app, new PImage( Images.LONE_PAIR_SMALL ), 0 ) {{
                setOffset( 0, 10 );
                MoleculeJMEApplication.showLonePairs.addObserver( JmeUtils.swingObserver( new Runnable() {
                    public void run() {
                        updateState();
                    }
                } ), false );

                // TODO: note that it looks weird adding a pair when it is invisible. this is the spot to make the change (override isEnabled)
            }};
            addChild( lonePairNode );

            /*---------------------------------------------------------------------------*
            * show/hide toggle
            *----------------------------------------------------------------------------*/
            final TextButtonNode toggleLonePairsButton = new ToggleLonePairsButton( app.getMolecule() );
            // set Y offset
            toggleLonePairsButton.setOffset( toggleLonePairsButton.getOffset().getX(), lonePairNode.getFullBounds().getMaxY() + 10 );
            addChild( toggleLonePairsButton );

            // extra padding below since the TextButtonNode changes size on press. without this, the panel size changes mid-click
            addChild( new PNode() {{
                setOffset( 0, toggleLonePairsButton.getFullBounds().getMaxY() + 3 );
                addChild( new Spacer( 0, 0, 10, 0.1f ) );
            }} );
        }}, "Lone Pair" ) {{
            setOffset( 0, bondingPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        addChild( nonBondingPanel );

        /*---------------------------------------------------------------------------*
        * options
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode optionsPanel = new MoleculeShapesPanelNode( new PNode() {{
            // enforce the width constraint
            addChild( new Spacer( 0, 0, MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, 10 ) );

            /*---------------------------------------------------------------------------*
            * show bond angles
            *----------------------------------------------------------------------------*/
            addChild( new PSwing( new MoleculeShapesPropertyCheckBox( Strings.CONTROL__SHOW_BOND_ANGLES, MoleculeShapesProperties.showBondAngles ) ) {{
                setOffset( 0, ( MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH - getFullBounds().getWidth() ) / 2 );
            }} );
        }}, "Options" ) {{
            setOffset( 0, nonBondingPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        addChild( optionsPanel );

        /*---------------------------------------------------------------------------*
        * real molecules panel
        *----------------------------------------------------------------------------*/
        final Property<Boolean> minimized = new Property<Boolean>( true ) {{
            // reset minimization on app reset
            app.resetNotifier.addTarget( new SimpleTarget() {
                public void update() {
                    reset();
                }
            } );
        }};

        realMoleculeNode = new RealMoleculePanelNode( app.getMolecule(), app, overlayNode, minimized );
        realMoleculePanel = new MoleculeShapesPanelNode( realMoleculeNode, new PNode() {{
            final PText title = new PText( "Real Examples" ) {{
                setFont( new PhetFont( 14, true ) );
                setTextPaint( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
            }};
            addChild( title );

            final double TEXT_PADDING = 4;
            addChild( new MinimizeMaximizeButtonNode( minimized ) {{
                setOffset( title.getWidth() + TEXT_PADDING, ( title.getFullBounds().getHeight() - getFullBounds().getHeight() ) / 2 + 1 );
            }} );
        }} ) {{
            setOffset( 0, optionsPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        addChild( realMoleculePanel );
    }

    public boolean isOverlayVisible() {
        return realMoleculeNode.isOverlayVisible();
    }

    /**
     * @return Where to position the 3D molecule overlay
     */
    public PBounds getOverlayBounds() {
        return realMoleculeNode.getOverlayBounds();
    }

}
