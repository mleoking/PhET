// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.model.Solvent;
import edu.colorado.phet.beerslawlab.concentration.model.Beaker;
import edu.colorado.phet.beerslawlab.concentration.model.ConcentrationSolution;
import edu.colorado.phet.beerslawlab.concentration.model.Dropper;
import edu.colorado.phet.beerslawlab.concentration.model.Solute;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Stock solution coming out of the dropper.
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
                    setPathTo( new Rectangle2D.Double( -tipWidth / 2, 0, tipWidth, beaker.location.getY() - dropper.getY() ) );
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
        final RichSimpleObserver colorObserver = new RichSimpleObserver() {
            public void update() {
                Color color = ConcentrationSolution.createColor( solvent, solute.get(), solute.get().stockSolutionConcentration );
                setPaint( color );
                setStrokePaint( BLLConstants.createFluidStrokeColor( color ) );
            }
        };
        colorObserver.observe( solute, solute.get().colorScheme );

        // rewire to a different color scheme when the solute changes
        solute.addObserver( new ChangeObserver<Solute>() {
            public void update( Solute newValue, Solute oldValue ) {
                if ( oldValue != null ) {
                    oldValue.colorScheme.removeObserver( colorObserver );
                }
                newValue.colorScheme.addObserver( colorObserver );
            }
        } );

        // hide this node when the dropper is invisible
        dropper.visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
    }
}
