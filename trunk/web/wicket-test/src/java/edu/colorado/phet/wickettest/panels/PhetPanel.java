package edu.colorado.phet.wickettest.panels;

import java.util.Locale;

import org.apache.wicket.markup.html.panel.Panel;

import edu.colorado.phet.wickettest.util.PageContext;

public class PhetPanel extends Panel {

    private Locale myLocale;

    // TODO: refactor out unneeded servlet context for hibernate usage

    public PhetPanel( String id, PageContext context ) {
        super( id );
        this.myLocale = context.getLocale();
    }

    public Locale getMyLocale() {
        return myLocale;
    }

    /**
     * Override locale, so that localized strings with wicket:message will use this panel's locale
     */
    @Override
    public Locale getLocale() {
        return myLocale;
    }
}
