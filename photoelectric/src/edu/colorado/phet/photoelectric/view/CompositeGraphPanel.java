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
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;
import edu.colorado.phet.photoelectric.view.util.RotatedTextLabel;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.io.IOException;

/**
 * CompositeGraphPanel
 * <p/>
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
    private BufferedImage snapshotBtnImage;
    private BufferedImage zoomOutImage;
    private BufferedImage zoomInImage;


    public CompositeGraphPanel( Module module ) {
        super( new GridBagLayout() );

        try {
            snapshotBtnImage = ImageLoader.loadBufferedImage( PhotoelectricConfig.SNAPSHOT_BUTTON_IMAGE );
            zoomInImage =ImageLoader.loadBufferedImage( PhotoelectricConfig.ZOOM_IN_BUTTON_IMAGE );
            zoomOutImage =ImageLoader.loadBufferedImage( PhotoelectricConfig.ZOOM_OUT_BUTTON_IMAGE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        PhotoelectricModel model = (PhotoelectricModel)module.getModel();
        controlPanel = module.getControlPanel();
        this.module = module;
        Insets graphInsets = new Insets( 5, 20, 17, 15 );

        GraphPanel currentVsVoltagePanel = new GraphPanel( module.getClock() );
        currentVsVoltagePanel.setGraph( new CurrentVsVoltageGraph( currentVsVoltagePanel, model ), graphInsets );
        addGraph( SimStrings.get( "GraphTitle.CurrentVsVoltage" ),
                  currentVsVoltagePanel,
                  SimStrings.get( "Voltage" ),
                  SimStrings.get( "Current" ) );

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
                  SimStrings.get( "GraphLabel.Frequency" ),
                  SimStrings.get( "GraphLabel.Energy" ) );

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
        final JButton zoomInBtn = new JButton( new ZoomInAction( new ImageIcon( zoomInImage), graphPanel.getGraph() ) );
        final JButton zoomOutBtn = new JButton( new ZoomOutAction( new ImageIcon( zoomOutImage), graphPanel.getGraph() ) );
        final JButton snapshotBtn = new JButton( new SnapshotAction( new ImageIcon( snapshotBtnImage ), this ) );
//        final JButton snapshotBtn = new JButton( new SnapshotAction( new ImageIcon( snapshotBtnImage ), graphPanel ) );
        cb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                graphPanel.setVisible( cb.isSelected() );
                graphPanel.clearGraph();
                yAxisLabel.setVisible( cb.isSelected() );
                xAxisLabel.setVisible( cb.isSelected() );
                zoomInBtn.setVisible( cb.isSelected() );
                zoomOutBtn.setVisible( cb.isSelected() );
                snapshotBtn.setVisible( cb.isSelected() );
                setLogoVisibility( controlPanel );
            }
        } );
        graphPanel.setVisible( cb.isSelected() );
        yAxisLabel.setVisible( cb.isSelected() );
        xAxisLabel.setVisible( cb.isSelected() );
        zoomInBtn.setVisible( cb.isSelected() );
        zoomOutBtn.setVisible( cb.isSelected() );
        snapshotBtn.setVisible( cb.isSelected() );

        // Lay out the panel
        GridBagConstraints checkBoxGbc = new GridBagConstraints( 0, 0,
                                                                 2, 1, 0, 0,
                                                                 GridBagConstraints.NORTHWEST,
                                                                 GridBagConstraints.NONE,
                                                                 new Insets( 0, 0, 0, 25 ), 0, 0 );
        GridBagConstraints graphGbc = new GridBagConstraints( 1, 0,
                                                              1, 3, 0, 0,
                                                              GridBagConstraints.NORTHWEST,
                                                              GridBagConstraints.NONE,
                                                              new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints xLabelGbc = new GridBagConstraints( 1, 0,
                                                               1, 1, 0, 0,
                                                               GridBagConstraints.NORTH,
                                                               GridBagConstraints.NONE,
                                                               new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints yLabelGbc = new GridBagConstraints( 0, 0,
                                                               1, 3, 0, 0,
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
        add( zoomInBtn, zoomBtnGbc );
        rowIdx++;
        zoomBtnGbc.gridy = rowIdx;
        add( zoomOutBtn, zoomBtnGbc );
        rowIdx++;
        zoomBtnGbc.gridy = rowIdx;
        add( snapshotBtn, zoomBtnGbc );

        rowIdx++;
        xLabelGbc.gridy = rowIdx;
        add( xAxisLabel, xLabelGbc );


        rowIdx++;
    }

    private static class ZoomInAction extends AbstractAction {
        private PhotoelectricGraph graph;

        public ZoomInAction( Icon icon, PhotoelectricGraph graph ) {
            super( "", icon );
            this.graph = graph;
        }

        public void actionPerformed( ActionEvent e ) {
            graph.zoomIn();
        }
    }

    private static class ZoomOutAction extends AbstractAction {
        private PhotoelectricGraph graph;

        public ZoomOutAction( Icon icon, PhotoelectricGraph graph ) {
            super( "", icon );
            this.graph = graph;
        }

        public void actionPerformed( ActionEvent e ) {
            graph.zoomOut();
        }
    }

    private static class SnapshotAction extends AbstractAction {
        private Component component;
        Point nextLocation = new Point();
        int subsequentSnapshotOffset = 30;

        public SnapshotAction( Icon icon, Component component ) {
            super( "", icon );
            this.component = component;
        }

        public void actionPerformed( ActionEvent e ) {
            JDialog snapshotDlg = new JDialog( PhetUtilities.getPhetFrame(), false );
            snapshotDlg.setResizable( false );
            Snapshot snapshot = new Snapshot( component );
            snapshotDlg.setContentPane( snapshot );
            snapshotDlg.pack();
            snapshotDlg.setVisible( true );
            snapshotDlg.setLocation( nextLocation );
            nextLocation.setLocation( nextLocation.getX() + subsequentSnapshotOffset,
                                      nextLocation.getY() + subsequentSnapshotOffset );
        }
    }

    private static class Snapshot extends JPanel {
        BufferedImage bi;

        public Snapshot( Component component ) {
            bi = (BufferedImage)component.createImage( component.getWidth(), component.getHeight() );
            Graphics g = bi.getGraphics();
            component.paint( g );
            setSize( bi.getWidth(), bi.getHeight() );
            setPreferredSize( getSize() );
        }

        protected void paintComponent( Graphics g ) {
            super.paintComponent( g );
            g.drawImage( bi, 0, 0, getWidth(), getHeight(), null );
        }
    }
}
