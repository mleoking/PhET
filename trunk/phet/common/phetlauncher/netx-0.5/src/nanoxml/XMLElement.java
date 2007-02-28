/* XMLElement.java
 *
 * $Revision$
 * $Date$
 * $Name$
 *
 * This file is part of NanoXML 2 Lite.
 * Copyright (C) 2000-2002 Marc De Scheemaecker, All Rights Reserved.
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software in
 *     a product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 *
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *
 *  3. This notice may not be removed or altered from any source distribution.
 *****************************************************************************/

/* JAM: hacked the source to remove unneeded methods and comments. */

package nanoxml;

import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/**
 * XMLElement is a representation of an XML object. The object is able to parse
 * XML code.
 * <P><DL>
 * <DT><B>Parsing XML Data</B></DT>
 * <DD>
 * You can parse XML data using the following code:
 * <UL><CODE>
 * XMLElement xml = new XMLElement();<BR>
 * FileReader reader = new FileReader("filename.xml");<BR>
 * xml.parseFromReader(reader);
 * </CODE></UL></DD></DL>
 * <DL><DT><B>Retrieving Attributes</B></DT>
 * <DD>
 * You can enumerate the attributes of an element using the method
 * {@link #enumerateAttributeNames() enumerateAttributeNames}.
 * The attribute values can be retrieved using the method
 * {@link #getStringAttribute(String) getStringAttribute}.
 * The following example shows how to list the attributes of an element:
 * <UL><CODE>
 * XMLElement element = ...;<BR>
 * Enumeration enum = element.getAttributeNames();<BR>
 * while (enum.hasMoreElements()) {<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;String key = (String) enum.nextElement();<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;String value = element.getStringAttribute(key);<BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;System.out.println(key + " = " + value);<BR>
 * }
 * </CODE></UL></DD></DL>
 * <DL><DT><B>Retrieving Child Elements</B></DT>
 * <DD>
 * You can enumerate the children of an element using
 * {@link #enumerateChildren() enumerateChildren}.
 * The number of child elements can be retrieved using
 * {@link #countChildren() countChildren}.
 * </DD></DL>
 * <DL><DT><B>Elements Containing Character Data</B></DT>
 * <DD>
 * If an elements contains character data, like in the following example:
 * <UL><CODE>
 * &lt;title&gt;The Title&lt;/title&gt;
 * </CODE></UL>
 * you can retrieve that data using the method
 * {@link #getContent() getContent}.
 * </DD></DL>
 * <DL><DT><B>Subclassing XMLElement</B></DT>
 * <DD>
 * When subclassing XMLElement, you need to override the method
 * {@link #createAnotherElement() createAnotherElement}
 * which has to return a new copy of the receiver.
 * </DD></DL>
 * <p/>
 *
 * @author Marc De Scheemaecker
 *         &lt;<A href="mailto:cyberelf@mac.com">cyberelf@mac.com</A>&gt;
 * @version $Name$, $Revision$
 * @see XMLParseException
 */
public class XMLElement {

    /**
     * The attributes given to the element.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li>The field can be empty.
     * <li>The field is never <code>null</code>.
     * <li>The keys and the values are strings.
     * </ul></dd></dl>
     */
    private Hashtable attributes;


    /**
     * Child elements of the element.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li>The field can be empty.
     * <li>The field is never <code>null</code>.
     * <li>The elements are instances of <code>XMLElement</code>
     * or a subclass of <code>XMLElement</code>.
     * </ul></dd></dl>
     */
    private Vector children;


    /**
     * The name of the element.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li>The field is <code>null</code> iff the element is not
     * initialized by either parse or setName.
     * <li>If the field is not <code>null</code>, it's not empty.
     * <li>If the field is not <code>null</code>, it contains a valid
     * XML identifier.
     * </ul></dd></dl>
     */
    private String name;


    /**
     * The #PCDATA content of the object.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li>The field is <code>null</code> iff the element is not a
     * #PCDATA element.
     * <li>The field can be any string, including the empty string.
     * </ul></dd></dl>
     */
    private String contents;


    /**
     * Conversion table for &amp;...; entities. The keys are the entity names
     * without the &amp; and ; delimiters.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li>The field is never <code>null</code>.
     * <li>The field always contains the following associations:
     * "lt"&nbsp;=&gt;&nbsp;"&lt;", "gt"&nbsp;=&gt;&nbsp;"&gt;",
     * "quot"&nbsp;=&gt;&nbsp;"\"", "apos"&nbsp;=&gt;&nbsp;"'",
     * "amp"&nbsp;=&gt;&nbsp;"&amp;"
     * <li>The keys are strings
     * <li>The values are char arrays
     * </ul></dd></dl>
     */
    private Hashtable entities;


    /**
     * The line number where the element starts.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li><code>lineNr &gt= 0</code>
     * </ul></dd></dl>
     */
    private int lineNr;


    /**
     * <code>true</code> if the case of the element and attribute names
     * are case insensitive.
     */
    private boolean ignoreCase;


    /**
     * <code>true</code> if the leading and trailing whitespace of #PCDATA
     * sections have to be ignored.
     */
    private boolean ignoreWhitespace;


    /**
     * Character read too much.
     * This character provides push-back functionality to the input reader
     * without having to use a PushbackReader.
     * If there is no such character, this field is '\0'.
     */
    private char charReadTooMuch;


    /**
     * The reader provided by the caller of the parse method.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li>The field is not <code>null</code> while the parse method
     * is running.
     * </ul></dd></dl>
     */
    private Reader reader;


    /**
     * The current line number in the source content.
     * <p/>
     * <dl><dt><b>Invariants:</b></dt><dd>
     * <ul><li>parserLineNr &gt; 0 while the parse method is running.
     * </ul></dd></dl>
     */
    private int parserLineNr;


    /**
     * Creates and initializes a new XML element.
     * Calling the construction is equivalent to:
     * <ul><code>new XMLElement(new Hashtable(), false, true)
     * </code></ul>
     * <p/>
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li>countChildren() => 0
     * <li>enumerateChildren() => empty enumeration
     * <li>enumeratePropertyNames() => empty enumeration
     * <li>getChildren() => empty vector
     * <li>getContent() => ""
     * <li>getLineNr() => 0
     * <li>getName() => null
     * </ul></dd></dl>
     */
    public XMLElement() {
        this( new Hashtable(), false, true, true );
    }


    /**
     * Creates and initializes a new XML element.
     * <p/>
     * This constructor should <I>only</I> be called from
     * {@link #createAnotherElement() createAnotherElement}
     * to create child elements.
     *
     * @param entities                 The entity conversion table.
     * @param skipLeadingWhitespace    <code>true</code> if leading and trailing whitespace in PCDATA
     *                                 content has to be removed.
     * @param fillBasicConversionTable <code>true</code> if the basic entities need to be added to
     *                                 the entity list (client code calling this constructor).
     * @param ignoreCase               <code>true</code> if the case of element and attribute names have
     *                                 to be ignored.
     *                                 <p/>
     *                                 </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                                 <ul><li><code>entities != null</code>
     *                                 <li>if <code>fillBasicConversionTable == false</code>
     *                                 then <code>entities</code> contains at least the following
     *                                 entries: <code>amp</code>, <code>lt</code>, <code>gt</code>,
     *                                 <code>apos</code> and <code>quot</code>
     *                                 </ul></dd></dl>
     *                                 <p/>
     *                                 <dl><dt><b>Postconditions:</b></dt><dd>
     *                                 <ul><li>countChildren() => 0
     *                                 <li>enumerateChildren() => empty enumeration
     *                                 <li>enumeratePropertyNames() => empty enumeration
     *                                 <li>getChildren() => empty vector
     *                                 <li>getContent() => ""
     *                                 <li>getLineNr() => 0
     *                                 <li>getName() => null
     *                                 </ul></dd></dl><dl>
     */
    protected XMLElement( Hashtable entities,
                          boolean skipLeadingWhitespace,
                          boolean fillBasicConversionTable,
                          boolean ignoreCase ) {
        this.ignoreWhitespace = skipLeadingWhitespace;
        this.ignoreCase = ignoreCase;
        this.name = null;
        this.contents = "";
        this.attributes = new Hashtable();
        this.children = new Vector();
        this.entities = entities;
        this.lineNr = 0;
        Enumeration enum = this.entities.keys();
        while( enum.hasMoreElements() ) {
            Object key = enum.nextElement();
            Object value = this.entities.get( key );
            if( value instanceof String ) {
                value = ( (String)value ).toCharArray();
                this.entities.put( key, value );
            }
        }
        if( fillBasicConversionTable ) {
            this.entities.put( "amp", new char[]{'&'} );
            this.entities.put( "quot", new char[]{'"'} );
            this.entities.put( "apos", new char[]{'\''} );
            this.entities.put( "lt", new char[]{'<'} );
            this.entities.put( "gt", new char[]{'>'} );
        }
    }


    /**
     * Adds a child element.
     *
     * @param child The child element to add.
     *              <p/>
     *              </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *              <ul><li><code>child != null</code>
     *              <li><code>child.getName() != null</code>
     *              <li><code>child</code> does not have a parent element
     *              </ul></dd></dl>
     *              <p/>
     *              <dl><dt><b>Postconditions:</b></dt><dd>
     *              <ul><li>countChildren() => old.countChildren() + 1
     *              <li>enumerateChildren() => old.enumerateChildren() + child
     *              <li>getChildren() => old.enumerateChildren() + child
     *              </ul></dd></dl><dl>
     */
    public void addChild( XMLElement child ) {
        this.children.addElement( child );
    }


    /**
     * Adds or modifies an attribute.
     *
     * @param name  The name of the attribute.
     * @param value The value of the attribute.
     *              <p/>
     *              </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *              <ul><li><code>name != null</code>
     *              <li><code>name</code> is a valid XML identifier
     *              <li><code>value != null</code>
     *              </ul></dd></dl>
     *              <p/>
     *              <dl><dt><b>Postconditions:</b></dt><dd>
     *              <ul><li>enumerateAttributeNames()
     *              => old.enumerateAttributeNames() + name
     *              <li>getAttribute(name) => value
     *              </ul></dd></dl><dl>
     */
    public void setAttribute( String name,
                              Object value ) {
        if( this.ignoreCase ) {
            name = name.toUpperCase();
        }
        this.attributes.put( name, value.toString() );
    }


    /**
     * Returns the number of child elements of the element.
     * <p/>
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li><code>result >= 0</code>
     * </ul></dd></dl>
     */
    public int countChildren() {
        return this.children.size();
    }


    /**
     * Enumerates the attribute names.
     * <p/>
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li><code>result != null</code>
     * </ul></dd></dl>
     */
    public Enumeration enumerateAttributeNames() {
        return this.attributes.keys();
    }


    /**
     * Enumerates the child elements.
     * <p/>
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li><code>result != null</code>
     * </ul></dd></dl>
     */
    public Enumeration enumerateChildren() {
        return this.children.elements();
    }


    /**
     * Returns the PCDATA content of the object. If there is no such content,
     * <CODE>null</CODE> is returned.
     */
    public String getContent() {
        return this.contents;
    }


    /**
     * Returns the line nr in the source data on which the element is found.
     * This method returns <code>0</code> there is no associated source data.
     * <p/>
     * <dl><dt><b>Postconditions:</b></dt><dd>
     * <ul><li><code>result >= 0</code>
     * </ul></dd></dl>
     */
    public int getLineNr() {
        return this.lineNr;
    }


    /**
     * Returns an attribute of the element.
     * If the attribute doesn't exist, <code>null</code> is returned.
     *
     * @param name The name of the attribute.
     *             <p/>
     *             </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *             <ul><li><code>name != null</code>
     *             <li><code>name</code> is a valid XML identifier
     *             </ul></dd></dl><dl>
     */
    public Object getAttribute( String name ) {
        if( this.ignoreCase ) {
            name = name.toUpperCase();
        }
        Object value = this.attributes.get( name );
        return value;
    }


    /**
     * Returns the name of the element.
     */
    public String getName() {
        return this.name;
    }


    /**
     * Reads one XML element from a java.io.Reader and parses it.
     *
     * @param reader The reader from which to retrieve the XML data.
     *               <p/>
     *               </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *               <ul><li><code>reader != null</code>
     *               <li><code>reader</code> is not closed
     *               </ul></dd></dl>
     *               <p/>
     *               <dl><dt><b>Postconditions:</b></dt><dd>
     *               <ul><li>the state of the receiver is updated to reflect the XML element
     *               parsed from the reader
     *               <li>the reader points to the first character following the last
     *               '&gt;' character of the XML element
     *               </ul></dd></dl><dl>
     * @throws java.io.IOException       If an error occured while reading the input.
     * @throws XMLParseException If an error occured while parsing the read data.
     */
    public void parseFromReader( Reader reader )
            throws IOException, XMLParseException {
        this.parseFromReader( reader, /*startingLineNr*/ 1 );
    }


    /**
     * Reads one XML element from a java.io.Reader and parses it.
     *
     * @param reader         The reader from which to retrieve the XML data.
     * @param startingLineNr The line number of the first line in the data.
     *                       <p/>
     *                       </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                       <ul><li><code>reader != null</code>
     *                       <li><code>reader</code> is not closed
     *                       </ul></dd></dl>
     *                       <p/>
     *                       <dl><dt><b>Postconditions:</b></dt><dd>
     *                       <ul><li>the state of the receiver is updated to reflect the XML element
     *                       parsed from the reader
     *                       <li>the reader points to the first character following the last
     *                       '&gt;' character of the XML element
     *                       </ul></dd></dl><dl>
     * @throws java.io.IOException       If an error occured while reading the input.
     * @throws XMLParseException If an error occured while parsing the read data.
     */
    public void parseFromReader( Reader reader,
                                 int startingLineNr )
            throws IOException, XMLParseException {
        this.charReadTooMuch = '\0';
        this.reader = reader;
        this.parserLineNr = startingLineNr;

        for( ; ; ) {
            char ch = this.scanWhitespace();

            if( ch != '<' ) {
                throw this.expectedInput( "<" );
            }

            ch = this.readChar();

            if( ( ch == '!' ) || ( ch == '?' ) ) {
                this.skipSpecialTag( 0 );
            }
            else {
                this.unreadChar( ch );
                this.scanElement( this );
                return;
            }
        }
    }


    /**
     * Creates a new similar XML element.
     * <p/>
     * You should override this method when subclassing XMLElement.
     */
    protected XMLElement createAnotherElement() {
        return new XMLElement( this.entities,
                               this.ignoreWhitespace,
                               false,
                               this.ignoreCase );
    }


    /**
     * Changes the content string.
     *
     * @param content The new content string.
     */
    public void setContent( String content ) {
        this.contents = content;
    }


    /**
     * Changes the name of the element.
     *
     * @param name The new name.
     *             <p/>
     *             </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *             <ul><li><code>name != null</code>
     *             <li><code>name</code> is a valid XML identifier
     *             </ul></dd></dl>
     */
    public void setName( String name ) {
        this.name = name;
    }


    /**
     * Scans an identifier from the current reader.
     * The scanned identifier is appended to <code>result</code>.
     *
     * @param result The buffer in which the scanned identifier will be put.
     *               <p/>
     *               </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *               <ul><li><code>result != null</code>
     *               <li>The next character read from the reader is a valid first
     *               character of an XML identifier.
     *               </ul></dd></dl>
     *               <p/>
     *               <dl><dt><b>Postconditions:</b></dt><dd>
     *               <ul><li>The next character read from the reader won't be an identifier
     *               character.
     *               </ul></dd></dl><dl>
     */
    protected void scanIdentifier( StringBuffer result )
            throws IOException {
        for( ; ; ) {
            char ch = this.readChar();
            if( ( ( ch < 'A' ) || ( ch > 'Z' ) ) && ( ( ch < 'a' ) || ( ch > 'z' ) )
                && ( ( ch < '0' ) || ( ch > '9' ) ) && ( ch != '_' ) && ( ch != '.' )
                && ( ch != ':' ) && ( ch != '-' ) && ( ch <= '\u007E' ) ) {
                this.unreadChar( ch );
                return;
            }
            result.append( ch );
        }
    }


    /**
     * This method scans an identifier from the current reader.
     *
     * @return the next character following the whitespace.
     */
    protected char scanWhitespace()
            throws IOException {
        for( ; ; ) {
            char ch = this.readChar();
            switch( ch ) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    break;
                default:
                    return ch;
            }
        }
    }


    /**
     * This method scans an identifier from the current reader.
     * The scanned whitespace is appended to <code>result</code>.
     *
     * @return the next character following the whitespace.
     *         <p/>
     *         </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *         <ul><li><code>result != null</code>
     *         </ul></dd></dl>
     */
    protected char scanWhitespace( StringBuffer result )
            throws IOException {
        for( ; ; ) {
            char ch = this.readChar();
            switch( ch ) {
                case ' ':
                case '\t':
                case '\n':
                    result.append( ch );
                case '\r':
                    break;
                default:
                    return ch;
            }
        }
    }


    /**
     * This method scans a delimited string from the current reader.
     * The scanned string without delimiters is appended to
     * <code>string</code>.
     * <p/>
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>string != null</code>
     * <li>the next char read is the string delimiter
     * </ul></dd></dl>
     */
    protected void scanString( StringBuffer string )
            throws IOException {
        char delimiter = this.readChar();
        if( ( delimiter != '\'' ) && ( delimiter != '"' ) ) {
            throw this.expectedInput( "' or \"" );
        }
        for( ; ; ) {
            char ch = this.readChar();
            if( ch == delimiter ) {
                return;
            }
            else if( ch == '&' ) {
                this.resolveEntity( string );
            }
            else {
                string.append( ch );
            }
        }
    }


    /**
     * Scans a #PCDATA element. CDATA sections and entities are resolved.
     * The next &lt; char is skipped.
     * The scanned data is appended to <code>data</code>.
     * <p/>
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>data != null</code>
     * </ul></dd></dl>
     */
    protected void scanPCData( StringBuffer data )
            throws IOException {
        for( ; ; ) {
            char ch = this.readChar();
            if( ch == '<' ) {
                ch = this.readChar();
                if( ch == '!' ) {
                    this.checkCDATA( data );
                }
                else {
                    this.unreadChar( ch );
                    return;
                }
            }
            else if( ch == '&' ) {
                this.resolveEntity( data );
            }
            else {
                data.append( ch );
            }
        }
    }


    /**
     * Scans a special tag and if the tag is a CDATA section, append its
     * content to <code>buf</code>.
     * <p/>
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>buf != null</code>
     * <li>The first &lt; has already been read.
     * </ul></dd></dl>
     */
    protected boolean checkCDATA( StringBuffer buf )
            throws IOException {
        char ch = this.readChar();
        if( ch != '[' ) {
            this.unreadChar( ch );
            this.skipSpecialTag( 0 );
            return false;
        }
        else if( ! this.checkLiteral( "CDATA[" ) ) {
            this.skipSpecialTag( 1 ); // one [ has already been read
            return false;
        }
        else {
            int delimiterCharsSkipped = 0;
            while( delimiterCharsSkipped < 3 ) {
                ch = this.readChar();
                switch( ch ) {
                    case ']':
                        if( delimiterCharsSkipped < 2 ) {
                            delimiterCharsSkipped += 1;
                        }
                        else {
                            buf.append( ']' );
                            buf.append( ']' );
                            delimiterCharsSkipped = 0;
                        }
                        break;
                    case '>':
                        if( delimiterCharsSkipped < 2 ) {
                            for( int i = 0; i < delimiterCharsSkipped; i++ ) {
                                buf.append( ']' );
                            }
                            delimiterCharsSkipped = 0;
                            buf.append( '>' );
                        }
                        else {
                            delimiterCharsSkipped = 3;
                        }
                        break;
                    default:
                        for( int i = 0; i < delimiterCharsSkipped; i += 1 ) {
                            buf.append( ']' );
                        }
                        buf.append( ch );
                        delimiterCharsSkipped = 0;
                }
            }
            return true;
        }
    }


    /**
     * Skips a comment.
     * <p/>
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li>The first &lt;!-- has already been read.
     * </ul></dd></dl>
     */
    protected void skipComment()
            throws IOException {
        int dashesToRead = 2;
        while( dashesToRead > 0 ) {
            char ch = this.readChar();
            if( ch == '-' ) {
                dashesToRead -= 1;
            }
            else {
                dashesToRead = 2;
            }
        }
        if( this.readChar() != '>' ) {
            throw this.expectedInput( ">" );
        }
    }


    /**
     * Skips a special tag or comment.
     *
     * @param bracketLevel The number of open square brackets ([) that have
     *                     already been read.
     *                     <p/>
     *                     </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                     <ul><li>The first &lt;! has already been read.
     *                     <li><code>bracketLevel >= 0</code>
     *                     </ul></dd></dl>
     */
    protected void skipSpecialTag( int bracketLevel )
            throws IOException {
        int tagLevel = 1; // <
        char stringDelimiter = '\0';
        if( bracketLevel == 0 ) {
            char ch = this.readChar();
            if( ch == '[' ) {
                bracketLevel += 1;
            }
            else if( ch == '-' ) {
                ch = this.readChar();
                if( ch == '[' ) {
                    bracketLevel += 1;
                }
                else if( ch == ']' ) {
                    bracketLevel -= 1;
                }
                else if( ch == '-' ) {
                    this.skipComment();
                    return;
                }
            }
        }
        while( tagLevel > 0 ) {
            char ch = this.readChar();
            if( stringDelimiter == '\0' ) {
                if( ( ch == '"' ) || ( ch == '\'' ) ) {
                    stringDelimiter = ch;
                }
                else if( bracketLevel <= 0 ) {
                    if( ch == '<' ) {
                        tagLevel += 1;
                    }
                    else if( ch == '>' ) {
                        tagLevel -= 1;
                    }
                }
                if( ch == '[' ) {
                    bracketLevel += 1;
                }
                else if( ch == ']' ) {
                    bracketLevel -= 1;
                }
            }
            else {
                if( ch == stringDelimiter ) {
                    stringDelimiter = '\0';
                }
            }
        }
    }


    /**
     * Scans the data for literal text.
     * Scanning stops when a character does not match or after the complete
     * text has been checked, whichever comes first.
     *
     * @param literal the literal to check.
     *                <p/>
     *                </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                <ul><li><code>literal != null</code>
     *                </ul></dd></dl>
     */
    protected boolean checkLiteral( String literal )
            throws IOException {
        int length = literal.length();
        for( int i = 0; i < length; i += 1 ) {
            if( this.readChar() != literal.charAt( i ) ) {
                return false;
            }
        }
        return true;
    }


    /**
     * Reads a character from a reader.
     */
    protected char readChar()
            throws IOException {
        if( this.charReadTooMuch != '\0' ) {
            char ch = this.charReadTooMuch;
            this.charReadTooMuch = '\0';
            return ch;
        }
        else {
            int i = this.reader.read();
            if( i < 0 ) {
                throw this.unexpectedEndOfData();
            }
            else if( i == 10 ) {
                this.parserLineNr += 1;
                return '\n';
            }
            else {
                return (char)i;
            }
        }
    }


    /**
     * Scans an XML element.
     *
     * @param elt The element that will contain the result.
     *            <p/>
     *            </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *            <ul><li>The first &lt; has already been read.
     *            <li><code>elt != null</code>
     *            </ul></dd></dl>
     */
    protected void scanElement( XMLElement elt )
            throws IOException {
        StringBuffer buf = new StringBuffer();
        this.scanIdentifier( buf );
        String name = buf.toString();
        elt.setName( name );
        char ch = this.scanWhitespace();
        while( ( ch != '>' ) && ( ch != '/' ) ) {
            buf.setLength( 0 );
            this.unreadChar( ch );
            this.scanIdentifier( buf );
            String key = buf.toString();
            ch = this.scanWhitespace();
            if( ch != '=' ) {
                throw this.expectedInput( "=" );
            }
            this.unreadChar( this.scanWhitespace() );
            buf.setLength( 0 );
            this.scanString( buf );
            elt.setAttribute( key, buf );
            ch = this.scanWhitespace();
        }
        if( ch == '/' ) {
            ch = this.readChar();
            if( ch != '>' ) {
                throw this.expectedInput( ">" );
            }
            return;
        }
        buf.setLength( 0 );
        ch = this.scanWhitespace( buf );
        if( ch != '<' ) {
            this.unreadChar( ch );
            this.scanPCData( buf );
        }
        else {
            for( ; ; ) {
                ch = this.readChar();
                if( ch == '!' ) {
                    if( this.checkCDATA( buf ) ) {
                        this.scanPCData( buf );
                        break;
                    }
                    else {
                        ch = this.scanWhitespace( buf );
                        if( ch != '<' ) {
                            this.unreadChar( ch );
                            this.scanPCData( buf );
                            break;
                        }
                    }
                }
                else {
                    buf.setLength( 0 );
                    break;
                }
            }
        }
        if( buf.length() == 0 ) {
            while( ch != '/' ) {
                if( ch == '!' ) {
                    ch = this.readChar();
                    if( ch != '-' ) {
                        throw this.expectedInput( "Comment or Element" );
                    }
                    ch = this.readChar();
                    if( ch != '-' ) {
                        throw this.expectedInput( "Comment or Element" );
                    }
                    this.skipComment();
                }
                else {
                    this.unreadChar( ch );
                    XMLElement child = this.createAnotherElement();
                    this.scanElement( child );
                    elt.addChild( child );
                }
                ch = this.scanWhitespace();
                if( ch != '<' ) {
                    throw this.expectedInput( "<" );
                }
                ch = this.readChar();
            }
            this.unreadChar( ch );
        }
        else {
            if( this.ignoreWhitespace ) {
                elt.setContent( buf.toString().trim() );
            }
            else {
                elt.setContent( buf.toString() );
            }
        }
        ch = this.readChar();
        if( ch != '/' ) {
            throw this.expectedInput( "/" );
        }
        this.unreadChar( this.scanWhitespace() );
        if( ! this.checkLiteral( name ) ) {
            throw this.expectedInput( name );
        }
        if( this.scanWhitespace() != '>' ) {
            throw this.expectedInput( ">" );
        }
    }


    /**
     * Resolves an entity. The name of the entity is read from the reader.
     * The value of the entity is appended to <code>buf</code>.
     *
     * @param buf Where to put the entity value.
     *            <p/>
     *            </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *            <ul><li>The first &amp; has already been read.
     *            <li><code>buf != null</code>
     *            </ul></dd></dl>
     */
    protected void resolveEntity( StringBuffer buf )
            throws IOException {
        char ch = '\0';
        StringBuffer keyBuf = new StringBuffer();
        for( ; ; ) {
            ch = this.readChar();
            if( ch == ';' ) {
                break;
            }
            keyBuf.append( ch );
        }
        String key = keyBuf.toString();
        if( key.charAt( 0 ) == '#' ) {
            try {
                if( key.charAt( 1 ) == 'x' ) {
                    ch = (char)Integer.parseInt( key.substring( 2 ), 16 );
                }
                else {
                    ch = (char)Integer.parseInt( key.substring( 1 ), 10 );
                }
            }
            catch( NumberFormatException e ) {
                throw this.unknownEntity( key );
            }
            buf.append( ch );
        }
        else {
            char[] value = (char[])this.entities.get( key );
            if( value == null ) {
                throw this.unknownEntity( key );
            }
            buf.append( value );
        }
    }


    /**
     * Pushes a character back to the read-back buffer.
     *
     * @param ch The character to push back.
     *           <p/>
     *           </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *           <ul><li>The read-back buffer is empty.
     *           <li><code>ch != '\0'</code>
     *           </ul></dd></dl>
     */
    protected void unreadChar( char ch ) {
        this.charReadTooMuch = ch;
    }


    /**
     * Creates a parse exception for when an invalid valueset is given to
     * a method.
     *
     * @param name The name of the entity.
     *             <p/>
     *             </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *             <ul><li><code>name != null</code>
     *             </ul></dd></dl>
     */
    protected XMLParseException invalidValueSet( String name ) {
        String msg = "Invalid value set (entity name = \"" + name + "\")";
        return new XMLParseException( this.getName(), this.parserLineNr, msg );
    }


    /**
     * Creates a parse exception for when an invalid value is given to a
     * method.
     *
     * @param name  The name of the entity.
     * @param value The value of the entity.
     *              <p/>
     *              </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *              <ul><li><code>name != null</code>
     *              <li><code>value != null</code>
     *              </ul></dd></dl>
     */
    protected XMLParseException invalidValue( String name,
                                              String value ) {
        String msg = "Attribute \"" + name + "\" does not contain a valid "
                     + "value (\"" + value + "\")";
        return new XMLParseException( this.getName(), this.parserLineNr, msg );
    }


    /**
     * Creates a parse exception for when the end of the data input has been
     * reached.
     */
    protected XMLParseException unexpectedEndOfData() {
        String msg = "Unexpected end of data reached";
        return new XMLParseException( this.getName(), this.parserLineNr, msg );
    }


    /**
     * Creates a parse exception for when a syntax error occured.
     *
     * @param context The context in which the error occured.
     *                <p/>
     *                </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                <ul><li><code>context != null</code>
     *                <li><code>context.length() &gt; 0</code>
     *                </ul></dd></dl>
     */
    protected XMLParseException syntaxError( String context ) {
        String msg = "Syntax error while parsing " + context;
        return new XMLParseException( this.getName(), this.parserLineNr, msg );
    }


    /**
     * Creates a parse exception for when the next character read is not
     * the character that was expected.
     *
     * @param charSet The set of characters (in human readable form) that was
     *                expected.
     *                <p/>
     *                </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *                <ul><li><code>charSet != null</code>
     *                <li><code>charSet.length() &gt; 0</code>
     *                </ul></dd></dl>
     */
    protected XMLParseException expectedInput( String charSet ) {
        String msg = "Expected: " + charSet;
        return new XMLParseException( this.getName(), this.parserLineNr, msg );
    }


    /**
     * Creates a parse exception for when an entity could not be resolved.
     *
     * @param name The name of the entity.
     *             <p/>
     *             </dl><dl><dt><b>Preconditions:</b></dt><dd>
     *             <ul><li><code>name != null</code>
     *             <li><code>name.length() &gt; 0</code>
     *             </ul></dd></dl>
     */
    protected XMLParseException unknownEntity( String name ) {
        String msg = "Unknown or invalid entity: &" + name + ";";
        return new XMLParseException( this.getName(), this.parserLineNr, msg );
    }

}
