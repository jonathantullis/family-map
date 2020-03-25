package client;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.client.R;

import java.util.ArrayList;

import _request.LoginRequest;
import _request.RegisterRequest;
import async.FetchAllPersonsAsync;
import async.LoginAsync;
import async.RegisterAsync;

public class LoginFragment extends Fragment {

    public static final String ARG_TITLE = "Title";

    private View view;
    private HttpClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        Button signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setEnabled(false);
        signInButton.setOnClickListener((View v) -> { login(); });

        Button registerButton = view.findViewById(R.id.register_button);
        registerButton.setEnabled(false);
        registerButton.setOnClickListener((View v) -> { register(); });

        // Add event listeners to all the EditText fields in the form
        ArrayList<EditText> editTexts = new ArrayList<>();
        editTexts.add(view.findViewById(R.id.server_host));
        editTexts.add(view.findViewById(R.id.server_port));
        editTexts.add(view.findViewById(R.id.user_name));
        editTexts.add(view.findViewById(R.id.password));
        editTexts.add(view.findViewById(R.id.first_Name));
        editTexts.add(view.findViewById(R.id.last_Name));
        editTexts.add(view.findViewById(R.id.email));
        for (EditText editText : editTexts) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    enableButtonsIfReady();
                }
            });
        }

        // Add event listener for gender selector radio buttons
        RadioGroup genderSelector = view.findViewById(R.id.gender_radio_group);
        genderSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        enableButtonsIfReady();
                    }
                });

        return view;
    }

    private void login() {
        client = HttpClient.getInstance(getServerHost(), getServerPort());
        LoginRequest request = getLoginRequest();
        new LoginAsync(this).execute(request);
    }

    private LoginRequest getLoginRequest() {
        if (view == null) {
            return null;
        }

        String userName;
        String password;

        TextView textView;
        // userName
        textView = view.findViewById(R.id.user_name);
        userName = textView.getText().toString();
        // Password
        textView = view.findViewById(R.id.password);
        password = textView.getText().toString();

        return new LoginRequest(userName, password);
    }

    private void register() {
        client = HttpClient.getInstance(getServerHost(), getServerPort());
        RegisterRequest request = getRegisterRequest();
        new RegisterAsync(this).execute(request);
    }

    private RegisterRequest getRegisterRequest() {
        if (view == null) {
            return null;
        }

        return new RegisterRequest(getUserName(), getPassword(), getEmail(),
                                   getFirstName(), getLastName(), getGender());
    }

    private void enableButtonsIfReady() {
        int serverHostLength = getServerHost().length();
        int serverPortLength = getServerPort().length();
        int userNameLength = getUserName().length();
        int passwordLength = getPassword().length();
        int firstNameLength = getFirstName().length();
        int lastNameLength = getLastName().length();
        int emailLength = getEmail().length();
        int genderLength = getGender().length();
        
        final int REQUIRED_LENGTH = 3;

        boolean enableLogin = serverHostLength >= REQUIRED_LENGTH &&
                            serverPortLength >= REQUIRED_LENGTH &&
                            userNameLength >= REQUIRED_LENGTH &&
                            passwordLength >= REQUIRED_LENGTH;

        Button signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setEnabled(enableLogin);

        boolean enableRegister = serverHostLength >= REQUIRED_LENGTH &&
                serverPortLength >= REQUIRED_LENGTH &&
                userNameLength >= REQUIRED_LENGTH &&
                passwordLength >= REQUIRED_LENGTH &&
                firstNameLength >= REQUIRED_LENGTH &&
                lastNameLength >= REQUIRED_LENGTH &&
                emailLength >= REQUIRED_LENGTH &&
                genderLength > 0;

        Button registerButton = view.findViewById(R.id.register_button);
        registerButton.setEnabled(enableRegister);
    }

    private String getServerHost() {
        TextView textView = view.findViewById(R.id.server_host);
        return textView.getText().toString();
    }

    private String getServerPort() {
        TextView textView = view.findViewById(R.id.server_port);
        return textView.getText().toString();
    }

    private String getUserName() {
        TextView textView = view.findViewById(R.id.user_name);
        return textView.getText().toString();
    }

    private String getPassword() {
        TextView textView = view.findViewById(R.id.password);
        return textView.getText().toString();
    }

    private String getFirstName() {
        TextView textView = view.findViewById(R.id.first_Name);
        return textView.getText().toString();
    }

    private String getLastName() {
        TextView textView = view.findViewById(R.id.last_Name);
        return textView.getText().toString();
    }

    private String getEmail() {
        TextView textView = view.findViewById(R.id.email);
        return textView.getText().toString();
    }

    private String getGender() {
        String gender;
        RadioGroup genderRadioGroup;
        genderRadioGroup = view.findViewById(R.id.gender_radio_group);
        int radioButtonID = genderRadioGroup.getCheckedRadioButtonId();
        View radioButton = genderRadioGroup.findViewById(radioButtonID);
        if (radioButton == null) {
            gender = "";
        } else {
            int index = genderRadioGroup.indexOfChild(radioButton);
            RadioButton r = (RadioButton) genderRadioGroup.getChildAt(index);
            gender = r.getText().toString().substring(0, 1);
        }

        return gender.toLowerCase();
    }
}
