<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor 
    version="1.1.0"
    xmlns="http://www.opengis.net/sld"
    xmlns:se="http://www.opengis.net/se"
    xmlns:ogc="http://www.opengis.net/ogc"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.1.0/StyledLayerDescriptor.xsd">
    <NamedLayer>
        <UserStyle>
            <FeatureTypeStyle>
                <FeatureTypeName>CANALS</FeatureTypeName>
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
                            <CssParameter name="fill">#4BA4EB</CssParameter>
                        </Fill>
                    </PolygonSymbolizer>
                    <TextSymbolizer>
                        <Label>
                            <ogc:coalesce>
                                <ogc:PropertyName>NOBJNM</ogc:PropertyName>
                                <ogc:PropertyName>OBJNAM</ogc:PropertyName>
                            </ogc:coalesce>
                        </Label>
                    </TextSymbolizer>
                </Rule>
                <Rule>
                    <ElseFilter/>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#4BA4EB</CssParameter>
                            <CssParameter name="stroke-width">1</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                    <TextSymbolizer>
                        <Label>
                            <ogc:coalesce>
                                <ogc:PropertyName>NOBJNM</ogc:PropertyName>
                                <ogc:PropertyName>OBJNAM</ogc:PropertyName>
                            </ogc:coalesce>
                        </Label>
                    </TextSymbolizer>
                </Rule>
            </FeatureTypeStyle>
        </UserStyle>
    </NamedLayer>
</StyledLayerDescriptor>

