<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" doctype-public="-//W3C//DTD XHTML 1.1//EN" doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"/>

   <xsl:template match="/">
      <xsl:comment>Auto-generated</xsl:comment>
      <xsl:apply-templates/>
   </xsl:template>

   <xsl:template match="element">
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
      <tr><td>Atomic number</td><td><xsl:value-of select="atomic-number"/></td></tr>
      <tr><td>Mass number</td><td><xsl:value-of select="mass-number"/></td></tr>
      <tr><td>Electronegativity</td><td><xsl:value-of select="electronegativity"/></td></tr>
      <tr><td>Covalent radius</td><td><xsl:value-of select="covalent-radius"/></td></tr>
      <tr><td>Atomic radius</td><td><xsl:value-of select="atomic-radius"/></td></tr>
      <tr><td>Melting point</td><td><xsl:value-of select="melting-point"/></td></tr>
      <tr><td>Boiling point</td><td><xsl:value-of select="boiling-point"/></td></tr>
      <tr><td>Density</td><td><xsl:value-of select="density"/></td></tr>
      <tr><td>Specific heat</td><td><xsl:value-of select="specific-heat"/></td></tr>
      <tr><td>Electrical conductivity</td><td><xsl:value-of select="electrical-conductivity"/></td></tr>
      <tr><td>Thermal conductivity</td><td><xsl:value-of select="thermal-conductivity"/></td></tr>
      </table>
      </div>
      </body>
      </html>
   </xsl:template>
</xsl:stylesheet>
