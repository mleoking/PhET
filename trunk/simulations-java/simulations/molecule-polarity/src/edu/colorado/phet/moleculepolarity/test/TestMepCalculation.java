// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.test;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.BitSet;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Point3f;

import org.jmol.api.VolumeDataInterface;
import org.jmol.jvxl.data.VolumeData;
import org.jmol.modelset.Atom;
import org.jmol.quantum.MepCalculation;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Feasibility test to see whether MEP data can be computed with JMol and used productively elsewhere.
 *
 * @author Sam Reid
 */
public class TestMepCalculation {
    public static void main( String[] args ) {
        MepCalculation mepCalculation = new MepCalculation();
        final VolumeDataInterface volumeData = new VolumeData();
        int nx = 50;
        volumeData.setVoxelCounts( nx, nx, nx );
        volumeData.setVoxelData( new float[nx][nx][nx] );
        volumeData.setVolumetricOrigin( 0, 0, 0 );
        volumeData.setVolumetricVector( 0, 1, 0, 0 );
        volumeData.setVolumetricVector( 1, 0, 1, 0 );
        volumeData.setVolumetricVector( 2, 0, 0, 1 );
        BitSet selected = new BitSet();
        selected.set( 0, 3, true );
        short x = 1;
        Point3f[] atomCoordAngstroms = new Point3f[] {
                new Atom( 0, 1, 1, 1, 1, 0.5f, new BitSet(), 0, x, 0, false, '0' ),
                new Atom( 0, 2, 1, 1, -2, 0.5f, new BitSet(), 0, x, 0, false, '1' ),
                new Atom( 0, 3, 3, 3, 3, 0.5f, new BitSet(), 0, x, 0, false, '2' ),
        };
        float[] potentials = new float[] { 1, 2, 3 };
        int calcType = 0;//ONE_OVER_D
        mepCalculation.calculate( volumeData, selected, atomCoordAngstroms, potentials, calcType );

        new JFrame() {{
            setContentPane( new PhetPCanvas() {{
                final Property<Integer> crossSectionIndex = new Property<Integer>( 0 );
                addScreenChild( new PNode() {{
                    crossSectionIndex.addObserver( new VoidFunction1<Integer>() {
                        public void apply( Integer integer ) {
                            removeAllChildren();
                            float[][][] data = volumeData.getVoxelData();
                            float[][] crossSection = data[crossSectionIndex.get()];
                            for ( int i = 0; i < crossSection.length; i++ ) {
                                for ( int k = 0; k < crossSection[0].length; k++ ) {
                                    double cellWidth = 10;
                                    float value = crossSection[i][k];
                                    addChild( new PhetPPath( new Rectangle2D.Double( i * cellWidth, k * cellWidth, cellWidth, cellWidth ), new Color( (float) MathUtil.clamp( 0, value, 1 ), 0, 0 ) ) );
                                }
                            }
                        }
                    } );
                }} );
                addScreenChild( new PSwing( new JSlider( 0, volumeData.getVoxelData().length - 1, crossSectionIndex.get() ) {{
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            crossSectionIndex.set( getValue() );
                        }
                    } );
                }} ) {{
                    setOffset( 0, 500 );
                }} );
            }} );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
            setSize( 800, 600 );
        }}.setVisible( true );
    }
}