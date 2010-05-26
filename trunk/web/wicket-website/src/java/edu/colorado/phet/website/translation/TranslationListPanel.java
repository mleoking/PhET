package edu.colorado.phet.website.translation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.content.IndexPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.WicketUtils;

public class TranslationListPanel extends PhetPanel {

    private PhetLocales phetLocales;

    private static final Logger logger = Logger.getLogger( TranslationListPanel.class.getName() );

    public TranslationListPanel( String id, PageContext context ) {
        super( id, context );

        final List<Translation> translations = new LinkedList<Translation>();
        final Map<Translation, Integer> sizes = new HashMap<Translation, Integer>();

        phetLocales = ( (PhetWicketApplication) getApplication() ).getSupportedLocales();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List trans = session.createQuery( "select t from Translation as t order by t.id" ).list();
                for ( Object tran : trans ) {
                    Translation translation = (Translation) tran;

                    if ( !translation.isAuthorizedUser( PhetSession.get().getUser() ) ) {
                        // don't show translations that the user doesn't have access to
                        continue;
                    }

                    // count the number of strings
                    sizes.put( translation, ( (Long) session.createQuery( "select count(*) from TranslatedString as ts where ts.translation = :translation" ).setEntity( "translation", translation ).iterate().next() ).intValue() );

                    translations.add( translation );
                }
                return true;
            }
        } );

        add( new TranslationListView( "translation-list", translations, sizes ) );

        if ( PhetSession.get().getUser().isTeamMember() ) {
            add( new Label( "admin-cols", "" ) );
        }
        else {
            add( new InvisibleComponent( "admin-cols" ) );
        }
    }

    private class TranslationListView extends ListView<Translation> {
        private final List<Translation> translations;
        private final Map<Translation, Integer> sizes;

        public TranslationListView( String id, List<Translation> translations, Map<Translation, Integer> sizes ) {
            super( id, translations );
            this.translations = translations;
            this.sizes = sizes;
        }

        protected void populateItem( ListItem<Translation> item ) {
            final Translation translation = item.getModelObject();
            final PhetUser user = PhetSession.get().getUser();

            // whether this is the main English translation
            boolean isDefaultEnglish = translation.isVisible() && translation.getLocale().equals( PhetWicketApplication.getDefaultLocale() );

            boolean visibleToggleShown = user.isTeamMember() && !isDefaultEnglish;
            boolean editShown = user.isTeamMember() || !translation.isLocked();
            boolean deleteShown = user.isTeamMember() && !isDefaultEnglish;

            item.add( new Label( "id", String.valueOf( translation.getId() ) ) );
            item.add( new Label( "locale", phetLocales.getName( translation.getLocale() ) + " (" + LocaleUtils.localeToString( translation.getLocale() ) + ")" ) );
            item.add( new Label( "num-strings", String.valueOf( sizes.get( translation ) ) ) );
            Label visibleLabel = new Label( "visible-label", String.valueOf( translation.isVisible() ? "visible" : "hidden" ) );
            if ( translation.isVisible() ) {
                visibleLabel.add( new AttributeAppender( "class", true, new Model<String>( "translation-visible" ), " " ) );
            }
            item.add( visibleLabel );

            // TODO: add visibility switcher component
            if ( visibleToggleShown ) {
                item.add( new Link( "visible-toggle" ) {
                    public void onClick() {
                        toggleVisibility( translation );
                    }
                } );
            }
            else {
                item.add( new InvisibleComponent( "visible-toggle" ) );
            }

            PageContext newContext = new PageContext( "/translation/" + String.valueOf( translation.getId() ) + "/", "", translation.getLocale() );
            Link popupLink = IndexPage.getLinker().getLink( "preview", newContext, getPhetCycle() );
            popupLink.setPopupSettings( new PopupSettings( PopupSettings.LOCATION_BAR | PopupSettings.MENU_BAR | PopupSettings.RESIZABLE
                                                           | PopupSettings.SCROLLBARS | PopupSettings.STATUS_BAR | PopupSettings.TOOL_BAR ) );
            item.add( popupLink );

            if ( editShown ) {
                item.add( new Link( "edit" ) {
                    public void onClick() {
                        PageParameters params = new PageParameters();
                        params.put( "translationId", translation.getId() );
                        params.put( "translationLocale", LocaleUtils.localeToString( translation.getLocale() ) );
                        setResponsePage( TranslationEditPage.class, params );
                    }
                } );
            }
            else {
                item.add( new InvisibleComponent( "edit" ) );
            }

            Label lockLabel = new Label( "locked-label", String.valueOf( translation.isLocked() ? "locked" : "editable" ) );
            if ( translation.isLocked() ) {
                lockLabel.add( new AttributeAppender( "class", true, new Model<String>( "translation-locked" ), " " ) );
            }
            item.add( lockLabel );

            if ( user.isTeamMember() ) {
                if ( deleteShown ) {
                    item.add( new Link( "delete" ) {
                        public void onClick() {
                            delete( translation );
                        }
                    } );
                }
                else {
                    item.add( new Label( "delete", "" ) );
                }
                item.add( new Link( "lock-toggle" ) {
                    @Override
                    public void onClick() {
                        toggleLock( translation );
                    }
                } );

            }
            else {
                item.add( new InvisibleComponent( "lock-toggle" ) );
                item.add( new InvisibleComponent( "delete" ) );
            }

            WicketUtils.highlightListItem( item );
        }

        public void toggleLock( final Translation translation ) {
            final boolean[] ret = new boolean[1];
            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Translation tr = (Translation) session.load( Translation.class, translation.getId() );

                    tr.setLocked( !tr.isLocked() );
                    ret[0] = tr.isLocked();
                    session.update( tr );

                    return true;
                }
            } );

            if ( success ) {
                translation.setLocked( ret[0] );
            }
        }

        public void toggleVisibility( final Translation translation ) {
            final boolean[] ret = new boolean[1];
            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Translation tr = (Translation) session.load( Translation.class, translation.getId() );
                    List otherTranslations = session.createQuery( "select t from Translation as t where t.visible = true and t.locale = :locale" ).setLocale( "locale", translation.getLocale() ).list();

                    if ( !tr.isVisible() && !otherTranslations.isEmpty() ) {
                        throw new RuntimeException( "There is already a visible translation of that locale" );
                    }

                    tr.setVisible( !tr.isVisible() );
                    ret[0] = tr.isVisible();
                    session.update( tr );
                    
                    return true;
                }
            } );
            if ( success ) {
                translation.setVisible( ret[0] );
                if ( translation.isVisible() ) {
                    ( (PhetWicketApplication) getApplication() ).addTranslation( translation );
                }
                else {
                    ( (PhetWicketApplication) getApplication() ).removeTranslation( translation );
                }
            }
        }

        public void delete( final Translation translation ) {
            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    session.load( translation, translation.getId() );

                    translations.remove( translation );
                    for ( Object o : translation.getTranslatedStrings() ) {
                        session.delete( o );
                    }
                    for ( Object o : translation.getAuthorizedUsers() ) {
                        PhetUser user = (PhetUser) o;
                        user.getTranslations().remove( translation );
                        session.update( user );
                    }
                    session.delete( translation );
                    return true;
                }
            } );
        }
    }

}
