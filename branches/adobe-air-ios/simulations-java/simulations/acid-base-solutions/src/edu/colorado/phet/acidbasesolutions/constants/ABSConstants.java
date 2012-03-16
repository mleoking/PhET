// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.constants;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSConstants {

    /* Not intended for instantiation. */
    private ABSConstants() {
    }

    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------

    public static final String PROJECT = "acid-base-solutions";
    public static final String FLAVOR_MAGNIFYING_GLASS_PROTOTYPE = "magnifying-glass-prototype";

    //----------------------------------------------------------------------------
    // View
    //----------------------------------------------------------------------------

    // reference coordinate frame size for world nodes
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 900, 900 );

    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------

    public static final int MIN_PH = 0;
    public static final int MAX_PH = 14;

    public static final double WATER_CONCENTRATION = 55.6; // water concentration when it's used as a solvent, mol/L
    public static final double WATER_EQUILIBRIUM_CONSTANT = 1E-14;

    public static final DoubleRange CONCENTRATION_RANGE = new DoubleRange( 1E-3, 1, 1E-2 );
    public static final DoubleRange WEAK_STRENGTH_RANGE = new DoubleRange( 1E-10, 1E2, 1E-7 );
    public static final double STRONG_STRENGTH = WEAK_STRENGTH_RANGE.getMax() + 1; // arbitrary, but needs to be greater than weak range

    // beaker, origin at bottom center - all other locations are specified relative to the beaker location
    public static final PDimension BEAKER_SIZE = new PDimension( 700, 540 );
    public static final Point2D BEAKER_LOCATION = new Point2D.Double( 0, 0 );

    // pH meter, origin at tip of probe
    public static final Point2D PH_METER_LOCATION = new Point2D.Double( BEAKER_LOCATION.getX() + ( 0.30 * BEAKER_SIZE.getWidth() ), BEAKER_LOCATION.getY() - BEAKER_SIZE.getHeight() - 15 );
    public static final boolean PH_METER_VISIBLE = true;
    public static final PDimension PH_METER_SHAFT_SIZE = new PDimension( 10, 100 );
    public static final PDimension PH_METER_TIP_SIZE = new PDimension( 25, 60 );

    // pH paper, origin at top center
    public static final PDimension PH_PAPER_SIZE = new PDimension( 30, 220 );
    public static final Point2D PH_PAPER_LOCATION = new Point2D.Double( BEAKER_LOCATION.getX() + 170, BEAKER_LOCATION.getY() - BEAKER_SIZE.getHeight() - PH_PAPER_SIZE.getHeight() - 15 );
    public static final boolean PH_PAPER_VISIBLE = false;

    // pH color key, origin at upper left
    public static final Point2D PH_COLOR_KEY_LOCATION = new Point2D.Double( BEAKER_LOCATION.getX() - ( BEAKER_SIZE.getWidth() / 2 ) + 55, PH_PAPER_LOCATION.getY() );

    // conductivity tester, origin at tip of light bulb
    public static final PDimension CONDUCTIVITY_TESTER_PROBE_SIZE = new PDimension( 30, 100 );
    public static final Point2D CONDUCTIVITY_TESTER_LOCATION = new Point2D.Double( BEAKER_LOCATION.getX() - 70, BEAKER_LOCATION.getY() - BEAKER_SIZE.getHeight() - 70 );
    public static final Point2D CONDUCTIVITY_TESTER_POSITIVE_PROBE_LOCATION = new Point2D.Double( CONDUCTIVITY_TESTER_LOCATION.getX() + 230, BEAKER_LOCATION.getY() - BEAKER_SIZE.getHeight() - 20 );
    public static final Point2D CONDUCTIVITY_TESTER_NEGATIVE_PROBE_LOCATION = new Point2D.Double( CONDUCTIVITY_TESTER_LOCATION.getX() - 95, BEAKER_LOCATION.getY() - BEAKER_SIZE.getHeight() - 20 );
    public static final boolean CONDUCTIVITY_TESTER_VISIBLE = false;

    // magnifying glass, origin at center of glass
    public static final Point2D MAGNIFYING_GLASS_LOCATION = new Point2D.Double( BEAKER_LOCATION.getX(), BEAKER_LOCATION.getY() - ( BEAKER_SIZE.getHeight() / 2 ) );
    public static final boolean MAGNIFYING_GLASS_VISIBLE = true;
    public static final double MAGNIFYING_GLASS_DIAMETER = 500;
    public static final boolean MAGNIFYING_GLASS_WATER_VISIBLE = false;

    // concentration graph, origin at center of data area
    public static final Point2D CONCENTRATION_GRAPH_LOCATION = new Point2D.Double( BEAKER_LOCATION.getX(), BEAKER_LOCATION.getY() - ( BEAKER_SIZE.getHeight() / 2 ) );
    public static final boolean CONCENTRATION_GRAPH_VISIBLE = false;
    public static final PDimension CONCENTRATION_GRAPH_SIZE = new PDimension( 360, BEAKER_SIZE.getHeight() - 50 );

    // reaction equation, origin at top center
    public static final Point2D REACTION_EQUATION_LOCATION = new Point2D.Double( BEAKER_LOCATION.getX(), BEAKER_LOCATION.getY() + 10 );
}
