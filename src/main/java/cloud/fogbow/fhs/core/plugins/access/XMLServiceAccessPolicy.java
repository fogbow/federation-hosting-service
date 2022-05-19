package cloud.fogbow.fhs.core.plugins.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;
import org.springframework.data.util.Pair;

import com.google.common.annotations.VisibleForTesting;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.ConfigurationErrorException;
import cloud.fogbow.common.util.XMLUtils;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.ServiceOperation;

public class XMLServiceAccessPolicy implements ServiceAccessPolicy {
    private Map<String, AccessLevel> accessLevels;
    private Map<Pair<String, AccessLevel>, Map<String, String>> credentialsByCloudAndAccessLevel;
    
    /*
       Policy String format
     
       <?xml version=\"1.0\"?> 
       <policy> 
           <accesslevel>
               <name></name>
               <operations>
                   <operation>
                       <method></method>
                       <path></path>
                   </operation>
               </operations>
               
               <attributes> 
                   <attribute></attribute>
               </attributes>
          
               <clouds> 
                   <cloud> 
                       <name></name>
                       <credentials>
                           <credential>
                               <key></key>
                               <value></value>
                           </credential>
                       </credentials>
                   </cloud>
               </clouds>
           </accesslevel>
       </policy>
     */
    
    public XMLServiceAccessPolicy(String policyString) throws ConfigurationErrorException {
        Element root = XMLUtils.getRootNodeFromXMLString(policyString);
        
        this.accessLevels = new HashMap<>();
        this.credentialsByCloudAndAccessLevel = new HashMap<>();
        
        readAccessLevels(root);
    }
    
    private void readAccessLevels(Element root) {
        List<Element> accessLevels = root.getChildren();
        
        for (Element accessLevelElement : accessLevels) {
            readAccessLevel(accessLevelElement);
        }
    }

    private void readAccessLevel(Element accessLevelElement) {
        String accessLevelName = accessLevelElement.getChild("name").getText();
        
        List<ServiceOperation> allowedOperations = readAllowedOperations(accessLevelElement.getChild("operations").getChildren());
        AccessLevel accessLevel = new AccessLevel(accessLevelName, allowedOperations);
        
        List<String> attributes = readAttributes(accessLevelElement.getChild("attributes").getChildren());
        
        for (String attribute : attributes) {
            this.accessLevels.put(attribute, accessLevel);
        }
        
        readCloudCredentials(accessLevelElement.getChild("clouds").getChildren(), accessLevel);
    }

    private void readCloudCredentials(List<Element> cloudsElements, AccessLevel accessLevel) {
        for (Element cloudElement : cloudsElements) {
            String cloudName = cloudElement.getChild("name").getText();
            
            Map<String, String> credentialMap = new HashMap<String, String>();
                    
            for (Element credentialElement : cloudElement.getChild("credentials").getChildren()) {
                String credentialKey = credentialElement.getChild("key").getText();
                String credentialValue = credentialElement.getChild("value").getText();
                
                credentialMap.put(credentialKey, credentialValue);
            }
            
            this.credentialsByCloudAndAccessLevel.put(Pair.of(cloudName, accessLevel), credentialMap);
        }
    }

    private List<ServiceOperation> readAllowedOperations(List<Element> operationsElementsList) {
        List<ServiceOperation> allowedOperations = new ArrayList<ServiceOperation>();
        
        for (Element operationElement : operationsElementsList) {
            String methodString = operationElement.getChild("method").getText();
            String pathString = operationElement.getChild("path").getText();
            
            HttpMethod method = HttpMethod.valueOf(methodString);
            
            ServiceOperation operation = new ServiceOperation(method);
            
            allowedOperations.add(operation);
        }
        
        return allowedOperations;
    }
    
    private List<String> readAttributes(List<Element> attributesElements) {
        List<String> attributes = new ArrayList<String>();
        
        for (Element attributeElement : attributesElements) {
            attributes.add(attributeElement.getText());
        }
        
        return attributes;
    }

    @Override
    public Map<String, String> getCredentialsForAccess(FederationUser user, String cloudName) {
        List<String> userAttributes = user.getAttributes();
        // FIXME should check if user has attributes
        AccessLevel level = accessLevels.get(userAttributes.get(0));
        // FIXME should check if access level is not null and try again if so
        return this.credentialsByCloudAndAccessLevel.get(Pair.of(cloudName, level));
    }

    @Override
    public boolean isAllowedToPerform(FederationUser user, ServiceOperation operation) {
        List<String> userAttributes = user.getAttributes();
        
        for (String userAttribute : userAttributes) {
            AccessLevel level = accessLevels.get(userAttribute); 
            
            if (level != null && level.getAllowedOperations().contains(operation)) {
                return true;
            }
        }
            
        return false;
    }
    
    @VisibleForTesting
    Map<String, AccessLevel> getAccessLevels() {
        return accessLevels;
    }

    @VisibleForTesting
    Map<Pair<String, AccessLevel>, Map<String, String>> getCredentialsByCloudAndAccessLevel() {
        return credentialsByCloudAndAccessLevel;
    }
}
