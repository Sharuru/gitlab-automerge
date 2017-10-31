package self.srr.tools.am.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class GitlabMRResponse {

    private int statusCode;

    private String id;

    private String iid;

    private String state;

    @SerializedName("merge_status")
    private String mergeStatus;

    @SerializedName("merge_commit_sha")
    private String mergeCommitSha;
}
