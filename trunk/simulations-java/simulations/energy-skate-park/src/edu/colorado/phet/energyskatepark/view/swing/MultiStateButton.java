package edu.colorado.phet.energyskatepark.view.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * This class represents a button that can change state.  Each state can have its own
 * list of ActionListeners.
 *
 * TODO: there should probably be mode change listeners for this class.
 *
 * @author Sam Reid
 */
public class MultiStateButton extends JButton {
    private Mode mode;
    private ArrayList modes = new ArrayList();

    public MultiStateButton( Mode[] mode ) {
        for( int i = 0; i < mode.length; i++ ) {
            addMode( mode[i].getKey(), mode[i].getLabel(), mode[i].getIcon() );
        }
        init();
    }

    //use a separate key object instead of label as key so bogus translations will still work properly
    public MultiStateButton( Object[] keys, String[] labels, Icon[] icons ) {
        for( int i = 0; i < labels.length; i++ ) {
            addMode( keys[i], labels[i], icons[i] );
        }
        init();
    }

    private void init() {
        if( getNumModes() > 0 ) {
            setMode( 0 );
        }
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
        addMode( new Mode( key, label, icon ) );
    }

    public void addMode( Mode mode ) {
        if( getMode( mode.getKey() ) != null ) {
            throw new IllegalArgumentException( "Duplicate mode not allowed, key=" + mode.getKey() );
        }
        modes.add( mode );

        updateDimension();

        if( this.mode != null ) {
            updateButton();
        }
    }

    public int getNumModes() {
        return modes.size();
    }

    private void updateDimension() {
        Dimension[] d = new Dimension[modes.size()];
        for( int i = 0; i < modes.size(); i++ ) {
            setIconAndText( ( (Mode)modes.get( i ) ) );
            d[i] = getUI().getPreferredSize( this );
        }
        int maxWidth = 0;
        int maxHeight = 0;
        for( int i = 0; i < d.length; i++ ) {
            Dimension dimension = d[i];
            maxWidth = Math.max( dimension.width, maxWidth );
            maxHeight = Math.max( dimension.height, maxHeight );
        }
        setPreferredSize( new Dimension( maxWidth, maxHeight ) );
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

    private void setIconAndText( Mode mode ) {
        setText( mode.getLabel() );
        setIcon( mode.getIcon() );
    }

    private void updateButton() {
        setIconAndText( mode );
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
