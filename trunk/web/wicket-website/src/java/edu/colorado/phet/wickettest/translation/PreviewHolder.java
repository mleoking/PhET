package edu.colorado.phet.wickettest.translation;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.wickettest.components.InvisibleComponent;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.entities.TranslationEntity;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class PreviewHolder extends PhetPanel {

    public PreviewHolder( String id, final PageContext context, TranslationEntity entity ) {
        super( id, context );

        setOutputMarkupId( true );

        add( new ListView( "sub-panels", entity.getPreviews() ) {
            protected void populateItem( ListItem item ) {
                PhetPanelPreview preview = (PhetPanelPreview) item.getModel().getObject();
                item.add( preview.getNewPanel( "holder-sub-panel", context, (PhetRequestCycle) getRequestCycle() ) );
                item.add( new Label( "preview-number", String.valueOf( item.getIndex() + 1 ) ) );
                item.add( new Label( "preview-name", preview.getName() ) );
            }
        } );

        if ( entity.hasPreviews() ) {
            add( new InvisibleComponent( "previews-unavailable" ) );
        }
        else {
            add( new Label( "previews-unavailable", "Previews are unavailable for this section." ) );
        }
    }

}
