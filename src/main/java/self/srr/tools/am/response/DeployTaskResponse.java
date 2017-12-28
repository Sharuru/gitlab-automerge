package self.srr.tools.am.response;

import lombok.Data;

@Data
public class DeployTaskResponse {

    private boolean bizStatus = false;

    private int returnCode = -1;

    private String msg = "ERR_UNKNOWN_INITIALIZE";

}
