/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.elements.junction.JunctionGraphic;
import edu.colorado.phet.cck.elements.particles.ParticleSetGraphic;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.CompositeTransformListener;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 28, 2003
 * Time: 3:00:58 AM
 * Copyright (c) Aug 28, 2003 by Sam Reid
 */
public class DefaultCompositeBranchGraphic extends CompositeInteractiveGraphic implements AbstractBranchGraphic {
    Branch branch;
    CompositeTransformListener compositeTransformListener = new CompositeTransformListener();
    private AbstractBranchGraphic wireGraphic;
    private JunctionGraphic startJunctionGraphic;
    private JunctionGraphic endJunctionGraphic;
    public static final int JUNCTION_RADIUS = 16;
//    public static final Color junctionColor = Color.red;
    public static Color JUNCTION_COLOR = CCK2Module.COPPER;// new Color(94, 222, 101);
    public static final BasicStroke JUNCTION_STROKE = new BasicStroke( 2 );
//    public static Stroke JUNCTION_STROKE;

    public DefaultCompositeBranchGraphic( ModelViewTransform2D transform, Branch branch, final CCK2Module module, AbstractBranchGraphic wireGraphic ) {
        this.branch = branch;
        this.wireGraphic = wireGraphic;
        addGraphic( wireGraphic, 0 );

        startJunctionGraphic = new JunctionGraphic( branch.getStartJunction(), module, JUNCTION_RADIUS, JUNCTION_COLOR, JUNCTION_STROKE, JUNCTION_COLOR );
        endJunctionGraphic = new JunctionGraphic( branch.getEndJunction(), module, JUNCTION_RADIUS, JUNCTION_COLOR, JUNCTION_STROKE, JUNCTION_COLOR );

        if( wireGraphic instanceof TransformListener ) {
            compositeTransformListener.addTransformListener( wireGraphic );
        }
        compositeTransformListener.addTransformListener( startJunctionGraphic );
        compositeTransformListener.addTransformListener( endJunctionGraphic );
        addGraphic( startJunctionGraphic, 1 );
        addGraphic( endJunctionGraphic, 1 );
        addGraphic( new Graphic() {
            public void paint( Graphics2D g ) {
                ParticleSetGraphic particleSetGraphic = module.getParticleSetGraphic();
                particleSetGraphic.paint( g, getBranch() );
            }
        }, 1000 );
        transform.addTransformListener( this );
    }

    public Shape getStartWireShape() {
        return wireGraphic.getStartWireShape();
    }

    public Shape getEndWireShape() {
        return wireGraphic.getEndWireShape();
    }

    public void transformChanged( ModelViewTransform2D ModelViewTransform2D ) {
        compositeTransformListener.transformChanged( ModelViewTransform2D );
    }

    public Branch getBranch() {
        return branch;
    }

    public void setWireColor( Color color ) {
        wireGraphic.setWireColor( color );
    }

    public InteractiveGraphic getMainBranchGraphic() {
        if( wireGraphic instanceof ImageBranchGraphic ) {
            ImageBranchGraphic ibg = (ImageBranchGraphic)wireGraphic;
            return ibg.getMainBranchGraphic();
        }
        return wireGraphic;
    }

    public AbstractBranchGraphic getAbstractBranchGraphic() {
        return wireGraphic;
    }
}

