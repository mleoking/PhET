// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.dev;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants;
import edu.colorado.phet.chemicalreactions.model.Atom;
import edu.colorado.phet.chemicalreactions.model.Kit;
import edu.colorado.phet.chemicalreactions.model.Molecule;
import edu.colorado.phet.chemicalreactions.model.MoleculeShape;
import edu.colorado.phet.chemicalreactions.model.Reaction;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM;

/**
 * For development of the molecule collision "attraction"
 */
public class MotionDeveloper extends JDialog {
    public final Kit kit;

    private final PNode base = new PNode();
    private final PNode graph = new PNode();

    private static final int GRAPH_HEIGHT = 100;

    private final Property<Boolean> showMolecules = new Property<Boolean>( true );
    private final Property<Boolean> showBoundingSpheres = new Property<Boolean>( false );
    private final Property<Boolean> showReactionTargets = new Property<Boolean>( true );
    private final Property<Boolean> showLinearPrediction = new Property<Boolean>( true );
    private final Property<Boolean> showLeastSquaresTimeTarget = new Property<Boolean>( false );
    private final Property<Boolean> showLeastSquaresRotationTimeTarget = new Property<Boolean>( false );
    private final Property<Boolean> updateGraph = new Property<Boolean>( true );
    private final Property<Double> linearPredictionTime = new Property<Double>( 0.0 );
    private final Property<Double> leastSquaresTime = new Property<Double>( 0.0 );
    private final double linearPredictionTimeScale = 0.1;

    private final Property<Integer> possibleReactionQuantity = new Property<Integer>( 0 );

    public MotionDeveloper( final Kit kit, final IClock clock, Frame frame ) {
        super( frame );
        this.kit = kit;

        new JDialog( frame ) {{
            setTitle( "Collision Development Dialog" );
            setResizable( false );

            setContentPane( new JPanel() {{
                setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

                /*---------------------------------------------------------------------------*
                * graphics display
                *----------------------------------------------------------------------------*/
                final Runnable updateGraphics = new Runnable() {
                    public void run() {
                        base.removeAllChildren();

                        final ModelViewTransform transform = ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM;

                        if ( showMolecules.get() ) {
                            for ( Molecule molecule : kit.moleculesInPlayArea ) {
                                for ( Atom atom : molecule.getAtoms() ) {
                                    drawCircle( atom.position.get(), atom.getRadius(), Color.BLACK );
                                }
                            }
                        }
                        if ( showBoundingSpheres.get() ) {
                            for ( Molecule molecule : kit.moleculesInPlayArea ) {
                                drawCircle( molecule.position.get(), molecule.shape.getBoundingCircleRadius(), new Color( 0, 0, 0, 64 ) );
                            }
                        }
                        if ( showLinearPrediction.get() && linearPredictionTime.get() > 0 ) {
                            for ( final Molecule molecule : kit.moleculesInPlayArea ) {
                                for ( final Atom atom : molecule.getAtoms() ) {
                                    drawTimedPath( new Function1<Double, Vector2D>() {
                                        public Vector2D apply( Double time ) {
                                            return transform.modelToView( molecule.predictLinearAtomPosition( atom, time ) );
                                        }
                                    }, new Color( 0, 255, 0, 84 ), true );

                                    // actual position
                                    drawCircle( molecule.predictLinearAtomPosition( atom, linearPredictionTime.get() ),
                                                atom.getRadius(), Color.GREEN );
                                }
                            }
                        }
                        if ( showReactionTargets.get() ) {
                            for ( Reaction reaction : kit.getReactions() ) {
                                double displayTime = Math.min( leastSquaresTime.get(), reaction.getTarget().t );
                                drawReactionTarget( reaction.getTarget(), Color.RED, new Color( 255, 0, 0, 84 ), new Color( 255, 0, 0, 84 ), displayTime );
                            }
                        }
                        List<Reaction> possibleReactions = kit.getAllPossibleReactions();
                        possibleReactionQuantity.set( possibleReactions.size() );

                        if ( showLeastSquaresTimeTarget.get() ) {
                            for ( Reaction reaction : possibleReactions ) {
                                Reaction.ReactionTarget target = reaction.computeForTime( linearPredictionTime.get() );
                                double displayTime = Math.min( leastSquaresTime.get(), target.t );

                                Color color = getTargetColor( target );
                                Color fadedColor = alphaMultiplier( color, 180 );

                                drawReactionTarget( target, color, fadedColor, fadedColor, displayTime );
                            }
                        }

                        if ( showLeastSquaresRotationTimeTarget.get() ) {
                            for ( Reaction reaction : possibleReactions ) {
                                double time = linearPredictionTime.get();
                                double rotation = reaction.computeRotationFromTime( 0 );
                                Reaction.ReactionTarget target = reaction.computeForTimeWithRotation( time, rotation );
                                double displayTime = Math.min( leastSquaresTime.get(), target.t );

                                Color color = getTargetColor( target );
                                Color fadedColor = alphaMultiplier( color, 180 );

                                drawReactionTarget( target, color, fadedColor, fadedColor, displayTime );
                            }
                        }

                        drawGraph();
                    }
                };
                SimpleObserver graphicsObserver = new SimpleObserver() {
                    public void update() {
                        updateGraphics.run();
                    }
                };

                kit.timestepCompleted.addListener( new VoidFunction1<Double>() {
                    public void apply( Double dt ) {
                        linearPredictionTime.set( linearPredictionTime.get() - dt );
                        leastSquaresTime.set( leastSquaresTime.get() - dt );
                        updateGraphics.run();
                    }
                } );
                showMolecules.addObserver( graphicsObserver );
                showBoundingSpheres.addObserver( graphicsObserver );
                showLinearPrediction.addObserver( graphicsObserver );
                showReactionTargets.addObserver( graphicsObserver );
                showLeastSquaresTimeTarget.addObserver( graphicsObserver );
                showLeastSquaresRotationTimeTarget.addObserver( graphicsObserver );
                updateGraph.addObserver( graphicsObserver );

                /*---------------------------------------------------------------------------*
                * canvas
                *----------------------------------------------------------------------------*/

                // canvas for graphing scores
                add( new PCanvas() {{
                    removeInputEventListener( getZoomEventHandler() );
                    removeInputEventListener( getPanEventHandler() );
                    getLayer().addChild( graph );
                    PBounds bounds = kit.getLayoutBounds().getAvailablePlayAreaViewBounds();
                    setPreferredSize( new Dimension( (int) bounds.width, GRAPH_HEIGHT ) );
                    setBackground( Color.GRAY );
                }} );

                // canvas that shows the play area (and any debugging information)
                add( new PCanvas() {{
                    removeInputEventListener( getZoomEventHandler() );
                    removeInputEventListener( getPanEventHandler() );
                    getLayer().addChild( base );
                    PBounds bounds = kit.getLayoutBounds().getAvailablePlayAreaViewBounds();
                    setPreferredSize( new Dimension( (int) bounds.width, (int) bounds.height ) );

                    // compensate for the play area's offset in the main window
                    base.setOffset( -bounds.getMinX(), -bounds.getMinY() );
                }} );

                /*---------------------------------------------------------------------------*
                * controls
                *----------------------------------------------------------------------------*/

                add( new JPanel() {{
                    setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
                    add( new PiccoloClockControlPanel( clock ) );
                    add( new JPanel() {{
                        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
                        add( new PropertyCheckBox( "Molecules", showMolecules ) );
                        add( new PropertyCheckBox( "Linear Prediction", showLinearPrediction ) );
                        add( new PropertyCheckBox( "Bounding Spheres", showBoundingSpheres ) );
                        add( new PropertyCheckBox( "Reaction Targets", showReactionTargets ) );
                        add( new PropertyCheckBox( "Full Least Squares Targets", showLeastSquaresTimeTarget ) );
                        add( new PropertyCheckBox( "Rotation Least Squares Targets", showLeastSquaresRotationTimeTarget ) );
                        add( new PropertyCheckBox( "Update Graph", updateGraph ) );
                        add( new JSlider( 0, 150, 0 ) {{
                            addChangeListener( new ChangeListener() {
                                public void stateChanged( ChangeEvent e ) {
                                    linearPredictionTime.set( getValue() * linearPredictionTimeScale );
                                    updateGraphics.run();
                                }
                            } );
                        }} );
                        add( new JSlider( 0, 150, 0 ) {{
                            addChangeListener( new ChangeListener() {
                                public void stateChanged( ChangeEvent e ) {
                                    leastSquaresTime.set( getValue() * linearPredictionTimeScale );
                                    updateGraphics.run();
                                }
                            } );
                        }} );
                        add( new JLabel() {{
                            possibleReactionQuantity.addObserver( new SimpleObserver() {
                                public void update() {
                                    setText( "Reactions possible: " + possibleReactionQuantity.get() );
                                }
                            } );
                        }} );
                        add( new JButton( "Graph score vs. time" ) {{
                            addActionListener( new ActionListener() {
                                public void actionPerformed( ActionEvent e ) {
                                    drawGraph();
                                }
                            } );
                        }} );
                    }} );
                }} );
            }} );
            pack();
            SwingUtils.centerInParent( this );
        }}.setVisible( true );
    }

    private void drawGraph() {
        graph.removeAllChildren();

        if ( !updateGraph.get() ) {
            return;
        }

        List<Reaction> possibleReactions = kit.getAllPossibleReactions();

        double timeMultiplier = 40;
        double timeX = linearPredictionTime.get() * timeMultiplier;

        graph.addChild( new PhetPPath( new Line2D.Double( timeX, 0, timeX, GRAPH_HEIGHT ), new BasicStroke( 1 ), Color.BLACK ) );

        for ( Reaction reaction : possibleReactions ) {
            for ( double t = 0.01; t < 15; t += 0.03 ) {
                Reaction.ReactionTarget target = reaction.computeForTime( t );

                Color color = getTargetColor( target );

                double radius = 0.5;
                double x = t * timeMultiplier;
                double y = GRAPH_HEIGHT - target.getApproximateAccelerationMagnitude() * Math.pow( t, 1.5 ) / 50;

                graph.addChild( new PhetPPath( new Ellipse2D.Double( x - radius, y - radius, radius * 2, radius * 2 ), color, null, Color.BLACK ) );
            }
        }
    }

    private Color getTargetColor( Reaction.ReactionTarget target ) {
        Color color = null;

        switch( target.isValidReactionTarget( kit ) ) {
            case VIABLE:
                double acceleration = target.getApproximateAccelerationMagnitude();
                double score = acceleration * target.t;
                double minThreshold = 400;
                double maxThreshold = 1400;
                double ratio = ( score - minThreshold ) / ( maxThreshold - minThreshold );
                color = new Color( 0, 0, 255, 127 + (int) ( 128 * MathUtil.clamp( 0, 1 - ratio, 1 ) ) );
                break;
            case OUT_OF_BOUNDS:
                color = new Color( 250, 250, 250 );
                break;
            case TOO_MUCH_ACCELERATION:
                color = new Color( 255, 220, 220 );
                break;
            case PREDICTED_SELF_COLLISION:
                color = new Color( 255, 220, 255 );
                break;
        }
        return color;
    }

    private void drawReactionTarget( Reaction.ReactionTarget reactionTarget, Color timedColor, Color pathColor, Color targetColor, double timedTime ) {
        final ModelViewTransform transform = ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM;

        Reaction reaction = reactionTarget.reaction;

        // target outlines
        for ( int i = 0; i < reaction.reactants.size(); i++ ) {
            Molecule molecule = reaction.reactants.get( i );
            Vector2D targetPosition = reactionTarget.transformedTargets.get( i );
            double angle = reactionTarget.rotation + reaction.getShape().reactantSpots.get( i ).rotation;

            drawMolecule( molecule.shape, targetPosition, angle, targetColor );
        }

        // path (dotted line)
        for ( int i = 0; i < reaction.reactants.size(); i++ ) {
            final Molecule molecule = reaction.reactants.get( i );

            final Vector2D acceleration = reactionTarget.getTweakAcceleration( molecule );
            final double angularAcceleration = reactionTarget.getTweakAngularAcceleration( molecule );

            for ( final Atom atom : molecule.getAtoms() ) {
                drawTimedPath( new Function1<Double, Vector2D>() {
                    public Vector2D apply( Double time ) {
                        return transform.modelToView(
                                molecule.predictConstantAccelerationAtomPosition( atom, time, acceleration, angularAcceleration ) );
                    }
                }, pathColor, true, reactionTarget.t );

                // position at timed time
                if ( timedTime > 0 ) {
                    drawCircle( molecule.predictConstantAccelerationAtomPosition( atom, timedTime,
                                                                                  acceleration, angularAcceleration ),
                                atom.getRadius(), timedColor );
                }
            }
        }
    }

    // draw a circle in model coordinates
    private void drawCircle( Vector2D modelPoint, Double modelRadius, Color color ) {
        Vector2D viewPoint = MODEL_VIEW_TRANSFORM.modelToView( modelPoint );
        double viewRadius = Math.abs( MODEL_VIEW_TRANSFORM.modelToViewDeltaX( modelRadius ) );

        base.addChild( new PhetPPath( new Ellipse2D.Double( viewPoint.getX() - viewRadius, viewPoint.getY() - viewRadius,
                                                            viewRadius * 2, viewRadius * 2 ), null, new BasicStroke( 1 ), color ) );
    }

    private PPath createPath( List<Vector2D> points ) {
        final DoubleGeneralPath path = new DoubleGeneralPath();
        if ( !points.isEmpty() ) {
            path.moveTo( points.get( 0 ).x, points.get( 0 ).y );
        }
        for ( Vector2D point : points ) {
            path.lineTo( point.x, point.y );
        }
        return new PPath() {{
            setPathTo( path.getGeneralPath() );
        }};
    }

    // create a path from a time => point mapping for the current linearPredictionTime
    private void drawTimedPath( final Function1<Double, Vector2D> pointAtTime, Color color, Boolean dotted ) {
        drawTimedPath( pointAtTime, color, dotted, linearPredictionTime.get() );
    }

    private void drawTimedPath( final Function1<Double, Vector2D> pointAtTime, Color color, Boolean dotted, final double maxTime ) {
        PPath path = createPath( new ArrayList<Vector2D>() {{
            for ( double time = 0; time < maxTime; time += 0.05 ) {
                add( pointAtTime.apply( time ) );
            }
        }} );
        if ( dotted ) {
            dotPath( path );
        }
        path.setStrokePaint( color );
        base.addChild( path );
    }

    private void drawMolecule( MoleculeShape shape, Vector2D position, double angle, Color color ) {
        for ( MoleculeShape.AtomSpot spot : shape.spots ) {
            Vector2D spotLocalPosition = spot.position;
            Vector2D rotatedPosition = spotLocalPosition.getRotatedInstance( angle );
            Vector2D translatedPosition = rotatedPosition.plus( position );
            drawCircle( translatedPosition, spot.element.getRadius(), color );
        }
    }

    private static void dotPath( PPath path ) {
        path.setStroke( new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[]{2, 3}, 0.0f ) );
    }

    private static Color alphaMultiplier( Color color, int alpha ) {
        return new Color( color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() * alpha / 255 );
    }
}
