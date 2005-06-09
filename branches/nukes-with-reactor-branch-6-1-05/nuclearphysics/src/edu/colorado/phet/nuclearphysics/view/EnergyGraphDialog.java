/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.instrumentation.BarGauge;
import edu.colorado.phet.nuclearphysics.model.FissionListener;
import edu.colorado.phet.nuclearphysics.model.FissionProducts;
import edu.colorado.phet.nuclearphysics.model.Vessel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * EnergyGraphDialog
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyGraphDialog extends JDialog {
    private static final Color BACKGROUND_COLOR = new Color( 255, 255, 225 );

    private BarGauge rateGauge;
    private BarGauge totalEnergyGauge;
    private double barWidth = 15;
    private double totalEnergy = 0;
    private TotalEnergyPanel totalEnergyPanel;
    private EnergyRatePanel energyRatePanel;

    public EnergyGraphDialog( Frame owner, int numNuclei ) throws HeadlessException {
        super( owner, "Energy Graphs", false );

        setUndecorated( true );
        getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );

        JPanel contentPane = new JPanel( new GridLayout( 1, 2 ) );
        setContentPane( contentPane );

        energyRatePanel = new EnergyRatePanel();
        contentPane.add( energyRatePanel );
        totalEnergyPanel = new TotalEnergyPanel();
        contentPane.add( totalEnergyPanel );

        reset( numNuclei );

        this.pack();
    }

    public void reset( int numNuclei ) {
        totalEnergy = 0;
        totalEnergyGauge.setLevel( totalEnergy );
        totalEnergyGauge.setMax( numNuclei * 1.2 );
    }

    public FissionListener getFissionListener() {
        return totalEnergyPanel;
    }

    public Vessel.ChangeListener getVesselChangeListener() {
        return energyRatePanel;
    }

    //----------------------------------------------------------------
    // Component panels
    //----------------------------------------------------------------

    private class EnergyRatePanel extends JPanel implements Vessel.ChangeListener {

        public EnergyRatePanel() {
            super( null );
            setBackground( BACKGROUND_COLOR );
            setPreferredSize( new Dimension( 100, 200 ) );
            rateGauge = new BarGauge( new Point2D.Double( ( getPreferredSize().getWidth() - barWidth ) / 2, 0 ),
                                      200, Color.green,
                                      barWidth, true, 0, 1 );
        }

        protected void paintComponent( Graphics g ) {
            super.paintComponent( g );
            Graphics2D g2 = (Graphics2D)g;
            rateGauge.paint( g2 );
        }

        public void temperatureChanged( Vessel.ChangeEvent event ) {
            rateGauge.setLevel( event.getVessel().getTemperature() );
            EnergyGraphDialog.this.repaint();
        }
    }

    private class TotalEnergyPanel extends JPanel implements FissionListener {
        public TotalEnergyPanel() {
            super( null );
            setBackground( BACKGROUND_COLOR );
            setPreferredSize( new Dimension( 100, 200 ) );
            totalEnergyGauge = new BarGauge( new Point2D.Double( ( getPreferredSize().getWidth() - barWidth ) / 2, 0 ),
                                             200, Color.green, barWidth, true, 0, 200 );
        }

        protected void paintComponent( Graphics g ) {
            super.paintComponent( g );
            Graphics2D g2 = (Graphics2D)g;
            totalEnergyGauge.paint( g2 );
        }

        public void fission( FissionProducts products ) {
            totalEnergy++;
            totalEnergyGauge.setLevel( totalEnergy );
            this.repaint();
        }
    }
}
