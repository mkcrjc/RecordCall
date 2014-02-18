
package com.example.RecordCall;

import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MyCallsAdapter extends ArrayAdapter<Model> {

	private final Context context;
	private List<Model> list;
	public static final String FILE_DIRECTORY = "recordedCalls";

    private static final String[] PHOTO_ID_PROJECTION = new String[] {
            ContactsContract.Contacts.PHOTO_ID
    };

    private static final String[] PHOTO_BITMAP_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Photo.PHOTO
    };

    private final ContentResolver contentResolver;

    public MyCallsAdapter(Context context, List<Model> list) {
		super(context, R.layout.record_list_item, list);
		this.list = list;
		this.context = context;
        contentResolver = context.getContentResolver();
    }
	
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.record_list_item, parent, false);
		final TextView tvName = (TextView) rowView.findViewById(R.id.record_list_item_tvName);
        final TextView tvDate = (TextView) rowView.findViewById(R.id.record_list_item_tvDate);
        final ImageView ivAvatar = (ImageView)rowView.findViewById(R.id.record_list_item_ivAvatar);

        if (list.get(position).getUriImage() != null)
        {
            Bitmap bitmap = getBitmap(Long.parseLong(list.get(position).getUriImage()));
            ivAvatar.setImageBitmap(bitmap);
        }
        else
        {
            ivAvatar.setImageResource(R.drawable.ava_default_person);
        }

		String myDateStr = list.get(position).getCallName().substring(1, 15);
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyyMMddkkmmss");
		
		Date dateObj = new Date();
		try {
			dateObj = curFormater.parse(myDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        tvDate.setText(DateFormat.getDateInstance().format(dateObj) + " " + DateFormat.getTimeInstance().format(dateObj));
		String myPhone = list.get(position).getCallName().substring(16, list.get(position).getCallName().length() - 4);
		
		if (!myPhone.matches("^[\\d]{1,}$"))
		{
			myPhone = context.getString(R.string.withheld_number);
		}
		else if (list.get(position).getUserNameFromContact() != myPhone)
		{
			myPhone = list.get(position).getUserNameFromContact();
		}

        tvName.setText(myPhone);
		
		return rowView;
	}
	
	/**
     * shows dialog of promotion tools
     */
    public void showPromotionPieceDialog(final String fileName, final int position)
    {
    	
    	final CharSequence[] items = {context.getString(R.string.options_delete), context.getString(R.string.confirm_play), context.getString(R.string.confirm_send)};
    	
    	new AlertDialog.Builder (context)
    	.setTitle(R.string.options_title)
    	.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        if (item == 0)
    	    	{
    	        	DeleteRecord(fileName, position);
    	    	}
    	    	else if (item == 1)
    	    	{
    	    		//startPlay(fileName);
    	    		startPlayExternal(fileName);
    	    	}
    	    	else if (item == 2)
    	    	{
    	    		sendMail(fileName);
    	    	}
    	    	
    	    }
    	})
    	.show();
    	
    	
    }
    
    void DeleteRecord(final String fileName, final int position)
    {
    	new AlertDialog.Builder (context)
        .setTitle (R.string.confirm_delete_title)
        .setMessage (R.string.confirm_delete_text)
        .setPositiveButton (R.string.confirm_delete_yes, new DialogInterface.OnClickListener(){
            public void onClick (DialogInterface dialog, int whichButton){
            	String filepath = Environment.getExternalStorageDirectory().getPath() + "/" + FILE_DIRECTORY;
            	File file = new File(filepath, fileName);
        		
        		if (file.exists()) {
        			file.delete();
        			list.remove(position);
        			notifyDataSetChanged();
        		}
            }
        })
        .setNegativeButton(R.string.confirm_delete_no, new DialogInterface.OnClickListener(){
            public void onClick (DialogInterface dialog, int whichButton){
                
                
            }
        })
        .show ();
    }
	
	void sendMail(String fileName)
	{
		Intent sendIntent;

		sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.sendMail_subject));
		sendIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.sendMail_body));
		sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + Environment.getExternalStorageDirectory().getPath() + "/" + FILE_DIRECTORY + "/" + fileName));
		sendIntent.setType("audio/mpeg");

		context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.send_mail)));
	}
	
	void startPlayExternal(String charSequence)
	{
		Uri intentUri = Uri.parse("file://" + Environment.getExternalStorageDirectory().getPath() + "/" + FILE_DIRECTORY + "/" + charSequence);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(intentUri, "audio/mpeg");
		context.startActivity(intent);
	}
	
	public void removeFromList(int position)
	{
		list.remove(position);
	}
    public Bitmap getBitmap(Long photoId)
    {
        BitmapDrawable bd=(BitmapDrawable) context.getResources().getDrawable(R.drawable.ava_default_person);
        int width=bd.getBitmap().getWidth();
        int height=bd.getBitmap().getHeight();
        final Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photoId);
        final Cursor cursor = contentResolver.query(uri, PHOTO_BITMAP_PROJECTION, null, null, null);
        try {
            Bitmap thumbnail = null;
            if (cursor.moveToFirst()) {
                final byte[] thumbnailBytes = cursor.getBlob(0);
                if (thumbnailBytes != null) {
                    thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
                }
            }
//            return Bitmap.createScaledBitmap(thumbnail, width, height, false);
            return thumbnail;
        }
        finally {
            cursor.close();
        }
    }
}
