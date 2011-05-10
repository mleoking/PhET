// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.acidbasesolutions.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.acidbasesolutions.model.ConductivityTester.ConductivityTesterChangeListener;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeListener;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Interface used by ConductivityTesterNode
 * @author Sam Reid
 */
public interface IConductivityTester {
    boolean isVisible();

    void addConductivityTesterChangeListener( ConductivityTesterChangeListener conductivityTesterChangeListener );

    PDimension getProbeSizeReference();

    Point2D getPositiveProbeLocationReference();

    Point2D getLocationReference();

    void setPositiveProbeLocation( double x, double y );

    Point2D getNegativeProbeLocationReference();

    void setNegativeProbeLocation( double x, double y );

    double getBrightness();

    void addSolutionRepresentationChangeListener( SolutionRepresentationChangeListener solutionRepresentationChangeListener );
}
