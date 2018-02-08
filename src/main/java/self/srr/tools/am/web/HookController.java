package self.srr.tools.am.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import self.srr.tools.am.common.AMConfig;
import self.srr.tools.am.request.GitlabBuildsRequest;
import self.srr.tools.am.request.GitlabHookRequest;
import self.srr.tools.am.service.MattermostApiService;

@Slf4j
@Controller
@RequestMapping("/api")
public class HookController {

    @Autowired
    MattermostApiService mattermostApiService;

    @Autowired
    AMConfig AMConfig;


    @RequestMapping("/pipelines")
    @ResponseBody
    public void pipelines(@RequestBody GitlabHookRequest request) {
        log.info("Get hook of pipeline: #" + request.getObject_attributes().getId());

        for (GitlabBuildsRequest b : request.getBuilds()) {
            if ("failed".equalsIgnoreCase(b.getStatus())) {
                log.info("Hook detected error status, pipeline #" + request.getObject_attributes().getId());
                String msg = "";
                String pId = request.getObject_attributes().getId();
                msg += ("Pipeline [#" + pId + "](" + AMConfig.getGitlab().getPublicProjectPage() + "/pipelines/" + pId + ") 发生构建错误。\n");
                msg += ("状况位于分支：" + request.getObject_attributes().getRef() + "，Build #" + b.getId() + "，构建名：" + b.getName() + "，阶段：" + b.getStage() + "，开始于：" + b.getStarted_at() + "，状态：" + b.getStatus());
                mattermostApiService.sendPost(msg);

                break;
            }
        }

    }


}
