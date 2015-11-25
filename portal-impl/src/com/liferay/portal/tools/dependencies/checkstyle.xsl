<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:java="http://xml.apache.org/xslt/java" exclude-result-prefixes="java">
<xsl:param name="team-name"/>
<xsl:template match="/">
  <html>
    <head>
    <title>Liferay Class Javadoc Metrics</title>
    </head>
    <body bgcolor="#FFFFFF">
    <p><b>Liferay Class Javadoc Results</b></p>
    <table border="1" cellspacing="0" cellpadding="2">
	<tr bgcolor="#CBCBFF">
	    <th colspan="2"><b><xsl:value-of select="$team-name"/> Summary</b></th>
	</tr>
	<tr bgcolor="#FFFFFF">
	    <td>Classes checked</td>
	    <td><xsl:number level="any" value="count(descendant::file)"/></td>
	</tr>
	<tr bgcolor="#FFFFFF">
	    <td>Classes with description</td>
	    <td><xsl:number level="any" value="count(descendant::file) - count(descendant::file[error])"/></td>
	</tr>
	<tr bgcolor="#FFFFFF">
	    <td>Percent documented</td>
	    <td><xsl:value-of select='format-number((count(descendant::file) - count(descendant::file[error])) div count(descendant::file), "##.##%")'/></td>
	</tr>
	<tr bgcolor="#FFFFFF">
	    <td>Generation Date</td>
	    <td>
	    <dateTimeStamp>
            <xsl:value-of select="java:format(java:java.text.SimpleDateFormat.new('dd-MMM-yyyy HH:mm a z'), java:java.util.Date.new())" />
        </dateTimeStamp>
        </td>
	</tr>
    </table>
    <hr align="left" width="95%" size="1"/>
    <p>The following Liferay <xsl:value-of select="$team-name"/> classes are missing a Javadoc class description:</p>
    <p/>
	<xsl:apply-templates/>
    </body>
  </html>
</xsl:template>

<xsl:template match="file[error]">
    <table bgcolor="#EAEAFF" width="95%" border="1" cellspacing="0" cellpadding="2">
	<xsl:variable name="filePath" select="substring-after(@name, substring-before(@name, 'com'))" />
	<xsl:variable name="uniformDashFilePath" select="translate($filePath, '/', '\')" />
	<tr>
	    <th> File: </th>
	    <td>
		<xsl:value-of select="translate($uniformDashFilePath, '\', '.')" />
	    </td>
	</tr>
    </table>
    <table bgcolor="#FFFFFF" width="95%" border="1" cellspacing="0" cellpadding="2">
	<tr>
	    <th> Line Number </th>
	    <th> Error Message </th>
	</tr>
	<xsl:apply-templates select="error"/>
    </table>
    <p/>
</xsl:template>

<xsl:template match="error">
    <tr>
	<td>
	    <xsl:value-of select="@line"/>
	</td>
	<td>
	    <xsl:value-of select="@message"/>
	</td>
    </tr>
</xsl:template>

</xsl:stylesheet>