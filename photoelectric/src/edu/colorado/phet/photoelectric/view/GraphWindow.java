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

import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;
import edu.colorado.phet.chart.Chart;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * GraphWindow
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GraphWindow extends JDialog {

    private static final int graphInsetX = 40;
    private static final int graphInsetY = 20;
    private static Font myFont;

    private CurrentVsVoltageGraph2 currentVsVoltageGraph;
    private CurrentVsIntensityGraph currentVsIntensityGraph;
    private EnergyVsFrequencyGraph energyVsFrequencyGraph;
    private AbstractClock clock;
    private PhotoelectricModel model;
    private JPanel currentVsVoltagePanel;
    private JPanel currentVsIntensityPanel;
    private JPanel energyVsFrequencyPanel;

    //----------------------------------------------------------------
    // Constructors and initialization
    //----------------------------------------------------------------

    public GraphWindow( Frame frame, AbstractClock clock, PhotoelectricModel model ) {
        super( frame, false );

        // Set up the defaultFont we want
        JLabel dummyLabel = new JLabel();
        Font defaultFont = dummyLabel.getFont();
        myFont = new Font( defaultFont.getFontName(), defaultFont.getStyle(), defaultFont.getSize() + 4 );

        setUndecorated( true );
        getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );

        this.clock = clock;
        this.model = model;
        getContentPane().setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        getContentPane().add( createCurrentVsVoltagePanel(), gbc );
        getContentPane().add( createCurrentVsIntensityPanel(), gbc );
        getContentPane().add( createEnergyVsFrequencyPanel(), gbc );
        pack();
    }

    /**
     * @return
     */
    private JPanel createCurrentVsVoltagePanel() {

        // Make the currentVsVoltagePanel with the graph
        ApparatusPanel2 graphPanel = new ApparatusPanel2( clock );
        graphPanel.setUseOffscreenBuffer( true );
        graphPanel.setDisplayBorder( false );
        currentVsVoltageGraph = new CurrentVsVoltageGraph2( graphPanel, model );
        graphPanel.setPreferredSize( new Dimension( 300, 200 ) );
        graphPanel.setSize( new Dimension( 300, 200 ) );
        currentVsVoltageGraph.setLocation( graphInsetX, graphInsetY );
        graphPanel.addGraphic( currentVsVoltageGraph );

        currentVsVoltagePanel = makeGraphPanel( graphPanel, "Battery Voltage", "Current" );
        return currentVsVoltagePanel;
    }

    /**
     * @return
     */
    private JPanel createCurrentVsIntensityPanel() {

        // Make the currentVsIntensityPanel with the graph
        ApparatusPanel2 graphPanel = new ApparatusPanel2( clock );
        graphPanel.setUseOffscreenBuffer( true );
        graphPanel.setDisplayBorder( false );
        currentVsIntensityGraph = new CurrentVsIntensityGraph( graphPanel, model );
        graphPanel.setPreferredSize( new Dimension( 300, 200 ) );
        graphPanel.setSize( new Dimension( 300, 200 ) );
        currentVsIntensityGraph.setLocation( graphInsetX, graphInsetY );
        graphPanel.addGraphic( currentVsIntensityGraph );

        currentVsIntensityPanel = makeGraphPanel( graphPanel, "Intensity", "Current" );
        return currentVsIntensityPanel;
    }

    /**
     * @return
     */
    private JPanel createEnergyVsFrequencyPanel() {

        // Make the currentVsIntensityPanel with the graph
        ApparatusPanel2 graphPanel = new ApparatusPanel2( clock );
        graphPanel.setUseOffscreenBuffer( true );
        graphPanel.setDisplayBorder( false );
        energyVsFrequencyGraph = new EnergyVsFrequencyGraph( graphPanel, model );
        graphPanel.setPreferredSize( new Dimension( 300, 200 ) );
        graphPanel.setSize( new Dimension( 300, 200 ) );
        energyVsFrequencyGraph.setLocation( graphInsetX, graphInsetY );
        graphPanel.addGraphic( energyVsFrequencyGraph );

        energyVsFrequencyPanel = makeGraphPanel( graphPanel, "Frequency", "Electron Energy" );
        return energyVsFrequencyPanel;
    }

    /**
     *
     * @param graphPanel
     * @param xAxisLabel
     * @param yAxisLabel
     * @return
     */
    private JPanel makeGraphPanel( JPanel graphPanel, String xAxisLabel, String yAxisLabel ) {
        // Lay out a panel with titles for the graph
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        JPanel panel = new JPanel( new GridBagLayout() );

        gbc.gridx = 0;
        panel.add( new RotatedTextLabel( yAxisLabel ), gbc );
        gbc.gridx = 1;
        panel.add( graphPanel, gbc );
        gbc.gridy++;
        JLabel label = new JLabel( xAxisLabel );
        label.setFont( myFont );
        panel.add( label, gbc );
        panel.setVisible( false );
        return panel;

    }

    //----------------------------------------------------------------
    // Setters for visibility of graphs
    //----------------------------------------------------------------

    public void setCurrentVsVoltageVisible( boolean isVisible ) {
        currentVsVoltagePanel.setVisible( isVisible );
        pack();
    }

    public void setCurrentVsIntensityVisible( boolean isVisible ) {
        currentVsIntensityPanel.setVisible( isVisible );
        pack();
    }

    public void setEnergyVsFrequency( boolean isVisible ) {
        energyVsFrequencyPanel.setVisible( isVisible );
        pack();
    }

    //----------------------------------------------------------------
    // Misc inner classes
    //----------------------------------------------------------------

    /**
     * Class for labels of the y axes
     */
    private static class RotatedTextLabel extends JPanel {
        private String label;

        public RotatedTextLabel( String label ) {
            super( null );
            this.label = "   " + label;
            setPreferredSize( new Dimension( 25, 150 ) );
        }

        public void paint( Graphics g ) {
            Graphics2D g2 = (Graphics2D)g;
            GraphicsState gs = new GraphicsState( g2 );
            JLabel dummyLabel = new JLabel();
            Font font = dummyLabel.getFont();
            Font f = new Font( font.getFontName(), font.getStyle(), font.getSize() + 2 );
            int x = 20;
            int y = 150;
            AffineTransform at = new AffineTransform();
            at.setToRotation( -Math.PI / 2.0, x, y );
            g2.transform( at );
            g2.setFont( myFont );
//            g2.setFont( font );
            g2.drawString( label, x, y );
            gs.restoreGraphics();
        }
    }
}
