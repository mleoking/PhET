package edu.colorado.phet.common.phetcommon.tests;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;

public class TestHTMLEditorPane {
    public static void main( String[] args ) {
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setEditorKit( new HTMLEditorKit() );
//        jEditorPane.setText( "<img src=\"image001.gif\"></img>" );
        jEditorPane.setText( "<img src=\"image001.gif\"/>" );
        JFrame frame = new JFrame( TestHTMLEditorPane.class.getName() );
        frame.setContentPane( jEditorPane );
        frame.setSize( 400, 400 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
