package edu.colorado.phet.wickettest.translation;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.wickettest.WicketApplication;
import edu.colorado.phet.wickettest.components.InvisibleComponent;
import edu.colorado.phet.wickettest.data.TranslatedString;
import edu.colorado.phet.wickettest.data.Translation;
import edu.colorado.phet.wickettest.panels.PanelHolder;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.entities.TranslationEntity;
import edu.colorado.phet.wickettest.util.HibernateTask;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PageContext;

public class TranslationEntityListPanel extends PhetPanel {

    public TranslationEntityListPanel( String id, PageContext context, final TranslationEditPage page ) {
        super( id, context );

        setModel( new StringMapModel( page.getTranslationId() ) );

        setOutputMarkupId( true );

        ListView entities = new ListView( "translation-entities", TranslationEntity.getTranslationEntities() ) {
            private AttributeAppender appender = new AttributeAppender( "class", true, new Model( "selected" ), " " );
            private ListItem selected = null;

            protected void populateItem( final ListItem item ) {
                final TranslationEntity entity = (TranslationEntity) item.getModel().getObject();
                //System.out.println( "Populating with Entity " + entity.getDisplayName() );
                AjaxLink link = new AjaxLink( "translation-entity-link" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        PanelHolder panelHolder = page.getPanelHolder();
                        TranslateEntityPanel subPanel = page.getSubPanel();
                        panelHolder.remove( subPanel );
                        subPanel = new TranslateEntityPanel( panelHolder.getWicketId(), page.getPageContext(), entity, page.getTranslationId(), page.getTestLocale() );
                        panelHolder.add( subPanel );
                        target.addComponent( panelHolder );
                        target.addComponent( TranslationEntityListPanel.this );
                        page.setSelectedEntityName( entity.getDisplayName() );
                        //target.appendJavascript( "document.getElementById('translation-holder').style.width = 200;" );
                    }
                };
                if ( page.getSelectedEntityName().equals( entity.getDisplayName() ) ) {
                    if ( selected != null ) {
                        selected.remove( appender );
                        selected = null;
                    }
                    item.add( appender );
                    selected = item;
                }
                link.add( new Label( "translation-entity-display-name", entity.getDisplayName() ) );

                final int[] counts = new int[2];

                /*
                HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        boolean yet = false;
                        String match = "";
                        for ( TranslationEntityString string : entity.getStrings() ) {
                            if ( yet == true ) {
                                match += " or ";
                            }
                            else {
                                yet = true;
                            }
                            match += "ts.key = '" + string.getKey() + "'";
                        }
                        if ( !yet ) {
                            match = "true";
                        }

                        // TODO: remove possible security risk here, although one would need access to the admin panel or database anyways!
                        String query = "select count(ts) from Translation as t, TranslatedString as ts where ts.translation = t and t.id = :id and (" + match + ")";
                        counts[1] = entity.getStrings().size() - ( (Long) session.createQuery( query ).setInteger( "id", page.getTranslationId() ).iterate().next() ).intValue();

                        String uQuery = "select count(distinct ts) from Translation as t, Translation as e, TranslatedString as ts, TranslatedString as es" +
                                        " where" +
                                        " ts.translation = t and" +
                                        " es.translation = e and" +
                                        " t.id = :id and" +
                                        " e.visible = true and" +
                                        " e.locale = :locale and" +
                                        " ts.key = es.key and" +
                                        " ts.updatedAt < es.updatedAt and" +
                                        " (" + match + ")";
                        counts[0] = ( (Long) session.createQuery( uQuery )
                                .setLocale( "locale", WicketApplication.getDefaultLocale() )
                                .setInteger( "id", page.getTranslationId() ).iterate().next() ).intValue();

                        return true;
                    }
                } );
                */

                counts[0] = 0;
                counts[1] = 0;

                Map<String, StringDat> map = (Map<String, StringDat>) TranslationEntityListPanel.this.getModel().getObject();

                for ( TranslationEntityString string : entity.getStrings() ) {
                    StringDat dat = map.get( string.getKey() );
                    if ( dat == null ) {
                        counts[1]++;
                    }
                    else if ( dat.getLocalDate().compareTo( dat.getEnglishDate() ) < 0 ) {
                        counts[0]++;
                    }
                }

                /// END TEST

                int numOutOfDate = counts[0];
                int numUntranslated = counts[1];
                if ( numOutOfDate == 0 ) {
                    link.add( new InvisibleComponent( "out-of-date" ) );
                }
                else {
                    link.add( new Label( "out-of-date", "(" + String.valueOf( numOutOfDate ) + ")" ) );
                }
                if ( numUntranslated == 0 ) {
                    link.add( new InvisibleComponent( "untranslated" ) );
                }
                else {
                    link.add( new Label( "untranslated", "(" + String.valueOf( numUntranslated ) + ")" ) );
                }
                item.add( link );
            }
        };
        add( entities );
    }

    private class StringMapModel extends LoadableDetachableModel {
        private int translationId;

        private StringMapModel( int translationId ) {
            this.translationId = translationId;
        }

        protected Object load() {
            final Map<String, StringDat> map = new HashMap<String, StringDat>();
            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Translation localTranslation = (Translation) session.load( Translation.class, translationId );
                    Translation englishTranslation = (Translation) session.createQuery( "select t from Translation as t where t.visible = true and t.locale = :locale" ).setLocale( "locale", WicketApplication.getDefaultLocale() ).uniqueResult();
                    for ( Object o : localTranslation.getTranslatedStrings() ) {
                        TranslatedString string = (TranslatedString) o;
                        map.put( string.getKey(), new StringDat( string.getKey(), string.getUpdatedAt() ) );
                    }
                    for ( Object o : englishTranslation.getTranslatedStrings() ) {
                        TranslatedString string = (TranslatedString) o;
                        StringDat dat = map.get( string.getKey() );
                        if ( dat != null ) {
                            dat.setEnglishDate( string.getUpdatedAt() );
                        }
                    }
                    return true;
                }
            } );
            return map;
        }
    }

    private static class StringDat implements Serializable {
        private String key;
        private Date englishDate;
        private Date localDate;

        private StringDat( String key, Date localDate ) {
            this.key = key;
            this.localDate = localDate;
        }

        public String getKey() {
            return key;
        }

        public void setKey( String key ) {
            this.key = key;
        }

        public Date getEnglishDate() {
            return englishDate;
        }

        public void setEnglishDate( Date englishDate ) {
            this.englishDate = englishDate;
        }

        public Date getLocalDate() {
            return localDate;
        }

        public void setLocalDate( Date localDate ) {
            this.localDate = localDate;
        }
    }
}
