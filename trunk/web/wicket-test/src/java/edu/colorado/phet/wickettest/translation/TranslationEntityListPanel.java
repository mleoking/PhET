package edu.colorado.phet.wickettest.translation;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import edu.colorado.phet.wickettest.panels.PanelHolder;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.entities.TranslationEntity;
import edu.colorado.phet.wickettest.util.PageContext;

public class TranslationEntityListPanel extends PhetPanel {
    public TranslationEntityListPanel( String id, PageContext context, final TranslationTestPage page ) {
        super( id, context );

        setOutputMarkupId( true );

        ListView entities = new ListView( "translation-entities", TranslationEntity.getTranslationEntities() ) {
            private AttributeAppender appender = new AttributeAppender( "class", true, new Model( "selected" ), " " );
            private ListItem selected = null;

            protected void populateItem( final ListItem item ) {
                final TranslationEntity entity = (TranslationEntity) item.getModel().getObject();
                System.out.println( "Populating with Entity " + entity.getDisplayName() );
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
                item.add( link );
            }
        };
        add( entities );
    }
}
