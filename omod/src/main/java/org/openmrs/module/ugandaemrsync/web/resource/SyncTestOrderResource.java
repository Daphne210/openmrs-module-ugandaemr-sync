package org.openmrs.module.ugandaemrsync.web.resource;

import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRHttpURLConnection;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.web.resource.DTO.SyncTestOrderSync;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.validation.ValidateUtil;

import java.util.*;

@Resource(name = RestConstants.VERSION_1 + "/syncTestOrder", supportedClass = SyncTestOrderSync.class, supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class SyncTestOrderResource extends DelegatingCrudResource<SyncTestOrderSync> {

    @Override
    public SyncTestOrderSync newDelegate() {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public SyncTestOrderSync save(SyncTestOrderSync TestResult) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
        // Retrieve required services once
        UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
        UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);
        SyncTestOrderSync delegate = new SyncTestOrderSync();
        List<Map> responses=new ArrayList<>();
        List<Order> orderList=new ArrayList<>();

        if (!ugandaEMRHttpURLConnection.isConnectionAvailable()) {
            Map<String, String> response = new HashMap<String, String>();
            response.put("responseMessage", "No Internet Connection to send order to CPHL");
            responses.add(response);
        }else {

            // Extract required properties
            List orderUuids = propertiesToCreate.get("orders");

            for (Object orderUuid : orderUuids) {
                Order order = Context.getOrderService().getOrderByUuid(orderUuid.toString());
                responses.add(ugandaEMRSyncService.sendSingleViralLoadOrder(order));
                orderList.add(order);
            }

            delegate.setOrderList(orderList);
            delegate.setResponseList(responses);
        }

        // Validate result
        ValidateUtil.validate(delegate);

        // Convert result to the required representation
        SimpleObject response = (SimpleObject) ConversionUtil.convertToRepresentation(delegate, context.getRepresentation());

        // Add type if necessary
        if (hasTypesDefined()) {
            response.add(RestConstants.PROPERTY_FOR_TYPE, getTypeName(delegate));
        }

        return response;
    }

    @Override
    public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public SyncTestOrderSync getByUniqueId(String uniqueId) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public NeedsPaging<SyncTestOrderSync> doGetAll(RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public List<Representation> getAvailableRepresentations() {
        return Arrays.asList(Representation.DEFAULT, Representation.FULL);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("orderList");
            description.addProperty("responseList");
            description.addSelfLink();
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("orderList", Representation.REF);
            description.addProperty("responseList", Representation.REF);
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof RefRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("orderList", Representation.REF);
            description.addProperty("responseList", Representation.REF);
            description.addSelfLink();
            return description;
        }
        return null;
    }

    @Override
    protected void delete(SyncTestOrderSync TestResult, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public void purge(SyncTestOrderSync TestResult, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("visit");
        return description;
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }
}
