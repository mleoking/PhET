package edu.colorado.phet.website.translation;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.markup.html.form.DropDownChoice;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

public class LocaleDropDownChoice extends PhetPanel {

    private LocaleModel selectedLocaleModel;

    public LocaleDropDownChoice( String id, PageContext context ) {
        super( id, context );

        PhetLocales phetLocales = ( (PhetWicketApplication) getApplication() ).getSupportedLocales();

        List<LocaleModel> models = new LinkedList<LocaleModel>();

        for ( String name : phetLocales.getSortedNames() ) {
            Locale locale = phetLocales.getLocale( name );
            models.add( new LocaleModel( locale, name ) );
        }

        selectedLocaleModel = new LocaleModel( LocaleUtils.stringToLocale( "en" ), "English" );

        DropDownChoice localeChoice = new DropDownChoice( "locales", selectedLocaleModel, models );
        add( localeChoice );

    }

    public Locale getLocale() {
        return selectedLocaleModel.getLocale();
    }
}
