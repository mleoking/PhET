// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.model.Beaker;
import edu.colorado.phet.beerslawlab.model.Dropper;
import edu.colorado.phet.beerslawlab.model.Solute;
import edu.colorado.phet.beerslawlab.model.Solution;
import edu.colorado.phet.beerslawlab.model.Solvent;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Stock solution coming out of dropper.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StockSolutionNode extends PPath {

    // solution inside the dropper, specific to the dropper image file
    private static final DoubleGeneralPath SOLUTION_INSIDE_DROPPER = new DoubleGeneralPath() {{
        final double tipWidth = 15;
        final double tipHeight = 5;
        final double glassWidth = 46;
        final double glassHeight = 150;
        final double glassYOffset = tipHeight + 14;
        moveTo( -tipWidth / 2, 0 );
        lineTo( -tipWidth / 2, -tipHeight );
        lineTo( -glassWidth / 2, -glassYOffset );
        lineTo( -glassWidth / 2, -glassHeight );
        lineTo( glassWidth / 2, -glassHeight );
        lineTo( glassWidth / 2, -glassYOffset );
        lineTo( tipWidth / 2, -tipHeight );
        lineTo( tipWidth / 2, 0 );
        closePath();
    }};

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
        observer.observe( solute, dropper.location, dropper.on, dropper.empty );

        dropper.visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
    }

    private void updateNode() {
        if ( dropper.empty.get() ) {
            setPathTo( new Rectangle2D.Double() );
        }
        else {
            // color
            setPaint( Solution.createColor( solvent, solute.get(), solute.get().stockSolutionConcentration ) );

            // solution outside the dropper
            Rectangle2D solutionOutsideDropper = null;
            if ( dropper.on.get() ) {

                double x = -dropperHoleWidth / 2;
                double y = 0;
                double width = dropperHoleWidth;
                double height = beaker.getY() - dropper.getY();
                solutionOutsideDropper = new Rectangle2D.Double( x, y, width, height );
            }

            // union of inside + outside
            Area area = new Area( SOLUTION_INSIDE_DROPPER.getGeneralPath() );
            if ( solutionOutsideDropper != null ) {
                area.add( new Area( solutionOutsideDropper ) );
            }
            setPathTo( area );

            // move this node to the dropper's location
            setOffset( dropper.location.get().toPoint2D() );
        }
    }
}
