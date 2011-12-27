// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.dev;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.beerslawlab.concentration.ConcentrationModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * This button opens a debugging dialog that displays observable properties of ConcentrationModel.
 * This is for developer use only, no i18n required.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationModelButton extends JButton {

    private static final String TITLE = "Model Properties (dev)";
    private JDialog dialog;

    // Button that opens the dialog
    public ConcentrationModelButton( final Frame owner, final ConcentrationModel model ) {
        super( TITLE );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( dialog == null ) {
                    dialog = new ConcentrationModelDialog( owner, model );
                    SwingUtils.centerInParent( dialog );
                }
                if ( !dialog.isVisible() ) {
                    dialog.setVisible( true );
                }
            }
        } );
    }

    // Dialog that displays the model panel
    private static class ConcentrationModelDialog extends JDialog {
        public ConcentrationModelDialog( Frame owner, ConcentrationModel model ) {
            super( owner, TITLE );
            setResizable( false );
            setContentPane( new ConcentrationModelPanel( model ) );
            pack();
        }
    }

    // Heading for related properties
    private static class HeadingLabel extends JLabel {
        public HeadingLabel( String text ) {
            super( text );
            setFont( new PhetFont( Font.BOLD, 12 ) );
        }
    }

    // Panel that displays the model properties
    private static class ConcentrationModelPanel extends VerticalLayoutPanel {

        public ConcentrationModelPanel( final ConcentrationModel model ) {
            setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
            setInsets( new Insets( 2, 2, 2, 2 ) );

            // labels
            final JLabel evaporationRate = new JLabel();
            final JLabel dropperLocation = new JLabel();
            final JLabel inputFaucetFlowRate = new JLabel();
            final JLabel outputFaucetFlowRate = new JLabel();
            final JLabel shakerLocation = new JLabel();
            final JLabel soluteForm = new JLabel();
            final JLabel soluteFormula = new JLabel();
            final JLabel solutionConcentration = new JLabel();
            final JLabel solutionPrecipitateAmount = new JLabel();
            final JLabel solutionPrecipitateParticles = new JLabel();
            final JLabel solutionSoluteAmount = new JLabel();
            final JLabel solutionVolume = new JLabel();
            final JLabel meterBodyLocation = new JLabel();
            final JLabel meterProbeLocation = new JLabel();
            final JLabel shakerDispensingRate = new JLabel();
            final JLabel dropperFlowRate = new JLabel();

            // populate panel
            add( new HeadingLabel( "solute:" ) );
            add( soluteForm );
            add( soluteFormula );
            add( Box.createVerticalStrut( 5 ) );
            add( new HeadingLabel( "solution:" ) );
            add( solutionSoluteAmount );
            add( solutionVolume );
            add( solutionConcentration );
            add( solutionPrecipitateAmount );
            add( solutionPrecipitateParticles );
            add( Box.createVerticalStrut( 5 ) );
            add( new HeadingLabel( "rates:" ) );
            add( inputFaucetFlowRate );
            add( outputFaucetFlowRate );
            add( evaporationRate );
            add( shakerDispensingRate );
            add( dropperFlowRate );
            add( Box.createVerticalStrut( 5 ) );
            add( new HeadingLabel( "locations:" ) );
            add( shakerLocation );
            add( dropperLocation );
            add( meterBodyLocation );
            add( meterProbeLocation );

            // formats
            final DecimalFormat rateFormat = new DecimalFormat( "0.000" );
            final DecimalFormat volumeFormat = new DecimalFormat( "0.00" );
            final DecimalFormat molesFormat = new DecimalFormat( "0.0000" );
            final DecimalFormat concentrationFormat = new DecimalFormat( "0.0000" );

            // observers
            model.dropper.location.addObserver( new SimpleObserver() {
                public void update() {
                    dropperLocation.setText( "dropper.location = (" + (int) model.dropper.location.get().getX() + "," + (int) model.dropper.location.get().getY() + ")" );
                }
            } );
            model.evaporationRate.addObserver( new SimpleObserver() {
                public void update() {
                    evaporationRate.setText( "evaporationRate = " + rateFormat.format( model.evaporationRate.get() ) + " L/sec" );
                }
            } );
            model.inputFaucet.flowRate.addObserver( new SimpleObserver() {
                public void update() {
                    inputFaucetFlowRate.setText( "inputFaucet.flowRate = " + rateFormat.format( model.inputFaucet.flowRate.get() ) + " L/sec" );
                }
            } );
            model.outputFaucet.flowRate.addObserver( new SimpleObserver() {
                public void update() {
                    outputFaucetFlowRate.setText( "outputFaucet.flowRate = " + rateFormat.format( model.outputFaucet.flowRate.get() ) + " L/sec" );
                }
            } );
            model.shaker.location.addObserver( new SimpleObserver() {
                public void update() {
                    shakerLocation.setText( "shaker.location = (" + (int) model.shaker.location.get().getX() + "," + (int) model.shaker.location.get().getY() + ")" );
                }
            } );
            model.solute.addObserver( new SimpleObserver() {
                public void update() {
                    soluteFormula.setText( "solute.formula = " + model.solute.get().formula );
                }
            } );
            model.soluteForm.addObserver( new SimpleObserver() {
                public void update() {
                    soluteForm.setText( "soluteForm = " + model.soluteForm.get() );
                }
            } );
            model.solution.addConcentrationObserver( new SimpleObserver() {
                public void update() {
                    solutionConcentration.setText( "solution.concentration = " + concentrationFormat.format( model.solution.getConcentration() ) + " M" );
                }
            } );
            model.solution.addPrecipitateAmountObserver( new SimpleObserver() {
                public void update() {
                    solutionPrecipitateAmount.setText( "solution.precipitateAmount = " + molesFormat.format( model.solution.getPrecipitateAmount() ) + " mol" );
                    solutionPrecipitateParticles.setText( "solution.precipitateParticles = " + model.solution.getNumberOfPrecipitateParticles() );
                }
            } );
            model.solution.soluteAmount.addObserver( new SimpleObserver() {
                public void update() {
                    solutionSoluteAmount.setText( "solution.soluteAmount = " + molesFormat.format( model.solution.soluteAmount.get() ) + " mol" );
                }
            } );
            model.solution.volume.addObserver( new SimpleObserver() {
                public void update() {
                    solutionVolume.setText( "solution.volume = " + volumeFormat.format( model.solution.volume.get() ) + " L" );
                }
            } );
            model.concentrationMeter.body.location.addObserver( new SimpleObserver() {
                public void update() {
                    meterBodyLocation.setText( "meter.body.location = (" + (int) model.concentrationMeter.body.location.get().getX() + "," + (int) model.concentrationMeter.body.location.get().getY() + ")" );
                }
            } );
            model.concentrationMeter.probe.location.addObserver( new SimpleObserver() {
                public void update() {
                    meterProbeLocation.setText( "meter.probe.location = (" + (int) model.concentrationMeter.probe.location.get().getX() + "," + (int) model.concentrationMeter.probe.location.get().getY() + ")" );
                }
            } );
            model.shaker.addDispensingRateObserver( new SimpleObserver() {
                public void update() {
                    shakerDispensingRate.setText( "shaker.dispensingRate = " + rateFormat.format( model.shaker.getDispensingRate() ) + " mol/sec" );
                }
            } );
            model.dropper.addFlowRateObserver( new SimpleObserver() {
                public void update() {
                    dropperFlowRate.setText( "dropper.flowRate = " + rateFormat.format( model.dropper.getFlowRate() ) + " L/sec" );
                }
            } );
        }
    }
}
