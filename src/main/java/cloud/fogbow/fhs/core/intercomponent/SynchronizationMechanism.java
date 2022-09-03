package cloud.fogbow.fhs.core.intercomponent;

/**
 * Abstraction of an entity responsible for ensuring federation data across multiple FHS instances
 * is consistent.
 */
public interface SynchronizationMechanism {
    /**
     * Sets the given FhsCommunicationMechanism as the object to use to establish communication
     * with other FHS instances.
     * 
     * @param communicationMechanism the mechanism to use
     */
    void setCommunicationMechanism(FhsCommunicationMechanism communicationMechanism);
    /**
     * Performs the required tasks to ensure federation synchronization on FHS startup.
     * <strong>MUST</strong> be called before local and remote facades are set up and working. 
     * 
     * @throws Exception if some error occurs while starting up
     */
    void onStartUp() throws Exception;
    /**
     * Registers the given local update on a federation to be synchronized between connected FHSs.
     * 
     * @param updatedFederation the local update
     */
    void onLocalUpdate(FederationUpdate updatedFederation);
    /**
     * Registers the given update, received from another FHS, to be synchronized between connected FHSs.
     * 
     * @param updatedFederation the remote update
     */
    void onRemoteUpdate(FederationUpdate updatedFederation);
}
