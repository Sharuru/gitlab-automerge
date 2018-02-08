package self.srr.tools.am.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GitlabHookRequest {

    private String object_kind;

    private GitlabObjectAttributeRequest object_attributes;

    private List<GitlabBuildsRequest> builds = new ArrayList<>();
}
