This program is a utility to help us visualize and annotate images, sound and data files used in phet simulations for purposes of copyright and licensing.
These are for internal use only; for putting information in the About->Credits dialog, see build-process, such as PhetProject.copyLicenseInfo.

To start, launch edu.colorado.phet.licensing.DependencyReport with an arg of the absolute path to trunk, or modify your working copy of Config to contain this information.
(May also require your working directory be set to simulations-java).

To annotate resources used in a simulation, place a license.txt in each subdirectory (recursively) of the sim's data directory; it should have an entry for each file in that directory (see glaciers example below). 

Here is a supported grammar defined by AnnotationParser:
resource file = (line \n)*
line = comment | annotation
comment = #.*
annotation = id (attribute )*
attribute = key=value
key = ch*
id = ch*
value = ch*
ch = any character except for equals signs and newlines (but includes whitespace)

For a simple example of usage of this grammar, see the sample main in AnnotationParser

For license.txt in particular, the id must be a filename in the same directory as the license.txt file.
The supported keys for the license.txt processing system are: source,author,license,notes,same,licensefile
These are identified in ResourceAnnotation.

Here are some examples from glaciers license.txt

bear.png source=http://openclipart.org/people/lemmling/lemmling_Cartoon_bear.svg author=lemmling license=http://creativecommons.org
boreholeDrill.png source=http://openclipart.org author=Machovka license=http://creativecommons.org
boreholeDrillOnButton.png source=PhET author=pixelzoom
boreholeDrillOffButton.png source=PhET author=pixelzoom
glacialBudgetMeter.png source=PhET author=Archie Paulson

For additional discussion, see #1908 in Unfuddle.  Let me know if you have questions or comments.

Sam Reid
8-17-2006
updated 12-5-2008
updated 11-25-2009