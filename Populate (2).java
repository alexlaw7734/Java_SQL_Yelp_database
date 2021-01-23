/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 * @author alexlaw
 */
package coen.pkg280.parse.and.populate;


import java.io.*;
import java.sql.*;
import java.util.*;
import java.lang.Long.*;
import org.json.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
*/
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


class Business {
    String business_id;
    String city;
    String state;
    String name;
    double stars;
    Business(String business_id, String city, String state, String name, double stars) {
        this.business_id = business_id;
        this.city = city;
        this.state = state;
        this.name = name;
        this.stars = stars;
    }
}


class MainCategory {
    String business_id;
    String mainCategory;
    MainCategory(String business_id, String mainCategory) {
        this.business_id = business_id;
        this.mainCategory = mainCategory;
    }
}

class SubCategory {
    String business_id;
    String subcategory;
    SubCategory(String business_id, String subcategory) {
        this.business_id = business_id;
        this.subcategory = subcategory;
    }
}
class Hour {
    String business_id;
    String hour;
    Hour(String business_id, String hour) {
        this.business_id = business_id;
        this.hour = hour;
    }
}

class Attribute {
    String business_id;
    String attribute;
    Attribute(String business_id, String attribute) {
        this.business_id = business_id;
        this.attribute = attribute;
    }
}



public class Populate {

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, ParseException {
        // TODO code application logic here
        Populate poprun = new Populate();
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection connection = null;
        try {

			connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521/XE", "system",
					"oracle");
                        System.out.println("connection suc");
		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}

        
        poprun.addMainCategories();
        poprun.Populate_business(connection);
        poprun.Populate_user(connection);
        poprun.Populate_Checkin(connection);
        poprun.Populate_reviews(connection);


        connection.close();

    }

/*
            File file = new File(BUSINESS_JSON_FILE_PATH);
        try (FileReader fileReader = new FileReader(file);
             BufferedReader reader = new BufferedReader(fileReader);
             Connection connection = getConnect();){
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject obj = new JSONObject(line);
                String business_id = obj.getString("business_id");
                String city = obj.getString("city");
                String state = obj.getString("state");
                String name = obj.getString("name");
                Double stars = obj.getDouble("stars");
                JSONArray arr = obj.getJSONArray("categories");
                for (int i = 0; i < arr.length(); i++) {
                    String category = arr.getString(i);
                    if (mainCategoriesHash.contains(category)) {
                        mainCategories.add(new MainCategory(business_id, category));
                    }
                    else {
                        subCategories.add(new SubCategory(business_id, category));
                    }
                }
                businesses.add(new Business(business_id, city, state, name, stars));
                
                JSONObject attributes1 = obj.getJSONObject("attributes");
                Iterator<?> keys1 = attributes1.keys();
                while (keys1.hasNext()) {
                    String key1 = (String) keys1.next();
                    StringBuilder sb1 = new StringBuilder(key1);
                    if (attributes1.get(key1) instanceof JSONObject) {
                        JSONObject attributes2 = attributes1.getJSONObject(key1);
                        Iterator<?> keys2 = attributes2.keys();
                        while (keys2.hasNext()) {
                            String key2 = (String) keys2.next();
                            StringBuilder sb2 = new StringBuilder(key2);
                            sb2.append("_");
                            sb2.append(attributes2.get(key2));
                            attributes.add(new Attribute(business_id, sb1.toString() + "_" + sb2.toString()));
                        }
                    }
                    else {
                        sb1.append("_");
                        sb1.append(attributes1.get(key1));
                        attributes.add(new Attribute(business_id, sb1.toString()));
                    }
                }
                
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(Populate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Populate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    */
private void Populate_business(Connection con) throws FileNotFoundException, IOException, SQLException{
        System.out.println("Populate_business");
        String file = "/Users/alexlaw/NetBeansProjects/Assignment3/yelp_business.json";
        BufferedReader br = new BufferedReader(new FileReader(file));
        int count = 0;
        int b_size = 500;
        String line = null;
        try{
            PreparedStatement preparedstmt = con.prepareStatement("INSERT into YELP_BUSINESS values(?,?,?,?,?,?,?)");
            while((line = br.readLine())!= null){
                JSONObject jo = new JSONObject(line);
                String b_id = jo.getString("business_id");
                String address = jo.getString("full_address");
                String city = jo.getString("city");
                String state = jo.getString("state");
                Integer reviews_count = jo.getInt("review_count");
                String b_name = jo.getString("name");
                Double stars = jo.getDouble("stars");
                
                parsing_categories_attributes(b_id, jo);
                
                preparedstmt.setString(1, b_id);
                preparedstmt.setString(2, address);                
                preparedstmt.setString(3, city);
                preparedstmt.setString(4, state);
                preparedstmt.setInt(5, reviews_count);
                preparedstmt.setString(6, b_name);                
                preparedstmt.setDouble(7, stars);
                preparedstmt.addBatch();
                count++;
                if(count % b_size == 0){
                    preparedstmt.executeBatch();
                    System.out.println(count);
                }
             
            }
            if(count % b_size != 0)
                preparedstmt.executeBatch(); 
            
            preparedstmt.close();
            populate_maincatgory(con);
            populate_subcatgory(con);
            populate_attribute(con);
            populate_hour(con);
        }
        catch(Exception e){
                e.printStackTrace();
        }
        
    }        

/*
private static void cleanTable() throws SQLException, ClassNotFoundException {
        try (Connection connection = getConnect()) {
            String sql;
            PreparedStatement preparedStatement;
            
//            System.out.println("Clean YelpUser table...");
//            sql = "DELETE FROM YelpUser";
//            preparedStatement = connection.prepareStatement(sql);
//            preparedStatement.executeUpdate();
//            
            System.out.println("Clean Review table...");
            sql = "DELETE FROM Review";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            
            System.out.println("Clean Attribute table...");
            sql = "DELETE FROM Attribute";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            
            System.out.println("Clean SubCategory table...");
            sql = "DELETE FROM SubCategory";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            
            System.out.println("Clean MainCategory table...");
            sql = "DELETE FROM MainCategory";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            preparedStatement.close();

*/
    private void Populate_user(Connection con) throws FileNotFoundException, IOException, SQLException{
        System.out.println("Populate_user");
        String file = "/Users/alexlaw/NetBeansProjects/Assignment3/yelp_user.json";
        BufferedReader br = new BufferedReader(new FileReader(file));
        int count = 0;
        int b_size = 500;
        String line = null;
        try{
            PreparedStatement preparedstmt = con.prepareStatement("INSERT into YELP_USER values(?,?)");
            while((line = br.readLine())!= null){
                JSONObject jo = new JSONObject(line);
                String fullname = jo.getString("name");
                String id =jo.getString("user_id");
                
                preparedstmt.setString(1, fullname);
                preparedstmt.setString(2, id);                

                preparedstmt.addBatch();
                count++;
                if(count % b_size == 0){
                    preparedstmt.executeBatch();
                    System.out.println(count);
                }
             
            }
            if(count % b_size != 0)
                preparedstmt.executeBatch(); 
            
            preparedstmt.close();
            //populate_attribute(con);
        }
        catch(Exception e){
                e.printStackTrace();
        }
        
    
    }

        private void Populate_Checkin( Connection con) throws FileNotFoundException, IOException, SQLException{
        System.out.println("Populate_checkin");
        String file = "/Users/alexlaw/NetBeansProjects/Assignment3/yelp_checkin.json";
        BufferedReader br = new BufferedReader(new FileReader(file));
        int count = 0;
        int b_size = 500;
        String line = null;
        try{
            PreparedStatement preparedstmt = con.prepareStatement("INSERT into YELP_CHECKIN values(?,?,?)");
            while((line = br.readLine())!= null){
                JSONObject jo = new JSONObject(line);
                //JSONArray info = jo.getJSONArray("checkin_info");
                String type = jo.getString("type");
                String id = jo.getString("business_id");                
              
                //preparedstmt.setArray(1, info);
                preparedstmt.setString(1, type); 
                preparedstmt.setString(2, id);    
                preparedstmt.setString(3, id);  

                preparedstmt.addBatch();
                count++;
                if(count % b_size == 0){
                    preparedstmt.executeBatch();
                    System.out.println(count);
                }
             
            }
            if(count % b_size != 0)
                preparedstmt.executeBatch(); 
            
            preparedstmt.close();
            //populate_attribute(con);
        }
        catch(Exception e){
                e.printStackTrace();
        }
        
    }  

/*
    private void Populate_Checkin(Connection con) throws IOException, SQLException, ClassNotFoundException {
        JSONParser parser = new JSONParser();
        Statement statement = null;
        statement = con.createStatement();
        statement.executeUpdate("DELETE YELP_CHECKIN");
        try {

		String jsonFile = "/Users/alexlaw/NetBeansProjects/Assignment3/yelp_checkin.json";
                BufferedReader br = new BufferedReader(new FileReader(jsonFile));
                String line = null;
                while((line = br.readLine()) != null){
                    Object obj = parser.parse(line);
                    JSONObject jobj = (JSONObject) obj;
                    String info = jobj.get("checkin_info").toString();
                    String type = (String) jobj.get("rev_type");
                    String id = (String) jobj.get("business_id");
                    System.out.println("info:" + info + " ");
                    System.out.println("type:" + type + " ");
                    System.out.println("id:" + id + " ");

                    PreparedStatement preparedStmt = con.prepareStatement("INSERT into YELP_CHECKIN VALUES(?,?,?)");
                    preparedStmt.setString (1, info);
                    preparedStmt.setString (2, type);
                    preparedStmt.setString (3, id);
                    preparedStmt.executeUpdate();
                    preparedStmt.close();
                }
		}catch (Exception e){
                    e.printStackTrace();
                }
    }
   */

    
    private void Populate_reviews( Connection con) throws FileNotFoundException, IOException, SQLException{
        System.out.println("Populate_reviews");
        String file = "/Users/alexlaw/NetBeansProjects/Assignment3/yelp_review.json";
        BufferedReader br = new BufferedReader(new FileReader(file));
        int count = 0;
        int b_size = 500;
        String line = null;
        try{
            PreparedStatement preparedstmt = con.prepareStatement("INSERT into YELP_REVIEW values(?,?,?,?,?,?,?)");
            while((line = br.readLine())!= null){
                JSONObject jo = new JSONObject(line);
                String u_id = jo.getString("user_id");
                String r_id =jo.getString("review_id");                
                Double stars = jo.getDouble("stars");
                String create_date = jo.getString("date");
                String text = jo.getString("text");
                String business_id = jo.getString("business_id");
                JSONObject votes = (JSONObject) jo.get("votes");
                    int vote_sum = 0;
                    vote_sum += (int) Integer.parseInt(votes.get("funny").toString());
                    vote_sum += (int) Integer.parseInt(votes.get("useful").toString());
                    vote_sum += (int) Integer.parseInt(votes.get("cool").toString());
                preparedstmt.setString (1, u_id);
                preparedstmt.setString (2, r_id);
                preparedstmt.setDouble (3, stars);
                preparedstmt.setString (4, create_date);
                preparedstmt.setString (5, text);
                preparedstmt.setString (6, business_id);
                preparedstmt.setInt (7, vote_sum);               

                preparedstmt.addBatch();
                count++;
                if(count % b_size == 0){
                    preparedstmt.executeBatch();
                    System.out.println(count);
                }
             
            }
            if(count % b_size != 0)
                preparedstmt.executeBatch(); 
            
            preparedstmt.close();
            //populate_attribute(con);
        }
        catch(Exception e){
                e.printStackTrace();
        }
        
    
    }    
    

    
   
      
        public static List<Business> businesses = new ArrayList();
        public static List<MainCategory> mainCategories = new ArrayList();
        public static List<SubCategory> subCategories = new ArrayList();
        public static List<Attribute> attributes = new ArrayList();
        public static HashSet<String> mainCategoriesHash = new HashSet();
        public static List<Hour> hours = new ArrayList();
        
    private void addMainCategories(){
        mainCategoriesHash.add("Active Life");
        mainCategoriesHash.add("Arts & Entertainment");
        mainCategoriesHash.add("Automotive");
        mainCategoriesHash.add("Car Rental");
        mainCategoriesHash.add("Cafes");
        mainCategoriesHash.add("Beauty & Spas");
        mainCategoriesHash.add("Convenience Stores");
        mainCategoriesHash.add("Dentists");
        mainCategoriesHash.add("Doctors");
        mainCategoriesHash.add("Drugstores");
        mainCategoriesHash.add("Department Stores");
        mainCategoriesHash.add("Education");
        mainCategoriesHash.add("Event Planning & Services");
        mainCategoriesHash.add("Flowers & Gifts");
        mainCategoriesHash.add("Food");
        mainCategoriesHash.add("Health & Medical");
        mainCategoriesHash.add("Home Services");
        mainCategoriesHash.add("Home & Garden");
        mainCategoriesHash.add("Hospitals");
        mainCategoriesHash.add("Hotels & Travel");
        mainCategoriesHash.add("Hardware Stores");
        mainCategoriesHash.add("Grocery");
        mainCategoriesHash.add("Medical Centers");
        mainCategoriesHash.add("Nurseries & Gardening");
        mainCategoriesHash.add("Nightlife");
        mainCategoriesHash.add("Restaurants");
        mainCategoriesHash.add("Shopping");
        mainCategoriesHash.add("Transportation");
    }

    private void parsing_categories_attributes(String id, JSONObject jo){
        JSONArray categories =jo.getJSONArray("categories");
                    
        for(int i = 0; i < categories.length(); i++){
            String category = categories.getString(i);
            if (mainCategoriesHash.contains(category)){
                mainCategories.add(new MainCategory(id, category));
            }         
            else{
                subCategories.add(new SubCategory(id, category));
            }
            
                        JSONObject allhours = jo.getJSONObject("hours");
            Iterator ikeys = allhours.keys();
            while(ikeys.hasNext()){
                String nextKeys = (String)ikeys.next();
                
                try{
                    if(allhours.get(nextKeys) instanceof JSONObject){
                        JSONObject subhours = allhours.getJSONObject(nextKeys);
                        Iterator subkeys = subhours.keys();
                        while(subkeys.hasNext()){
                            String nextsubKeys = (String)subkeys.next();
                            hours.add(new Hour(id, nextKeys + "" + nextsubKeys + "" + subhours.get(nextsubKeys)));
                        }
                    }                      
                    else{
                         hours.add(new Hour(id, nextKeys + "_" + allhours.get(nextKeys)));
                    }}catch (Exception e) {
                    e.printStackTrace();
                }
            
            
            }
            
            
            JSONObject allattributes = jo.getJSONObject("attributes");
            Iterator keys = allattributes.keys();
            
            while(keys.hasNext()){
                String nextKeys = (String)keys.next();
                
                try{
                    if(allattributes.get(nextKeys) instanceof JSONObject){
                        JSONObject subattributes = allattributes.getJSONObject(nextKeys);
                        Iterator subkeys = subattributes.keys();
                        while(subkeys.hasNext()){
                            String nextsubKeys = (String)subkeys.next();
                            attributes.add(new Attribute(id, nextKeys + "" + nextsubKeys + "" + subattributes.get(nextsubKeys)));
                            //System.out.println(nextKeys + "" + nextsubKeys + "" + subattributes.get(nextsubKeys));
                        }
                    }                      
                    else{
                         attributes.add(new Attribute(id, nextKeys + "_" + allattributes.get(nextKeys)));
                         //System.out.println(nextKeys + "_"  + allattributes.get(nextKeys));
                    }
                
                }catch (Exception e) {
                    e.printStackTrace();
                }
            
            
            }
        
        }
    
    }
    private void populate_maincatgory(/*String name,*/ Connection con) throws SQLException{
        PreparedStatement preparedstmt = con.prepareStatement("INSERT into MAIN_CATEGORY values(?,?)");
        for(int i = 0; i < mainCategories.size(); i++){
            MainCategory m = mainCategories.get(i);
            
            
            preparedstmt.setString(1, m.business_id);
            preparedstmt.setString(2, m.mainCategory);
            preparedstmt.executeUpdate();
            
        }
        preparedstmt.close(); 
    }
    
    private void populate_subcatgory( Connection con) throws SQLException{
        PreparedStatement preparedstmt = con.prepareStatement("INSERT into SubCategories values(?,?)");
        int count = 0;
        int b_size = 500;
        for(int i = 0; i < subCategories.size(); i++){
            SubCategory s = subCategories.get(i);
            
            preparedstmt.setString(1, s.business_id);
            preparedstmt.setString(2, s.subcategory);
            preparedstmt.addBatch();
            count++;
            if(count % b_size == 0){
                preparedstmt.executeBatch();
                System.out.println(count);
            }
             
        }
        if(count % b_size != 0)
            preparedstmt.executeBatch(); 

        preparedstmt.close(); 
    }    

    private void populate_attribute( Connection con) throws SQLException{
        PreparedStatement preparedstmt = con.prepareStatement("INSERT into Attributes values(?,?)");
        int count = 0;
        int b_size = 500;
        for(int i = 0; i < attributes.size(); i++){
            Attribute s = attributes.get(i);
            
            preparedstmt.setString(1, s.business_id);
            preparedstmt.setString(2, s.attribute);
            preparedstmt.addBatch();
            count++;
            if(count % b_size == 0){
                preparedstmt.executeBatch();
                System.out.println(count);
            }
             
        }
        if(count % b_size != 0)
            preparedstmt.executeBatch(); 

        preparedstmt.close(); 
    }    
private void populate_hour (Connection con) throws SQLException, FileNotFoundException, IOException {
        
        String file = "/Users/alexlaw/NetBeansProjects/Assignment3/yelp_business.json";
        BufferedReader br = new BufferedReader(new FileReader(file));
        int count = 0;
        int b_size = 500;
        String line = null;
        try{
            PreparedStatement stmt = con.prepareStatement("INSERT into BUSINESS_HOURS values(?,?,?,?)");
            while((line = br.readLine())!= null){
                for(int i = 0; i < hours.size(); i++){
                    Hour s = hours.get(i);
                    JSONObject jo = new JSONObject(line);
                    JSONObject allhours = jo.getJSONObject("hours");
                    Iterator<String> ikeys = allhours.keySet().iterator();
                    while(ikeys.hasNext()){
                        String nextKeys = ikeys.next();
                        JSONObject subhours = allhours.getJSONObject(nextKeys);
                        String open = (String) subhours.get("open");
                        String close = (String) subhours.get("close");
                        stmt.setString(1, s.business_id);
                        stmt.setString(2, nextKeys);
                        stmt.setString(3, open);
                        stmt.setString(4, close);
                        stmt.addBatch();
                        count++;
                        if(count % b_size == 0){
                            stmt.executeBatch();
                            System.out.println(count);
                        }
                    }
                 
                }
                if(count % b_size != 0)
                    stmt.executeBatch(); 

                    stmt.close();
            }
            
            

        } 
        catch (Exception e) {
                    e.printStackTrace();
        } 
    }
           

}//type:"review"

