// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

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

import edu.colorado.phet.beerslawlab.concentration.model.ConcentrationModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Dialog that displays observable properties of ConcentrationModel.
 * This is for developer use only, no i18n required.
 * Inner class ConcentrationModelButton provides a button that opens the dialog.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationModelDialog extends JDialog {

    private static final String TITLE = "Model Properties (dev)";

    // Dialog that displays the model panel
    public ConcentrationModelDialog( Frame owner, ConcentrationModel model ) {
        super( owner, TITLE );
        setResizable( false );
        setContentPane( new ConcentrationModelPanel( model ) );
        pack();
    }

    // Button that opens the dialog
    public static class ConcentrationModelButton extends JButton {

        private JDialog dialog;

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
    }

    // Heading for related properties, used in the panel
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
            final JLabel soluteSaturatedConcentration = new JLabel();
            final JLabel soluteStockConcentration = new JLabel();

            // populate panel
            add( new HeadingLabel( "solute:" ) );
            add( soluteFormula );
            add( soluteForm );
            add( soluteSaturatedConcentration );
            add( soluteStockConcentration );
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
            final DecimalFormat concentrationFormat = new DecimalFormat( ConcentrationMeterNode.VALUE_PATTERN );

            // observers
            model.dropper.location.addObserver( new SimpleObserver() {
                public void update() {
                    dropperLocation.setText( "dropper = (" + (int) model.dropper.location.get().getX() + "," + (int) model.dropper.location.get().getY() + ")" );
                }
            } );
            model.evaporator.evaporationRate.addObserver( new SimpleObserver() {
                public void update() {
                    evaporationRate.setText( "evaporation = " + rateFormat.format( model.evaporator.evaporationRate.get() ) + " L/sec" );
                }
            } );
            model.solventFaucet.flowRate.addObserver( new SimpleObserver() {
                public void update() {
                    inputFaucetFlowRate.setText( "solvent faucet = " + rateFormat.format( model.solventFaucet.flowRate.get() ) + " L/sec" );
                }
            } );
            model.drainFaucet.flowRate.addObserver( new SimpleObserver() {
                public void update() {
                    outputFaucetFlowRate.setText( "drain faucet = " + rateFormat.format( model.drainFaucet.flowRate.get() ) + " L/sec" );
                }
            } );
            model.shaker.location.addObserver( new SimpleObserver() {
                public void update() {
                    shakerLocation.setText( "shaker = (" + (int) model.shaker.location.get().getX() + "," + (int) model.shaker.location.get().getY() + ")" );
                }
            } );
            model.solute.addObserver( new SimpleObserver() {
                public void update() {
                    soluteFormula.setText( HTMLUtils.toHTMLString( "formula = " + model.solute.get().formula ) );
                    soluteSaturatedConcentration.setText( "saturated concentration = " + concentrationFormat.format( model.solute.get().getSaturatedConcentration() ) + " M" );
                    soluteStockConcentration.setText( "stock concentration = " + concentrationFormat.format( model.solute.get().stockSolutionConcentration ) + " M" );
                }
            } );
            model.soluteForm.addObserver( new SimpleObserver() {
                public void update() {
                    soluteForm.setText( "form = " + model.soluteForm.get() );
                }
            } );
            model.solution.concentration.addObserver( new SimpleObserver() {
                public void update() {
                    solutionConcentration.setText( "concentration = " + concentrationFormat.format( model.solution.concentration.get() ) + " M" );
                }
            } );
            model.solution.precipitateAmount.addObserver( new SimpleObserver() {
                public void update() {
                    solutionPrecipitateAmount.setText( "precipitate amount = " + molesFormat.format( model.solution.precipitateAmount.get() ) + " mol" );
                    solutionPrecipitateParticles.setText( "precipitate particles = " + model.solution.getNumberOfPrecipitateParticles() );
                }
            } );
            model.solution.soluteAmount.addObserver( new SimpleObserver() {
                public void update() {
                    solutionSoluteAmount.setText( "solute amount = " + molesFormat.format( model.solution.soluteAmount.get() ) + " mol" );
                }
            } );
            model.solution.volume.addObserver( new SimpleObserver() {
                public void update() {
                    solutionVolume.setText( "volume = " + volumeFormat.format( model.solution.volume.get() ) + " L" );
                }
            } );
            model.concentrationMeter.body.location.addObserver( new SimpleObserver() {
                public void update() {
                    meterBodyLocation.setText( "meter = (" + (int) model.concentrationMeter.body.location.get().getX() + "," + (int) model.concentrationMeter.body.location.get().getY() + ")" );
                }
            } );
            model.concentrationMeter.probe.location.addObserver( new SimpleObserver() {
                public void update() {
                    meterProbeLocation.setText( "probe = (" + (int) model.concentrationMeter.probe.location.get().getX() + "," + (int) model.concentrationMeter.probe.location.get().getY() + ")" );
                }
            } );
            model.shaker.addDispensingRateObserver( new SimpleObserver() {
                public void update() {
                    shakerDispensingRate.setText( "shaker = " + rateFormat.format( model.shaker.getDispensingRate() ) + " mol/sec" );
                }
            } );
            model.dropper.flowRate.addObserver( new SimpleObserver() {
                public void update() {
                    dropperFlowRate.setText( "dropper = " + rateFormat.format( model.dropper.flowRate.get() ) + " L/sec" );
                }
            } );
        }
    }
}
