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
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;
import edu.colorado.phet.photoelectric.model.util.PhotoelectricModelUtil;
import edu.colorado.phet.photoelectric.view.util.RotatedTextLabel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

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
    private ArrayList graphPanels = new ArrayList();
    private JButton snapshotBtn;


    public CompositeGraphPanel( Module module ) {
        super( new GridBagLayout() );

        try {
            snapshotBtnImage = ImageLoader.loadBufferedImage( PhotoelectricConfig.SNAPSHOT_BUTTON_IMAGE );
            zoomInImage = ImageLoader.loadBufferedImage( PhotoelectricConfig.ZOOM_IN_BUTTON_IMAGE );
            zoomOutImage = ImageLoader.loadBufferedImage( PhotoelectricConfig.ZOOM_OUT_BUTTON_IMAGE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        PhotoelectricModel model = (PhotoelectricModel)module.getModel();
        controlPanel = module.getControlPanel();
        this.module = module;
        Insets graphInsets = new Insets( 5, 20, 17, 15 );

        // The button that takes a snapshop of the graphs
        snapshotBtn = new JButton( new SnapshotAction( new ImageIcon( snapshotBtnImage ), this ) );
        GridBagConstraints gbc = new GridBagConstraints( 2, 0,
                                                         1, 1, 0, 0,
                                                         GridBagConstraints.NORTHWEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        add( snapshotBtn, gbc );

        // Add the graph panels themselves
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
        final JButton zoomInBtn = new JButton( new ZoomInAction( new ImageIcon( zoomInImage ), graphPanel.getGraph() ) );
        final JButton zoomOutBtn = new JButton( new ZoomOutAction( new ImageIcon( zoomOutImage ), graphPanel.getGraph() ) );
        cb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                graphPanel.setVisible( cb.isSelected() );
                graphPanel.clearGraph();
                yAxisLabel.setVisible( cb.isSelected() );
                xAxisLabel.setVisible( cb.isSelected() );
                zoomInBtn.setVisible( cb.isSelected() );
                zoomOutBtn.setVisible( cb.isSelected() );
                setLogoVisibility( controlPanel );
                setSnapshotButtonVisibility( checkBoxes );
            }
        } );
        graphPanel.setVisible( cb.isSelected() );
        yAxisLabel.setVisible( cb.isSelected() );
        xAxisLabel.setVisible( cb.isSelected() );
        zoomInBtn.setVisible( cb.isSelected() );
        zoomOutBtn.setVisible( cb.isSelected() );
        setLogoVisibility( controlPanel );
        setSnapshotButtonVisibility( checkBoxes );

        // Lay out the panel
        GridBagConstraints checkBoxGbc = new GridBagConstraints( 0, rowIdx,
                                                                 2, 1, 0, 0,
                                                                 GridBagConstraints.NORTHWEST,
                                                                 GridBagConstraints.NONE,
                                                                 new Insets( 0, 0, 0, 25 ), 0, 0 );
        GridBagConstraints graphGbc = new GridBagConstraints( 1, rowIdx + 1,
                                                              1, 1, 0, 0,
                                                              GridBagConstraints.NORTHWEST,
                                                              GridBagConstraints.NONE,
                                                              new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints xLabelGbc = new GridBagConstraints( 1, rowIdx + 2,
                                                               1, 1, 0, 0,
                                                               GridBagConstraints.NORTH,
                                                               GridBagConstraints.NONE,
                                                               new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints yLabelGbc = new GridBagConstraints( 0, rowIdx + 1,
                                                               1, 1, 0, 0,
                                                               GridBagConstraints.WEST,
                                                               GridBagConstraints.NONE,
                                                               new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints zoomBtnGbc = new GridBagConstraints( 2, rowIdx + 1,
                                                                1, 1, 0, 0,
                                                                GridBagConstraints.NORTH,
                                                                GridBagConstraints.VERTICAL,
                                                                new Insets( 0, 0, 0, 0 ), 0, 0 );

        // The checkbox
        add( cb, checkBoxGbc );

        // The y axis label
        add( yAxisLabel, yLabelGbc );

        // The graph itself
        add( graphPanel, graphGbc );

        // The zoom buttons
        {
            JPanel btnPnl = new JPanel( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.SOUTH,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
            btnPnl.add( zoomInBtn, gbc );
            gbc.anchor = GridBagConstraints.NORTH;
            btnPnl.add( zoomOutBtn, gbc );
            add( btnPnl, zoomBtnGbc );
        }

        // The x axis label
        add( xAxisLabel, xLabelGbc );

        // Bump the row index for the next time this is called
        rowIdx += 3;
    }

    /**
     * Sets the visibility of the snapshot button depending on the visibility of the
     * graphs.
     *
     * @param checkBoxes
     */
    private void setSnapshotButtonVisibility( ArrayList checkBoxes ) {
        boolean isSnapShotButtonVisible = false;
        for( int i = 0; i < checkBoxes.size(); i++ ) {
            JCheckBox jCheckBox = (JCheckBox)checkBoxes.get( i );
            isSnapShotButtonVisible |= jCheckBox.isSelected();
        }
        snapshotBtn.setVisible( isSnapShotButtonVisible );
    }

    //--------------------------------------------------------------------------------------------------
    // The zooom in and zoom out actions
    //--------------------------------------------------------------------------------------------------

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

    private /*static */class SnapshotAction extends AbstractAction {
        private Component component;
        Point nextLocation = new Point();
        int subsequentSnapshotOffset = 30;

        public SnapshotAction( Icon icon, Component component ) {
            super( "", icon );
            this.component = component;
        }

        public void actionPerformed( ActionEvent e ) {
            JDialog snapshotDlg = createSnapshotDialog();
            snapshotDlg.setLocation( nextLocation );
            nextLocation.setLocation( nextLocation.getX() + subsequentSnapshotOffset,
                                      nextLocation.getY() + subsequentSnapshotOffset );
        }

        private JDialog createSnapshotDialog() {
            JDialog snapshotDlg = new JDialog( PhetUtilities.getPhetFrame(), false );
            snapshotDlg.setResizable( false );
            Snapshot snapshot = new Snapshot( component, module );

            // Construct the header information
            PhotoelectricModel model = (PhotoelectricModel)module.getModel();
            String material = model.getTarget().getMaterial().toString();
            String voltage = Double.toString( model.getVoltage() );
            String wavelength = Double.toString( model.getBeam().getWavelength() );
            String intensity = Double.toString( PhotoelectricModelUtil.photonRateToIntensity( model.getBeam().getPhotonsPerSecond(),
                                                                                              model.getBeam().getWavelength() ) );
            String header = "<html><table border=\"1\" >" +
                            "<tr><td align=\"right\">" + "Material" + "</td>" +
                            "<td align=\"right\">" + "Wavelength" + "</td>" +
                            "<td align=\"right\">" + "Intensity" + "</td>" +
                            "<td align=\"right\">" + "Voltage" + "</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td>" + material + "</td>" +
                            "<td>" + wavelength + " nm" + "</td>" +
                            "<td>" + intensity + "%" + "</td>" +
                            "<td>" + voltage + " v" + "</td>" +
                            "</tr>" +
                            "</table></html>";
            JPanel headerPnl = new JPanel();
            headerPnl.setBorder( new TitledBorder( "Experimental Parameters" ) );
            headerPnl.add( new JLabel( header ) );
            JPanel jp = new JPanel( new BorderLayout() );
            jp.add( headerPnl, BorderLayout.NORTH );
            jp.add( snapshot, BorderLayout.CENTER );
            snapshotDlg.setContentPane( jp );

            snapshotDlg.pack();
            snapshotDlg.setVisible( true );
            return snapshotDlg;
        }
    }

    private static class Snapshot extends JPanel {
        BufferedImage bi;

        public Snapshot( Component component, Module module ) {

            // Make the snapshot image
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
