/**
 * Class: ModelViewTx1D
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Sep 12, 2003
 */
package edu.colorado.phet.cck3.common;

/**
 * A class that performs linear tranforms between model and view values.
 * Useful for JSliders, JTextFields, and other view elements that work with
 * scalars in the model.
 */
public class ModelViewTx1D {
    private double modelMin;
    private int viewMin;
    private double m;
    private double modelMax;
    private int viewMax;

    /**
     * @param modelValue1 Model value corresponding to viewValue1
     * @param modelValue2 Model value corresponding to viewValue2
     * @param viewValue1  View value corresponding to modelValue1
     * @param viewValue2  View value corresponding to modelValue2
     */
    public ModelViewTx1D( double modelValue1, double modelValue2, int viewValue1, int viewValue2 ) {
        this.modelMin = modelValue1;
        this.viewMin = viewValue1;
        this.modelMax = modelValue2;
        this.viewMax = viewValue2;
        m = ( (double)( viewValue2 - viewValue1 ) ) / ( modelValue2 - modelValue1 );
    }

    public double viewToModel( int view ) {
        double model = modelMin + ( (double)( view - viewMin ) ) / m;
        return model;
    }

    public int modelToViewDifferential( double dx ) {
        return (int)( m * dx );
    }

    public double viewToModelDifferential( int dx ) {
        return dx / m;
    }

    public int modelToView( double model ) {
        int view = viewMin + (int)( m * ( model - modelMin ) );
        return view;
    }
}
