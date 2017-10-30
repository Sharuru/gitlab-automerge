package self.srr.tools.am.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class GitlabAPIResponse {

    private int statusCode;

    private String id;

    private String iid;

    @SerializedName("merge_status")
    private String mergeStatus;
}
