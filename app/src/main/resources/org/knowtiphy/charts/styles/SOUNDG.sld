<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version = "1.0.0"
                       xsi:schemaLocation = "http://www.opengis.net/sld StyledLayerDescriptor.xsd"
                       xmlns = "http://www.opengis.net/sld"
                       xmlns:ogc = "http://www.opengis.net/ogc"
                       xmlns:xsi = "http://www.w3.org/2001/XMLSchema-instance">
    <NamedLayer>
        <UserStyle>
            <FeatureTypeStyle>
                <FeatureTypeName>SOUNDG</FeatureTypeName>
                <Rule>
                    <TextSymbolizer>
                        <Label>
                            <ogc:Function name = "depthToMapUnits">
                                <ogc:Function name = "getZ">
                                    <ogc:PropertyName>the_geom</ogc:PropertyName>
                                </ogc:Function>
                            </ogc:Function>
                        </Label>
                        <Font>
                            <CssParameter name = "font-Size">9</CssParameter>
                        </Font>
                        <Fill>
                            <!--                            <CssParameter name = "fill">#91a3b0</CssParameter>-->
                            <CssParameter name = "fill">#2d2d2d</CssParameter>
                        </Fill>
                    </TextSymbolizer>
                </Rule>
            </FeatureTypeStyle>
        </UserStyle>
    </NamedLayer>
</StyledLayerDescriptor>