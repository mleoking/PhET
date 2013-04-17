// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.CircuitComponent;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * User: Sam Reid
 * Date: Sep 17, 2006
 * Time: 7:49:22 PM
 */

public abstract class RectangularComponentNode extends ComponentNode {
    private FlameNode flameNode;
    private PDimension dimension;
    private Branch.FlameListener flameListener = new Branch.FlameListener() {
        public void flameStarted() {
            update();
        }

        public void flameFinished() {
            update();
        }
    };

    public RectangularComponentNode( final CCKModel model, final CircuitComponent circuitComponent, double width, double height, JComponent component, CCKModule module ) {
        super( model, circuitComponent, component, module );
        dimension = new PDimension( width, height );
        flameNode = new FlameNode( getBranch() );
        addChild( flameNode );
        getBranch().addFlameListener( flameListener );
        update();
    }

    public void delete() {
        super.delete();
        getBranch().removeFlameListener( flameListener );
    }

    public PDimension getDimension() {
        return new PDimension( dimension );
    }

    protected void update() {
        super.update();

        Rectangle2D aShape = new Rectangle2D.Double( 0, 0, dimension.getWidth(), dimension.getHeight() );
        aShape = RectangleUtils.expand( aShape, 2, 2 );
        getHighlightNode().setPathTo( aShape );
        getHighlightNode().setVisible( getBranch().isSelected() );

        double resistorLength = getBranch().getStartPoint().distance( getBranch().getEndPoint() );
        double imageLength = dimension.getWidth();
        double sx = resistorLength / imageLength;
        double sy = getCircuitComponent().getHeight() / dimension.getHeight();
        double angle = new MutableVector2D( getBranch().getStartPoint(), getBranch().getEndPoint() ).getAngle();
        setTransform( new AffineTransform() );
        if ( Math.abs( sx ) > 1E-4 ) {
            setScale( sx );
        }
        setOffset( getBranch().getStartPoint() );
        rotate( angle );
        translate( 0, -dimension.getHeight() / 2 );

        flameNode.setOffset( 0, -flameNode.getFullBounds().getHeight() + dimension.getHeight() / 2 );
        if ( getParent() != null && getParent().getChildrenReference().indexOf( flameNode ) != getParent().getChildrenReference().size() - 1 ) {
            flameNode.moveToFront();
        }
    }

}
