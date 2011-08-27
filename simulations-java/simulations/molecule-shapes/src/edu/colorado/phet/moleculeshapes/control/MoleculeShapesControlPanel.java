// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D.Double;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.jmol.api.JmolViewer;

import edu.colorado.phet.common.jmolphet.JmolPanel;
import edu.colorado.phet.common.jmolphet.Molecule;
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
import edu.umd.cs.piccolox.pswing.PSwing;

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
        final MoleculeShapesPanelNode jmolPanel = new MoleculeShapesPanelNode( new PNode() {{
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
        }}, "Real Molecules" ) {{
            setOffset( 0, nonBondingPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        addChild( jmolPanel );
    }

}
