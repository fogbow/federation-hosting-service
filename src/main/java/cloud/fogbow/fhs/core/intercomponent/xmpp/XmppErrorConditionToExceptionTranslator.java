package cloud.fogbow.fhs.core.intercomponent.xmpp;

import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

import cloud.fogbow.common.exceptions.CommunicationErrorException;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InstanceNotFoundException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.common.exceptions.NotImplementedOperationException;
import cloud.fogbow.common.exceptions.UnacceptableOperationException;
import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import cloud.fogbow.common.exceptions.UnauthorizedRequestException;
import cloud.fogbow.common.exceptions.UnavailableProviderException;
import cloud.fogbow.fhs.constants.Messages;

public class XmppErrorConditionToExceptionTranslator {

    public static void handleError(IQ response, String providerId) throws FogbowException {
        if (response == null) {
            throw new UnavailableProviderException(String.format(Messages.Exception.UNABLE_TO_RETRIEVE_RESPONSE_FROM_PROVIDER_S, providerId));
        } else if (response.getError() != null) {
            PacketError.Condition condition = response.getError().getCondition();
            String message = response.getError().getText();
            XmppErrorConditionToExceptionTranslator.throwException(condition, message);
        }
    }

    private static void throwException(PacketError.Condition condition, String message) throws FogbowException {
        switch (condition) {
            case forbidden:
                throw new UnauthorizedRequestException(message);
            case not_authorized:
                throw new UnauthenticatedUserException(message);
            case bad_request:
                throw new InvalidParameterException(message);
            case item_not_found:
                throw new InstanceNotFoundException(message);
            case not_acceptable:
                throw new UnacceptableOperationException(message);
            case remote_server_not_found:
                throw new UnavailableProviderException(message);
            case conflict:
                throw new ConfigurationErrorException(message);
            case internal_server_error:
                throw new InternalServerErrorException(message);
            case feature_not_implemented:
                throw new NotImplementedOperationException(message);
            case undefined_condition:
                throw new FogbowException(message);
            default:
                throw new CommunicationErrorException(message);
        }
    }
}
