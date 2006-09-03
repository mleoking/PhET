/** Sam Reid*/
package edu.colorado.phet.movingman.tests;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 8, 2004
 * Time: 10:46:43 AM
 * Copyright (c) Nov 8, 2004 by Sam Reid
 */
public class AlphaCompositeTest {
    private JFrame alphaFrame = new JFrame();
    private ArrayList listeners = new ArrayList();

    static interface Listener {
        void alphaCompositeChanged( AlphaComposite alpha );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public AlphaCompositeTest() {
        final JPanel contentPane = new JPanel();
        contentPane.setLayout( new BoxLayout( contentPane, BoxLayout.Y_AXIS ) );
        Field[] f = AlphaComposite.class.getFields();
        final JList list = new JList( f );

        list.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
//                    BufferedGraphicForComponent bg = module.getBackground();
                Field ac = (Field)list.getSelectedValue();
                AlphaComposite alpha = null;
                try {
                    alpha = (AlphaComposite)ac.get( null );
                }
                catch( IllegalAccessException e1 ) {
                    e1.printStackTrace();
                }
                for( int i = 0; i < listeners.size(); i++ ) {
                    Listener listener = (Listener)listeners.get( i );
                    listener.alphaCompositeChanged( alpha );
                }
//                    bg.setAlphaComposite( alpha );
                list.repaint();
                contentPane.invalidate();
                contentPane.validate();
                contentPane.repaint();
            }
        } );
        contentPane.add( list );
        alphaFrame.setContentPane( contentPane );
        alphaFrame.setContentPane( contentPane );
        alphaFrame.pack();
    }
}
