package self.srr.tools.am.model.response;

import lombok.Data;

@Data
public class GitlabJobResponse {

    private String id;

    private String status;

    private String stage;

    private String name;

    private String ref;

}
