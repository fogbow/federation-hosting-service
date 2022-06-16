package cloud.fogbow.fhs.core.intercomponent.xmpp.requesters;

import cloud.fogbow.common.exceptions.FogbowException;

public interface RemoteRequest<T> {

    T send() throws FogbowException;
}
