// Copyright 2002-2011, University of Colorado
package org.reid.scenic;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.reid.scenic.controller.MouseMovedHandler;
import org.reid.scenic.controller.MousePressHandler;
import org.reid.scenic.controller.MouseReleasedHandler;
import org.reid.scenic.model.Model;
import org.reid.scenic.view.View;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;

import static java.awt.Cursor.getPredefinedCursor;

/**
 * @author Sam Reid
 */
public class TestScenicPanel {

    public static final int MAX_Y = 400;

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
                    final ScenicPanel<Model> scenicPanel = new ScenicPanel<Model>( new Model(), painter, new MousePressHandler(), new MouseReleasedHandler(), new MouseMovedHandler() ) {{
                        setPreferredSize( new Dimension( 800, 600 ) );
                    }};
                    setContentPane( scenicPanel );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                    pack();

                    final Function1<Model, Cursor> cursorHandler = new Function1<Model, Cursor>() {
                        public Cursor apply( Model model ) {
                            return model.button1.hover || model.button2.hover ? getPredefinedCursor( Cursor.HAND_CURSOR ) : getPredefinedCursor( Cursor.DEFAULT_CURSOR );
                        }
                    };
                    new Thread( new Runnable() {
                        public void run() {
                            recurse( scenicPanel, new ModelUpdater(), cursorHandler );
                        }
                    } ).start();
                }}.setVisible( true );
            }
        } );
    }
}