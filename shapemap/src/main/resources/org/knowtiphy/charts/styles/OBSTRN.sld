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
                <FeatureTypeName>OBSTRN</FeatureTypeName>
                <Rule>
                    <PointSymbolizer>
                        <Graphic>
                            <Mark>
                                <WellKnownName>square</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#000000</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>6</Size>
                        </Graphic>
                    </PointSymbolizer>
                    <TextSymbolizer>
                        <Label>
                            <ogc:coalesce>
                                <ogc:PropertyName>NOBJNM</ogc:PropertyName>
                                <ogc:PropertyName>OBJNAM</ogc:PropertyName>
                            </ogc:coalesce>
                        </Label>
                        <Font>
                            <CssParameter name="font-family">Avenir</CssParameter>
                            <CssParameter name="font-Size">14</CssParameter>
                        </Font>
                        <LabelPlacement>
                            <PointPlacement>
                                <AnchorPoint>
                                    <AnchorPointX>0</AnchorPointX>
                                    <AnchorPointY>0.5</AnchorPointY>
                                </AnchorPoint>
                                <Displacement>
                                    <DisplacementX>7</DisplacementX>
                                    <DisplacementY>0</DisplacementY>
                                </Displacement>
                            </PointPlacement>
                        </LabelPlacement>
                    </TextSymbolizer>
                </Rule>
            </FeatureTypeStyle>
        </UserStyle>
    </NamedLayer>
</StyledLayerDescriptor>

