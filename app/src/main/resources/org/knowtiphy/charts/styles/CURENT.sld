<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version = "1.1.0.2"
                       xsi:schemaLocation = "http://www.opengis.net/sld StyledLayerDescriptor.xsd"
                       xmlns = "http://www.opengis.net/sld"
                       xmlns:ogc = "http://www.opengis.net/ogc"
                       xmlns:xsi = "http://www.w3.org/2001/XMLSchema-instance">
  <NamedLayer>
    <UserStyle>
      <FeatureTypeStyle>
        <FeatureTypeName>CURENT</FeatureTypeName>
        <Rule>
          <PointSymbolizer>
            <Graphic>
              <Mark>
                <!--                                <WellKnownName>extshape://arrow?hr=4&amp;ab=0.8</WellKnownName>-->
                <WellKnownName>s52::Arrow</WellKnownName>
                <Fill>
                  <CssParameter name = "fill">#A8B9BD</CssParameter>
                </Fill>
              </Mark>
              <Rotation>
                <PropertyName>ORIENT</PropertyName>
              </Rotation>
              <Opacity>0.5</Opacity>
              <Size>8</Size>
            </Graphic>
            <VendorOption name = "labelObstacle">true</VendorOption>
          </PointSymbolizer>
          <TextSymbolizer>
            <Label>
              <ogc:Function name = "knotsToMapUnit">
                <ogc:PropertyName>CURVEL</ogc:PropertyName>
              </ogc:Function>
            </Label>
            <VendorOption name = "group">yes</VendorOption>
            <VendorOption name = "goodnessOfFit">0.1</VendorOption>
            <VendorOption name = "maxDisplacement">200</VendorOption>
            <Font>
              <CssParameter name = "font-Size">12</CssParameter>
            </Font>
          </TextSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>