package edu.colorado.phet.semiconductor.phetcommon.view.plaf;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSpinnerUI;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

/**
 * User: Sam Reid
 * Date: Apr 2, 2004
 * Time: 2:45:20 PM
 */
public class MySpinnerUI extends BasicSpinnerUI {
    //    static String path="semiconductor/images/animfactory/suites/alum/";
    static String path = "semiconductor/images/animfactory/suites/graymarble/";
    static ImageIcon up = ( new ImageIcon( MySpinnerUI.class.getClassLoader().getResource( path + "up.gif" ) ) );
    static ImageIcon uppressed = ( new ImageIcon( MySpinnerUI.class.getClassLoader().getResource( path + "up2.gif" ) ) );
    static ImageIcon down = ( new ImageIcon( MySpinnerUI.class.getClassLoader().getResource( path + "down.gif" ) ) );
    static ImageIcon downpressed = ( new ImageIcon( MySpinnerUI.class.getClassLoader().getResource( path + "down2.gif" ) ) );

//    static ImageIcon up = ( new ImageIcon( MySpinnerUI.class.getClassLoader().getResource( "semiconductor/images/components/webt/up1-unpressed.gif" ) ) );
//    static ImageIcon uppressed = ( new ImageIcon( MySpinnerUI.class.getClassLoader().getResource( "semiconductor/images/components/webt/up1-pressed.gif" ) ) );
//    static ImageIcon down = ( new ImageIcon( MySpinnerUI.class.getClassLoader().getResource( "semiconductor/images/components/webt/down1-unpressed.gif" ) ) );
//    static ImageIcon downpressed = ( new ImageIcon( MySpinnerUI.class.getClassLoader().getResource( "semiconductor/images/components/webt/down1-pressed.gif" ) ) );

    static {
        SimStrings.setStrings( "localization/SemiConductorPCStrings" );
    }

    public MySpinnerUI() {
        super();
    }

    public void paint( Graphics g, JComponent c ) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        JSpinner spinner = (JSpinner) c;
        Component[] comp = spinner.getComponents();
        boolean first = true;
        //This hack is for working around swing's lack of support for customizing spinner components.
        for ( int i = 0; i < comp.length; i++ ) {
            Component component = comp[i];
//            System.out.println( "component = " + component );
            if ( component instanceof JButton && first ) {
                JButton jb = (JButton) component;
                jb.setIcon( up );
                jb.setPressedIcon( uppressed );
                first = false;
            }
            else if ( component instanceof JButton ) {
                JButton jb = (JButton) component;
                jb.setIcon( down );
                jb.setPressedIcon( downpressed );
            }
        }

        super.paint( g, c );
    }

    class MouseButtonAdapter implements MouseListener {
        JButton origButton;

        public MouseButtonAdapter( JButton origButton ) {
            this.origButton = origButton;
        }

        public void mouseClicked( MouseEvent e ) {
            MouseListener[] m = origButton.getMouseListeners();
            for ( int i = 0; i < m.length; i++ ) {
                MouseListener mouseListener = m[i];
                mouseListener.mouseClicked( e );
            }
        }

        public void mouseEntered( MouseEvent e ) {
            MouseListener[] m = origButton.getMouseListeners();
            for ( int i = 0; i < m.length; i++ ) {
                MouseListener mouseListener = m[i];
                mouseListener.mouseEntered( e );
            }
        }

        public void mouseExited( MouseEvent e ) {
            MouseListener[] m = origButton.getMouseListeners();
            for ( int i = 0; i < m.length; i++ ) {
                MouseListener mouseListener = m[i];
                mouseListener.mouseExited( e );
            }
        }

        public void mousePressed( MouseEvent e ) {
            MouseListener[] m = origButton.getMouseListeners();
            for ( int i = 0; i < m.length; i++ ) {
                MouseListener mouseListener = m[i];
                mouseListener.mousePressed( e );
            }
        }

        public void mouseReleased( MouseEvent e ) {
            MouseListener[] m = origButton.getMouseListeners();
            for ( int i = 0; i < m.length; i++ ) {
                MouseListener mouseListener = m[i];
                mouseListener.mouseReleased( e );
            }
        }
    }

    class ActionListenerAdapter implements ActionListener {
        JButton curButton;

        public ActionListenerAdapter( JButton next ) {
            this.curButton = next;
        }

        public void actionPerformed( ActionEvent e ) {
            ActionListener[] a = curButton.getActionListeners();
            for ( int i = 0; i < a.length; i++ ) {
                ActionListener actionListener = a[i];
                actionListener.actionPerformed( e );
            }
        }
    }

    protected Component createNextButton() {
        final JButton next = (JButton) super.createNextButton();
        JButton but = new JButton( SimStrings.get( "MySpinnerUI.NextButton" ), up );

        but.setIcon( up );
        but.setPressedIcon( uppressed );

        //delegation classes to the origButton button that handles right, but looks wrong.
        //had to do this because everything is private, but this looks safe.
        MouseButtonAdapter mba = new MouseButtonAdapter( next );
        but.addMouseListener( mba );

        ActionListenerAdapter ala = new ActionListenerAdapter( next );
        but.addActionListener( ala );
        return but;
    }

    protected Component createPreviousButton() {
        final JButton prev = (JButton) super.createPreviousButton();
        JButton but = new JButton( SimStrings.get( "MySpinnerUI.DownButton" ), down );

        but.setIcon( down );
        but.setPressedIcon( down );

        //delegation classes to the origButton button that handles right, but looks wrong.
        //had to do this because everything is private, but this looks safe.
        MouseButtonAdapter mba = new MouseButtonAdapter( prev );
        but.addMouseListener( mba );

        ActionListenerAdapter ala = new ActionListenerAdapter( prev );
        but.addActionListener( ala );
        return but;
    }


    public void installUI( JComponent c ) {
        super.installUI( c );
        JSpinner spinner = (JSpinner) c;

        Dimension cur = spinner.getPreferredSize();
        int up1 = up.getIconHeight();
        int up2 = down.getIconHeight();
        int insets = 30;
        Dimension newDim = new Dimension( cur.width, up1 + up2 + insets );
        spinner.setPreferredSize( newDim );
    }
}
