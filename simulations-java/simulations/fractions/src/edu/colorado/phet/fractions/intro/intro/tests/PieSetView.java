// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.tests;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ImmutableList;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.fractions.intro.intro.tests.model.MovableSlice;
import edu.colorado.phet.fractions.intro.intro.tests.model.PieSetState;
import edu.colorado.phet.fractions.intro.intro.tests.model.Slice;

/**
 * @author Sam Reid
 */
public class PieSetView {
    public static void main( String[] args ) {
//        PDebug.debugRegionManagement=true;
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new JFrame() {{
                    final int numPies = 6;
                    final int denominator = 3;
                    final double pieDiameter = 120;
                    final double pieSpacing = 10;
                    final double anglePerSlice = 2 * Math.PI / denominator;
                    ArrayList<Slice> cells = new ArrayList<Slice>() {{
                        for ( int i = 0; i < numPies; i++ ) {
                            for ( int k = 0; k < denominator; k++ ) {
                                add( createSlice( i, anglePerSlice, k, pieDiameter, pieSpacing ) );
                            }
                        }
                    }};

                    ArrayList<MovableSlice> slices = new ArrayList<MovableSlice>() {{
                        for ( int i = 0; i < numPies; i++ ) {
                            for ( int k = 0; k < denominator; k++ ) {
                                if ( Math.random() < 0.5 ) {
                                    add( new MovableSlice( createSlice( i, anglePerSlice, k, pieDiameter, pieSpacing ), null ) );
                                }
                            }
                        }
                    }};

                    final PieSetState state = new PieSetState( 0, denominator, new ImmutableList<Slice>( cells ), new ImmutableList<MovableSlice>( slices ) ).snapTo();
                    final Property<PieSetState> model = new Property<PieSetState>( state );

                    new Timer( 30, new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            //Any piece that is dragging should align with the closest open cell
                            model.set( model.get().stepInTime() );
                        }
                    } ).start();

                    setContentPane( new PhetPCanvas() {{
                        addScreenChild( new PieSetNode( model ) );
                    }} );
                    pack();
                    setSize( new Dimension( 1024, 768 ) );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                }}.setVisible( true );
            }
        } );
    }

    private static Slice createSlice( int i, double anglePerSlice, int k, double pieDiameter, double pieSpacing ) {
        return new Slice( new ImmutableVector2D( pieDiameter * ( i + 1 ) + pieSpacing * ( i + 1 ), pieDiameter ), anglePerSlice * k, anglePerSlice, pieDiameter / 2, false );
    }
}