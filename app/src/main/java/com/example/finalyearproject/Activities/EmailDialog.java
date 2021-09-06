package com.example.finalyearproject.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.finalyearproject.R;

import org.jetbrains.annotations.NotNull;

public class EmailDialog extends AppCompatDialogFragment {
    private EditText mEtEmail;
    private EditText mEtPassword;
    private EmailDialogListener mListener;
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder lBuilder=new AlertDialog.Builder(getActivity());
        View lView=getActivity().getLayoutInflater().inflate(R.layout.get_email_credentials_dialog,null);
        lBuilder.setView(lView).setTitle("Enter Credentials")
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email=mEtEmail.getText().toString().trim();
                String password=mEtPassword.getText().toString().trim();
                mListener.applyText(email,password);

            }
        });
        mEtEmail=lView.findViewById(R.id.current_email);
        mEtPassword=lView.findViewById(R.id.current_password);

        return lBuilder.create();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            mListener = (EmailDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"must implement EmailDialogListener");
        }
    }

    public interface EmailDialogListener{
        void applyText(String email,String password);
    }
}
