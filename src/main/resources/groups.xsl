<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:p="http://javaops.ru">
    <xsl:output method="html" omit-xml-declaration="yes" indent="yes"/>
    <xsl:param name="projectName"/>
    <xsl:template match="/">
    <html>
        <head>
            <title>
                <xsl:value-of select="$projectName"/> groups</title>
        </head>
        <body>
            <h2>
                <xsl:value-of select="$projectName"/> groups</h2>
            <table border="1" cellpadding="8" cellspacing="0">
                <tr>
                    <th>GroupName</th>
                    <th>Type</th>
                </tr>
                <xsl:for-each select="/p:Payload/p:Projects/p:Project[@projectName=$projectName]/p:Group">
                    <tr>
                        <td><xsl:value-of select="@groupName"/></td>
                        <td><xsl:value-of select="@type"/></td>
                    </tr>
                </xsl:for-each>
            </table>
        </body>
    </html>
    </xsl:template>
</xsl:stylesheet>