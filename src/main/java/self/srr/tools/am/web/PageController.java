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

import javax.servlet.http.HttpServletRequest;

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
    public DeployTaskResponse deployStep1(HttpServletRequest request) {
        DeployTaskResponse response = deployService.deploy(true, true, request.getRemoteAddr());
        log.info("IP: " + request.getRemoteAddr() + " triggered deployStep1");
        return response;
    }

    @RequestMapping("/deployStep1s")
    @ResponseBody
    public DeployTaskResponse deployStep1s(HttpServletRequest request) {
        DeployTaskResponse response = deployService.deploy(true, false, request.getRemoteAddr());
        log.info("IP: " + request.getRemoteAddr() + " triggered deployStep1s");
        return response;
    }

    @RequestMapping("/deployStep2")
    @ResponseBody
    public DeployTaskResponse deployStep2(HttpServletRequest request) {
        DeployTaskResponse response = deployService.deploy(false, true, request.getRemoteAddr());
        log.info("IP: " + request.getRemoteAddr() + " triggered deployStep2");
        return response;

    }

    @RequestMapping("/deployStep2s")
    @ResponseBody
    public DeployTaskResponse deployStep2s(HttpServletRequest request) {
        DeployTaskResponse response = deployService.deploy(false, false, request.getRemoteAddr());
        log.info("IP: " + request.getRemoteAddr() + " triggered deployStep2s");
        return response;
    }

}
