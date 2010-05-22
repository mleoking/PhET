package edu.colorado.phet.website.translation;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.website.components.StringTextField;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class TranslationUserPanel extends PhetPanel {

    private int translationId;

    final List<PhetUser> users;

    public TranslationUserPanel( String id, PageContext context, final int translationId ) {
        super( id, context );

        this.translationId = translationId;

        users = new LinkedList<PhetUser>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                Translation translation = (Translation) session.load( Translation.class, translationId );
                for ( Object o : translation.getAuthorizedUsers() ) {
                    PhetUser user = (PhetUser) o;
                    users.add( user );
                }
                return true;
            }
        } );

        add( new ListView( "user-list", users ) {
            protected void populateItem( ListItem item ) {
                final PhetUser user = (PhetUser) item.getModel().getObject();
                item.add( new Label( "user-email", user.getEmail() ) );
                item.add( new AjaxLink( "remove-user-link" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        if ( users.size() > 1 ) {
                            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                                public boolean run( Session session ) {
                                    Translation translation = (Translation) session.load( Translation.class, translationId );
                                    PhetUser curUser = (PhetUser) session.load( PhetUser.class, user.getId() );
                                    translation.removeUser( curUser );
                                    return true;
                                }
                            } );
                            if ( success ) {
                                users.remove( user );
                            }
                        }
                        target.addComponent( TranslationUserPanel.this );
                    }
                } );
            }
        } );

        add( new AddUserForm( "add-user-form" ) );

        setOutputMarkupId( true );
    }

    private class AddUserForm extends Form {

        private TextField userField;
        private Label errorLabel;

        public AddUserForm( String id ) {
            super( id );

            errorLabel = new Label( "error-text", "" );
            add( errorLabel );

            userField = new StringTextField( "user", new Model( "" ) );
            userField.setEscapeModelStrings( false );
            add( userField );

            add( new AjaxButton( "submit-button", this ) {
                protected void onSubmit( AjaxRequestTarget target, Form form ) {
                    final PhetUser[] phetUsers = new PhetUser[]{null};

                    final String email = userField.getModelObject().toString();

                    HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                        public boolean run( Session session ) {
                            Translation translation = (Translation) session.load( Translation.class, translationId );
                            PhetUser user = (PhetUser) session.createQuery( "select u from PhetUser as u where u.email = :email" ).setString( "email", email ).uniqueResult();
                            if ( translation == null || user == null ) {
                                return false;
                            }

                            // make sure the user doesn't already have access
                            for ( Object o : translation.getAuthorizedUsers() ) {
                                if ( ( (PhetUser) o ).getId() == user.getId() ) {
                                    return false;
                                }
                            }

                            // user ok, so add them
                            phetUsers[0] = user;

                            translation.addUser( user );
                            session.update( translation );
                            session.update( user );

                            return true;
                        }
                    } );

                    if ( phetUsers[0] != null ) {
                        PhetUser user = phetUsers[0];
                        users.add( user );
                        errorLabel.setDefaultModelObject( "" );
                    }
                    else {
                        errorLabel.setDefaultModelObject( "Error adding user!" );
                    }

                    target.addComponent( TranslationUserPanel.this );
                }
            } );
        }

    }
}
