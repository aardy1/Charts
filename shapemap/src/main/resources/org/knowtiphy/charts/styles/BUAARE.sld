<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0"
                       xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd"
                       xmlns="http://www.opengis.net/sld"
                       xmlns:ogc="http://www.opengis.net/ogc"
                       xmlns:xlink="http://www.w3.org/1999/xlink"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <NamedLayer>
        <UserStyle>
            <FeatureTypeStyle>
                <FeatureTypeName>BUAARE</FeatureTypeName>
                <Rule>
                    <!--                    always show cat 1 cities -->
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>CATBUA</ogc:PropertyName>
                            <ogc:Literal>1</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <PolygonSymbolizer>
                        <Fill>
                            <CssParameter name="fill">#b09039</CssParameter>
                        </Fill>
                    </PolygonSymbolizer>
                    <TextSymbolizer>
                        <Label>
                            <ogc:coalesce>
                                <ogc:PropertyName>NOBJNM</ogc:PropertyName>
                                <ogc:PropertyName>OBJNAM</ogc:PropertyName>
                            </ogc:coalesce>
                        </Label>
                        <VendorOption name="group">yes</VendorOption>
                        <VendorOption name="goodnessOfFit">0.1</VendorOption>
                        <VendorOption name="maxDisplacement">200</VendorOption>
                        <Font>
                            <CssParameter name="font-family">Avenir</CssParameter>
                            <CssParameter name="font-Size">16</CssParameter>
                        </Font>
                    </TextSymbolizer>
                </Rule>
                <Rule>
                    <!--                    always show cat 1 cities -->
                    <ogc:Filter>
                        <ogc:PropertyIsNotEqualTo>
                            <ogc:PropertyName>CATBUA</ogc:PropertyName>
                            <ogc:Literal>1</ogc:Literal>
                        </ogc:PropertyIsNotEqualTo>
                    </ogc:Filter>
                    <PolygonSymbolizer>
                        <Fill>
                            <CssParameter name="fill">#b09039</CssParameter>
                        </Fill>
                    </PolygonSymbolizer>
                    <TextSymbolizer>
                        <Label>
                            <ogc:coalesce>
                                <ogc:PropertyName>NOBJNM</ogc:PropertyName>
                                <ogc:PropertyName>OBJNAM</ogc:PropertyName>
                            </ogc:coalesce>
                        </Label>
                        <VendorOption name="group">yes</VendorOption>
                        <VendorOption name="goodnessOfFit">0.1</VendorOption>
                        <VendorOption name="maxDisplacement">200</VendorOption>
                        <Font>
                            <CssParameter name="font-family">Avenir</CssParameter>
                            <CssParameter name="font-Size">14</CssParameter>
                        </Font>
                    </TextSymbolizer>
                </Rule>
            </FeatureTypeStyle>
        </UserStyle>
    </NamedLayer>
</StyledLayerDescriptor>

