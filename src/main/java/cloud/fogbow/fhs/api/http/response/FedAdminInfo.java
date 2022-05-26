package cloud.fogbow.fhs.api.http.response;

import java.util.List;

public class FedAdminInfo {
    private String memberId;
    private String memberName;
    private String email;
    private String description;
    private Boolean enabled;
    private List<String> fedsOwned;
    
    public FedAdminInfo() {
        
    }
    
    public FedAdminInfo(String memberId, String memberName, String email, String description, Boolean enabled,
            List<String> fedsOwned) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.email = email;
        this.description = description;
        this.enabled = enabled;
        this.fedsOwned = fedsOwned;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public List<String> getFedsOwned() {
        return fedsOwned;
    }
}
