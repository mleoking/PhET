<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
 exclude-result-prefixes="xlink"
 xmlns:xlink="http://www.w3.org/1999/xlink"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" doctype-public="-//W3C//DTD XHTML 1.1//EN" doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"/>
   <xsl:variable name="title">Periodic table reference</xsl:variable>

   <xsl:template match="/">
      <xsl:comment>Auto-generated</xsl:comment>
      <html xml:lang="en">
      <head>
      <title><xsl:value-of select="$title"/></title>
      <style type="text/css">
      .center {text-align: center}
      </style>
      </head>
      <body>
      <h1 class="center"><xsl:value-of select="$title"/></h1>
      <ul>
      <xsl:apply-templates/>
      </ul>
      </body>
      </html>
   </xsl:template>

   <xsl:template match="element">
      <li><a href="{substring-before(@xlink:href,'.xml')}.html"><xsl:value-of select="@name"/></a> (<xsl:value-of select="@symbol"/>)</li>
   </xsl:template>
</xsl:stylesheet>
