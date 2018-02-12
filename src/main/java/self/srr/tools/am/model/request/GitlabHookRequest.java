package self.srr.tools.am.model.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GitlabHookRequest {

    private String object_kind;

    private GitlabObjectAttributeRequest object_attributes;

    private List<GitlabBuildsRequest> builds = new ArrayList<>();
}
