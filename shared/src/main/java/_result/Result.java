package _result;

/**
 * Parent class for all Results
 */
public class Result {
    /**
     * Message to describe error or indicate success
     */
    private String message;
    /**
     * Boolean to indicate success or failure
     */
    private boolean success;

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
