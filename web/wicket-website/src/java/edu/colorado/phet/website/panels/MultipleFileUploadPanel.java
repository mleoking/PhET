package edu.colorado.phet.website.panels;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.MultiFileUploadField;
import org.apache.wicket.model.Model;

import edu.colorado.phet.website.util.PageContext;

public class MultipleFileUploadPanel extends PhetPanel {

    private MultiFileUploadField field;

    private List<File> files;

    public MultipleFileUploadPanel( String id, PageContext context ) {
        super( id, context );

        files = new LinkedList<File>();

        field = new MultiFileUploadField( "uploader", new Model( new LinkedList() ) );
        ( (MarkupContainer) field.get( "container" ) ).get( "caption" ).setVisible( false );
        add( field );
    }

    public List<FileUpload> getUploadedFiles() {
        LinkedList<FileUpload> ret = new LinkedList<FileUpload>();
        Iterator iter = ( (Collection) field.getModelObject() ).iterator();
        while ( iter.hasNext() ) {
            FileUpload fup = (FileUpload) iter.next();
            ret.add( fup );
        }
        return ret;
    }
}
