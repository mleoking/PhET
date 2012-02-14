// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.CobaltChlorideSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.CobaltIINitrateSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.CopperSulfateSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.KoolAidSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.NickelIIChlorideSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.PotassiumChromateSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.PotassiumDichromateSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.PotassiumPermanganateSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.PureWater;
import edu.colorado.phet.beerslawlab.beerslaw.model.Light.LightRepresentation;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Model for the "Beer's Law" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawModel implements Resettable {

    private static final double BEAKER_VOLUME = 1; // L
    private static final DoubleRange SOLUTION_VOLUME_RANGE = new DoubleRange( 0, BEAKER_VOLUME, 0.5 ); // L
    private static final double DEFAULT_SOLUTE_AMOUNT = 0; // moles
    private static final DoubleRange CUVETTE_WIDTH_RANGE = new DoubleRange( 0.5, 2.0, 1.0 ); // cm
    private static final double CUVETTE_HEIGHT = 3; // cm

    private final ArrayList<BeersLawSolution> solutions; // the supported set of solutions
    public final Property<BeersLawSolution> solution; // the selected solution
    public final Light light;
    public final ModelViewTransform mvt;
    public final Cuvette cuvette;
    public final ATDetector detector;

    public BeersLawModel() {

        // No offset, scale 125x when going from model to view (1cm == 125 pixels)
        this.mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 125 );

        // Solutions, in rainbow (ROYGBIV) order.
        this.solutions = new ArrayList<BeersLawSolution>() {{
            add( new KoolAidSolution() );
            add( new CobaltIINitrateSolution() );
            add( new CobaltChlorideSolution() );
            add( new PotassiumDichromateSolution() );
            add( new PotassiumChromateSolution() );
            add( new NickelIIChlorideSolution() );
            add( new CopperSulfateSolution() );
            add( new PotassiumPermanganateSolution() );
            add( new PureWater() );
        }};
        this.solution = new Property<BeersLawSolution>( solutions.get( 0 ) );

        double defaultWavelength = 500; //TODO get lambdaMax from solute
        this.light = new Light( new ImmutableVector2D( 1.5, 2 ), false, LightRepresentation.BEAM, defaultWavelength );

        this.cuvette = new Cuvette( new ImmutableVector2D( 3.25, 1.25 ), CUVETTE_WIDTH_RANGE.getDefault(), CUVETTE_HEIGHT, CUVETTE_WIDTH_RANGE );

        //TODO compute drag bounds to match the stage size
        this.detector = new ATDetector( new ImmutableVector2D( 6, 3.75 ), new PBounds( 0, 0, 7.9, 5.25 ),
                                        new ImmutableVector2D( 5.5, 2.5 ), new PBounds( 0, 0, 7.9, 5.25 ) );
    }

    public void reset() {
        solution.reset();
        cuvette.reset();
        detector.reset();
    }

    public ArrayList<BeersLawSolution> getSolutions() {
        return new ArrayList<BeersLawSolution>( solutions );
    }

    public DoubleRange getCuvetteWidthRange() {
        return CUVETTE_WIDTH_RANGE;
    }
}