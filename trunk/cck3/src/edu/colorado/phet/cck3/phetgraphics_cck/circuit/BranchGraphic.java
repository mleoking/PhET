/** Sam Reid*/
package edu.colorado.phet.cck3.phetgraphics_cck.circuit;

import edu.colorado.phet.cck3.common.LineSegment;
import edu.colorado.phet.cck3.model.components.Branch;
import edu.colorado.phet.cck3.phetgraphics_cck.CCKCompositePhetGraphic;
import edu.colorado.phet.cck3.phetgraphics_cck.CCKModule;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common_cck.view.util.RectangleUtils;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:39:53 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class BranchGraphic extends CCKCompositePhetGraphic {
    private Branch branch;
    private double thickness;
    private ModelViewTransform2D transform;
    private SimpleObserver simpleObserver;
    private TransformListener transformListener;
    private PhetShapeGraphic core;
    private Color highlightColor = Color.yellow;
    private PhetShapeGraphic highlight;
    private PhetTextGraphic debugText;
//    private boolean debug = false;

    public BranchGraphic( Branch branch, ApparatusPanel apparatusPanel, double thickness, ModelViewTransform2D transform, Color color ) {
        super( apparatusPanel );
        highlight = new PhetShapeGraphic( apparatusPanel, null, highlightColor );
        core = new PhetShapeGraphic( apparatusPanel, null, color );
        addGraphic( highlight );
        addGraphic( core );
        this.branch = branch;
        this.thickness = thickness;
        this.transform = transform;
        simpleObserver = new SimpleObserver() {
            public void update() {
                doupdate();
            }
        };
        branch.addObserver( simpleObserver );
        transformListener = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                doupdate();
            }
        };
        transform.addTransformListener( transformListener );
//<<<<<<< BranchGraphic.java
        if( CircuitGraphic.GRAPHICAL_DEBUG ) {
            debugText = new PhetTextGraphic( apparatusPanel, new Font( "Dialog", 0, 12 ), "", Color.black, 0, 0 );
        }
////=======
//        if( debug ) {
//            debugText = new PhetTextGraphic( apparatusPanel, new Font( "Dialog", 0, 12 ), "", Color.black, 0, 0 );
//            addGraphic( debugText );
//        }
//>>>>>>> 1.12

        doupdate();
        setVisible( true );
//<<<<<<< BranchGraphic.java
        if( debugText != null ) {
            addGraphic( debugText );
        }
//=======
//>>>>>>> 1.12
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if( highlight != null ) {
            highlight.setVisible( visible && branch.isSelected() );
        }
    }

    private void doupdate() {
        Shape coreshape = LineSegment.getSegment( branch.getX1(), branch.getY1(), branch.getX2(), branch.getY2(), thickness );
        Shape highlightShape = LineSegment.getSegment( branch.getX1(), branch.getY1(), branch.getX2(), branch.getY2(), thickness * 1.5 );
        if( coreshape.getBounds().width == 0 && coreshape.getBounds().height == 0 ) {
            //            throw new RuntimeException( "No bounds to coreshape." );
        }
        else {
            highlight.setVisible( branch.isSelected() );
            highlight.setShape( transform.createTransformedShape( highlightShape ) );
            core.setShape( transform.createTransformedShape( coreshape ) );

            if( debugText != null ) {
                CCKModule module = null;
                String text = "r=" + branch.getResistance();
                if( module.getParticleSet() != null ) {
                    text += ", n=" + module.getParticleSet().getParticles( branch ).length;
                }
                debugText.setText( text );
                Point bounds = RectangleUtils.getCenter( core.getShape().getBounds() );
                debugText.setPosition( bounds.x, bounds.y );
            }
            super.setBoundsDirty();
            //            System.out.println( "transform.createTransformedShape( coreshape) = " + transform.createTransformedShape( coreshape ).getBounds() );
        }
    }

    public Branch getBranch() {
        return branch;
    }

    public void delete() {
        branch.removeObserver( simpleObserver );
        transform.removeTransformListener( transformListener );
    }

    public Shape getCoreShape() {
        return core.getShape();
    }

}
