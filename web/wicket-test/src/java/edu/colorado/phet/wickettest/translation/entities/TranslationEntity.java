package edu.colorado.phet.wickettest.translation.entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.translation.PhetPanelPreview;
import edu.colorado.phet.wickettest.translation.TranslationEntityString;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public abstract class TranslationEntity implements Serializable {
    private List<TranslationEntityString> strings = new LinkedList<TranslationEntityString>();
    private List<PhetPanelPreview> previews = new LinkedList<PhetPanelPreview>();

    public abstract String getDisplayName();

    protected void addString( String key ) {
        strings.add( new TranslationEntityString( key ) );
    }

    protected void addString( String key, String notes ) {
        strings.add( new TranslationEntityString( key, notes ) );
    }

    protected void addPreview( final PhetPanelFactory factory, final String name ) {
        previews.add( new PhetPanelPreview() {
            public String getName() {
                return name;
            }

            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return factory.getNewPanel( id, context, requestCycle );
            }
        } );
    }

    public boolean hasPreviews() {
        return !previews.isEmpty();
    }

    public List<PhetPanelPreview> getPreviews() {
        return previews;
    }

    public List<TranslationEntityString> getStrings() {
        return strings;
    }

    public static List<TranslationEntity> getTranslationEntities() {
        List<TranslationEntity> entities = new LinkedList<TranslationEntity>();
        entities.add( new CommonEntity() );
        entities.add( new IndexEntity() );
        entities.add( new NavigationEntity() );
        entities.add( new SimulationMainEntity() );
        entities.add( new WorkshopsEntity() );
        entities.add( new SponsorsEntity() );
        entities.add( new RunSimulationsEntity() );
        entities.add( new TroubleshootingMainEntity() );
        entities.add( new TroubleshootingJavaEntity() );
        entities.add( new TroubleshootingFlashEntity() );
        entities.add( new TroubleshootingJavascriptEntity() );
        entities.add( new KeywordsEntity() );
        entities.add( new AboutPhetEntity() );
        entities.add( new TitlesEntity() );
        return entities;
    }
}
