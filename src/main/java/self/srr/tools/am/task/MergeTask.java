package self.srr.tools.am.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import self.srr.tools.am.api.ApiComp;
import self.srr.tools.am.common.AMConfig;
import self.srr.tools.am.response.MergeRequestResponse;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MergeTask {

    @Autowired
    AMConfig AMConfig;

    @Autowired
    ApiComp apiComp;

    @Scheduled(fixedRate = 1000000000)
    public void print() throws Exception {

        // create MR
        MergeRequestResponse createMRResponse = apiComp.createMR();

        if (createMRResponse.getStatusCode() == 201) {
            TimeUnit.SECONDS.sleep(5L);
            // accept MR
            MergeRequestResponse acceptMRResponse = apiComp.acceptMR(createMRResponse.getIid());
            if (acceptMRResponse.getStatusCode() == 200 && "can_be_merged".equalsIgnoreCase(acceptMRResponse.getMergeStatus())) {
                log.info("MergeTask success!");
            } else {
                log.error("Can not merge!");
            }

        } else {
            log.error("Can not send MR!");
        }
    }
}
