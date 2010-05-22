package edu.colorado.phet.website.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.value.ValueMap;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.StringTextField;
import edu.colorado.phet.website.data.*;
import edu.colorado.phet.website.data.util.CategoryChangeHandler;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.StringUtils;

public class AdminSimPage extends AdminPage {
    private Simulation simulation = null;
    private List<LocalizedSimulation> localizedSimulations;

    private static final Logger logger = Logger.getLogger( AdminSimPage.class.getName() );

    public AdminSimPage( PageParameters parameters ) {
        super( parameters );

        int simulationId = parameters.getInt( "simulationId" );

        List<TeachersGuide> guides = new LinkedList<TeachersGuide>();

        localizedSimulations = new LinkedList<LocalizedSimulation>();
        List<Keyword> simKeywords = new LinkedList<Keyword>();
        List<Keyword> simTopics = new LinkedList<Keyword>();
        List<Keyword> allKeywords = new LinkedList<Keyword>();

        final Session session = getHibernateSession();
        final Locale english = LocaleUtils.stringToLocale( "en" );

        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            simulation = (Simulation) session.load( Simulation.class, simulationId );

            for ( Object o : simulation.getLocalizedSimulations() ) {
                LocalizedSimulation lsim = (LocalizedSimulation) o;
                localizedSimulations.add( lsim );
            }

            for ( Object o : simulation.getKeywords() ) {
                simKeywords.add( (Keyword) o );
            }

            for ( Object o : simulation.getTopics() ) {
                simTopics.add( (Keyword) o );
            }

            List allKeys = session.createQuery( "select k from Keyword as k" ).list();
            for ( Object allKey : allKeys ) {
                allKeywords.add( (Keyword) allKey );
            }

            List tguides = session.createQuery( "select tg from TeachersGuide as tg where tg.simulation = :sim" )
                    .setEntity( "sim", simulation ).list();
            for ( Object o : tguides ) {
                guides.add( (TeachersGuide) o );
            }

            tx.commit();
        }
        catch( RuntimeException e ) {
            logger.warn( "Exception: " + e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    logger.error( "ERROR: Error rolling back transaction", e1 );
                }
                throw e;
            }
        }

        final PhetLocalizer localizer = (PhetLocalizer) getLocalizer();

        sortKeywords( allKeywords );

        HibernateUtils.orderSimulations( localizedSimulations, english );

        add( new Label( "simulation-name", simulation.getName() ) );

        add( new AddKeywordForm( "add-keyword", simKeywords, allKeywords ) );

        add( new AddTopicForm( "add-topic", simTopics, allKeywords ) );

        add( new CreateKeywordForm( "create-keyword", allKeywords ) );

        add( new DesignTeamForm( "design-team" ) );
        add( new LibrariesForm( "libraries" ) );
        add( new ThanksForm( "thanks-to" ) );

        add( new DescriptionForm( "description" ) );
        add( new LearningGoalsForm( "learning-goals" ) );

        add( new UnderConstructionForm( "under-construction" ) );
        add( new GuidanceRecommendedForm( "guidance-recommended" ) );
        add( new ClassroomTestedForm( "classroom-tested" ) );
        add( new VisibleForm( "simulation-visibility" ) );

        add( new KilobytesForm( "kilobytes" ) );

        add( new ModifyTranslationForm( "add-set-translation" ) );

        add( new ListView( "translation-list", localizedSimulations ) {
            protected void populateItem( ListItem item ) {
                final LocalizedSimulation lsim = (LocalizedSimulation) item.getModel().getObject();
                item.add( new Label( "locale", LocaleUtils.localeToString( lsim.getLocale() ) ) );
                item.add( new Label( "lang-en", lsim.getLocale().getDisplayName( english ) ) );
                item.add( new Label( "lang-locale", lsim.getLocale().getDisplayName( lsim.getLocale() ) ) );
                item.add( new Label( "title", lsim.getTitle() ) );
                item.add( new Link( "remove" ) {
                    public void onClick() {
                        boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                            public boolean run( Session session ) {
                                LocalizedSimulation localizedSimulation = (LocalizedSimulation) session.load( LocalizedSimulation.class, lsim.getId() );
                                Simulation simulation = localizedSimulation.getSimulation();
                                simulation.getLocalizedSimulations().remove( localizedSimulation );
                                session.delete( localizedSimulation );
                                return true;
                            }
                        } );
                        if ( success ) {
                            localizedSimulations.remove( lsim );
                        }
                    }
                } );
            }
        } );

        if ( !guides.isEmpty() ) {
            add( guides.get( 0 ).getLinker().getLink( "guide-link", getPageContext(), getPhetCycle() ) );
        }
        else {
            add( new InvisibleComponent( "guide-link" ) );
        }

        add( new FileUploadForm( "upload-guide-form" ) );

        add( new Link( "remove-guide" ) {
            public void onClick() {
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        session.delete( session.createQuery( "select tg from TeachersGuide as tg where tg.simulation = :sim" )
                                .setEntity( "sim", simulation ).uniqueResult() );
                        return true;
                    }
                } );
                if ( success ) {
                    setResponsePage( AdminSimsPage.class );
                }
            }
        } );

        add( new CategoryForm( "category-form" ) );

    }

    private void sortKeywords( List<Keyword> allKeywords ) {
        final PhetLocalizer localizer = (PhetLocalizer) getLocalizer();
        Collections.sort( allKeywords, new Comparator<Keyword>() {
            public int compare( Keyword a, Keyword b ) {
                return localizer.getString( a.getKey(), AdminSimPage.this ).compareTo( localizer.getString( b.getKey(), AdminSimPage.this ) );
            }
        } );
    }


    private class AddKeywordForm extends AbstractKeywordForm {
        private AddKeywordForm( String id, List<Keyword> simKeywords, List<Keyword> allKeywords ) {
            super( id, simKeywords, allKeywords );
        }

        public List getKeywordList( Simulation simulation ) {
            return simulation.getKeywords();
        }
    }

    private class AddTopicForm extends AbstractKeywordForm {
        private AddTopicForm( String id, List<Keyword> simKeywords, List<Keyword> allKeywords ) {
            super( id, simKeywords, allKeywords );
        }

        public List getKeywordList( Simulation simulation ) {
            return simulation.getTopics();
        }
    }

    private class CategoryForm extends Form {
        private CategoryDropDownChoice dropDownChoice;
        private List<Category> allCategories = new LinkedList<Category>();
        private List<Category> myCategories = new LinkedList<Category>();

        public CategoryForm( String id ) {
            super( id );

            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Category root = Category.getRootCategory( session );
                    addCategory( root );

                    myCategories.addAll( simulation.getCategories() );

                    return true;
                }

                private void addCategory( Category category ) {
                    if ( !category.isRoot() ) {
                        allCategories.add( category );
                    }
                    for ( Object o : category.getSubcategories() ) {
                        addCategory( (Category) o );
                    }
                }
            } );

            sortCategories();

            add( new ListView( "categories", myCategories ) {
                protected void populateItem( ListItem item ) {
                    final Category category = (Category) item.getModel().getObject();
                    item.add( new Label( "category-english", new ResourceModel( category.getNavLocation( getNavMenu() ).getLocalizationKey() ) ) );
                    item.add( new Link( "category-remove" ) {
                        public void onClick() {
                            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                                public boolean run( Session session ) {
                                    Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                                    Category cat = (Category) session.load( Category.class, category.getId() );

                                    cat.removeSimulation( sim );
                                    session.update( sim );
                                    session.update( cat );
                                    CategoryChangeHandler.notifySimulationChange( category, sim );
                                    return true;
                                }
                            } );
                            if ( success ) {
                                myCategories.remove( category );
                            }
                        }
                    } );
                }
            } );

            dropDownChoice = new CategoryDropDownChoice( "all-categories", allCategories );

            add( dropDownChoice );
        }

        private void sortCategories() {
            Collections.sort( myCategories, new Comparator<Category>() {
                public int compare( Category a, Category b ) {
                    for ( Category category : allCategories ) {
                        if ( category.getId() == a.getId() ) {
                            return -1;
                        }
                        else if ( category.getId() == b.getId() ) {
                            return 1;
                        }
                    }
                    // bailout, should never happen
                    throw new RuntimeException( "unknown category with ids " + a.getId() + ", " + b.getId() );
                }
            } );
        }

        @Override
        protected void onSubmit() {
            final int catId = Integer.valueOf( dropDownChoice.getModelValue() );
            final Category category = new Category();
            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                    session.load( category, catId );

                    boolean ok = true;

                    // make sure the sim doesn't already have the keyword
                    for ( Object o : sim.getCategories() ) {
                        ok = ok && ( (Category) o ).getId() != catId;
                    }

                    if ( ok ) {
                        category.addSimulation( sim );
                        session.update( sim );
                        session.update( category );
                        CategoryChangeHandler.notifySimulationChange( category, sim );
                        return true;
                    }
                    else {
                        // category was already in the list, so we don't want to double-add it to the model
                        return false;
                    }
                }
            } );
            if ( success ) {
                myCategories.add( category );
            }
        }

        private class CategoryDropDownChoice extends DropDownChoice {
            public CategoryDropDownChoice( String id, List<Category> allCategories ) {
                super( id, new Model(), allCategories, new IChoiceRenderer() {
                    public Object getDisplayValue( Object object ) {
                        return PhetWicketApplication.get().getResourceSettings().getLocalizer().getString( ( (Category) object ).getNavLocation( getNavMenu() ).getLocalizationKey(), AdminSimPage.this );
                    }

                    public String getIdValue( Object object, int index ) {
                        return String.valueOf( ( (Category) object ).getId() );
                    }
                } );
            }
        }

    }

    /**
     * Form now abstract, so that we can duplicate the functionality for adding keywords and topics
     */
    private abstract class AbstractKeywordForm extends Form {
        public AdminSimPage.AddKeywordForm.KeywordDropDownChoice dropDownChoice;
        private List<Keyword> simKeywords;

        public abstract List getKeywordList( Simulation simulation );

        public AbstractKeywordForm( String id, final List<Keyword> simKeywords, List<Keyword> allKeywords ) {
            super( id );
            this.simKeywords = simKeywords;

            add( new ListView( "keywords", simKeywords ) {
                protected void populateItem( final ListItem item ) {
                    final Keyword keyword = (Keyword) item.getModel().getObject();
                    item.add( new Label( "keyword-english", new ResourceModel( keyword.getKey() ) ) );
                    item.add( new Link( "keyword-remove" ) {
                        public void onClick() {
                            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                                public boolean run( Session session ) {
                                    Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                                    Keyword kword = (Keyword) session.load( Keyword.class, keyword.getId() );

                                    getKeywordList( sim ).remove( kword );
                                    session.update( sim );
                                    return true;
                                }
                            } );
                            if ( success ) {
                                simKeywords.remove( keyword );
                            }
                        }
                    } );
                    if ( item.getIndex() != 0 ) {
                        item.add( new Link( "keyword-move-up" ) {
                            public void onClick() {
                                swapKeywordOrder( simKeywords, item.getIndex() - 1, item.getIndex() );
                            }
                        } );
                    }
                    else {
                        item.add( new InvisibleComponent( "keyword-move-up" ) );
                    }
                    if ( item.getIndex() < simKeywords.size() - 1 ) {
                        item.add( new Link( "keyword-move-down" ) {
                            public void onClick() {
                                swapKeywordOrder( simKeywords, item.getIndex(), item.getIndex() + 1 );
                            }
                        } );
                    }
                    else {
                        item.add( new InvisibleComponent( "keyword-move-down" ) );
                    }
                }
            } );

            dropDownChoice = new KeywordDropDownChoice( "current-keywords", allKeywords );

            add( dropDownChoice );
        }

        public void swapKeywordOrder( final List<Keyword> simKeywords, final int a, final int b ) {
            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                    Collections.swap( getKeywordList( sim ), a, b );
                    session.update( sim );
                    return true;
                }
            } );
            if ( success ) {
                Collections.swap( simKeywords, a, b );
            }
        }

        @Override
        protected void onSubmit() {
            final int keywordId = Integer.valueOf( dropDownChoice.getModelValue() );
            final Keyword kword = new Keyword();
            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                    session.load( kword, keywordId );

                    boolean ok = true;

                    // make sure the sim doesn't already have the keyword
                    for ( Object o : getKeywordList( sim ) ) {
                        ok = ok && ( (Keyword) o ).getId() != keywordId;
                    }

                    if ( ok ) {
                        getKeywordList( sim ).add( kword );
                        session.update( sim );
                        return true;
                    }
                    else {
                        // keyword was already in the list, so we don't want to double-add it to the model
                        return false;
                    }
                }
            } );
            if ( success ) {
                simKeywords.add( kword );
            }
        }

        public class KeywordDropDownChoice extends DropDownChoice {
            public KeywordDropDownChoice( String id, List<Keyword> allKeywords ) {
                super( id, new Model(), allKeywords, new IChoiceRenderer() {
                    public Object getDisplayValue( Object object ) {
                        return PhetWicketApplication.get().getResourceSettings().getLocalizer().getString( ( (Keyword) object ).getKey(), AdminSimPage.this );
                    }

                    public String getIdValue( Object object, int index ) {
                        return String.valueOf( ( (Keyword) object ).getId() );
                    }
                } );
            }
        }
    }

    private class CreateKeywordForm extends Form {

        private final ValueMap properties = new ValueMap();
        private TextField valueText;
        private TextField keyText;
        private List<Keyword> allKeywords;

        public CreateKeywordForm( String id, List<Keyword> allKeywords ) {
            super( id );
            this.allKeywords = allKeywords;

            add( valueText = new StringTextField( "value", new PropertyModel( properties, "value" ) ) );
            add( keyText = new StringTextField( "key", new PropertyModel( properties, "key" ) ) );
        }

        @Override
        protected void onSubmit() {
            final String key = keyText.getModelObjectAsString();
            final String value = valueText.getModelObjectAsString();
            final String localizationKey = "keyword." + key;
            boolean success = StringUtils.setEnglishString( getHibernateSession(), localizationKey, value );
            if ( success ) {
                final Keyword keyword = new Keyword();
                keyword.setKey( localizationKey );
                success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        List sameKeywords = session.createQuery( "select k from Keyword as k where k.key = :key" ).setString( "key", key ).list();
                        if ( sameKeywords.isEmpty() ) {
                            session.save( keyword );
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                } );
                if ( success ) {
                    allKeywords.add( keyword );
                    sortKeywords( allKeywords );
                }
            }
        }
    }

    public class DesignTeamForm extends TextSetForm {

        public DesignTeamForm( String id ) {
            super( id );
        }

        public void handleString( final String str ) {
            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                    sim.setDesignTeam( str );
                    session.update( sim );
                    return true;
                }
            } );
        }

        public String getCurrentValue() {
            return simulation.getDesignTeam();
        }
    }

    public class LibrariesForm extends TextSetForm {

        public LibrariesForm( String id ) {
            super( id );
        }

        public void handleString( final String str ) {
            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                    sim.setLibraries( str );
                    session.update( sim );
                    return true;
                }
            } );
        }

        public String getCurrentValue() {
            return simulation.getLibraries();
        }
    }

    public class ThanksForm extends TextSetForm {

        public ThanksForm( String id ) {
            super( id );
        }

        public void handleString( final String str ) {
            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                    sim.setThanksTo( str );
                    session.update( sim );
                    return true;
                }
            } );
        }

        public String getCurrentValue() {
            return simulation.getThanksTo();
        }
    }

    private class DescriptionForm extends TextSetForm {

        public DescriptionForm( String id ) {
            super( id );
        }

        public void handleString( final String str ) {
            StringUtils.setEnglishString( getHibernateSession(), simulation.getDescriptionKey(), str );
        }

        public String getCurrentValue() {
            return StringUtils.getDefaultStringDirect( getHibernateSession(), simulation.getDescriptionKey() );
        }
    }

    public class LearningGoalsForm extends TextSetForm {

        public LearningGoalsForm( String id ) {
            super( id );
        }

        public void handleString( final String str ) {
            StringUtils.setEnglishString( getHibernateSession(), simulation.getLearningGoalsKey(), str );
        }

        public String getCurrentValue() {
            return StringUtils.getDefaultStringDirect( getHibernateSession(), simulation.getLearningGoalsKey() );
        }
    }

    private abstract class TextSetForm extends Form {

        private TextArea value;

        public abstract void handleString( String str );

        public abstract String getCurrentValue();

        public TextSetForm( String id ) {
            super( id );

            String curValue = getCurrentValue();
            if ( curValue == null ) {
                curValue = "";
            }
            value = new TextArea( "value", new Model( curValue.replaceAll( "<br/>", "\n" ) ) );
            add( value );
        }

        @Override
        protected void onSubmit() {
            super.onSubmit();
            String text = value.getModelObjectAsString();

            List<String> strings = new LinkedList<String>();
            String str;

            BufferedReader reader = new BufferedReader( new StringReader( text ) );

            try {
                while ( ( str = reader.readLine() ) != null ) {
                    if ( str.length() > 0 ) {
                        strings.add( str );
                    }
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
            }

            String ret = "";

            boolean start = true;
            for ( String string : strings ) {
                if ( !start ) {
                    ret += "<br/>";
                }
                start = false;
                ret += string;
            }

            logger.info( "Submitted:\n" + ret + "\nEND" );

            handleString( ret );
        }
    }

    private class UnderConstructionForm extends CheckBoxForm {
        public UnderConstructionForm( String id ) {
            super( id );
        }

        public void handle( final boolean val ) {
            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                    sim.setUnderConstruction( val );
                    session.update( sim );
                    return true;
                }
            } );
        }

        public boolean getCurrentValue() {
            return simulation.isUnderConstruction();
        }
    }

    private class GuidanceRecommendedForm extends CheckBoxForm {
        public GuidanceRecommendedForm( String id ) {
            super( id );
        }

        public void handle( final boolean val ) {
            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                    sim.setGuidanceRecommended( val );
                    session.update( sim );
                    return true;
                }
            } );
        }

        public boolean getCurrentValue() {
            return simulation.isGuidanceRecommended();
        }
    }

    private class ClassroomTestedForm extends CheckBoxForm {
        public ClassroomTestedForm( String id ) {
            super( id );
        }

        public void handle( final boolean val ) {
            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                    sim.setClassroomTested( val );
                    session.update( sim );
                    return true;
                }
            } );
        }

        public boolean getCurrentValue() {
            return simulation.isClassroomTested();
        }
    }

    private class VisibleForm extends CheckBoxForm {
        public VisibleForm( String id ) {
            super( id );
        }

        public void handle( final boolean val ) {
            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                    sim.setSimulationVisible( val );
                    session.update( sim );
                    return true;
                }
            } );
        }

        public boolean getCurrentValue() {
            return simulation.isSimulationVisible();
        }
    }

    private static abstract class CheckBoxForm extends Form {
        private CheckBox checkbox;

        public abstract void handle( boolean val );

        public abstract boolean getCurrentValue();

        public CheckBoxForm( String id ) {
            super( id );

            checkbox = new CheckBox( "value", new Model( new Boolean( getCurrentValue() ) ) );
            add( checkbox );
        }

        @Override
        protected void onSubmit() {
            super.onSubmit();
            Boolean v = (Boolean) checkbox.getModelObject();
            handle( v );
        }
    }

    private class KilobytesForm extends Form {
        private TextField textfield;

        private KilobytesForm( String id ) {
            super( id );

            textfield = new StringTextField( "value", new Model( Integer.toString( simulation.getKilobytes() ) ) );
            add( textfield );
        }

        @Override
        protected void onSubmit() {
            super.onSubmit();
            try {
                final int kilobytes = Integer.valueOf( textfield.getModelObjectAsString() );
                HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                        sim.setKilobytes( kilobytes );
                        session.update( sim );
                        return true;
                    }
                } );
            }
            catch( NumberFormatException e ) {
                // silent failure
            }

        }
    }

    private class ModifyTranslationForm extends Form {

        private TextField localeField;
        private TextField titleField;

        public ModifyTranslationForm( String id ) {
            super( id );

            localeField = new StringTextField( "locale", new Model( "" ) );
            localeField.setEscapeModelStrings( false );
            add( localeField );
            titleField = new StringTextField( "title", new Model( "" ) );
            add( titleField );
        }

        @Override
        protected void onSubmit() {
            super.onSubmit();

            final Locale locale = LocaleUtils.stringToLocale( localeField.getModelObjectAsString() );
            final String title = titleField.getModelObjectAsString();

            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    List matching = session.createQuery( "select ls from LocalizedSimulation as ls, Simulation as s where s.id = :simId and ls.simulation = s and ls.locale = :locale" )
                            .setInteger( "simId", simulation.getId() ).setLocale( "locale", locale ).list();
                    if ( matching.isEmpty() ) {
                        // we are adding a new translation for this sim
                        LocalizedSimulation lsim = new LocalizedSimulation();
                        lsim.setLocale( locale );
                        lsim.setTitle( title );
                        lsim.setSimulation( (Simulation) session.load( Simulation.class, simulation.getId() ) );
                        session.save( lsim );
                        localizedSimulations.add( lsim );
                        HibernateUtils.orderSimulations( localizedSimulations, PhetWicketApplication.getDefaultLocale() );
                    }
                    else {
                        LocalizedSimulation lsim = (LocalizedSimulation) matching.get( 0 );
                        lsim.setTitle( title );
                        session.update( lsim );
                        for ( LocalizedSimulation s : localizedSimulations ) {
                            if ( s.getLocale().equals( locale ) ) {
                                s.setTitle( title );
                            }
                        }
                    }
                    return true;
                }
            } );
        }
    }

    private class FileUploadForm extends Form {

        private FileUploadField field;

        public FileUploadForm( String id ) {
            super( id );

            add( field = new FileUploadField( "file" ) );

            setMultiPart( true );

        }

        @Override
        protected void onSubmit() {
            final FileUpload fup = field.getFileUpload();
            if ( fup != null ) {
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        List tguides = session.createQuery( "select tg from TeachersGuide as tg where tg.simulation = :sim" )
                                .setEntity( "sim", simulation ).list();

                        boolean old = !tguides.isEmpty();
                        TeachersGuide guide = old ? (TeachersGuide) tguides.get( 0 ) : new TeachersGuide();

                        if ( !old ) {
                            guide.setSimulation( simulation );
                            guide.setFilename( TeachersGuide.createFilename( simulation ) );
                            //guide.setLocation( guide.getFileLocation().getAbsolutePath() );
                        }

                        File file = guide.getFileLocation();
                        file.getParentFile().mkdirs();
                        try {
                            file.createNewFile();
                            fup.writeTo( file );
                            guide.setSize( (int) file.length() );
                        }
                        catch( IOException e ) {
                            e.printStackTrace();
                            logger.warn( e );
                            return false;
                        }

                        if ( old ) {
                            session.update( guide );
                        }
                        else {
                            session.save( guide );
                        }

                        return true;
                    }
                } );

                if ( success ) {
                    setResponsePage( AdminSimsPage.class );
                }
            }
        }
    }
}
