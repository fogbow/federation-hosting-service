# Federation Hosting Service

The Federation Hosting Service is responsible for providing federation functionalities, allowing management of federation members and federated services.

## How to use

In this section the installation explanation will be facing a possible contributor. If you are interested in deployment, please take a look at [fogbow-deploy](https://github.com/fogbow/fogbow-deploy).

### Dependencies

- Java 8
- Maven
- [Common module](https://github.com/fogbow/common/), which is a dependency for most fogbow service.
- [Authentication Service](https://github.com/fogbow/authentication-service/), a fogbow service for authentication.
- [Resource Allocation Service](https://github.com/fogbow/resource-allocation-service/), a fogbow service for resource management.

### Installing

First of all, create a directory to organize all fogbow modules/services then clone the required repositories.

```bash
mkdir fogbow && cd fogbow
git clone https://github.com/fogbow/common.git
git clone https://github.com/fogbow/authentication-service.git
git clone https://github.com/fogbow/resource-allocation-service.git
git clone https://github.com/fogbow/federation-hosting-service.git

cd common
git checkout develop && mvn install -DskipTests

cd ../authentication-service
git checkout develop && mvn install -DskipTests

cd ../resource-allocation-service
git checkout develop && mvn install -DskipTests

cd ../federation-hosting-service
git checkout develop && mvn install -DskipTests
```

### Configuration

This service requires some initial configuration. Most of them will have a template for help you to get started.

First of all, you need to create a directory named  _private_  at `src/main/resources`, it will be holding your private settings.

#### private/private.key, private/public.key

```bash
openssl genrsa -out keypair.pem 2048
openssl rsa -in keypair.pem -pubout -out public.key
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.key
rm keypair.pem
```

After the keys (private.key and public.key) are created, you must put them in `src/main/resources/private/` folder.

#### private/fhs.conf

Check out `src/main/resources/templates/fhs.conf` for a file template.

**Example:**

```conf
# FHS configurations
provider_id=my-provider-id

# FHS public/private keys
# Required
public_key_file_path=src/main/resources/private/public.key
# Required
private_key_file_path=src/main/resources/private/private.key

# The authorization plugin used by the FHS
# Required
authorization_plugin_class=cloud.fogbow.fhs.core.plugins.authorization.FhsOperatorAuthorizationPlugin

# FHS communication configurations
communication_mechanism_class_name=cloud.fogbow.fhs.core.intercomponent.xmpp.XmppCommunicationMechanism
synchronization_mechanism_class_name=cloud.fogbow.fhs.core.intercomponent.synchronization.TimeBasedSynchronizationMechanism
```

### Starting the service

1. Start your IDE (IntelliJ, Eclipse, etc);
2. Open the Federation Hosting Service (FHS) project;
3. Add/import common, authentication service and resource allocation service as module in the FHS project;
4. Run the RAS (if necessary) and FHS application.

### Optional tools

- Postman, for REST requests.
