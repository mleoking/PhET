/** Sam Reid*/
package edu.colorado.phet.common_cck.examples;

import edu.colorado.phet.common_cck.application.ApplicationModel;
import edu.colorado.phet.common_cck.application.Module;
import edu.colorado.phet.common_cck.application.PhetApplication;
import edu.colorado.phet.common_cck.model.BaseModel;
import edu.colorado.phet.common_cck.model.clock.AbstractClock;
import edu.colorado.phet.common_cck.model.clock.SwingTimerClock;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.BasicGraphicsSetup;
import edu.colorado.phet.common_cck.view.graphics.Boundary;
import edu.colorado.phet.common_cck.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common_cck.view.graphics.Graphic;
import edu.colorado.phet.common_cck.view.graphics.ShapeGraphic;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.util.FrameSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Mar 25, 2004
 * Time: 9:36:13 AM
 * Copyright (c) Mar 25, 2004 by Sam Reid
 */
public class TestTransforms extends Module {
    public TestTransforms( AbstractClock clock ) {
        super( "Test Transforms" );
        Stroke modelStroke = new BasicStroke( .2f );
        Stroke viewStroke = new BasicStroke( 2 );
        Color fill = Color.blue;
        Color outline = Color.black;
        setApparatusPanel( new ApparatusPanel() );
        setModel( new BaseModel() );
        Rectangle2D.Double modelBounds = new Rectangle2D.Double( 0, 0, 10, 10 );
        Rectangle viewBounds = new Rectangle( 40, 40, 400, 400 );
        ModelViewTransform2D transform = new ModelViewTransform2D( modelBounds, viewBounds );
        double w = 1.4;
        double h = 1.4;
        Shape centeredShape = new Ellipse2D.Double( 5 - w / 2, 5 - h / 2, w, h );
        //        ShapeGraphic shapeGraphic = new ShapeGraphic( centeredShape, fill, outline, modelStroke );

        JPanel cp = new JPanel();
        setControlPanel( cp );
        //        getApparatusPanel().addGraphic( shapeGraphic );

        ImplicitTransformGraphic gtg = new ImplicitTransformGraphic( transform, new ShapeGraphic( centeredShape, fill, outline, modelStroke ) );
        getApparatusPanel().addGraphic( gtg );
        getApparatusPanel().addGraphicsSetup( new BasicGraphicsSetup() );
        InteractiveImplicitTrfGraphic iitg = new InteractiveImplicitTrfGraphic( gtg, gtg );
        getApparatusPanel().addGraphic( iitg );

        ExplicitTransformGraphic stg = new ExplicitTransformGraphic( transform, centeredShape, viewStroke, fill, outline );
        //        getApparatusPanel().addGraphic( stg);
        InteractiveExplicitTrfGraphic ietg = new InteractiveExplicitTrfGraphic( stg, stg );
        //        getApparatusPanel().addGraphic( ietg);


    }

    class InteractiveImplicitTrfGraphic extends DefaultInteractiveGraphic {
        public InteractiveImplicitTrfGraphic( Graphic graphic, Boundary boundary ) {
            super( graphic, boundary );
            addCursorHandBehavior();
        }
    }

    class InteractiveExplicitTrfGraphic extends DefaultInteractiveGraphic {
        public InteractiveExplicitTrfGraphic( Graphic graphic, Boundary boundary ) {
            super( graphic, boundary );
            addCursorHandBehavior();
        }

    }

    class ExplicitTransformGraphic implements Graphic, Boundary {
        ModelViewTransform2D transform;
        Shape shape;
        ShapeGraphic sg;
        private Shape viewShape;

        public ExplicitTransformGraphic( ModelViewTransform2D transform, Shape shape, Stroke viewStroke, Color fill, Color outline ) {
            this.transform = transform;
            this.shape = shape;
            sg = new ShapeGraphic( shape, fill, outline, viewStroke );
        }

        public void paint( Graphics2D g ) {
            viewShape = transform.createTransformedShape( shape );
            sg.setShape( viewShape );
            sg.paint( g );
        }

        public boolean contains( int x, int y ) {
            return viewShape != null && viewShape.contains( x, y );
        }
    }

    class ImplicitTransformGraphic implements Graphic, Boundary {
        ModelViewTransform2D transform;
        ShapeGraphic sg;

        public ImplicitTransformGraphic( ModelViewTransform2D transform, ShapeGraphic sg ) {
            this.transform = transform;
            this.sg = sg;
        }

        public void paint( Graphics2D g ) {
            AffineTransform orig = g.getTransform();
            g.transform( transform.getAffineTransform() );
            sg.paint( g );
            g.setTransform( orig );
        }

        public boolean contains( int x, int y ) {
            Point2D modelCoord = transform.viewToModel( x, y );
            return sg.getShape().contains( modelCoord );
        }
    }

    public static void main( String[] args ) {
        AbstractClock ac = new SwingTimerClock( 1, 30, true );
        Module m = new TestTransforms( ac );
        ApplicationModel ad = new ApplicationModel( "Test Transforms", "A Test", "1.0", new FrameSetup.MaxExtent(), m, ac );


        PhetApplication pa = new PhetApplication( ad );

        pa.startApplication();
    }

}
