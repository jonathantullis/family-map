package async;

import android.os.AsyncTask;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import _model.Person;
import _request.AllPersonsRequest;
import _result.AllPersonsResult;
import client.DataCache;
import client.HttpClient;

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
        DataCache dataCache = DataCache.getInstance();
        if (result.isSuccess()) {
            String nameOfUser = "Name of User";
            for (Person person : result.getData()) {
                if (person.getPersonID().equals(dataCache.getUserPersonId())) {
                    nameOfUser = person.getFirstName() + " " + person.getLastName();
                }
                Toast toast = Toast.makeText(parentFragment.getContext(), "Hello " + nameOfUser + "!", Toast.LENGTH_LONG);
                toast.show();
            }
        } else {
            System.err.println(result.getMessage());
        }
    }
}
