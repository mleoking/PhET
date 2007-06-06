/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/components/OrderOfMagnitudeSpinner.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.view.components;

import javax.swing.*;

/**
 * A subclass of JSpinner that moves up and down through orders of magnitude
 * 
 * @author ?
 * @version $Revision: 1.1.1.1 $
 */
public class OrderOfMagnitudeSpinner extends JSpinner {

    public OrderOfMagnitudeSpinner( float minFactor, float maxFactor ) {
        super();
        final SpinnerModel model = new OrderOfMagnitudeListModel( minFactor, maxFactor );
        setModel( model );
    }

    //
    // Inner classes
    //

    /**
     * The model class for the OrderOfMagnitudeSpinner
     */
    private class OrderOfMagnitudeListModel extends SpinnerNumberModel {

        private float maxFactor;
        private float minFactor;

        OrderOfMagnitudeListModel( float minFactor, float maxFactor ) {
            super( 1.0, minFactor, maxFactor, 1 );
            this.maxFactor = maxFactor;
            this.minFactor = minFactor;
        }

        public Object getNextValue() {
            Double currValue = (Double)getValue();
            if( currValue.floatValue() >= maxFactor ) {
                return currValue;
            }
            else {
                return new Double( ( (Double)getValue() ).floatValue() * 10 );
            }
        }

        public Object getPreviousValue() {
            Double currValue = (Double)getValue();
            if( currValue.floatValue() <= minFactor ) {
                return currValue;
            }
            else {
                return new Double( ( (Double)getValue() ).floatValue() / 10 );
            }
        }
    }
}
