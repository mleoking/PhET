package edu.colorado.phet.unfuddle;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.filters.StringInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Created by: Sam
 * Feb 17, 2008 at 4:44:25 PM
 */
public class XMLObject {
    private Node node;

    public XMLObject( Node node ) {
        this.node = node;
    }

    public XMLObject( String xml ) throws IOException, SAXException, ParserConfigurationException {
        this( parseXML( xml ) );
    }

    private static Node parseXML( String xml ) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document s = documentBuilder.parse( new StringInputStream( xml ) );
        return s.getDocumentElement();
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
        //this might be the correct 1.4 implementation
//                if ( getNode( child ) == null || getNode( child ).node == null || getNode( child ).node.getFirstChild() == null ) {
//            return "";
//        }
//        else {
//            return getNode( child ).node.getFirstChild().getNodeValue();
//        }
        
//        System.out.println("################");
//        System.out.println("trying to get text content for child = " + child);
//        System.out.println("getNode(child) = " + getNode(child));
        
        //ignore elements with the wrong formatting, see #2353
        if (getNode(child)==null){
//            System.out.println("Found a child that has no text content, see #2353");
//            printTree(0);
            return "";
        }
//        System.out.println("getNode(child).node = " + getNode(child).node);
//        System.out.println("getNode(child).node.getTextContent() = " + getNode(child).node.getTextContent());
        return getNode( child ).node.getTextContent();
    }

    private void printTree(int level) {
        NodeList ch = node.getChildNodes();
        for (int i=0;i<level;i++)System.out.print("\t");
        for ( int i = 0; i < ch.getLength(); i++ ) {
            final Node node = ch.item( i );
            System.out.println(node.getNodeName()+": "+node.getNodeValue());
            new XMLObject( node ).printTree(level  +1);
        }
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

    public boolean containsNode( String node ) {
        return getNode( node ) != null;
    }

    public int getTextContentAsInt( String s ) {
        try{
            return Integer.parseInt( getTextContent( s ) );
        }catch (Exception d){
            System.out.println("Error: Expected a non-zero length string.  This may");
            System.out.println("have been caused by a category value of \'none\'");
            d.printStackTrace();
            return -1;
        }
    }
}
