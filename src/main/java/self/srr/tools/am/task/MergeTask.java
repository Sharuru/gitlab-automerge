package self.srr.tools.am.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import self.srr.tools.am.api.GitlabApiComp;
import self.srr.tools.am.api.MattermostApiComp;
import self.srr.tools.am.common.AMConfig;
import self.srr.tools.am.response.GitlabAPIResponse;

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
    public void task() throws Exception {

        log.info("MergeTask started.");

        // create MR
        GitlabAPIResponse createMRResponse = gitlabApiComp.createMR();

        if (createMRResponse.getStatusCode() == 201) {
            TimeUnit.SECONDS.sleep(5L);
            // accept MR
            GitlabAPIResponse acceptMRResponse = gitlabApiComp.acceptMR(createMRResponse.getIid());
            if (acceptMRResponse.getStatusCode() == 200 && "can_be_merged".equalsIgnoreCase(acceptMRResponse.getMergeStatus())) {
                log.info("MR: " + acceptMRResponse.getIid() + " is merged successfully.");
            } else {
                log.error("MR: " + acceptMRResponse.getIid() + " can not be merged automatically.");
                mattermostApiComp.sendPost("MergeRequest: " + acceptMRResponse.getIid() + "自动合并失败了。");
            }
        } else {
            log.error("Can not create new MR.");
            mattermostApiComp.sendPost("无法创建新的 MR，前序 MR 未解决？");
        }

    }
}
