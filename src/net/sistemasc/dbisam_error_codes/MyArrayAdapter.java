package net.sistemasc.dbisam_error_codes;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Formats every row in list view moving data from current item to every control
 * Visual part is in list_item.xml
 *
 */
public class MyArrayAdapter extends ArrayAdapter<Item> {

  private int resource;

  public MyArrayAdapter(Context _context, int _resource, List<Item> _items) {
    super(_context, _resource, _items);
    resource = _resource;
  }

  /* 
   * Returns a full-filled LinearLayout
   */
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LinearLayout result;
    Item item = getItem(position); // current item

    // if already inflated returns saved View
    if (convertView == null) {
      result = new LinearLayout(getContext());
      String inflater = Context.LAYOUT_INFLATER_SERVICE;
      LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
      vi.inflate(resource, result, true);
    } else {
      result = (LinearLayout) convertView;
    }

    // fetching controls
    TextView errIdView = (TextView)result.findViewById(R.id.tvRowId);
    TextView errMsgView = (TextView)result.findViewById(R.id.tvRowMsg);
    // changing some appearance styles. 
    errIdView.setTextAppearance(result.getContext(),android.R.style.TextAppearance_Large);
      
    errIdView.setText(item.toString());
    errMsgView.setText(item.getErrMsg());

    return result;
  }
}