<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version = "1.0.0"
                       xsi:schemaLocation = "http://www.opengis.net/sld StyledLayerDescriptor.xsd"
                       xmlns = "http://www.opengis.net/sld"
                       xmlns:ogc = "http://www.opengis.net/ogc"
                       xmlns:xsi = "http://www.w3.org/2001/XMLSchema-instance">
    <NamedLayer>
        <UserStyle>
            <FeatureTypeStyle>
                <FeatureTypeName>DEPCNT</FeatureTypeName>
                <Rule>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name = "stroke">#808080</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                    <TextSymbolizer>
                        <Label>
                            <ogc:Function name = "depthToMapUnits">
                                <ogc:PropertyName>VALDCO</ogc:PropertyName>
                            </ogc:Function>
                        </Label>
                        <Font>
                            <CssParameter name = "font-Size">12</CssParameter>
                        </Font>
                        <VendorOption name = "group">yes</VendorOption>
                        <VendorOption name = "goodnessOfFit">0.1</VendorOption>
                        <VendorOption name = "maxDisplacement">200</VendorOption>
                    </TextSymbolizer>
                </Rule>
                <VendorOption name = "composite-base">true</VendorOption>
            </FeatureTypeStyle>
        </UserStyle>
    </NamedLayer>
</StyledLayerDescriptor>