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
import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.MoleculeAB;
import edu.colorado.phet.molecularreactions.model.MoleculeBC;
import edu.colorado.phet.molecularreactions.model.MoleculeC;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * MoleculePaints
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculePaints {
    private static Map moleculeTypeToPaint = new HashMap();

    private static BufferedImage moleculeBCPaintImg;
    private static Paint moleculeBCPaint;
    static {
        try {
            moleculeBCPaintImg = ImageLoader.loadBufferedImage( "images/molecule-bc-paint.gif");
            moleculeBCPaint = new TexturePaint( moleculeBCPaintImg, new Rectangle( 0,0,
                                                                                   moleculeBCPaintImg.getWidth(),
                                                                                   moleculeBCPaintImg.getHeight()) ) ;
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private static BufferedImage moleculeABPaintImg;
    private static Paint moleculeABPaint;
    static {
        try {
            moleculeABPaintImg = ImageLoader.loadBufferedImage( "images/molecule-AB-paint.gif");
            moleculeABPaint = new TexturePaint( moleculeABPaintImg, new Rectangle( 0,0,
                                                                                   moleculeABPaintImg.getWidth(),
                                                                                   moleculeABPaintImg.getHeight()) ) ;
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private static Paint moleculeAPaint = new Color( 180, 180, 0 );
//    private static Paint moleculeBCPaint = new Color( 180, 80, 100 ) ;
//    private static Paint moleculeABPaint = new Color( 60, 180, 120 );
    private static Paint moleculeCPaint = new Color( 60, 10, 180 );
    static {
        moleculeTypeToPaint.put( MoleculeA.class, moleculeAPaint );
        moleculeTypeToPaint.put( MoleculeBC.class, moleculeBCPaint );
        moleculeTypeToPaint.put( MoleculeAB.class, moleculeABPaint );
        moleculeTypeToPaint.put( MoleculeC.class, moleculeCPaint );
    }

    public static Paint getPaint( Class moleculeType ) {
        return (Paint)moleculeTypeToPaint.get( moleculeType );
    }
}
