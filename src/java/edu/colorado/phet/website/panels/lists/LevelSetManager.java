package edu.colorado.phet.website.panels.lists;

import java.util.Collection;
import java.util.Locale;

import org.hibernate.Session;

import edu.colorado.phet.website.data.contribution.ContributionLevel;
import edu.colorado.phet.website.data.contribution.Level;

/**
 * Handles selection of a subset of levels
 */
public abstract class LevelSetManager extends EnumSetManager<Level> {

    public LevelSetManager( Session session, final Locale locale ) {
        super( session, locale );
    }

    public Collection<Level> getAllValues() {
        return ContributionLevel.getCurrentLevels();
    }

    public String getTranslationKey( Level val ) {
        return val.getTranslationKey();
    }
}