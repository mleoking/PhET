/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


public class TranslationUtility extends JFrame {
    
    private static final String NAME = "PhET Simulation Translator";
    private static final String VERSION = "0.00.01";

    private static final String TITLE = NAME + " (" + VERSION + ")";

    public TranslationUtility( String[] projectNames ) {
        super( TITLE );
        
        JPanel translationsPanel = new TranslationPanel( projectNames );
        JScrollPane scrollPane = new JScrollPane( translationsPanel );
        ButtonPanel buttonPanel = new ButtonPanel();
        
        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
        bottomPanel.add( buttonPanel, BorderLayout.CENTER );

        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        mainPanel.add( scrollPane, BorderLayout.CENTER );
        mainPanel.add( bottomPanel, BorderLayout.SOUTH );
        
        getContentPane().add( mainPanel );
        
        pack();
    }
    
    public static void main( String[] args ) {
        String[] projectNames = { "optical-tweezers", "phetcommon" };
        JFrame frame = new TranslationUtility( projectNames );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.show();
    }
}
