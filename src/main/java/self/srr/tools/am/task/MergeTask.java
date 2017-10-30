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

    @Scheduled(fixedRate = 1000000000)
    public void print() throws Exception {

        // create MR
        GitlabAPIResponse createMRResponse = gitlabApiComp.createMR();
        mattermostApiComp.sendPost("开始干活了！");

        if (createMRResponse.getStatusCode() == 201) {
            TimeUnit.SECONDS.sleep(5L);
            // accept MR
            GitlabAPIResponse acceptMRResponse = gitlabApiComp.acceptMR(createMRResponse.getIid());
            if (acceptMRResponse.getStatusCode() == 200 && "can_be_merged".equalsIgnoreCase(acceptMRResponse.getMergeStatus())) {
                log.info("MergeTask success!");
                mattermostApiComp.sendPost("MergeSuccess");
            } else {
                log.error("Can not merge!");
                mattermostApiComp.sendPost("合并失败！");
            }
        } else {
            log.error("Can not send MR!");
            mattermostApiComp.sendPost("咋回事？");
        }

        mattermostApiComp.sendPost("活干完了！");
    }
}
