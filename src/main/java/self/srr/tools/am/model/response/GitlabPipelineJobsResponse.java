package self.srr.tools.am.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GitlabPipelineJobsResponse {

    private boolean bizStatus = false;

    List<GitlabJobResponse> jobLst = new ArrayList<>(0);


}
