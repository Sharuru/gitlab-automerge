package self.srr.tools.am.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import self.srr.tools.am.common.AMConfig;
import self.srr.tools.am.response.GitlabMRListResponse;
import self.srr.tools.am.response.GitlabMRResponse;
import self.srr.tools.am.response.MergeTaskResponse;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MergeService {

    @Autowired
    GitlabApiService gitlabApiService;

    @Autowired
    MattermostApiService mattermostApiService;

    @Autowired
    AMConfig AMConfig;

    /**
     * Merge two giving branches
     *
     * @param refSource source branch
     * @param refTarget target branch
     * @return merge result
     * @throws Exception exception
     */
    public MergeTaskResponse mergeTwoBranches(String refSource, String refTarget) throws Exception {

        MergeTaskResponse response = new MergeTaskResponse();

        log.info("MergeTask started, merge " + refSource + " into " + refTarget);

        String mrId = null;

        boolean haveUnClosedMR = false;

        // check MR
        GitlabMRListResponse listMRResponse = gitlabApiService.listMR("opened");

        for (GitlabMRResponse aMr : listMRResponse.getMrList()) {
            if (aMr.getTargetBranch().equalsIgnoreCase(refTarget)) {
                // Have unclosed or unmerged MR, fetch and try close it
                mrId = aMr.getIid();
                haveUnClosedMR = true;
                break;
            }
        }

        if (!haveUnClosedMR) {
            // create MR
            GitlabMRResponse gitlabMRResponse = gitlabApiService.createMR(refSource, refTarget);
            if (gitlabMRResponse.getStatusCode() == 201) {
                mrId = gitlabMRResponse.getIid();
            } else {
                log.error("Can not create new MR.");
                response.setStatus(false);
                response.setMessage("ERR_CAN_NOT_CREATE_MR");
                return response;
            }
        }

        TimeUnit.SECONDS.sleep(5L);

        // accept MR
        GitlabMRResponse acceptMRResponse = gitlabApiService.acceptMR(mrId);
        if (acceptMRResponse.getStatusCode() == 200 && "can_be_merged".equalsIgnoreCase(acceptMRResponse.getMergeStatus())) {
            log.info("MR: " + mrId + " is merged successfully.");
            response.setStatus(true);
        } else {
            log.error("MR: " + mrId + " can not be merged automatically.");
            response.setStatus(false);
            response.setMessage("WARN_ALREADY_NEW_OR_CONFLICT");
            mattermostApiService.sendPost("MergeRequest of " + refTarget + ": [!" + mrId + "](" + AMConfig.getGitlab().getPublicProjectPage() + "/merge_requests/" + mrId + ") 自动合并失败了（分支已是最新状态 / 代码无法自动合并）。");
        }

        return response;
    }
}
