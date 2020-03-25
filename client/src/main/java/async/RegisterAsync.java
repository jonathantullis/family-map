package async;

import android.os.AsyncTask;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import _request.RegisterRequest;
import _result.RegisterResult;
import client.HttpClient;

public class RegisterAsync extends AsyncTask<RegisterRequest, Integer, RegisterResult> {
    private RegisterResult result;
    private Fragment fragment;

    public RegisterAsync(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected RegisterResult doInBackground(RegisterRequest... registerRequests) {
        try {
            HttpClient client = HttpClient.getInstance();
            result = client.register(registerRequests[0]);
            return result;
        } catch (Exception e) {
            result = new RegisterResult("Failed to Register");
            return result;
        }
    }

    @Override
    protected void onPostExecute(RegisterResult r) {
        if (!result.isSuccess()) {
            Toast toast = Toast.makeText(fragment.getContext(), result.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
