package com.example.finalyearproject.Activities.ViewUsers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.finalyearproject.R;

import org.jetbrains.annotations.NotNull;

public class ContactDialog extends AppCompatDialogFragment {
//    private ContactDialog.ContactDialogListener mListener;
    private String mEmail;
    private String mPhoneNo;

    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder lBuilder=new AlertDialog.Builder(getActivity());
        mEmail = getArguments().getString("email");
        mPhoneNo = getArguments().getString("phoneNo");
        View lView=getActivity().getLayoutInflater().inflate(R.layout.contact_user_dialog,null);
        LinearLayout lLinearLayoutEmail=lView.findViewById(R.id.contact_email);
        LinearLayout lLinearLayoutWhatsapp=lView.findViewById(R.id.contact_whatsapp);
//        LinearLayout lLinearLayoutSms=lView.findViewById(R.id.contact_sms);
        LinearLayout lLinearLayoutCall=lView.findViewById(R.id.contact_call);
        if(mEmail==null){
            lLinearLayoutEmail.setVisibility(View.GONE);
        }
        if(mPhoneNo==null){
            lLinearLayoutWhatsapp.setVisibility(View.GONE);
//            lLinearLayoutSms.setVisibility(View.GONE);
            lLinearLayoutCall.setVisibility(View.GONE);
        }

        lLinearLayoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mEmail});
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });
        lLinearLayoutWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = mPhoneNo.substring(1);
                String url = "https://api.whatsapp.com/send?phone="+"+254"+str;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        lLinearLayoutCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "Your Phone_number"));
                startActivity(intent);
            }
        });
//        lLinearLayoutSms.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                intent.setType("vnd.android-dir/mms-sms");
//                intent.putExtra("address", mPhoneNo);
//                startActivity(intent);
//
//            }
//        });
        lBuilder.setView(lView).setTitle("Contact User")
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


        return lBuilder.create();
    }


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        View lView=getView();

//        try {
//            mListener = (ContactDialogListener) context;
//        }catch (ClassCastException e){
//            throw new ClassCastException(context.toString()+"must implement ContactDialogListener");
//        }
    }

    public interface ContactDialogListener{
        void applyText(String email,String password);
    }


}
