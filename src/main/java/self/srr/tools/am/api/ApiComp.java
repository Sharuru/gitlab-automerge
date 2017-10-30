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
import self.srr.tools.am.response.MergeRequestResponse;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ApiComp {

    @Autowired
    AMConfig amConfig;

    public MergeRequestResponse createMR() {

        MergeRequestResponse mergeRequestResponse = new MergeRequestResponse();

        HttpPost httpPost = new HttpPost(amConfig.getGitlab().getUrl() + "/api/v4/projects/" + amConfig.getGitlab().getProjectId() + "/merge_requests");
        httpPost.setHeader("PRIVATE-TOKEN", amConfig.getGitlab().getPrivateToken());

        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("id", amConfig.getGitlab().getProjectId()));
        params.add(new BasicNameValuePair("source_branch", amConfig.getGitlab().getSourceBranch()));
        params.add(new BasicNameValuePair("target_branch", amConfig.getGitlab().getTargetBranch()));
        params.add(new BasicNameValuePair("title", "Auto merge test"));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = HttpClients.createDefault().execute(httpPost);

            String responseStr = EntityUtils.toString(response.getEntity());
            mergeRequestResponse = new Gson().fromJson(responseStr, MergeRequestResponse.class);
            mergeRequestResponse.setStatusCode(response.getStatusLine().getStatusCode());

            log.info("API triggered with code " + response.getStatusLine().getStatusCode() + ": " + responseStr);

            // FIXME: workaround cause Gitlab won't refresh the MR status #17287
            HttpGet httpGet = new HttpGet(amConfig.getGitlab().getProjectPage() + "/merge_requests/" + mergeRequestResponse.getIid());
            httpGet.setHeader("PRIVATE-TOKEN", amConfig.getGitlab().getPrivateToken());
            HttpClients.createDefault().execute(httpGet);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened in 'createMR': " + e.getMessage());
        }

        return mergeRequestResponse;
    }

    public MergeRequestResponse updateMR(String iid) {

        MergeRequestResponse mergeRequestResponse = new MergeRequestResponse();

        HttpGet httpGet = new HttpGet(amConfig.getGitlab().getUrl() + "/api/v4/projects/" + amConfig.getGitlab().getProjectId() + "/merge_requests/" + iid);
        httpGet.setHeader("PRIVATE-TOKEN", amConfig.getGitlab().getPrivateToken());

        try {
            HttpResponse response = HttpClients.createDefault().execute(httpGet);
            String responseStr = EntityUtils.toString(response.getEntity());
            mergeRequestResponse = new Gson().fromJson(responseStr, MergeRequestResponse.class);
            mergeRequestResponse.setStatusCode(response.getStatusLine().getStatusCode());

            log.info("API triggered with code " + response.getStatusLine().getStatusCode() + ": " + responseStr);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened in 'updateMR': " + e.getMessage());
        }

        return mergeRequestResponse;
    }

    public MergeRequestResponse acceptMR(String iid) {

        MergeRequestResponse mergeRequestResponse = new MergeRequestResponse();

        HttpPut httpPut = new HttpPut(amConfig.getGitlab().getUrl() + "/api/v4/projects/" + amConfig.getGitlab().getProjectId() + "/merge_requests/" + iid + "/merge");
        httpPut.setHeader("PRIVATE-TOKEN", amConfig.getGitlab().getPrivateToken());
        try {
            HttpResponse response = HttpClients.createDefault().execute(httpPut);
            String responseStr = EntityUtils.toString(response.getEntity());
            mergeRequestResponse = new Gson().fromJson(responseStr, MergeRequestResponse.class);
            mergeRequestResponse.setStatusCode(response.getStatusLine().getStatusCode());

            log.info("API triggered with code " + response.getStatusLine().getStatusCode() + ": " + responseStr);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened in 'acceptMR': " + e.getMessage());
        }
        
        return mergeRequestResponse;
    }
}
