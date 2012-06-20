// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM;

public class MoleculeBucket extends Bucket {
    private final MoleculeShape shape;
    private final Dimension2D size;

    private final List<Molecule> molecules = new ArrayList<Molecule>();

    public MoleculeBucket( final MoleculeShape shape, int initialQuantity ) {
        this( shape, new PDimension( MODEL_VIEW_TRANSFORM.modelToViewDeltaX( shape.getBoundingCircleRadius() * 0.7 * ChemicalReactionsConstants.THIS_CONSTANT_SHOULD_NOT_EXIST + 200 ), 200 ), initialQuantity );
    }

    public MoleculeBucket( final MoleculeShape shape, Dimension2D size, int initialQuantity ) {
        super( new Point2D.Double(), size, Color.GRAY, "" );
        this.shape = shape;
        this.size = size;

        FunctionalUtils.repeat( new Runnable() {
            public void run() {
                molecules.add( new Molecule( shape ) );
            }
        }, initialQuantity );
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

    @Override public void setPosition( Point2D position ) {
        super.setPosition( position );

        for ( Molecule molecule : molecules ) {
            molecule.setPosition( new ImmutableVector2D( getPosition().getX(), getPosition().getY() ) );
        }
    }
}
