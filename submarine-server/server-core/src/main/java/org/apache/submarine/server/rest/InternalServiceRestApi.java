package org.apache.submarine.server.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.submarine.commons.utils.exception.SubmarineRuntimeException;
import org.apache.submarine.server.api.common.CustomResourceType;
import org.apache.submarine.server.api.environment.Environment;
import org.apache.submarine.server.internal.InternalServiceManager;
import org.apache.submarine.server.response.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * Internal Service REST API v1, providing internal service to sync status of CR.
 * 
 */
@Path(RestConstants.V1 + "/" + RestConstants.INTERNAL)
@Produces("application/json")
public class InternalServiceRestApi {
    
  private static final Logger LOG = LoggerFactory.getLogger(InternalServiceRestApi.class);
  private final InternalServiceManager internalServiceManager = InternalServiceManager.getInstance();
    
  /**
   * Update status of custom resource
   * @param name Name of the environment
   * @param spec environment spec
   * @return the detailed info about updated environment
  */
  @POST
  @Path("/{customResourceType}/{resourceId}/{status}")
  @Consumes({RestConstants.MEDIA_TYPE_YAML, MediaType.APPLICATION_JSON})
  @Operation(summary = "Update the environment with job spec",
          tags = {"environments"},
          responses = {
                  @ApiResponse(description = "successful operation", 
                      content = @Content(
                          schema = @Schema(
                              implementation = String.class))),
                  @ApiResponse(
                      responseCode = "404", 
                      description = "resource not found")})
  public Response updateEnvironment(
      @PathParam(RestConstants.CUSTOM_RESOURCE_TYPE) CustomResourceType type,
      @PathParam(RestConstants.CUSTOM_RESOURCE_ID) String resourceId,
      @PathParam(RestConstants.CUSTOM_RESOURCE_STATUS) String status) {
    try {
      internalServiceManager.updateCRStatus(type, resourceId, status);
      return new JsonResponse.Builder<Environment>(Response.Status.OK)
        .success(true).build();
    } catch (SubmarineRuntimeException e) {
      return new JsonResponse.Builder<String>(e.getCode()).message(e.getMessage())
        .build();
    }
  }
}
