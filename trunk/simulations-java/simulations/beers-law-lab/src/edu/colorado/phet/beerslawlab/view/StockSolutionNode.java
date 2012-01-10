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

        dropper.visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
    }

    private void updateNode() {

        // color
        setPaint( Solution.createColor( solvent, solute.get(), solute.get().stockSolutionConcentration ) );

        // solution inside the dropper
        DoubleGeneralPath solutionInsideDropper = new DoubleGeneralPath() {{
            // These constants are specific to the dropper image file.
            final double tipWidth = 16;
            final double tipHeight = 10;
            final double glassWidth = 50;
            final double glassHeight = 150;
            final double glassYOffset = tipHeight + 10;
            moveTo( dropper.getX() - tipWidth / 2, dropper.getY() );
            lineTo( dropper.getX() - tipWidth / 2, dropper.getY() - tipHeight );
            lineTo( dropper.getX() - glassWidth / 2, dropper.getY() - glassYOffset );
            lineTo( dropper.getX() - glassWidth / 2, dropper.getY() - glassHeight );
            lineTo( dropper.getX() + glassWidth / 2, dropper.getY() - glassHeight );
            lineTo( dropper.getX() + glassWidth / 2, dropper.getY() - glassYOffset );
            lineTo( dropper.getX() + tipWidth / 2, dropper.getY() - tipHeight );
            lineTo( dropper.getX() + tipWidth / 2, dropper.getY() );
            closePath();
        }};

        // solution outside the dropper
        Rectangle2D solutionOutsideDropper = null;
        if ( dropper.on.get() ) {

            double x = dropper.getX() - ( dropperHoleWidth / 2 );
            double y = dropper.getY();
            double width = dropperHoleWidth;
            double height = beaker.getY() - dropper.getY();
            solutionOutsideDropper = new Rectangle2D.Double( x, y, width, height );
        }

        // union of inside + outside
        Area area = new Area( solutionInsideDropper.getGeneralPath() );
        if ( solutionOutsideDropper != null ) {
            area.add( new Area( solutionOutsideDropper ) );
        }
        setPathTo( area );
    }
}
