/**
 * Class: EnergyHistogramDialog
 * Class: edu.colorado.phet.idealgas.graphics
 * User: Ron LeMaster
 * Date: Jan 19, 2004
 * Time: 9:19:30 AM
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.LightSpecies;
import edu.colorado.phet.instrumentation.Histogram;
import edu.colorado.phet.mechanics.Body;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * A non-modal dialog that shows histograms of energy and average speed of the particles in an IdealGasModel.
 */
public class EnergyHistogramDialog extends JDialog {

    private Histogram energyHistogram;
    // Number of energyHistogram updates between times it will be displayed and then
    // the data cleared
    private int averagingRatio = 4;
    // Bin count beyond which the energyHistogram will clip
    private int initialEnergyClippingLevel = 50;
    private int initialSpeedClippingLevel = 20;
    private double maxSpeed = 150;
    private Histogram speedHistogram;
    private Histogram heavySpeedHistogram;
    private Histogram lightSpeedHistogram;
    private boolean showDetails;
    private JButton detailsBtn;
    private JLabel lightSpeedLabel;
    private JLabel heavySpeedLabel;
    private IdealGasModel model;
    private RotatedTextLabel heavySpeedYLabel;
    private RotatedTextLabel lightSpeedYLabel;
    private Updater updater;

    /**
     * @param owner
     * @param model
     */
    public EnergyHistogramDialog( Frame owner, IdealGasModel model ) {
        super( owner );
        this.model = model;
        this.setTitle( SimStrings.get( "EnergyHistorgramDialog.Title" ) );
//        this.setUndecorated( true );
//        this.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );
        this.setResizable( false );
        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                updater.setRunning( false );
            }
        } );

        // Create the histograms
        energyHistogram = new Histogram( 200, 150, 0, 100E3 * 1.01, 20, initialEnergyClippingLevel * averagingRatio, new Color( 0, 0, 0 ) );
        speedHistogram = new Histogram( 200, 150, 0, maxSpeed * 1.01, 20, initialSpeedClippingLevel * averagingRatio, new Color( 0, 0, 0 ) );
        heavySpeedHistogram = new Histogram( 200, 150, 0, maxSpeed * 1.01, 20, initialSpeedClippingLevel * averagingRatio, new Color( 20, 0, 200 ) );
        lightSpeedHistogram = new Histogram( 200, 150, 0, maxSpeed * 1.01, 20, initialSpeedClippingLevel * averagingRatio, new Color( 200, 0, 20 ) );

        // Add a button for hiding/displaying the individual species
        detailsBtn = new JButton();
        detailsBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showDetails = !showDetails;
                hideShowDetails();
            }
        } );
        this.layoutComponents();
        this.setDefaultCloseOperation( JDialog.EXIT_ON_CLOSE );
        hideShowDetails();

        this.pack();

        // Add a listener for the close event that gets rid of this dialog
        this.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent evt ) {
                JDialog dlg = (JDialog)evt.getSource();
                // Hide the frame and dispose it
                dlg.setVisible( false );
                dlg.dispose();
            }
        } );

        // Create and start updaters for the histograms
        updater = new Updater( model );
        updater.addClient( new EnergyUpdaterClient( model, energyHistogram ) );
        updater.addClient( new SpeedUpdaterClient( speedHistogram ) );
        updater.addClient( new SpeciesSpeedUpdaterClient( HeavySpecies.class, heavySpeedHistogram ) );
        updater.addClient( new SpeciesSpeedUpdaterClient( LightSpecies.class, lightSpeedHistogram ) );
        updater.start();
    }

    private void hideShowDetails() {
        heavySpeedHistogram.setVisible( showDetails );
        heavySpeedLabel.setVisible( showDetails );
        lightSpeedHistogram.setVisible( showDetails );
        lightSpeedLabel.setVisible( showDetails );
        heavySpeedYLabel.setVisible( showDetails );
        lightSpeedYLabel.setVisible( showDetails );
        if( showDetails ) {
            detailsBtn.setText( SimStrings.get( "EnergyHistorgramDialog.Fewer_Details" ) );
        }
        else {
            detailsBtn.setText( SimStrings.get( "EnergyHistorgramDialog.More_Details" ) );
        }
        pack();
        repaint();
    }

    /**
     *
     */
    private void layoutComponents() {
        this.getContentPane().setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );

        heavySpeedLabel = new JLabel( SimStrings.get( "EnergyHistorgramDialog.Heavy_Speed_label" ) );
        lightSpeedLabel = new JLabel( SimStrings.get( "EnergyHistorgramDialog.Light_Speed_label" ) );

        Container contentPane = getContentPane();
        // Upper histogram
        gbc.gridx = 0;
        contentPane.add( new RotatedTextLabel(), gbc );
        gbc.gridx = 1;
        contentPane.add( energyHistogram, gbc );
        gbc.gridy++;
        contentPane.add( new JLabel( SimStrings.get( "EnergyHistorgramDialog.Energy_Distribution" ) ), gbc );

        // Second histogram
        gbc.gridx = 0;
        gbc.gridy++;
        contentPane.add( new RotatedTextLabel(), gbc );
        gbc.gridx = 1;
        contentPane.add( speedHistogram, gbc );
        contentPane.add( new RotatedTextLabel(), gbc );
        gbc.gridy++;
        contentPane.add( new JLabel( SimStrings.get( "EnergyHistorgramDialog.Speed_Distribution" ) ), gbc );

        // Details histograms
        gbc.gridy++;
        gbc.gridx = 0;
        heavySpeedYLabel = new RotatedTextLabel();
        contentPane.add( heavySpeedYLabel, gbc );
        gbc.gridx = 1;
        contentPane.add( heavySpeedHistogram, gbc );
        gbc.gridy++;
        contentPane.add( heavySpeedLabel, gbc );
        gbc.gridy++;
        gbc.gridx = 0;
        lightSpeedYLabel = new RotatedTextLabel();
        contentPane.add( lightSpeedYLabel, gbc );
        gbc.gridx = 1;
        contentPane.add( lightSpeedHistogram, gbc );
        gbc.gridy++;
        contentPane.add( lightSpeedLabel, gbc );

        // Details button
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.insets = new Insets( 10, 10, 10, 10 );
        contentPane.add( detailsBtn, gbc );
    }

    //
    // Inner classes
    //

    /**
     * A thread that updates the client objects that record attributes of objects in the
     * model. At specific intervals, it causes the histogram display to repaint.
     */
    private class Updater extends Thread {
        private IdealGasModel model;
        private ArrayList clients = new ArrayList();
        private boolean isRunning = false;

        Updater( IdealGasModel model ) {
            this.model = model;
        }

        void addClient( UpdaterClient client ) {
            clients.add( client );
        }

        public synchronized boolean isRunning() {
            return isRunning;
        }

        public synchronized void setRunning( boolean running ) {
            isRunning = running;
        }

        public void run() {
            int cnt = 0;
            isRunning = true;
            while( isRunning() ) {
                try {
                    Thread.sleep( 200 );

                    // If the dialog isn't visible, don't go through the work of
                    // collecting the information
                    if( EnergyHistogramDialog.this.isVisible() ) {
                        // If we are at the first iteration of an averaging cycle, clear the data from the energyHistogram
                        // and compute the new clipping level
                        if( ( cnt % averagingRatio ) == 1 ) {
                            for( int i = 0; i < clients.size(); i++ ) {
                                UpdaterClient client = (UpdaterClient)clients.get( i );
                                client.clear();
                            }
                        }
                        List bodies = model.getBodies();
                        for( int i = 0; i < bodies.size(); i++ ) {
                            Body body = (Body)bodies.get( i );
                            if( body instanceof GasMolecule ) {
                                for( int j = 0; j < clients.size(); j++ ) {
                                    UpdaterClient client = (UpdaterClient)clients.get( j );
                                    client.recordBody( body );
                                }
                            }
                        }

                        // Force a redraw
                        if( ( cnt++ % averagingRatio ) == 0 ) {
                            EnergyHistogramDialog.this.repaint();
                        }
                    }
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            } // while( isRunning() )
        }
    }

    //----------------------------------------------------------------
    // Updater clients. These classes update a histogram's information
    //----------------------------------------------------------------

    private abstract class UpdaterClient {
        Histogram histogram;

        UpdaterClient( Histogram histogram ) {
            this.histogram = histogram;
        }

        void clear() {
            histogram.clear();
            histogram.setClippingLevel( this.getClippingLevel() );
        }

        void recordBody( Body body ) {
            histogram.add( getBodyAttribute( body ) );
        }

        abstract int getClippingLevel();

        abstract double getBodyAttribute( Body body );
    }

    private class EnergyUpdaterClient extends UpdaterClient {
        private IdealGasModel model;

        EnergyUpdaterClient( IdealGasModel model, Histogram histogram ) {
            super( histogram );
            this.model = model;
        }

        protected double getBodyAttribute( Body body ) {
            return body.getKineticEnergy();
        }

        protected int getClippingLevel() {
            int cl = averagingRatio * Math.max( model.getBodies().size() / 3,
                                                initialEnergyClippingLevel );
            return cl;
        }
    }

    private class SpeedUpdaterClient extends UpdaterClient {
        SpeedUpdaterClient( Histogram histogram ) {
            super( histogram );
        }

        protected double getBodyAttribute( Body body ) {
            return body.getSpeed();
        }

        protected int getClippingLevel() {
            int cl = averagingRatio * Math.max( model.getBodies().size() / 5,
                                                initialSpeedClippingLevel );
            return cl;
        }
    }

    private class SpeciesSpeedUpdaterClient extends SpeedUpdaterClient {
        private Class species;

        SpeciesSpeedUpdaterClient( Class species, Histogram histogram ) {
            super( histogram );
            this.species = species;
        }

        protected double getBodyAttribute( Body body ) {
            if( species.isInstance( body ) ) {
                return super.getBodyAttribute( body );
            }
            else {
                return -1;
            }
        }
    }

    //----------------------------------------------------------------
    // Misc inner classes
    //----------------------------------------------------------------

    /**
     * Class for labels of the y axes
     */
    private static class RotatedTextLabel extends JPanel {
        public RotatedTextLabel() {
            super( null );
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
            g2.drawString( "Number of Particles", x, y );
            gs.restoreGraphics();
        }
    }
}
