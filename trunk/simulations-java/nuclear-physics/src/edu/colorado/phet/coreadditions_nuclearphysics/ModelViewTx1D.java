/**
 * Class: ModelViewTx1D
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.coreadditions_nuclearphysics;

public class ModelViewTx1D {
    private double modelMin;
    private int viewMin;
    private double m;

    /**
     * @param modelValue1 Model value corresponding to viewValue1
     * @param modelValue2 Model value corresponding to viewValue2
     * @param viewValue1  View value corresponding to modelValue1
     * @param viewValue2  View value corresponding to modelValue2
     */
    public ModelViewTx1D( double modelValue1, double modelValue2, int viewValue1, int viewValue2 ) {

        this.modelMin = modelValue1;
        this.viewMin = viewValue1;
        m = ( (double)( viewValue2 - viewValue1 ) ) / ( modelValue2 - modelValue1 );

    }

    public double viewToModel( int view ) {
        double model = modelMin + ( (double)( view - viewMin ) ) / m;
        return model;
    }

    public double modelToView( double model ) {
        int view = viewMin + (int)( m * ( model - modelMin ) );
        return view;
    }
}
