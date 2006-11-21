/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.Profiles;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
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

    //--------------------------------------------------------------------------------------------------
    // Members and methods for getting the paints used for graphics representing different
    // molecules
    //--------------------------------------------------------------------------------------------------

    private static Map moleculeTypeToPaint = new HashMap();

    private static BufferedImage moleculeBCPaintImg;
    private static Paint moleculeBCPaint;

    static {
        try {
            moleculeBCPaintImg = ImageLoader.loadBufferedImage( "images/molecule-bc-paint.gif" );
            moleculeBCPaint = new TexturePaint( moleculeBCPaintImg, new Rectangle( 0, 0,
                                                                                   moleculeBCPaintImg.getWidth(),
                                                                                   moleculeBCPaintImg.getHeight() ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private static BufferedImage moleculeABPaintImg;
    private static Paint moleculeABPaint;

    static {
        try {
            moleculeABPaintImg = ImageLoader.loadBufferedImage( "images/molecule-ab-paint.gif" );
            moleculeABPaint = new TexturePaint( moleculeABPaintImg, new Rectangle( 0, 0,
                                                                                   moleculeABPaintImg.getWidth(),
                                                                                   moleculeABPaintImg.getHeight() ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private static Paint moleculeAPaint = new Color( 220, 220, 80 );
//    private static Paint moleculeBCPaint = new Color( 180, 80, 100 ) ;
    //    private static Paint moleculeABPaint = new Color( 60, 180, 120 );
    private static Paint moleculeCPaint = new Color( 70, 80, 180 );

    static {
        moleculeTypeToPaint.put( MoleculeA.class, moleculeAPaint );
        moleculeTypeToPaint.put( MoleculeBC.class, moleculeBCPaint );
        moleculeTypeToPaint.put( MoleculeAB.class, moleculeABPaint );
        moleculeTypeToPaint.put( MoleculeC.class, moleculeCPaint );
    }

    public static Paint getPaint( Class moleculeType ) {
        return (Paint)moleculeTypeToPaint.get( moleculeType );
    }

    //--------------------------------------------------------------------------------------------------
    // Members and methods for the colors corresponding to different molecules and different
    // energy profiles
    //--------------------------------------------------------------------------------------------------

    /**
     * For MoleculeA
     */
    private static Map mapR1 = new HashMap();
    private static Map mapR2 = new HashMap();
    private static Map mapR3 = new HashMap();
    private static Map mapDYO = new HashMap();

    static {
        mapR1.put( MoleculeA.class, new Color( 0, 200, 0 ) );
        mapR1.put( MoleculeB.class, new Color( 200, 0, 0 ) );
        mapR1.put( MoleculeC.class, new Color( 0, 0, 200 ) );

        mapR2.put( MoleculeA.class, new Color( 0, 200, 200 ) );
        mapR2.put( MoleculeB.class, new Color( 200, 0, 200 ) );
        mapR2.put( MoleculeC.class, new Color( 200, 200, 0 ) );

        mapR3.put( MoleculeA.class, new Color( 200, 30, 0 ) );
        mapR3.put( MoleculeB.class, new Color( 0, 200, 30 ) );
        mapR3.put( MoleculeC.class, new Color( 30, 0, 200 ) );

        mapDYO.put( MoleculeA.class, new Color( 120, 30, 0 ) );
        mapDYO.put( MoleculeB.class, new Color( 30, 0, 120 ) );
        mapDYO.put( MoleculeC.class, new Color( 0, 120, 30 ) );
    }

    private static Map profileToMolecules = new HashMap();

    static {
        profileToMolecules.put( Profiles.R1, mapR1 );
        profileToMolecules.put( Profiles.R2, mapR2 );
        profileToMolecules.put( Profiles.R3, mapR3 );
        profileToMolecules.put( Profiles.DYO, mapDYO );
    }

    public static Color getColor( SimpleMolecule molecule, EnergyProfile profile ) {
        Map colors = (Map)profileToMolecules.get( profile );
        if( colors == null ) {
            throw new RuntimeException( "internal error" );
        }
        Color color = (Color)colors.get( molecule.getClass() );
        return color;
    }


}
