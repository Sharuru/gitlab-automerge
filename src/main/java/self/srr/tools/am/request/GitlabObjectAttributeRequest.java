package self.srr.tools.am.request;

import lombok.Data;

@Data
public class GitlabObjectAttributeRequest {

    private String id;

    private String ref;

    private String status;

}
