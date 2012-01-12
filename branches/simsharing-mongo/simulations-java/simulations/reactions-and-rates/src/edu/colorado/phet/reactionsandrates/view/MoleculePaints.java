// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.view;

import edu.colorado.phet.common.phetcommon.view.util.ColorFilter;
import edu.colorado.phet.common.phetcommon.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.reactionsandrates.model.*;
import edu.colorado.phet.reactionsandrates.model.reactions.Profiles;
import edu.colorado.phet.reactionsandrates.view.factories.TextureImageFactory;

import org.apache.commons.collections.keyvalue.MultiKey;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * MoleculePaints
 * <p/>
 * This is a class of static members that provides paints to be used for various
 * graphics representing different molecules. Examples of uses are in bar charts,
 * pie charts and strip charts.
 * <p/>
 * This class also has the colors used to modify molecule graphics for different
 * reactions (i.e., energy profiles) using the MakeDuotoneImageOp.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculePaints {
    private static final int LINE_WIDTH = 3;
    //--------------------------------------------------------------------------------------------------
    // Members and methods for getting the paints used for graphics representing different
    // molecules
    //--------------------------------------------------------------------------------------------------

    private static Color moleculeAPaint = new Color( 220, 220, 80 );
    private static Color moleculeBPaint = new Color( 200, 120, 110 );
    private static Color moleculeCPaint = new Color( 70, 80, 180 );

    private static Map cpToPaint = new HashMap();

    public static Paint getPaint( Class moleculeType ) {
        return getPaint( moleculeType, Profiles.DEFAULT );
    }

    public static Paint getPaint( Class moleculeType, EnergyProfile profile ) {
        MultiKey cacheKey = key( moleculeType, profile );

        Paint paint = (Paint)cpToPaint.get( cacheKey );

        if( paint != null ) {
            return paint;
        }

        ColorFilter filterA = ColorFilter.NULL,
                filterB = ColorFilter.NULL,
                filterC = ColorFilter.NULL;

        if( profile != Profiles.DEFAULT ) {
            filterA = new MakeDuotoneImageOp( getDuotoneHue( MoleculeA.class, profile ) );
            filterB = new MakeDuotoneImageOp( getDuotoneHue( MoleculeB.class, profile ) );
            filterC = new MakeDuotoneImageOp( getDuotoneHue( MoleculeC.class, profile ) );
        }

        Color paintA = filterA.filter( moleculeAPaint ),
                paintB = filterB.filter( moleculeBPaint ),
                paintC = filterC.filter( moleculeCPaint );

        if( moleculeType == MoleculeA.class ) {
            return paintA;
        }
        else if( moleculeType == MoleculeB.class ) {
            paint = paintB;
        }
        else if( moleculeType == MoleculeC.class ) {
            paint = paintC;
        }
        else if( moleculeType == MoleculeAB.class ) {
            paint = TextureImageFactory.createTexturePaint( paintA, paintB, LINE_WIDTH );
        }
        else if( moleculeType == MoleculeBC.class ) {
            paint = TextureImageFactory.createTexturePaint( paintB, paintC, LINE_WIDTH );
        }
        else {
            assert false : "A new molecule class type was added; how to generate the paint for said type is not known.";

            paint = Color.BLACK;
        }

        cpToPaint.put( key( moleculeType, profile ), paint );

        return paint;
    }

    //--------------------------------------------------------------------------------------------------
    // Members and methods for the colors corresponding to different molecules and different
    // energy profiles
    //--------------------------------------------------------------------------------------------------

    private static Map cpToColor = new HashMap();

    static {
        cpToColor.put( key( MoleculeA.class, Profiles.R1 ), new Color( 0, 150, 0 ) );
        cpToColor.put( key( MoleculeB.class, Profiles.R1 ), new Color( 150, 0, 0 ) );
        cpToColor.put( key( MoleculeC.class, Profiles.R1 ), new Color( 0, 0, 250 ) );

        cpToColor.put( key( MoleculeA.class, Profiles.R2 ), new Color( 0, 100, 250 ) );
        cpToColor.put( key( MoleculeB.class, Profiles.R2 ), new Color( 250, 0, 250 ) );
        cpToColor.put( key( MoleculeC.class, Profiles.R2 ), new Color( 150, 150, 0 ) );

        cpToColor.put( key( MoleculeA.class, Profiles.R3 ), new Color( 200, 60, 0 ) );
        cpToColor.put( key( MoleculeB.class, Profiles.R3 ), new Color( 0, 150, 0 ) );
        cpToColor.put( key( MoleculeC.class, Profiles.R3 ), new Color( 60, 0, 250 ) );

        cpToColor.put( key( MoleculeA.class, Profiles.DYO ), new Color( 120, 30, 0 ) );
        cpToColor.put( key( MoleculeB.class, Profiles.DYO ), new Color( 30, 0, 120 ) );
        cpToColor.put( key( MoleculeC.class, Profiles.DYO ), new Color( 0, 120, 30 ) );
    }

    public static Color getDuotoneHue( Class moleculeType, EnergyProfile profile ) {
        Color color = (Color)cpToColor.get( new MultiKey( moleculeType, profile ) );

        if( color == null ) {
            throw new InternalError( "A duotone hue for the molecule " + moleculeType.getName() + " and the profile " + profile + " was not found." );
        }

        return color;
    }

    public static Color getDuotoneHue( SimpleMolecule molecule, EnergyProfile profile ) {
        return getDuotoneHue( molecule.getClass(), profile );
    }

    private static MultiKey key( Class moleculeType, EnergyProfile profile ) {
        return new MultiKey( moleculeType, profile );
    }
}
