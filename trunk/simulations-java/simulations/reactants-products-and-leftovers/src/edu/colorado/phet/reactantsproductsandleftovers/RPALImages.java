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
    public static final BufferedImage BREAD = RPALResources.getImage( "sandwich/bread.png" );
    public static final BufferedImage CHEESE = RPALResources.getImage( "sandwich/cheese.png" );
    public static final BufferedImage MEAT = RPALResources.getImage( "sandwich/meat.png" );
    
    // all molecule images (used to populate game "reward"), created programmatically
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
}
