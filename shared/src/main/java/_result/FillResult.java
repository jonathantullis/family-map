package _result;

public class FillResult extends Result {
    /**
     * @param success Either a description of the error or a success message
     * @param message Boolean to indicate whether the request was successful
     */
    public FillResult(boolean success, String message) {
        super(success, message);
    }
}
