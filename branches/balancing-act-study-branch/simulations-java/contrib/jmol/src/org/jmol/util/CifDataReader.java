package org.jmol.util;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.jmol.api.JmolLineReader;

public class CifDataReader {
  /**
   * A special tokenizer class for dealing with quoted strings in CIF files.
   *<p>
   * regarding the treatment of single quotes vs. primes in
   * cif file, PMR wrote:
   *</p>
   *<p>
   *   * There is a formal grammar for CIF
   * (see http://www.iucr.org/iucr-top/cif/index.html)
   * which confirms this. The textual explanation is
   *<p />
   *<p>
   * 14. Matching single or double quote characters (' or ") may
   * be used to bound a string representing a non-simple data value
   * provided the string does not extend over more than one line.
   *<p />
   *<p>
   * 15. Because data values are invariably separated from other
   * tokens in the file by white space, such a quote-delimited
   * character string may contain instances of the character used
   * to delimit the string provided they are not followed by white
   * space. For example, the data item
   *<code>
   *  _example  'a dog's life'
   *</code>
   * is legal; the data value is a dog's life.
   *</p>
   *<p>
   * [PMR - the terminating character(s) are quote+whitespace.
   * That would mean that:
   *<code>
   *  _example 'Jones' life'
   *</code>
   * would be an error
   *</p>
   *<p>
   * The CIF format was developed in that late 1980's under the aegis of the
   * International Union of Crystallography (I am a consultant to the COMCIFs 
   * committee). It was ratified by the Union and there have been several 
   * workshops. mmCIF is an extension of CIF which includes a relational 
   * structure. The formal publications are:
   *</p>
   *<p>
   * Hall, S. R. (1991). "The STAR File: A New Format for Electronic Data 
   * Transfer and Archiving", J. Chem. Inform. Comp. Sci., 31, 326-333.
   * Hall, S. R., Allen, F. H. and Brown, I. D. (1991). "The Crystallographic
   * Information File (CIF): A New Standard Archive File for Crystallography",
   * Acta Cryst., A47, 655-685.
   * Hall, S.R. & Spadaccini, N. (1994). "The STAR File: Detailed 
   * Specifications," J. Chem. Info. Comp. Sci., 34, 505-508.
   *</p>
   */
  private JmolLineReader reader;
  private BufferedReader br;

  private String line;  
  public String str;
  public int ich;
  public int cch;
  public boolean wasUnQuoted;
  public String strPeeked;
  public int ichPeeked;
  public int fieldCount;
  public String[] loopData;
  public StringBuffer fileHeader = new StringBuffer();
  private boolean isHeader = true;
  
  ////////////////////////////////////////////////////////////////
  // special tokenizer class
  ////////////////////////////////////////////////////////////////

  
  public CifDataReader(JmolLineReader reader) {
    this.reader = reader;
  }

  public CifDataReader(BufferedReader br) {
    this.br = br;
  }

  public String getFileHeader() {
    return fileHeader.toString();
  }
  
  public static Map<String, Object> readCifData(BufferedReader br) {
    CifDataReader cdr = new CifDataReader(br);
    return cdr.getAllCifData();
  }
  
  
  /**
   * reads all Cif Data for a reader defined in the constructor
   * 
   * @return Hashtable of models Vector of Hashtable data
   */
  private Map<String, Object> getAllCifData() {
    line = "";
    String key;
    allData = new Hashtable<String, Object>();
    List<Map<String, Object>> models = new ArrayList<Map<String,Object>>();
    allData.put("models", models);
    try {
      while ((key = getNextToken()) != null) {
        if (key.startsWith("global_") || key.startsWith("data_")) {
          models.add(data = new Hashtable<String, Object>());
          data.put("name", key);
          continue;
        }
        if (key.startsWith("loop_")) {
          getCifLoopData();
          continue;
        }
        if (key.indexOf("_") != 0) {
          Logger.warn("CIF ERROR ? should be an underscore: " + key);
        } else {
          String value = getNextToken();
          if (value == null) {
            Logger.warn("CIF ERROR ? end of file; data missing: " + key);
          } else {
            data.put(key, value);
          }
        }
      }
    } catch (Exception e) {
      // ?
    }
    try {
      if (br != null)
        br.close();
    } catch (Exception e) {
      // ?
    }
    return allData;
  }

  public String readLine() {
    try {
      line = (reader != null ? reader.readLine() : br.readLine());
      if (line == null)
        return null;
      if (isHeader) {
        if (line.startsWith("#"))
          fileHeader.append(line).append('\n');
        else
          isHeader = false;
      }
      return line;
    } catch (Exception e) {
      return null;
    }
  }
  
  /**
   * general reader for loop data
   * fills loopData with fieldCount fields
   * 
   * @return false if EOF
   * @throws Exception
   */
  public boolean getData() throws Exception {
    // line is already present, and we leave with the next line to parse
    for (int i = 0; i < fieldCount; ++i)
      if ((loopData[i] = getNextDataToken()) == null)
        return false;
    return true;
  }

  /**
   * 
   * @return the next token of any kind, or null
   * @throws Exception
   */
  public String getNextToken() throws Exception {
    while (!hasMoreTokens())
      if (setStringNextLine() == null)
        return null;
    return nextToken();
  }

  /**
   * sets a string to be parsed from the beginning
   * 
   * @param str
   */
  private void setString(String str) {
    this.str = line = str;
    cch = (str == null ? 0 : str.length());
    ich = 0;
  }

  /*
   * http://www.iucr.org/iucr-top/cif/spec/version1.1/cifsyntax.html#syntax
   * 
   * 17. The special sequence of end-of-line followed 
   * immediately by a semicolon in column one (denoted "<eol>;") 
   * may also be used as a delimiter at the beginning and end 
   * of a character string comprising a data value. The complete 
   * bounded string is called a text field, and may be used to 
   * convey multi-line values. The end-of-line associated with 
   * the closing semicolon does not form part of the data value. 
   * Within a multi-line text field, leading white space within 
   * text lines must be retained as part of the data value; trailing 
   * white space on a line may however be elided.
   * 
   * 18. A text field delimited by the <eol>; digraph may not 
   * include a semicolon at the start of a line of text as 
   * part of its value.
   * 
   * 20. For example, the data value foo may be expressed 
   * equivalently as an unquoted string foo, as a quoted 
   * string 'foo' or as a text field
   *
   *;foo
   *;
   *
   * By contrast the value of the text field
   *
   *; foo
   *  bar
   *;
   *
   * is  foo<eol>  bar (where <eol> represents an end-of-line); 
   * the embedded space characters are significant.
   * 
   * 
   * I (BH) note, however, that we sometimes have:
   * 
   * _some_name
   * ;
   * the name here
   * ;
   * 
   * so this should actually be
   * 
   * ;the name here
   * ;
   * 
   * for this, we use fullTrim();
   * 
   */
  
  /**
   * 
   * sets the string for parsing to be from the next line 
   * when the token buffer is empty, and if ';' is at the 
   * beginning of that line, extends the string to include
   * that full multiline string. Uses \1 to indicate that 
   * this is a special quotation. 
   * 
   * @return  the next line or null if EOF
   * @throws Exception
   */
  private String setStringNextLine() throws Exception {
    setString(readLine());
    if (line == null || line.length() == 0 || line.charAt(0) != ';')
      return line;
    ich = 1;
    String str = '\1' + line.substring(1) + '\n';
    while (readLine() != null) {
      if (line.startsWith(";")) {
        // remove trailing <eol> only, and attach rest of next line
        str = str.substring(0, str.length() - 1)
          + '\1' + line.substring(1);
        break;
      }
      str += line + '\n';
    }
    setString(str);
    return str;
  }

  /**
   * @return TRUE if there are more tokens in the line buffer
   * 
   */
  private boolean hasMoreTokens() {
    if (str == null)
      return false;
    char ch = '#';
    while (ich < cch && ((ch = str.charAt(ich)) == ' ' || ch == '\t'))
      ++ich;
    return (ich < cch && ch != '#');
  }

  /**
   * assume that hasMoreTokens() has been called and that
   * ich is pointing at a non-white character. Also sets
   * boolean wasUnQuoted, because we need to know if we should 
   * be checking for a control keyword. 'loop_' is different from just 
   * loop_ without the quotes.
   *
   * @return null if no more tokens, "\0" if '.' or '?', or next token 
   */
  private String nextToken() {
    if (ich == cch)
      return null;
    int ichStart = ich;
    char ch = str.charAt(ichStart);
    if (ch != '\'' && ch != '"' && ch != '\1') {
      wasUnQuoted = true;
      while (ich < cch && (ch = str.charAt(ich)) != ' ' && ch != '\t')
        ++ich;
      if (ich == ichStart + 1)
        if (str.charAt(ichStart) == '.' || str.charAt(ichStart) == '?')
          return "\0";
      return str.substring(ichStart, ich);
    }
    wasUnQuoted = false;
    char chOpeningQuote = ch;
    boolean previousCharacterWasQuote = false;
    while (++ich < cch) {
      ch = str.charAt(ich);
      if (previousCharacterWasQuote && (ch == ' ' || ch == '\t'))
        break;
      previousCharacterWasQuote = (ch == chOpeningQuote);
    }
    if (ich == cch) {
      if (previousCharacterWasQuote) // close quote was last char of string
        return str.substring(ichStart + 1, ich - 1);
      // reached the end of the string without finding closing '
      return str.substring(ichStart, ich);
    }
    ++ich; // throw away the last white character
    return str.substring(ichStart + 1, ich - 2);
  }

  /**
   * 
   * first checks to see if the next token is an unquoted
   * control code, and if so, returns null 
   * 
   * @return next data token or null
   * @throws Exception
   */
  public String getNextDataToken() throws Exception { 
    String str = peekToken();
    if (str == null)
      return null;
    if (wasUnQuoted)
      if (str.charAt(0) == '_' || str.startsWith("loop_")
          || str.startsWith("data_")
          || str.startsWith("stop_")
          || str.startsWith("global_"))
        return null;
    return getTokenPeeked();
  }
  
  /**
   * just look at the next token. Saves it for retrieval 
   * using getTokenPeeked()
   * 
   * @return next token or null if EOF
   * @throws Exception
   */
  public String peekToken() throws Exception {
    while (!hasMoreTokens())
      if (setStringNextLine() == null)
        return null;
    int ich = this.ich;
    strPeeked = nextToken();
    ichPeeked= this.ich;
    this.ich = ich;
    return strPeeked;
  }
  
  /**
   * 
   * @return the token last acquired; may be null
   */
  public String getTokenPeeked() {
    ich = ichPeeked;
    return strPeeked;
  }
  
  /**
   * specially for names that might be multiline
   * 
   * @param str
   * @return str without any leading/trailing white space, and no '\n'
   */
  public String fullTrim(String str) {
    int pt0 = 0;
    int pt1 = str.length();
    for (;pt0 < pt1; pt0++)
      if ("\n\t ".indexOf(str.charAt(pt0)) < 0)
        break;
    for (;pt0 < pt1; pt1--)
      if ("\n\t ".indexOf(str.charAt(pt1 - 1)) < 0)
        break;
    return str.substring(pt0, pt1);
  }

  Map<String, Object> data;
  Map<String, Object> allData;
  @SuppressWarnings("unchecked")
  private void getCifLoopData() throws Exception {
    String str;
    List<String> keyWords = new ArrayList<String>();
    while ((str = peekToken()) != null && str.charAt(0) == '_') {
      str  = getTokenPeeked();
      keyWords.add(str);
      data.put(str, new ArrayList<String>());
    }
    fieldCount = keyWords.size();
    if (fieldCount == 0)
      return;
    loopData = new String[fieldCount];
    while (getData()) {
      for (int i = 0; i < fieldCount; i++) {
        ((List<String>)data.get(keyWords.get(i))).add(loopData[i]);
      }
    }
  }  
}