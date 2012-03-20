// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers;

import java.awt.Image;
import java.awt.image.BufferedImage;

import edu.colorado.phet.chemistry.molecules.C2H2Node;
import edu.colorado.phet.chemistry.molecules.C2H4Node;
import edu.colorado.phet.chemistry.molecules.C2H5ClNode;
import edu.colorado.phet.chemistry.molecules.C2H5OHNode;
import edu.colorado.phet.chemistry.molecules.C2H6Node;
import edu.colorado.phet.chemistry.molecules.CH2ONode;
import edu.colorado.phet.chemistry.molecules.CH3OHNode;
import edu.colorado.phet.chemistry.molecules.CH4Node;
import edu.colorado.phet.chemistry.molecules.H2ONode;
import edu.colorado.phet.chemistry.molecules.H2SNode;
import edu.colorado.phet.chemistry.molecules.HClNode;
import edu.colorado.phet.chemistry.molecules.HFNode;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.CNode;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.CO2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.CONode;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.CS2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.Cl2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.F2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.H2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.N2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.N2ONode;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.NONode;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.O2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.SNode;
import edu.colorado.phet.chemistry.molecules.NH3Node;
import edu.colorado.phet.chemistry.molecules.NO2Node;
import edu.colorado.phet.chemistry.molecules.OF2Node;
import edu.colorado.phet.chemistry.molecules.P4Node;
import edu.colorado.phet.chemistry.molecules.PCl3Node;
import edu.colorado.phet.chemistry.molecules.PCl5Node;
import edu.colorado.phet.chemistry.molecules.PF3Node;
import edu.colorado.phet.chemistry.molecules.PH3Node;
import edu.colorado.phet.chemistry.molecules.SO2Node;
import edu.colorado.phet.chemistry.molecules.SO3Node;

/**
 * Images used in this project.
 * Statically loaded so we can easily see if any are missing.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALImages {

    private RPALImages() {}

    // sandwich images
    public static final BufferedImage BREAD = getSandwichImage( "bread.png" );
    public static final BufferedImage CHEESE = getSandwichImage( "cheese.png" );
    public static final BufferedImage MEAT = getSandwichImage( "meat.png" );
    
    // molecule images
    public static final BufferedImage C = getMoleculeImage( "C.png" );
    public static final BufferedImage C2H2 = getMoleculeImage( "C2H2.png" );
    public static final BufferedImage C2H4 = getMoleculeImage( "C2H4.png" );
    public static final BufferedImage C2H5Cl = getMoleculeImage( "C2H5Cl.png" );
    public static final BufferedImage C2H5OH = getMoleculeImage( "C2H5OH.png" );
    public static final BufferedImage C2H6 = getMoleculeImage( "C2H6.png" );
    public static final BufferedImage CH2O = getMoleculeImage( "CH2O.png" );
    public static final BufferedImage CH3OH = getMoleculeImage( "CH3OH.png" );
    public static final BufferedImage CH4 = getMoleculeImage( "CH4.png" );
    public static final BufferedImage CO = getMoleculeImage( "CO.png" );
    public static final BufferedImage CO2 = getMoleculeImage( "CO2.png" );
    public static final BufferedImage CS2 = getMoleculeImage( "CS2.png" );
    public static final BufferedImage Cl2 = getMoleculeImage( "Cl2.png" );
    public static final BufferedImage F2 = getMoleculeImage( "F2.png" );
    public static final BufferedImage H2 = getMoleculeImage( "H2.png" );
    public static final BufferedImage H2O = getMoleculeImage( "H2O.png" );
    public static final BufferedImage H2S = getMoleculeImage( "H2S.png" );
    public static final BufferedImage HCl = getMoleculeImage( "HCl.png" );
    public static final BufferedImage HF = getMoleculeImage( "HF.png" );
    public static final BufferedImage N2 = getMoleculeImage( "N2.png" );
    public static final BufferedImage N2O = getMoleculeImage( "N2O.png" );
    public static final BufferedImage NH3 = getMoleculeImage( "NH3.png" );
    public static final BufferedImage NO = getMoleculeImage( "NO.png" );
    public static final BufferedImage NO2 = getMoleculeImage( "NO2.png" );
    public static final BufferedImage O2 = getMoleculeImage( "O2.png" );
    public static final BufferedImage OF2 = getMoleculeImage( "OF2.png" );
    public static final BufferedImage P4 = getMoleculeImage( "P4.png" );
    public static final BufferedImage PCl3 = getMoleculeImage( "PCl3.png" );
    public static final BufferedImage PCl5 = getMoleculeImage( "PCl5.png" );
    public static final BufferedImage PF3 = getMoleculeImage( "PF3.png" );
    public static final BufferedImage PH3 = getMoleculeImage( "PH3.png" );
    public static final BufferedImage S = getMoleculeImage( "S.png" );
    public static final BufferedImage SO2 = getMoleculeImage( "SO2.png" );
    public static final BufferedImage SO3 = getMoleculeImage( "SO3.png" );
    
    // all molecule images, used in game
    public static final Image[] ALL_MOLECULES = {
            new CNode().toImage(),
            new C2H2Node().toImage(),
            new C2H4Node().toImage(),
            new C2H5ClNode().toImage(),
            new C2H5ClNode().toImage(),
            new C2H5OHNode().toImage(),
            new C2H6Node().toImage(),
            new CH2ONode().toImage(),
            new CH3OHNode().toImage(),
            new CH4Node().toImage(),
            new CONode().toImage(),
            new CO2Node().toImage(),
            new CS2Node().toImage(),
            new Cl2Node().toImage(),
            new F2Node().toImage(),
            new H2Node().toImage(),
            new H2ONode().toImage(),
            new H2SNode().toImage(),
            new HClNode().toImage(),
            new HFNode().toImage(),
            new N2Node().toImage(),
            new N2ONode().toImage(),
            new NH3Node().toImage(),
            new NONode().toImage(),
            new NO2Node().toImage(),
            new O2Node().toImage(),
            new OF2Node().toImage(),
            new P4Node().toImage(),
            new PCl3Node().toImage(),
            new PCl5Node().toImage(),
            new PF3Node().toImage(),
            new PH3Node().toImage(),
            new SNode().toImage(),
            new SO2Node().toImage(),
            new SO3Node().toImage()
    };
    
    private static final BufferedImage getSandwichImage( String name ) {
        return RPALResources.getImage( "sandwich/" + name );
    }
    
    private static final BufferedImage getMoleculeImage( String name ) {
        return RPALResources.getImage( "molecules/" + name );
    }
}
