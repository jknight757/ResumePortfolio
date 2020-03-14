//Jeffrey Knight
//Java2 1911
// CE04 contacts
package com.example.knightjeffrey_ce04_java2.fragments;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.knightjeffrey_ce04_java2.Contact;
import com.example.knightjeffrey_ce04_java2.R;
import android.provider.ContactsContract;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Objects;

public class ContactListFragment extends ListFragment {

    private static final String PERMISSION_CONTACT_DATA = "android.permission.READ_CONTACTS";
    private final static String[] CONTACT_COLUMNS = { ContactsContract.Contacts.NAME_RAW_CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
    private ArrayList<Contact> contacts;
    private ContactListener listener;

    public ContactListFragment() {
        // Required empty public constructor
    }
    public interface ContactListener{
        void sendContact(Contact contact);
        void sendFirstContact(Contact contact);
    }

    public static ContactListFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ContactListFragment fragment = new ContactListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ContactListener){
            listener = (ContactListener) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Activity activity = getActivity();

        if(activity != null){

            ContentResolver cr = activity.getContentResolver();
            int resultCode = ContextCompat.checkSelfPermission(activity, PERMISSION_CONTACT_DATA);

            if( resultCode == PackageManager.PERMISSION_GRANTED){
                Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);

                if(cursor != null){
                    contacts = new ArrayList<>();
                    while(cursor.moveToNext()){
                        int id = cursor.getInt(cursor.getColumnIndex(CONTACT_COLUMNS[0]));
                        String idStr = id +"";
                        String name = cursor.getString(cursor.getColumnIndex(CONTACT_COLUMNS[1]));
                        String imagePath = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));

                        ArrayList<String> nums = new ArrayList<>();

                        if (cursor.getInt(cursor.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0){

                            Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{idStr},null);

                            while (phoneCursor != null && phoneCursor.moveToNext()){
                                nums.add(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                            }
                            Objects.requireNonNull(phoneCursor).close();
                        }else{
                            nums.add("000-000-000");
                        }

                        Log.i("Image path", "path: " + imagePath);
                        contacts.add(new Contact(name,nums,imagePath));
                        listener.sendFirstContact(contacts.get(0));

                    }
                    cursor.close();

                    if(getView() != null) {
                        ListView lv = getView().findViewById(R.id.list);


                        if (lv != null) {
                            ContactAdapter contactAdapter = new ContactAdapter(activity, contacts);
                            lv.setAdapter(contactAdapter);
                        }
                    }

                }
            }

        }


    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        listener.sendContact(contacts.get(position));

    }

    // Nested Custom Cursor adapter class
    class ContactAdapter extends BaseAdapter {

        private final Context mContext;
        private final ArrayList<Contact> mContacts;

        ContactAdapter(Context _context, ArrayList<Contact> _contacts) {
            mContext = _context;
            mContacts =_contacts;
        }

        @Override
        public int getCount() {
            if(mContacts != null){
                return mContacts.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if(mContacts != null && position >= 0 && position <mContacts.size()){
                return mContacts.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            Contact c = (Contact) getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_contact_layout, parent, false);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            }else{
                vh = (ViewHolder) convertView.getTag();
            }

            if(c != null){

               vh.nameTV.setText(c.getName());
               vh.numTV.setText(c.getNums().get(0));
               if(c.getImgPath() == null){
                   vh.imageView.setImageResource(R.drawable.profile);
               }else{
                   vh.imageView.setImageURI(Uri.parse(c.getImgPath()));
               }

            }

            return convertView;
        }


    }
    static class ViewHolder{
        final ImageView imageView;
        final TextView nameTV;
        final TextView numTV;

        ViewHolder(View _layout){

            imageView = _layout.findViewById(R.id.icon1);
            nameTV = _layout.findViewById(R.id.text1);
            numTV = _layout.findViewById(R.id.text2);
        }
    }

}
