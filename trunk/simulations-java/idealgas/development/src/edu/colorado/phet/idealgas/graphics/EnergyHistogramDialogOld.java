/**
 * Class: EnergyHistogramDialog
 * Class: edu.colorado.phet.idealgas.graphics
 * User: Ron LeMaster
 * Date: Jan 19, 2004
 * Time: 9:19:30 AM
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.graphics.Histogram;
import edu.colorado.phet.graphics.util.GraphicsUtil;
import edu.colorado.phet.idealgas.controller.IdealGasApplication;
import edu.colorado.phet.idealgas.physics.GasMolecule;
import edu.colorado.phet.idealgas.physics.IdealGasSystem;
import edu.colorado.phet.idealgas.physics.HeavySpecies;
import edu.colorado.phet.idealgas.physics.LightSpecies;
import edu.colorado.phet.physics.body.Body;
import edu.colorado.phet.physics.body.Particle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;

public class EnergyHistogramDialogOld extends JDialog {

    private Histogram energyHistogram;
    private IdealGasApplication application;
    // Number of energyHistogram updates between times it will be displayed and then
    // the data cleared
    private int averagingRatio = 4;
    // Bin count beyond which the energyHistogram will clip
    private int initialEnergyClippingLevel = 50;
    private int initialSpeedClippingLevel = 20;
    private Histogram speedHistogram;
    private Histogram heavySpeedHistogram;
    private Histogram lightSpeedHistogram;
    private boolean showDetails;
    private JButton detailsBtn;
    private JLabel lightSpeedLabel;
    private JLabel heavySpeedLabel;

    public EnergyHistogramDialogOld( IdealGasApplication application ) throws HeadlessException {
        super( application.getPhetFrame() );
        this.setTitle( "Particle Statistics" );

        this.application = application;
        this.setResizable( false );

        // Create the histograms
        energyHistogram = new Histogram( 200, 150, 0, 100E3, 20, initialEnergyClippingLevel * averagingRatio, new Color( 0, 0, 0 ) );
        speedHistogram = new Histogram( 200, 150, 0, 70, 20, initialSpeedClippingLevel * averagingRatio, new Color( 0, 0, 0 ) );
        heavySpeedHistogram = new Histogram( 200, 150, 0, 70, 20, initialSpeedClippingLevel * averagingRatio, new Color( 20, 0, 200 ) );
        lightSpeedHistogram = new Histogram( 200, 150, 0, 70, 20, initialSpeedClippingLevel * averagingRatio, new Color( 200, 0, 20 ) );

        // Add a button for hiding/displaying the individual species
        detailsBtn = new JButton();
        detailsBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showDetails = !showDetails;
                heavySpeedHistogram.setVisible( showDetails );
                heavySpeedLabel.setVisible( showDetails );
                lightSpeedHistogram.setVisible( showDetails );
                lightSpeedLabel.setVisible( showDetails );

                EnergyHistogramDialogOld.this.pack();
                EnergyHistogramDialogOld.this.repaint();
            }
        } );
        this.layoutComponents();
        this.setDefaultCloseOperation( JDialog.EXIT_ON_CLOSE );

        heavySpeedHistogram.setVisible( showDetails );
        heavySpeedLabel.setVisible( showDetails );
        lightSpeedHistogram.setVisible( showDetails );
        lightSpeedLabel.setVisible( showDetails );

        this.pack();

        // Add a listener for the close event that gets rid of this dialog
        this.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent evt ) {
                JDialog dlg = (JDialog)evt.getSource();

                // Hide the frame
                dlg.setVisible( false );

                // If the frame is no longer needed, call dispose
                dlg.dispose();
            }
        } );

        // Create and start updaters for the histograms
        new EnergyUpdater( application.getIdealGasSystem(), energyHistogram ).start();
        new SpeedUpdater( application.getIdealGasSystem(), speedHistogram ).start();
        new SpeciesSpeedUpdater( HeavySpecies.class, application.getIdealGasSystem(), heavySpeedHistogram ).start();
        new SpeciesSpeedUpdater( LightSpecies.class, application.getIdealGasSystem(), lightSpeedHistogram ).start();
    }

    private void layoutComponents() {

        if( showDetails ) {
            detailsBtn.setText( "<< Fewer details" );
        }
        else {
            detailsBtn.setText( "More details >>" );
        }

        this.getContentPane().setLayout( new GridBagLayout() );
        try {
            int rowIdx = 0;
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              new JLabel( "Energy Distribution" ),
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              energyHistogram,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              new JLabel( "Speed Distribution" ),
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              speedHistogram,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            heavySpeedLabel = new JLabel( "Heavy Speed Distribution" );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              heavySpeedLabel,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              heavySpeedHistogram,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            lightSpeedLabel = new JLabel( "Light Speed Distribution" );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              lightSpeedLabel,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              lightSpeedHistogram,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              detailsBtn,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
        this.repaint();
    }

    public void paintComponents( Graphics g ) {
        super.paintComponents( g );
    }

    //
    // Static fields and methods
    //

    //
    // Inner classes
    //
    private abstract class Updater extends Thread {
        private IdealGasSystem model;
        private Histogram histogram;

        Updater( IdealGasSystem model, Histogram histogram ) {
            this.model = model;
            this.histogram = histogram;
        }

        protected abstract double getParticleAttribute( Particle particle );

        protected abstract int getClippingLevel();

        public void run() {
            int cnt = 0;
            while( true ) {
                try {
                    Thread.sleep( 500 );

                    // If the dialog isn't visible, don't go through the work of
                    // collecting the information
                    if( EnergyHistogramDialogOld.this.isVisible() ) {
                        // If we are at the first iteration of an averaging cycle, clear the data from the energyHistogram
                        // and compute the new clipping level
                        if( ( cnt % averagingRatio ) == 1 ) {
                            histogram.clear();
                            histogram.setClippingLevel( this.getClippingLevel() );
                        }
                        List bodies = model.getBodies();
                        for( int i = 0; i < bodies.size(); i++ ) {
                            Body body = (Body)bodies.get( i );
                            if( body instanceof GasMolecule ) {
                                histogram.add( getParticleAttribute( body ) );
                            }
                        }

                        // Force a redraw
                        if( ( cnt++ % averagingRatio ) == 0 ) {
                            EnergyHistogramDialogOld.this.repaint();
                        }
                    }
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }

            }
        }

        protected IdealGasSystem getModel() {
            return model;
        }
    }


    private class EnergyUpdater extends Updater {
        EnergyUpdater( IdealGasSystem model, Histogram histogram ) {
            super( model, histogram );
        }

        protected double getParticleAttribute( Particle particle ) {
            return getModel().getBodyEnergy( particle );
        }


        protected int getClippingLevel() {
            int cl = averagingRatio * Math.max( application.getIdealGasSystem().getBodies().size() / 3,
                                                initialEnergyClippingLevel );
            return cl;
        }
    }


    private class SpeedUpdater extends Updater {
        SpeedUpdater( IdealGasSystem model, Histogram histogram ) {
            super( model, histogram );
        }

        protected double getParticleAttribute( Particle particle ) {
            return particle.getSpeed();
        }

        protected int getClippingLevel() {
            int cl = averagingRatio * Math.max( application.getIdealGasSystem().getBodies().size() / 5,
                                                initialSpeedClippingLevel );
            return cl;
        }
    }


    private class SpeciesSpeedUpdater extends SpeedUpdater {
        private Class species;

        SpeciesSpeedUpdater( Class species, IdealGasSystem model, Histogram histogram ) {
            super( model, histogram );
            this.species = species;
        }

        protected double getParticleAttribute( Particle particle ) {
            if( species.isInstance( particle ) ) {
                return super.getParticleAttribute( particle );
            }
            else {
                return -1;
            }
        }

//        protected int getClippingLevel() {
//            return super.getClippingLevel() / 2;
//        }
    }
}
