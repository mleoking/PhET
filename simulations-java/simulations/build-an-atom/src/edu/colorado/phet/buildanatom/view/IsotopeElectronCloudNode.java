// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that represents the electron shells in an isotope as a "cloud" that
 * grows and shrinks depending on the number of electrons that it contains.
 * This particular class implements behavior needed for the Isotopes
 * simulation, which is somewhat different from that needed for Build an
 * Atom.
 *
 * @author John Blanco
 */
public class IsotopeElectronCloudNode extends PNode {

    // Base color to use when drawing clouds.
    private static final Color CLOUD_BASE_COLOR = Color.BLUE;
    private static final int MAX_ELECTRONS = 10; // For neon.

    // Cloud version of the representation.
    private final PhetPPath electronCloudNode;

    /**
     * Constructor.
     */
    public IsotopeElectronCloudNode( final ModelViewTransform mvt, final OrbitalViewProperty orbitalView, final Atom atom ) {

        // Add a listener that will update the cloud's appearance when the
        // atom configuration changes.
        atom.addAtomListener( new AtomListener.Adapter() {
            @Override
            public void configurationChanged() {
                double radius = getElectronShellDiameter( atom.getNumProtons() );
                double centerX = atom.getPosition().getX();
                double centerY = atom.getPosition().getY();
                final Shape electronShellShape = mvt.modelToView( new Ellipse2D.Double( centerX - radius,
                        centerY - radius, radius * 2, radius * 2 ) );
                electronCloudNode.setPathTo( electronShellShape );
                Function.LinearFunction electronCountToAlphaMapping = new Function.LinearFunction( 0, MAX_ELECTRONS, 80, 110 );//Map to alpha values
                int alpha = atom.getNumElectrons() == 0 ? 0 : (int) electronCountToAlphaMapping.evaluate( atom.getNumElectrons() );//But if there are no electrons, be transparent
                // Create the paint for depicting the electron cloud.  This is lighter
                // in the center and darker as we move out so that the nucleus can be
                // clearly seen, and it looks like a cloud but with a distinct edge.
                Paint electronShellPaint;
                if (radius > 0){
                    electronShellPaint = new RoundGradientPaint(
                            electronShellShape.getBounds2D().getCenterX(),
                            electronShellShape.getBounds2D().getCenterY(),
                            new Color( CLOUD_BASE_COLOR.getRed(), CLOUD_BASE_COLOR.getGreen(), CLOUD_BASE_COLOR.getBlue(), 0 ),
                            new Point2D.Double( electronShellShape.getBounds2D().getWidth() / 3, electronShellShape.getBounds2D().getHeight() / 3 ),
                            new Color( CLOUD_BASE_COLOR.getRed(), CLOUD_BASE_COLOR.getGreen(), CLOUD_BASE_COLOR.getBlue(), alpha ) );
                }
                else{
                    electronShellPaint = CLOUD_BASE_COLOR;
                }
                electronCloudNode.setPaint( electronShellPaint );
            }
        } );

        Paint initialPaint = new Color( 0, 0, 0, 0 );
        electronCloudNode = new PhetPPath( initialPaint ) {
            {
                orbitalView.addObserver( new SimpleObserver() {
                    public void update() {
                        setVisible( orbitalView.getValue() == OrbitalView.ISOTOPES_RESIZING_CLOUD );
                    }
                } );
            }
        };

        addChild( electronCloudNode );
    }

    /**
     * Maps a number of electrons to a diameter in screen coordinates for the
     * electron shell.  This mapping function is based on the real size
     * relationships between the various atoms, but has some tweakable
     * parameters to reduce the range and scale to provide values that
     * are usable for our needs on the canvas.
     */
    private double getElectronShellDiameter( int numElectrons ){
        if (mapElectronCountToRadius.containsKey( numElectrons )){
            return reduceRadiusRange( mapElectronCountToRadius.get( numElectrons ) );
        }
        else{
            if ( numElectrons > MAX_ELECTRONS ){
                System.out.println(getClass().getName() + " - Warning: Atom has more than supported number of electrons, " + numElectrons);
            }
            return 0;
        }
    }

    // This data structure maps atomic number of atomic radius.  The values
    // are the covalent radii, and were taken from a Wikipedia entry entitled
    // "Atomic radii of the elements".  Values are in picometers.
    private static Map<Integer, Double> mapElectronCountToRadius = new HashMap<Integer, Double>(){{
        put(1, 38d);   // Hydrogen
        put(2, 32d);   // Helium
        put(3, 134d);  // Lithium
        put(4, 90d);   // Beryllium
        put(5, 82d);   // Boron
        put(6, 77d);   // Carbon
        put(7, 75d);   // Nitrogen
        put(8, 73d);   // Oxygen
        put(9, 71d);   // Fluorine
        put(10, 69d);  // Neon
    }};

    // Determine the min and max radii of the supported atoms.
    private static double minShellRadius, maxShellRadius;
    static {
        minShellRadius = Double.MAX_VALUE;
        maxShellRadius = 0;
        for ( Double radius : mapElectronCountToRadius.values()){
            if ( radius > maxShellRadius ){
                maxShellRadius = radius;
            }
            if ( radius < minShellRadius ){
                minShellRadius = radius;
            }
        }
    }

    /**
     * This method increases the value of the smaller radius values and
     * decreases the value of the larger ones.  This effectively reduces
     * the range of radii values used.
     *
     * This is a very specialized function for the purposes of this class.
     */
    private double reduceRadiusRange( double value ){
        // The following two factors define the way in which the input values
        // are increased or decreased.  These values can be adjusted as needed
        // to make the cloud size appear as desired.  In general, the min
        // value will be somewhat larger than the smallest value in the table,
        // and the max value will be somewhat smaller than the largest value
        // in the table.
        double minChangedRadius = 40;
        double maxChangedRadius = 110;

        Function.LinearFunction compressionFunction = new Function.LinearFunction( minShellRadius, maxShellRadius, minChangedRadius, maxChangedRadius );
        return compressionFunction.evaluate( value );
    }
}
