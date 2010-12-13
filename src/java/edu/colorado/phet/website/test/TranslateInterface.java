package edu.colorado.phet.website.test;

import java.awt.*;

import javax.swing.*;

public class TranslateInterface extends JFrame {
    private JTextField from;
    private JTextField to;
    private JTextArea top;
    private JTextArea bottom;

    public TranslateInterface() throws HeadlessException {
        super( "Translate" );

        setSize( 900, 400 );

        setVisible( true );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        JPanel panel = new JPanel( new GridBagLayout() );

        JPanel topPanel = new JPanel( new GridLayout( 1, 1 ) );
        setContentPane( topPanel );

        topPanel.add( panel );

        //setContentPane( panel );

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        panel.add( new JLabel( "X:" ), c );
        c.gridx = 1;
        from = new JTextField( "es" );
        from.setEditable( true );
        panel.add( from, c );
        c.gridx = 2;
        panel.add( new JLabel( "Y:" ), c );
        c.gridx = 3;
        c.weightx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        to = new JTextField( "en" );
        to.setEditable( true );
        panel.add( to, c );
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 4;
        top = new JTextArea( 4, 100 );
        top.setEditable( true );
        panel.add( top, c );
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 4;
        bottom = new JTextArea( 4, 100 );
        bottom.setEditable( true );
        panel.add( bottom, c );

        //top.setMinimumSize( new Dimension( 700, 200 ) );
        //bottom.setMinimumSize( new Dimension( 700, 200 ) );

        pack();
    }

    public static void main( String[] args ) {
        new TranslateInterface();
    }
}
