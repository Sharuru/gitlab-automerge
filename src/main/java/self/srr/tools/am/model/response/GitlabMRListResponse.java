package self.srr.tools.am.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GitlabMRListResponse {

    private int statusCode;

    List<GitlabMRResponse> mrList = new ArrayList<>(0);

}
