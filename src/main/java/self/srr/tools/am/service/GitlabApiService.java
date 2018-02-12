package self.srr.tools.am.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import self.srr.tools.am.common.AMConfig;
import self.srr.tools.am.model.response.GitlabMRListResponse;
import self.srr.tools.am.model.response.GitlabMRResponse;
import self.srr.tools.am.model.response.GitlabPipelineResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class GitlabApiService {

    @Autowired
    AMConfig amConfig;

    private static final String API_NODE = "/api/v4/projects/";

    private static final String H_PARA_PRI_TOKN = "PRIVATE-TOKEN";

    /**
     * Create MR request
     *
     * @return response
     */
    public GitlabMRResponse createMR(String refSource, String refTarget, String callerIp) {

        GitlabMRResponse mergeRequestResponse = new GitlabMRResponse();

        HttpPost httpPost = new HttpPost(amConfig.getGitlab().getUrl() + API_NODE + amConfig.getGitlab().getProjectId() + "/merge_requests");
        httpPost.setHeader(H_PARA_PRI_TOKN, amConfig.getGitlab().getPrivateToken());

        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("id", amConfig.getGitlab().getProjectId()));
        params.add(new BasicNameValuePair("source_branch", refSource));
        params.add(new BasicNameValuePair("target_branch", refTarget));
        params.add(new BasicNameValuePair("title", "Pre-deploy MR for branch: " + refTarget));
        if (!StringUtils.isEmpty(callerIp)) {
            params.add(new BasicNameValuePair("description", "Requested by: " + callerIp));
        }

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = HttpClients.createDefault().execute(httpPost);

            String responseStr = EntityUtils.toString(response.getEntity());
            log.info("createMR API triggered with code " + response.getStatusLine().getStatusCode() + ": " + responseStr);
            mergeRequestResponse = new Gson().fromJson(responseStr, GitlabMRResponse.class);
            mergeRequestResponse.setStatusCode(response.getStatusLine().getStatusCode());

            // TODO: workaround cause Gitlab won't refresh the MR status #17287
            HttpGet httpGet = new HttpGet(amConfig.getGitlab().getProjectPage() + "/merge_requests/" + mergeRequestResponse.getIid());
            httpGet.setHeader(H_PARA_PRI_TOKN, amConfig.getGitlab().getPrivateToken());
            HttpResponse workaroundResponse = HttpClients.createDefault().execute(httpGet);
            log.info("createMR workaround engaged with return code: " + workaroundResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            log.error("Error happened in 'createMR': " + e);
        }

        return mergeRequestResponse;
    }

    public GitlabPipelineResponse triggerPipe(String ref) {

        GitlabPipelineResponse gitlabPipelineResponse = new GitlabPipelineResponse();

        HttpPost httpPost = new HttpPost(amConfig.getGitlab().getUrl() + API_NODE + amConfig.getGitlab().getProjectId() + "/trigger/pipeline");
        httpPost.setHeader(H_PARA_PRI_TOKN, amConfig.getGitlab().getPrivateToken());

        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("token", amConfig.getGitlab().getPipelineToken()));
        params.add(new BasicNameValuePair("ref", ref));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = HttpClients.createDefault().execute(httpPost);

            String responseStr = EntityUtils.toString(response.getEntity());
            log.info("Pipeline triggered with code " + response.getStatusLine().getStatusCode() + ": " + responseStr);
            gitlabPipelineResponse = new Gson().fromJson(responseStr, GitlabPipelineResponse.class);

        } catch (Exception e) {
            log.error("Error happened in 'triggerPipe': " + e);
        }

        return gitlabPipelineResponse;

    }


    /**
     * Accept matched MR
     *
     * @param iid MR id
     * @return response
     */
    public GitlabMRResponse acceptMR(String iid) {

        GitlabMRResponse mergeRequestResponse = new GitlabMRResponse();

        HttpPut httpPut = new HttpPut(amConfig.getGitlab().getUrl() + API_NODE + amConfig.getGitlab().getProjectId() + "/merge_requests/" + iid + "/merge");
        httpPut.setHeader(H_PARA_PRI_TOKN, amConfig.getGitlab().getPrivateToken());
        try {
            HttpResponse response = HttpClients.createDefault().execute(httpPut);
            String responseStr = EntityUtils.toString(response.getEntity());
            log.info("acceptMR API triggered with code " + response.getStatusLine().getStatusCode() + ": " + responseStr);
            mergeRequestResponse = new Gson().fromJson(responseStr, GitlabMRResponse.class);
            mergeRequestResponse.setStatusCode(response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            log.error("Error happened in 'acceptMR': " + e);
        }

        return mergeRequestResponse;
    }

    /**
     * List all merge requests
     *
     * @param reqState request status
     * @return response
     */
    public GitlabMRListResponse listMR(String reqState) {

        GitlabMRListResponse listResponse = new GitlabMRListResponse();

        HttpGet httpGet = new HttpGet(amConfig.getGitlab().getUrl() + API_NODE + amConfig.getGitlab().getProjectId() + "/merge_requests?state=" + reqState);
        httpGet.setHeader(H_PARA_PRI_TOKN, amConfig.getGitlab().getPrivateToken());

        try {
            HttpResponse response = HttpClients.createDefault().execute(httpGet);
            String responseStr = EntityUtils.toString(response.getEntity());
            log.info("listMR API triggered with code " + response.getStatusLine().getStatusCode() + ": " + responseStr);
            GitlabMRResponse[] mrs = new Gson().fromJson(responseStr, GitlabMRResponse[].class);
            listResponse.setStatusCode(response.getStatusLine().getStatusCode());
            listResponse.setMrList(Arrays.asList(mrs));
        } catch (Exception e) {
            log.error("Error happened in 'listMR': " + e);
        }

        return listResponse;
    }
}
