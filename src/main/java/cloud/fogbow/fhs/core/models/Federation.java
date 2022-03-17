package cloud.fogbow.fhs.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Federation {
    private String id;
    private String owner;
    private String name;
    private boolean enabled;
    private List<FederationUser> members;
    
    public Federation(String owner, String name, boolean enabled) {
        this.id = UUID.randomUUID().toString();
        this.owner = owner;
        this.name = name;
        this.enabled = enabled;
        this.members = new ArrayList<FederationUser>();
    }

    public FederationUser addUser(String userId) {
        FederationUser newMember = new FederationUser(userId, "", "", true);
        this.members.add(newMember);
        return newMember;
    }

    public FederationUser getUser(String memberId) {
        for (FederationUser member : members) {
            if (member.getMemberId().equals(memberId)) {
                return member;
            }
        }
        
        // FIXME should throw exception
        return null;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean enabled() {
        return enabled;
    }

    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<FederationUser> getMemberList() {
        return members;
    }
}
