// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.CobaltChlorideSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.CobaltIINitrateSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.CopperSulfateSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.DrinkMixSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.NickelIIChlorideSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.PotassiumChromateSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.PotassiumDichromateSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.PotassiumPermanganateSolution;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Model for the "Beer's Law" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawModel implements Resettable {

    private final ArrayList<BeersLawSolution> solutions; // the supported set of solutions
    public final Property<BeersLawSolution> solution; // the selected solution
    public final Light light;
    public final Beam beam;
    public final ModelViewTransform mvt;
    public final Cuvette cuvette;
    public final ATDetector detector;
    public final Ruler ruler;

    // Absorbance model
    private final CompositeProperty<Double> absorbance; // A=abC
    private final CompositeProperty<Double> molarAbsorptivity; // a
    private final CompositeProperty<Double> pathLength; // b
    private final Property<Double> concentration; // C

    // % Transmittance model
    public final CompositeProperty<Double> percentTransmittance;

    public BeersLawModel( IClock clock ) {

        // No offset, scale 125x when going from model to view (1cm == 125 pixels)
        this.mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 125 );

        // Solutions, in rainbow (ROYGBIV) order.
        this.solutions = new ArrayList<BeersLawSolution>() {{
            add( new DrinkMixSolution() );
            add( new CobaltIINitrateSolution() );
            add( new CobaltChlorideSolution() );
            add( new PotassiumDichromateSolution() );
            add( new PotassiumChromateSolution() );
            add( new NickelIIChlorideSolution() );
            add( new CopperSulfateSolution() );
            add( new PotassiumPermanganateSolution() );
        }};
        this.solution = new Property<BeersLawSolution>( solutions.get( 0 ) );

        this.light = new Light( new ImmutableVector2D( 1.5, 2.2 ), false, 0.45, solution );

        this.cuvette = new Cuvette( new ImmutableVector2D( light.location.getX() + 1.5, 1.25 ), 1.0, 2.75, new DoubleRange( 0.5, 2.0 ) );

        //TODO this is too complicated
        // absorbance model: A=abC
        {
            // a: molar absorptivity, units=1/(cm*M)
            this.molarAbsorptivity = new CompositeProperty<Double>( new Function0<Double>() {
                public Double apply() {
                    return solution.get().molarAbsorptionMax;
                }
            }, solution );

            // b: path length, synonymous with cuvette width, units=cm
            this.pathLength = new CompositeProperty<Double>( new Function0<Double>() {
                public Double apply() {
                    return cuvette.width.get();
                }
            }, cuvette.width );

            // C: concentration, units=M
            {
                this.concentration = new Property<Double>( solution.get().concentration.get() );

                // This will be attached to the concentration property of the current solution.
                final VoidFunction1<Double> concentrationObserver = new VoidFunction1<Double>() {
                    public void apply( Double concentration ) {
                        BeersLawModel.this.concentration.set( concentration );
                    }
                };

                // Rewire the concentration observer when the solution changes.
                ChangeObserver<BeersLawSolution> solutionObserver = new ChangeObserver<BeersLawSolution>() {
                    public void update( BeersLawSolution newValue, BeersLawSolution oldValue ) {
                        if ( oldValue != null ) {
                            oldValue.concentration.removeObserver( concentrationObserver );
                        }
                        newValue.concentration.addObserver( concentrationObserver );
                    }
                };
                solution.addObserver( solutionObserver );
                solutionObserver.update( solution.get(), null ); // because ChangeObserver.update is not called on registration
            }

            // A=abC
            this.absorbance = new CompositeProperty<Double>( new Function0<Double>() {
                public Double apply() {
                    return molarAbsorptivity.get() * pathLength.get() * concentration.get();
                }
            }, molarAbsorptivity, pathLength, concentration );
        }

        // percent transmittance model: %T = 100 * 10^(-A)
        percentTransmittance = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                return 100 * Math.pow( 10, -absorbance.get() );
            }
        }, absorbance );

        //TODO compute drag bounds to match the stage size
        this.detector = new ATDetector( new ImmutableVector2D( 6, 3.70 ), new PBounds( 0, 0, 7.9, 5.25 ),
                                        new ImmutableVector2D( 6, light.location.getY() ), new PBounds( 0, 0, 7.9, 5.25 ),
                                        light, cuvette, absorbance, percentTransmittance );

        //TODO compute drag bounds to match the stage size
        this.ruler = new Ruler( 2, 0.35, new ImmutableVector2D( 3, 4.9 ), new PBounds( 0, 1, 8, 4.5 ) );

        this.beam = new Beam( light, cuvette, detector, percentTransmittance, mvt );
    }

    public void reset() {
        for ( BeersLawSolution solution : solutions ) {
            solution.reset();
        }
        solution.reset();
        light.reset();
        cuvette.reset();
        detector.reset();
        ruler.reset();
    }

    public ArrayList<BeersLawSolution> getSolutions() {
        return new ArrayList<BeersLawSolution>( solutions );
    }
}