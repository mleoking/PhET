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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * EnergyGraphDialog
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
//public class EnergyGraphDialog extends JInternalFrame {
public class EnergyGraphDialog extends JDialog {
    private static final Color BACKGROUND_COLOR = new Color( 255, 255, 225 );

    private BarGauge rateGauge;
    private BarGauge totalEnergyGauge;
    private double barWidth = 15;
    private double totalEnergy = 0;
    private TotalEnergyPanel totalEnergyPanel;
    private EnergyRatePanel energyRatePanel;
    String family = "SansSerif";
    int style = Font.BOLD;
    int size = 14;
    private Font font = new Font( family, Font.BOLD, size );
    private Dimension panelSize = new Dimension( 100, 245 );
    private Insets barInsets = new Insets( 5, 0, 5,0);


    public EnergyGraphDialog( Frame owner, int numNuclei ) throws HeadlessException {
        super( owner, "Energy Graphs", false );

        setResizable( false );
        JPanel contentPane = new JPanel( new GridBagLayout() );
        contentPane.setBackground( BACKGROUND_COLOR );
        setContentPane( contentPane );

        GridBagConstraints gbc = new GridBagConstraints( GridBagConstraints.RELATIVE,0,
                                                         1,1,1,1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 5, 0, 5, 0),0,0);

        energyRatePanel = new EnergyRatePanel();
        contentPane.add( energyRatePanel, gbc );
        totalEnergyPanel = new TotalEnergyPanel();
        contentPane.add( totalEnergyPanel, gbc  );

        reset( numNuclei );
        setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );

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
        private double scale = 2;

        public EnergyRatePanel() {
            super( null );
            setBackground( BACKGROUND_COLOR );
            setPreferredSize( new Dimension( panelSize.width / 2, panelSize.height - barInsets.top - barInsets.bottom ));
            rateGauge = new BarGauge( new Point2D.Double( ( getPreferredSize().getWidth() - barWidth ) / 2, 0 ),
                                      getPreferredSize().getHeight() - 2,
                                      Color.orange,
                                      barWidth, true, 0, 1 );
        }

        protected void paintComponent( Graphics g ) {
            super.paintComponent( g );
            Graphics2D g2 = (Graphics2D)g;
            rateGauge.paint( g2 );
            // Draw string rotated counter-clockwise 90 degrees
            AffineTransform at = new AffineTransform();
            at.setToTranslation( 10, (int)getPreferredSize().getHeight() - 10 );
            at.rotate( -Math.PI / 2.0 );
            g2.transform( at );
            g2.setFont( font );
            g2.drawString( "Energy production rate (J/sec)", 0, 0 );
        }

        public void temperatureChanged( Vessel.ChangeEvent event ) {
            rateGauge.setLevel( event.getVessel().getTemperature() * scale  );
            EnergyGraphDialog.this.repaint();
        }
    }

    private class TotalEnergyPanel extends JPanel implements FissionListener {
        public TotalEnergyPanel() {
            super( null );
            setBackground( BACKGROUND_COLOR );
            setPreferredSize( new Dimension( panelSize.width / 2, panelSize.height - barInsets.top - barInsets.bottom ));
            totalEnergyGauge = new BarGauge( new Point2D.Double( ( getPreferredSize().getWidth() - barWidth ) / 2, 0 ),
                                             getPreferredSize().getHeight() - 2,
                                             Color.green, barWidth, true, 0, 200 );
        }

        protected void paintComponent( Graphics g ) {
            super.paintComponent( g );
            Graphics2D g2 = (Graphics2D)g;
            totalEnergyGauge.paint( g2 );
            // Draw string rotated counter-clockwise 90 degrees
            AffineTransform at = new AffineTransform();
            at.setToTranslation( 10, (int)getPreferredSize().getHeight() - 10 );
            at.rotate( -Math.PI / 2.0 );
            g2.transform( at );
            g2.setFont( font );
            g2.drawString( "Total energy produced (J)", 0, 0 );
        }

        public void fission( FissionProducts products ) {
            totalEnergy++;
            totalEnergyGauge.setLevel( totalEnergy );
            this.repaint();
        }
    }
}
