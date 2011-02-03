// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations;

import java.awt.Image;

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

    // molecules
    public static final Image CH4 = new CH4Node().toImage();
    public static final Image CO2 = new CO2Node().toImage();
    public static final Image H2 = new H2Node().toImage();
    public static final Image H2O = new H2ONode().toImage();
    public static final Image N2 = new N2Node().toImage();
    public static final Image NH3 = new NH3Node().toImage();
    public static final Image O2 = new O2Node().toImage();

    // atoms
    public static final Image C = new AtomNode( BCEColors.C ).toImage();
    public static final Image H = new AtomNode( BCEColors.H ).toImage();
    public static final Image N = new AtomNode( BCEColors.N ).toImage();
    public static final Image O = new AtomNode( BCEColors.O ).toImage();
    public static final Image S = new AtomNode( BCEColors.S ).toImage();
}
