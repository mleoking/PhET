/* Copyright 2008, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PCanvas;


/**
 * This class defines a dialog window that shows a representation of the
 * electromagnetic spectrum.
 *
 */
public class SpectrumDialog extends PaintImmediateDialog {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    private static final Font LABEL_FONT = new PhetFont(12);

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public SpectrumDialog( Frame parentFrame ) {
        super( parentFrame, true );

        // Don't let the user resize this window.
        setResizable( false );

        // Create the panel that will contain the canvas and the "Close" button.
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) ); // top, left, bottom, right
        mainPanel.setLayout( new BoxLayout( mainPanel, BoxLayout.Y_AXIS ) );

        // Create the canvas and add it to the panel.
        PCanvas canvas = new PCanvas();
        canvas.setBackground( new Color( 233, 236, 174 ) );
        canvas.setPreferredSize( new Dimension( 600, 400 ) );
        canvas.setBorder( BorderFactory.createEtchedBorder() ); // top, left, bottom, right
        mainPanel.add( canvas );

        // Add an invisible panel that will create space between the diagram
        // and the close button.
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize( new Dimension( 1, 15) );
        mainPanel.add( spacerPanel );

        // Add the close button.
        // TODO: i18n
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                SpectrumDialog.this.dispose();
            }
        });
        closeButton.setAlignmentX( Component.CENTER_ALIGNMENT );
        mainPanel.add( closeButton );

        // Add to the dialog
        setContentPane( mainPanel );
        pack();
    }
}
