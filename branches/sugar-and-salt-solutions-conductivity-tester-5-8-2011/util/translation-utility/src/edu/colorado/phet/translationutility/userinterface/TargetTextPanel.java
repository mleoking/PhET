// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.userinterface;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicHTML;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.translationutility.TUImages;
import edu.colorado.phet.translationutility.TUStrings;
import edu.colorado.phet.translationutility.util.HTMLValidator;
import edu.colorado.phet.translationutility.util.MessageFormatValidator;

/**
 * Panel that combines a target text field and validation error icon.
 * This panel handles validation of the text field, and visibility of the icon.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class TargetTextPanel extends JPanel {

    private static final Color OK_COLOR = Color.WHITE;
    private static final Color INVALID_COLOR = Color.RED;

    private final TargetTextArea textArea;
    private final JLabel errorIcon;
    private final JLabel previewIcon;
    private final JLabel helpIcon;
    private final MessageFormatValidator messageFormatValidator;
    private final HTMLValidator htmlValidator;
    private boolean isValidValue;
    private String errorMessage;
    private String helpText;

    public TargetTextPanel( String key, final String sourceValue, final Locale sourceLocale, final Font sourceFont, String targetValue, final Locale targetLocale, final Font targetFont ) {
        super();

        // target text area
        textArea = new TargetTextArea( key, targetValue );
        textArea.setFont( targetFont );
        textArea.setBackground( OK_COLOR );
        textArea.addFocusListener( new FocusAdapter() {
            public void focusLost( FocusEvent e ) {
                validateValue();
            }
        } );
        textArea.addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                clearError();
            }
        } );

        // icon that provides access to error message
        errorIcon = new JLabel( TUImages.ERROR_BUTTON );
        errorIcon.setToolTipText( TUStrings.TOOLTIP_ERROR );
        errorIcon.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
        errorIcon.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent event ) {
                showErrorDetails( errorMessage );
            }
        } );

        // icon that provides access to a preview
        previewIcon = new JLabel( TUImages.PREVIEW_BUTTON );
        previewIcon.setToolTipText( TUStrings.TOOLTIP_PREVIEW );
        previewIcon.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
        previewIcon.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent event ) {
                showPreview( sourceValue, sourceLocale, sourceFont, textArea.getText(), targetLocale, targetFont );
            }
        } );
        previewIcon.setVisible( isPreviewable( sourceValue ) );

        // help icon
        helpIcon = new JLabel( TUImages.HELP_ICON );
        helpIcon.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
        helpIcon.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent event ) {
                showHelp();
            }
        } );
        helpIcon.setVisible( false );

        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( textArea, row, column++ );
        layout.addComponent( errorIcon, row, column++ );
        layout.addComponent( previewIcon, row, column++ );
        layout.addComponent( helpIcon, row, column++ );
        layout.setMinimumWidth( 1, errorIcon.getPreferredSize().width + 20 );
        layout.setMinimumWidth( 2, previewIcon.getPreferredSize().width + 20 );

        messageFormatValidator = new MessageFormatValidator( sourceValue );
        htmlValidator = new HTMLValidator( sourceValue );
        validateValue();
    }

    public TargetTextArea getTextArea() {
        return textArea;
    }

    public boolean isValidValue() {
        validateValue();
        return isValidValue;
    }

    public void setHelpText( String helpText ) {
        this.helpText = helpText;
        helpIcon.setVisible( helpText != null );
    }

    /**
     * Visibility of the preview icon is typically set automatically by the constructor,
     * based on the format of the source text string. This setter is provided for
     * situations where we take control of the preview icon visibility.
     * @param visible
     */
    public void setPreviewIconVisible( boolean visible ) {
        previewIcon.setVisible( visible );
    }

    private void clearError() {
        isValidValue = true;
        errorMessage = null;
        textArea.setBackground( OK_COLOR );
        errorIcon.setVisible( false );
    }

    /**
     * Validates the text area's value, comparing it to the source value.
     * Notifies interested listeners if there are validation errors.
     */
    public void validateValue() {

        clearError();

        // validate
        String targetString = textArea.getText();
        ArrayList<String> missingPlaceholders = messageFormatValidator.validate( targetString );
        ArrayList<String> missingTags = htmlValidator.validate( targetString );

        // report errors
        if ( missingPlaceholders != null || missingTags != null ) {
            isValidValue = false;
            errorMessage = createErrorMessage( missingPlaceholders, missingTags );
            textArea.setBackground( INVALID_COLOR );
            errorIcon.setVisible( true );
        }
    }

    private String createErrorMessage( ArrayList<String> missingPlaceholders, ArrayList<String> missingTags ) {

        String message = TUStrings.VALIDATION_MESSAGE;

        if ( missingPlaceholders != null ) {
            message += "<br><br>";
            message += TUStrings.VALIDATION_MESSAGE_FORMAT;
            for ( String placeholder : missingPlaceholders ) {
                message += " " + placeholder;
            }
        }

        if ( missingTags != null ) {
            message += "<br><br>";
            message += TUStrings.VALIDATION_HTML;
            for ( String tag : missingTags ) {
                message += " " + HTMLUtils.escape( tag );
            }
        }

        return HTMLUtils.toHTMLString( message );
    }

    private static boolean isPreviewable( String sourceString ) {
        return isHTML( sourceString ) || isHTMLFragment( sourceString );
    }

    private static boolean isHTML( String s ) {
        return BasicHTML.isHTMLString( s );
    }

    private static boolean isHTMLFragment( String s ) {
        return s.contains( "<" ) && s.contains( ">" ); //TODO is this sufficient?
    }

    private void showErrorDetails( String message ) {
        JOptionPane.showMessageDialog( null, message, TUStrings.ERROR_DETAILS_TITLE, JOptionPane.ERROR_MESSAGE );
    }

    private void showPreview( String source, Locale sourceLocale, Font sourceFont, String target, Locale targetLocale, Font targetFont ) {
        JPanel panel = new PreviewPanel( source, sourceLocale, sourceFont, target, targetLocale, targetFont );
        JOptionPane.showMessageDialog( null, panel, TUStrings.PREVIEW_TITLE, JOptionPane.PLAIN_MESSAGE );
    }

    private void showHelp() {
        JOptionPane.showMessageDialog( null, helpText, TUStrings.HELP_TITLE, JOptionPane.INFORMATION_MESSAGE );
    }
}
