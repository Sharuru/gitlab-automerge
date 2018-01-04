package self.srr.tools.am.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import self.srr.tools.am.common.AMConfig;
import self.srr.tools.am.response.DeployTaskResponse;
import self.srr.tools.am.response.MergeTaskResponse;
import self.srr.tools.am.service.DeployService;

@Slf4j
@Controller
@RequestMapping("/")
public class PageController {

    @Autowired
    DeployService deployService;

    @Autowired
    AMConfig AMConfig;

    @RequestMapping("/")
    public String page() {
        return "index";
    }


    @RequestMapping("/deployStep1")
    @ResponseBody
    public DeployTaskResponse deployStep1() {

        return deployService.deploy(true, true);

    }

    @RequestMapping("/deployStep1s")
    @ResponseBody
    public DeployTaskResponse deployStep1s() {

        return deployService.deploy(true, false);

    }

    @RequestMapping("/deployStep2")
    @ResponseBody
    public DeployTaskResponse deployStep2() {

        return deployService.deploy(false, true);

    }

    @RequestMapping("/deployStep2s")
    @ResponseBody
    public DeployTaskResponse deployStep2s() {

        return deployService.deploy(false, false);

    }

}
