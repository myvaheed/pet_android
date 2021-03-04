package not.forgot.again.ui.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

import not.forgot.again.R;

public class LoadingDialog extends DialogFragment {
    private static volatile LoadingDialog INSTANCE;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext()).setCancelable(false)
                .setView(R.layout.dialog_loading)
                .setCancelable(false)
                .create();
    }

    public LoadingDialog() {}

    public synchronized static void showDialog(FragmentManager fragmentManager) {
        if (INSTANCE == null) {
            INSTANCE = new LoadingDialog();
        }
        if (INSTANCE.isVisible()) {
            return;
        }
        INSTANCE.setCancelable(false);
        INSTANCE.show(fragmentManager, null);
    }

    public synchronized static void dismissDialog() {
        if (INSTANCE == null) return;
        INSTANCE.dismiss();
        INSTANCE = null;
    }

}
