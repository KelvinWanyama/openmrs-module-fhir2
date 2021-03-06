/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.providers;

import javax.validation.constraints.NotNull;

import java.util.List;

import ca.uhn.fhir.rest.annotation.History;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceOrListParam;
import ca.uhn.fhir.rest.param.StringOrListParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Resource;
import org.openmrs.module.fhir2.api.FhirLocationService;
import org.openmrs.module.fhir2.util.FhirServerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("fhirResources")
@Setter(AccessLevel.PACKAGE)
public class LocationFhirResourceProvider implements IResourceProvider {
	
	@Autowired
	FhirLocationService fhirLocationService;
	
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Location.class;
	}
	
	@Read
	@SuppressWarnings("unused")
	public Location getLocationById(@IdParam @NotNull IdType id) {
		Location location = fhirLocationService.getLocationByUuid(id.getIdPart());
		if (location == null) {
			throw new ResourceNotFoundException("Could not find location with Id " + id.getIdPart());
		}
		return location;
	}
	
	@History
	@SuppressWarnings("unused")
	public List<Resource> getLocationHistoryById(@IdParam @NotNull IdType id) {
		Location location = fhirLocationService.getLocationByUuid(id.getIdPart());
		if (location == null) {
			throw new ResourceNotFoundException("Could not find location with Id " + id.getIdPart());
		}
		return location.getContained();
	}
	
	@Search
	public Bundle searchLocations(@OptionalParam(name = Location.SP_NAME) StringOrListParam name,
	        @OptionalParam(name = Location.SP_ADDRESS_CITY) StringOrListParam city,
	        @OptionalParam(name = Location.SP_ADDRESS_COUNTRY) StringOrListParam country,
	        @OptionalParam(name = Location.SP_ADDRESS_POSTALCODE) StringOrListParam postalCode,
	        @OptionalParam(name = Location.SP_ADDRESS_STATE) StringOrListParam state,
	        @OptionalParam(name = "_tag") TokenOrListParam tag,
	        @OptionalParam(name = Location.SP_PARTOF) ReferenceOrListParam parent, @Sort SortSpec sort) {
		return FhirServerUtils.convertSearchResultsToBundle(
		    fhirLocationService.searchForLocations(name, city, country, postalCode, state, tag, parent, sort));
	}
}
