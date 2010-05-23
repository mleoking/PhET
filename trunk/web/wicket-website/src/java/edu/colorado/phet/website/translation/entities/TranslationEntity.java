package edu.colorado.phet.website.translation.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.translation.PhetPanelPreview;
import edu.colorado.phet.website.translation.TranslationEntityString;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public abstract class TranslationEntity implements Serializable {

    // TODO: add descriptions to many other entities and strings

    private List<TranslationEntityString> strings = new LinkedList<TranslationEntityString>();
    private List<PhetPanelPreview> previews = new LinkedList<PhetPanelPreview>();

    // translation id => value
    private Map<Integer, Integer> untranslatedMap = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> outOfDateMap = new HashMap<Integer, Integer>();

    public abstract String getDisplayName();

    public int getMinDisplaySize() {
        return 525;
    }

    protected void addString( String key ) {
        strings.add( new TranslationEntityString( key ) );
    }

    protected void addString( String key, String notes ) {
        strings.add( new TranslationEntityString( key, notes ) );
    }

    protected void removeString( String key ) {
        strings.remove( new TranslationEntityString( key ) );
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

    public Map<Integer, Integer> getUntranslatedMap() {
        return untranslatedMap;
    }

    public Map<Integer, Integer> getOutOfDateMap() {
        return outOfDateMap;
    }

    public List<PhetPanelPreview> getPreviews() {
        return previews;
    }

    public List<TranslationEntityString> getStrings() {
        return strings;
    }

    private static List<TranslationEntity> cachedEntities = null;

    /**
     * @return The list (in order) of the entities that should be shown in the translation area
     */
    public static synchronized List<TranslationEntity> getTranslationEntities() {
        if ( cachedEntities == null ) {
            cachedEntities = new LinkedList<TranslationEntity>();
            cachedEntities.add( new CommonEntity() );
            cachedEntities.add( new IndexEntity() );
            cachedEntities.add( new NavigationEntity() );
            cachedEntities.add( new SimulationMainEntity() );
            cachedEntities.add( new TranslatedSimsEntity() );
            cachedEntities.add( new WorkshopsEntity() );
            cachedEntities.add( new SponsorsEntity() );
            cachedEntities.add( new RunSimulationsEntity() );
            cachedEntities.add( new FullInstallEntity() );
            cachedEntities.add( new DonateEntity() );
            cachedEntities.add( new ResearchEntity() );
            cachedEntities.add( new TroubleshootingMainEntity() );
            cachedEntities.add( new TroubleshootingJavaEntity() );
            cachedEntities.add( new TroubleshootingFlashEntity() );
            cachedEntities.add( new TroubleshootingJavascriptEntity() );
            cachedEntities.add( new KeywordsEntity() );
            cachedEntities.add( new ContributeEntity() );
            cachedEntities.add( new ForTeachersEntity() );
            cachedEntities.add( new AboutPhetEntity() );
            cachedEntities.add( new TitlesEntity() );
            cachedEntities.add( new LanguagesEntity() );
            cachedEntities.add( new SimulationsEntity() );
            cachedEntities.add( new SearchEntity() );
            cachedEntities.add( new UserEntity() );
            cachedEntities.add( new ErrorEntity() );
            cachedEntities.add( new TranslationUtilityEntity() );
            cachedEntities.add( new MiscellaneousEntity() );
        }
        return cachedEntities;
    }
}
