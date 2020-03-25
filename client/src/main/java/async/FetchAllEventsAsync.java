package async;

import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import _request.AllEventsRequest;
import _result.AllEventsResult;
import client.HttpClient;

public class FetchAllEventsAsync extends AsyncTask<AllEventsRequest, Integer, AllEventsResult> {
    private Fragment parentFragment;

    public FetchAllEventsAsync(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    protected AllEventsResult doInBackground(AllEventsRequest... requests) {
        HttpClient client = HttpClient.getInstance();
        return client.fetchAllEvents(requests[0]);
    }

    @Override
    protected void onPostExecute(AllEventsResult result) {
        if (result.isSuccess()) {
            System.out.println("Successfully fetched related events");
        } else {
            System.err.println(result.getMessage());
        }
    }
}
