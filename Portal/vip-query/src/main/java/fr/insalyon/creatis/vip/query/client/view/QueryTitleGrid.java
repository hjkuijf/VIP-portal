package fr.insalyon.creatis.vip.query.client.view;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.FileLoader;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.MouseOverHandler;
import fr.insalyon.creatis.vip.core.client.CoreModule;
import fr.insalyon.creatis.vip.core.client.view.application.ApplicationsTileGrid;
import fr.insalyon.creatis.vip.core.client.view.layout.Layout;
import fr.insalyon.creatis.vip.query.client.rpc.QueryService;
import fr.insalyon.creatis.vip.query.client.view.launch.QueryLaunchTab;
import fr.insalyon.creatis.vip.query.server.dao.business.MySQLDAOFactory;
import fr.insalyon.creatis.vip.query.server.dao.business.QueryDAOFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Boujelben
 */
public class QueryTitleGrid extends ApplicationsTileGrid {

   // private List<String> applicationNames;
     private HashMap<Key, String> map;
    
     private static QueryTitleGrid  instance;

    // Singleton
    
     public static QueryTitleGrid getInstance() {
        if (instance == null) {
            instance = new QueryTitleGrid();
        }
        
        return instance;
    }
     
    public QueryTitleGrid() {
        super("My Queries");
        this.setID(id);
        //applicationNames = new ArrayList<String>();
        loadApplications();
    }

   
    /**
     *
     * @param queryName
     * @param queryVersion
     */
    
    
    @Override
    public void parse(String queryName, String queryVersion) {
        String queryVersionID;
        queryVersionID = map.get(new Key(queryName, queryVersion));
        Layout.getInstance().addTab(new QueryLaunchTab(queryName, queryVersionID, queryVersion));

    }

    public void loadApplications() {

        final AsyncCallback<List<String[]>> callback;
        callback = new AsyncCallback<List<String[]>>() {
            @Override
            public void onFailure(Throwable caught) {
                Layout.getInstance().setWarningMessage("Unable to load queries:<br />" + caught.getMessage());
            }

            @Override
            public void onSuccess(List<String[]> result) {
                map = new HashMap<Key, String>();
                for (String[] q : result) {
                    //name,version,image
                   
                    addApplication(q[0], "v." + q[2], QueryConstants.APP_IMG_QUERY); 
                    map.put(new Key(q[0], "v." + q[2]), q[3]);
                   // applicationNames.add(q[0] + " " + q[2]);
                   
                    
                }
            }
        };
        QueryService.Util.getInstance().getQueries(callback);
        
    }

    public class Key {

        private String name;
        private String version;

        /**
         *
         * @param name
         * @param version
         */
        public Key(String name, String version) {
            this.name = name;
            this.version = version;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof Key) {
                Key s = (Key) obj;
                return name.equals(s.name) && version.equals(s.version);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (name + version).hashCode();
        }
    }
    
   

    
    
    public void addQuery(){
          // addApplication(queryName, "v." + version, QueryConstants.APP_IMG_QUERY);   
          // map.put(new Key(queryName, "v." + version), versionID);
           loadApplications();
          
          
           
    }
}