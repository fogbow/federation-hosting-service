package cloud.fogbow.fhs.core.plugins.access;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.util.Pair;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.fhs.core.models.FederationUser;
import cloud.fogbow.fhs.core.models.ServiceOperation;

public class DefaultServiceAccessPolicyTest {
    private static final String CREDENTIAL_NAME_1 = "credential_name_1";
    private static final String CREDENTIAL_NAME_2 = "credential_name_2";
    private static final String CREDENTIAL_NAME_3 = "credential_name_3";
    private static final String CREDENTIAL_NAME_4 = "credential_name_4";
    private static final String CREDENTIAL_NAME_5 = "credential_name_5";
    private static final String CREDENTIAL_NAME_6 = "credential_name_6";
    private static final String CREDENTIAL_NAME_7 = "credential_name_7";
    private static final String CREDENTIAL_NAME_8 = "credential_name_8";
    private static final String CREDENTIAL_VALUE_1 = "credential_value_1";
    private static final String CREDENTIAL_VALUE_2 = "credential_value_2";
    private static final String CREDENTIAL_VALUE_3 = "credential_value_3";
    private static final String CREDENTIAL_VALUE_4 = "credential_value_4";
    private static final String CREDENTIAL_VALUE_5 = "credential_value_5";
    private static final String CREDENTIAL_VALUE_6 = "credential_value_6";
    private static final String CREDENTIAL_VALUE_7 = "credential_value_7";
    private static final String CREDENTIAL_VALUE_8 = "credential_value_8";
    private static final String CLOUD_NAME_1 = "cloud1";
    private static final String CLOUD_NAME_2 = "cloud2";
    private static final String ATTRIBUTE_0 = "attribute_0";
    private static final String ATTRIBUTE_1 = "attribute_1";
    private static final String ATTRIBUTE_2 = "attribute_2";
    private static final String GET_METHOD_STRING = HttpMethod.GET.getName();
    private static final String POST_METHOD_STRING = HttpMethod.POST.getName();
    private static final String ACCESS_LEVEL_1_NAME = "access_level_1";
    private static final String ACCESS_LEVEL_2_NAME = "access_level_2";
    private static final String PATH_OPERATION = "path_0";

    private DefaultServiceAccessPolicy accessPolicy;
    private String accessLevelString1;
    private String accessLevelString2;
    private String accessLevelString;
    private HashMap<String, String> expectedCredentialsCloud1ForLevel1;
    private HashMap<String, String> expectedCredentialsCloud2ForLevel1;
    private HashMap<String, String> expectedCredentialsCloud1ForLevel2;
    private HashMap<String, String> expectedCredentialsCloud2ForLevel2;
    private FederationUser user1;
    private FederationUser user2;
    private FederationUser user3;
    private Map<String, String> mapOptions1;
    private Map<String, String> mapOptions2;

    @Before
    public void setUp() {
        this.accessLevelString1 = 
                String.format("%s:", ACCESS_LEVEL_1_NAME) +
                String.format("%s,%s~%s,%s:", GET_METHOD_STRING, PATH_OPERATION, POST_METHOD_STRING, PATH_OPERATION) + 
                String.format("%s:", ATTRIBUTE_1) + 
                String.format("%s=%s#%s~%s#%s,", CLOUD_NAME_1, CREDENTIAL_NAME_1, CREDENTIAL_VALUE_1, 
                        CREDENTIAL_NAME_2, CREDENTIAL_VALUE_2) + 
                String.format("%s=%s#%s~%s#%s", CLOUD_NAME_2, CREDENTIAL_NAME_3, CREDENTIAL_VALUE_3, 
                        CREDENTIAL_NAME_4, CREDENTIAL_VALUE_4);
        
        this.accessLevelString2 = 
                String.format("%s:", ACCESS_LEVEL_2_NAME) +
                String.format("%s,%s:", GET_METHOD_STRING, PATH_OPERATION) + 
                String.format("%s:", ATTRIBUTE_0) + 
                String.format("%s=%s#%s~%s#%s,", CLOUD_NAME_1, CREDENTIAL_NAME_5, CREDENTIAL_VALUE_5, 
                        CREDENTIAL_NAME_6, CREDENTIAL_VALUE_6) + 
                String.format("%s=%s#%s~%s#%s", CLOUD_NAME_2, CREDENTIAL_NAME_7, CREDENTIAL_VALUE_7, 
                        CREDENTIAL_NAME_8, CREDENTIAL_VALUE_8);
        
        this.accessLevelString = accessLevelString1 + ";" + accessLevelString2;
        
        this.expectedCredentialsCloud1ForLevel1 = new HashMap<String, String>();
        this.expectedCredentialsCloud2ForLevel1 = new HashMap<String, String>();
        
        this.expectedCredentialsCloud1ForLevel1.put(CREDENTIAL_NAME_1, CREDENTIAL_VALUE_1);
        this.expectedCredentialsCloud1ForLevel1.put(CREDENTIAL_NAME_2, CREDENTIAL_VALUE_2);
        
        this.expectedCredentialsCloud2ForLevel1.put(CREDENTIAL_NAME_3, CREDENTIAL_VALUE_3);
        this.expectedCredentialsCloud2ForLevel1.put(CREDENTIAL_NAME_4, CREDENTIAL_VALUE_4);
        
        this.expectedCredentialsCloud1ForLevel2 = new HashMap<String, String>();
        this.expectedCredentialsCloud2ForLevel2 = new HashMap<String, String>();
        
        this.expectedCredentialsCloud1ForLevel2.put(CREDENTIAL_NAME_5, CREDENTIAL_VALUE_5);
        this.expectedCredentialsCloud1ForLevel2.put(CREDENTIAL_NAME_6, CREDENTIAL_VALUE_6);
        
        this.expectedCredentialsCloud2ForLevel2.put(CREDENTIAL_NAME_7, CREDENTIAL_VALUE_7);
        this.expectedCredentialsCloud2ForLevel2.put(CREDENTIAL_NAME_8, CREDENTIAL_VALUE_8);
    
        this.user1 = Mockito.mock(FederationUser.class);
        Mockito.when(this.user1.getAttributes()).thenReturn(Arrays.asList(new String[]{ATTRIBUTE_0}));
        this.user2 = Mockito.mock(FederationUser.class);
        Mockito.when(this.user2.getAttributes()).thenReturn(Arrays.asList(new String[]{ATTRIBUTE_1}));
        this.user3 = Mockito.mock(FederationUser.class);
        Mockito.when(this.user3.getAttributes()).thenReturn(Arrays.asList(new String[]{ATTRIBUTE_2}));
        
        this.mapOptions1 = new HashMap<String, String>();
        this.mapOptions1.put("cloudName", CLOUD_NAME_1);
        
        this.mapOptions2 = new HashMap<String, String>();
        this.mapOptions2.put("cloudName", CLOUD_NAME_2);
    }
    
    @Test
    public void testConstructor() {
        this.accessPolicy = new DefaultServiceAccessPolicy(accessLevelString);
        
        List<ServiceOperation> expectedOperationsAccessLevel1 = new ArrayList<ServiceOperation>();
        expectedOperationsAccessLevel1.add(new ServiceOperation(HttpMethod.GET));
        expectedOperationsAccessLevel1.add(new ServiceOperation(HttpMethod.POST));
        
        List<ServiceOperation> expectedOperationsAccessLevel2 = new ArrayList<ServiceOperation>();
        expectedOperationsAccessLevel2.add(new ServiceOperation(HttpMethod.GET));
        
        Map<String, AccessLevel> accessLevels = this.accessPolicy.getAccessLevels();
        assertEquals(2, accessLevels.size());
        assertEquals(new AccessLevel(ACCESS_LEVEL_1_NAME, expectedOperationsAccessLevel1), accessLevels.get(ATTRIBUTE_1));
        assertEquals(new AccessLevel(ACCESS_LEVEL_2_NAME, expectedOperationsAccessLevel2), accessLevels.get(ATTRIBUTE_0));

        Map<Pair<String, AccessLevel>, Map<String, String>> credentials = this.accessPolicy.getCredentialsByCloudAndAccessLevel();
        assertEquals(4, credentials.size());
        assertEquals(expectedCredentialsCloud1ForLevel1, credentials.get(Pair.of(CLOUD_NAME_1, new AccessLevel(ACCESS_LEVEL_1_NAME, expectedOperationsAccessLevel1))));
        assertEquals(expectedCredentialsCloud2ForLevel1, credentials.get(Pair.of(CLOUD_NAME_2, new AccessLevel(ACCESS_LEVEL_1_NAME, expectedOperationsAccessLevel1))));
        assertEquals(expectedCredentialsCloud1ForLevel2, credentials.get(Pair.of(CLOUD_NAME_1, new AccessLevel(ACCESS_LEVEL_2_NAME, expectedOperationsAccessLevel2))));
        assertEquals(expectedCredentialsCloud2ForLevel2, credentials.get(Pair.of(CLOUD_NAME_2, new AccessLevel(ACCESS_LEVEL_2_NAME, expectedOperationsAccessLevel2))));
    }
    
    @Test
    public void testIsAllowedToPerform() {
        this.accessPolicy = new DefaultServiceAccessPolicy(accessLevelString);

        assertTrue(this.accessPolicy.isAllowedToPerform(user1, new ServiceOperation(HttpMethod.GET)));
        assertTrue(this.accessPolicy.isAllowedToPerform(user2, new ServiceOperation(HttpMethod.GET)));
        assertFalse(this.accessPolicy.isAllowedToPerform(user1, new ServiceOperation(HttpMethod.POST)));
        assertTrue(this.accessPolicy.isAllowedToPerform(user2, new ServiceOperation(HttpMethod.POST)));
        assertFalse(this.accessPolicy.isAllowedToPerform(user3, new ServiceOperation(HttpMethod.GET)));
        assertFalse(this.accessPolicy.isAllowedToPerform(user3, new ServiceOperation(HttpMethod.POST)));
    }
    
    @Test
    public void testGetCredentialsForAccess() {
        this.accessPolicy = new DefaultServiceAccessPolicy(accessLevelString);

        Map<String, String> returnedCredentials = this.accessPolicy.getCredentialsForAccess(user2, CLOUD_NAME_1);
        assertEquals(expectedCredentialsCloud1ForLevel1, returnedCredentials);
        
        Map<String, String> returnedCredentials2 = this.accessPolicy.getCredentialsForAccess(user2, CLOUD_NAME_2);
        assertEquals(expectedCredentialsCloud2ForLevel1, returnedCredentials2);
    }
}
