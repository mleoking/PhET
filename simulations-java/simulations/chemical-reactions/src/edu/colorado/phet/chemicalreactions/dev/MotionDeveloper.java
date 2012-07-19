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
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
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
                final Property<Double> linearPredictionTime = new Property<Double>( 0.0 );
                final double linearPredictionTimeScale = 0.1;

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
                                    ImmutableVector2D viewPosition = transform.modelToView( atom.position.get() );
                                    double radius = transform.modelToViewDeltaX( atom.getRadius() );

                                    base.addChild( new PhetPPath( new Ellipse2D.Double( viewPosition.getX() - radius,
                                                                                        viewPosition.getY() - radius,
                                                                                        2 * radius, 2 * radius ) ) );
                                }
                            }
                        }
                        if ( linearPredictionTime.get() > 0 ) {
                            for ( Molecule molecule : kit.moleculesInPlayArea ) {
                                for ( Atom atom : molecule.getAtoms() ) {
                                    double radius = transform.modelToViewDeltaX( atom.getRadius() );

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
                                        ImmutableVector2D viewPosition = transform.modelToView( molecule.predictLinearAtomPosition( atom, linearPredictionTime.get() ) );

                                        base.addChild( new PhetPPath( new Ellipse2D.Double( viewPosition.getX() - radius,
                                                                                            viewPosition.getY() - radius,
                                                                                            2 * radius, 2 * radius ),
                                                                      new BasicStroke( 1 ), Color.GREEN ) );
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

                /*---------------------------------------------------------------------------*
                * canvas
                *----------------------------------------------------------------------------*/

                // canvas that shows the play area (and any debugging information)
                add( new PCanvas() {{
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
