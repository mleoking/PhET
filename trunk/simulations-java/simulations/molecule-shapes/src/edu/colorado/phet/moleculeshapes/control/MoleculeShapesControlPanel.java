// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D.Double;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Images;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel.AnyChangeAdapter;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

public class MoleculeShapesControlPanel extends PNode {
    private static final double PANEL_SPACER = 20; // space between text and bond lines

    public MoleculeShapesControlPanel( final MoleculeJMEApplication app ) {

        /*---------------------------------------------------------------------------*
        * bonding panel
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode bondingPanel = new MoleculeShapesPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new PhetPPath( new java.awt.geom.Rectangle2D.Double( 0, 0, MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, 10 ), new Color( 0, 0, 0, 0 ) ) );

            final double spaceBetweenTypes = 15;

            final PNode singleNode = new BondTypeControlNode(
                    app,
                    new PImage( Images.SINGLE_BOND_SMALL ), 1 ) {{
                setOffset( 0, 10 );
                addDragListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {
                        if ( enabled && event.getButton() == MouseEvent.BUTTON1 ) {
                            app.startNewInstanceDrag( 1 );
                        }
                    }
                } );
            }};
            addChild( singleNode );
            final PNode doubleNode = new BondTypeControlNode(
                    app,
                    new PImage( Images.DOUBLE_BOND_SMALL ), 2 ) {{
                setOffset( 0, singleNode.getFullBounds().getMaxY() + spaceBetweenTypes );
                addDragListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {
                        if ( enabled && event.getButton() == MouseEvent.BUTTON1 ) {
                            app.startNewInstanceDrag( 2 );
                        }
                    }
                } );
            }};
            addChild( doubleNode );
            final PNode tripleNode = new BondTypeControlNode(
                    app,
                    new PImage( Images.TRIPLE_BOND_SMALL ), 3 ) {{
                setOffset( 0, doubleNode.getFullBounds().getMaxY() + spaceBetweenTypes );
                addDragListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {
                        if ( enabled && event.getButton() == MouseEvent.BUTTON1 ) {
                            app.startNewInstanceDrag( 3 );
                        }
                    }
                } );
            }};
            addChild( tripleNode );
        }}, "Bonding" );
        addChild( bondingPanel );

        // put it on 0 vertically
        setOffset( 0, 10 );

        /*---------------------------------------------------------------------------*
        * non-bonding panel
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode nonBondingPanel = new MoleculeShapesPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new PhetPPath( new Double( 0, 0, MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, 10 ), new Color( 0, 0, 0, 0 ) ) );

            final BondTypeControlNode lonePairNode = new BondTypeControlNode(
                    app,
                    new PImage( Images.LONE_PAIR_SMALL ), 0 ) {
                {
                    setOffset( 0, 10 );
                    // TODO: refactor the input listener into something more common (code duplication)
                    addDragListener( new PBasicInputEventHandler() {
                        @Override public void mousePressed( PInputEvent event ) {
                            if ( enabled && event.getButton() == MouseEvent.BUTTON1 ) {
                                app.startNewInstanceDrag( 0 );
                            }
                        }
                    } );
                    MoleculeJMEApplication.showLonePairs.addObserver( new SimpleObserver() {
                        public void update() {
                            updateState();
                        }
                    } );
                }

                @Override protected boolean isEnabled() {
                    // TODO: note that it looks weird adding a pair when it is invisible
                    return super.isEnabled();
//                    return super.isEnabled() && MoleculeJMEApplication.showLonePairs.get();
                }
            };
            addChild( lonePairNode );

            final TextButtonNode toggleLonePairsButton = new TextButtonNode( "<will be replaced>", new PhetFont( 14 ), Color.ORANGE ) {
                {
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            MoleculeJMEApplication.showLonePairs.set( !MoleculeJMEApplication.showLonePairs.get() );
                            updateState();
                        }
                    } );
                    app.getMolecule().addListener( new AnyChangeAdapter() {
                        @Override public void onGroupChange( PairGroup group ) {
                            updateState();
                        }
                    } );
                    updateState();
                }

                public void updateState() {
                    // update the text state. TODO i18n
                    setText( MoleculeJMEApplication.showLonePairs.get() ? "Hide Lone Pairs" : "Show Lone Pairs" );

                    // center horizontally, and place below the lone pair image
                    setOffset(
                            ( MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH - getFullBounds().getWidth() ) / 2,
                            lonePairNode.getFullBounds().getMaxY() + 10 );

                    // enabled if there are lone pairs to hide
                    setEnabled( !app.getMolecule().getLonePairs().isEmpty() );
                }
            };
            addChild( toggleLonePairsButton );

            addChild( new PNode() {{
                setOffset( 0, toggleLonePairsButton.getFullBounds().getMaxY() + 3 );
                addChild( new PhetPPath( new java.awt.geom.Rectangle2D.Float( 0, 0, 10, 0.1f ), new Color( 0, 0, 0, 0 ) ) );
            }} );
        }}, "Lone Pair" ) {{
            setOffset( 0, bondingPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        addChild( nonBondingPanel );

        /*---------------------------------------------------------------------------*
        * experimental Jmol panel
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode jmolPanel = new MoleculeShapesPanelNode( new RealMoleculeNode(), "Real Molecules" ) {{
            setOffset( 0, nonBondingPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        addChild( jmolPanel );
    }

}
