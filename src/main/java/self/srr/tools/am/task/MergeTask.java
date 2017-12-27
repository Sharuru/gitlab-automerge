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

    // Everyday at 0920 GMT+8 during weekdays
    @Scheduled(cron = "0 20 9 * * MON-FRI")
    //@Scheduled(fixedRate = 100000000L)
    public void MergeTaskScheduleCaller() throws Exception {
        task(false);
    }

    public MergeTaskResponse task(boolean isWebCall) throws Exception {

        String fromTxt = isWebCall ? "GAM Maintenance Panel" : "GAM Schedule Back-front";
        String shortFromTxt = isWebCall ? "网页请求的任务：" : "后台自动任务：";

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
            if("Created by GAM Maintenance Panel".equalsIgnoreCase(listMRResponse.getMrList().get(0).getTitle())){
                shortFromTxt = "网页请求的任务：";
            }
            oldMR = true;
        } else {
            // create MR
            GitlabMRResponse createMRResponse = gitlabApiComp.createMR(fromTxt);
            if (createMRResponse.getStatusCode() == 201) {
                mrId = createMRResponse.getIid();
                oldMR = false;
            } else {
                log.error("Can not create new MR.");
                mattermostApiComp.sendPost(shortFromTxt + "无法创建新的 MR，前序 MR 未解决？");
                response.setStatus(false);
                response.setMessage("更新失败，前序任务合并中，请稍做等待。");
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
                mattermostApiComp.sendPost(shortFromTxt + "MergeRequest: [!" + mrId + "](" + AMConfig.getGitlab().getPublicProjectPage() + "/merge_requests/" + mrId + ") 自动重试成功。");
            }
        }
//        else if("null".equalsIgnoreCase(acceptMRResponse.getMergeCommitSha()) || acceptMRResponse.getMergeCommitSha() == null){
//            log.error("MR: " + mrId + " no further update.");
//            response.setStatus(false);
//            response.setMessage("ITA 分支已是最新状态。");
//        }
        else {
            log.error("MR: " + mrId + " can not be merged automatically.");
            response.setStatus(false);
            response.setMessage("更新失败，分支已是最新状态 / 代码无法自动合并，请联系管理员。");
            if(!isWebCall){
                mattermostApiComp.sendPost(shortFromTxt + "MergeRequest: [!" + mrId + "](" + AMConfig.getGitlab().getPublicProjectPage() + "/merge_requests/" + mrId + ") 自动合并失败了。");
            }
        }

        return response;

    }
}
