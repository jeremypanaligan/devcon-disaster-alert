package com.devcon.devise;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;

public class MenuNewsFeed extends Fragment {
	private final String WEB_URL = "http://www.ajdeguzman.x10.mx/api/books.xml";
	MyTask task;
	String[] books = new String[] {"book_title", "book_published", "book_author", "book_genre"};
	int[] books_layout = new int[]{R.id.book_title, R.id.book_published, R.id.book_author, R.id.book_genre};
	private PullToRefreshListView lstBooks;
	private ProgressDialog pd; 
	
	Document doc;
	DocumentBuilder db;
	DocumentBuilderFactory dbf;
	
	public MenuNewsFeed(){}
	private MenuItem menuItem;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
        View rootView = inflater.inflate(R.layout.layout_home, container, false);
		lstBooks = (PullToRefreshListView) rootView.findViewById(R.id.pull_to_refresh_listview);
		lstBooks.setOnRefreshListener(new OnRefreshListener<ListView>() {
		    @Override
		    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		        // Do work to refresh the list here.
				task = new MyTask();
				task.execute();
		    }
		});
		SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(getActivity());
		soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
		soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
		soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
		lstBooks.setOnPullEventListener(soundListener);
        return rootView;
    }
	private class MyTask extends AsyncTask<String,String,List>{
		@Override
		protected List doInBackground(String... arg0) {
			List<HashMap<String, String>> allBooks = new ArrayList<HashMap<String, String>>();
			//call this method which connections to web
			try {
				dbf = DocumentBuilderFactory.newInstance();
				db = dbf.newDocumentBuilder();
				doc = db.parse(webConnect());
			} catch (Exception e) {
				e.printStackTrace();
			}
			NodeList nodeListBook = doc.getElementsByTagName("book");
			for(int i = 0; i < nodeListBook.getLength(); i++){
				HashMap<String, String> map = new HashMap<String, String>();
				Element elementBook = (Element) nodeListBook.item(i);
				
				NodeList nodeListTitle = elementBook.getElementsByTagName("title");	
				Element elementTitle = (Element) nodeListTitle.item(0);
				
				
				NodeList nodeListAuthor = elementBook.getElementsByTagName("author");	
				Element elementAuthor = (Element) nodeListAuthor.item(0);
				
				NodeList nodeListGenre = elementBook.getElementsByTagName("genre");
				Element elementGenre = (Element) nodeListGenre.item(0);
				
				NodeList nodeListDate= elementBook.getElementsByTagName("publish_date");
				Element elementPublish = (Element) nodeListDate.item(0);
				
				map.put("book_title", elementTitle.getFirstChild().getTextContent());
				map.put("book_author", elementAuthor.getFirstChild().getTextContent());
				map.put("book_genre", elementGenre.getFirstChild().getTextContent());
				map.put("book_published", elementPublish.getFirstChild().getTextContent());
				allBooks.add(map);
							
			}
			return allBooks;
		}
		@Override
		protected void onPostExecute(List str){
			SimpleAdapter simple = new SimpleAdapter(getActivity(), str, R.layout.books_layout, books, books_layout);
			lstBooks.setAdapter(simple);
			simple.notifyDataSetChanged();
			lstBooks.onRefreshComplete();
			super.onPostExecute(str);
		}
	}
	private InputStream webConnect(){
		InputStream in = null;
		try {
			URL url = new URL(WEB_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			in = conn.getInputStream();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return in;
	}
	
		
}
