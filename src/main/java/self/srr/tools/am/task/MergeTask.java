package self.srr.tools.am.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import self.srr.tools.am.api.GitlabApiComp;
import self.srr.tools.am.api.MattermostApiComp;
import self.srr.tools.am.common.AMConfig;
import self.srr.tools.am.response.GitlabMRListResponse;
import self.srr.tools.am.response.GitlabMRResponse;
import self.srr.tools.am.response.MergeTaskResponse;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MergeTask {

    @Autowired
    AMConfig AMConfig;

    @Autowired
    GitlabApiComp gitlabApiComp;

    @Autowired
    MattermostApiComp mattermostApiComp;

    // Every 15 minutes during workdays
    @Scheduled(cron = "0 0/15 9-20 * * MON-FRI")
    //@Scheduled(fixedRate = 100000000L)
    public void MergeTaskScheduleCaller() throws Exception {
        task("GAM Schedule Back-front");
    }

    public MergeTaskResponse task(String from) throws Exception {

        MergeTaskResponse response = new MergeTaskResponse();

        log.info("MergeTask started.");

        String mrId = null;

        boolean oldMR = false;

        // check MR
        GitlabMRListResponse listMRResponse = gitlabApiComp.listMR();
        if ((listMRResponse.getMrList().size() > 0) && (!"merged".equalsIgnoreCase(listMRResponse.getMrList().get(0).getState()) &&
                !"closed".equalsIgnoreCase(listMRResponse.getMrList().get(0).getState()))) {
            // Have unclosed or unmerged MR, fetch and try close it
            mrId = listMRResponse.getMrList().get(0).getIid();
            oldMR = true;
        } else {
            // create MR
            GitlabMRResponse createMRResponse = gitlabApiComp.createMR(from);
            if (createMRResponse.getStatusCode() == 201) {
                mrId = createMRResponse.getIid();
                oldMR = false;
            } else {
                log.error("Can not create new MR.");
                mattermostApiComp.sendPost("无法创建新的 MR，前序 MR 未解决？");
                response.setStatus(false);
                response.setMessage("更新失败，有前序任务阻碍，请联系管理员。");
            }
        }

        TimeUnit.SECONDS.sleep(5L);

        // accept MR
        GitlabMRResponse acceptMRResponse = gitlabApiComp.acceptMR(mrId);
        if (acceptMRResponse.getStatusCode() == 200 && "can_be_merged".equalsIgnoreCase(acceptMRResponse.getMergeStatus())) {
            log.info("MR: " + mrId + " is merged successfully.");
            response.setStatus(true);
            response.setMessage("更新完成，请前往 pipeline 页面查看编译状态。");
            if (oldMR) {
                mattermostApiComp.sendPost("MergeRequest: [!" + mrId + "](" + AMConfig.getGitlab().getPublicProjectPage() + "/merge_requests/" + mrId + ") 自动重试成功。");
            }
        }
//        else if("null".equalsIgnoreCase(acceptMRResponse.getMergeCommitSha()) || acceptMRResponse.getMergeCommitSha() == null){
//            log.error("MR: " + mrId + " no further update.");
//            response.setStatus(false);
//            response.setMessage("ITA 分支已是最新状态。");
//        }
        else {
            log.error("MR: " + mrId + " can not be merged automatically.");
            mattermostApiComp.sendPost("MergeRequest: [!" + mrId + "](" + AMConfig.getGitlab().getPublicProjectPage() + "/merge_requests/" + mrId + ") 自动合并失败了。");
            response.setStatus(false);
            response.setMessage("更新失败，分支已是最新状态 / 代码无法自动合并，请联系管理员。");
        }

        return response;

    }
}
