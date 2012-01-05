// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.model.Beaker;
import edu.colorado.phet.beerslawlab.model.Dropper;
import edu.colorado.phet.beerslawlab.model.Solute;
import edu.colorado.phet.beerslawlab.model.Solution;
import edu.colorado.phet.beerslawlab.model.Solvent;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Stock solution coming out of dropper.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StockSolutionNode extends PPath {

    private final Solvent solvent;
    private final Property<Solute> solute;
    private final Dropper dropper;
    private final Beaker beaker;
    private final double dropperHoleWidth;

    public StockSolutionNode( Solvent solvent, Property<Solute> solute, Dropper dropper, Beaker beaker, double dropperHoleWidth ) {
        setPickable( false );
        setChildrenPickable( false );
        setStroke( null );

        this.solvent = solvent;
        this.solute = solute;
        this.dropper = dropper;
        this.beaker = beaker;
        this.dropperHoleWidth = dropperHoleWidth;

        RichSimpleObserver observer = new RichSimpleObserver() {
            public void update() {
                updateNode();
            }
        };
        observer.observe( solute, dropper.location, dropper.on );
    }

    private void updateNode() {
        // color
        setPaint( Solution.createColor( solvent, solute.get(), solute.get().stockSolutionConcentration ) );
        // shape
        if ( dropper.on.get() ) {
            double x = dropper.getX() - ( dropperHoleWidth / 2 );
            double y = dropper.getY();
            double width = dropperHoleWidth;
            double height = beaker.getY() - dropper.getY();
            setPathTo( new Rectangle2D.Double( x, y, width, height ) );
        }
        else {
            setPathTo( new Rectangle2D.Double() ); // empty rectangle
        }
    }
}
