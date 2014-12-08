package nu.info.zeeshan.rnf;

import nu.info.zeeshan.adapters.NewsAdapter;
import nu.info.zeeshan.dao.DbHelper;
import nu.info.zeeshan.dao.DbStructure;
import nu.info.zeeshan.utility.Utility;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentNews extends Fragment {
	SharedPreferences spf;
	String feed_url;
	static ViewHolder holder;
	static NewsAdapter adapter;
	static SQLiteDatabase db;
	static String TAG="nu.info.zeeshan.rnf.FragmentNews";
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		spf = ((MainActivity) getActivity()).getSharedPreferences(
				getString(R.string.pref_filename), Context.MODE_PRIVATE);
		feed_url = spf.getString(getString(R.string.pref_newsrss),
				getString(R.string.empty_feed));
		db = new DbHelper(getActivity()).getReadableDatabase();
		updateAdapter(getActivity());
		holder = new ViewHolder();
		holder.list = (ListView) rootView.findViewById(R.id.listViewFeed);
		holder.list.setEmptyView(rootView.findViewById(R.id.linearViewError));
		holder.list.setAdapter(adapter);
		holder.errorMsg = (TextView) rootView.findViewById(R.id.textViewError);
		holder.errorView = (LinearLayout) rootView
				.findViewById(R.id.linearViewError);
		rootView.setTag(holder);
		return rootView;
	}

	public static void updateAdapter(Context context) {
		String[] select = { DbStructure.FeedTable._ID,
				DbStructure.FeedTable.COLUMN_TITLE,
				DbStructure.FeedTable.COLUMN_TEXT,
				DbStructure.FeedTable.COLUMN_TIME,
				DbStructure.FeedTable.COLUMN_IMAGE,
				DbStructure.FeedTable.COLUMN_LINK, };
		Cursor c = db.query(DbStructure.FeedTable.TABLE_NAME, select, "type=1",
				null, null, null, DbStructure.FeedTable.COLUMN_TIME);
		if (adapter == null)
			adapter = new NewsAdapter(context, c);
		else
			adapter.changeCursor(c);
		adapter.notifyDataSetChanged();
		Utility.log(TAG,"dataset updated news");
	}

	public void showError(String msg) {
		holder.errorMsg.setText(msg);
		holder.errorView.setVisibility(View.VISIBLE);
	}

	public void hideError() {
		holder.errorView.setVisibility(View.GONE);
	}

	static class ViewHolder {
		ListView list;
		LinearLayout errorView;
		TextView errorMsg;
	}
}