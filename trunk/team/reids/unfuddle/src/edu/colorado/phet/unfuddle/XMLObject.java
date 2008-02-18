package edu.colorado.phet.unfuddle;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by: Sam
 * Feb 17, 2008 at 4:44:25 PM
 */
public class XMLObject {
    private Node node;

    public XMLObject( Node node ) {
        this.node = node;
    }

    public XMLObject getNode( String nodeName ) {
        return getNode( 0, nodeName );
    }

    public XMLObject getNode( int index, String nodeName ) {
        NodeList ch = node.getChildNodes();
        int count = 0;
        for ( int i = 0; i < ch.getLength(); i++ ) {
            final Node node = ch.item( i );
            if ( node.getNodeName().equals( nodeName ) ) {
                if ( count == index ) {
                    return new XMLObject( node );
                }
                else {
                    count++;
                }
            }
        }
        return null;
    }

    public String getTextContent( String child ) {
        return getNode( child ).node.getTextContent();
    }

    public String toString() {
        return "node=" + node.getNodeName();
    }

    public int getNodeCount( String child ) {
        NodeList ch = node.getChildNodes();
        int count = 0;
        for ( int i = 0; i < ch.getLength(); i++ ) {
            final Node node = ch.item( i );
            if ( node.getNodeName().equals( child ) ) {
                count++;
            }
        }
        return count;
    }

    public Node getNode() {
        return node;
    }

    public int getListCount( String listName ) {
        return getListCount( listName, getSingular( listName ) );
    }

    public int getListCount( String listName, String elementName ) {
        return getNode( listName ).getNodeCount( elementName );
    }

    private String getSingular( String listName ) {
        return listName.substring( 0, listName.length() - 1 );
    }

    public Node getListElement( String s, int i ) {
        return getListElement( s, getSingular( s ), i );
    }

    public Node getListElement( String s, String elementName, int i ) {
        return getNode( s ).getNode( i, elementName ).getNode();
    }
}
