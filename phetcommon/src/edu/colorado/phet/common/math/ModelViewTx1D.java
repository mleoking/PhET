/**
 * Class: ModelViewTx1D
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Sep 12, 2003
 */
package edu.colorado.phet.common.math;

/**
 * A class that performs linear tranforms between model and view values.
 * Useful for JSliders, JTextFields, and other view elements that work with
 * scalars in the model.
 */
public class ModelViewTx1D {

    public interface ModelToViewFunction {
        double transform( double modelValue );
    }

    public static ModelToViewFunction linearFunction = new ModelToViewFunction() {
        public double transform( double modelValue ) {
            return modelValue;
        }
    };

    public static class PowerFunction implements ModelToViewFunction {
        private double power;

        public PowerFunction( double power ) {
            this.power = power;
        }

        public double transform( double modelValue ) {
            return Math.pow( modelValue, power );
        }
    }

    private double modelMin;
    private int viewMin;
    private double m;
    private ModelToViewFunction modelToViewFunction = linearFunction;

    public ModelViewTx1D( double modelValue1, double modelValue2, int viewValue1, int viewValue2 ) {
        this.modelMin = modelValue1;
        this.viewMin = viewValue1;
        m = ( (double)( viewValue2 - viewValue1 ) ) / ( modelValue2 - modelValue1 );
    }

    public void setModelToViewFunction( ModelToViewFunction function ) {
        this.modelToViewFunction = function;
    }

    public double viewToModel( int view ) {
        double model = modelMin + ( (double)( view - viewMin ) ) / m;
        return model;
    }

    public int modelToViewDifferential( double dx ) {
        return (int)modelToViewFunction.transform( m * dx );
    }

    public double viewToModelDifferential( int dx ) {
        return dx / m;
    }

    public int modelToView( double model ) {
        int view = (int)modelToViewFunction.transform( viewMin + (int)( m * ( model - modelMin ) ));
        return view;
    }
}
