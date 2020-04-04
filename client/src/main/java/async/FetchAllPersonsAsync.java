package async;

import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import _request.AllPersonsRequest;
import _result.AllPersonsResult;
import client.HttpClient;
import client.MainActivity;

public class FetchAllPersonsAsync extends AsyncTask<AllPersonsRequest, Integer, AllPersonsResult> {
    private Fragment parentFragment;

    public FetchAllPersonsAsync(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    protected AllPersonsResult doInBackground(AllPersonsRequest... requests) {
        HttpClient client = HttpClient.getInstance();
        return client.fetchAllPersons(requests[0]);
    }

    @Override
    protected void onPostExecute(AllPersonsResult result) {
        if (result.isSuccess()) {
            MainActivity activity = (MainActivity) parentFragment.getActivity();
            assert(activity != null);
            activity.switchToMapFragment();
        } else {
            System.err.println(result.getMessage());
        }
    }
}
