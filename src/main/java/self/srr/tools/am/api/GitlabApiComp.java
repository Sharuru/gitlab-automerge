package self.srr.tools.am.api;

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
import org.springframework.stereotype.Component;
import self.srr.tools.am.common.AMConfig;
import self.srr.tools.am.response.GitlabMRListResponse;
import self.srr.tools.am.response.GitlabMRResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class GitlabApiComp {

    @Autowired
    AMConfig amConfig;

    /**
     * Create MR request
     *
     * @return response
     */
    public GitlabMRResponse createMR(String from) {

        GitlabMRResponse mergeRequestResponse = new GitlabMRResponse();

        HttpPost httpPost = new HttpPost(amConfig.getGitlab().getUrl() + "/api/v4/projects/" + amConfig.getGitlab().getProjectId() + "/merge_requests");
        httpPost.setHeader("PRIVATE-TOKEN", amConfig.getGitlab().getPrivateToken());

        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("id", amConfig.getGitlab().getProjectId()));
        params.add(new BasicNameValuePair("source_branch", amConfig.getGitlab().getSourceBranch()));
        params.add(new BasicNameValuePair("target_branch", amConfig.getGitlab().getTargetBranch()));
        params.add(new BasicNameValuePair("title", "Created by " + from));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = HttpClients.createDefault().execute(httpPost);

            String responseStr = EntityUtils.toString(response.getEntity());
            log.info("createMR API triggered with code " + response.getStatusLine().getStatusCode() + ": " + responseStr);
            mergeRequestResponse = new Gson().fromJson(responseStr, GitlabMRResponse.class);
            mergeRequestResponse.setStatusCode(response.getStatusLine().getStatusCode());

            // FIXME: workaround cause Gitlab won't refresh the MR status #17287
            HttpGet httpGet = new HttpGet(amConfig.getGitlab().getProjectPage() + "/merge_requests/" + mergeRequestResponse.getIid());
            httpGet.setHeader("PRIVATE-TOKEN", amConfig.getGitlab().getPrivateToken());
            HttpResponse workaroundResponse = HttpClients.createDefault().execute(httpGet);
            log.info("createMR workaround engaged with return code: " + workaroundResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened in 'createMR': " + e.getMessage());
        }

        return mergeRequestResponse;
    }

    /**
     * Accept matched MR
     *
     * @param iid MR id
     * @return response
     */
    public GitlabMRResponse acceptMR(String iid) {

        GitlabMRResponse mergeRequestResponse = new GitlabMRResponse();

        HttpPut httpPut = new HttpPut(amConfig.getGitlab().getUrl() + "/api/v4/projects/" + amConfig.getGitlab().getProjectId() + "/merge_requests/" + iid + "/merge");
        httpPut.setHeader("PRIVATE-TOKEN", amConfig.getGitlab().getPrivateToken());
        try {
            HttpResponse response = HttpClients.createDefault().execute(httpPut);
            String responseStr = EntityUtils.toString(response.getEntity());
            log.info("acceptMR API triggered with code " + response.getStatusLine().getStatusCode() + ": " + responseStr);
            mergeRequestResponse = new Gson().fromJson(responseStr, GitlabMRResponse.class);
            mergeRequestResponse.setStatusCode(response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened in 'acceptMR': " + e.getMessage());
        }

        return mergeRequestResponse;
    }

    public GitlabMRListResponse listMR() {

        GitlabMRListResponse listResponse = new GitlabMRListResponse();

        HttpGet httpGet = new HttpGet(amConfig.getGitlab().getUrl() + "/api/v4/projects/" + amConfig.getGitlab().getProjectId() + "/merge_requests/");
        httpGet.setHeader("PRIVATE-TOKEN", amConfig.getGitlab().getPrivateToken());

        try {
            HttpResponse response = HttpClients.createDefault().execute(httpGet);
            String responseStr = EntityUtils.toString(response.getEntity());
            log.info("listMR API triggered with code " + response.getStatusLine().getStatusCode() + ": " + responseStr);
            GitlabMRResponse[] mrs = new Gson().fromJson(responseStr, GitlabMRResponse[].class);
            listResponse.setStatusCode(response.getStatusLine().getStatusCode());
            listResponse.setMrList(Arrays.asList(mrs));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened in 'listMR': " + e.getMessage());
        }

        return listResponse;
    }
}
