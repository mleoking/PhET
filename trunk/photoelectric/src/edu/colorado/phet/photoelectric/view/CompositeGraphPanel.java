/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;
import edu.colorado.phet.photoelectric.view.util.RotatedTextLabel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * CompositeGraphPanel
 * <p>
 * A JPanel that holds all the individual GraphPanels
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompositeGraphPanel extends JPanel {

    private ControlPanel controlPanel;
    private int rowIdx;
    private ArrayList checkBoxes = new ArrayList();
    private Module module;


    public CompositeGraphPanel( Module module ) {
        super( new GridBagLayout() );
        PhotoelectricModel model = (PhotoelectricModel)module.getModel();
        controlPanel = module.getControlPanel();
        this.module = module;
        Insets graphInsets = new Insets( 5, 20, 17, 15 );

        GraphPanel currentVsVoltagePanel = new GraphPanel( module.getClock() );
        currentVsVoltagePanel.setGraph( new CurrentVsVoltageGraph( currentVsVoltagePanel, model ), graphInsets );
        addGraph( SimStrings.get( "GraphTitle.CurrentVsVoltage" ),
                  currentVsVoltagePanel,
                  SimStrings.get( "Voltage" ),
                  SimStrings.get( "Current" ));

        GraphPanel currentVsIntensityPanel = new GraphPanel( module.getClock() );
        currentVsIntensityPanel.setGraph( new CurrentVsIntensityGraph( currentVsIntensityPanel, model ), graphInsets );
        addGraph( SimStrings.get( "GraphTitle.CurrentVsIntensity" ),
                  currentVsIntensityPanel,
                  SimStrings.get( "GraphLabel.Intensity" ),
                  SimStrings.get( "GraphLabel.Current" ) );

        GraphPanel energyVsFreqPanel = new GraphPanel( module.getClock() );
        energyVsFreqPanel.setGraph( new EnergyVsFrequencyGraph( currentVsIntensityPanel, model ), graphInsets );
        addGraph( SimStrings.get( "GraphTitle.EnergyVsFrequency" ),
                  energyVsFreqPanel,
                  SimStrings.get( "GraphLabel.Frequency"),
                  SimStrings.get( "GraphLabel.Energy" ));

        setBorder( new TitledBorder( "Graphs" ) );
    }

    private void setLogoVisibility( final ControlPanel controlPanel ) {
        boolean isVisible = true;
        for( int i = 0; i < checkBoxes.size(); i++ ) {
            JCheckBox jCheckBox = (JCheckBox)checkBoxes.get( i );
            if( jCheckBox.isSelected() ) {
                isVisible = false;
            }
        }
        module.getLogoPanel().setVisible( isVisible );
    }

    private void addGraph( String title, final GraphPanel graphPanel, String xLabel, String yLabel ) {

        // The checkbox that controls visibility of the graph
        final JCheckBox cb = new JCheckBox( title );
        checkBoxes.add( cb );
        final RotatedTextLabel yAxisLabel = new RotatedTextLabel( yLabel );
        final JLabel xAxisLabel = new JLabel( xLabel );
        final JButton zoomInBtn = new JButton( new ZoomInAction( graphPanel.getGraph() ) );
        final JButton zoomOutBtn = new JButton( new ZoomOutAction( graphPanel.getGraph() ) );
        cb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                graphPanel.setVisible( cb.isSelected() );
                graphPanel.clearGraph();
                yAxisLabel.setVisible( cb.isSelected() );
                xAxisLabel.setVisible( cb.isSelected() );
                zoomInBtn.setVisible( cb.isSelected() );
                zoomOutBtn.setVisible( cb.isSelected() );
                setLogoVisibility( controlPanel );
            }
        } );
        graphPanel.setVisible( cb.isSelected() );
        yAxisLabel.setVisible( cb.isSelected() );
        xAxisLabel.setVisible( cb.isSelected() );
        zoomInBtn.setVisible( cb.isSelected() );
        zoomOutBtn.setVisible( cb.isSelected() );


        // Lay out the panel
        GridBagConstraints checkBoxGbc = new GridBagConstraints( 0, 0,
                                                                 2, 1, 0, 0,
                                                                 GridBagConstraints.NORTHWEST,
                                                                 GridBagConstraints.NONE,
                                                                 new Insets( 0, 0, 0, 25 ), 0, 0 );
        GridBagConstraints graphGbc = new GridBagConstraints( 1, 0,
                                                              1, 2, 0, 0,
                                                              GridBagConstraints.NORTHWEST,
                                                              GridBagConstraints.NONE,
                                                              new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints xLabelGbc = new GridBagConstraints( 1, 0,
                                                               1, 1, 0, 0,
                                                               GridBagConstraints.NORTH,
                                                               GridBagConstraints.NONE,
                                                               new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints yLabelGbc = new GridBagConstraints( 0, 0,
                                                               1, 2, 0, 0,
                                                               GridBagConstraints.WEST,
                                                               GridBagConstraints.NONE,
                                                               new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints zoomBtnGbc = new GridBagConstraints( 2, 0,
                                                               1, 1, 0, 0,
                                                               GridBagConstraints.NORTH,
                                                               GridBagConstraints.NONE,
                                                               new Insets( 0, 0, 0, 0 ), 0, 0 );

        checkBoxGbc.gridy = rowIdx;
        add( cb, checkBoxGbc );

        rowIdx++;
        graphGbc.gridy = rowIdx;
        yLabelGbc.gridy = rowIdx;
        add( yAxisLabel, yLabelGbc );
        add( graphPanel, graphGbc );

        zoomBtnGbc.gridy = rowIdx;
        add( zoomInBtn,  zoomBtnGbc );
        rowIdx++;
        zoomBtnGbc.gridy = rowIdx;
        add( zoomOutBtn,  zoomBtnGbc );

        rowIdx++;
        xLabelGbc.gridy = rowIdx;
        add( xAxisLabel, xLabelGbc );


        rowIdx++;
    }

    private static class ZoomInAction extends AbstractAction {
        private PhotoelectricGraph graph;

        public ZoomInAction( PhotoelectricGraph graph ) {
            super( "+" );
            this.graph = graph;
        }

        public void actionPerformed( ActionEvent e ) {
            graph.zoomIn();
        }
    }

    private static class ZoomOutAction extends AbstractAction {
        private PhotoelectricGraph graph;

        public ZoomOutAction( PhotoelectricGraph graph ) {
            super( "-" );
            this.graph = graph;
        }

        public void actionPerformed( ActionEvent e ) {
            graph.zoomOut();
        }
    }
}
