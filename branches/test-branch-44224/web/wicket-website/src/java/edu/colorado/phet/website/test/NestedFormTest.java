package edu.colorado.phet.website.test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.IClusterable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.*;
import org.apache.wicket.validation.validator.StringValidator;

public class NestedFormTest extends WebPage {

    private String outerStatus = "Not submitted yet";

    private static final List<String> strings = Arrays.asList( "Apple", "Pear", "Orange" );

    private static final Logger logger = Logger.getLogger( NestedFormTest.class.getName() );

    public NestedFormTest() {

        add( new Label( "outer-status", new PropertyModel<String>( this, "outerStatus" ) ) );

        add( new OuterForm( "outer-form" ) );

    }

    public class OuterForm extends Form<OuterFormModel> {
        public OuterForm( String id ) {
            super( id );

            CompoundPropertyModel<OuterFormModel> model = new CompoundPropertyModel<OuterFormModel>( new OuterFormModel() );

            setModel( model );

            add( new TextField<String>( "name" ).add( StringValidator.maximumLength( 3 ) ) );

            add( new InnerForm( "inner-form", model ) );
        }

        @Override
        protected void onSubmit() {
            outerStatus = "Submitted. Name: " + getModelObject().getName() + ", tags: ";
            for ( String s : getModelObject().getTags() ) {
                outerStatus += s + " ";
            }
        }

    }

    public class InnerForm extends Form<OuterFormModel> {

        private String selected;

        public InnerForm( String id, IModel<OuterFormModel> outerModel ) {
            super( id, outerModel );

            IModel<List<String>> choices = new AbstractReadOnlyModel<List<String>>() {
                @Override
                public List<String> getObject() {
                    return strings;
                }
            };

            add( new DropDownChoice<String>( "taglist", new PropertyModel<String>( this, "selected" ), choices ) );

            add( new AjaxButton( "add", InnerForm.this ) {
                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form ) {
                    target.addComponent( InnerForm.this );

                    if ( selected == null ) {
                        logger.warn( "nothing selected" );
                    }
                    else {
                        logger.info( "selecting " + selected );

                        InnerForm.this.getModelObject().getTags().add( selected );
                    }
                }
            } );

            IModel<LinkedList<String>> tags = new Model<LinkedList<String>>( outerModel.getObject().getTags() );

            add( new ListView<String>( "added", tags ) {
                @Override
                protected void populateItem( ListItem<String> item ) {
                    item.add( new Label( "name", item.getModel() ) );
                }
            } );
        }

    }

    public static class OuterFormModel implements IClusterable {
        private LinkedList<String> tags = new LinkedList<String>();
        private String name = "Default Name";

        public LinkedList<String> getTags() {
            return tags;
        }

        public void setTags( LinkedList<String> tags ) {
            this.tags = tags;
        }

        public String getName() {
            return name;
        }

        public void setName( String name ) {
            this.name = name;
        }
    }

}
