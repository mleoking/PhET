package edu.colorado.phet.energyskatepark.view.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Jun 8, 2007, 6:40:14 AM
 */
public class MultiStateButton extends JButton {
    private Mode mode;
    private ArrayList modes = new ArrayList();

    //use a separate key object instead of label as key so bogus translations will still work properly
    public MultiStateButton( Object[] keys, String[] labels, Icon[] icons ) {
        for( int i = 0; i < labels.length; i++ ) {
            addMode( keys[i], labels[i], icons[i] );
        }
        setMode( 0 );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Mode sourceMode = mode;
                int m = modes.indexOf( mode );
                setMode( ( m + 1 ) % modes.size() );
                sourceMode.dispatchEvent( e );//use our own dispatch since order of dispatch is backwards and/or undocumented in swing
            }
        } );
    }

    private void setMode( int i ) {
        setMode( getMode( i ) );
    }

    private void setMode( Mode mode ) {
        this.mode = mode;
        updateButton();
    }

    private Mode getMode( int i ) {
        return (Mode)modes.get( i );
    }

    public void addMode( Object key, String label, Icon icon ) {
        if( getMode( key ) != null ) {
            throw new IllegalArgumentException( "Duplicate mode not allowed, key=" + key );
        }
        modes.add( new Mode( key, label, icon ) );
    }

    protected static class Mode {
        private Object key;
        private String label;
        private Icon icon;
        private ArrayList actionListeners = new ArrayList();

        public Mode( Object key, String label, Icon icon ) {
            this.key = key;
            this.label = label;
            this.icon = icon;
        }

        public Object getKey() {
            return key;
        }

        public String getLabel() {
            return label;
        }

        public Icon getIcon() {
            return icon;
        }

        public void dispatchEvent( ActionEvent e ) {
            for( int i = 0; i < actionListeners.size(); i++ ) {
                ActionListener listener = (ActionListener)actionListeners.get( i );
                listener.actionPerformed( e );
            }
        }

        public void addActionListener( ActionListener actionListener ) {
            actionListeners.add( actionListener );
        }
    }

    public void setMode( Object key ) {
        setMode( getMode( key ) );
        updateButton();
    }

    private Mode getMode( Object key ) {
        for( int i = 0; i < modes.size(); i++ ) {
            Mode mode = (Mode)modes.get( i );
            if( mode.getKey().equals( key ) ) {
                return mode;
            }
        }
        return null;
    }

    private void updateButton() {
        setText( mode.getLabel() );
        setIcon( mode.getIcon() );
    }

    public void addActionListener( Object key, final ActionListener actionListener ) {
        getMode( key ).addActionListener( actionListener );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        String KEY_PLAY = "play";
        String KEY_PAUSE = "pause";
        MultiStateButton stateButton = new MultiStateButton( new Object[]{KEY_PLAY, KEY_PAUSE}, new String[]{"play", "pause"}, new Icon[2] );
        stateButton.addActionListener( KEY_PLAY, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "play was pressed" );
            }
        } );
        stateButton.addActionListener( KEY_PAUSE, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "pause was pressed" );
            }
        } );
        frame.setContentPane( stateButton );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width / 2 - frame.getWidth() / 2,
                           Toolkit.getDefaultToolkit().getScreenSize().height / 2 - frame.getHeight() / 2 );
        frame.show();
    }
}
