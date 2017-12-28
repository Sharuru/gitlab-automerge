package self.srr.tools.am.response;

import lombok.Data;

@Data
public class GitlabPipelineResponse {

    private boolean bizStatus = false;

    private int id;

    private String status;

    private String ref;

}
