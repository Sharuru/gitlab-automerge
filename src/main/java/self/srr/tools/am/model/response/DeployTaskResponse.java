package self.srr.tools.am.model.response;

import lombok.Data;

@Data
public class DeployTaskResponse {

    private boolean bizStatus = false;

    private int returnCode = -1;

    private String msg = "ERR_UNKNOWN_INITIALIZE";

    private String pipelineId = "";

}
