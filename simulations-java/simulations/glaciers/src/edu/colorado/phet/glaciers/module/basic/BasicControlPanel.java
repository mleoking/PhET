/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.module.basic;

import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.control.BasicClimateControlPanel;
import edu.colorado.phet.glaciers.control.GlaciersClockControlPanel;
import edu.colorado.phet.glaciers.control.MiscControlPanel;
import edu.colorado.phet.glaciers.control.ViewControlPanel;
import edu.colorado.phet.glaciers.control.BasicClimateControlPanel.BasicClimateControlPanelListener;
import edu.colorado.phet.glaciers.defaults.BasicDefaults;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;

/**
 * BasicControlPanel is the control panel for BasicModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BasicControlPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.CONTROL_PANEL_BACKGROUND_COLOR;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BasicModel _model;
    
    private ViewControlPanel _viewControlPanel;
    private BasicClimateControlPanel _climateControlPanel;
    private GlaciersClockControlPanel _clockControlPanel;
    private MiscControlPanel _miscControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BasicControlPanel( BasicModel model ) {
        super();
        
        _model = model;
        
        _viewControlPanel = new ViewControlPanel();
        _climateControlPanel = new BasicClimateControlPanel( BasicDefaults.SNOWFALL_RANGE, BasicDefaults.TEMPERATURE_RANGE );
        _clockControlPanel = new GlaciersClockControlPanel( model.getClock() );
        _miscControlPanel = new MiscControlPanel();
        
        int row;
        int column;
        
        JPanel topPanel = new JPanel();
        EasyGridBagLayout topLayout = new EasyGridBagLayout( topPanel );
        topPanel.setLayout( topLayout  );
        row = 0;
        column = 0;
        topLayout.addFilledComponent( _viewControlPanel, row, column++, GridBagConstraints.VERTICAL );
        topLayout.addFilledComponent( _climateControlPanel, row, column++, GridBagConstraints.VERTICAL  );
        
        JPanel bottomPanel = new JPanel();
        EasyGridBagLayout bottomLayout = new EasyGridBagLayout( bottomPanel );
        bottomPanel.setLayout( bottomLayout );
        row = 0;
        column = 0;
        bottomLayout.addAnchoredComponent( _clockControlPanel, row, column++, GridBagConstraints.WEST );
        bottomLayout.addComponent( Box.createHorizontalStrut( 10 ), row, column++ );
        bottomLayout.addFilledComponent( new JSeparator( SwingConstants.VERTICAL ), row, column++, GridBagConstraints.VERTICAL );
        bottomLayout.addComponent( Box.createHorizontalStrut( 10 ), row, column++ );
        bottomLayout.addAnchoredComponent( _miscControlPanel, row, column++, GridBagConstraints.EAST );
        
        EasyGridBagLayout thisLayout = new EasyGridBagLayout( this );
        setLayout( thisLayout );
        row = 0;
        column = 0;
        thisLayout.addComponent( topPanel, row++, column );
        thisLayout.addComponent( bottomPanel, row++, column );
        
        Class[] excludedClasses = { ViewControlPanel.class, BasicClimateControlPanel.class, JTextComponent.class };
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, excludedClasses, false /* processContentsOfExcludedContainers */ );
        
        // Initialization
        _climateControlPanel.setSnowfall( _model.getClimate().getReferencePrecipition() );
        _climateControlPanel.setTemperture( _model.getClimate().getReferenceTemperature() );
        
        // Listeners
        _climateControlPanel.addBasicClimateControlPanelListener( new BasicClimateControlPanelListener() {

            public void snowfallChanged( double snowfall ) {
                _model.getClimate().setReferencePrecipitation( snowfall );
            }

            public void temperatureChanged( double temperature ) {
                _model.getClimate().setReferenceTemperature( temperature );
            }
        });
        _model.getClimate().addClimateListener( new ClimateListener() {

            public void referencePrecipitationChanged() {
                _climateControlPanel.setSnowfall( _model.getClimate().getReferencePrecipition() );
            }

            public void referenceTemperatureChanged() {
                _climateControlPanel.setTemperture( _model.getClimate().getReferenceTemperature() );
            }
        } );
    }
}
