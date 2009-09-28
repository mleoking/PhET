/* Copyright 2009, University of Colorado */

package edu.colorado.phet.translationutility;

/**
 * Strings used in this application.
 * Loaded statically so that we can see if anything is missing without visiting all features.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TUStrings {

    /* not intended for instantiation */
    private TUStrings() {}
    
    public static final String TRANSLATION_UTILITY_NAME = TUResources.getString( "translation-utility.name" );
    
    // buttons
    public static final String CLOSE_BUTTON = TUResources.getString( "button.close" );
    public static final String SAVE_BUTTON = TUResources.getString( "button.save" );
    public static final String LOAD_BUTTON = TUResources.getString( "button.load" );
    public static final String TEST_BUTTON = TUResources.getString( "button.test" );
    public static final String SUBMIT_BUTTON = TUResources.getString( "button.submit" );
    public static final String FIND_BUTTON = TUResources.getString( "button.find" );
    public static final String HELP_BUTTON = TUResources.getString( "button.help" );
    public static final String NEXT_BUTTON = TUResources.getString( "button.next" );
    public static final String PREVIOUS_BUTTON = TUResources.getString( "button.previous" );
    public static final String BROWSE_BUTTON = TUResources.getString( "button.browse" );
    public static final String CANCEL_BUTTON = TUResources.getString( "button.cancel" );
    public static final String CONTINUE_BUTTON = TUResources.getString( "button.continue" );
    public static final String GET_NEW_VERSION_BUTTON =  TUResources.getString( "button.getNewVersion" );
    public static final String CONTINUE_WITH_OLD_VERSION_BUTTON = TUResources.getString( "button.continueWithOldVersion" );
    
    // labels
    public static final String FIND_LABEL = TUResources.getString( "label.find" );
    public static final String JAR_PATH_LABEL = TUResources.getString( "label.jarPath" );
    public static final String LOCALE_LABEL = TUResources.getString( "label.locale" );
    public static final String SELECT_LOCALE_LABEL = TUResources.getString( "label.selectLocale" );
    public static final String CUSTOM_LOCALE_LABEL = TUResources.getString( "label.custom" );
    public static final String ERROR_LABEL = TUResources.getString( "label.error" );
    
    // titles
    public static final String CONFIRM_TITLE = TUResources.getString( "title.confirm" );
    public static final String SUBMIT_TITLE = TUResources.getString( "title.submit" );
    public static final String HELP_TITLE = TUResources.getString( "title.help" );
    public static final String ERROR_TITLE = TUResources.getString( "title.error" );
    public static final String FATAL_ERROR_TITLE = TUResources.getString( "title.fatalError" );
    public static final String UPDATE_DIALOG_TITLE = TUResources.getString( "title.update" );
    public static final String FIND_TITLE = TUResources.getString( "title.find" );
    
    // menus and items
    public static final String FILE_MENU = TUResources.getString( "menu.file" );
    public static final char FILE_MENU_MNEMONIC = TUResources.getChar( "menu.file.mnemonic", 'F' );
    public static final String EXIT_MENU_ITEM = TUResources.getString( "menu.item.exit" );
    public static final char EXIT_MENU_ITEM_MNEMONIC = TUResources.getChar( "menu.item.exit.mnemonic", 'x' );
    
    // messages
    public static final String CONFIRM_OVERWRITE_MESSAGE = TUResources.getString( "message.confirmOverwrite" );
    public static final String SUBMIT_MESSAGE = TUResources.getString( "message.submit" );
    public static final String CHECKING_FOR_UPDATE_MESSAGE = TUResources.getString( "message.checkingForUpdate" );
    public static final String UPDATE_AVAILABLE_MESSAGE = TUResources.getString( "message.updateAvailable" );
    public static final String ERROR_REPORTING_MESSAGE = TUResources.getString( "message.errorReporting" );
    public static final String HELP_MESSAGE = TUResources.getString( "message.help.general" );
    public static final String HELP_JAR_MESSAGE = TUResources.getString( "message.help.jar" );
    public static final String HELP_LOCALE_MESSAGE = TUResources.getString( "message.help.locale" );
    public static final String VALIDATION_MESSAGE = TUResources.getString( "message.validation" );
    public static final String VALIDATION_HTML = TUResources.getString( "message.validation.html" );
    public static final String VALIDATION_MESSAGE_FORMAT = TUResources.getString( "message.validation.messageFormat" );
    
    // error messages
    public static final String ERROR_NO_SUCH_JAR = TUResources.getString( "error.noSuchJar" );
    public static final String ERROR_NOT_CUSTOM_LOCALE = TUResources.getString( "error.notACustomLocale" );
    public static final String ERROR_VALIDATION = TUResources.getString( "error.validation" );
    
    // tooltips
    public static final String TOOLTIP_TEST = TUResources.getString( "tooltip.test" );
    public static final String TOOLTIP_SUBMIT = TUResources.getString( "tooltip.submit" );
    public static final String TOOLTIP_SAVE = TUResources.getString( "tooltip.save" );
    public static final String TOOLTIP_LOAD = TUResources.getString( "tooltip.load" );
    public static final String TOOLTIP_FIND = TUResources.getString( "tooltip.find" );
    public static final String TOOLTIP_ERROR = TUResources.getString( "tooltip.error" );
}
