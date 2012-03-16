// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.SettableNot;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.common.view.WhiteControlPanelNode;
import edu.colorado.phet.sugarandsaltsolutions.water.dev.DeveloperControlDialog;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.*;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.BeakerAndShakerCanvas.CONTROL_FONT;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.BeakerAndShakerCanvas.TITLE_FONT;

/**
 * Control panel for user options in the water tab
 *
 * @author Sam Reid
 */
public class WaterControlPanel extends WhiteControlPanelNode {

    public WaterControlPanel( final WaterModel waterModel, final GlobalState state, final WaterCanvas waterCanvas, final Sucrose3DDialog sucrose3DDialog ) {
        super( new VBox(

                //Show the title "Show"
                new PhetPText( SHOW, TITLE_FONT ),

                //Add checkboxes for showing partial charges or sugar atoms
                //Put the checkboxes together in one VerticalLayoutPanel so they will be left-aligned
                new PSwing( new VerticalLayoutPanel() {{

                    //Checkbox to show/hide water charges (showing partial charges)
                    add( new PropertyCheckBox( WATER_PARTIAL_CHARGES, waterModel.showWaterCharges ) {{
                        setFont( CONTROL_FONT );
                    }} );

                    //Allow the user to show individual atoms within the sugar molecule, but only if a sugar molecule is in the scene
                    //Works for both the sugar in the bucket and any in the model
                    add( new PropertyCheckBox( SUGAR_HIGHLIGHT, new SettableNot( waterModel.showSugarAtoms ) ) {{
                        setFont( CONTROL_FONT );
                    }} );
                }} ),

                //If development version, show button to launch developer controls
                state.config.isDev() ? new TextButtonNode( "Developer Controls" ) {{
                    addActionListener( new ActionListener() {
                        DeveloperControlDialog dialog = null;

                        public void actionPerformed( ActionEvent e ) {
                            if ( dialog == null ) {
                                dialog = new DeveloperControlDialog( SwingUtilities.getWindowAncestor( waterCanvas ), waterModel );
                                SwingUtils.centerInParent( dialog );
                            }
                            dialog.setVisible( true );
                        }
                    } );
                }} : new PNode(),

                //Add a button that allows the user to show the 3D water molecule
                new TextButtonNode( SUGAR_IN_3_D, CONTROL_FONT, Color.yellow ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            sucrose3DDialog.showDialog();
                        }
                    } );
                }}
        ) );
    }
}