/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * BasicClimateControlPanel is the climate control panel for the "Basic" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BasicClimateControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.INNER_PANEL_BACKGROUND_COLOR;
    private static final Color TITLE_COLOR = GlaciersConstants.INNER_PANEL_TITLE_COLOR;
    private static final Font TITLE_FONT = GlaciersConstants.CONTROL_PANEL_TITLE_FONT;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private SnowfallAndTemperatureControlPanel _snowfallAndTemperatureControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BasicClimateControlPanel( DoubleRange snowfallRange, DoubleRange temperatureRange ) {
        super();
        
        _snowfallAndTemperatureControlPanel = new SnowfallAndTemperatureControlPanel( snowfallRange, temperatureRange );
        
        Border emptyBorder = BorderFactory.createEmptyBorder( 3, 3, 3, 3 );
        TitledBorder titledBorder = new TitledBorder( GlaciersStrings.TITLE_CLIMATE_CONTROLS );
        titledBorder.setTitleFont( TITLE_FONT );
        titledBorder.setTitleColor( TITLE_COLOR );
        titledBorder.setBorder( BorderFactory.createLineBorder( TITLE_COLOR, 1 ) );
        Border compoundBorder = BorderFactory.createCompoundBorder( emptyBorder, titledBorder );
        setBorder( compoundBorder );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _snowfallAndTemperatureControlPanel, row, column++ );
        
        Class[] excludedClasses = { JSpinner.class };
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, excludedClasses, false /* processContentsOfExcludedContainers */ );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public SnowfallAndTemperatureControlPanel getSnowfallAndTemperatureControlPanel() {
        return _snowfallAndTemperatureControlPanel;
    }
}
