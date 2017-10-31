package self.srr.tools.am.api;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import self.srr.tools.am.common.AMConfig;
import self.srr.tools.am.request.MattermostPostAPIRequest;
import self.srr.tools.am.response.MattermostAPIResponse;

@Slf4j
@Component
public class MattermostApiComp {

    @Autowired
    AMConfig amConfig;

    /**
     * Send message to Mattermost users
     *
     * @param message message
     * @return response
     */
    public MattermostAPIResponse sendPost(String message) {

        MattermostAPIResponse apiResponse = new MattermostAPIResponse();

        HttpPost httpPost = new HttpPost(amConfig.getMattermost().getUrl() + "/api/v4/posts");
        httpPost.addHeader("Authorization", "Bearer " + amConfig.getMattermost().getPrivateToken());

        MattermostPostAPIRequest request = new MattermostPostAPIRequest();
        request.setChannelId(amConfig.getMattermost().getNotifyChannel());
        request.setMessage(message);
        try {
            StringEntity params = new StringEntity(new Gson().toJson(request), "UTF-8");
            httpPost.setEntity(params);

            HttpResponse response = HttpClients.createDefault().execute(httpPost);

            String responseStr = EntityUtils.toString(response.getEntity());
            apiResponse = new Gson().fromJson(responseStr, MattermostAPIResponse.class);
            apiResponse.setStatusCode(response.getStatusLine().getStatusCode());

            log.info("sendPost API triggered with code " + response.getStatusLine().getStatusCode() + ": " + responseStr);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened in 'sendPost': " + e.getMessage());
        }

        return apiResponse;
    }


}
