/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.particles;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.BranchObserver;
import edu.colorado.phet.cck.elements.circuit.Junction;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.fastpaint.FastPaintImageGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * User: Sam Reid
 * Date: Sep 4, 2003
 * Time: 3:36:19 AM
 * Copyright (c) Sep 4, 2003 by Sam Reid
 */
public class BranchParticleGraphic extends FastPaintImageGraphic {
    BranchParticle particle;
    ModelViewTransform2D transform;
    private BufferedImage image;
    private Point viewCoord;

    public BranchParticleGraphic( BranchParticle particle, ModelViewTransform2D transform, Module module, BufferedImage image, ImageObserver obs ) {
        super( image, module.getApparatusPanel() );
        this.particle = particle;
        this.transform = transform;
        this.image = image;
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D ModelViewTransform2D ) {
                stateChanged();
            }
        } );
        particle.addObserver( new SimpleObserver() {
            public void update() {
                stateChanged();
            }
        } );
        particle.getBranch().addObserver( new BranchObserver() {
            public void junctionMoved( Branch branch2, Junction j ) {
                stateChanged();
            }

            public void currentOrVoltageChanged( Branch branch2 ) {
            }
        } );

        ImmutableVector2D loc = particle.getPosition2D();
        this.viewCoord = transform.modelToView( loc );
        super.setPositionNoRepaint( viewCoord );
    }

    private void stateChanged() {
        ImmutableVector2D loc = particle.getPosition2D();
        this.viewCoord = transform.modelToView( loc );
        super.setPosition( viewCoord );
    }

    public BranchParticle getBranchParticle() {
        return particle;
    }

}
