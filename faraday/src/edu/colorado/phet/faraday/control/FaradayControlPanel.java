/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.faraday.FaradayConfig;


/**
 * FaradayControlPanel is the base class for all control panels in the Faraday simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class FaradayControlPanel extends ControlPanel {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param module the module
     */
    public FaradayControlPanel( Module module ) {
        super( module );
        
        JPanel fillerPanel = new JPanel();
        {
            fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
            // WORKAROUND: Filler to set consistent panel width
            fillerPanel.add( Box.createHorizontalStrut( FaradayConfig.CONTROL_PANEL_MIN_WIDTH ) );
        }
        addFullWidth( fillerPanel );
    }
}
