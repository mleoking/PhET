// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.BLLResources.Symbols;
import edu.colorado.phet.beerslawlab.model.Dropper;
import edu.colorado.phet.beerslawlab.model.Shaker;
import edu.colorado.phet.beerslawlab.model.Solute;
import edu.colorado.phet.beerslawlab.model.SoluteForm;
import edu.colorado.phet.beerslawlab.model.Solution;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * Model for the "Concentration" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationModel implements Resettable {

    private static final DoubleRange SOLUTE_AMOUNT_RANGE = new DoubleRange( 0, 1, 0.5 ); // moles
    private static final DoubleRange SOLUTION_VOLUME_RANGE = new DoubleRange( 0, 1, 0.5 ); // liters

    static {
        assert ( SOLUTE_AMOUNT_RANGE.getMin() >= 0 );
        assert ( SOLUTION_VOLUME_RANGE.getMin() >= 0 );
    }

    private final ArrayList<Solute> solutes; // the supported set of solutes
    public final Property<Solute> solute; // the selected solute
    public final Solution solution;
    public final Property<SoluteForm> soluteForm = new Property<SoluteForm>( SoluteForm.SOLID );
    public final Shaker shaker;
    public final Dropper dropper;

    public ConcentrationModel() {

        // Solutes, in rainbow (ROYBIV) order. Values are specified in the design document.
        this.solutes = new ArrayList<Solute>() {{
            final int precipitateParticlesPerMole = 200;
            add( new Solute( Strings.KOOL_AID, Symbols.KOOL_AID, 342.296, 5.96, 5.50, Color.RED, 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.COBALT_II_NITRATE, Symbols.COBALT_II_NITRATE, 182.942, 5.64, 5.0, Color.RED, 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.COBALT_CHLORIDE, Symbols.COBALT_CHLORIDE, 129.839, 4.33, 4.0, new Color( 0xFF6A6A ) /* rose pink */, 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.POTASSIUM_DICHROMATE, Symbols.POTASSIUM_DICHROMATE, 294.185, 0.51, 0.50, new Color( 0xFF7F00 ) /* orange */, 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.POTASSIUM_CHROMATE, Symbols.POTASSIUM_CHROMATE, 194.191, 3.35, 3.0, Color.YELLOW, 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.NICKEL_II_CHLORIDE, Symbols.NICKEL_II_CHLORIDE, 129.599, 5.21, 5.0, new Color( 0x008000 ) /* green */, 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.COPPER_SULFATE, Symbols.COPPER_SULFATE, 159.609, 1.38, 1.0, new Color( 0x1E90FF ) /* blue */, 5, precipitateParticlesPerMole ) );
            add( new Solute( Strings.POTASSIUM_PERMANGANATE, Symbols.POTASSIUM_PERMANGANATE, 158.034, 0.48, 0.4, new Color( 0x8B008B ) /* purple */, Color.BLACK, 5, precipitateParticlesPerMole ) );
        }};
        this.solute = new Property<Solute>( solutes.get( 0 ) );
        this.solution = new Solution( solute, SOLUTE_AMOUNT_RANGE.getDefault(), SOLUTION_VOLUME_RANGE.getDefault() );
        this.shaker = new Shaker( new ImmutableVector2D( 100, 100 ), new Rectangle2D.Double( 10, 10, 400, 200 ), solute );
        this.dropper = new Dropper( new ImmutableVector2D( 100, 100 ), new Rectangle2D.Double( 10, 10, 400, 200 ), solute );
    }

    public ArrayList<Solute> getSolutes() {
        return new ArrayList<Solute>( solutes );
    }

    public DoubleRange getSoluteAmountRange() {
        return SOLUTE_AMOUNT_RANGE;
    }

    public DoubleRange getSolutionVolumeRange() {
        return SOLUTION_VOLUME_RANGE;
    }

    public void reset() {
        solution.reset();
        soluteForm.reset();
    }
}
