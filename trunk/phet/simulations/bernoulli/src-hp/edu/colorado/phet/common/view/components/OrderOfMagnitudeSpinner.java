/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 13, 2003
 * Time: 11:34:16 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.common.view.components;

import javax.swing.*;

/**
 * A subclass of JSpinner that moves up and down through orders of magnitude
 */
public class OrderOfMagnitudeSpinner extends JSpinner {

    public OrderOfMagnitudeSpinner(float minFactor, float maxFactor) {
        super();
        final SpinnerModel model = new OrderOfMagnitudeListModel(minFactor, maxFactor);
        setModel(model);
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

        OrderOfMagnitudeListModel(float minFactor, float maxFactor) {
            super(1.0, minFactor, maxFactor, 1);
            this.maxFactor = maxFactor;
            this.minFactor = minFactor;
        }

        public Object getNextValue() {
            Double currValue = (Double) getValue();
            if (currValue.floatValue() >= maxFactor) {
                return currValue;
            } else {
                return new Double(((Double) getValue()).floatValue() * 10);
            }
        }

        public Object getPreviousValue() {
            Double currValue = (Double) getValue();
            if (currValue.floatValue() <= minFactor) {
                return currValue;
            } else {
                return new Double(((Double) getValue()).floatValue() / 10);
            }
        }
    }
}
