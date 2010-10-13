package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.Electron;
import edu.colorado.phet.buildanatom.model.ElectronShell;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Node that represents an electron shell, aka "orbit", in the view.  This
 * node is able to switch between different representations of a shell.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class ElectronShellNode extends PNode {

    // Base color to use when drawing clouds.
    private static final Color CLOUD_BASE_COLOR = Color.BLUE;

    // Stroke for drawing the electron orbits.
    private static final Stroke ELECTRON_SHELL_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_BEVEL, 0, new float[] { 3, 3 }, 0 );

    // Paint for the electron orbitals.
    private static final Paint ELECTRON_SHELL_STROKE_PAINT = new Color( 0, 0, 255, 100 );

    // Cloud version of the representation.
    private final PhetPPath electronCloudNode;

    /**
     * Constructor.
     */
    public ElectronShellNode( final ModelViewTransform2D mvt, final BooleanProperty viewOrbitals, final Atom atom, final ElectronShell electronShell ) {

        final Shape electronShellShape = mvt.createTransformedShape( new Ellipse2D.Double(
                -electronShell.getRadius(),
                -electronShell.getRadius(),
                electronShell.getRadius() * 2,
                electronShell.getRadius() * 2 ) );

        // Create and add the node that will depict the shell as a circular
        // orbit.
        final PNode electronOrbitNode = new PhetPPath( electronShellShape, ELECTRON_SHELL_STROKE, ELECTRON_SHELL_STROKE_PAINT ) { {
                setOffset( atom.getPosition() );
                final SimpleObserver updateVisibility = new SimpleObserver() {
                    public void update() {
                        setVisible( viewOrbitals.getValue() );
                    }
                };
                viewOrbitals.addObserver( updateVisibility );
            } };
        addChild( electronOrbitNode );

        // Create and add the nodes that will be used when depicting the
        // electrons as a fuzzy cloud.
        Paint initialPaint = new Color(0, 0, 0, 0);
        electronCloudNode = new PhetPPath( electronShellShape, initialPaint ) { {
                viewOrbitals.addObserver( new SimpleObserver() {
                    public void update() {
                        setVisible( !viewOrbitals.getValue() );
                    }
                } );
                electronShell.addObserver( new SimpleObserver() {
                    public void update() {
                        Function.LinearFunction electronCountToAlphaMapping = new Function.LinearFunction( 0,electronShell.getElectronCapacity(),50,200);//Map to alpha values between 50 and 200
                        int alpha = electronShell.getNumElectrons()==0?0:(int) electronCountToAlphaMapping.evaluate( electronShell.getNumElectrons() );//But if there are no electrons, be transparent
                        Paint shellGradientPaint = new RoundGradientPaint(
                                electronShellShape.getBounds2D().getCenterX(),
                                electronShellShape.getBounds2D().getCenterY(),
                                new Color( CLOUD_BASE_COLOR.getRed(), CLOUD_BASE_COLOR.getGreen(), CLOUD_BASE_COLOR.getBlue(), alpha ),
                                new Point2D.Double( electronShellShape.getBounds2D().getWidth() / 3, electronShellShape.getBounds2D().getHeight() / 3 ),
                                new Color( CLOUD_BASE_COLOR.getRed(), CLOUD_BASE_COLOR.getGreen(), CLOUD_BASE_COLOR.getBlue(), 0 ) );
                        setPaint( shellGradientPaint );
                    }
                } );
                //Make fuzzy electron shell graphic pickable if visible and if it contains any electrons
                final SimpleObserver updatePickable = new SimpleObserver() {
                    public void update() {
                        final boolean pickable = electronShell.getNumElectrons() > 0 && !viewOrbitals.getValue();
                        setPickable( pickable );
                        setChildrenPickable( pickable );
                    }
                };
                electronShell.addObserver( updatePickable );
                viewOrbitals.addObserver( updatePickable );
                addInputEventListener( new CursorHandler() );

                //Make it possible to grab and manipulate electrons from the cloud representation, see related handling code in SubatomicParticleNode
                addInputEventListener( new PBasicInputEventHandler() {
                    Electron grabbedElectron = null;
                    @Override
                    public void mousePressed( PInputEvent event ) {
                        //Grab an electron out from the shell
                        final Point2D position = mvt.viewToModel( event.getCanvasPosition() );
                        grabbedElectron=electronShell.getClosestElectron( position );
                        grabbedElectron.setUserControlled( true );
                        grabbedElectron.setPositionAndDestination( position );
                    }

                    @Override
                    public void mouseDragged( PInputEvent event ) {
                        grabbedElectron.translate( mvt.viewToModelDifferential(event.getCanvasDelta().width,event.getCanvasDelta().height ));
                        grabbedElectron.setDestination( grabbedElectron.getPosition() ); //So it doesn't run away
                    }

                    @Override
                    public void mouseReleased( PInputEvent event ) {
                        grabbedElectron.setUserControlled( false );
                        grabbedElectron=null;
                    }
                } );
        }
        };
//        viewOrbitals.setValue( false );//Uncomment this line to set the default view to cloud
        addChild( electronCloudNode );
    }
}
