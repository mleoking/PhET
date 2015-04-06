// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.CCKSimSharing;
import edu.colorado.phet.circuitconstructionkit.model.NoiseGenerator;
import edu.colorado.phet.circuitconstructionkit.model.analysis.CircuitSolutionListener;
import edu.colorado.phet.circuitconstructionkit.model.components.CircuitComponent;
import edu.colorado.phet.circuitconstructionkit.model.components.SeriesAmmeter;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.ComponentNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.DelayedRunner;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.LineSegment;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PAffineTransform;

/**
 * User: Sam Reid
 * Date: May 29, 2004
 * Time: 12:57:37 AM
 */
public class SeriesAmmeterNode extends ComponentNode {
    private static final DecimalFormat DF = new DecimalFormat( "0.00" );
    private SeriesAmmeter component;
    private CCKModule module;
    private static final double SCALE = 1.0 / 60.0;
    private Stroke stroke = new BasicStroke( (float) ( 5 * SCALE ) );
    private Font font = new PhetFont( Font.BOLD, 17 );
    private Shape shape;
    private String text = CCKResources.getString( "SeriesAmmeterGraphic.Ammeter" );
    private SimpleObserver simpleObserver;
    private CircuitSolutionListener circuitSolutionListener;
    private int numWindows = 3;
    private double previouslyReportedCurrent = 0;

    private PhetPPath blackGraphic;
    private PhetPPath[] windowGraphics = new PhetPPath[numWindows];
    private PhetPPath areaGraphic;
    private PText textGraphic;
    private Area area;
    private final DelayedRunner runner = new DelayedRunner();
    private double randomness = 0;
    private boolean noiseDirty = true;
    private double noise = 0;

    public SeriesAmmeterNode( JComponent parent, final SeriesAmmeter component, final CCKModule module ) {
        super( module.getCCKModel(), component, parent, module );
        this.component = component;
        this.module = module;

        simpleObserver = new SimpleObserver() {
            public void update() {
                changed();
            }
        };
        component.addObserver( simpleObserver );

        final Runnable update = new Runnable() {
            @Override public void run() {
                double current = component.getCurrent();
                if ( CCKModule.randomFluctuations && noiseDirty ) {
                    double readout = NoiseGenerator.getReadout( current );
                    noise = readout - current;
                    noiseDirty = false;
                }
                if ( CCKModule.randomFluctuations ) {
                    current = current + noise;
                }
                String form = DF.format( Math.abs( current ) );
                text = "" + form + " " + CCKResources.getString( "SeriesAmmeterGraphic.Amps" );
                changed();
            }
        };
        circuitSolutionListener = new CircuitSolutionListener() {
            public void circuitSolverFinished() {
                update.run();
            }
        };
        CCKModule.fluctuateRandomly( new Runnable() {
            @Override public void run() {
                noiseDirty = true;
                update.run();
            }
        } );
        module.getCCKModel().getCircuitSolver().addSolutionListener( circuitSolutionListener );
        blackGraphic = new PhetPPath( new Area(), stroke, Color.black );

        addChild( blackGraphic );
        areaGraphic = new PhetPPath( new Area(), Color.black );
        addChild( areaGraphic );
        for ( int i = 0; i < windowGraphics.length; i++ ) {
            windowGraphics[i] = new PhetPPath( new Area(), new BasicStroke( (float) ( 1.2f * SCALE ) ), Color.black );
            addChild( windowGraphics[i] );
        }
        textGraphic = new PText();
        textGraphic.setFont( font );
        textGraphic.setVisible( true );
        getHighlightNode().setStroke( new BasicStroke( (float) ( 3f * SCALE ) ) );
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
        BasicStroke stroke = new BasicStroke( (float) ( 12 * SCALE ) );
        getHighlightNode().setPathTo( stroke.createStrokedShape( shape ) );
        getHighlightNode().setVisible( component.isSelected() );

        blackGraphic.setPathTo( shape );

        MutableVector2D dir = new MutableVector2D( start, end ).normalize();
        Vector2D north = dir.getPerpendicularVector();

        double angle = new MutableVector2D( start, end ).getAngle();
        Rectangle r = shape.getBounds();

        area = new Area( shape );
        double windowHeightFraction = .3;
        double windowHeight = ( component.getHeight() * windowHeightFraction );
        double length = start.distance( end );
        double windowWidth = length / ( numWindows + 1.0 );
        double spacingWidth = ( length - windowWidth * numWindows ) / ( numWindows + 1 );
        double x = 0;
        north = north.getInstanceOfMagnitude( windowHeight / 2 ).times( 1 );
        for ( int i = 0; i < numWindows; i++ ) {
            x += spacingWidth;
            Point2D a = dir.getInstanceOfMagnitude( x ).getDestination( start );
            a = north.getDestination( a );
            x += windowWidth;
            Point2D b = dir.getInstanceOfMagnitude( x ).getDestination( start );
            b = north.getDestination( b );
            Shape seg = LineSegment.getSegment( a, b, windowHeight );
            area.subtract( new Area( seg ) );
            windowGraphics[i].setPathTo( seg );
        }

        Point a = r.getLocation();
        Point b = new Point( (int) ( a.getX() + r.getWidth() ), (int) ( a.getY() + r.getHeight() ) );
        Color startColor = new Color( 255, 230, 250 );
        Color endColor = new Color( 230, 255, 230 );
        areaGraphic.setPaint( new GradientPaint( a, startColor, b, endColor ) );
        areaGraphic.setPathTo( area );

        Point2D textLoc = north.times( -2.9 * SCALE ).getDestination( start );
        textLoc = dir.getInstanceOfMagnitude( 2 * SCALE ).getDestination( textLoc );

        String msg = text;
        textGraphic.setTransform( new AffineTransform() );
        textGraphic.setText( msg );
        textGraphic.scale( SCALE );
        textGraphic.setOffset( textLoc.getX(), textLoc.getY() );
        textGraphic.rotate( angle );

        // Send sim sharing message if reading has changed. Thresholding was
        // necessary to avoid spurious messages.
        if ( Math.abs( component.getCurrent() - previouslyReportedCurrent ) > 0.01 ) {
            runner.set( new Runnable() {
                public void run() {
                    SimSharingManager.sendModelMessage( getBranch().getModelComponentID(),
                                                        ModelComponentTypes.modelElement,
                                                        CCKSimSharing.ModelActions.measuredCurrentChanged,
                                                        new ParameterSet( new Parameter( new ParameterKey( "current" ), DF.format( component.getCurrent() ) ) ) );
                    previouslyReportedCurrent = component.getCurrent();
                }
            } );
        }
    }

    public CircuitComponent getCircuitComponent() {
        return component;
    }

    public void delete() {
        super.delete();
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
