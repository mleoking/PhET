// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D.Double;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.moleculeshapes.MoleculeShapesApplication;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Images;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel.Adapter;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

public class MoleculeShapesControlPanel extends PNode {
    private static final double PANEL_SPACER = 20; // space between text and bond lines

    private final MoleculeJMEApplication app;

    public MoleculeShapesControlPanel( final MoleculeJMEApplication app ) {
        this.app = app;

        /*---------------------------------------------------------------------------*
        * bonding panel
        *----------------------------------------------------------------------------*/
        final TitledControlPanelNode bondingPanel = new TitledControlPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new PhetPPath( new java.awt.geom.Rectangle2D.Double( 0, 0, MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, 10 ), new Color( 0, 0, 0, 0 ) ) );

            final double spaceBetweenTypes = 15;

            final PNode singleNode = new BondTypeNode(
                    new PImage( Images.SINGLE_BOND_SMALL ), 1 ) {{
                setOffset( 0, 10 );
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {
                        if ( enabled && event.getButton() == MouseEvent.BUTTON1 ) {
                            app.startNewInstanceDrag( 1 );
                        }
                    }
                } );
            }};
            addChild( singleNode );
            final PNode doubleNode = new BondTypeNode(
                    new PImage( Images.DOUBLE_BOND_SMALL ), 2 ) {{
                setOffset( 0, singleNode.getFullBounds().getMaxY() + spaceBetweenTypes );
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {
                        if ( enabled && event.getButton() == MouseEvent.BUTTON1 ) {
                            app.startNewInstanceDrag( 2 );
                        }
                    }
                } );
            }};
            addChild( doubleNode );
            final PNode tripleNode = new BondTypeNode(
                    new PImage( Images.TRIPLE_BOND_SMALL ), 3 ) {{
                setOffset( 0, doubleNode.getFullBounds().getMaxY() + spaceBetweenTypes );
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {
                        if ( enabled && event.getButton() == MouseEvent.BUTTON1 ) {
                            app.startNewInstanceDrag( 3 );
                        }
                    }
                } );
            }};
            addChild( tripleNode );
        }}, "Bonding", Color.BLACK, new BasicStroke( MoleculeShapesConstants.CONTROL_PANEL_BORDER_WIDTH ), MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
        addChild( bondingPanel );

        // put it on 0 vertically
        setOffset( 0, 10 );

        /*---------------------------------------------------------------------------*
        * non-bonding panel
        *----------------------------------------------------------------------------*/
        final TitledControlPanelNode nonBondingPanel = new TitledControlPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new PhetPPath( new Double( 0, 0, MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, 10 ), new Color( 0, 0, 0, 0 ) ) );

            addChild( new BondTypeNode(
                    new PImage( Images.LONE_PAIR_SMALL ), 0 ) {{
                setOffset( 0, 10 );
                // TODO: refactor the input listener into something more common (code duplication)
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {
                        if ( enabled && event.getButton() == MouseEvent.BUTTON1 ) {
                            app.startNewInstanceDrag( 0 );
                        }
                    }
                } );
            }} );
        }}, "Lone Pair", Color.BLACK, new BasicStroke( MoleculeShapesConstants.CONTROL_PANEL_BORDER_WIDTH ), MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR ) {{
            setOffset( 0, bondingPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        addChild( nonBondingPanel );
    }

    private class BondTypeNode extends PNode {
        private final int bondOrder;
        protected boolean enabled = true;

        private BondTypeNode( final PNode graphic, int bondOrder ) {
            this.bondOrder = bondOrder;

            // custom cursor handler for only showing hand when it is enabled
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseEntered( PInputEvent event ) {
                    if ( enabled ) {
                        ( (JComponent) event.getComponent() ).setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                    }
                }

                @Override public void mouseExited( PInputEvent event ) {
                    ( (JComponent) event.getComponent() ).setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                }
            } );

            addChild( graphic );

            // add a blank background that will allow the user to click on this
            addChild( 0, new PhetPPath( getFullBounds(), new Color( 0, 0, 0, 0 ) ) );

            app.getMolecule().addListener( new Adapter() {
                @Override public void onGroupAdded( PairGroup group ) {
                    updateEnabled();
                }

                @Override public void onGroupRemoved( PairGroup group ) {
                    updateEnabled();
                }
            } );
            updateEnabled();
        }

        private void updateEnabled() {
            enabled = app.getMolecule().wouldAllowBondOrder( bondOrder );
            if ( enabled ) {
                setTransparency( 1 );
            }
            else {
                setTransparency( 0.3f );
            }
            repaint();
        }
    }

}
