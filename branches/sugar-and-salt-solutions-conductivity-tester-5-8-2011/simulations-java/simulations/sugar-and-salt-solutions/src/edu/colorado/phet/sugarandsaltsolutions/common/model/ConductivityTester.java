// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.acidbasesolutions.model.ConductivityTester.ConductivityTesterChangeListener;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeListener;
import edu.colorado.phet.acidbasesolutions.view.IConductivityTester;
import edu.colorado.phet.common.phetcommon.model.property5.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Conductivity tester that can be dipped in the water to light a light bulb.
 *
 * @author Sam Reid
 */
public class ConductivityTester implements IConductivityTester {
    //Position of the probes, in model coordinates
    private Point2D.Double negativeProbeLocation = new Point2D.Double( 200, 200 );
    private Point2D.Double positiveProbeLocation = new Point2D.Double( 500, 200 );

    //Listeners
    private final ArrayList<SolutionRepresentationChangeListener> solutionRepresentationListeners = new ArrayList<SolutionRepresentationChangeListener>();
    private final ArrayList<ConductivityTesterChangeListener> conductivityTesterListeners = new ArrayList<ConductivityTesterChangeListener>();

    //Visibility flag used as an adapter for signifying change messages when the user presses the "show conductivity tester" checkbox
    public final Property<Boolean> visible = new Property<Boolean>( false ) {{
        addObserver( new SimpleObserver() {
            public void update() {
                for ( SolutionRepresentationChangeListener listener : solutionRepresentationListeners ) {
                    listener.visibilityChanged();
                }
            }
        } );
    }};

    //Determine if the conductivity tester is visible
    public boolean isVisible() {
        return visible.getValue();
    }

    //Add a listener
    public void addConductivityTesterChangeListener( ConductivityTesterChangeListener conductivityTesterChangeListener ) {
        conductivityTesterListeners.add( conductivityTesterChangeListener );
    }

    //Determine the size of the probes
    public PDimension getProbeSizeReference() {
        return new PDimension( 50, 100 );
    }

    //Determine the location of the positive probe
    public Point2D getPositiveProbeLocationReference() {
        return positiveProbeLocation;
    }

    //Determine the location of the bulb/battery unit.
    public Point2D getLocationReference() {
        return new Point2D.Double( 300, 200 );
    }

    //Set the location of the positive probe and notify observers
    public void setPositiveProbeLocation( double x, double y ) {
        positiveProbeLocation.setLocation( x, y );
        for ( ConductivityTesterChangeListener listener : conductivityTesterListeners ) {
            listener.positiveProbeLocationChanged();
        }
    }

    //Get the location of the negative probe
    public Point2D getNegativeProbeLocationReference() {
        return negativeProbeLocation;
    }

    //Set the location of the negative probe and notify observers
    public void setNegativeProbeLocation( double x, double y ) {
        negativeProbeLocation.setLocation( x, y );
        for ( ConductivityTesterChangeListener listener : conductivityTesterListeners ) {
            listener.negativeProbeLocationChanged();
        }
    }

    //Get the bulb brightness, a function of the conductivity of the liquid
    public double getBrightness() {
        return 100;
    }

    //Add a listener for changes in the visibility of the control and other properties
    public void addSolutionRepresentationChangeListener( SolutionRepresentationChangeListener listener ) {
        solutionRepresentationListeners.add( listener );
    }
}
