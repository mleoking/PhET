// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.*;
import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.beerslaw.model.Light.LightRepresentation;
import edu.colorado.phet.beerslawlab.common.model.Solute;
import edu.colorado.phet.beerslawlab.common.model.Solute.CobaltChloride;
import edu.colorado.phet.beerslawlab.common.model.Solute.CobaltIINitrate;
import edu.colorado.phet.beerslawlab.common.model.Solute.CopperSulfate;
import edu.colorado.phet.beerslawlab.common.model.Solute.KoolAid;
import edu.colorado.phet.beerslawlab.common.model.Solute.NickelIIChloride;
import edu.colorado.phet.beerslawlab.common.model.Solute.PotassiumChromate;
import edu.colorado.phet.beerslawlab.common.model.Solute.PotassiumDichromate;
import edu.colorado.phet.beerslawlab.common.model.Solute.PotassiumPermanganate;
import edu.colorado.phet.beerslawlab.common.model.Solution;
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

    private final ArrayList<Solute> solutes; // the supported set of solutes
    public final Property<Solute> solute; // the selected solute
    public final Solution solution;
    public final Light light;
    public final ModelViewTransform mvt;
    public final Cuvette cuvette;
    public final ATDetector detector;

    public BeersLawModel() {

        // No offset, scale 125x when going from model to view (1cm == 125 pixels)
        this.mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 125 );

        //TODO same set of solutes as ConcentrationModel, move to base class?
        // Solutes, in rainbow (ROYGBIV) order.
        this.solutes = new ArrayList<Solute>() {{
            add( new KoolAid() );
            add( new CobaltIINitrate() );
            add( new CobaltChloride() );
            add( new PotassiumDichromate() );
            add( new PotassiumChromate() );
            add( new NickelIIChloride() );
            add( new CopperSulfate() );
            add( new PotassiumPermanganate() );
        }};
        this.solute = new Property<Solute>( solutes.get( 0 ) );

        this.solution = new Solution( solute, DEFAULT_SOLUTE_AMOUNT, SOLUTION_VOLUME_RANGE.getDefault() );
        solution.soluteAmount.set( 0.1 ); //TODO we need to set concentration directly in this model

        double defaultWavelength = 500; //TODO get lambdaMax from solute
        this.light = new Light( new ImmutableVector2D( 1.5, 2 ), false, LightRepresentation.BEAM, defaultWavelength );

        this.cuvette = new Cuvette( new ImmutableVector2D( 3.25, 1.25 ), CUVETTE_WIDTH_RANGE.getDefault(), CUVETTE_HEIGHT, CUVETTE_WIDTH_RANGE );

        //TODO compute drag bounds to match the stage size
        this.detector = new ATDetector( new ImmutableVector2D( 6, 3.75 ), new PBounds( 0, 0, 7.9, 5.25 ),
                                        new ImmutableVector2D( 5.5, 2.5 ), new PBounds( 0, 0, 7.9, 5.25 ) );
    }

    public void reset() {
        solute.reset();
        solution.reset();
        cuvette.reset();
        detector.reset();
    }

    public ArrayList<Solute> getSolutes() {
        return new ArrayList<Solute>( solutes );
    }

    public DoubleRange getCuvetteWidthRange() {
        return CUVETTE_WIDTH_RANGE;
    }
}