package edu.colorado.phet.website.admin;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.data.Category;
import edu.colorado.phet.website.data.util.CategoryChangeHandler;
import edu.colorado.phet.website.util.hibernate.HibernateTask;
import edu.colorado.phet.website.util.hibernate.HibernateUtils;
import edu.colorado.phet.website.util.StringUtils;

/**
 * Shows a list of categories to edit, along with a few other options
 */
public class AdminCategoriesPage extends AdminPage {
    public AdminCategoriesPage( PageParameters parameters ) {
        super( parameters );

        final List<Category> categories = new LinkedList<Category>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                Category root = Category.getRootCategory( session );
                addCategory( root );
                return true;
            }

            private void addCategory( Category category ) {
                //if ( !category.isRoot() ) {
                categories.add( category );
                //}
                for ( Object o : category.getSubcategories() ) {
                    addCategory( (Category) o );
                }
            }
        } );

        add( new ListView<Category>( "categories", categories ) {
            protected void populateItem( ListItem<Category> item ) {
                final Category category = item.getModelObject();

                Component titleComponent;
                if ( category.isRoot() ) {
                    titleComponent = new Label( "title", "Root" );
                }
                else {
                    titleComponent = new LocalizedText( "title", category.getNavLocation( getNavMenu() ).getLocalizationKey() );
                }

                Link categoryLink = new Link( "category-link" ) {
                    public void onClick() {
                        PageParameters params = new PageParameters();
                        params.put( AdminCategoryPage.CATEGORY_ID, category.getId() );
                        setResponsePage( AdminCategoryPage.class, params );
                    }
                };

                categoryLink.add( titleComponent );
                item.add( categoryLink );

                String spaces = "";
                int depth = category.getDepth() * 4;
                for ( int i = 0; i < depth; i++ ) {
                    spaces += "&nbsp;";
                }
                item.add( new RawLabel( "spacer", spaces ) );
            }
        } );

        add( new AddCategoryForm( "new-category-form" ) );
    }

    private class AddCategoryForm extends Form {

        private List<Category> allCategories = new LinkedList<Category>();
        private TextField nameText;
        private TextField keyText;
        private CategoryDropDownChoice dropDownChoice;

        public AddCategoryForm( String id ) {
            super( id );

            add( nameText = new TextField<String>( "name", new Model<String>( "" ) ) );
            add( keyText = new TextField<String>( "key", new Model<String>( "" ) ) );

            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Category root = Category.getRootCategory( session );
                    addCategory( root );

                    return true;
                }

                private void addCategory( Category category ) {
                    allCategories.add( category );
                    for ( Object o : category.getSubcategories() ) {
                        addCategory( (Category) o );
                    }
                }
            } );

            dropDownChoice = new CategoryDropDownChoice( "all-categories", allCategories );
            add( dropDownChoice );
        }

        @Override
        protected void onSubmit() {
            final String name = nameText.getModelObject().toString();
            final String key = keyText.getModelObject().toString();
            final String navKey = "nav." + key;

            final int catId = Integer.valueOf( dropDownChoice.getModelValue() );
            if ( name.isEmpty() || key.isEmpty() || catId == 0 ) {
                return;
            }
            final Category[] cats = new Category[1];

            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    List matchingNavStrings = session.createQuery( "select ts from TranslatedString as ts where ts.key = :key" )
                            .setString( "key", navKey ).list();
                    if ( !matchingNavStrings.isEmpty() ) {
                        // if the nav key already exists, we don't want to create a category by that name and obliterate it
                        return false;
                    }

                    Category category = new Category();
                    category.setAuto( false );
                    category.setRoot( false );
                    Category parent = (Category) session.load( Category.class, catId );
                    category.setParent( parent );
                    parent.getSubcategories().add( category );
                    category.setName( key );
                    session.save( category );
                    session.update( parent );
                    cats[0] = category;
                    return true;
                }
            } );
            if ( success ) {
                StringUtils.setEnglishString( getHibernateSession(), navKey, name );

                CategoryChangeHandler.notifyAdded( cats[0] );

                // redirect to the new category so we can edit it
                PageParameters params = new PageParameters();
                params.put( AdminCategoryPage.CATEGORY_ID, cats[0].getId() );
                setResponsePage( AdminCategoryPage.class, params );
            }
        }

        private class CategoryDropDownChoice extends DropDownChoice {
            public CategoryDropDownChoice( String id, List<Category> allCategories ) {
                super( id, new Model(), allCategories, new IChoiceRenderer() {
                    public Object getDisplayValue( Object object ) {
                        Category cat = (Category) object;
                        if ( cat.isRoot() ) {
                            return "Root";
                        }
                        else {
                            return PhetWicketApplication.get().getResourceSettings().getLocalizer().getString( cat.getNavLocation( getNavMenu() ).getLocalizationKey(), AdminCategoriesPage.this );
                        }
                    }

                    public String getIdValue( Object object, int index ) {
                        return String.valueOf( ( (Category) object ).getId() );
                    }
                } );
            }
        }
    }
}
