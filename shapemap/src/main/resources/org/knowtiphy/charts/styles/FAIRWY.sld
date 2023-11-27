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
                <FeatureTypeName>FAIRWY</FeatureTypeName>
                <Rule>
                    <ogc:Filter>
                        <ogc:IsLike>
                            <ogc:Function name="geometryType">
                                <ogc:PropertyName>the_geom</ogc:PropertyName>
                            </ogc:Function>
                            <ogc:Literal>Polygon|MultiPolygon</ogc:Literal>
                        </ogc:IsLike>
                    </ogc:Filter>
                    <PolygonSymbolizer>
                        <Fill>
                            <CssParameter name="fill">#7CFC00</CssParameter>
                        </Fill>
                    </PolygonSymbolizer>
                </Rule>
                <Rule>
                    <ElseFilter/>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#90EE90</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                </Rule>
                <VendorOption name="composite-base">true</VendorOption>
            </FeatureTypeStyle>
        </UserStyle>
    </NamedLayer>
</StyledLayerDescriptor>

