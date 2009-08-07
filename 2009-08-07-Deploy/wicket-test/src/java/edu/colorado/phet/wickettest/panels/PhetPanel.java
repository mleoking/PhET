package edu.colorado.phet.wickettest.panels;

import java.util.Locale;

import org.apache.wicket.markup.html.panel.Panel;

import edu.colorado.phet.wickettest.WicketApplication;
import edu.colorado.phet.wickettest.menu.NavMenu;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class PhetPanel extends Panel {

    private Locale myLocale;

    public PhetPanel( String id, PageContext context ) {
        super( id );
        this.myLocale = context.getLocale();
    }

    public Locale getMyLocale() {
        return myLocale;
    }

    public org.hibernate.Session getHibernateSession() {
        return ( (PhetRequestCycle) getRequestCycle() ).getHibernateSession();
    }

    public NavMenu getNavMenu() {
        return ( (WicketApplication) getApplication() ).getMenu();
    }

    /**
     * Override locale, so that localized strings with wicket:message will use this panel's locale
     */
    @Override
    public Locale getLocale() {
        return myLocale;
    }
}
