package self.srr.tools.am.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import self.srr.tools.am.api.GitlabApiComp;
import self.srr.tools.am.api.MattermostApiComp;
import self.srr.tools.am.common.AMConfig;
import self.srr.tools.am.response.GitlabAPIResponse;
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
    @Scheduled(cron = "0 */15 9-19 * * MON-FRI")
    public void MergeTaskScheduleCaller() throws Exception {
        task("GAM Schedule Back-front");
    }

    public MergeTaskResponse task(String from) throws Exception {

        MergeTaskResponse response = new MergeTaskResponse();

        log.info("MergeTask started.");

        // create MR
        GitlabAPIResponse createMRResponse = gitlabApiComp.createMR(from);

        if (createMRResponse.getStatusCode() == 201) {
            TimeUnit.SECONDS.sleep(5L);
            // accept MR
            GitlabAPIResponse acceptMRResponse = gitlabApiComp.acceptMR(createMRResponse.getIid());
            if (acceptMRResponse.getStatusCode() == 200 && "can_be_merged".equalsIgnoreCase(acceptMRResponse.getMergeStatus())) {
                log.info("MR: " + acceptMRResponse.getIid() + " is merged successfully.");
                response.setStatus(true);
                response.setMessage("更新完成，请前往 pipeline 页面查看编译状态。");
            } else {
                log.error("MR: " + acceptMRResponse.getIid() + " can not be merged automatically.");
                mattermostApiComp.sendPost("MergeRequest: " + acceptMRResponse.getIid() + "自动合并失败了。");
                response.setStatus(false);
                response.setMessage("更新失败，无法自动合并，请联系管理员。");
            }
        } else {
            log.error("Can not create new MR.");
            mattermostApiComp.sendPost("无法创建新的 MR，前序 MR 未解决？");
            response.setStatus(false);
            response.setMessage("更新失败，有前序任务阻碍，请联系管理员。");
        }

        return response;

    }
}
