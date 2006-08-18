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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.MoleculeB;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.Selectable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;

/**
 * SimpleMoleculeGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpatialSimpleMoleculeGraphic extends AbstractSimpleMoleculeGraphic {

    public SpatialSimpleMoleculeGraphic( SimpleMolecule molecule ) {
        super( molecule );
    }

    public void update() {
        setOffset( getMolecule().getPosition() );
    }
}
