<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version = "1.0.0"
                       xsi:schemaLocation = "http://www.opengis.net/sld StyledLayerDescriptor.xsd"
                       xmlns = "http://www.opengis.net/sld"
                       xmlns:ogc = "http://www.opengis.net/ogc"
                       xmlns:xsi = "http://www.w3.org/2001/XMLSchema-instance">
  <NamedLayer>
    <UserStyle>
      <FeatureTypeStyle>
        <FeatureTypeName>BCNSAW</FeatureTypeName>
        <Rule>
          <PointSymbolizer>
            <Graphic>
              <Mark>
                <WellKnownName>s52:Beacon</WellKnownName>
                <Stroke>
                  <CssParameter name = "stroke">#FF0000</CssParameter>
                </Stroke>
              </Mark>
              <Size>8</Size>
            </Graphic>
            <!--          <VendorOption name= "labelObstacle">true</VendorOption>-->
          </PointSymbolizer>
          <!--          <LineSymbolizer>-->
          <!--            <Stroke>-->
          <!--              <CssParameter name = "stroke">#FFFF00</CssParameter>-->
          <!--              <CssParameter name = "stroke-width">40</CssParameter>-->
          <!--            </Stroke>-->
          <!--            &lt;!&ndash;          <VendorOption name= "labelObstacle">true</VendorOption>&ndash;&gt;-->
          <!--          </LineSymbolizer>-->
          <TextSymbolizer>
            <Label>
              <ogc:coalesce>
                <ogc:PropertyName>NOBJNM</ogc:PropertyName>
                <ogc:PropertyName>OBJNAM</ogc:PropertyName>
              </ogc:coalesce>
            </Label>
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