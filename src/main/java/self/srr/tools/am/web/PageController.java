package self.srr.tools.am.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;
import self.srr.tools.am.common.AMConfig;
import self.srr.tools.am.model.response.DeployTaskResponse;
import self.srr.tools.am.service.DeployService;
import self.srr.tools.am.service.GitlabApiService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/")
public class PageController {

    private static final String H_PARA_XIP = "X-Real-IP";

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
        DeployTaskResponse response = deployService.deploy(true, true, request.getHeader(H_PARA_XIP));
        log.info("IP: " + request.getHeader(H_PARA_XIP) + " triggered deployStep1");
        return response;
    }

    @RequestMapping("/deployStep1s")
    @ResponseBody
    public DeployTaskResponse deployStep1s(HttpServletRequest request) {
        DeployTaskResponse response = deployService.deploy(true, false, request.getHeader(H_PARA_XIP));
        log.info("IP: " + request.getHeader(H_PARA_XIP) + " triggered deployStep1s");
        return response;
    }

    @RequestMapping("/deployStep2")
    @ResponseBody
    public DeployTaskResponse deployStep2(HttpServletRequest request) {
        DeployTaskResponse response = deployService.deploy(false, true, request.getHeader(H_PARA_XIP));
        log.info("IP: " + request.getHeader(H_PARA_XIP) + " triggered deployStep2");
        return response;

    }

    @RequestMapping("/deployStep2s")
    @ResponseBody
    public DeployTaskResponse deployStep2s(HttpServletRequest request) {
        DeployTaskResponse response = deployService.deploy(false, false, request.getHeader(H_PARA_XIP));
        log.info("IP: " + request.getHeader(H_PARA_XIP) + " triggered deployStep2s");
        return response;
    }

    @RequestMapping("/triggerManual/{pipeId}")
    @ResponseBody
    public List<String> triggerManual(@PathVariable(name = "pipeId", required = false) String pipeId) {
        if (StringUtils.isEmpty(pipeId) || "undefined".equalsIgnoreCase(pipeId)) {
            return new ArrayList<>(Collections.singletonList("No pipeline id."));
        } else {
            return deployService.triggerManual(pipeId);
        }
    }

}
