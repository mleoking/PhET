package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.common.LineSegment;
import edu.colorado.phet.cck.model.analysis.CircuitSolutionListener;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.cck.model.components.SeriesAmmeter;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PAffineTransform;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: May 29, 2004
 * Time: 12:57:37 AM
 * Copyright (c) May 29, 2004 by Sam Reid
 */
public class SeriesAmmeterNode extends ComponentNode {
    private SeriesAmmeter component;
    private ICCKModule module;
    private static final double SCALE = 1.0 / 60.0;
    private Stroke stroke = new BasicStroke( (float)( 5 * SCALE ) );
    private Font font = new Font( "Lucida Sans", Font.BOLD, 17 );
    private Shape shape;
    private String text = SimStrings.get( "SeriesAmmeterGraphic.Ammeter" );
    private String fixedMessage;
    private SimpleObserver simpleObserver;
    private CircuitSolutionListener circuitSolutionListener;
    private int numWindows = 3;

    private PhetPPath blackGraphic;
    private PhetPPath[] windowGraphics = new PhetPPath[numWindows];
    private PhetPPath areaGraphic;
    private PText textGraphic;
    private Area area;

    public SeriesAmmeterNode( Component parent, final SeriesAmmeter component, ICCKModule module, String fixedMessage ) {
        this( parent, component, module );
        this.fixedMessage = fixedMessage;
    }

    public SeriesAmmeterNode( Component parent, final SeriesAmmeter component, final ICCKModule module ) {
        super( module.getCCKModel(), component, parent );
        this.component = component;
        this.module = module;

        simpleObserver = new SimpleObserver() {
            public void update() {
                changed();
            }
        };
        component.addObserver( simpleObserver );
        circuitSolutionListener = new CircuitSolutionListener() {
            public void circuitSolverFinished() {
                DecimalFormat df = new DecimalFormat( "0.00" );
                String form = df.format( Math.abs( component.getCurrent() ) );
                text = "" + form + " " + SimStrings.get( "SeriesAmmeterGraphic.Amps" );
                changed();
            }
        };
        module.getCCKModel().getCircuitSolver().addSolutionListener( circuitSolutionListener );
        blackGraphic = new PhetPPath( new Area(), stroke, Color.black );

        addChild( blackGraphic );
        areaGraphic = new PhetPPath( new Area(), Color.black );
        addChild( areaGraphic );
        for( int i = 0; i < windowGraphics.length; i++ ) {
            windowGraphics[i] = new PhetPPath( new Area(), new BasicStroke( (float)( 1.2f * SCALE ) ), Color.black );
            addChild( windowGraphics[i] );
        }
        textGraphic = new PText();
        textGraphic.setFont( font );
        textGraphic.setVisible( true );
        getHighlightNode().setStroke( new BasicStroke( (float)( 3f * SCALE ) ) );
        addChild( textGraphic );
        changed();
        setVisible( true );
    }

    public void setFont( Font font ) {
        textGraphic.setFont( font );
    }

    private void changed() {
        double newHeight = ( component.getHeight() );
        Point2D start = ( component.getStartJunction().getPosition() );
        Point2D end = ( component.getEndJunction().getPosition() );
        this.shape = LineSegment.getSegment( start, end, newHeight );
        BasicStroke stroke = new BasicStroke( (float)( 12 * SCALE ) );
        getHighlightNode().setPathTo( stroke.createStrokedShape( shape ) );
        getHighlightNode().setVisible( component.isSelected() );

        blackGraphic.setPathTo( shape );

        Vector2D dir = new Vector2D.Double( start, end ).normalize();
        AbstractVector2D north = dir.getNormalVector();

        double angle = new Vector2D.Double( start, end ).getAngle();
        Rectangle r = shape.getBounds();

        area = new Area( shape );
        double windowHeightFraction = .3;
        double windowHeight = ( component.getHeight() * windowHeightFraction );
        double length = start.distance( end );
        double windowWidth = length / ( numWindows + 1.0 );
        double spacingWidth = ( length - windowWidth * numWindows ) / ( numWindows + 1 );
        double x = 0;
        north = north.getInstanceOfMagnitude( windowHeight / 2 ).getScaledInstance( 1 );
//        ArrayList windows = new ArrayList();
        for( int i = 0; i < numWindows; i++ ) {
            x += spacingWidth;
            Point2D a = dir.getInstanceOfMagnitude( x ).getDestination( start );
            a = north.getDestination( a );
            x += windowWidth;
            Point2D b = dir.getInstanceOfMagnitude( x ).getDestination( start );
            b = north.getDestination( b );
            Shape seg = LineSegment.getSegment( a, b, windowHeight );
//            windows.add( seg );
            area.subtract( new Area( seg ) );
            windowGraphics[i].setPathTo( seg );
        }

        Point a = r.getLocation();
        Point b = new Point( (int)( a.getX() + r.getWidth() ), (int)( a.getY() + r.getHeight() ) );
        Color startColor = new Color( 255, 230, 250 );
        Color endColor = new Color( 230, 255, 230 );
        areaGraphic.setPaint( new GradientPaint( a, startColor, b, endColor ) );
        areaGraphic.setPathTo( area );

        Point2D textLoc = north.getScaledInstance( -2.9 * SCALE ).getDestination( start );
        textLoc = dir.getInstanceOfMagnitude( 2 * SCALE ).getDestination( textLoc );

        String msg = text;
        if( fixedMessage != null ) {
            msg = fixedMessage;
        }
        textGraphic.setTransform( new AffineTransform() );
        textGraphic.setText( msg );
        textGraphic.scale( SCALE );
        textGraphic.setOffset( textLoc.getX(), textLoc.getY() );
        textGraphic.rotateAboutPoint( angle, textLoc.getX(), textLoc.getY() );
    }

    public CircuitComponent getCircuitComponent() {
        return component;
    }

    public void delete() {
        component.removeObserver( simpleObserver );
        module.getCCKModel().getCircuitSolver().removeSolutionListener( circuitSolutionListener );
    }

    public Shape getClipShape( PNode frame ) {
        Shape clipShape = new Area( area );

        PAffineTransform a = getLocalToGlobalTransform( null );
        PAffineTransform b = frame.getGlobalToLocalTransform( null );
        clipShape = a.createTransformedShape( clipShape );
        clipShape = b.createTransformedShape( clipShape );
        return clipShape;
    }
}
