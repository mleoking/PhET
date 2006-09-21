package edu.colorado.phet.waveinterference;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.RotationGlyph;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Aug 23, 2006
 * Time: 9:16:43 PM
 * Copyright (c) Aug 23, 2006 by Sam Reid
 */

public class ThisSideUpWrapper extends PhetPNode {
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private Lattice2D lattice2D;
    private ThisSideUpGraphic thisSideUpGraphic;
    private RotationGlyph rotationGlyph;

    public ThisSideUpWrapper( final RotationGlyph rotationGlyph, LatticeScreenCoordinates latticeScreenCoordinates, Lattice2D lattice2D ) {
        super();
        this.rotationGlyph = rotationGlyph;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        this.lattice2D = lattice2D;
        thisSideUpGraphic = new ThisSideUpGraphic();
        addChild( thisSideUpGraphic );
        rotationGlyph.addListener( new RotationGlyph.Listener() {
            public void angleChanged() {
                update();
            }
        } );
        update();
    }

    public void update() {
        thisSideUpGraphic.setAngle( rotationGlyph.getAngle() );
        Point2D point = getLatticeScreenCoordinates().toScreenCoordinates( getLattice().getWidth(), getLattice().getHeight() / 2 );
        point = new Point2D.Double( point.getX() - ThisSideUpWrapper.this.getFullBounds().getWidth() * 2, point.getY() );
        ThisSideUpWrapper.this.setOffset( point );
    }

    private Lattice2D getLattice() {
        return lattice2D;
    }

    private LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return latticeScreenCoordinates;
    }
}
