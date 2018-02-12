package self.srr.tools.am.model.request;

import lombok.Data;

@Data
public class GitlabObjectAttributeRequest {

    private String id;

    private String ref;

    private String status;

}
