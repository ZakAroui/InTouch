package com.ikazme.intouch.ui;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.ContactsContract.CommonDataKinds;

import com.ikazme.intouch.R;
import com.ikazme.intouch.service.ContactsService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ikazme
 */

public class ContactAdapter extends CursorAdapter {
    private Context context;

    public ContactAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView photoIv = view.findViewById(R.id.contactPhoto);
        String photoUri = cursor.getString(cursor.getColumnIndex(Data.PHOTO_THUMBNAIL_URI));
        if(photoUri != null){
            Bitmap bm = getBitmapFromAsset(Uri.parse(photoUri));
            photoIv.setImageBitmap(getCroppedBitmap(bm));
        } else {
            photoIv.invalidate();
            photoIv.setImageResource(R.drawable.ic_face);
        }

        TextView nameTv = view.findViewById(R.id.contact_textview);
        String contactName = cursor.getString(cursor.getColumnIndex(Data.DISPLAY_NAME_PRIMARY));
        nameTv.setText(contactName);

        TextView emailTv = view.findViewById(R.id.email_textView);
        String contactEmail = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Email.ADDRESS));
        emailTv.setText(contactEmail);

        TextView phoneTv =  view.findViewById(R.id.phone_textView);
        int hasPnumber = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.Phone.HAS_PHONE_NUMBER));
        if (hasPnumber == 1){
            ContactsService cs = ContactsService.getInstance();
            String[] mSelectionArgs = { cursor.getString(cursor.getColumnIndex(Data.LOOKUP_KEY)) };
            String phoneNumber = cs.getPhoneNumber(mSelectionArgs, context);
            phoneTv.setText(phoneNumber);
        } else {
            phoneTv.setText("");
        }
    }

    private Bitmap getBitmapFromAsset(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

}
