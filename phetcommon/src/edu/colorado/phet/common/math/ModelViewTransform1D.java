/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.math;

/**
 * A class that performs linear tranforms between model and view values.
 * Useful for JSliders, JTextFields, and other view elements that work with
 * scalars in the model.
 * 
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModelViewTransform1D {

    public interface Function {
        double transform( double modelValue );
    }

    public static Function linearFunction = new Function() {
        public double transform( double modelValue ) {
            return modelValue;
        }
    };

    public static class PowerFunction implements Function {
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
    private Function function = linearFunction;

    public ModelViewTransform1D( double modelValue1, double modelValue2, int viewValue1, int viewValue2 ) {
        this.modelMin = modelValue1;
        this.viewMin = viewValue1;
        m = ( (double)( viewValue2 - viewValue1 ) ) / ( modelValue2 - modelValue1 );
    }

    public void setModelToViewFunction( Function function ) {
        this.function = function;
    }

    public double viewToModel( int view ) {
        double model = modelMin + ( (double)( view - viewMin ) ) / m;
        return model;
    }

    public int modelToViewDifferential( double dx ) {
        return (int)function.transform( m * dx );
    }

    public double viewToModelDifferential( int dx ) {
        return dx / m;
    }

    public int modelToView( double model ) {
        int view = (int)function.transform( viewMin + (int)( m * ( model - modelMin ) ) );
        return view;
    }
}
