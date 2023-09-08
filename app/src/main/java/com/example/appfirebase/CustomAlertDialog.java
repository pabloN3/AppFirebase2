package com.example.appfirebase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class CustomAlertDialog extends DialogFragment {
    private String title;
    private String message;
    private  String text_btn_positive;

    private  String text_btn_negative;
    private DialogInterface.OnClickListener positiveClickListener;
    private DialogInterface.OnClickListener negativeClickListener;

    // Constructor que acepta títulos, mensajes y acciones personalizados
    public CustomAlertDialog(String title, String message, String msg_btn_positive, String msg_btn_negative,
                             DialogInterface.OnClickListener positiveClickListener,
                             DialogInterface.OnClickListener negativeClickListener) {
        this.title = title;
        this.message = message;
        this.text_btn_negative = msg_btn_negative;
        this.text_btn_positive = msg_btn_positive;
        this.positiveClickListener = positiveClickListener;
        this.negativeClickListener = negativeClickListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(text_btn_positive, positiveClickListener)
                .setNegativeButton(text_btn_negative, negativeClickListener);

        return builder.create();
    }

}
