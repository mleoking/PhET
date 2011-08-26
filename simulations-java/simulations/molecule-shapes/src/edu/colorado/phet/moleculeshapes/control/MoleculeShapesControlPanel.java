// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D.Double;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.jmol.api.JmolViewer;

import edu.colorado.phet.common.jmolphet.JmolPanel;
import edu.colorado.phet.common.jmolphet.Molecule;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
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

        /*---------------------------------------------------------------------------*
        * experimental Jmol panel
        *----------------------------------------------------------------------------*/
        final TitledControlPanelNode jmolPanel = new TitledControlPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new PhetPPath( new Double( 0, 0, MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, 10 ), new Color( 0, 0, 0, 0 ) ) );

            addChild( new PSwing( new JTabbedPane() {{
                setBorder( new EmptyBorder( new Insets( 0, 0, 0, 0 ) ) );
                setBackground( Color.BLACK );
                addTab( "Water", new JmolPanel( new Molecule() {
                    public String getDisplayName() {
                        return "Water";
                    }

                    public String getData() {
                        return "<?xml version=\"1.0\"?>\n" +
                               "<molecule id=\"id962\" xmlns=\"http://www.xml-cml.org/schema\">\n" +
                               " <name>962</name>\n" +
                               " <atomArray>\n" +
                               "  <atom id=\"a1\" elementType=\"O\" x3=\"0.000000\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
                               "  <atom id=\"a2\" elementType=\"H\" x3=\"0.277400\" y3=\"0.892900\" z3=\"0.254400\"/>\n" +
                               "  <atom id=\"a3\" elementType=\"H\" x3=\"0.606800\" y3=\"-0.238300\" z3=\"-0.716900\"/>\n" +
                               " </atomArray>\n" +
                               " <bondArray>\n" +
                               "  <bond atomRefs2=\"a1 a2\" order=\"1\"/>\n" +
                               "  <bond atomRefs2=\"a1 a3\" order=\"1\"/>\n" +
                               " </bondArray>\n" +
                               "</molecule>";
                    }

                    public void fixJmolColors( JmolViewer viewer ) {
                    }
                }, "Loading..." ) );
                addTab( "Ethane", new JmolPanel( new Molecule() {
                    public String getDisplayName() {
                        return "Ethane";
                    }

                    public String getData() {
                        return "<?xml version=\"1.0\"?>\n" +
                               "<molecule id=\"id6324\" xmlns=\"http://www.xml-cml.org/schema\">\n" +
                               " <name>6324</name>\n" +
                               " <atomArray>\n" +
                               "  <atom id=\"a1\" elementType=\"C\" x3=\"-0.756000\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
                               "  <atom id=\"a2\" elementType=\"C\" x3=\"0.756000\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
                               "  <atom id=\"a3\" elementType=\"H\" x3=\"-1.140400\" y3=\"0.658600\" z3=\"0.784500\"/>\n" +
                               "  <atom id=\"a4\" elementType=\"H\" x3=\"-1.140400\" y3=\"0.350100\" z3=\"-0.962600\"/>\n" +
                               "  <atom id=\"a5\" elementType=\"H\" x3=\"-1.140500\" y3=\"-1.008700\" z3=\"0.178100\"/>\n" +
                               "  <atom id=\"a6\" elementType=\"H\" x3=\"1.140400\" y3=\"-0.350100\" z3=\"0.962600\"/>\n" +
                               "  <atom id=\"a7\" elementType=\"H\" x3=\"1.140500\" y3=\"1.008700\" z3=\"-0.178100\"/>\n" +
                               "  <atom id=\"a8\" elementType=\"H\" x3=\"1.140400\" y3=\"-0.658600\" z3=\"-0.784500\"/>\n" +
                               " </atomArray>\n" +
                               " <bondArray>\n" +
                               "  <bond atomRefs2=\"a1 a2\" order=\"1\"/>\n" +
                               "  <bond atomRefs2=\"a1 a3\" order=\"1\"/>\n" +
                               "  <bond atomRefs2=\"a1 a4\" order=\"1\"/>\n" +
                               "  <bond atomRefs2=\"a1 a5\" order=\"1\"/>\n" +
                               "  <bond atomRefs2=\"a2 a6\" order=\"1\"/>\n" +
                               "  <bond atomRefs2=\"a2 a7\" order=\"1\"/>\n" +
                               "  <bond atomRefs2=\"a2 a8\" order=\"1\"/>\n" +
                               " </bondArray>\n" +
                               "</molecule>";
                    }

                    public void fixJmolColors( JmolViewer viewer ) {
                    }
                }, "Loading..." ) );
                setPreferredSize( new Dimension( (int) MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, (int) MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH ) );
            }} ) );
        }}, "Real Molecules", Color.BLACK, new BasicStroke( MoleculeShapesConstants.CONTROL_PANEL_BORDER_WIDTH ), MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR ) {{
            setOffset( 0, nonBondingPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        addChild( jmolPanel );
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
