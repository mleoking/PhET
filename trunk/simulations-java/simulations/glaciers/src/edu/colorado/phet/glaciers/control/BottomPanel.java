/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.model.GlaciersClock;


public class BottomPanel extends JPanel {
    
    private static final Color BACKGROUND_COLOR = new Color( 180, 158, 134 ); // tan
    private static final Font TITLE_FONT = new PhetDefaultFont( PhetDefaultFont.getDefaultFontSize(), true /* bold */ );
    private static final Font CONTROL_FONT = new PhetDefaultFont();

    public BottomPanel( GlaciersClock clock ) {
        
        ViewControlPanel viewControlPanel = new ViewControlPanel( TITLE_FONT, CONTROL_FONT );
        GlaciersClockControlPanel clockControlPanel = new GlaciersClockControlPanel( clock );
        MiscControlPanel miscControlPanel = new MiscControlPanel();
        
        int row;
        int column;
        
        JPanel topPanel = new JPanel();
        EasyGridBagLayout topLayout = new EasyGridBagLayout( topPanel );
        topPanel.setLayout( topLayout  );
        row = 0;
        column = 0;
        topLayout.addComponent( viewControlPanel, row, column++ );
        
        JPanel bottomPanel = new JPanel();
        EasyGridBagLayout bottomLayout = new EasyGridBagLayout( bottomPanel );
        bottomPanel.setLayout( bottomLayout );
        row = 0;
        column = 0;
        bottomLayout.addAnchoredComponent( clockControlPanel, row, column++, GridBagConstraints.WEST );
        bottomLayout.addComponent( Box.createHorizontalStrut( 10 ), row, column++ );
        bottomLayout.addFilledComponent( new JSeparator( SwingConstants.VERTICAL ), row, column++, GridBagConstraints.VERTICAL );
        bottomLayout.addComponent( Box.createHorizontalStrut( 10 ), row, column++ );
        bottomLayout.addAnchoredComponent( miscControlPanel, row, column++, GridBagConstraints.EAST );
        
        EasyGridBagLayout thisLayout = new EasyGridBagLayout( this );
        setLayout( thisLayout );
        row = 0;
        column = 0;
        thisLayout.addComponent( topPanel, row++, column );
        thisLayout.addComponent( bottomPanel, row++, column );
        
        Class[] excludedClasses = { ViewControlPanel.class, JTextComponent.class };
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, excludedClasses, false /* processContentsOfExcludedContainers */ );
    }
}
