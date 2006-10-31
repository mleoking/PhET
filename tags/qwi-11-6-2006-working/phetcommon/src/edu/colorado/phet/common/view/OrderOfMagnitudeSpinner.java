/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import javax.swing.*;
import java.text.NumberFormat;

/**
 * A subclass of JSpinner that moves up and down through orders of magnitude
 *
 * @author ?
 * @version $Revision$
 */
public class OrderOfMagnitudeSpinner extends JSpinner {

    public OrderOfMagnitudeSpinner( double initValue, double minFactor, double maxFactor, String numberFormat ) {
        this( initValue, minFactor, maxFactor );
        JSpinner.NumberEditor numberEditor = new NumberEditor( this, numberFormat );
        this.setEditor( numberEditor );
    }

    public OrderOfMagnitudeSpinner( double initValue, double minFactor, double maxFactor ) {
        super();
        final SpinnerModel model = new OrderOfMagnitudeListModel( initValue, minFactor, maxFactor );
        setModel( model );
    }

    public OrderOfMagnitudeSpinner( double minFactor, double maxFactor ) {
        this( 1, minFactor, maxFactor );
    }

    //
    // Inner classes
    //

    /**
     * The model class for the OrderOfMagnitudeSpinner
     */
    private class OrderOfMagnitudeListModel extends SpinnerNumberModel {

        private double maxFactor;
        private double minFactor;

        OrderOfMagnitudeListModel( double initValue,double minFactor, double maxFactor ) {
            super( initValue, minFactor, maxFactor, 1 );
            this.maxFactor = maxFactor;
            this.minFactor = minFactor;
        }

        public Object getNextValue() {
            Double currValue = (Double)getValue();
            if( currValue.doubleValue() >= maxFactor ) {
                return currValue;
            }
            else {
                return new Double( ( (Double)getValue() ).doubleValue() * 10 );
            }
        }

        public Object getPreviousValue() {
            Double currValue = (Double)getValue();
            if( currValue.doubleValue() <= minFactor ) {
                return currValue;
            }
            else {
                return new Double( ( (Double)getValue() ).doubleValue() / 10 );
            }
        }
    }
}
