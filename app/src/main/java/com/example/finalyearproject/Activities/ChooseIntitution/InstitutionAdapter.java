package com.example.finalyearproject.Activities.ChooseIntitution;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Institution;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.finalyearproject.Activities.Launch.LaunchActivity.INSTITUTION_DETAILS;

public class InstitutionAdapter extends RecyclerView.Adapter<InstitutionAdapter.InstitutionViewHolder>{
    ArrayList<String> mInstList;
    Activity mActivity;
    ArrayList<String> mInstNameList;

    public InstitutionAdapter(Activity activity, ArrayList<String> instList, ArrayList<String> instNameList) {
        this.mInstList =instList;
        this.mActivity =activity;
        this.mInstNameList=instNameList;

    }

    private void getInstDetails(String code) {
            FirebaseUtils.FIRESTORE.collection(FirebaseUtils.INSTITUTIONS).document(code)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        Institution selectedInst=task.getResult().toObject(Institution.class);
                        Intent lIntent=new Intent(mActivity, MainActivity.class);
                        lIntent.putExtra(INSTITUTION_DETAILS, selectedInst);
                        lIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mActivity.startActivity(lIntent);
                        mActivity.finish();
                    }
                }
            });
    }


    @NonNull
    @NotNull
    @Override
    public InstitutionViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.institution_card, parent, false);
        InstitutionViewHolder lInstitutionViewHolder= new InstitutionViewHolder(view);
        return lInstitutionViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull InstitutionAdapter.InstitutionViewHolder holder, int position) {
        String lInstName=mInstNameList.get(position);
        String lInstCode=mInstList.get(position);
        holder.mTvName.setText(lInstName);
        holder.mRCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInstDetails(lInstCode);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mInstList.size();
    }

    public class InstitutionViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTvName;
        private final RelativeLayout mRCard;

        public InstitutionViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mRCard = itemView.findViewById(R.id.inst_card);
            mTvName = itemView.findViewById(R.id.tv_inst_name);
        }
    }

}
