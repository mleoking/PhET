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

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.reactionsandrates.model.EnergyProfile;
import edu.colorado.phet.reactionsandrates.model.SimpleMolecule;

/**
 * ObservingMoleculeGraphic
 * <p/>
 * Base class used in the spatial and energy views for the graphics for simple molecules
 * <p/>
 * The buffered images used for the PImage nodes are shared between all instances for each
 * type of molecule. This is the flyweight GOF design pattern.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class ObservingMoleculeGraphic extends SimpleMoleculeGraphicNode implements SimpleObserver {
    private final SimpleMolecule molecule;

    public ObservingMoleculeGraphic( SimpleMolecule molecule, EnergyProfile profile ) {
        this( molecule, profile, false );
    }

    /*
     * Constructor that provides option of the graphic being annotated with a letter that
     * indicates the type of molecule.
     */
    public ObservingMoleculeGraphic( SimpleMolecule molecule, EnergyProfile profile, boolean annotate ) {
        super( molecule.getClass(), profile, annotate );

        this.molecule = molecule;

        molecule.addObserver( this );

        update();
    }

    public SimpleMolecule getMolecule() {
        return molecule;
    }
}
