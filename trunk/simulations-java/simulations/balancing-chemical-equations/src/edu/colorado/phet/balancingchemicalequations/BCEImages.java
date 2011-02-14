// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations;

import java.awt.Image;

import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.SmallAtomNode;
import edu.colorado.phet.balancingchemicalequations.view.molecules.*;

/**
 * Images used in this project.
 * Statically loaded so we can easily see if any are missing.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCEImages {

    /* not intended for instantiation */
    private BCEImages() {}

    // atoms
    public static final Image C = new SmallAtomNode( BCEColors.C ).toImage();
    public static final Image Cl = new SmallAtomNode( BCEColors.Cl ).toImage();
    public static final Image F = new SmallAtomNode( BCEColors.F ).toImage();
    public static final Image H = new SmallAtomNode( BCEColors.H ).toImage();
    public static final Image N = new SmallAtomNode( BCEColors.N ).toImage();
    public static final Image O = new SmallAtomNode( BCEColors.O ).toImage();
    public static final Image P = new SmallAtomNode( BCEColors.P ).toImage();
    public static final Image S = new SmallAtomNode( BCEColors.S ).toImage();

    // molecules
    public static final Image CH4 = new CH4Node().toImage();
    public static final Image CO2 = new CO2Node().toImage();
    public static final Image F2 = new F2Node().toImage();
    public static final Image H2 = new H2Node().toImage();
    public static final Image H2O = new H2ONode().toImage();
    public static final Image HF = new HFNode().toImage();
    public static final Image N2 = new N2Node().toImage();
    public static final Image NH3 = new NH3Node().toImage();
    public static final Image O2 = new O2Node().toImage();
}
