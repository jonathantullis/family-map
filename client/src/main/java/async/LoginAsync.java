package async;

import android.os.AsyncTask;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import _request.AllEventsRequest;
import _request.AllPersonsRequest;
import _request.LoginRequest;
import _result.LoginResult;
import client.DataCache;
import client.HttpClient;

public class LoginAsync extends AsyncTask<LoginRequest, Integer, LoginResult> {
    private Fragment parentFragment;

    public LoginAsync(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    protected LoginResult doInBackground(LoginRequest... loginRequests) {
        HttpClient client = HttpClient.getInstance();
        return client.login(loginRequests[0]);
    }

    @Override
    protected void onPostExecute(LoginResult result) {
        if (result.isSuccess()) {
            // Call async to get all related family data
            DataCache dataCache = DataCache.getInstance();
            AllEventsRequest allEventsRequest = new AllEventsRequest(dataCache.userName(), dataCache.authToken());
            new FetchAllEventsAsync(parentFragment).execute(allEventsRequest);
            AllPersonsRequest allPersonsRequest = new AllPersonsRequest(dataCache.userName(), dataCache.authToken());
            new FetchAllPersonsAsync(parentFragment).execute(allPersonsRequest);
        } else {
            Toast toast = Toast.makeText(parentFragment.getContext(), result.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
