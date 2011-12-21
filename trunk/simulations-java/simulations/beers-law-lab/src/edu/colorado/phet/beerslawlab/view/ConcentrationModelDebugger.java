// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.Color;
import java.text.DecimalFormat;

import edu.colorado.phet.beerslawlab.concentration.ConcentrationModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Debugging node that displays observable properties of ConcentrationModel.
 * This is for developer use only, no i18n required.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationModelDebugger extends VBox {

    public ConcentrationModelDebugger( final ConcentrationModel model ) {
        super( 1, LEFT_ALIGNED );

        setPickable( false );
        setChildrenPickable( false );

        // nodes, initialized with "?" as a workaround for VBox bug #3191
        final PText evaporationRate = new PText( "?" );
        final PText dropperLocation = new PText( "?" );
        final PText inputFaucetFlowRate = new PText( "?" );
        final PText outputFaucetFlowRate = new PText( "?" );
        final PText shakerLocation = new PText( "?" );
        final PText soluteForm = new PText( "?" );
        final HTMLNode soluteFormula = new HTMLNode( "?" );
        final PText solutionConcentration = new PText( "?" );
        final PText solutionPrecipitateAmount = new PText( "?" );
        final PText solutionSoluteAmount = new PText( "?" );
        final PText solutionVolume = new PText( "?" );

        // rendering order
        addChild( new PText( "MODEL PROPERTIES (dev)" ) {{ setTextPaint( Color.RED ); }} );
        addChild( dropperLocation );
        addChild( evaporationRate );
        addChild( inputFaucetFlowRate );
        addChild( outputFaucetFlowRate );
        addChild( shakerLocation );
        addChild( soluteForm );
        addChild( soluteFormula );
        addChild( solutionConcentration );
        addChild( solutionPrecipitateAmount );
        addChild( solutionSoluteAmount );
        addChild( solutionVolume );

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
                soluteFormula.setHTML( "solute.formula = " + model.solute.get().formula );
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
    }
}
