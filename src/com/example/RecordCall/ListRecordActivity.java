package com.example.RecordCall;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Trung
 * Date: 2/18/14
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListRecordActivity extends Activity
{
    public static final String FILE_DIRECTORY = "recordedCalls";
    public ListView listRecord;
    //public ScrollView mScrollView;
    public TextView mTextView;
    public static final String LISTEN_ENABLED = "ListenEnabled";
    private static final int CATEGORY_DETAIL = 1;
    private static final int NO_MEMORY_CARD = 2;
    private static final int TERMS = 3;

    public RadioButton radEnable;
    public RadioButton radDisable;

    public static final int MEDIA_MOUNTED = 0;
    public static final int MEDIA_MOUNTED_READ_ONLY = 1;
    public static final int NO_MEDIA = 2;

    private static Resources res;
    private Context context;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records_list_layout);
        res = getResources();
        listRecord = (ListView) findViewById(R.id.records_list_layout_lvRecord);

//        SharedPreferences settings = this.getSharedPreferences(LISTEN_ENABLED, 0);
//        boolean silent = settings.getBoolean("silentMode", false);
//
//        if (!silent)
//            showDialog(CATEGORY_DETAIL);

        context = this.getBaseContext();
    }

    @Override
    protected void onResume()
    {
        if (Main.updateExternalStorageState() == MEDIA_MOUNTED)
        {
            String filepath = Environment.getExternalStorageDirectory().getPath();
            final File file = new File(filepath, FILE_DIRECTORY);

            if (!file.exists())
            {
                file.mkdirs();
            }

            final List<Model> listDir = ListDir2(file);

            if (listDir.isEmpty())
            {
                listRecord.setVisibility(ScrollView.GONE);
            }
            else
            {
                listRecord.setVisibility(ScrollView.VISIBLE);
            }

            final MyCallsAdapter adapter = new MyCallsAdapter(this, listDir);

            listRecord.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {

                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id)
                {
                    adapter.showPromotionPieceDialog(listDir.get(position)
                            .getCallName(), position);
                }
            });

            adapter.sort(new Comparator<Model>()
            {

                public int compare(Model arg0, Model arg1)
                {
                    Long date1 = Long.valueOf(arg0.getCallName().substring(1, 15));
                    Long date2 = Long.valueOf(arg1.getCallName().substring(1, 15));
                    return (date1 > date2 ? -1 : (date1 == date2 ? 0 : 1));
                }

            });

            listRecord.setAdapter(adapter);
        }
        else if (Main.updateExternalStorageState() == MEDIA_MOUNTED_READ_ONLY)
        {
            listRecord.setVisibility(ScrollView.GONE);
            showDialog(NO_MEMORY_CARD);
        }
        else
        {
            listRecord.setVisibility(ScrollView.GONE);
            showDialog(NO_MEMORY_CARD);
        }

        super.onResume();
    }

    private List<Model> ListDir2(File f)
    {
        File[] files = f.listFiles();
        List<Model> fileList = new ArrayList<Model>();
        for (File file : files)
        {
            Model mModel = new Model(file.getName());
            String phonenum = mModel.getCallName().substring(16,
                    mModel.getCallName().length() - 4);
            String uriImage = getBitmapAvatar(phonenum);
            mModel.setUserNameFromContact(getContactName(phonenum));

            if (!uriImage.equals(""))
            {
                mModel.setUriImage(uriImage);
            }
            fileList.add(mModel);

        }

        Collections.sort(fileList);
        Collections.sort(fileList, Collections.reverseOrder());

        return fileList;
    }

    private String getContactName(String phoneNum)
    {
        String res = phoneNum;
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selection = null;// =
        // ContactsContract.CommonDataKinds.Phone.NUMBER
        // + " = ?";
        String[] selectionArgs = null;// = new String[] { "1111111" };
        Cursor names = getContentResolver().query(uri, projection, selection,
                selectionArgs, null);

        int indexName = names
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = names
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        if (names.getCount() > 0)
        {
            names.moveToFirst();
            do
            {
                String name = names.getString(indexName);
                String number = names.getString(indexNumber)
                        .replaceAll("-", "");

                if (number.compareTo(phoneNum) == 0)
                {
                    res = name;
                    break;
                }

            } while (names.moveToNext());
        }

        return res;
    }

    private String getBitmapAvatar(String phoneNum)
    {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID
        };
        String[] params = new String[]{phoneNum};
        Cursor cur = getContentResolver().query(uri, projection, ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
                params, null);
        int uriImageIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
        if (cur.getCount() > 0)
        {
            cur.moveToFirst();
            String urlImage = cur.getString(uriImageIndex);
            if (urlImage != null || !urlImage.equals(""))
            {
                return urlImage;
            }
        }
        return "";
    }
}