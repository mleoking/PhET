// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.module.realmolecules;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesPanelNode;
import edu.colorado.phet.moleculeshapes.control.OptionsNode;
import edu.colorado.phet.moleculeshapes.control.PropertyCheckBoxNode;
import edu.colorado.phet.moleculeshapes.control.RealMoleculePanelNode;
import edu.colorado.phet.moleculeshapes.control.TitledControlPanelNode.TitleNode;
import edu.colorado.phet.moleculeshapes.model.RealMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

public class RealMoleculesControlPanel extends PNode {
    private static final double PANEL_SPACER = 20; // space between text and bond lines

    private RealMoleculePanelNode realMoleculeNode;

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

    public RealMoleculesControlPanel( final RealMoleculesModule module, final Function0<Double> getControlPanelXPosition ) {

        // put it on 0 vertically
        setOffset( 0, 10 );

        final PNode moleculePanel = new PSwing( new JComboBox( RealMolecule.TAB_2_MOLECULES ) {
            {
                addActionListener( new java.awt.event.ActionListener() {
                    public void actionPerformed( final ActionEvent e ) {
                        JMEUtils.invoke( new Runnable() {
                            public void run() {
                                RealMolecule selectedRealMolecule = (RealMolecule) ( (JComboBox) e.getSource() ).getSelectedItem();
                                module.switchToMolecule( selectedRealMolecule );
                            }
                        } );
                    }
                } );
            }

            @Override public Point getLocationOnScreen() {
//                Point screenLocation = getSimulationPanel().getLocationOnScreen();
                Point screenLocation = PhetApplication.getInstance().getModule( 0 ).getSimulationPanel().getLocationOnScreen();
//                return new Point( screenLocation.x, screenLocation.y );
//                return new Point( 0, 0 );
                return new Point( screenLocation.x + getControlPanelXPosition.apply().intValue(), screenLocation.y + 10 );
//                return new Point( screenLocation.x + (int) controlPanel.position.get().getX(), screenLocation.y );
            }
        } );
        addChild( moleculePanel );

        /*---------------------------------------------------------------------------*
        * options
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode optionsPanel = new MoleculeShapesPanelNode( new OptionsNode( module ), Strings.CONTROL__OPTIONS );
        optionsPanel.setOffset( 0, moleculePanel.getFullBounds().getMaxY() + 10 );
        addChild( optionsPanel );
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
