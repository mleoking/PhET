package edu.colorado.phet.website.authentication.panels;

import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.value.ValueMap;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.authentication.PhetSession;

public class EditProfilePanel extends PhetPanel {

    private Model errorModel;

    private static final String ERROR_SEPARATOR = "<br/>";

    private static Logger logger = Logger.getLogger( EditProfilePanel.class.getName() );

    public EditProfilePanel( String id, PageContext context ) {
        super( id, context );

        errorModel = new Model( "" );
        Label errorLabel = new Label( "profile-errors", errorModel );
        add( errorLabel );
        errorLabel.setEscapeModelStrings( false );

        add( new EditProfileForm( "edit-profile-form" ) );
    }

    private final class EditProfileForm extends Form {

        private TextField name;
        private TextField organization;
        private DropDownChoice description;
        private TextField jobTitle;

        private TextField address1;
        private TextField address2;
        private TextField city;
        private TextField state;
        private TextField country;
        private TextField zipcode;

        private TextField phone1;
        private TextField phone2;
        private TextField fax;

        private CheckBox receiveEmail;

        private final ValueMap properties = new ValueMap();

        public EditProfileForm( String id ) {
            super( id );

            PhetUser user = PhetSession.get().getUser();

            properties.add( "name", user.getName() );
            properties.add( "organization", user.getOrganization() );
            properties.add( "description", user.getDescription() );
            properties.add( "jobTitle", user.getJobTitle() );
            properties.add( "address1", user.getAddress1() );
            properties.add( "address2", user.getAddress2() );
            properties.add( "city", user.getCity() );
            properties.add( "state", user.getState() );
            properties.add( "country", user.getCountry() );
            properties.add( "zipcode", user.getZipcode() );
            properties.add( "phone1", user.getPhone1() );
            properties.add( "phone2", user.getPhone2() );
            properties.add( "fax", user.getFax() );

            add( name = new TextField( "name", new PropertyModel( properties, "name" ) ) );
            add( organization = new TextField( "organization", new PropertyModel( properties, "organization" ) ) );
            add( description = new DropDownChoice( "description", new PropertyModel( properties, "description" ), PhetUser.getDescriptionOptions() ) );
            add( jobTitle = new TextField( "jobTitle", new PropertyModel( properties, "jobTitle" ) ) );
            add( address1 = new TextField( "address1", new PropertyModel( properties, "address1" ) ) );
            add( address2 = new TextField( "address2", new PropertyModel( properties, "address2" ) ) );
            add( city = new TextField( "city", new PropertyModel( properties, "city" ) ) );
            add( state = new TextField( "state", new PropertyModel( properties, "state" ) ) );
            add( country = new TextField( "country", new PropertyModel( properties, "country" ) ) );
            add( zipcode = new TextField( "zipcode", new PropertyModel( properties, "zipcode" ) ) );
            add( phone1 = new TextField( "phone1", new PropertyModel( properties, "phone1" ) ) );
            add( phone2 = new TextField( "phone2", new PropertyModel( properties, "phone2" ) ) );
            add( fax = new TextField( "fax", new PropertyModel( properties, "fax" ) ) );
            add( receiveEmail = new CheckBox( "receiveEmail", new Model( new Boolean( user.isReceiveEmail() ) ) ) );


        }

        @Override
        protected void onSubmit() {
            boolean error = false;
            String errorString = "";
            String err = null;

            String nom = name.getModelObjectAsString();
            String desc = description.getModelObjectAsString();

            if ( nom == null || nom.length() == 0 ) {
                error = true;
                errorString += ERROR_SEPARATOR + getPhetLocalizer().getString( "validation.user.user", this, "Please fill in the name field" );
            }

            if ( desc == null || desc.length() == 0 ) {
                error = true;
                errorString += ERROR_SEPARATOR + getPhetLocalizer().getString( "validation.user.description", this, "Please pick a description" );
            }

            if ( !error ) {
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        PhetUser user = (PhetUser) session.load( PhetUser.class, PhetSession.get().getUser().getId() );
                        user.setName( name.getModelObjectAsString() );
                        user.setOrganization( organization.getModelObjectAsString() );
                        user.setDescription( description.getModelObjectAsString() );
                        user.setJobTitle( jobTitle.getModelObjectAsString() );
                        user.setAddress1( address1.getModelObjectAsString() );
                        user.setAddress2( address2.getModelObjectAsString() );
                        user.setCity( city.getModelObjectAsString() );
                        user.setState( state.getModelObjectAsString() );
                        user.setCountry( country.getModelObjectAsString() );
                        user.setZipcode( zipcode.getModelObjectAsString() );
                        user.setPhone1( phone1.getModelObjectAsString() );
                        user.setPhone2( phone2.getModelObjectAsString() );
                        user.setFax( fax.getModelObjectAsString() );
                        user.setReceiveEmail( (Boolean) receiveEmail.getModelObject() );
                        session.update( user );
                        return true;
                    }
                } );
                if ( success ) {
                    // synchronize the user data for the session instance
                    PhetUser user = PhetSession.get().getUser();
                    user.setName( name.getModelObjectAsString() );
                    user.setOrganization( organization.getModelObjectAsString() );
                    user.setDescription( description.getModelObjectAsString() );
                    user.setJobTitle( jobTitle.getModelObjectAsString() );
                    user.setAddress1( address1.getModelObjectAsString() );
                    user.setAddress2( address2.getModelObjectAsString() );
                    user.setCity( city.getModelObjectAsString() );
                    user.setState( state.getModelObjectAsString() );
                    user.setCountry( country.getModelObjectAsString() );
                    user.setZipcode( zipcode.getModelObjectAsString() );
                    user.setPhone1( phone1.getModelObjectAsString() );
                    user.setPhone2( phone2.getModelObjectAsString() );
                    user.setFax( fax.getModelObjectAsString() );
                    user.setReceiveEmail( (Boolean) receiveEmail.getModelObject() );
                }
                error = !success;
            }
            if ( error ) {
                logger.error( "Error editing profile" );
                logger.error( "Reason: " + errorString );
                errorString = getPhetLocalizer().getString( "validation.user.problems", this, "Please fix the following problems with the form:" ) + "<br/>" + errorString;
                errorModel.setObject( errorString );
            }
        }
    }
}
