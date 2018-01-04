package self.srr.tools.am.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import self.srr.tools.am.common.AMConfig;
import self.srr.tools.am.response.DeployTaskResponse;
import self.srr.tools.am.response.GitlabPipelineResponse;
import self.srr.tools.am.response.MergeTaskResponse;

@Slf4j
@Service
public class DeployService {

    @Autowired
    AMConfig AMConfig;

    @Autowired
    GitlabApiService gitlabApiService;

    @Autowired
    MattermostApiService mattermostApiComp;

    @Autowired
    MergeService mergeService;


    public DeployTaskResponse deploy(boolean isStep1, boolean needMerge) {

        DeployTaskResponse response = new DeployTaskResponse();

        String taskName = isStep1 ? "STEP 1" : "STEP 2";
        log.info(taskName + " deploy task started.");

        String refTarget = isStep1 ? "feature/enhancement" : "feature/ita";

        if (needMerge) {
            // phase1. merge
            try {
                MergeTaskResponse mergeTaskResponse = mergeService.mergeTwoBranches(AMConfig.getGitlab().getSourceBranch(), refTarget);
                if (!mergeTaskResponse.isStatus()) {
                    response.setMsg(mergeTaskResponse.getMessage());
                    return response;
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                response.setMsg("ERR_MERGE_EXCEPTION");
                return response;
            }
        }

        // phase2. trigger
        try {
            GitlabPipelineResponse gitlabPipelineResponse = gitlabApiService.triggerPipe(refTarget);
            if (refTarget.equalsIgnoreCase(gitlabPipelineResponse.getRef())) {
                response.setBizStatus(true);
                response.setReturnCode(0);
                response.setPipelineId(String.valueOf(gitlabPipelineResponse.getId()));
                String pipeLink = "<a href=\"" + AMConfig.getGitlab().getPublicProjectPage() + "/pipelines/" + gitlabPipelineResponse.getId() + "\">前往 PIPELINE 页面</a>";
                response.setMsg(pipeLink);
            } else {
                response.setMsg("ERR_PIPE_BROKE");
                return response;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setMsg("ERR_PIPE_EXCEPTION");
            return response;
        }

        return response;

    }


}
