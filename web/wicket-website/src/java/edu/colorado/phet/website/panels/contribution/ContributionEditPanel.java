package edu.colorado.phet.website.panels.contribution;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.authentication.AuthenticatedPage;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.content.contribution.ContributionPage;
import edu.colorado.phet.website.content.contribution.ContributionSuccessPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.contribution.*;
import edu.colorado.phet.website.data.util.IntId;
import edu.colorado.phet.website.panels.MultipleFileUploadPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.panels.lists.*;
import edu.colorado.phet.website.translation.LocaleDropDownChoice;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.StringUtils;

/**
 * This panel presents the user with a form that either creates a new contribution or modifies an existing one.
 */
public class ContributionEditPanel extends PhetPanel {

    /**
     * The contribution itself. We create it upon loading this panel, and when the user submits successful changes it
     * should be filled with most of its parameters automatically. The others have set models that are integrated in
     * and synchronized (like levels and type, which can have multiples of each).
     */
    private Contribution contribution;

    /**
     * Whether we are creating a new contribution, or otherwise modifying an existing one
     */
    private boolean creating;

    private PageContext context;

    private SimSetManager simManager;
    private TypeSetManager typeManager;
    private LevelSetManager levelManager;
    private SubjectSetManager subjectManager;

    private List existingFiles;
    private List filesToRemove;

    private static final Logger logger = Logger.getLogger( ContributionEditPanel.class.getName() );
    private FeedbackPanel feedback;

    public ContributionEditPanel( String id, PageContext context, Contribution preContribution ) {
        super( id, context );

        // TODO: add labels to the form components that aren't easy to label

        // should be already handled by now, this is just a sanity check
        AuthenticatedPage.checkSignedIn();

        this.context = context;

        add( HeaderContributor.forCss( CSS.CONTRIBUTION_MAIN ) );

        contribution = preContribution;

        creating = contribution.getId() == 0;

        existingFiles = new LinkedList();
        filesToRemove = new LinkedList();

        final PhetUser currentUser = PhetSession.get().getUser();

        if ( !creating ) {
            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    List list = session.createQuery( "select f from ContributionFile as f where f.contribution.id = :cid" )
                            .setInteger( "cid", contribution.getId() ).list();
                    for ( Object o : list ) {
                        existingFiles.add( o );
                    }
                    ContributionFile.orderFilesCast( existingFiles );
                    if ( !currentUser.isTeamMember() ) {
                        Contribution tmp = (Contribution) session.load( Contribution.class, contribution.getId() );
                        if ( currentUser.getId() != tmp.getPhetUser().getId() ) {
                            return false;
                        }
                    }
                    return true;
                }
            } );

            if ( !success ) {
                logger.info( "Auth error?" );
                // authentication failure!
                // TODO: possibly bail out here? investigate wicket options
                setResponsePage( PhetWicketApplication.get().getApplicationSettings().getAccessDeniedPage() );
            }
        }
        else {
            // initialize defaults for a new contribution
            contribution.setFromPhet( currentUser.isTeamMember() );
            contribution.setLocale( PhetWicketApplication.getDefaultLocale() );
            contribution.setAuthors( currentUser.getName() );
            contribution.setAuthorOrganization( currentUser.getOrganization() );
            contribution.setContactEmail( currentUser.getEmail() );
            contribution.setApproved( true );
        }

        // initialize selectors

        simManager = new SimSetManager( getHibernateSession(), getLocale() ) {
            @Override
            public Set getInitialSimulations( Session session ) {
                if ( contribution.getId() != 0 ) {
                    Contribution activeContribution = (Contribution) session.load( Contribution.class, contribution.getId() );
                    return activeContribution.getSimulations();
                }
                else {
                    return new HashSet();
                }
            }
        };

        typeManager = new TypeSetManager( getHibernateSession(), getLocale() ) {
            @Override
            public Set getInitialValues( Session session ) {
                HashSet set = new HashSet();
                if ( contribution.getId() != 0 ) {
                    Contribution activeContribution = (Contribution) session.load( Contribution.class, contribution.getId() );
                    Set contributionTypes = activeContribution.getTypes();
                    for ( Object o : contributionTypes ) {
                        set.add( ( (ContributionType) o ).getType() );
                    }
                }
                return set;
            }
        };

        levelManager = new LevelSetManager( getHibernateSession(), getLocale() ) {
            public Collection<Level> getInitialValues( Session session ) {
                HashSet set = new HashSet();
                if ( contribution.getId() != 0 ) {
                    Contribution activeContribution = (Contribution) session.load( Contribution.class, contribution.getId() );
                    Set contributionLevels = activeContribution.getLevels();
                    for ( Object o : contributionLevels ) {
                        set.add( ( (ContributionLevel) o ).getLevel() );
                    }
                }
                return set;
            }
        };

        subjectManager = new SubjectSetManager( getHibernateSession(), getLocale() ) {
            public Collection<Subject> getInitialValues( Session session ) {
                HashSet set = new HashSet();
                if ( contribution.getId() != 0 ) {
                    Contribution activeContribution = (Contribution) session.load( Contribution.class, contribution.getId() );
                    Set contributionSubjects = activeContribution.getSubjects();
                    for ( Object o : contributionSubjects ) {
                        set.add( ( (ContributionSubject) o ).getSubject() );
                    }
                }
                return set;
            }
        };

        add( new ContributionForm( "contributionform" ) );

        feedback = new FeedbackPanel( "feedback" );
        feedback.setVisible( false );
        add( feedback );
    }

    public final class ContributionForm extends Form<Contribution> {

        private MultipleFileUploadPanel uploadPanel;

        private DurationDropDownChoice durationChoice;

        private LocaleDropDownChoice localeChoice;

        public ContributionForm( String id ) {
            super( id );

            CompoundPropertyModel<Contribution> model = new CompoundPropertyModel<Contribution>( contribution );
            setModel( model );

            // for ajax fallback link
            setOutputMarkupId( true );

            setMultiPart( true );
            setMaxSize( Bytes.megabytes( 64 ) );

            add( new RequiredTextField<String>( "authors" ) );
            add( new RequiredTextField<String>( "authorOrganization" ) );
            add( new RequiredTextField<String>( "contactEmail" ).add( EmailAddressValidator.getInstance() ) );
            add( new RequiredTextField<String>( "title" ) );
            add( new RequiredTextField<String>( "keywords" ) );
            add( new TextArea<String>( "description" ) );

            uploadPanel = new MultipleFileUploadPanel( "file-upload", context );
            add( uploadPanel );

            add( new LocalizedText( "contribution-file-tip", "contribution.edit.newfiles.tip", new Object[]{
                    ContributionFile.getFiletypes( ContributionEditPanel.this )
            } ) );

            final SortedList<SimOrderItem> simList = simManager.getComponent( "simulations", context );
            add( simList );
            final SortedList<EnumSetManager.ListItem<Type>> typeList = typeManager.getComponent( "types", context );
            add( typeList );
            final SortedList<EnumSetManager.ListItem<Level>> levelList = levelManager.getComponent( "levels", context );
            add( levelList );

            add( subjectManager.getComponent( "subjects", context ) );

            durationChoice = new DurationDropDownChoice( "duration", creating ? 0 : contribution.getDuration() );
            add( durationChoice );

            add( new CheckBox( "answersIncluded" ) );

            localeChoice = new LocaleDropDownChoice( "locale", context );
            add( localeChoice );

            if ( PhetSession.get().getUser().isTeamMember() ) {
                add( new CheckBox( "fromPhet" ) );
                add( new CheckBox( "goldStar" ) );
                add( new CheckBox( "approved" ) );
            }
            else {
                add( new InvisibleComponent( "fromPhet" ) );
                add( new InvisibleComponent( "goldStar" ) );
                add( new InvisibleComponent( "approved" ) );
            }

            //stdK4A = new CheckBox( "stdK4A", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4A() ) ) ); add( stdK4A );

            for ( String level : new String[]{"K4", "58", "912"} ) {
                for ( String standard : new String[]{"A", "B", "C", "D", "E", "F", "G"} ) {
                    add( new CheckBox( "standard" + level + standard ) );
                }
            }

            // wrap the existing files in a container so we can refresh it via ajax without wiping other form changes.
            // particularly significant, since if would wipe any files to be uploaded otherwise
            WebMarkupContainer fileContainer = new WebMarkupContainer( "file-markup-container" );
            fileContainer.setOutputMarkupId( true );
            add( fileContainer );
            fileContainer.add( new ExistingListView( "existing-files" ) );

            add( new AbstractFormValidator() {
                public FormComponent[] getDependentFormComponents() {
                    return new FormComponent[]{uploadPanel.getField()};
                }

                public void validate( Form form ) {
                    uploadPanel.getField().updateModel();
                    if ( uploadPanel.getUploadedFiles().size() + existingFiles.size() == 0 ) {
                        error( uploadPanel.getField(), "contribution.edit.validation.mustHaveFiles" );
                    }
                    List<FileUpload> newFiles = uploadPanel.getUploadedFiles();
                    for ( FileUpload fileUpload : newFiles ) {
                        if ( !ContributionFile.validateFileExtension( fileUpload.getClientFileName() ) ) {
                            // TODO: verify that everything is properly escaped
                            HashMap map = new HashMap();
                            map.put( "0", fileUpload.getClientFileName() );
                            map.put( "1", ContributionFile.getFiletypes( ContributionEditPanel.this ) );
                            error( uploadPanel.getField(), "contribution.edit.validation.fileType", map );
                            logger.warn( "Attempt to upload unaccepted file type for file: " + fileUpload.getClientFileName() );
                        }
                    }
                }
            } );

            add( new AbstractFormValidator() {
                public FormComponent[] getDependentFormComponents() {
                    return new FormComponent[]{simList.getFormComponent(), typeList.getFormComponent(), levelList.getFormComponent()};
                }

                public void validate( Form form ) {
                    if ( simManager.getSimulations().isEmpty() ) {
                        error( simList.getFormComponent(), "contribution.edit.validation.mustHaveSims" );
                    }
                    if ( typeManager.getValues().isEmpty() ) {
                        error( typeList.getFormComponent(), "contribution.edit.validation.mustHaveTypes" );
                    }
                    if ( levelManager.getValues().isEmpty() ) {
                        error( levelList.getFormComponent(), "contribution.edit.validation.mustHaveLevels" );
                    }
                }
            } );

        }

        @Override
        protected void onValidate() {
            super.onValidate();
            feedback.setVisible( feedback.anyMessage() );
        }

        @Override
        protected void onSubmit() {
            super.onSubmit();

            final int ids[] = new int[1];

            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {

                    // pull out values
                    int duration = ( (DurationItem) durationChoice.getModelObject() ).getDuration();
                    boolean answers = getModelObject().isAnswersIncluded();
                    Locale locale = localeChoice.getLocale() == null ? PhetWicketApplication.getDefaultLocale() : localeChoice.getLocale();

                    //Contribution contribution;
                    PhetUser user = (PhetUser) session.load( PhetUser.class, PhetSession.get().getUser().getId() );

                    // set up the contribution
                    if ( creating ) {
                        contribution.setDateCreated( new Date() );
                    } else {
                        contribution = (Contribution) session.merge( contribution ); // make it a persistent object so lazy collections are fetched as necessary
                    }

                    contribution.setDateUpdated( new Date() );

                    // set simple fields
                    contribution.setDuration( duration );
                    contribution.setPhetUser( user );
                    contribution.setLocale( locale );

                    int contribId = !creating ? contribution.getId() : (Integer) session.save( contribution );

                    /*---------------------------------------------------------------------------*
                    * sync simulations
                    *----------------------------------------------------------------------------*/

                    Set sims = contribution.getSimulations();
                    Set iterSims = new HashSet( sims );
                    Set selectedSims = new HashSet();

                    // load into persistence
                    for ( Simulation presim : simManager.getSimulations() ) {
                        Simulation sim = (Simulation) session.load( Simulation.class, presim.getId() );
                        selectedSims.add( presim );
                    }

                    for ( Object sim : selectedSims ) {
                        if ( !containsId( sims, sim ) ) {
                            logger.info( "adding sim " + ( (Simulation) sim ).getName() + " to contribution" );
                            contribution.addSimulation( (Simulation) session.load( Simulation.class, ( (Simulation) sim ).getId() ) );
                        }
                    }

                    for ( Object sim : iterSims ) {
                        if ( !containsId( selectedSims, sim ) ) {
                            logger.info( "removing sim " + ( (Simulation) sim ).getName() + " from contribution" );
                            contribution.removeSimulation( (Simulation) sim );
                        }
                    }

                    /*---------------------------------------------------------------------------*
                    * sync types
                    *----------------------------------------------------------------------------*/

                    Set types = contribution.getTypes();
                    Set iterTypes = new HashSet( types );

                    for ( Type type : typeManager.getValues() ) {
                        boolean exists = false;
                        for ( Object o : types ) {
                            if ( ( (ContributionType) o ).getType() == type ) {
                                exists = true;
                                break;
                            }
                        }
                        if ( exists ) {
                            continue;
                        }
                        logger.info( "adding type " + type + " to contribution" );
                        ContributionType ctype = new ContributionType();
                        ctype.setType( type );
                        contribution.addType( ctype );
                        ctype.setContribution( contribution );
                        session.save( ctype );
                    }

                    for ( Object o : iterTypes ) {
                        ContributionType ctype = (ContributionType) o;
                        if ( !typeManager.getValues().contains( ctype.getType() ) ) {
                            contribution.getTypes().remove( ctype );
                        }
                    }

                    /*---------------------------------------------------------------------------*
                    * sync levels
                    *----------------------------------------------------------------------------*/

                    Set levels = contribution.getLevels();
                    Set iterLevels = new HashSet( levels );

                    for ( Level level : levelManager.getValues() ) {
                        boolean exists = false;
                        for ( Object o : levels ) {
                            if ( ( (ContributionLevel) o ).getLevel() == level ) {
                                exists = true;
                                break;
                            }
                        }
                        if ( exists ) {
                            continue;
                        }
                        logger.info( "adding level " + level + " to contribution" );
                        ContributionLevel clevel = new ContributionLevel();
                        clevel.setLevel( level );
                        contribution.addLevel( clevel );
                        clevel.setContribution( contribution );
                        session.save( clevel );
                    }

                    for ( Object o : iterLevels ) {
                        ContributionLevel clevel = (ContributionLevel) o;
                        if ( !levelManager.getValues().contains( clevel.getLevel() ) ) {
                            contribution.getLevels().remove( clevel );
                        }
                    }

                    /*---------------------------------------------------------------------------*
                    * sync subjects
                    *----------------------------------------------------------------------------*/

                    Set subjects = contribution.getSubjects();
                    Set iterSubjects = new HashSet( subjects );

                    for ( Subject subject : subjectManager.getValues() ) {
                        boolean exists = false;
                        for ( Object o : subjects ) {
                            if ( ( (ContributionSubject) o ).getSubject() == subject ) {
                                exists = true;
                                break;
                            }
                        }
                        if ( exists ) {
                            continue;
                        }
                        logger.info( "adding subject " + subject + " to contribution" );
                        ContributionSubject csubject = new ContributionSubject();
                        csubject.setSubject( subject );
                        contribution.addSubject( csubject );
                        csubject.setContribution( contribution );
                        session.save( csubject );
                    }

                    for ( Object o : iterSubjects ) {
                        ContributionSubject csubject = (ContributionSubject) o;
                        if ( !subjectManager.getValues().contains( csubject.getSubject() ) ) {
                            contribution.getSubjects().remove( csubject );
                        }
                    }

                    /*---------------------------------------------------------------------------*
                    * file removal
                    *----------------------------------------------------------------------------*/

                    for ( Object o : filesToRemove ) {
                        ContributionFile x = (ContributionFile) o;
                        ContributionFile cfile = (ContributionFile) session.load( ContributionFile.class, x.getId() );
                        contribution.getFiles().remove( cfile );
                        session.delete( cfile );
                    }

                    // sanity check
                    contribution.setId( contribId );
                    ids[0] = contribId;

                    /*---------------------------------------------------------------------------*
                    * file uploads
                    *----------------------------------------------------------------------------*/

                    for ( FileUpload upload : uploadPanel.getUploadedFiles() ) {
                        ContributionFile cfile = new ContributionFile();
                        cfile.setFilename( upload.getClientFileName() );
                        contribution.addFile( cfile );
                        cfile.setSize( (int) upload.getSize() );

                        // remember to contribution.addFile before this, otherwise it will fail
                        //cfile.setLocation( cfile.getFileLocation().getAbsolutePath() ); TODO: remove this line after confirming transferdata works
                        File file = cfile.getFileLocation();
                        file.getParentFile().mkdirs();
                        try {
                            upload.writeTo( file );
                        }
                        catch( IOException e ) {
                            e.printStackTrace();
                            logger.warn( "upload failed", e );
                            return false;
                        }
                        session.save( cfile );
                    }

                    if ( !creating ) {
                        session.update( contribution );
                    }

                    return true;
                }
            } );

            if ( success ) {
                //PageParameters params = new PageParameters();
                //params.add( "contribution", Integer.toString( ids[0] ) );
                //setResponsePage( new RedirectPage( new ContributionSuccessPage( params ) ) );
                setResponsePage( new RedirectPage( ContributionSuccessPage.getLinker( ids[0] ).getRawUrl( context, getPhetCycle() ) ) );

                // should redirect us. not the pretty way, however it passes in all of the correct page parameters
                // and reduces the dependencies
//                ContributionBrowsePage.getLinker().getLink( "id", context, getPhetCycle() ).onClick();
            }

        }

        /**
         * Should only be applied to containsId( Collection<IntId> group, IntId element )
         *
         * @param group
         * @param element
         * @return
         */
        private boolean containsId( Collection group, Object element ) {
            int elemId = ( (IntId) element ).getId();

            for ( Object ob : group ) {
                if ( ( (IntId) ob ).getId() == elemId ) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void delegateSubmit( IFormSubmittingComponent submittingComponent ) {
            // we must override the delegate submit because we will have child ajax buttons to handle adding / removing
            // things like the simulations. they should NOT trigger a full form submission.

            // TODO: consider changing, (nested forms partially used now)
            // yes this is ugly, maybe nested forms could be used?

            logger.debug( "submit delegation from: " + submittingComponent );

            if ( submittingComponent == null ) {
                // submit like normal
                super.delegateSubmit( submittingComponent );
            }
            else {
                submittingComponent.onSubmit();
            }
        }

        private class ExistingListView extends ListView {
            public ExistingListView( String id ) {
                super( id, existingFiles );
            }

            protected void populateItem( ListItem item ) {
                final ContributionFile cfile = (ContributionFile) item.getModel().getObject();
                item.add( new Label( "file-name", cfile.getFilename() ) );
                item.add( new AjaxFallbackLink( "remove-link" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        existingFiles.remove( cfile );
                        filesToRemove.add( cfile );

                        // only update this list view, so we don't wipe new added files to be uploaded
                        target.addComponent( ExistingListView.this.getParent() );
                    }
                } );
            }
        }
    }

    public class DurationDropDownChoice extends DropDownChoice {
        public DurationDropDownChoice( String id, int initialDuration ) {
            super( id, new Model( new DurationItem( initialDuration ) ), DurationItem.getAllItems(), new IChoiceRenderer() {
                public Object getDisplayValue( Object object ) {
                    if ( object instanceof DurationItem ) {
                        return ( (DurationItem) object ).getDisplayValue();
                    }
                    else {
                        throw new RuntimeException( "Not an DurationItem" );
                    }
                }

                public String getIdValue( Object object, int index ) {
                    if ( object instanceof DurationItem ) {
                        return String.valueOf( ( (DurationItem) object ).getId() );
                    }
                    else {
                        throw new RuntimeException( "Not an DurationItem" );
                    }
                }
            } );
        }
    }

    private static class DurationItem implements Serializable {

        private int duration;

        private DurationItem( int duration ) {
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }

        public static List getAllItems() {
            // TODO: build into contribution code so we can keep values in one place
            return Arrays.asList( new DurationItem( 0 ), new DurationItem( 30 ), new DurationItem( 60 ), new DurationItem( 90 ), new DurationItem( 120 ) );
        }

        public String getDisplayValue() {
            // TODO: localize
            if ( duration == 0 ) {
                return "NA";
            }
            return String.valueOf( duration ) + " minutes";
        }

        public Component getDisplayComponent( String id ) {
            return new Label( id, getDisplayValue() );
        }

        public int getId() {
            return duration;
        }

    }

    private Contribution getContribution() {
        return contribution;
    }
}
