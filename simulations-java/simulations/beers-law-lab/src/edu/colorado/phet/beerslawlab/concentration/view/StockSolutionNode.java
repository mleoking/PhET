// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.model.Beaker;
import edu.colorado.phet.beerslawlab.common.model.Solute;
import edu.colorado.phet.beerslawlab.common.model.Solution;
import edu.colorado.phet.beerslawlab.common.model.Solvent;
import edu.colorado.phet.beerslawlab.concentration.model.Dropper;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Stock solution coming out of dropper.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class StockSolutionNode extends PPath {

    private final Solvent solvent;
    private final Property<Solute> solute;
    private final Dropper dropper;
    private final Beaker beaker;
    private final double dropperTipWidth;

    public StockSolutionNode( Solvent solvent, Property<Solute> solute, Dropper dropper, Beaker beaker ) {
        setPickable( false );
        setChildrenPickable( false );
        setStroke( BLLConstants.FLUID_STROKE );

        this.solvent = solvent;
        this.solute = solute;
        this.dropper = dropper;
        this.beaker = beaker;
        this.dropperTipWidth = DropperNode.TIP_WIDTH;

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
            Color color = Solution.createColor( solvent, solute.get(), solute.get().stockSolutionConcentration );
            setPaint( color );
            setStrokePaint( BLLConstants.createFluidStrokeColor( color ) );

            // solution outside the dropper
            Rectangle2D solutionOutsideDropper = null;
            if ( dropper.on.get() ) {

                double x = -dropperTipWidth / 2;
                double y = 0;
                double width = dropperTipWidth;
                double height = beaker.getY() - dropper.getY();
                solutionOutsideDropper = new Rectangle2D.Double( x, y, width, height );
            }

            // union of inside + outside
            Area area = new Area( DropperNode.GLASS_PATH );
            if ( solutionOutsideDropper != null ) {
                area.add( new Area( solutionOutsideDropper ) );
            }
            setPathTo( area );

            // move this node to the dropper's location
            setOffset( dropper.location.get().toPoint2D() );
        }
    }
}
