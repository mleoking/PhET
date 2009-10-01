/* Copyright 2009, University of Colorado */

package edu.colorado.phet.translationutility.userinterface;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.text.MessageFormat;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.translationutility.TUStrings;

/**
 * Panel that shows a preview of an HTML translation.
 * It shows both the source and target strings.
 * It also checks for potential layout problems and (if detected) warns the user.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PreviewPanel extends JPanel {
    
    private static final int MARGIN = 10;
    private static final Border BORDER = new CompoundBorder( new LineBorder( Color.BLACK, 1 ), new EmptyBorder( MARGIN, MARGIN, MARGIN, MARGIN ) );
    private static final double WIDTH_WARNING_THRESHOLD = 1.25;
    private static final double HEIGHT_WARNING_THRESHOLD = 1.25;
    
    public PreviewPanel( String source, Locale sourceLocale, String target, Locale targetLocale ) {
        super();
        
        JLabel sourceTitle = new JLabel( PhetLocales.getInstance().getName( sourceLocale ) );
        JLabel translationTitle = new JLabel( PhetLocales.getInstance().getName( targetLocale ) );
        
        JLabel sourceLabel = new JLabel( HTMLUtils.toHTMLString( source ) );
        sourceLabel.setBorder( BORDER );
        
        // if there is no target, show that the source will be used
        String t = target;
        if ( t == null || t.length() == 0 ) {
            t = source;
        }
        JLabel targetLabel = new JLabel( HTMLUtils.toHTMLString( t ) );
        targetLabel.setBorder( BORDER );
        
        JLabel arrowLabel = new JLabel( "-->" );
        
        // warnings related to dimensions
        JLabel widthWarningLabel = getWidthWarningLabel( sourceLabel, targetLabel );
        JLabel heightWarningLabel = getHeightWarningLabel( sourceLabel, targetLabel );

        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addFilledComponent( sourceTitle, row, column++, GridBagConstraints.HORIZONTAL  );
        layout.addFilledComponent( translationTitle, row, column + 1, GridBagConstraints.HORIZONTAL  );
        row++;
        column = 0;
        layout.addComponent( sourceLabel, row, column++  );
        layout.addFilledComponent( arrowLabel, row, column++, GridBagConstraints.HORIZONTAL  );
        layout.addComponent( targetLabel, row, column++ );
        row++;
        column = 0;
        if ( widthWarningLabel != null || heightWarningLabel != null ) {
            layout.addComponent( Box.createVerticalStrut( 15 ), row++, column );
            if ( widthWarningLabel != null ) {
                layout.addComponent( widthWarningLabel, row++, column, 4, 1 );
            }
            if ( heightWarningLabel != null ) {
                layout.addComponent( heightWarningLabel, row++, column, 4, 1 );
            }
            layout.addComponent( new JLabel( TUStrings.LAYOUT_PROBLEM_MESSAGE ), row++, column, 4, 1 );
        }
    }
    
    private JLabel getWidthWarningLabel( JLabel sourceLabel, JLabel targetLabel ) {
        JLabel label = null;
        if ( targetLabel.getPreferredSize().width >= ( WIDTH_WARNING_THRESHOLD * sourceLabel.getPreferredSize().width ) ) {
            int percent = (int)( 100 * ( targetLabel.getPreferredSize().width - sourceLabel.getPreferredSize().width ) / sourceLabel.getPreferredSize().width );
            Object[] args = { new Integer( percent ) };
            String s = MessageFormat.format( TUStrings.WIDTH_WARNING_MESSAGE, args );
            label = new JLabel( s );
            label.setForeground( Color.RED );
        }
        return label;
    }
    
    private JLabel getHeightWarningLabel( JLabel sourceLabel, JLabel targetLabel ) {
        JLabel label = null;
        if ( targetLabel.getPreferredSize().height >= ( HEIGHT_WARNING_THRESHOLD * sourceLabel.getPreferredSize().height ) ) {
            int percent = (int)( 100 * ( targetLabel.getPreferredSize().height - sourceLabel.getPreferredSize().height ) / sourceLabel.getPreferredSize().height );
            Object[] args = { new Integer( percent ) };
            String s = MessageFormat.format( TUStrings.HEIGHT_WARNING_MESSAGE, args );
            label = new JLabel( s );
            label.setForeground( Color.RED );
        }
        return label;
    }
}