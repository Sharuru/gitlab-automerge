package self.srr.tools.am.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import self.srr.tools.am.response.DeployTaskResponse;
import self.srr.tools.am.response.MergeTaskResponse;
import self.srr.tools.am.service.DeployService;

@Slf4j
@Controller
@RequestMapping("/")
public class PageController {

    @Autowired
    DeployService mergeTask;

    @RequestMapping("/")
    public String page() {
        return "index";
    }

    @RequestMapping("/update")
    @ResponseBody
    public MergeTaskResponse update() {

        MergeTaskResponse response = new MergeTaskResponse();
        try {
            //response = mergeTask.service(true);
            response.setStatus(false);
            response.setMessage("当前系统设置不允许来自网页端的操作。");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened in `update`： " + e.getMessage());
        }

        return response;
    }

    @RequestMapping("/deployStep1")
    @ResponseBody
    public DeployTaskResponse deployStep1() {

        DeployTaskResponse response = new DeployTaskResponse();

        return response;
    }

}
