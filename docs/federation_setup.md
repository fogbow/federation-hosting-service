# Basic federation setup

This document presents the main operations used to properly set up a federation instance in the Federation Hosting Service. First, an FHS Operator must add a new federation admin. Then, the federation admin creates a new federation. After that, it grants membership to the federation to a certain user. This new user, a federation member, can be granted the ServiceOwner role. Then, it can register a new service in the federation.

## FHS-Operator authentication
The authentication operations require a public key to encrypt the authentication token. 
Therefore, first, we get the public key from the FHS by accessing the public key endpoint `FHS_URL/fhs/publicKey`.

Then, we acquire an authentication token for the FHS-Operator by accessing the FHS-Operator login endpoint `FHS_URL/fhs/FHSOperator/Login` using the headers and body described as follows.

```
type: POST

headers = {
	"Content-Type": "application/json"
}

body = {
    "operatorId": operator_id,
    "credentials": {
        "userPublicKey": fhs_public_key,
        "username": operator.username,
        "password": operator.password 
    }
}

response = {
    "token":token_str
}
```

## Federation admin creation
After acquiring the FHS-Operator token, we must then add a new federation admin by using the federation admin creation endpoint `FHS_URL/fhs/FHSOperator/NewFedAdmin` using the headers and body described as follows. The request returns the ID of the created federation admin, which we must keep for future use.

```
type: POST

headers = {
    "Content-Type": "application/json",
    "Fogbow-User-Token": token
}

body = {
    "name": name,
    "email": email,
    "description": description,
    "enabled": enabled,
    "authenticationProperties": {
                "identityPluginClassName": "cloud.fogbow.fhs.core.plugins.authentication.LdapFederationAuthenticationPlugin"
    }            
}

response = {
    "memberId": federation_admin_id
}
```

## Federation admin authentication
Before making requests as federation admin, first we must authenticate and acquire a token. 
As federation admin, we must use the endpoint `FHS_URL/fhs/MemberLogin/FedAdmin` using the headers and body described as follows.

```
type: POST

headers = {
    "Content-Type": "application/json"
}

body = {
    "federationAdminId": federation_admin_id,
    "credentials": {
            "userPublicKey": fhs_public_key,
            "username": federation_admin_name,
            "password": federation_admin_password
    }
}

response = {
    "token":token_str
}
```

## Federation creation
With the federation admin token, we can create a new federation by using the endpoint `FHS_URL/fhs/Federation` using the headers and body described as follows. The request returns the ID of the created federation, which we must keep for future use.

```
type: POST

headers = {
    "Content-Type": "application/json",
    "Fogbow-User-Token": federation_admin_token
}

body = {
    "name": name,
    "metadata": {},
    "description": description,
    "enabled": enabled
}

response = {
    "id": federation_id
}
```

## Granting membership
After the federation creation, the next step is populate it, granting federation membership to users.
For this purpose, we use the grant membership endpoint `FHS_URL/fhs/Membership/federation_id` using the headers and body described as follows. The request returns the ID of the new member, which we must keep for future use.

```
type: POST

headers = {
    "Content-Type": "application/json",
    "Fogbow-User-Token": federation_admin_token
}

body = {
    "name": username,
    "authenticationProperties": {"identityPluginClassName": "cloud.fogbow.fhs.core.plugins.authentication.LdapFederationAuthenticationPlugin"},
    "email": email,
    "description": description,
    "enabled": enabled
}

response = {
    "memberId": member_id
}
```

## Granting service owner role
This new member can be granted the ServiceOwner role.
This role attribution is performed by making a request to the endpoint `FHS_URL/fhs/Authorization/federation_id/member_id/serviceOwner` using the headers and body described as follows.
                        
```
type: PUT

headers = {
    "Content-Type": "application/json",
    "Fogbow-User-Token": federation_admin_token
}

body = {}

response = status code
```

## Getting service owner token
Before registering services, the new service owner must authenticate and acquire a new token.
The authentication endpoint for the common federation users is `FHS_URL/fhs/MemberLogin`.

```
type: POST

headers = {
    "Content-Type": "application/json"
}

body = {
    "federationId": federation_id,
    "memberId": member_id,
    "credentials": {
        "userPublicKey": fhs_public_key,
        "username": username,
        "password": password
    }
}

response = {
    "token":token_str
}
```

## Registering services
The new service owner can, then, register services in the federation, by making requests to the endpoint `FHS_URL/fhs/Services/federation_id`. The request returns the ID of the registered service, which we must keep for future use.

```
type: POST

headers = {
    "Content-Type": "application/json",
    "Fogbow-User-Token": service_owner_token
}

body = {
    "ownerId": service_owner_id,
    "endpoint": endpoint,
    "discoveryPolicy": discovery_policy_class_name,
    "accessPolicy": access_policy_class_name,
    "metadata": {
        "servicePublicKeyEndpoint": service_public_key_endpoint,
        "invokerClassName": service_invoker_class_name,
        "accessPolicyRules": service_access_policy_rules
    }
}

response = {
    "serviceId": service_id
}
```
