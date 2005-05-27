/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.coreadditions.ToggleButton;
import edu.colorado.phet.idealgas.model.Pump;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * ToolPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ToolPanel extends JPanel {

    JPanel toolsPanel = new JPanel( new GridBagLayout() );
    private JComponent button;
    private Insets optionInsets = new Insets( 0, 10, 0, 10 );
    private Insets buttonInsets = new Insets( 10, 0, 0, 0 );

    private GridBagConstraints topLevelGbc = new GridBagConstraints( 0, 0,
                                                                     1, 1, 1, 1,
                                                                     GridBagConstraints.CENTER,
                                                                     GridBagConstraints.NONE,
                                                                     new Insets( 0, 0, 0, 0 ), 0, 0 );
    private GridBagConstraints toolsPanelInternalGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                                               1, 1, 1, 1,
                                                                               GridBagConstraints.NORTHWEST,
                                                                               GridBagConstraints.NONE,
                                                                               optionInsets, 0, 0 );
    private GridBagConstraints advToolsPanelInternalGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                                                  1, 1, 1, 1,
                                                                                  GridBagConstraints.NORTHWEST,
                                                                                  GridBagConstraints.NONE,
                                                                                  new Insets( 0, 10, 0, 10 ), 0, 0 );
    private JPanel advToolPanel;

    /**
     * @param module
     */
    public ToolPanel( final IdealGasModule module ) {
        setLayout( new GridBagLayout() );

        setBorder( new TitledBorder( SimStrings.get( "IdealGasControlPanel.Tools_and_options" ) ) );

        button = new ToggleButton( SimStrings.get( "IdealGasControlPanel.Measurement_Tools_on" ),
                                   SimStrings.get( "IdealGasControlPanel.Measurement_Tools_off" ) ) {
            public void onAction() {
                toolsPanel.setVisible( true );
//                ((ControlPanel)module.getControlPanel()).resizeControlPane();
                module.getControlPanel().revalidate();
            }

            public void offAction() {
                toolsPanel.setVisible( false );
//                ((ControlPanel)module.getControlPanel()).resizeControlPane();
                module.getControlPanel().revalidate();
            }
        };
        topLevelGbc.fill = GridBagConstraints.NONE;
        topLevelGbc.anchor = GridBagConstraints.CENTER;
        topLevelGbc.insets = buttonInsets;
        add( button, topLevelGbc );

        toolsPanel.add( new MeasurementTools.PressureSliceControl( module ), toolsPanelInternalGbc );
        toolsPanel.add( new MeasurementTools.RulerControl( module ), toolsPanelInternalGbc );
        toolsPanel.add( new MeasurementTools.SpeciesMonitorControl( module ), toolsPanelInternalGbc );
        toolsPanel.add( new MeasurementTools.StopwatchControl( module ), toolsPanelInternalGbc );
        toolsPanel.add( new MeasurementTools.HistogramControlPanel( module ), advToolsPanelInternalGbc );
        toolsPanel.add( new MeasurementTools.CmLinesControl( module ), advToolsPanelInternalGbc );
        toolsPanel.setBorder( BorderFactory.createEtchedBorder() );
        toolsPanel.setVisible( false );
        topLevelGbc.insets = optionInsets;
        topLevelGbc.gridy++;
        topLevelGbc.fill = GridBagConstraints.HORIZONTAL;
        add( toolsPanel, topLevelGbc );

        ToggleButton advButton = new ToggleButton( SimStrings.get( "IdealGasControlPanel.MoreOptions" ),
                                                   SimStrings.get( "IdealGasControlPanel.FewerOptions" ) ) {
            public void onAction() {
                advToolPanel.setVisible( true );
                module.getControlPanel().revalidate();
            }

            public void offAction() {
                advToolPanel.setVisible( false );
                module.getControlPanel().revalidate();
            }
        };

        topLevelGbc.fill = GridBagConstraints.NONE;
        topLevelGbc.anchor = GridBagConstraints.CENTER;
        topLevelGbc.insets = buttonInsets;
        topLevelGbc.gridy++;
        add( advButton, topLevelGbc );

        advToolPanel = new JPanel( new GridBagLayout() );
        advToolPanel.add( new MeasurementTools.ParticleInteractionControl( module.getIdealGasModel() ), advToolsPanelInternalGbc );
        Pump[] pumps = new Pump[]{module.getPump()};
        advToolPanel.add( new InputTemperatureControlPanel( module, pumps ), advToolsPanelInternalGbc );
        advToolPanel.setBorder( BorderFactory.createEtchedBorder() );

        advToolPanel.setVisible( false );
        topLevelGbc.insets = optionInsets;
        topLevelGbc.anchor = GridBagConstraints.NORTHWEST;
        topLevelGbc.fill = GridBagConstraints.HORIZONTAL;
        topLevelGbc.gridy++;
        add( advToolPanel, topLevelGbc );


    }
}
