package async;

import android.os.AsyncTask;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import _request.LoginRequest;
import _result.LoginResult;
import client.HttpClient;

public class LoginAsync extends AsyncTask<LoginRequest, Integer, LoginResult> {
    private LoginResult result;
    private Fragment fragment;

    public LoginAsync(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected LoginResult doInBackground(LoginRequest... loginRequests) {
        try {
            HttpClient client = HttpClient.getInstance();
            result = client.login(loginRequests[0]);
            return result;
        } catch (Exception e) {
            result = new LoginResult("Failed to Sign In");
            return result;
        }
    }

    @Override
    protected void onPostExecute(LoginResult r) {
        if (!result.isSuccess()) {
            Toast toast = Toast.makeText(fragment.getContext(), result.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
