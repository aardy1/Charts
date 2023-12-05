/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package shapemap.style.parser;

/**
 * @author graham
 */
public class XML {

	// elements of styles

	public static final String STYLED_LAYER_DESCRIPTOR = "styledlayerdescriptor";

	public static final String USER_STYLE = "userstyle";

	public static final String NAMED_LAYER = "namedlayer";

	public static final String FEATURE_TYPE_STYLE = "featuretypestyle";

	public static final String FEATURE_TYPE_NAME = "featuretypename";

	public static final String RULE = "rule";

	public static final String ELSE_FILTER = "elsefilter";

	public static final String POINT_SYMBOLIZER = "pointsymbolizer";

	public static final String LINE_SYMBOLIZER = "linesymbolizer";

	public static final String POLYGON_SYMBOLIZER = "polygonsymbolizer";

	public static final String TEXT_SYMBOLIZER = "textsymbolizer";

	public static final String STROKE = "stroke";

	public static final String FILL = "fill";

	public static final String FONT = "font";

	public static final String FILTER = "filter";

	public static final String VENDOR_OPTION = "vendoroption";

	public static final String GRAPHIC = "graphic";

	public static final String MARK = "mark";

	public static final String WELL_KNOWN_NAME = "wellknownname";

	public static final String SIZE = "size";

	public static final String OPACITY = "opacity";

	public static final String ROTATION = "rotation";

	public static final String CSS_PARAMETER = "cssparameter";

	public static final String LABEL = "label";

	public static final String LABEL_PLACEMENT = "labelplacement";

	public static final String POINT_PLACEMENT = "pointplacement";

	public static final String DISPLACEMENT = "displacement";

	public static final String DISPLACEMENTX = "displacementx";

	public static final String DISPLACEMENTY = "displacementy";

	public static final String ANCHOR_POINT = "anchorpoint";

	public static final String ANCHOR_POINTX = "anchorpointx";

	public static final String ANCHOR_POINTY = "anchorpointy";

	// CSS attributes

	public static final String CSS_FILL = "fill";

	public static final String CSS_FILL_OPACITY = "fill-opacity";

	public static final String CSS_STROKE = "stroke";

	public static final String CSS_STROKE_WIDTH = "stroke-width";

	public static final String CSS_STROKE_OPACITY = "stroke-opacity";

	public static final String CSS_FONT_FAMILY = "font-family";

	public static final String CSS_FONT_WEIGHT = "font-weight";

	public static final String CSS_FONT_STYLE = "font-style";

	public static final String CSS_FONT_SIZE = "font-size";

	// general expressions

	public static final String LITERAL = "literal";

	public static final String PROPERTY_NAME = "propertyname";

	public static final String LT = "propertyislessthan";

	public static final String GT = "greaterthan";

	public static final String NE = "propertyisnotequalto";

	public static final String EQ = "propertyisequalto";

	public static final String IS_LIKE = "islike";

	public static final String COALESCE = "coalesce";

	public static final String FUNCTION = "function";

	public static final String ATTR_NAME = "name";

}
