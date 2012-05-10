// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.Bucket;

public class MoleculeBucket extends Bucket {
    private final MoleculeShape shape;
    private final Dimension2D size;

    private final List<Molecule> molecules = new ArrayList<Molecule>();

    public MoleculeBucket( MoleculeShape shape, Dimension2D size ) {
        super( new Point2D.Double(), size, Color.GRAY, "" );
        this.shape = shape;
        this.size = size;
    }

    public MoleculeShape getShape() {
        return shape;
    }

    public List<Molecule> getMolecules() {
        return molecules;
    }

    public List<Atom> getAtoms() {
        List<Atom> result = new ArrayList<Atom>();
        for ( Molecule molecule : molecules ) {
            result.addAll( molecule.getAtoms() );
        }
        return result;
    }

    public Dimension2D getSize() {
        return size;
    }
}
