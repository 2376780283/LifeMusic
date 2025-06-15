package com.zmusicfx.musicfx;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.zmusicfx.musicfx.Compatibility.Service;

import java.util.ArrayList;
import java.util.List;

import zzh.lifeplayer.music.R;
public class ControlPanelPicker extends AppCompatActivity {

    private MatrixCursor mCursor;
    private int mCheckedItem = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] cols = new String[]{"_id", "title", "package", "name"};
        mCursor = new MatrixCursor(cols);

        PackageManager pmgr = getPackageManager();
        Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
        List<ResolveInfo> ris = pmgr.queryIntentActivities(intent, PackageManager.GET_DISABLED_COMPONENTS);

        SharedPreferences pref = getSharedPreferences("musicfx", MODE_PRIVATE);
        String savedDefPackage = pref.getString("defaultpanelpackage", null);
        String savedDefName = pref.getString("defaultpanelname", null);

        ArrayList<String> titles = new ArrayList<>();
        int index = 0;
        for (ResolveInfo info : ris) {
            if (info.activityInfo.name.equals(Compatibility.Redirector.class.getName())) {
                continue;
            }

            CharSequence label = pmgr.getApplicationLabel(info.activityInfo.applicationInfo);
            titles.add(label.toString());

            mCursor.addRow(new Object[]{index, label.toString(), info.activityInfo.packageName, info.activityInfo.name});

            if (info.activityInfo.name.equals(savedDefName) &&
                info.activityInfo.packageName.equals(savedDefPackage) &&
                info.activityInfo.enabled) {
                mCheckedItem = index;
            }

            index++;
        }

        showSelectionDialog(titles);
    }

    private void showSelectionDialog(List<String> titles) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.picker_title);

        builder.setSingleChoiceItems(
            titles.toArray(new String[0]),
            mCheckedItem,
            (dialog, which) -> mCheckedItem = which
        );

        builder.setPositiveButton(R.string.fx_ok, (dialog, which) -> {
            if (mCheckedItem >= 0) {
                mCursor.moveToPosition(mCheckedItem);
                String defPackage = mCursor.getString(2);
                String defName = mCursor.getString(3);

                Intent updateIntent = new Intent(this, Service.class);
                updateIntent.putExtra("defPackage", defPackage);
                updateIntent.putExtra("defName", defName);
                startService(updateIntent);
            }
        });

        builder.setNegativeButton(R.string.fx_cancel, null);
        builder.create().show();
    }
}