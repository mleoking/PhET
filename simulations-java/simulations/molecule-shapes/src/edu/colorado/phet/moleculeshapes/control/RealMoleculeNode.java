// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;

import org.jmol.api.JmolViewer;

import edu.colorado.phet.common.jmolphet.JmolPanel;
import edu.colorado.phet.common.jmolphet.Molecule;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.Kit;
import edu.colorado.phet.common.piccolophet.nodes.kit.KitSelectionNode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Displays a 3D view for molecules that are "real" versions of the currently visible VSEPR model
 */
public class RealMoleculeNode extends PNode {

    private PText labelText( String str ) {
        return new PText( str ) {{
            setFont( new PhetFont( 14 ) );
            setTextPaint( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
        }};
    }

    public RealMoleculeNode() {
        // padding, and make sure we have the width
        addChild( new PhetPPath( new java.awt.geom.Rectangle2D.Double( 0, 0, MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, 10 ), new Color( 0, 0, 0, 0 ) ) );

        addChild( new KitSelectionNode<PSwing>( new Property<Integer>( 0 ),
                                                new Kit<PSwing>( labelText( "Water" ), new PSwing( new JmolPanel( new Molecule() {
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
                                                }, "Loading..." ) {{
                                                    setPreferredSize( new Dimension( (int) MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, (int) MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH ) );
                                                }} ) ),
                                                new Kit<PSwing>( labelText( "Ethane" ), new PSwing( new JmolPanel( new Molecule() {
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
                                                }, "Loading..." ) {{
                                                    setPreferredSize( new Dimension( (int) MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, (int) MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH ) );
                                                }} ) ) ) );

//        addChild( new PSwing( new JmolPanel( new Molecule() {
//            public String getDisplayName() {
//                return "Water";
//            }
//
//            public String getData() {
//                return "<?xml version=\"1.0\"?>\n" +
//                       "<molecule id=\"id962\" xmlns=\"http://www.xml-cml.org/schema\">\n" +
//                       " <name>962</name>\n" +
//                       " <atomArray>\n" +
//                       "  <atom id=\"a1\" elementType=\"O\" x3=\"0.000000\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
//                       "  <atom id=\"a2\" elementType=\"H\" x3=\"0.277400\" y3=\"0.892900\" z3=\"0.254400\"/>\n" +
//                       "  <atom id=\"a3\" elementType=\"H\" x3=\"0.606800\" y3=\"-0.238300\" z3=\"-0.716900\"/>\n" +
//                       " </atomArray>\n" +
//                       " <bondArray>\n" +
//                       "  <bond atomRefs2=\"a1 a2\" order=\"1\"/>\n" +
//                       "  <bond atomRefs2=\"a1 a3\" order=\"1\"/>\n" +
//                       " </bondArray>\n" +
//                       "</molecule>";
//            }
//
//            public void fixJmolColors( JmolViewer viewer ) {
//            }
//        }, "Loading..." ) {{
//            setPreferredSize( new Dimension( (int) MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, (int) MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH ) );
//        }} ) );

//        addChild( new PSwing( new JTabbedPane() {{
//            setBorder( new EmptyBorder( new Insets( 0, 0, 0, 0 ) ) );
//            setBackground( Color.BLACK );
//            setOpaque( false );
//            addTab( "Water", new JmolPanel( new Molecule() {
//                public String getDisplayName() {
//                    return "Water";
//                }
//
//                public String getData() {
//                    return "<?xml version=\"1.0\"?>\n" +
//                           "<molecule id=\"id962\" xmlns=\"http://www.xml-cml.org/schema\">\n" +
//                           " <name>962</name>\n" +
//                           " <atomArray>\n" +
//                           "  <atom id=\"a1\" elementType=\"O\" x3=\"0.000000\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
//                           "  <atom id=\"a2\" elementType=\"H\" x3=\"0.277400\" y3=\"0.892900\" z3=\"0.254400\"/>\n" +
//                           "  <atom id=\"a3\" elementType=\"H\" x3=\"0.606800\" y3=\"-0.238300\" z3=\"-0.716900\"/>\n" +
//                           " </atomArray>\n" +
//                           " <bondArray>\n" +
//                           "  <bond atomRefs2=\"a1 a2\" order=\"1\"/>\n" +
//                           "  <bond atomRefs2=\"a1 a3\" order=\"1\"/>\n" +
//                           " </bondArray>\n" +
//                           "</molecule>";
//                }
//
//                public void fixJmolColors( JmolViewer viewer ) {
//                }
//            }, "Loading..." ) );
//            addTab( "Ethane", new JmolPanel( new Molecule() {
//                public String getDisplayName() {
//                    return "Ethane";
//                }
//
//                public String getData() {
//                    return "<?xml version=\"1.0\"?>\n" +
//                           "<molecule id=\"id6324\" xmlns=\"http://www.xml-cml.org/schema\">\n" +
//                           " <name>6324</name>\n" +
//                           " <atomArray>\n" +
//                           "  <atom id=\"a1\" elementType=\"C\" x3=\"-0.756000\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
//                           "  <atom id=\"a2\" elementType=\"C\" x3=\"0.756000\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
//                           "  <atom id=\"a3\" elementType=\"H\" x3=\"-1.140400\" y3=\"0.658600\" z3=\"0.784500\"/>\n" +
//                           "  <atom id=\"a4\" elementType=\"H\" x3=\"-1.140400\" y3=\"0.350100\" z3=\"-0.962600\"/>\n" +
//                           "  <atom id=\"a5\" elementType=\"H\" x3=\"-1.140500\" y3=\"-1.008700\" z3=\"0.178100\"/>\n" +
//                           "  <atom id=\"a6\" elementType=\"H\" x3=\"1.140400\" y3=\"-0.350100\" z3=\"0.962600\"/>\n" +
//                           "  <atom id=\"a7\" elementType=\"H\" x3=\"1.140500\" y3=\"1.008700\" z3=\"-0.178100\"/>\n" +
//                           "  <atom id=\"a8\" elementType=\"H\" x3=\"1.140400\" y3=\"-0.658600\" z3=\"-0.784500\"/>\n" +
//                           " </atomArray>\n" +
//                           " <bondArray>\n" +
//                           "  <bond atomRefs2=\"a1 a2\" order=\"1\"/>\n" +
//                           "  <bond atomRefs2=\"a1 a3\" order=\"1\"/>\n" +
//                           "  <bond atomRefs2=\"a1 a4\" order=\"1\"/>\n" +
//                           "  <bond atomRefs2=\"a1 a5\" order=\"1\"/>\n" +
//                           "  <bond atomRefs2=\"a2 a6\" order=\"1\"/>\n" +
//                           "  <bond atomRefs2=\"a2 a7\" order=\"1\"/>\n" +
//                           "  <bond atomRefs2=\"a2 a8\" order=\"1\"/>\n" +
//                           " </bondArray>\n" +
//                           "</molecule>";
//                }
//
//                public void fixJmolColors( JmolViewer viewer ) {
//                }
//            }, "Loading..." ) );
//            setPreferredSize( new Dimension( (int) MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, (int) MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH ) );
//        }} ) );
    }
}
