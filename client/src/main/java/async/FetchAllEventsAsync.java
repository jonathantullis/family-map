package async;

import android.os.AsyncTask;

import _request.AllEventsRequest;
import _result.AllEventsResult;

public class FetchAllEventsAsync extends AsyncTask<AllEventsRequest, Integer, AllEventsResult> {
    @Override
    protected AllEventsResult doInBackground(AllEventsRequest... allEventsRequests) {
        return null;
    }
}
