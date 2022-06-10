package cloud.fogbow.fhs.constants;

import java.util.concurrent.TimeUnit;

public class ConfigurationPropertyDefaults {
    public static final String XMPP_CSC_PORT = Integer.toString(5347);;
    public static final String XMPP_TIMEOUT = Long.toString(TimeUnit.SECONDS.toMillis(5));
}
