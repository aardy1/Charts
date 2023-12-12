<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version = "1.0.0"
                       xsi:schemaLocation = "http://www.opengis.net/sld StyledLayerDescriptor.xsd"
                       xmlns = "http://www.opengis.net/sld"
                       xmlns:ogc = "http://www.opengis.net/ogc"
                       xmlns:xsi = "http://www.w3.org/2001/XMLSchema-instance">
  <NamedLayer>
    <UserStyle>
      <FeatureTypeStyle>
        <FeatureTypeName>LAKARE</FeatureTypeName>
        <Rule>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name = "fill">#4BA4EB</CssParameter>
            </Fill>
          </PolygonSymbolizer>
          <LineSymbolizer>
            <Stroke>
              <CssParameter name = "stroke">#4BA4EB</CssParameter>
            </Stroke>
          </LineSymbolizer>
          <TextSymbolizer>
            <Label>
              <ogc:coalesce>
                <ogc:PropertyName>NOBJNM</ogc:PropertyName>
                <ogc:PropertyName>OBJNAM</ogc:PropertyName>
              </ogc:coalesce>
            </Label>
            <VendorOption name = "group">yes</VendorOption>
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