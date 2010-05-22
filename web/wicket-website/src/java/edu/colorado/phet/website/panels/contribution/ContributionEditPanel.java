package edu.colorado.phet.website.panels.contribution;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.authentication.AuthenticatedPage;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.content.contribution.ContributionPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.contribution.*;
import edu.colorado.phet.website.data.util.IntId;
import edu.colorado.phet.website.panels.MultipleFileUploadPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.panels.lists.*;
import edu.colorado.phet.website.translation.LocaleDropDownChoice;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class ContributionEditPanel extends PhetPanel {

    private final Contribution contribution;
    private boolean creating;

    private PageContext context;

    private static final Logger logger = Logger.getLogger( ContributionEditPanel.class.getName() );

    private SimSetManager simManager;
    private TypeSetManager typeManager;
    private LevelSetManager levelManager;
    private SubjectSetManager subjectManager;

    private List existingFiles;
    private List filesToRemove;

    public ContributionEditPanel( String id, PageContext context, Contribution preContribution ) {
        super( id, context );

        AuthenticatedPage.checkSignedIn();

        this.context = context;

        add( HeaderContributor.forCss( CSS.CONTRIBUTION_MAIN ) );

        contribution = preContribution;

        creating = contribution.getId() == 0;

        existingFiles = new LinkedList();
        filesToRemove = new LinkedList();

        final PhetUser currentUser = PhetSession.get().getUser();

        boolean success = true;

        if ( !creating ) {
            success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
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

        add( new FeedbackPanel( "feedback" ) );
    }

    public final class ContributionForm extends Form {

        private RequiredTextField authorsText;
        private RequiredTextField organizationText;
        private RequiredTextField emailText;
        private RequiredTextField titleText;
        private RequiredTextField keywordsText;

        private MultipleFileUploadPanel uploadPanel;

        private TextArea descriptionText;

        private DurationDropDownChoice durationChoice;

        private CheckBox answersCheck;

        private CheckBox stdK4A;
        private CheckBox stdK4B;
        private CheckBox stdK4C;
        private CheckBox stdK4D;
        private CheckBox stdK4E;
        private CheckBox stdK4F;
        private CheckBox stdK4G;
        private CheckBox std58A;
        private CheckBox std58B;
        private CheckBox std58C;
        private CheckBox std58D;
        private CheckBox std58E;
        private CheckBox std58F;
        private CheckBox std58G;
        private CheckBox std912A;
        private CheckBox std912B;
        private CheckBox std912C;
        private CheckBox std912D;
        private CheckBox std912E;
        private CheckBox std912F;
        private CheckBox std912G;

        private LocaleDropDownChoice localeChoice;

        public ContributionForm( String id ) {
            super( id );

            // for ajax fallback link
            setOutputMarkupId( true );

            setMultiPart( true );
            setMaxSize( Bytes.megabytes( 64 ) );

            authorsText = new RequiredTextField( "contribution.edit.authors", new Model( creating ? "" : contribution.getAuthors() ) );
            add( authorsText );

            organizationText = new RequiredTextField( "contribution.edit.organization", new Model( creating ? "" : contribution.getAuthorOrganization() ) );
            add( organizationText );

            emailText = new RequiredTextField( "contribution.edit.email", new Model( creating ? "" : contribution.getContactEmail() ) );
            emailText.add( EmailAddressValidator.getInstance() );
            add( emailText );

            titleText = new RequiredTextField( "contribution.edit.title", new Model( creating ? "" : contribution.getTitle() ) );
            add( titleText );

            keywordsText = new RequiredTextField( "contribution.edit.keywords", new Model( creating ? "" : contribution.getKeywords() ) );
            add( keywordsText );

            descriptionText = new TextArea( "contribution.edit.description", new Model( creating ? "" : contribution.getDescription() ) );
            add( descriptionText );

            uploadPanel = new MultipleFileUploadPanel( "file-upload", context );
            add( uploadPanel );

            final SortedList<SimOrderItem> simList = simManager.getComponent( "simulations", context );
            add( simList );
            final SortedList<EnumSetManager.ListItem<Type>> typeList = typeManager.getComponent( "types", context );
            add( typeList );
            final SortedList<EnumSetManager.ListItem<Level>> levelList = levelManager.getComponent( "levels", context );
            add( levelList );

            add( subjectManager.getComponent( "subjects", context ) );

            durationChoice = new DurationDropDownChoice( "duration", creating ? 0 : contribution.getDuration() );
            add( durationChoice );

            answersCheck = new CheckBox( "answers", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isAnswersIncluded() ) ) );
            add( answersCheck );

            localeChoice = new LocaleDropDownChoice( "locale", context );
            add( localeChoice );

            //stdK4A = new CheckBox( "stdK4A", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4A() ) ) ); add( stdK4A );

            stdK4A = new CheckBox( "stdK4A", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4A() ) ) );
            add( stdK4A );
            stdK4B = new CheckBox( "stdK4B", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4B() ) ) );
            add( stdK4B );
            stdK4C = new CheckBox( "stdK4C", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4C() ) ) );
            add( stdK4C );
            stdK4D = new CheckBox( "stdK4D", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4D() ) ) );
            add( stdK4D );
            stdK4E = new CheckBox( "stdK4E", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4E() ) ) );
            add( stdK4E );
            stdK4F = new CheckBox( "stdK4F", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4F() ) ) );
            add( stdK4F );
            stdK4G = new CheckBox( "stdK4G", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4G() ) ) );
            add( stdK4G );
            std58A = new CheckBox( "std58A", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard58A() ) ) );
            add( std58A );
            std58B = new CheckBox( "std58B", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard58B() ) ) );
            add( std58B );
            std58C = new CheckBox( "std58C", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard58C() ) ) );
            add( std58C );
            std58D = new CheckBox( "std58D", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard58D() ) ) );
            add( std58D );
            std58E = new CheckBox( "std58E", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard58E() ) ) );
            add( std58E );
            std58F = new CheckBox( "std58F", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard58F() ) ) );
            add( std58F );
            std58G = new CheckBox( "std58G", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard58G() ) ) );
            add( std58G );
            std912A = new CheckBox( "std912A", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard912A() ) ) );
            add( std912A );
            std912B = new CheckBox( "std912B", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard912B() ) ) );
            add( std912B );
            std912C = new CheckBox( "std912C", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard912C() ) ) );
            add( std912C );
            std912D = new CheckBox( "std912D", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard912D() ) ) );
            add( std912D );
            std912E = new CheckBox( "std912E", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard912E() ) ) );
            add( std912E );
            std912F = new CheckBox( "std912F", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard912F() ) ) );
            add( std912F );
            std912G = new CheckBox( "std912G", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard912G() ) ) );
            add( std912G );

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
                            error( uploadPanel.getField(), "contribution.edit.validation.fileType", map );
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
        protected void onSubmit() {
            super.onSubmit();

            final int ids[] = new int[1];

            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {

                    // pull out values
                    String authors = authorsText.getModelObjectAsString();
                    String org = organizationText.getModelObjectAsString();
                    String email = emailText.getModelObjectAsString();
                    String title = titleText.getModelObjectAsString();
                    String keywords = keywordsText.getModelObjectAsString();
                    String description = descriptionText.getModelObjectAsString();
                    int duration = ( (DurationItem) durationChoice.getModelObject() ).getDuration();
                    boolean answers = (Boolean) answersCheck.getModelObject();
                    Locale locale = localeChoice.getLocale() == null ? PhetWicketApplication.getDefaultLocale() : localeChoice.getLocale();

                    Contribution contribution;
                    PhetUser user = (PhetUser) session.load( PhetUser.class, PhetSession.get().getUser().getId() );

                    // set up the contribution
                    if ( creating ) {
                        contribution = new Contribution();
                        contribution.setDateCreated( new Date() );
                        contribution.setApproved( false );
                        contribution.setFromPhet( user.isTeamMember() );
                        contribution.setGoldStar( false );
                        contribution.setOldId( 0 );
                    }
                    else {
                        contribution = (Contribution) session.load( Contribution.class, getContribution().getId() );
                    }

                    contribution.setDateUpdated( new Date() );

                    // set simple fields
                    contribution.setAuthors( authors );
                    contribution.setAuthorOrganization( org );
                    contribution.setContactEmail( email );
                    contribution.setTitle( title );
                    contribution.setKeywords( keywords );
                    contribution.setDescription( description );
                    contribution.setDuration( duration );
                    contribution.setAnswersIncluded( answers );
                    contribution.setStandardK4A( (Boolean) stdK4A.getModelObject() );
                    contribution.setStandardK4B( (Boolean) stdK4B.getModelObject() );
                    contribution.setStandardK4C( (Boolean) stdK4C.getModelObject() );
                    contribution.setStandardK4D( (Boolean) stdK4D.getModelObject() );
                    contribution.setStandardK4E( (Boolean) stdK4E.getModelObject() );
                    contribution.setStandardK4F( (Boolean) stdK4F.getModelObject() );
                    contribution.setStandardK4G( (Boolean) stdK4G.getModelObject() );
                    contribution.setStandard58A( (Boolean) std58A.getModelObject() );
                    contribution.setStandard58B( (Boolean) std58B.getModelObject() );
                    contribution.setStandard58C( (Boolean) std58C.getModelObject() );
                    contribution.setStandard58D( (Boolean) std58D.getModelObject() );
                    contribution.setStandard58E( (Boolean) std58E.getModelObject() );
                    contribution.setStandard58F( (Boolean) std58F.getModelObject() );
                    contribution.setStandard58G( (Boolean) std58G.getModelObject() );
                    contribution.setStandard912A( (Boolean) std912A.getModelObject() );
                    contribution.setStandard912B( (Boolean) std912B.getModelObject() );
                    contribution.setStandard912C( (Boolean) std912C.getModelObject() );
                    contribution.setStandard912D( (Boolean) std912D.getModelObject() );
                    contribution.setStandard912E( (Boolean) std912E.getModelObject() );
                    contribution.setStandard912F( (Boolean) std912F.getModelObject() );
                    contribution.setStandard912G( (Boolean) std912G.getModelObject() );
                    contribution.setPhetUser( user );
                    contribution.setLocale( locale );

                    int contribId = !creating ? contribution.getId() : (Integer) session.save( contribution );

                    //----------------------------------------------------------------------------
                    // sync simulations
                    //----------------------------------------------------------------------------

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

                    //----------------------------------------------------------------------------
                    // sync types
                    //----------------------------------------------------------------------------

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

                    //----------------------------------------------------------------------------
                    // sync levels
                    //----------------------------------------------------------------------------

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

                    //----------------------------------------------------------------------------
                    // sync subjects
                    //----------------------------------------------------------------------------

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

                    //----------------------------------------------------------------------------
                    // file removal
                    //----------------------------------------------------------------------------

                    for ( Object o : filesToRemove ) {
                        ContributionFile x = (ContributionFile) o;
                        ContributionFile cfile = (ContributionFile) session.load( ContributionFile.class, x.getId() );
                        contribution.getFiles().remove( cfile );
                        session.delete( cfile );
                    }

                    // sanity check
                    contribution.setId( contribId );
                    ids[0] = contribId;

                    //----------------------------------------------------------------------------
                    // file uploads
                    //----------------------------------------------------------------------------

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
                setResponsePage( new RedirectPage( ContributionPage.getLinker( ids[0] ).getRawUrl( context, getPhetCycle() ) ) );
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
