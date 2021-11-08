package com.example.finalyearproject.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.NavObjects;
import com.example.finalyearproject.Modules.Notice;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.finalyearproject.Activities.Main.MainActivity.NAV_OBJECT;

public class ReportActivity extends AppCompatActivity {
    public static File pFile;
    private File mStoragePath;
    private PDFView pdfView;
    private ArrayList<User> mUsersList;
    private NavObjects mNavObjects;
    private ArrayList<String> mIdList;
    private ArrayList<Notice> mNoticesList;
    private ArrayList<String> mSenderNames;
    private PdfPTable mTable;
    private Document mDocument;
    private String mDomainName;
    private String mUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.changeTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Report Generation");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        pdfView = findViewById(R.id.payment_pdf_viewer);
        Button shareBt=findViewById(R.id.btn_share_report);
//        payfile = new File("/storage/emulated/0/Report/");
        mStoragePath = new File(this.getExternalFilesDir(""),"Reports");
        Intent lIntent=getIntent();
        mUsersList = (ArrayList<User>) lIntent.getSerializableExtra("UserList");
        mDomainName = lIntent.getStringExtra("DomainName");
        mUserType = lIntent.getStringExtra("UserType");
        mNavObjects = (NavObjects) lIntent.getParcelableExtra(NAV_OBJECT);


        //check if they exist, if not create them(directory)
        if ( !mStoragePath.exists()) {
            mStoragePath.mkdirs();
        }
        long unixTime = System.currentTimeMillis() / 1000L;
        pFile = new File(mStoragePath, "Report"+unixTime+".pdf");
        if(mNavObjects!=null){
            mIdList = mNavObjects.getIdList();
            try{
                createNoticesReport();
            }catch (DocumentException | FileNotFoundException e){
                e.printStackTrace();
            }
        }else if(mUsersList !=null){
            try {
                createReport("First Name","Email","Phone No");
            } catch ( DocumentException| FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        shareBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pFile.exists()) {
                    Uri uri= FileProvider.getUriForFile(ReportActivity.this,getPackageName()+".provider",pFile);
                    Intent intent = ShareCompat.IntentBuilder.from(ReportActivity.this)
                            .setType("application/pdf")
                            .setStream(uri)
                            .setChooserTitle("Choose bar")
                            .createChooserIntent()
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    ReportActivity.this.startActivity(intent);
                }
            }
        });


    }
    private void createNoticesReport() throws DocumentException,FileNotFoundException{
        mNoticesList = new ArrayList<>();
        mSenderNames=new ArrayList<>();
        CollectionReference mNoticeRef; mNoticeRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mNavObjects.getInstDetails().getCode()).collection("notices");
        if(mIdList.size()>0){
            mNoticeRef = mNoticeRef.document(mIdList.get(mIdList.size()-1)).collection("my_notices");
        }else{
            mNoticeRef = mNoticeRef.document("0").collection("my_notices");
        }
        mNoticeRef.orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("SnapshotListener", error.getMessage());
                    return;
                }
                if(value.getDocumentChanges().size()>0) {

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                String id = dc.getDocument().getId();
                                Notice lNotice = dc.getDocument().toObject(Notice.class).withId(id);
                                mNoticesList.add(lNotice);
                                FirebaseUtils.FIRESTORE.collection("users").document(lNotice.getSenderId())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            mSenderNames.add((String) task.getResult().get(User.USERNAME));
                                            if(mSenderNames.size()==value.getDocumentChanges().size()){
                                                try {
                                                    createReport("Notice subject", "Date", "Sender");
                                                }catch (FileNotFoundException|DocumentException e){
                                                    Log.d("Report Exception", "onEvent: "+e.getMessage());
                                                }
                                            }
                                        }
                                    }
                                });
                                Log.d("NoticeSender", lNotice.getSenderId());
                                break;
                            case MODIFIED:
                                break;
                            case REMOVED:
                                break;
                        }

                    }

                }
            }
        });


    }

    private void createReport(String column1Title, String column2Title, String column3Title) throws FileNotFoundException, DocumentException {
        BaseColor colorWhite = WebColors.getRGBColor("#ffffff");
        BaseColor colorBlue = WebColors.getRGBColor("#056FAA");
        BaseColor grayColor = WebColors.getRGBColor("#425066");


        Font white = new Font(Font.FontFamily.HELVETICA, 15.0f, Font.BOLD, colorWhite);
        FileOutputStream output = new FileOutputStream(pFile);
        mDocument = new Document(PageSize.A4);
        mTable = new PdfPTable(new float[]{6, 25, 20, 20});
        mTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        mTable.getDefaultCell().setFixedHeight(50);
        mTable.setTotalWidth(PageSize.A4.getWidth());
        mTable.setWidthPercentage(100);
        mTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        Chunk noText = new Chunk("No.", white);
        PdfPCell noCell = new PdfPCell(new Phrase(noText));
        noCell.setFixedHeight(50);
        noCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        noCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk nameText = new Chunk(column1Title, white);
        PdfPCell nameCell = new PdfPCell(new Phrase(nameText));
        nameCell.setFixedHeight(50);
        nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        nameCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk phoneText = new Chunk(column2Title, white);
        PdfPCell phoneCell = new PdfPCell(new Phrase(phoneText));
        phoneCell.setFixedHeight(50);
        phoneCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        phoneCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk amountText = new Chunk(column3Title, white);
        PdfPCell amountCell = new PdfPCell(new Phrase(amountText));
        amountCell.setFixedHeight(50);
        amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        amountCell.setVerticalAlignment(Element.ALIGN_CENTER);


        Chunk footerText = new Chunk("Notify Auto Report");
        PdfPCell footCell = new PdfPCell(new Phrase(footerText));
        footCell.setFixedHeight(70);
        footCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        footCell.setVerticalAlignment(Element.ALIGN_CENTER);
        footCell.setColspan(4);


        mTable.addCell(noCell);
        mTable.addCell(nameCell);
        mTable.addCell(phoneCell);
        mTable.addCell(amountCell);
        mTable.setHeaderRows(1);

        PdfPCell[] cells = mTable.getRow(0).getCells();


        for (PdfPCell cell : cells) {
            cell.setBackgroundColor(grayColor);
        }
        if(mUsersList !=null){
            insertUserDetails(mTable);
        }else if(mNavObjects!=null){
            insertNoticeDetails(mTable);
        }



        PdfPTable footTable = new PdfPTable(new float[]{6, 25, 20, 20});
        footTable.setTotalWidth(PageSize.A4.getWidth());
        footTable.setWidthPercentage(100);
        footTable.addCell(footCell);

        PdfWriter.getInstance(mDocument, output);
        mDocument.open();
        Font g = new Font(Font.FontFamily.HELVETICA, 25.0f, Font.NORMAL, grayColor);
        if(mUsersList !=null){
            mDocument.add(new Paragraph(mDomainName+" "+mUserType+" "+"report\n\n", g));
        }else if(mNavObjects!=null){
            mDocument.add(new Paragraph(mNavObjects.getDomainName()+" "+"notices report\n\n", g));
        }
        mDocument.add(mTable);
        mDocument.add(footTable);

        mDocument.close();
        DisplayReport();

    }

    private void insertNoticeDetails(PdfPTable table) {
        for (int i = 0; i < mNoticesList.size(); i++) {
            Notice lNotice = mNoticesList.get(i);

            String id = String.valueOf(i + 1);
            String name = lNotice.getSubject();
            Date currentDate = new Date(lNotice.getTimeStamp()*1000);
            SimpleDateFormat dateFormat= new SimpleDateFormat("dd MMM yyyy ");
            String lDate = dateFormat.format(currentDate);
            String phone = mSenderNames.get(i);


            table.addCell(id + ". ");
            table.addCell(name);
            table.addCell(lDate);
            table.addCell(phone);

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertUserDetails(PdfPTable table) {
        for (int i = 0; i < mUsersList.size(); i++) {
            User lUser = mUsersList.get(i);

            String id = String.valueOf(i + 1);
            String name = lUser.getUsername();
            String lEmail = lUser.getEmail();
            String phone = lUser.getPhoneNo();


            table.addCell(id + ". ");
            table.addCell(name);
            table.addCell(lEmail);
            table.addCell(phone);

        }
    }

    private void DisplayReport()
    {
        pdfView.fromFile(pFile)
                .pages(0,2,1,3,3,3)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();


    }

}
