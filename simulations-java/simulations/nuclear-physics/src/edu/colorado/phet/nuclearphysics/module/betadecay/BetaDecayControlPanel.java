/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.betadecay;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.module.alphadecay.NucleusTypeControl;

/**
 * This class represents the control panel that presents the legend and allows
 * the user to control some aspects of the beta decay behavior.
 *
 * @author John Blanco
 */
public class BetaDecayControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BetaDecayLegendPanel _legendPanel;
    private BetaDecayNucleusSelectionPanel _selectionPanel;
    private JCheckBox _labelsVisibleCheckBox;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param piccoloModule
     * @param parentFrame parent frame, for creating dialogs
     * @param betaDecayModel 
     */
    public BetaDecayControlPanel( PiccoloModule piccoloModule, Frame parentFrame, 
    		final NucleusTypeControl betaDecayModel,
    		final LabelVisibilityModel labelVisibilityModel ) {
        
        // Set the control panel's minimum width.
        int minimumWidth = NuclearPhysicsResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panels
        _legendPanel = new BetaDecayLegendPanel();
        _selectionPanel = new BetaDecayNucleusSelectionPanel( betaDecayModel );
        
        // Add the legend panel.
        addControlFullWidth( _legendPanel );
        
        // Add the selection panel.
        addControlFullWidth( _selectionPanel );
        
        // Add the check box for label visibility.
        // TODO: i18n
        _labelsVisibleCheckBox = new JCheckBox("Show Labels", labelVisibilityModel.isVisible());
        _labelsVisibleCheckBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				labelVisibilityModel.setIsVisible(_labelsVisibleCheckBox.isSelected());
			}
		});
        addControlFullWidth(_labelsVisibleCheckBox);
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( piccoloModule );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void closeAllDialogs() {
        //XXX close any dialogs created via the control panel
    }
    
    //----------------------------------------------------------------------------
    // Access to subpanels
    //----------------------------------------------------------------------------
    
}
