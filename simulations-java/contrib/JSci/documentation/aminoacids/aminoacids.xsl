<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" doctype-public="-//W3C//DTD XHTML 1.1//EN" doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"/>

   <xsl:template match="/">
      <xsl:comment>Auto-generated</xsl:comment>
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="amino-acid">
      <html xml:lang="en">
      <head>
      <title><xsl:value-of select="name"/></title>
      <style type="text/css">
      .left {text-align: left}
      .center {text-align: center}
      .right {text-align: right}
      td {text-align: left}
      </style>
      </head>
      <body>
      <h1 class="center"><xsl:value-of select="name"/> (<xsl:value-of select="symbol"/>)</h1>
      <div class="center">
      <table>
      <tr><td>Abbreviation</td><td><xsl:value-of select="abbreviation"/></td></tr>
      <tr><td>Molecular formula</td><td><xsl:value-of select="molecular-formula"/></td></tr>
      <tr><td>Molecular weight</td><td><xsl:value-of select="molecular-weight"/></td></tr>
      <tr><td>Isoelectric point</td><td><xsl:value-of select="isoelectric-point"/></td></tr>
      <tr><td>CAS registry number</td><td><xsl:value-of select="CAS-registry-number"/></td></tr>
      </table>
      </div>
      </body>
      </html>
   </xsl:template>
</xsl:stylesheet>
