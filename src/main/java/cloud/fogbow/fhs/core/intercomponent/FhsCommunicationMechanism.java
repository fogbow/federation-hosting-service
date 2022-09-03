package cloud.fogbow.fhs.core.intercomponent;

import java.util.List;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.fhs.api.http.response.FederationInstance;
import cloud.fogbow.fhs.core.models.Federation;
import cloud.fogbow.fhs.core.models.FederationUser;

/**
 * Abstraction of an entity responsible for establishing secure communication between FHS instances, 
 * using an underlying communication method.
 */
public interface FhsCommunicationMechanism {
    /**
     * Queries the given FHS on available federations for joining.
     * 
     * @param remoteFhs the ID of the remote FHS to query
     * @return a list of available federations for joining
     * @throws FogbowException if some error occurs while requesting
     */
    List<FederationInstance> getRemoteFederations(String remoteFhs) throws FogbowException;
    /**
     * Queries the given FHS on available federations for joining and updates the given FHS with
     * the local federations available for joining.
     * 
     * @param remoteFhs the ID of the remote FHS to query
     * @param localFederations the local federations available for joining
     * @return a list of available federations for joining
     * @throws FogbowException if some error occurs while requesting
     */
    List<FederationInstance> syncFederations(String remoteFhs, List<FederationInstance> localFederations) throws FogbowException;
    /**
     * Requests the given FHS to add the given user as admin of the given federation.
     * 
     * @param requester the user to add as admin
     * @param federationId the federation to be modified
     * @param remoteFhs the ID of the remote FHS which holds the federation
     * @return the joined federation data
     * @throws FogbowException if some error occurs while requesting
     */
    Federation joinRemoteFederation(FederationUser requester, String federationId, String remoteFhs) throws FogbowException;
    /**
     * Updates a federation's data on the given FHS.
     * 
     * @param remoteFhs the ID of the remote FHS that must receive the update
     * @param update the federation's data update
     * @throws FogbowException if some error occurs while requesting
     */
    void updateFederation(String remoteFhs, FederationUpdate update) throws FogbowException;
}
