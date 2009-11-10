/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.genenetwork.controlpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.genenetwork.GeneNetworkResources;
import edu.colorado.phet.genenetwork.model.IObtainGeneModelElements;
import edu.colorado.phet.genenetwork.module.LacOperonModule;

/**
 * Control panel template.
 */
public class LacOperonControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     * @param parentFrame parent frame, for creating dialogs
     */
    public LacOperonControlPanel( LacOperonModule module, Frame parentFrame, IObtainGeneModelElements model ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = GeneNetworkResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Add the legend panel.
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BorderLayout());
        legendPanel.add(new LacOperonLegendPanelContents(), BorderLayout.WEST);
        Border baseBorder = BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                "Legend",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        legendPanel.setBorder( titledBorder );
        addControlFullWidth(legendPanel);
        
        // Layout
        {
            addResetAllButton( module );
        }
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
