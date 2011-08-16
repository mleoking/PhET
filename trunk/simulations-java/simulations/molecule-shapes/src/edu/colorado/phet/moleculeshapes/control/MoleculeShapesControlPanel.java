// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D.Double;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.moleculeshapes.MoleculeShapesApplication;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel.Adapter;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

public class MoleculeShapesControlPanel extends PNode {
    private static final double CONTROL_PANEL_INNER_WIDTH = 150; // width of the inner parts of the control panel
    private static final double BOND_TEXT_SPACER = 5; // space between text and bond lines
    private static final double PANEL_SPACER = 20; // space between text and bond lines

    public static final int BOND_WIDTH = 50;
    public static final int BOND_HEIGHT = 5;
    public static final int BOND_SPACING = 2;

    private final MoleculeJMEApplication app;

    public MoleculeShapesControlPanel( final MoleculeJMEApplication app ) {
        this.app = app;
        /*---------------------------------------------------------------------------*
        * bonding panel
        *----------------------------------------------------------------------------*/
        final InnerControlPanelNode bondingPanel = new InnerControlPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new PhetPPath( new java.awt.geom.Rectangle2D.Double( 0, 0, CONTROL_PANEL_INNER_WIDTH, 10 ), new Color( 0, 0, 0, 0 ) ) );

            final double spaceBetweenTypes = 15;

            final PNode singleNode = new BondTypeNode(
                    new BondLine( 0 ), 1, "Single" ) {{
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
                    new PNode() {{
                        addChild( new BondLine( 0 ) );
                        addChild( new BondLine( 1 ) );
                    }}, 2, "Double" ) {{
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
                    new PNode() {{
                        addChild( new BondLine( 0 ) );
                        addChild( new BondLine( 1 ) );
                        addChild( new BondLine( 2 ) );
                    }}, 3, "Triple" ) {{
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
        }}, "Bonding" );
        addChild( bondingPanel );

        /*---------------------------------------------------------------------------*
        * non-bonding panel
        *----------------------------------------------------------------------------*/
        final InnerControlPanelNode nonBondingPanel = new InnerControlPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new PhetPPath( new Double( 0, 0, CONTROL_PANEL_INNER_WIDTH, 10 ), new Color( 0, 0, 0, 0 ) ) );

            addChild( new BondTypeNode(
                    new PNode() {{
                        double centerX = CONTROL_PANEL_INNER_WIDTH / 2;
                        double radius = 5;
                        double spacing = 4;
                        addChild( new PhetPPath( new Ellipse2D.Double( centerX - spacing / 2 - 2 * radius, 0, 2 * radius, 2 * radius ), Color.BLACK ) );
                        addChild( new PhetPPath( new Ellipse2D.Double( centerX + spacing / 2, 0, 2 * radius, 2 * radius ), Color.BLACK ) );
                    }}, 0, "Lone Pair" ) {{
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
        }}, "Non-Bonding" ) {{
            setOffset( 0, bondingPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        addChild( nonBondingPanel );

        /*---------------------------------------------------------------------------*
        * geometry panel
        *----------------------------------------------------------------------------*/
        final InnerControlPanelNode geometryPanel = new InnerControlPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new PhetPPath( new Double( 0, 0, CONTROL_PANEL_INNER_WIDTH, 10 ), new Color( 0, 0, 0, 0 ) ) );

            final PSwing molecularCheckbox = new PSwing( new PropertyCheckBox( "Molecular", MoleculeShapesApplication.showMolecularShapeName ) {{
                setFont( new PhetFont( 12 ) );
            }} ) {{
                setOffset( 10, 10 );
            }};
            addChild( molecularCheckbox );
            PSwing electronCheckbox = new PSwing( new PropertyCheckBox( "Electron", MoleculeShapesApplication.showElectronShapeName ) {{
                setFont( new PhetFont( 12 ) );
            }} ) {{
                setOffset( 10, molecularCheckbox.getFullBounds().getMaxY() + 2 );
            }};
            addChild( electronCheckbox );
        }}, "Geometry Name" ) {{
            setOffset( 0, nonBondingPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        addChild( geometryPanel );
    }

    private static class InnerControlPanelNode extends ControlPanelNode {
        public InnerControlPanelNode( final PNode content, final String title ) {
            super( content, ControlPanelNode.DEFAULT_BACKGROUND_COLOR, new BasicStroke( 1 ), ControlPanelNode.DEFAULT_BORDER_COLOR );

            final ControlPanelNode controlPanelNode = this;

            // title
            background.addChild( 0, new PNode() {{
                PText text = new PText( title ) {{
                    setFont( new PhetFont( 14, false ) );
                }};

                // background to block out border
                addChild( new PhetPPath( padBoundsHorizontally( text.getFullBounds(), 10 ), ControlPanelNode.DEFAULT_BACKGROUND_COLOR ) );
                addChild( text );
                setOffset( ( controlPanelNode.getFullBounds().getWidth() - text.getFullBounds().getWidth() ) / 2,
                           -text.getFullBounds().getHeight() / 2 );
            }} );
        }

        private static PBounds padBoundsHorizontally( PBounds bounds, double amount ) {
            return new PBounds( bounds.x - amount, bounds.y, bounds.width + 2 * amount, bounds.height );
        }
    }

    private class BondTypeNode extends PNode {
        private final int bondOrder;
        protected boolean enabled = true;

        private BondTypeNode( final PNode graphic, int bondOrder, String type ) {
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
            addChild( new PText( type ) {{
                setFont( new PhetFont( 12 ) );
                setOffset( ( CONTROL_PANEL_INNER_WIDTH - getFullBounds().getWidth() ) / 2, BOND_TEXT_SPACER + graphic.getFullBounds().getHeight() );
            }} );

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

    private static class BondLine extends PhetPPath {
        public BondLine( int number ) {
            super( new java.awt.geom.Rectangle2D.Double( ( CONTROL_PANEL_INNER_WIDTH - BOND_WIDTH ) / 2, 0, BOND_WIDTH, BOND_HEIGHT ), Color.BLACK );

            // offset by bond number
            setOffset( 0, number * ( BOND_HEIGHT + BOND_SPACING ) );
        }
    }

}
