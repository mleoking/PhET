/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.common.LineSegment;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.fastpaint.FastPaintShapeGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:39:53 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class BranchGraphic extends FastPaintShapeGraphic {
    private Branch branch;
    private double thickness;
    private ModelViewTransform2D transform;

    public BranchGraphic( Branch branch, ApparatusPanel apparatusPanel, double thickness, ModelViewTransform2D transform, Color color ) {
        super( null, color, apparatusPanel );
        this.branch = branch;
        this.thickness = thickness;
        this.transform = transform;

        branch.addObserver( new SimpleObserver() {
            public void update() {
                doupdate();
            }
        } );
        doupdate();
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                doupdate();
            }
        } );
    }

    private void doupdate() {
        Shape shape = LineSegment.getSegment( branch.getX1(), branch.getY1(), branch.getX2(), branch.getY2(), thickness );
        if( shape.getBounds().width == 0 && shape.getBounds().height == 0 ) {
//            throw new RuntimeException( "No bounds to shape." );
        }
        else {
            setShape( transform.createTransformedShape( shape ) );
        }
    }

    public Branch getBranch() {
        return branch;
    }

}
