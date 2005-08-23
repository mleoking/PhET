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
    private CurrentVsVoltageGraph2 currentVsVoltageGraph;
    private CurrentVsIntensityGraph currentVsIntensityGraph;
    private AbstractClock clock;
    private PhotoelectricModel model;

    public GraphWindow( Frame frame, Component component, AbstractClock clock, PhotoelectricModel model ) {
        super( frame, false );
        this.clock = clock;
        this.model = model;
        getContentPane().setLayout( new GridLayout( 2,1 ));
        getContentPane().add( createCurrentVsVoltagePanel() );
        getContentPane().add( createCurrentVsIntensityPanel() );
        pack();
    }

    public CurrentVsVoltageGraph2 getCurrentVsVoltageGraph() {
        return currentVsVoltageGraph;
    }

    private JPanel createCurrentVsVoltagePanel() {

        // Make the panel with the graph
        ApparatusPanel2 graphPanel = new ApparatusPanel2( clock );
        graphPanel.setUseOffscreenBuffer( true );
        graphPanel.setDisplayBorder( false );
        currentVsVoltageGraph = new CurrentVsVoltageGraph2( graphPanel );
        graphPanel.setPreferredSize( new Dimension( 300, 200 ) );
        graphPanel.setSize( new Dimension( 300, 200 ) );
        currentVsVoltageGraph.setLocation( (int)( graphPanel.getPreferredSize().getWidth() - currentVsVoltageGraph.getWidth()) / 2,
                                           (int)( graphPanel.getPreferredSize().getHeight() - currentVsVoltageGraph.getHeight()) / 2);
        graphPanel.addGraphic( currentVsVoltageGraph );

        // Lay out a panel with titles for the graph
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0,0,0,0),0,0 );
        JPanel panel = new JPanel( new GridBagLayout( ));

        gbc.gridx = 0;
        panel.add( new RotatedTextLabel( "Current" ), gbc );
        gbc.gridx = 1;
        panel.add( graphPanel, gbc );
        gbc.gridy++;
        panel.add( new JLabel( SimStrings.get( "Voltage" ) ), gbc );
        return panel;
    }

    
    private JPanel createCurrentVsIntensityPanel() {

        // Make the panel with the graph
        ApparatusPanel2 graphPanel = new ApparatusPanel2( clock );
        graphPanel.setUseOffscreenBuffer( true );
        graphPanel.setDisplayBorder( false );
        currentVsIntensityGraph= new CurrentVsIntensityGraph( graphPanel, model );
        graphPanel.setPreferredSize( new Dimension( 300, 200 ) );
        graphPanel.setSize( new Dimension( 300, 200 ) );
        currentVsIntensityGraph.setLocation( (int)( graphPanel.getPreferredSize().getWidth() - currentVsVoltageGraph.getWidth()) / 2,
                                           (int)( graphPanel.getPreferredSize().getHeight() - currentVsVoltageGraph.getHeight()) / 2);
        graphPanel.addGraphic( currentVsIntensityGraph );

        // Lay out a panel with titles for the graph
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0,0,0,0),0,0 );
        JPanel panel = new JPanel( new GridBagLayout( ));

        gbc.gridx = 0;
        panel.add( new RotatedTextLabel( "Current" ), gbc );
        gbc.gridx = 1;
        panel.add( graphPanel, gbc );
        gbc.gridy++;
        panel.add( new JLabel( SimStrings.get( "Intensity" ) ), gbc );
        return panel;
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
            setPreferredSize( new Dimension( 20, 150 ) );
        }

        public void paint( Graphics g ) {
            Graphics2D g2 = (Graphics2D)g;
            GraphicsState gs = new GraphicsState( g2 );
            JLabel dummyLabel = new JLabel();
            Font font = dummyLabel.getFont();
            int x = 20;
            int y = 150;
            AffineTransform at = new AffineTransform();
            at.setToRotation( -Math.PI / 2.0, x, y );
            g2.transform( at );
            g2.setFont( font );
            g2.drawString( label, x, y );
            gs.restoreGraphics();
        }
    }
}
