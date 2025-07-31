/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.enc;

import org.geotools.api.feature.simple.SimpleFeatureType;
import org.knowtiphy.charts.memstore.MemFeature;
import org.knowtiphy.charts.settings.AppSettings;
import org.knowtiphy.shapemap.api.IFeatureFunction;
import org.knowtiphy.shapemap.api.IStyleCompiler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author graham
 */
public class StyleCompiler implements IStyleCompiler<MemFeature> {

    private static final Map<
                    String,
                    BiFunction<
                            AppSettings,
                            IFeatureFunction<MemFeature, Object>,
                            IFeatureFunction<MemFeature, Object>>>
            UNIT_MAP = new HashMap<>();

    static {
        UNIT_MAP.put("knotsToMapUnits", StyleCompiler::knotsToMapUnits);
        UNIT_MAP.put("depthToMapUnits", StyleCompiler::depthToMapUnits);
    }

    private final SimpleFeatureType featureType;

    private final AppSettings settings;

    public StyleCompiler(SimpleFeatureType featureType, AppSettings settings) {
        this.featureType = featureType;
        this.settings = settings;
    }

    @Override
    public IFeatureFunction<MemFeature, Object> compilePropertyAccess(String propertyName) {
        var index1 = featureType.indexOf(propertyName);
        return (f, g) -> f.getAttribute(index1);
    }

    @Override
    public IFeatureFunction<MemFeature, Object> compileFunctionCall(
            String name, Collection<IFeatureFunction<MemFeature, Object>> args) {
        var function = UNIT_MAP.get(name);
        return function.apply(settings, args.iterator().next());
    }

    private static IFeatureFunction<MemFeature, Object> knotsToMapUnits(
            AppSettings settings, IFeatureFunction<MemFeature, Object> quantity) {
        return (f, g) -> {
            var value = quantity.apply(f, g);
            return value == null
                    ? null
                    : settings.unitProfile()
                            .formatSpeed(
                                    (Number) quantity.apply(f, g),
                                    settings.unitProfile()::knotsToMapUnits);
        };
    }

    private static IFeatureFunction<MemFeature, Object> depthToMapUnits(
            AppSettings settings, IFeatureFunction<MemFeature, Object> quantity) {
        return (f, g) -> {
            var value = quantity.apply(f, g);
            return value == null
                    ? null
                    : settings.unitProfile()
                            .formatDepth(
                                    (Number) quantity.apply(f, g),
                                    settings.unitProfile()::depthToMapUnits);
        };
    }
}