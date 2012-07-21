// Copyright 2002-2012, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.BoundNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 10:50:18 AM
 */

public class Protractor extends PhetPNode {
    LegGraphic leftLeg;
    LegGraphic rightLeg;
    ArcGraphic arcGraphic;
    ReadoutGraphic readoutGraphic;
    ArrayList listeners = new ArrayList();

    public Protractor() {
        leftLeg = new LegGraphic();
        rightLeg = new LegGraphic();

        readoutGraphic = new ReadoutGraphic();
        arcGraphic = new ArcGraphic();
        addChild( leftLeg );
        addChild( rightLeg );
        addChild( arcGraphic );
        addChild( readoutGraphic );
        readoutGraphic.setOffset( 20, -50 );
        leftLeg.setAngle( Math.PI / 2 );
        rightLeg.setAngle( 0.0 );
        update();

        addPropertyChangeListener( PNode.PROPERTY_VISIBLE, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                notifyVisibilityChanged();
            }

        } );
    }

    private void notifyVisibilityChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.visibilityChanged( this );
        }
    }

    private void notifyAngleChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.angleChanged( this );
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public static interface Listener {
        void angleChanged( Protractor protractor );

        void visibilityChanged( Protractor protractor );
    }

    public void setLeftLegPickable( boolean pickable ) {
        leftLeg.setPickable( pickable );
        leftLeg.setChildrenPickable( pickable );
    }

    public void setReadoutGraphicPickable( boolean pickable ) {
        readoutGraphic.setPickable( pickable );
        readoutGraphic.setChildrenPickable( pickable );
    }

    private static double toDegrees( double radians ) {
        return radians * 360 / Math.PI / 2.0;
    }

    public double getAngle() {
        return leftLeg.getAngle() - rightLeg.getAngle();
    }

    private void update() {
        leftLeg.update();
        rightLeg.update();
        arcGraphic.update();
        readoutGraphic.update();
        notifyAngleChanged();//todo this will give some spurious events.
    }

    class LegGraphic extends PNode {
        double angle;
        PPath path;
        private int LEG_LENGTH = 250;
        ProtractorHandleGraphic protractorHandleGraphic;

        public LegGraphic() {
            path = new PPath();
            path.setStrokePaint( Color.orange );
            path.setStroke( new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[] { 10, 10 }, 0 ) );
            addChild( path );
            addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
            addInputEventListener( new PDragEventHandler() {
                protected void drag( PInputEvent event ) {
                    setAngle( getAngle( event ) );
                }
            } );
            protractorHandleGraphic = new ProtractorHandleGraphic();
            addChild( protractorHandleGraphic );
            update();
        }

        private double getAngle( PInputEvent event ) {
            Point2D loc = event.getPositionRelativeTo( getParent() );
            return new MutableVector2D( new Point2D.Double(), loc ).getAngle();
        }

        private void update() {
            Point2D pt = Vector2D.createPolar( LEG_LENGTH, angle ).getDestination( new Point2D.Double() );
            Line2D.Double line = new Line2D.Double( pt, new Point2D.Double() );
            path.setPathTo( line );
            protractorHandleGraphic.setOffset( pt );
            protractorHandleGraphic.setRotation( angle - Math.PI / 2 );
        }

        public double getAngle() {
            return angle;
        }

        public void setAngle( double angle ) {
            this.angle = angle;
            Protractor.this.update();
        }
    }

    class ReadoutGraphic extends PNode {

        //        private DefaultDecimalFormat numberFormat = new DefaultDecimalFormat( "0.00" );
        private DefaultDecimalFormat numberFormat = new DefaultDecimalFormat( "0" );
        private PText text;

        public ReadoutGraphic() {

            text = new PText();
            text.setTextPaint( Color.black );
            text.setFont( new PhetFont( 16, true ) );

            addInputEventListener( new PDragEventHandler() {
                protected void drag( PInputEvent event ) {
                    PDimension deltaRelativeTo = event.getDeltaRelativeTo( Protractor.this.getParent() );
                    Protractor.this.translate( deltaRelativeTo.getWidth(), deltaRelativeTo.getHeight() );
                }
            } );
            BoundNode boundGraphic = new BoundNode( text, 3, 3 );
            boundGraphic.setPaint( new Color( Color.yellow.getRed(), Color.yellow.getGreen(), Color.yellow.getBlue(), 200 ) );
            addChild( boundGraphic );

            addChild( text );
            addInputEventListener( new CursorHandler() );
        }

        public void update() {
//            String string = numberFormat.format( getDegreesSigned() );
            String string = numberFormat.format( getDegreesUnsigned() );
            text.setText( string + getDegreeString() );
        }

        private String getDegreeString() {
            return "\u00B0";
        }
    }

    public double getDegreesUnsigned() {
        return Math.abs( getDegreesSigned() );
    }

    public double getDegreesSigned() {
        double degreeValue = 360 / Math.PI / 2.0 * getAngle();
        if ( degreeValue >= 180 && degreeValue <= 270 ) {
            degreeValue = -( 360 - degreeValue );
        }
        else if ( degreeValue < 0 ) {
//            degreeValue = Math.abs( degreeValue );
        }
        return degreeValue;
    }

    class ArcGraphic extends PNode {
        private PPath path;

        public ArcGraphic() {
            path = new PPath();
            path.setStroke( new BasicStroke( 5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f ) );
            path.setStrokePaint( Color.green );
            addChild( path );
        }

        public void update() {
//            System.out.println( "leftLeg.getAngle() = " + leftLeg.getAngle() );
//            System.out.println( "right.getAngle() = " + rightLeg.getAngle() );
            double arcDist = 30;
            Arc2D.Double arc = new Arc2D.Double( -arcDist, -arcDist, arcDist * 2, arcDist * 2, toDegrees( 0 ), toDegrees( 0 ), Arc2D.Double.OPEN );

            Point2D p1 = Vector2D.createPolar( 20, leftLeg.getAngle() ).getDestination( new Point2D.Double() );
            Point2D p2 = Vector2D.createPolar( 20, rightLeg.getAngle() ).getDestination( new Point2D.Double() );
            if ( getDegreesSigned() >= 0 ) {
                arc.setAngles( p1, p2 );
            }
            else {
                arc.setAngles( p2, p1 );
            }
            path.setPathTo( arc );
        }
    }
}
