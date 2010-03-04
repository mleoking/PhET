package edu.colorado.phet.website.translation;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.markup.html.form.DropDownChoice;

import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

/**
 * Presents the user with a drop-down choice of locales. Should almost always be used from within a form, otherwise the
 * locale will not be updated properly (does not use AJAX)
 */
public class LocaleDropDownChoice extends PhetPanel {

    private LocaleModel selectedLocaleModel;

    public LocaleDropDownChoice( String id, PageContext context ) {
        this( id, context, PhetWicketApplication.getDefaultLocale() );
    }

    public LocaleDropDownChoice( String id, PageContext context, Locale defaultLocale ) {
        super( id, context );

        PhetLocales phetLocales = ( (PhetWicketApplication) getApplication() ).getSupportedLocales();

        List<LocaleModel> models = new LinkedList<LocaleModel>();

        for ( String name : phetLocales.getSortedNames() ) {
            Locale locale = phetLocales.getLocale( name );
            models.add( new LocaleModel( locale, name ) );
        }

        selectedLocaleModel = new LocaleModel( defaultLocale, phetLocales.getName( defaultLocale ) );

        DropDownChoice localeChoice = new DropDownChoice( "locales", selectedLocaleModel, models );
        add( localeChoice );

    }

    /**
     * Get the user-selected locale
     *
     * @return The user-selected locale
     */
    public Locale getLocale() {
        return selectedLocaleModel.getLocale();
    }
}
