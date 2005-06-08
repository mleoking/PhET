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

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
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
public class EnergyGraphDialog extends JDialog implements FissionListener, ClockTickListener {
    private static final Color BACKGROUND_COLOR = new Color( 255, 255, 225 );

    private BarGauge rateGauge;
    private BarGauge totalEnergyGauge;
    private double barWidth = 15;
    private double totalEnergy = 0;
    private AbstractClock clock;

    public EnergyGraphDialog( Frame owner, Vessel vessel, AbstractClock clock ) throws HeadlessException {
        super( owner, "Energy Graphs", false );

        this.clock = clock;
        clock.addClockTickListener( this );

        setUndecorated( true );
        getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );

        JPanel contentPane = new JPanel( new GridLayout( 1, 2 ) );
        setContentPane( contentPane );

        contentPane.add( new EnergyRatePanel() );
        contentPane.add( new TotalEnergyPanel() );

        // Add a listener to the vessel to track energy rate and total energy produced
//        vessel.addChangeListener( new VesselEnergyListener() );

        this.pack();
    }

    public void reset() {
        totalEnergyGauge.setLevel( 0 );
    }

    //----------------------------------------------------------------
    // Component panels
    //----------------------------------------------------------------

    private class EnergyRatePanel extends JPanel {

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
    }

    private class TotalEnergyPanel extends JPanel {
        public TotalEnergyPanel() {
            super( null );
            setBackground( BACKGROUND_COLOR );
            setPreferredSize( new Dimension( 100, 200 ) );
            totalEnergyGauge = new BarGauge( new Point2D.Double( ( getPreferredSize().getWidth() - barWidth ) / 2, 0 ),
                                             200, Color.green, barWidth, true, 0, 300 );
        }

        protected void paintComponent( Graphics g ) {
            super.paintComponent( g );
            Graphics2D g2 = (Graphics2D)g;
            totalEnergyGauge.paint( g2 );
        }
    }

    //----------------------------------------------------------------
    // Data acquisition
    //----------------------------------------------------------------
    private double startTime;

    public void startAcquisition() {
        startTime = clock.getRunningTime();
    }

    public void fission( FissionProducts products ) {
        totalEnergy += 1;
        totalEnergyGauge.setLevel( totalEnergy );
        this.repaint();
    }

    public void clockTicked( AbstractClock clock, double v ) {
        double elapsedTime = clock.getRunningTime() - startTime;
        double rate = totalEnergy / elapsedTime;
        rateGauge.setLevel( rate );
        this.repaint();
    }

    /**
     * Catches fission events from the vessel to track the energy being produced
     */
//    private class VesselEnergyListener implements Vessel.ChangeListener {
//        public void temperatureChanged( Vessel.ChangeEvent event ) {
//            rateGauge.setLevel( event.getVessel().getTemperature() );
//            EnergyGraphDialog.this.repaint();
//        }
//    }
}
