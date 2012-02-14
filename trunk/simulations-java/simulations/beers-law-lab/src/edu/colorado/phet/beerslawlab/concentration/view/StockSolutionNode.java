// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.model.Solute;
import edu.colorado.phet.beerslawlab.concentration.model.Solution;
import edu.colorado.phet.beerslawlab.common.model.Solvent;
import edu.colorado.phet.beerslawlab.concentration.model.Beaker;
import edu.colorado.phet.beerslawlab.concentration.model.Dropper;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Stock solution coming out of dropper.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class StockSolutionNode extends PPath {

    public StockSolutionNode( final Solvent solvent, final Property<Solute> solute, final Dropper dropper, final Beaker beaker, final double tipWidth ) {

        setPickable( false );
        setChildrenPickable( false );
        setStroke( BLLConstants.FLUID_STROKE );

        // shape and location
        RichSimpleObserver observer = new RichSimpleObserver() {
            public void update() {
                // path
                if ( dropper.on.get() && !dropper.empty.get() ) {
                    setPathTo( new Rectangle2D.Double( -tipWidth / 2, 0, tipWidth, beaker.getY() - dropper.getY() ) );
                }
                else {
                    setPathTo( new Rectangle2D.Double() );
                }
                // move this node to the dropper's location
                setOffset( dropper.location.get().toPoint2D() );
            }
        };
        observer.observe( dropper.location, dropper.on, dropper.empty );

        // set color to match solute
        solute.addObserver( new SimpleObserver() {
            public void update() {
                Color color = Solution.createColor( solvent, solute.get(), solute.get().stockSolutionConcentration );
                setPaint( color );
                setStrokePaint( BLLConstants.createFluidStrokeColor( color ) );
            }
        } );

        dropper.visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
    }
}
