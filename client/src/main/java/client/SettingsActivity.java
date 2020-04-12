package client;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.client.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import model.SettingsItem;


public class SettingsActivity extends AppCompatActivity {
    private DataCache dataCache = DataCache.getInstance();
    ArrayList<SettingsItem> settingsItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Family Map: Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsItems.add(new SettingsItem(1, "Life Story Lines",
                "Show life story lines", dataCache.settings().isLifeStoryLines()));
        settingsItems.add(new SettingsItem(2 ,"Family Tree Lines",
                "Show family tree lines", dataCache.settings().isFamilyTreeLines()));
        settingsItems.add(new SettingsItem(3, "Spouse Lines",
                "Show spouse lines", dataCache.settings().isSpouseLines()));
        settingsItems.add(new SettingsItem(4, "Father's Side",
                "Filter by father's side of the family", dataCache.settings().isFathersSide()));
        settingsItems.add(new SettingsItem(5, "Mother's Side",
                "Filter by mother's side of the family", dataCache.settings().isMothersSide()));
        settingsItems.add(new SettingsItem(6, "Male Events",
                "Filter events based on gender", dataCache.settings().isMaleEvents()));
        settingsItems.add(new SettingsItem(7, "Female Events",
                "Filter events based on gender", dataCache.settings().isFemaleEvents()));

        RecyclerView recyclerView = findViewById(R.id.settings_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SettingsAdapter adapter = new SettingsAdapter(settingsItems);
        recyclerView.setAdapter(adapter);

        Button buttonView = findViewById(R.id.logout_button);
        buttonView.setOnClickListener(v -> {
            logout();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return false;
    }

    public void logout() {
        dataCache.invalidateData();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private class SettingsAdapter extends RecyclerView.Adapter<SettingsViewHolder> {
        private final List<SettingsItem> settingsItems;

        SettingsAdapter(List<SettingsItem> settingsItems) {
            this.settingsItems = settingsItems;
        }

        @NonNull
        @Override
        public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            view = getLayoutInflater().inflate(R.layout.item_setting, parent, false);
            return new SettingsViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
            holder.bind(settingsItems.get(position));
        }

        @Override
        public int getItemCount() {
            return settingsItems.size();
        }
    }

    private class SettingsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView titleView;
        private final TextView descriptionView;
        private final SwitchCompat switchView;

        private SettingsItem settingsItem;
        private final int viewType;

        SettingsViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            titleView = itemView.findViewById(R.id.setting_title);
            descriptionView = itemView.findViewById(R.id.setting_description);
            switchView = itemView.findViewById(R.id.setting_switch);

            switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
                switch (settingsItem.getID()) {
                    case 1:
                        dataCache.settings().setLifeStoryLines(isChecked);
                        break;
                    case 2:
                        dataCache.settings().setFamilyTreeLines(isChecked);
                        break;
                    case 3:
                        dataCache.settings().setSpouseLines(isChecked);
                        break;
                    case 4:
                        dataCache.settings().setFathersSide(isChecked);
                        break;
                    case 5:
                        dataCache.settings().setMothersSide(isChecked);
                        break;
                    case 6:
                        dataCache.settings().setMaleEvents(isChecked);
                        break;
                    case 7:
                        dataCache.settings().setFemaleEvents(isChecked);
                        break;
                }
            });
        }

        private void bind(SettingsItem settingsItem) {
            this.settingsItem = settingsItem;
            titleView.setText(settingsItem.getTitle());
            descriptionView.setText(settingsItem.getDescription());
            switchView.setChecked(settingsItem.getValue());
        }

        @Override
        public void onClick(View view) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}