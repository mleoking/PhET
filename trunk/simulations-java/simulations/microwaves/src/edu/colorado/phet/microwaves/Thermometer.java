/**
 * Class: Thermometer
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Sep 30, 2003
 */
package edu.colorado.phet.microwaves;

import java.awt.geom.Point2D;
import java.util.Observable;

import edu.colorado.phet.common.phetcommon.math.MedianFilter;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.microwaves.model.MicrowaveModel;
import edu.colorado.phet.microwaves.model.WaterMolecule;

public class Thermometer extends Observable implements ModelElement {
    private MicrowaveModel model;
    private Point2D.Double location = new Point2D.Double();
    private static final int historySize = 5;
    private double[] history = new double[historySize];

    public Thermometer( MicrowaveModel model ) {
        this.model = model;
    }

    public void stepInTime( double dt ) {
        double ke = 0;
        for ( int i = 0; i < model.numModelElements(); i++ ) {
            ModelElement modelElement = model.modelElementAt( i );
            if ( modelElement instanceof WaterMolecule ) {
                WaterMolecule waterMolecule = (WaterMolecule) modelElement;
                double vBar = waterMolecule.getVelocity().getLength();
                ke += waterMolecule.getMass() * vBar * vBar / 2;
                ke += waterMolecule.getMomentOfInertia() * waterMolecule.getOmega() * waterMolecule.getOmega() / 2;
            }
        }
        ke /= model.numModelElements();

        // Scale the reading. The 3 is an arbitrary factor
        ke /= 3;

        for ( int i = historySize - 1; i > 0; i-- ) {
            history[i] = history[i - 1];
        }
        history[0] = ke;
        Double result = new Double( MedianFilter.getMedian( history ) );

        setChanged();
//        notifyObservers( new Double( ke ) );
        notifyObservers( result );
    }

    public Point2D.Double getLocation() {
        return location;
    }

    public void setLocation( Point2D.Double location ) {
        this.location = location;
    }
}
