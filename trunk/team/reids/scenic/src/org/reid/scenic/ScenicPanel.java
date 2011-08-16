// Copyright 2002-2011, University of Colorado
package org.reid.scenic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;
import edu.colorado.phet.sugarandsaltsolutions.common.util.ImmutableList;

/**
 * This project is an attempt at a functional library for Java2D programming, see readme
 *
 * @author Sam Reid
 */
public class ScenicPanel<T> extends JPanel {

    protected static int MAX_Y;
    private T model;
    private VoidFunction2<T, Graphics2D> painter;

    public ScenicPanel( VoidFunction2<T, Graphics2D> painter ) {
        this.painter = painter;
    }

    @Override protected void paintComponent( Graphics g ) {
        super.paintComponent( g );    //To change body of overridden methods use File | Settings | File Templates.
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        painter.apply( model, g2 );
    }

    public static class Atom {
        public final ImmutableVector2D position;
        public final ImmutableVector2D velocity;
        public final double mass;

        Atom( ImmutableVector2D position, ImmutableVector2D velocity, double mass ) {
            this.position = position;
            this.velocity = velocity;
            this.mass = mass;
        }
    }

    static class Model {
        public final ImmutableList<Atom> atoms;

        Model( ImmutableList<Atom> atoms ) {
            this.atoms = atoms;
        }
    }

    static class View {
        private Model model;

        public View( Model model ) {
            this.model = model;
        }

        public void paint( Graphics2D graphics2D ) {
            for ( Atom atom : model.atoms ) {
                new AtomView( atom ).paint( graphics2D );
            }
        }
    }

    static class AtomView {
        private Atom atom;

        public AtomView( Atom atom ) {
            this.atom = atom;
        }

        public void paint( Graphics2D graphics2D ) {
            graphics2D.setPaint( Color.blue );
            int w = 10;
            graphics2D.fill( new Ellipse2D.Double( atom.position.getX() - w / 2, atom.position.getY() - w / 2, w, w ) );
        }
    }

    //Todo: rewrite in scala to get tail recursion
    public static <T> void recurse( ScenicPanel<T> panel, Function1<T, T> update, T model ) {
        while ( true ) {
            model = update.apply( model );
            panel.setModel( model );
            panel.paintImmediately( 0, 0, panel.getWidth(), panel.getHeight() );
            Thread.yield();
        }
    }

    private void setModel( T model ) {
        this.model = model;
    }

    public static void main( String[] args ) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                new JFrame() {{
                    final ScenicPanel<Model> scenicPanel = new ScenicPanel<Model>( new VoidFunction2<Model, Graphics2D>() {
                        public void apply( Model model, Graphics2D graphics2D ) {
                            new View( model ).paint( graphics2D );
                        }
                    } ) {{
                        setPreferredSize( new Dimension( 800, 600 ) );
                    }};
                    setContentPane( scenicPanel );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                    pack();
                    new Thread( new Runnable() {
                        public void run() {
                            recurse( scenicPanel, new Function1<Model, Model>() {
                                public Model apply( final Model model ) {
                                    final double dt = 0.05;
                                    final ImmutableVector2D force = new ImmutableVector2D( 0, 9.8 );

                                    return new Model( model.atoms.map( new Function1<Atom, Atom>() {
                                        public Atom apply( Atom atom ) {
                                            //v = v0 + at, a = f/m, v = v0+ft/m
                                            ImmutableVector2D velocity = atom.velocity.plus( force.times( dt / atom.mass ) );
                                            return new Atom( atom.position.plus( atom.velocity.times( dt ) ), atom.position.getY() < MAX_Y ? velocity : new ImmutableVector2D( velocity.getX(), -Math.abs( velocity.getY() ) ), atom.mass );
                                        }
                                    } ) );
                                }
                            }, new Model( new ImmutableList<Atom>( createAtoms() ) ) );
                        }
                    } ).start();
                }}.setVisible( true );
            }
        } );
    }

    private static Atom[] createAtoms() {
        Random random = new Random();
        Atom[] a = new Atom[500];
        for ( int i = 0; i < a.length; i++ ) {
            MAX_Y = 400;
            a[i] = new Atom( new ImmutableVector2D( random.nextDouble() * 800, random.nextDouble() * MAX_Y ), new ImmutableVector2D( random.nextDouble() * 10 - 5, random.nextDouble() * 10 - 5 ), random.nextDouble() + 1 );
        }
        return a;
    }
}