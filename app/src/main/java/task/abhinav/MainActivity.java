package task.abhinav;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {


  ListView l1;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    l1 = (ListView)findViewById(R.id.list_view);

    int MyVersion = Build.VERSION.SDK_INT;
    if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
      if (!checkIfAlreadyhavePermission()) {
        requestForSpecificPermission();
      }
      else fetchContacts();
    }



    l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TextView textView = view.findViewById(R.id._id);
        showDetails(textView.getText().toString());

      }
    });

  }

  @Override
  protected void onResume() {
    super.onResume();
    fetchContacts();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    switch (requestCode) {
      case 101:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          //granted
          fetchContacts();
        } else {
          //not granted
          Toast.makeText(MainActivity.this, "Permission denied to read your Contacts", Toast.LENGTH_SHORT).show();
        }
        break;
      default:
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }




  public void fetchContacts(){
    Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
    startManagingCursor(cursor);
    String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
    ContactsContract.CommonDataKinds.Phone._ID};
    int[] to = {R.id.name,R.id.number,R.id._id};
    SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.contact_row,cursor,from,to);
    l1.setAdapter(simpleCursorAdapter);
    l1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

  }


  private boolean checkIfAlreadyhavePermission() {
    int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
    int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
    if (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED) {
      return true;
    } else {
      return false;
    }
  }



  private void requestForSpecificPermission() {
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE}, 101);
  }

  public void showDetails(String id){

    Intent intent = new Intent(this, Info.class);
    intent.putExtra("user_id", id);
    startActivity(intent);
  }
}
