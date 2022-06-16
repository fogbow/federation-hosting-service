package cloud.fogbow.fhs.core.intercomponent.xmpp;

public enum IqElement {
    QUERY("query"), 
    FEDERATION_LIST("federationList"), 
    FEDERATION_LIST_CLASS_NAME("federationListClassName");
    
    private final String element;

    IqElement(final String elementName) {
        this.element = elementName;
    }

    @Override
    public String toString() {
        return element;
    }
}
