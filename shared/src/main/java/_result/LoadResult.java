package _result;

public class LoadResult extends Result {
    /**
     * @param message message for user describing success or reason for failure
     * @param success boolean to indicate whether the request was successful
     */
    public LoadResult(boolean success, String message) {
        super(success, message);
    }
}
