// Copyright 2002-2011, University of Colorado
package org.reid.scenic;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.reid.scenic.model.Atom;
import org.reid.scenic.model.ButtonModel;
import org.reid.scenic.model.Model;
import org.reid.scenic.view.View;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.sugarandsaltsolutions.common.util.ImmutableList;

/**
 * @author Sam Reid
 */
public class TestScenicPanel {

    protected static int MAX_Y;

    //Todo: rewrite in scala to get tail recursion
    public static <T> void recurse( final ScenicPanel<T> panel, final Function1<T, T> update, final Function1<T, Cursor> cursor ) {
        while ( true ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    final T model = update.apply( panel.getModel() );
                    panel.setModel( model );
                    panel.setCursor( cursor.apply( model ) );
                }
            } );
            panel.paintImmediately( 0, 0, panel.getWidth(), panel.getHeight() );
        }
    }

    public static void main( String[] args ) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                new JFrame() {{
                    final VoidFunction2<Model, Graphics2D> painter = new VoidFunction2<Model, Graphics2D>() {
                        public void apply( Model model, Graphics2D graphics2D ) {
                            new View( model ).paint( graphics2D );
                        }
                    };
                    final Function2<Model, MouseEvent, Model> mousePressHandler = new Function2<Model, MouseEvent, Model>() {
                        public Model apply( Model model, MouseEvent mouseEvent ) {
                            return new Model( model.atoms, model.buttonModel.hover ? model.buttonModel.pressed( true ) : model.buttonModel );
                        }
                    };
                    final Function2<Model, MouseEvent, Model> mouseReleasedHandler = new Function2<Model, MouseEvent, Model>() {
                        public Model apply( Model model, MouseEvent mouseEvent ) {
                            return new Model( model.atoms, model.buttonModel.pressed( false ) );
                        }
                    };
                    Function2<Model, MouseEvent, Model> mouseMovedHandler = new Function2<Model, MouseEvent, Model>() {
                        public Model apply( Model model, MouseEvent mouseEvent ) {
                            boolean hover = new View( model ).buttonContains( mouseEvent.getX(), mouseEvent.getY() );
                            return new Model( model.atoms, model.buttonModel.hover( hover ) );
                        }
                    };
                    final ScenicPanel<Model> scenicPanel = new ScenicPanel<Model>(
                            new Model( new ImmutableList<Atom>( createAtoms() ), new ButtonModel( new PhetFont( 16, true ), "Button", 100, 100, false, false ) ),
                            painter, mousePressHandler, mouseReleasedHandler, mouseMovedHandler
                    ) {{
                        setPreferredSize( new Dimension( 800, 600 ) );
                    }};
                    setContentPane( scenicPanel );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                    pack();
                    new Thread( new Runnable() {
                        public void run() {
                            recurse( scenicPanel, new ModelUpdater(), new Function1<Model, Cursor>() {
                                public Cursor apply( Model model ) {
                                    return model.buttonModel.hover ? Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) : Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
                                }
                            } );
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
