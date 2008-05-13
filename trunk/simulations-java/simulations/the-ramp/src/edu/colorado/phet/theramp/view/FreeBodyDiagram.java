/*  */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Dec 16, 2004
 * Time: 5:57:05 PM
 */

public class FreeBodyDiagram extends PNode {
    private RampModule module;
    private PPath background;
    private AxesGraphic axes;
    private Rectangle rect;

    private ForceArrow mg;
    private ForceArrow normal;
    private ForceArrow appliedForce;
    private ForceArrow wallForce;
    private ForceArrow frictionForce;
    private ForceArrow netForce;

    private double scale = 1.0 / 20.0;
    private RampLookAndFeel laf;
    private boolean userClicked = false;
    private RampPhysicalModel model;
    private RampPanel component;
    private JComponent owner;

    public FreeBodyDiagram( RampPanel component, final RampModule module, JComponent owner ) {
        this.component = component;
        this.owner = owner;
//        super( component );
        this.model = module.getRampPhysicalModel();
        this.module = module;
        rect = new Rectangle( 0, 0, 200, 200 );
        laf = new RampLookAndFeel();

        background = new PPath( rect );
        background.setPaint( Color.white );
        background.setStroke( new BasicStroke( 1.0f ) );
        background.setStrokePaint( Color.black );
        addChild( background );
        axes = new AxesGraphic();
        addChild( axes );
//        axes.setVisible( false );

        mg = new ForceArrow( this, laf.getWeightColor(), ( TheRampStrings.getString( "force.subscript.gravity" ) ), new Vector2D.Double( 0, 80 ) );
        addForceArrow( mg );

        normal = new ForceArrow( this, laf.getNormalColor(), ( TheRampStrings.getString( "force.subscript.normal" ) ), new Vector2D.Double( 0, 80 ) );
        addForceArrow( normal );

        appliedForce = new ForceArrow( this, laf.getAppliedForceColor(), ( TheRampStrings.getString( "force.subscript.applied" ) ), new Vector2D.Double() );
        addForceArrow( appliedForce );

        frictionForce = new ForceArrow( this, laf.getFrictionForceColor(), ( TheRampStrings.getString( "force.subscript.friction" ) ), new Vector2D.Double() );
        addForceArrow( frictionForce );

        netForce = new ForceArrow( this, laf.getNetForceColor(), ( TheRampStrings.getString( "force.subscript.net" ) ), new Vector2D.Double() );
        addForceArrow( netForce );
        netForce.setVerticalOffset( -30 );

        wallForce = new ForceArrow( this, laf.getWallForceColor(), ( TheRampStrings.getString( "force.subscript.wall" ) ), new Vector2D.Double() );
        addForceArrow( wallForce );
        wallForce.setVerticalOffset( -30 );

        PBasicInputEventHandler mia = new PBasicInputEventHandler() {
            // implements java.awt.event.MouseListener
            public void mousePressed( PInputEvent e ) {
                module.record();
                setForce( e.getPositionRelativeTo( FreeBodyDiagram.this ) );
                userClicked = true;
            }

            public void mouseDragged( PInputEvent e ) {
//                System.out.println( "dragged: e = " + e );
                setForce( e.getPositionRelativeTo( FreeBodyDiagram.this ) );
            }

            // implements java.awt.event.MouseListener
            public void mouseReleased( PInputEvent e ) {
//                System.out.println( "released: e = " + e );
                model.setAppliedForce( 0.0 );
            }
        };
        ThresholdedPDragAdapter listener = new ThresholdedPDragAdapter( mia, 10, 0, 1000 );
        addInputEventListener( listener );
        addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        updateAll();
        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                updateAll();
            }
        } );

        PText title = new PText( TheRampStrings.getString( "display.free-body-diagram" ) );
        addChild( title );
    }

    private void setForce( Point2D pt ) {
        double x = pt.getX();

        //set the applied force
        double dx = x - getCenter().getX();
        double appliedForceRequest = dx / scale;
        model.setAppliedForce( appliedForceRequest );
    }

    private void updateXForces() {

        Vector2D.Double af = new Vector2D.Double( model.getAppliedForce().getScaledInstance( scale ) );
        appliedForce.setVector( af );

        Vector2D.Double ff = new Vector2D.Double( model.getFrictionForce().getScaledInstance( scale ) );
        frictionForce.setVector( ff );

        AbstractVector2D net = new Vector2D.Double( model.getTotalForce().getScaledInstance( scale ) );
        netForce.setVector( net );

        Vector2D.Double wf = new Vector2D.Double( model.getWallForce().getScaledInstance( scale ) );
        wallForce.setVector( wf );
    }

    private void updateMG() {
        Vector2D gravity = model.getGravityForce();
        mg.setVector( gravity.getScaledInstance( scale ) );
        normal.setVector( model.getNormalForce().getScaledInstance( scale ) );
    }

    public void addForceArrow( ForceArrow forceArrow ) {
        addChild( forceArrow );
    }

    private Point2D getCenter() {
        return RectangleUtils.getCenter( rect );
    }

    public void updateAll() {
        if( owner.isVisible() ) {
//        System.out.println( "FreeBodyDiagram.updateAll@"+System.currentTimeMillis() );
            updateXForces();
            updateMG();
            axes.update();
        }
    }

    public void setBounds( int x, int y, int width, int height ) {
        rect.setBounds( x, y, width, height );
        updateAll();
    }

    public static class ForceArrow extends PNode {
        private PPath shapeGraphic;
        private HTMLNode textGraphic;
        private FreeBodyDiagram fbd;
//        private double dx;
        //        private double dy;
        private String name;
        private Arrow lastArrow;
        private double verticalOffset = 0;

        public ForceArrow( FreeBodyDiagram fbd, Color color, String name, Vector2D.Double v ) {
            this.fbd = fbd;
            this.name = name;
            shapeGraphic = new PPath();
//            component, null, RampUtil.transparify( color, 150 ), new BasicStroke( 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ), RampUtil.transparify( Color.black, 128 ) );
            shapeGraphic.setStroke( new BasicStroke( 1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
            shapeGraphic.setPaint( color );
//            shapeGraphic.setStrokePaint( RampUtil.transparify( Color.black, 150 ) );
            shapeGraphic.setStrokePaint( Color.black );
            addChild( shapeGraphic );
            Font font = new Font( PhetFont.getDefaultFontName(), Font.BOLD, 16 );
//            textGraphic = new PhetShadowTextGraphic( component, name, font, 0, 0, color, 1, 1, Color.black );
            textGraphic = new HTMLNode( name, font, color );
            addChild( textGraphic );
            setVector( v );
            setPickable( false );
            setChildrenPickable( false );
        }

        public void setVerticalOffset( double verticalOffset ) {
            this.verticalOffset = verticalOffset;
        }

        public void setVector( AbstractVector2D v ) {
            Point2D origin = fbd.getCenter();
            SurfaceGraphic surfaceGraphic = fbd.getRampPanel().getRampWorld().getBlockGraphic().getCurrentSurfaceGraphic();
            double viewAngle = surfaceGraphic.getViewAngle();

//            origin = new Point2D.Double( origin.getX() + dx, origin.getY() + dy );
            origin = Vector2D.Double.parseAngleAndMagnitude( verticalOffset, viewAngle ).getNormalVector().getNormalVector().getNormalVector().getDestination( origin );
            Arrow arrow = new Arrow( origin, v.getDestination( origin ), 20, 20, 8, 0.5, true );
            Shape sh = arrow.getShape();
            if( lastArrow == null || !lastArrow.equals( arrow ) ) {
                shapeGraphic.setPathTo( sh );
            }
            lastArrow = arrow;

            Rectangle b = sh.getBounds();
            Point ctr = RectangleUtils.getCenter( b );
            if( v.getX() > 0 ) {
                this.textGraphic.setOffset( b.x + b.width + 5, ctr.y - textGraphic.getHeight() / 2 );
                textGraphic.setVisible( true );
            }
            else if( v.getX() < 0 ) {
                this.textGraphic.setOffset( b.x - textGraphic.getWidth() - 5, ctr.y - textGraphic.getHeight() / 2 );
                textGraphic.setVisible( true );
            }
            else if( v.getY() > 0 ) {
                this.textGraphic.setOffset( ctr.x - textGraphic.getWidth() / 2, b.y + b.height );
                textGraphic.setVisible( true );
//                System.out.println( name+", y>0" );
            }
            else if( v.getY() < 0 ) {
                this.textGraphic.setOffset( ctr.x - textGraphic.getWidth() / 2, b.y - textGraphic.getHeight() );
                textGraphic.setVisible( true );
            }
            else {
                textGraphic.setVisible( false );
            }
            if( v.getMagnitude() <= 0.05 ) {
                setVisible( false );
            }
            else {
                setVisible( true );
            }
        }
    }

    private RampPanel getRampPanel() {
        return component;
    }

    public class AxesGraphic extends PNode {
        private PPath xAxis;
        private PPath yAxis;
        private PNode xLabel;
        private PNode yLabel;
        private Line2D.Double yLine = null;
        private Line2D.Double xLine = null;

        public AxesGraphic() {
            Stroke stroke = new BasicStroke( 1.0f );
            Color color = Color.black;
            xAxis = new PPath();
            xAxis.setStroke( stroke );
            xAxis.setStrokePaint( color );

            yAxis = new PPath();
            yAxis.setStroke( stroke );
            yAxis.setStrokePaint( color );

            addChild( xAxis );
            addChild( yAxis );

//            Font font = new Font( PhetDefaultFont.LUCIDA_SANS, Font.PLAIN, 16 );
//            xLabel = new HTMLGraphic( ( "FreeBodyDiagram.fx" ), font, Color.black );
//            yLabel = new HTMLGraphic( ( "FreeBodyDiagram.fy" ), font, Color.black );
//            addChild( xLabel );
//            addChild( yLabel );

            update();
        }

        public void update() {

            Line2D.Double xLine = new Line2D.Double( rect.x, rect.y + rect.height / 2, rect.x + rect.width, rect.y + rect.height / 2 );
            Line2D.Double yLine = new Line2D.Double( rect.x + rect.width / 2, rect.y, rect.x + rect.width / 2, rect.y + rect.height );
//
            if( this.xLine == null || !xLine.equals( this.xLine ) ) {
                this.xLine = xLine;
                xAxis.setPathTo( xLine );
//                xLabel.setOffset( (int)xLine.getX2() - xLabel.getWidth(), (int)xLine.getY2() );
            }
            if( this.yLine == null || !yLine.equals( this.yLine ) ) {
                this.yLine = yLine;
                yAxis.setPathTo( yLine );
//                yLabel.setOffset( (int)yLine.getX1() + 3, (int)yLine.getY1() );
            }

        }
    }

    public boolean isUserClicked() {
        return userClicked;
    }

    public void resetUserClicked() {
        this.userClicked = false;
    }
}
