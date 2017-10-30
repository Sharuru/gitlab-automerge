package self.srr.tools.am.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "am")
@Data
public class AMConfig {

    private String name;

    private Gitlab gitlab;

    private Key key;

    @Data
    public static class Gitlab {
        private String url;
        private String privateToken;
        private String projectPage;
        private String projectId;
        private String sourceBranch;
        private String targetBranch;
    }


    @Data
    public static class Key {

        private String gitlab;

        private String mattermost;
    }
}
