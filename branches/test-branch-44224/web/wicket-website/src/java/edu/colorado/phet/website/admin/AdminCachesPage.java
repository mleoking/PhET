package edu.colorado.phet.website.admin;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.cache.CacheUtils;
import edu.colorado.phet.website.util.SearchUtils;

/**
 * Useful for clearing caches or updating the search index
 */
public class AdminCachesPage extends AdminPage {

    private static final Logger logger = Logger.getLogger( AdminCachesPage.class.getName() );

    public AdminCachesPage( PageParameters parameters ) {
        super( parameters );

        add( new Link( "clear-strings" ) {
            public void onClick() {
                CacheUtils.clearTranslatedStringCache();
            }
        } );

        add( new Link( "clear-panels" ) {
            public void onClick() {
                CacheUtils.clearPanelCache();
            }
        } );

        add( new Link( "clear-simulations" ) {
            public void onClick() {
                CacheUtils.clearSimulationCache();
            }
        } );

        add( new Link( "clear-second-level" ) {
            public void onClick() {
                CacheUtils.clearSecondLevelCache();
            }
        } );

        add( new Link( "clear-all" ) {
            public void onClick() {
                CacheUtils.clearAllCaches();
            }
        } );

        add( new Link( "rebuild-index" ) {
            public void onClick() {
                SearchUtils.reindex( PhetWicketApplication.get(), getPhetLocalizer() );
            }
        } );

    }

}