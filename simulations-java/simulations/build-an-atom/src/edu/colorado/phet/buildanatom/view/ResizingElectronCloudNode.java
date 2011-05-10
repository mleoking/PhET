// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.Electron;
import edu.colorado.phet.buildanatom.model.ElectronShell;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Node that represents the electron shell in an atom as a "cloud" that grows
 * and shrinks depending on the number of electrons that it contains.  This
 * has also been referred to as the "Schroedinger model" representation.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class ResizingElectronCloudNode extends PNode {

    // Base color to use when drawing clouds.
    private static final Color CLOUD_BASE_COLOR = Color.BLUE;

    // Cloud version of the representation.
    private final PhetPPath electronCloudNode;

    private final ArrayList<ElectronShell> electronShells = new ArrayList<ElectronShell>();

    /**
     * Constructor.
     */
    public ResizingElectronCloudNode( final ModelViewTransform mvt, final OrbitalViewProperty orbitalView, final Atom atom ) {

        // This representation only pays attention to the first two shells.
        // This may need to be changed if this is ever expanded to use more
        // shells.
        electronShells.add( atom.getElectronShells().get( 0 ) );
        electronShells.add( atom.getElectronShells().get( 1 ) );

        // Find the range of radii to display, assuming shells are already ordered from small to large
        final double minRadius = electronShells.get( 0 ).getRadius();
        final double maxRadius = electronShells.get( electronShells.size() - 1 ).getRadius();

        SimpleObserver shellObserver = new SimpleObserver() {
            public void update() {
                Function.LinearFunction electronCountToRadiusFunction = new Function.LinearFunction( 1, getTotalElectronCapacity(), minRadius, maxRadius );
                double radius = electronCountToRadiusFunction.evaluate( getElectronCount() );
                double centerX = electronShells.get( 0 ).getCenterLocation().getX();
                double centerY = electronShells.get( 0 ).getCenterLocation().getY();
                final Shape electronShellShape = mvt.modelToView( new Ellipse2D.Double( centerX - radius,
                        centerY - radius, radius * 2, radius * 2 ) );
                electronCloudNode.setPathTo( electronShellShape );
                Function.LinearFunction electronCountToAlphaMapping = new Function.LinearFunction( 0, getTotalElectronCapacity(), 50, 175 );//Map to alpha values between 50 and 200
                int alpha = getElectronCount() == 0 ? 0 : (int) electronCountToAlphaMapping.evaluate( getElectronCount() );//But if there are no electrons, be transparent
                Paint shellGradientPaint = new RoundGradientPaint(
                        electronShellShape.getBounds2D().getCenterX(),
                        electronShellShape.getBounds2D().getCenterY(),
                        new Color( CLOUD_BASE_COLOR.getRed(), CLOUD_BASE_COLOR.getGreen(), CLOUD_BASE_COLOR.getBlue(), alpha ),
                        new Point2D.Double( electronShellShape.getBounds2D().getWidth() / 3, electronShellShape.getBounds2D().getHeight() / 3 ),
                        new Color( CLOUD_BASE_COLOR.getRed(), CLOUD_BASE_COLOR.getGreen(), CLOUD_BASE_COLOR.getBlue(), 0 ) );
                        electronCloudNode.setPaint( shellGradientPaint );
            }
        };

        // Create and add the nodes that will be used when depicting the
        // electrons as a fuzzy cloud.
        Paint initialPaint = new Color( 0, 0, 0, 0 );
        electronCloudNode = new PhetPPath( initialPaint ) {
            {
                orbitalView.addObserver( new SimpleObserver() {
                    public void update() {
                        setVisible( orbitalView.get() == OrbitalView.RESIZING_CLOUD );
                    }
                } );
                // Make fuzzy electron shell graphic pickable if visible and if it contains any electrons.
                final SimpleObserver updatePickable = new SimpleObserver() {
                    public void update() {
                        final boolean pickable = getElectronCount() > 0 && orbitalView.get() == OrbitalView.RESIZING_CLOUD;
                        setPickable( pickable );
                        setChildrenPickable( pickable );
                    }
                };
                for ( ElectronShell electronShell : electronShells ) {
                    electronShell.addObserver( updatePickable );
                }
                orbitalView.addObserver( updatePickable );

                addInputEventListener( new CursorHandler() );

                // Make it possible to grab and manipulate electrons from the
                // cloud representation, see related handling code in SubatomicParticleNode.
                addInputEventListener( new PBasicInputEventHandler() {
                    Electron grabbedElectron = null;

                    @Override
                    public void mousePressed( PInputEvent event ) {
                        // Grab an electron out from one of the shells
                        final Point2D position = mvt.viewToModel( event.getPositionRelativeTo( getParent() ) );
                        grabbedElectron = atom.removeElectron();
                        grabbedElectron.setUserControlled( true );
                        grabbedElectron.setPositionAndDestination( position );
                    }

                    @Override
                    public void mouseDragged( PInputEvent event ) {
                        PDimension delta = event.getDeltaRelativeTo( getParent() );
                        grabbedElectron.translate( mvt.viewToModelDelta( new ImmutableVector2D( delta ) ) );
                        grabbedElectron.setDestination( grabbedElectron.getPosition() ); //So it doesn't run away
                    }

                    @Override
                    public void mouseReleased( PInputEvent event ) {
                        grabbedElectron.setUserControlled( false );
                        grabbedElectron = null;
                    }
                } );
            }
        };

        addChild( electronCloudNode );

        for ( ElectronShell electronShell : electronShells ) {
            electronShell.addObserver( shellObserver );
        }
    }

    private int getElectronCount() {
        int currentElectronCount = 0;
        for ( ElectronShell electronShell : electronShells ) {
            currentElectronCount += electronShell.getNumElectrons();
        }
        return currentElectronCount;
    }

    private int getTotalElectronCapacity() {
        int totalElectronCapacity = 0;
        for ( ElectronShell electronShell : electronShells ) {
            totalElectronCapacity += electronShell.getElectronCapacity();
        }
        return totalElectronCapacity;
    }
}
