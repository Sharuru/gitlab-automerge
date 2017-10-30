package self.srr.tools.am.request;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class MattermostPostAPIRequest {

    @SerializedName("channel_id")
    private String channelId;

    private String message;
}
