package oob.lolprofile.HomeComponent.Framework.Fragment.Option;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import oob.lolprofile.ApplicationComponent.BaseApplication;
import oob.lolprofile.HomeComponent.Domain.DefaultELO.GetDefaultELOUseCase;
import oob.lolprofile.HomeComponent.Domain.DefaultELO.SetDefaultELOUseCase;
import oob.lolprofile.HomeComponent.Domain.DeleteStoredData.DeleteStoredDataUseCase;
import oob.lolprofile.HomeComponent.Framework.Fragment.Option.DependencyInjection.DaggerOptionsFragmentComponentInterface;
import oob.lolprofile.HomeComponent.Framework.Fragment.Option.DependencyInjection.OptionsFragmentComponentInterface;
import oob.lolprofile.HomeComponent.Framework.Fragment.Option.DependencyInjection.OptionsFragmentModule;
import oob.lolprofile.R;
import oob.lolprofile.Util.Dialog;

public class OptionsFragment extends Fragment implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    OptionsFragmentComponentInterface component;
    private Context context;

    @BindView(R.id.spinnerDefaultELO)
    Spinner spinnerDefaultELO;
    @BindView(R.id.switchDeleteStoredData)
    Switch switchDeleteStoredData;

    @Inject
    GetDefaultELOUseCase getDefaultELOUseCase;
    @Inject
    SetDefaultELOUseCase setDefaultELOUseCase;
    @Inject
    DeleteStoredDataUseCase deleteStoredDataUseCase;

    private String[] elo_keys;

    public OptionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options, container, false);
        ButterKnife.bind(this, view);

        this.context = view.getContext();
        this.component = DaggerOptionsFragmentComponentInterface.builder()
                .baseApplicationComponentInterface(((BaseApplication) this.context.getApplicationContext()).getComponent())
                .optionsFragmentModule(new OptionsFragmentModule(getString(R.string.key_default_stored_elo)))
                .build();
        this.component.inject(this);

        this.elo_keys = getResources().getStringArray(R.array.elo_keys);

        this.spinnerDefaultELO.setOnItemSelectedListener(this);
        this.switchDeleteStoredData.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.spinnerDefaultELO.setSelection(
                this.getELOKeyPosition(this.getDefaultELOUseCase.getDefaultELO()),
                false
        );
    }

    private int getELOKeyPosition(String elo) {
        int length = this.elo_keys.length;
        for(int i = 0; i < length; i++) {
            if (this.elo_keys[i].equals(elo)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        this.setDefaultELOUseCase.setDefaultELO(this.elo_keys[i]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            this.buildDialog();
        }
        compoundButton.setChecked(false);
    }

    private void buildDialog() {
        Dialog.showDialog(
                this.context,
                getString(R.string.options_dialog_delete_title),
                getString(R.string.options_dialog_delete_description),
                getString(R.string.options_dialog_delete_positive_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteStoredData();
                    }
                },
                getString(R.string.options_dialog_delete_negative_text),
                null
        );
    }

    private void deleteStoredData() {
        this.deleteStoredDataUseCase.deleteStoredData();
        Toast.makeText(this.context, getString(R.string.options_message_data_deleted), Toast.LENGTH_LONG).show();
    }
}
