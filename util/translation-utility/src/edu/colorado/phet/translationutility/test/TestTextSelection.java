// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.test;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;


public class TestTextSelection extends JFrame {
    
    private static final String TEST_STRING = "this is a test of selection";
    
    private static final Border TEXT_AREA_BORDER = BorderFactory.createCompoundBorder( 
            /* outside */ BorderFactory.createLineBorder( Color.BLACK, 1 ), 
            /* inside */ BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
    
    public TestTextSelection() {
        super();
        
        JTextArea textArea = new JTextArea( TEST_STRING );
        textArea.setColumns( 20 );
        textArea.setEditable( false );
        textArea.setFocusable( true ); // must be true for selection to work
        textArea.setBackground( this.getBackground() );
        textArea.setBorder( TEXT_AREA_BORDER );
        textArea.setSelectionColor( Color.GREEN );
        textArea.setSelectionStart( 2 );
        textArea.setSelectionEnd( 7 );
        
        JPanel panel = new JPanel();
        panel.add( textArea );
        
        getContentPane().add( panel );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        pack();
    }
    
    public static final void main( String[] args ) {
        JFrame frame = new TestTextSelection();
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

}
