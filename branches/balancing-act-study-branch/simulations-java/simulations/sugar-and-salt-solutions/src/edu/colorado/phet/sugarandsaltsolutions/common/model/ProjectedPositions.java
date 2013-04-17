// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SphericalParticle.Carbon;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SphericalParticle.Hydrogen;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SphericalParticle.NeutralOxygen;

/**
 * Provides physical locations (positions) of the atoms within a molecule.
 * Positions sampled from a 2d rasterized view from JMol with ProjectorUtil
 * <p/>
 *
 * @author Sam Reid
 */
public class ProjectedPositions {

    //Raw text to be parsed
    private final String text;

    //Conversion factor from pixels to model units (meters)
    private final double scale;

    public ProjectedPositions( String text, double scale ) {
        this.text = text;
        this.scale = scale;
    }

    //Data structure that has the type of the atom, its element identifier and the position in model space
    public static abstract class AtomPosition {
        public final String type;
        public final Vector2D position;

        AtomPosition( String type, Vector2D position ) {
            this.type = type;
            this.position = position;
        }

        //Create the SphericalParticle corresponding to this atom type that can be used in the model
        public abstract SphericalParticle createConstituent();
    }

    //Get the positions for a specific list of atom types.
    public ArrayList<AtomPosition> getAtoms() {
        ArrayList<AtomPosition> list = new ArrayList<AtomPosition>();
        StringTokenizer stringTokenizer = new StringTokenizer( text, "\n" );

        //Iterate over the list and convert each line to an atom instance
        while ( stringTokenizer.hasMoreTokens() ) {
            list.add( parseAtom( stringTokenizer.nextToken() ) );
        }
        return list;
    }

    //Reads a line from the string and converts to an Atom instance at the right model location
    private AtomPosition parseAtom( String line ) {
        StringTokenizer st = new StringTokenizer( line, ", " );

        //Read the type and location
        String type = st.nextToken();
        double x = Double.parseDouble( st.nextToken() );
        double y = Double.parseDouble( st.nextToken() );

        //For showing partial charges on sucrose, read from the file from certain atoms that have a charge
        //http://www.chemistryland.com/CHM130W/LabHelp/Experiment10/Exp10.html
        String charge = "";
        if ( st.hasMoreTokens() ) {
            charge = st.nextToken();
        }

        //Add an atom instance based on the type, location and partial charge (if any)
        final String finalCharge = charge;
        return new AtomPosition( type, toModel( new Vector2D( x, y ) ) ) {
            @Override public SphericalParticle createConstituent() {
                if ( type.equals( "H" ) ) {
                    return new Hydrogen() {
                        @Override public double getPartialChargeDisplayValue() {
                            if ( finalCharge.equals( "charge" ) ) { return super.getPartialChargeDisplayValue(); }
                            else { return 0.0; }
                        }
                    };
                }
                if ( type.equals( "C" ) ) {
                    return new Carbon() {
                        @Override public double getPartialChargeDisplayValue() {

                            //All the charged carbons have a partial positive charge, see http://www.chemistryland.com/CHM130W/LabHelp/Experiment10/Exp10.html
                            if ( finalCharge.equals( "charge" ) ) { return 1.0; }
                            else { return 0.0; }
                        }
                    };
                }
                if ( type.equals( "O" ) ) {
                    return new NeutralOxygen() {
                        @Override public double getPartialChargeDisplayValue() {
                            if ( finalCharge.equals( "charge" ) ) { return super.getPartialChargeDisplayValue(); }
                            else { return 0.0; }
                        }
                    };
                }
                throw new RuntimeException();
            }
        };
    }

    //Use the position of the first atom as the origin, which other positions will be based on.  Origin is in pixel coordinates and converted to model coordinates in toModel
    public Vector2D getOrigin() {
        StringTokenizer st = new StringTokenizer( text.substring( 0, text.indexOf( '\n' ) ), ", " );

        //Throw away the type
        st.nextToken();

        //Read the position
        return new Vector2D( Double.parseDouble( st.nextToken() ), Double.parseDouble( st.nextToken() ) );
    }

    private Vector2D toModel( Vector2D position ) {
        return position.minus( getOrigin() ).times( scale );
    }
}