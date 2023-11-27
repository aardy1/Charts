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
                <FeatureTypeName>LIGHTS</FeatureTypeName>
                <Rule>
                    <PointSymbolizer>
                        <Graphic>
                            <Mark>
                                <WellKnownName>circle</WellKnownName>
                                <Stroke>
                                    <CssParameter name="stroke">#f3d948</CssParameter>
                                    <CssParameter name="stroke-width">3</CssParameter>
                                </Stroke>
                            </Mark>
                            <Size>
                                <ogc:coalesce>
                                    <ogc:PropertyName>VALNMR</ogc:PropertyName>
                                    <ogc:literal>4</ogc:literal>
                                </ogc:coalesce>
                            </Size>
                        </Graphic>

                        <!--   <PointSymbolizer uom="http://www.opengeospatial.org/se/units/metre">
                                <Size>
                                    <ogc:mul>
                                        <ogc:PropertyName>VALNMR</ogc:PropertyName>
                                        <ogc:literal>1852</ogc:literal>
                                    </ogc:mul>
                                </Size>
                            </Graphic>
                            <VendorOption name= "labelObstacle">true</VendorOption>
                        </PointSymbolizer>-->
                    </PointSymbolizer>
                    <TextSymbolizer>
                        <Label>
                            <ogc:coalesce>
                                <ogc:PropertyName>NOBJNM</ogc:PropertyName>
                                <ogc:PropertyName>OBJNAM</ogc:PropertyName>
                            </ogc:coalesce>
                        </Label>
                        <Font>
                            <CssParameter name="font-Size">22</CssParameter>
                        </Font>
                    </TextSymbolizer>
                </Rule>
            </FeatureTypeStyle>
        </UserStyle>
    </NamedLayer>
</StyledLayerDescriptor>