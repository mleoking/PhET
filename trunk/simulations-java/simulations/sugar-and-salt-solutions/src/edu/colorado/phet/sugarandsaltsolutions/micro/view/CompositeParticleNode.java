// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.PFrame;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createOffsetScaleMapping;

/**
 * Shows all components of a molecule, used in bar chart legends, but not used in the beaker play area--in that case each atom is a top-level node.
 *
 * @author Sam Reid
 */
public class CompositeParticleNode<T extends Particle> extends PNode {
    public CompositeParticleNode( ModelViewTransform transform, Iterable<Constituent<T>> molecule, ObservableProperty<Boolean> showChargeColor ) {
        for ( Constituent constituent : molecule ) {

            //Put particles at the correct relative locations and add as children
            SphericalParticle particle = (SphericalParticle) constituent.particle;
            particle.position.set( constituent.location );
            addChild( new SphericalParticleNode( transform, particle, showChargeColor ) );
        }
    }

    //Test main
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PFrame() {{

                    //Large transform is needed since nodes are rasterized
                    getCanvas().getLayer().addChild( new CompositeParticleNode<SphericalParticle>( createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 1E11 ), new SucroseMolecule( ZERO ), new Property<Boolean>( false ) ) {{
                        double width = getFullBounds().getWidth();
                        System.out.println( "width = " + width );
                        scale( 100 );
                    }} );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                }}.setVisible( true );
            }
        } );
    }
}
