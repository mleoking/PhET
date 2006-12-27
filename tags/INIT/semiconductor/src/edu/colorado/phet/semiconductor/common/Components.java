/** Sam Reid*/
package edu.colorado.phet.semiconductor.common;

import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Mar 31, 2004
 * Time: 9:28:39 PM
 * Copyright (c) Mar 31, 2004 by Sam Reid
 */
public class Components {
//    static BufferedImage image;
    private static ImageIcon onIcon;
    private static ImageIcon offIcon;

    static {
        try {
//            image = new ImageLoader().loadImage( "images/Phet-logo-48x48.gif" );
            onIcon = new ImageIcon( new ImageLoader().loadImage( "images/components/webt/on.gif" ) );
            offIcon = new ImageIcon( new ImageLoader().loadImage( "images/components/webt/off.gif" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }


    public static void main( String[] args ) {
        JFrame jf = new JFrame( "Test" );
//        MySliderUI myui=new MySliderUI();
        JSlider js = new JSlider( 0, 50 );
//        myui.installUI( js );

        JPanel jp = new JPanel();
        jp.add( js );
        CheckboxPanel cp = new CheckboxPanel();
        jp.add( cp );
        jf.getContentPane().add( jp );

        jf.pack();
        jf.setVisible( true );

        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    static class CheckboxPanel extends JPanel {

        Icon unchecked = new ToggleIcon( false );
        Icon checked = new ToggleIcon( true );

        public CheckboxPanel() {

            // Set the layout for the JPanel
            setLayout( new GridLayout( 2, 1 ) );
            // Create checkbox with its state
            //  initialized to true
            JCheckBox cb1 = new JCheckBox( "Choose Me", true );
//            cb1.setIcon( unchecked );
            cb1.setIcon( onIcon );
//            cb1.setSelectedIcon( checked );
            cb1.setSelectedIcon( offIcon );
            // Create checkbox with its state
            // initialized to false
            JCheckBox cb2 = new JCheckBox( "No Choose Me", false );
            cb2.setIcon( unchecked );
            cb2.setSelectedIcon( checked );
            add( cb1 );
            add( cb2 );
        }

        class ToggleIcon implements Icon {
            boolean state;

            public ToggleIcon( boolean s ) {
                state = s;
            }

            public void paintIcon( Component c, Graphics g,
                                   int x, int y ) {
                int width = getIconWidth();
                int height = getIconHeight();
                g.setColor( Color.black );
                if( state ) {
                    g.fillRect( x, y, width, height );
                }
                else {
                    g.drawRect( x, y, width, height );
                }
            }

            public int getIconWidth() {
                return 10;
            }

            public int getIconHeight() {
                return 10;
            }
        }
    }


}
