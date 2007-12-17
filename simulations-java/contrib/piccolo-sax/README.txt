-------------------------------------------------------------------------
Piccolo XML Parser for Java
Version 1.04, released July 11, 2004
http://piccolo.sourceforge.net/

Please report bugs to yuval@bluecast.com. Thank you.

(C) Copyright 2002-2004 by Yuval Oren. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

-------------------------------------------------------------------------

Piccolo supports the following specifications:

* SAX 1
* SAX 2 extensions 1.0
* JAXP 1.1 (SAX parsing only)

Note that, per the specifications, namespace handling is turned on by default
with SAX2; when using JAXP, namespace handling is off by default. Turning off
namespace handling will provide a performance gain.

Piccolo requires Java 1.2 or higher.

For usage information, please see the usage documentation available at
http://piccolo.sourceforge.net/using.html .

-------------------------------------------------------------------------
CHANGES from version 1.03:
-------------------------------------------------------------------------
* Character sets natively supported by Java (e.g. Shift_JIS) are now supported
* Piccolo's JAXP will work in applets without giving security exceptions
* All reported bugs should now be fixed:
    - Namespace scope-related bugs
    - Line number reporting should be better now
    - Simple document type (e.g. <!DOCTYPE xyz>) is now recognized
    - Very large CDATA sections are properly handled
    - EntityResolver now works when an InputSource with only a system ID is returned
    - Exceptions are thrown for more invalid SAX2 usage and bad XML
* Performance has been improved by up to 10% over previous versions of Piccolo
* Piccolo attempts to avoid blocking I/O calls, so that it can read from always-open network streams

Many thanks to Eric Vasilik and Cezar Andrei for these enhancements:
- relicensing from LGPL to Apache License Version 2.0
- line info for start elements on the "<".
- added getVersion and getEncoding to the parser
- bug fixing for attribute localname
- added more encodings to the charsetTable


-------------------------------------------------------------------------
CHANGES from version 1.02:
-------------------------------------------------------------------------
This is a maintenance release, correcting the following bugs:

* Turning string interning off would throw the wrong exception
* The ValidatingSAXParserFactory property could not be specified in 
  jaxp.properties
* setLocale() did not throw an exception when set to non-English
* Setting the default namespace within an element already declaring
  a default namespace would cause errors.

-------------------------------------------------------------------------
CHANGES from version 1.01:
-------------------------------------------------------------------------
This is a maintenance release, correcting bugs mostly related to 
error handling:

* If an EntityResolver is registered, it is now correctly called.
* ContentHandler.skippedEntity() is no longer called within attribute values.
* ContentHandler.endDocument() is now called when a fatal error occurs.
* If an error handler is not registered, fatal errors will throw 
  a SAXParseException.
* LexicalHandler.startEntity() is called before the entity is resolved.
* A bug that caused root-level attributes to be ignored sometimes was fixed.
* Very large CDATA sections are now handled efficiently.
* Build and packaging scripts have been enhanced.


-------------------------------------------------------------------------
CHANGES from version 1.0:
-------------------------------------------------------------------------
* A bug causing parsing of "Reader" input sources to fail was fixed.

-------------------------------------------------------------------------
CHANGES from version 0.5:
-------------------------------------------------------------------------

* Detects many more conformance errors
* Namespace processing is faster
* Now supports SAX2 extensions and JAXP
* Bug fixes


