package self.srr.tools.am.model.response;

import lombok.Data;

@Data
public class MergeTaskResponse {

    private boolean status = false;

    private String message = "未知错误，请联系管理员。";
}
