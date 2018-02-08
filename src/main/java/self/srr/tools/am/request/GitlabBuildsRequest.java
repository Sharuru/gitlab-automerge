package self.srr.tools.am.request;

import lombok.Data;

@Data
public class GitlabBuildsRequest {

    private String id;

    private String stage;

    private String name;

    private String status;

    private String created_at;

    private String started_at;

    private String finished_at;

}
