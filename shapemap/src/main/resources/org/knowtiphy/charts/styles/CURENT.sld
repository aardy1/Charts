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
                <FeatureTypeName>CURENT</FeatureTypeName>
                <Rule>
                    <!--                    <PointSymbolizer>
                                 <Graphic>
                                                 <Mark>
                                <WellKnownName>extshape://arrow?hr=4&amp;ab=0.8</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#A8B9BD</CssParameter>
                                </Fill>
                            </Mark>
                            <Rotation>
                                <PropertyName>ORIENT</PropertyName>
                            </Rotation>
                            <Size>16</Size>
                        </Graphic>
                        <VendorOption name= "labelObstacle">true</VendorOption>
                    </PointSymbolizer>-->
                    <TextSymbolizer>
                        <Label>
                            <ogc:PropertyName>CURVEL</ogc:PropertyName>
                        </Label>
                        <VendorOption name="group">yes</VendorOption>
                        <VendorOption name="goodnessOfFit">0.1</VendorOption>
                        <VendorOption name="maxDisplacement">200</VendorOption>
                        <Font>
                            <CssParameter name="font-family">Avenir</CssParameter>
                            <CssParameter name="font-Size">12</CssParameter>
                        </Font>
                    </TextSymbolizer>
                </Rule>
            </FeatureTypeStyle>
        </UserStyle>
    </NamedLayer>
</StyledLayerDescriptor>

