package self.srr.tools.am.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import self.srr.tools.am.common.AMConfig;
import self.srr.tools.am.response.DeployTaskResponse;
import self.srr.tools.am.response.GitlabMRListResponse;
import self.srr.tools.am.response.GitlabMRResponse;
import self.srr.tools.am.response.MergeTaskResponse;

import java.util.concurrent.TimeUnit;

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


    public DeployTaskResponse deploy(boolean isStep1) {

        DeployTaskResponse response = new DeployTaskResponse();

        String taskName = isStep1 ? "STEP 1" : "STEP 2";
        log.info(taskName + " deploy task started.");

        String refTarget = isStep1 ? "feature/enhancement" : "feature/ita";

        // phase1. merge
        try {
            MergeTaskResponse mergeTaskResponse = mergeService.mergeTwoBranchs(AMConfig.getGitlab().getSourceBranch(), refTarget);
            if (!mergeTaskResponse.isStatus()) {
                response.setMsg("通知发布失败，请联系管理员。参考信息：" + mergeTaskResponse.getMessage());
                return response;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setMsg("通知发布失败，请联系管理员。参考信息：ERR_MERGE_EXCEPTION");
            return response;
        }
        // phase2. trigger


        return response;

    }


}
}
