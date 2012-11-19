package net.sistemasc.dbisam_error_codes;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MainActivity extends Activity {
  // Visual controls
  private ListView            myListView;
  private EditText            etSearchText;
  private Button              buSearch;
  // dialogs
  static final private int    DETAIL_DIALOG     = 1;
  static final private int    ABOUT_DIALOG      = 2;
  // menu related
  private static final int    ABOUT_MENU_ITEM   = Menu.FIRST;
  // Save/Restore state
  private static final String SELECTED_ITEM_IDX = "SELECTED_ITEM_IDX";
  private int                 mSelectedItemIdx;
  private Item                mSelectedItem;
  // data providers
  private MyDbAdapter         dbApapter;
  private Cursor              cursor;
  private MyArrayAdapter      arrayAdapter;
  private ArrayList<Item>     itemsArrayList;

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    // bind visual to code
    setContentView(R.layout.main);
    etSearchText = (EditText) findViewById(R.id.edSearchText);
    buSearch = (Button) findViewById(R.id.buSearch);
    buSearch.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        doSearchErr();
      }
    });
    
    myListView = (ListView) findViewById(R.id.myListView);
    myListView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> _av, View _v, int _index, long arg3) {
        mSelectedItem = itemsArrayList.get(_index);
        showDialog(DETAIL_DIALOG);
      }
    });
    
    itemsArrayList = new ArrayList<Item>();
    arrayAdapter = new MyArrayAdapter(
        this, 
        R.layout.list_item, // binds layout for viewing every item in ListView
        itemsArrayList);
    myListView.setAdapter(arrayAdapter);
    
    dbApapter = new MyDbAdapter(this);
    // open database
    dbApapter.open();
    // copy database data to stored ArrayList
    populateArrayList();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    // Group ID
    int groupId = 0;
    // Unique menu item identifier. Used for event handling.
    int menuItemId = ABOUT_MENU_ITEM;
    // The order position of the item
    int menuItemOrder = Menu.NONE;
    // Text to be displayed for this menu item.
    int menuItemText = R.string.about_menu;

    // Create the menu item and keep a reference to it.
    MenuItem menuItem = menu.add(groupId, menuItemId, menuItemOrder,
        menuItemText);
    menuItem.setIcon(android.R.drawable.ic_menu_info_details);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);
    // Find which menu item has been selected
    switch (item.getItemId()) {
      // Check for each known menu item
      case (ABOUT_MENU_ITEM): 
        showDialog(ABOUT_DIALOG);
        return true;
    } // switch

    // Return false if you have not handled the menu item.
    return false;    
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    // restore selected item in ListView
    mSelectedItemIdx = -1;
    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey(SELECTED_ITEM_IDX)) {
        mSelectedItemIdx = savedInstanceState.getInt(SELECTED_ITEM_IDX,-1);
      }
    }
    myListView.setSelection(mSelectedItemIdx);
    if (mSelectedItemIdx >= 0) {
      mSelectedItem = itemsArrayList.get(mSelectedItemIdx);
    }
    super.onRestoreInstanceState(savedInstanceState);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    int idx = itemsArrayList.indexOf(mSelectedItem);
    outState.putInt(SELECTED_ITEM_IDX, idx);
    super.onSaveInstanceState(outState);
  }

  protected void doSearchErr() {
    boolean bFound = false;
    String search = etSearchText.getText().toString();
    int iSearch;
    try {
      iSearch = Integer.parseInt(search);
    } catch (NumberFormatException e) {
      iSearch = 0;
    }
    for (int i = 0; i < itemsArrayList.size(); i++) {
      if (iSearch == itemsArrayList.get(i).getErrId()) {
        myListView.setSelection(i);
        bFound = true;
        mSelectedItem = itemsArrayList.get(i);
        showDialog(DETAIL_DIALOG);
        break;
      }
    } // for
    if (!bFound) {
      Context context = getApplicationContext();
      CharSequence text = getString(R.string.error_code_not_found);
      int duration = Toast.LENGTH_SHORT;

      Toast toast = Toast.makeText(context, text, duration);
      toast.show();      
    }
  }

  private void populateArrayList() {
    cursor = dbApapter.getAllRows();
    updateArrayList();
  }

  private void updateArrayList() {
    cursor.requery();
    itemsArrayList.clear();
    if (cursor.moveToFirst()) {
      do {
        // declare fields to store every column in query
        int errId = cursor.getInt(cursor.getColumnIndex(MyDbAdapter.KEY_ID));
        String errConst = cursor.getString(cursor.getColumnIndex(MyDbAdapter.ERR_CONST));
        String errMsg = cursor.getString(cursor.getColumnIndex(MyDbAdapter.ERR_MSG));
        String errDesc = cursor.getString(cursor.getColumnIndex(MyDbAdapter.ERR_DESC));
        Item errItem = new Item(errId,errConst,errMsg,errDesc);
        itemsArrayList.add(errItem);
      } while (cursor.moveToNext());
    }
    arrayAdapter.notifyDataSetChanged();
    if (itemsArrayList.size() > 0)
      mSelectedItem = itemsArrayList.get(0)
    ;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // close db
    dbApapter.close();
  }
  @Override
  public Dialog onCreateDialog(int id) {
    switch(id) {
      case (DETAIL_DIALOG) : 
        LayoutInflater liItemDetails = LayoutInflater.from(this);
        View itemDetailsView = liItemDetails.inflate(R.layout.item_details, null); // from item_details.xml

        // create an alert dialog to show item details
        AlertDialog.Builder itemDetailsDialog = new AlertDialog.Builder(this);
        itemDetailsDialog.setTitle(R.string.error_information);
        itemDetailsDialog.setIcon(android.R.drawable.btn_star_big_on);
        itemDetailsDialog.setPositiveButton(android.R.string.ok, null);
        itemDetailsDialog.setView(itemDetailsView); // binds xml visual definition to code
        return itemDetailsDialog.create();
      case (ABOUT_DIALOG) : 
        LayoutInflater liAbout = LayoutInflater.from(this);
        View aboutView = liAbout.inflate(R.layout.about, null); // from about.xml
        AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
        aboutDialog.setTitle(R.string.about_menu);
        aboutDialog.setIcon(android.R.drawable.ic_dialog_info);
        aboutDialog.setView(aboutView); // binds xml visual definition to code
      return aboutDialog.create();
    } // switch
    return null;
  }

  @Override
  public void onPrepareDialog(int id, Dialog _dialog) {
    switch(id) {
      case (DETAIL_DIALOG) :
        String itemTitle = null;
        String itemDesc = null;
        if (mSelectedItem != null) {
          itemTitle = mSelectedItem.toString();
          itemDesc = mSelectedItem.getErrMsg() + "\n" + "\n" + mSelectedItem.getErrDesc();
        }
        AlertDialog detailDialog = (AlertDialog) _dialog;
        detailDialog.setTitle(itemTitle);
        TextView tvItemDetails = (TextView) detailDialog.findViewById(R.id.tvItemDetails); // from item_details.xml
        tvItemDetails.setText(itemDesc);
        break;
      case (ABOUT_DIALOG) :
        AlertDialog aboutDialog = (AlertDialog) _dialog;
        aboutDialog.setTitle(R.string.about_menu);
        TextView tvAbout = (TextView) aboutDialog.findViewById(R.id.tvAbout); // from about.xml
        tvAbout.setText(Html.fromHtml(getString(R.string.more_info_text)));
        break;
    } // switch
  }
}