<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--    <xsl:output method="html" omit-xml-declaration="yes" indent="no"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/*[name()='Payload']/*[name()='Users']/*[name()='User']"">
        <xsl:copy-of select="."/>
        <xsl:text>&#xa;</xsl:text>
    </xsl:template>
    <xsl:template match="text()"/> -->
    <xsl:template match="/">
    <html>
        <body>
            <h2>Groups</h2>
            <table border="1">
                <tr>
                    <th>GroupName</th>
                    <th>Type</th>
                </tr>
                <xsl:for-each select="/*[name()='Payload']/*[name()='Projects']/*[name()='Project']/*[name()='Group']">
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