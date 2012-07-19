// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.dev;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants;
import edu.colorado.phet.chemicalreactions.model.Atom;
import edu.colorado.phet.chemicalreactions.model.Kit;
import edu.colorado.phet.chemicalreactions.model.Molecule;
import edu.colorado.phet.chemicalreactions.model.MoleculeShape;
import edu.colorado.phet.chemicalreactions.model.Reaction;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function3;
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

    public MotionDeveloper( final Kit kit, final IClock clock, Frame frame ) {
        super( frame );
        this.kit = kit;

        new JDialog( frame ) {{
            setTitle( "Collision Development Dialog" );
            setResizable( false );

            setContentPane( new JPanel() {{

                final PNode base = new PNode();
                setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

                final Property<Boolean> showMolecules = new Property<Boolean>( true );
                final Property<Boolean> showBoundingSpheres = new Property<Boolean>( false );
                final Property<Boolean> showReactionTargets = new Property<Boolean>( true );
                final Property<Double> linearPredictionTime = new Property<Double>( 0.0 );
                final double linearPredictionTimeScale = 0.1;

                // draw a circle in model coordinates
                final Function3<ImmutableVector2D, Double, Color, Void> drawCircle = new Function3<ImmutableVector2D, Double, Color, Void>() {
                    public Void apply( ImmutableVector2D modelPoint, Double modelRadius, Color color ) {
                        ImmutableVector2D viewPoint = MODEL_VIEW_TRANSFORM.modelToView( modelPoint );
                        double viewRadius = Math.abs( MODEL_VIEW_TRANSFORM.modelToViewDeltaX( modelRadius ) );

                        base.addChild( new PhetPPath( new Ellipse2D.Double( viewPoint.getX() - viewRadius, viewPoint.getY() - viewRadius,
                                                                            viewRadius * 2, viewRadius * 2 ), null, new BasicStroke( 1 ), color ) );
                        return null;
                    }
                };

                /*---------------------------------------------------------------------------*
                * graphics display
                *----------------------------------------------------------------------------*/
                final Runnable updateGraphics = new Runnable() {
                    public void run() {
                        base.removeAllChildren();

                        ModelViewTransform transform = ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM;

                        if ( showMolecules.get() ) {
                            for ( Molecule molecule : kit.moleculesInPlayArea ) {
                                for ( Atom atom : molecule.getAtoms() ) {
                                    drawCircle.apply( atom.position.get(), atom.getRadius(), Color.BLACK );
                                }
                            }
                        }
                        if ( showBoundingSpheres.get() ) {
                            for ( Molecule molecule : kit.moleculesInPlayArea ) {
                                drawCircle.apply( molecule.position.get(), molecule.shape.getBoundingCircleRadius(), new Color( 0, 0, 0, 64 ) );
                            }
                        }
                        if ( linearPredictionTime.get() > 0 ) {
                            for ( Molecule molecule : kit.moleculesInPlayArea ) {
                                for ( Atom atom : molecule.getAtoms() ) {
                                    ImmutableVector2D currentPosition = transform.modelToView( atom.position.get() );

                                    // dotted line
                                    final DoubleGeneralPath path = new DoubleGeneralPath();
                                    path.moveTo( currentPosition.getX(), currentPosition.getY() );
                                    for ( double time = 0; time < linearPredictionTime.get(); time += 0.05 ) {
                                        ImmutableVector2D viewPosition = transform.modelToView( molecule.predictLinearAtomPosition( atom, time ) );
                                        path.lineTo( viewPosition.getX(), viewPosition.getY() );
                                    }
                                    base.addChild( new PPath() {{
                                        setPathTo( path.getGeneralPath() );
                                        setStroke( new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[]{2, 3}, 0.0f ) );
                                        setStrokePaint( new Color( 0, 255, 0, 84 ) );
                                    }} );

                                    {
                                        // actual position
                                        drawCircle.apply( molecule.predictLinearAtomPosition( atom, linearPredictionTime.get() ),
                                                          atom.getRadius(), Color.GREEN );
                                    }
                                }
                            }
                        }
                        if ( showReactionTargets.get() ) {
                            for ( Reaction reaction : kit.getReactions() ) {

                                // target outlines
                                for ( int i = 0; i < reaction.reactants.size(); i++ ) {
                                    Molecule molecule = reaction.reactants.get( i );
                                    ImmutableVector2D targetPosition = reaction.getTarget().transformedTargets.get( i );
                                    double angle = reaction.getTarget().rotation + reaction.getShape().reactantSpots.get( i ).rotation;

                                    MoleculeShape moleculeShape = molecule.shape;

                                    for ( MoleculeShape.AtomSpot spot : moleculeShape.spots ) {
                                        ImmutableVector2D spotLocalPosition = spot.position;
                                        ImmutableVector2D rotatedPosition = spotLocalPosition.getRotatedInstance( angle );
                                        ImmutableVector2D translatedPosition = rotatedPosition.plus( targetPosition );
                                        Color color = spot.element.getColor();
                                        drawCircle.apply( translatedPosition, spot.element.getRadius(), Color.RED );
                                    }
                                }

                                // path (dotted line)
                                for ( int i = 0; i < reaction.reactants.size(); i++ ) {
                                    Molecule molecule = reaction.reactants.get( i );

                                    ImmutableVector2D acceleration = reaction.getTweakAcceleration( i );
                                    double angularAcceleration = reaction.getTweakAngularAcceleration( i );

                                    for ( Atom atom : molecule.getAtoms() ) {
                                        ImmutableVector2D currentPosition = transform.modelToView( atom.position.get() );

                                        // path (dotted line)
                                        final DoubleGeneralPath path = new DoubleGeneralPath();
                                        path.moveTo( currentPosition.getX(), currentPosition.getY() );
                                        for ( double time = 0; time < reaction.getTarget().t; time += 0.05 ) {
                                            ImmutableVector2D viewPosition = transform.modelToView(
                                                    molecule.predictConstantAccelerationAtomPosition( atom, time, acceleration, angularAcceleration ) );
                                            path.lineTo( viewPosition.getX(), viewPosition.getY() );
                                        }
                                        base.addChild( new PPath() {{
                                            setPathTo( path.getGeneralPath() );
                                            setStroke( new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[]{2, 3}, 0.0f ) );
                                            setStrokePaint( Color.RED );
                                        }} );

                                        // position at linear time
                                        drawCircle.apply( molecule.predictConstantAccelerationAtomPosition( atom, linearPredictionTime.get(),
                                                                                                            acceleration, angularAcceleration ),
                                                          atom.getRadius(), Color.BLUE );
                                    }
                                }
                            }
                        }
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
                        updateGraphics.run();
                    }
                } );
                showMolecules.addObserver( graphicsObserver );
                showBoundingSpheres.addObserver( graphicsObserver );

                /*---------------------------------------------------------------------------*
                * canvas
                *----------------------------------------------------------------------------*/

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
                        add( new PropertyCheckBox( "Bounding Spheres", showBoundingSpheres ) );
                        add( new PropertyCheckBox( "Reaction Targets", showReactionTargets ) );
                        add( new JSlider( 0, 150, 0 ) {{
                            addChangeListener( new ChangeListener() {
                                public void stateChanged( ChangeEvent e ) {
                                    linearPredictionTime.set( getValue() * linearPredictionTimeScale );
                                    updateGraphics.run();
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
}
