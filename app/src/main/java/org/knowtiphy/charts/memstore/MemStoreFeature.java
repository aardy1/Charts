/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.memstore;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.geotools.api.feature.GeometryAttribute;
import org.geotools.api.feature.IllegalAttributeException;
import org.geotools.api.feature.Property;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.feature.type.Name;
import org.geotools.api.filter.identity.FeatureId;
import org.geotools.api.geometry.BoundingBox;
import org.geotools.feature.GeometryAttributeImpl;
import org.locationtech.jts.geom.MultiPoint;

/**
 * @author graham
 */
public class MemStoreFeature implements SimpleFeature {

	private final SimpleFeature feature;

	private final int id;

	private final int index;

	private final GeometryDescriptor geometryDescriptor;

	public MemStoreFeature(SimpleFeature feature, int id, int index, GeometryDescriptor geometryDescriptor) {
		this.feature = feature;
		this.id = id;
		this.index = index;
		this.geometryDescriptor = geometryDescriptor;
	}

	@Override
	public String getID() {
		return getIdentifier().getID();
	}

	@Override
	public SimpleFeatureType getType() {
		throw new UnsupportedOperationException();
		// return feature.getType();
	}

	@Override
	public SimpleFeatureType getFeatureType() {
		throw new UnsupportedOperationException();
		// return feature.getFeatureType();
	}

	@Override
	public List<Object> getAttributes() {
		throw new UnsupportedOperationException();
		// return feature.getAttributes();
	}

	@Override
	public void setAttributes(List<Object> list) {
		throw new UnsupportedOperationException();

		// feature.setAttributes(list);
	}

	@Override
	public void setAttributes(Object[] os) {
		throw new UnsupportedOperationException();
		// feature.setAttributes(os);
	}

	@Override
	public Object getAttribute(String string) {
		if (string.equals("the_geom"))
			throw new UnsupportedOperationException();
		// assert !string.equals("the_geom");
		return feature.getAttribute(string);
	}

	@Override
	public void setAttribute(String string, Object o) {
		throw new UnsupportedOperationException();
		// feature.setAttribute(string, o);
	}

	@Override
	public Object getAttribute(Name name) {
		throw new UnsupportedOperationException();
		// assert !name.getLocalPart().equals("the_geom");
		// return feature.getAttribute(name);
	}

	@Override
	public void setAttribute(Name name, Object o) {
		throw new UnsupportedOperationException();
		// feature.setAttribute(name, o);
	}

	@Override
	public Object getAttribute(int i) throws IndexOutOfBoundsException {
		throw new UnsupportedOperationException();
		// return feature.getAttribute(i);
	}

	@Override
	public void setAttribute(int i, Object o) throws IndexOutOfBoundsException {
		throw new UnsupportedOperationException();
		// feature.setAttribute(i, o);
	}

	@Override
	public int getAttributeCount() {
		throw new UnsupportedOperationException();
		// return feature.getAttributeCount();
	}

	@Override
	public Object getDefaultGeometry() {
		var res = ((MultiPoint) feature.getDefaultGeometry()).getGeometryN(index);
		assert res.getGeometryType().equals("Point");
		return res;
	}

	@Override
	public void setDefaultGeometry(Object o) {
		throw new UnsupportedOperationException();
		// feature.setDefaultGeometry(o);
	}

	@Override
	public FeatureId getIdentifier() {
		throw new UnsupportedOperationException();
		// return new FeatureIdImpl(feature.getID() + "_" + id);//
		// feature.getIdentifier();
	}

	@Override
	public BoundingBox getBounds() {
		throw new UnsupportedOperationException();
		// return feature.getBounds();
		// return ((Geometry) getDefaultGeometry()).get

	}

	@Override
	public GeometryAttribute getDefaultGeometryProperty() {
		return new GeometryAttributeImpl(getDefaultGeometry(), geometryDescriptor, null);
	}

	@Override
	public void setDefaultGeometryProperty(GeometryAttribute ga) {
		throw new UnsupportedOperationException();
		// feature.setDefaultGeometryProperty(ga);
	}

	@Override
	public void setValue(Collection<Property> clctn) {
		throw new UnsupportedOperationException();
		// feature.setValue(clctn);
	}

	@Override
	public Collection<? extends Property> getValue() {
		throw new UnsupportedOperationException();
		// return feature.getValue();
	}

	@Override
	public Collection<Property> getProperties(Name name) {
		throw new UnsupportedOperationException();
		// return feature.getProperties(name);
	}

	@Override
	public Property getProperty(Name name) {
		throw new UnsupportedOperationException();
		// return feature.getProperty(name);
	}

	@Override
	public Collection<Property> getProperties(String string) {
		throw new UnsupportedOperationException();
		// return feature.getProperties(string);
	}

	@Override
	public Collection<Property> getProperties() {
		throw new UnsupportedOperationException();
		// return feature.getProperties();
	}

	@Override
	public Property getProperty(String string) {
		if (string.equals("the_geom")) {
			return getDefaultGeometryProperty();
		}

		return feature.getProperty(string);
	}

	@Override
	public void validate() throws IllegalAttributeException {
		feature.validate();
	}

	@Override
	public AttributeDescriptor getDescriptor() {
		throw new UnsupportedOperationException();
		// return feature.getDescriptor();
	}

	@Override
	public void setValue(Object o) {
		throw new UnsupportedOperationException();
		// feature.setValue(o);
	}

	@Override
	public Name getName() {
		return feature.getName();
	}

	@Override
	public boolean isNillable() {
		throw new UnsupportedOperationException();
		// return feature.isNillable();
	}

	@Override
	public Map<Object, Object> getUserData() {
		return feature.getUserData();
	}

	@Override
	public boolean hasUserData() {
		return feature.hasUserData();
	}

	@Override
	public int hashCode() {
		// return feature.hashCode();
		return getID().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof MemStoreFeature msf && getID().equals(msf.getID());
		// return feature.equals(obj);
	}

	@Override
	public String toString() {
		return getID() + " : " + getDefaultGeometry();
	}

}
