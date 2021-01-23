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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.JXDatePicker;

import java.io.*;
import java.sql.*;
import java.lang.Long.*;
import org.json.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

public class hw3 extends javax.swing.JFrame {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private static HashSet<String> mainCategoriesSet = new HashSet();
    private static HashSet<String> subCategoriesSet = new HashSet();
    private static HashSet<String> attributesSet = new HashSet();
    private static StringBuilder mainCategoriesString = new StringBuilder();
    private static StringBuilder subCategoriesString = new StringBuilder();
    private static StringBuilder attributesString = new StringBuilder();
    private static Connection con;

    LinkedList<JCheckBox> All_main_category = new LinkedList<>();
    LinkedList<JCheckBox> All_sub_category = new LinkedList<>();
    ArrayList<String> main_categories_list = new ArrayList<>();
    TreeSet<String> sub_categories_list = new TreeSet();

    /**
     * Creates new form NewJFrame
     */
    public hw3() throws SQLException {
        initComponents();
        try {Class.forName("oracle.jdbc.driver.OracleDriver");
                con = DriverManager.getConnection(
                                    "jdbc:oracle:thin:@localhost:1521/XE", "system",
					"oracle");
        
        
        
        
 
        } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Exception: " + e.getMessage());
        }
        
        init();           
    }

    private void init() throws SQLException {

        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery("SELECT DISTINCT mainc FROM Main_Category ORDER BY mainc");

        while (res.next()) {
            JCheckBox maincate = new JCheckBox(res.getString("mainc"));

            maincate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JCheckBox clickedBox = (JCheckBox) e.getSource();
                    System.out.println(clickedBox.getText());

                    if (clickedBox.isSelected()) {
                        mainCategoriesSet.add(clickedBox.getText());
                    } else {
                        mainCategoriesSet.remove(clickedBox.getText());
                    }
                    try {

                        genSub(con);
                        genAttr(con);
                    } catch (SQLException ex) {
                        Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            });

            mainCategoryPanel.add(maincate);

        }

    }
    
    /*        File file = new File(BUSINESS_JSON_FILE_PATH);
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

    private void genSub(Connection con) throws SQLException {
        subCategoriesSet.clear();
        subCategoryPanel.removeAll();

        Iterator<String> it = mainCategoriesSet.iterator();

        Statement stmt = con.createStatement();
        System.out.println(mainCategoriesSet.size());
        if (it.hasNext()) {
            String sql = "SELECT DISTINCT sc.subc FROM SUBCATEGORIES sc, MAIN_CATEGORY mc WHERE sc.business_id = mc.business_id AND ( mc.mainc = ";

            while (it.hasNext()) {
                sql = sql + "'" + it.next() + "'";
                if (it.hasNext()) {
                    sql = sql + " OR mc.mainc = ";
                }
            }
            sql = sql + ") ORDER BY sc.subc";
            System.out.println(sql);

            ResultSet res = stmt.executeQuery(sql);

            while (res.next()) {
                JCheckBox subcate = new JCheckBox(res.getString("subc"));

                subcate.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JCheckBox clickedBox = (JCheckBox) e.getSource();
                        System.out.println(clickedBox.getText());

                        if (clickedBox.isSelected()) {
                            subCategoriesSet.add(clickedBox.getText());
                        } else {
                            subCategoriesSet.remove(clickedBox.getText());
                            attributesSet.clear();
                        }

                        try {
                            genAttr(con);
                        } catch (SQLException ex) {
                            Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                });

                subCategoryPanel.add(subcate);

            }
        }

        subCategoryPanel.revalidate();
        subCategoryPanel.repaint();

    }

    private void genAttr(Connection con) throws SQLException {
        attributesSet.clear();
        attributesPanel.removeAll();

        Iterator<String> it = subCategoriesSet.iterator();

        Statement stmt = con.createStatement();
        System.out.println(subCategoriesSet.size());
        if (it.hasNext()) {
            String sql = "SELECT DISTINCT at.attr FROM Attributes at, SUBCATEGORIES sc WHERE at.business_id = sc.business_id AND ( sc.subc = ";

            while (it.hasNext()) {
                sql = sql + "'" + it.next() + "'";
                if (it.hasNext()) {
                    sql = sql + " OR sc.subc = ";
                }
            }
            sql = sql + ") ORDER BY at.attr";
            System.out.println(sql);

            ResultSet res = stmt.executeQuery(sql);

            while (res.next()) {
                JCheckBox attribute = new JCheckBox(res.getString("attr"));

                attribute.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JCheckBox clickedBox = (JCheckBox) e.getSource();
                        System.out.println(clickedBox.getText());

                        if (clickedBox.isSelected()) {
                            attributesSet.add(clickedBox.getText());
                        } else {
                            attributesSet.remove(clickedBox.getText());
                            attributesSet.clear();
                        }

                    }

                });

                attributesPanel.add(attribute);

            }

        }
        attributesPanel.revalidate();
        attributesPanel.repaint();

    }
/*
    private void get_review_Bus(Object s) {
        DefaultTableModel model;
        model = new DefaultTableModel();
        poptable.setModel(model);
        model.addColumn("ReviewDate");
        model.addColumn("Stars");
        model.addColumn("Review Text");
        model.addColumn("UserID");
        model.addColumn("Useful Votes");
        String id = (String) s;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521/XE", "system",
                    "oracle");

            StringBuilder sb = new StringBuilder("SELECT CREATE_DATE, STARS, TEXT, USER_ID, VOTES FROM YELP_REVIEW  WHERE BUSINESS_ID = '" + id + "'");
            System.out.println(sb.toString());
            PreparedStatement sta = con.prepareStatement(sb.toString());
            java.sql.ResultSet rs = sta.executeQuery(sb.toString());
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
            }
            con.close();
            rs.close();
            sta.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Exception: " + e.getMessage());
        }
    }

    private int getColumnByName(JTable table, String name) {
        for (int i = 0; i < table.getColumnCount(); ++i) {
            if (table.getColumnName(i).equals(name)) {
                return i;
            }
        }
        return -1;
    }
/*
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        review_frame = new javax.swing.JFrame();
        jScrollPane1 = new javax.swing.JScrollPane();
        review_table = new javax.swing.JTable();
        mainPanel = new javax.swing.JPanel();
        businessPanel = new javax.swing.JPanel();
        categoriesPanel = new javax.swing.JPanel();
        mainCategoryScrollPane = new javax.swing.JScrollPane();
        mainCategoryPanel = new javax.swing.JPanel();
        subCategoryScrollPane = new javax.swing.JScrollPane();
        subCategoryPanel = new javax.swing.JPanel();
        resultsPane = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();
        attributesScrollPane = new javax.swing.JScrollPane();
        attributesPanel = new javax.swing.JPanel();
        dayOfTheWeekLabel = new javax.swing.JLabel();
        fromLabel = new javax.swing.JLabel();
        toLabel = new javax.swing.JLabel();
        searchForLabel = new javax.swing.JLabel();
        searchButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        dayOfTheWeekComboBox = new javax.swing.JComboBox<>();
        fromComboBox = new javax.swing.JComboBox<>();
        toComboBox = new javax.swing.JComboBox<>();
        searchForComboBox = new javax.swing.JComboBox<>();
        categoryLabel = new javax.swing.JLabel();
        subCategoryLabel = new javax.swing.JLabel();
        attributeLabel = new javax.swing.JLabel();
        locationLabel = new javax.swing.JLabel();
        locationComboBox = new javax.swing.JComboBox<>();
        businessLabel = new javax.swing.JLabel();

        review_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(review_table);

        javax.swing.GroupLayout review_frameLayout = new javax.swing.GroupLayout(review_frame.getContentPane());
        review_frame.getContentPane().setLayout(review_frameLayout);
        review_frameLayout.setHorizontalGroup(
            review_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(review_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(review_frameLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        review_frameLayout.setVerticalGroup(
            review_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(review_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(review_frameLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainCategoryPanel.setLayout(new java.awt.GridLayout(28, 1, 0, 10));
        mainCategoryScrollPane.setViewportView(mainCategoryPanel);

        subCategoryPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 10));
        subCategoryScrollPane.setViewportView(subCategoryPanel);

        resultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        resultsPane.setViewportView(resultsTable);

        attributesPanel.setLayout(new java.awt.GridLayout(50, 1, 0, 10));
        attributesScrollPane.setViewportView(attributesPanel);

        javax.swing.GroupLayout categoriesPanelLayout = new javax.swing.GroupLayout(categoriesPanel);
        categoriesPanel.setLayout(categoriesPanelLayout);
        categoriesPanelLayout.setHorizontalGroup(
            categoriesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(categoriesPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(mainCategoryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(subCategoryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attributesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultsPane, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
        );
        categoriesPanelLayout.setVerticalGroup(
            categoriesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(categoriesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(categoriesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(categoriesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(subCategoryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                        .addComponent(attributesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(resultsPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(mainCategoryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(859, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout businessPanelLayout = new javax.swing.GroupLayout(businessPanel);
        businessPanel.setLayout(businessPanelLayout);
        businessPanelLayout.setHorizontalGroup(
            businessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, businessPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(categoriesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        businessPanelLayout.setVerticalGroup(
            businessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, businessPanelLayout.createSequentialGroup()
                .addComponent(categoriesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        dayOfTheWeekLabel.setText("Day of the week");

        fromLabel.setText("From");

        toLabel.setText("To");

        searchForLabel.setText("Search for:");

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        dayOfTheWeekComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" }));
        dayOfTheWeekComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dayOfTheWeekComboBoxActionPerformed(evt);
            }
        });

        fromComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24" }));
        fromComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromComboBoxActionPerformed(evt);
            }
        });

        toComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24" }));
        toComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toComboBoxActionPerformed(evt);
            }
        });

        searchForComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));
        searchForComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchForComboBoxActionPerformed(evt);
            }
        });

        categoryLabel.setText("Main Category");

        subCategoryLabel.setText("Sub Category");

        attributeLabel.setText("Attribute");

        locationLabel.setText("Location");

        locationComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dayOfTheWeekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dayOfTheWeekLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fromComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fromLabel))
                        .addGap(18, 18, 18)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(toComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(toLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(searchForLabel)
                                .addGap(45, 45, 45)
                                .addComponent(locationLabel))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(searchForComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(locationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(searchButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(closeButton))))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(categoryLabel)
                        .addGap(67, 67, 67)
                        .addComponent(subCategoryLabel)
                        .addGap(79, 79, 79)
                        .addComponent(attributeLabel))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(businessPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(7, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(categoryLabel)
                    .addComponent(subCategoryLabel)
                    .addComponent(attributeLabel))
                .addGap(2, 2, 2)
                .addComponent(businessPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 286, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dayOfTheWeekLabel)
                    .addComponent(fromLabel)
                    .addComponent(toLabel)
                    .addComponent(searchForLabel)
                    .addComponent(locationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dayOfTheWeekComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fromComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchForComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeButton)
                    .addComponent(locationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addGap(16, 16, 16))
        );

        businessLabel.setText("Start");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(businessLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(businessLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dayOfTheWeekComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dayOfTheWeekComboBoxActionPerformed
        // TODO add your handling code here:
        dayOfTheWeekComboBox.insertItemAt("", 0);
    }//GEN-LAST:event_dayOfTheWeekComboBoxActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        //main_categories_list.removeAll(main_categories_list);
        //main_categories_list = buildCategoriesList();
Statement stmt;
        
        try {
            DefaultTableModel model = (DefaultTableModel) resultsTable.getModel();
            model.setRowCount(0);
            
            if (mainCategoriesSet.size() > 0) {
                Iterator<String> it = mainCategoriesSet.iterator();
            
                stmt = con.createStatement();
            
                String sql = "SELECT b.business_id AS business_id, b.name AS name, b.full_address AS full_address, b.city AS city, b.state AS state, b.review_count AS review_count, b.stars AS stars " + "From Yelp_Business b, Main_Category mc " +
                        "WHERE mc.business_id = b.business_id";
                sql = sql + " AND (mc.mainc= ";
                while (it.hasNext()) {
                    sql = sql + "'" + it.next()+ "'";
                    if (it.hasNext()) {
                        sql = sql + " OR mc.mainc = ";
                    }
                }
                sql = sql + ")";  
                if (subCategoriesSet.size()>0) {
                    it = subCategoriesSet.iterator();
                    
                    sql = sql + " AND b.business_id IN (SELECT b2.business_id " + 
                            "FROM yelp_business b2, subcategories sc " +
                            "WHERE b2.business_id = sc.business_id AND (sc.subc = ";
                    while (it.hasNext()){
                        sql = sql + "'" + it.next()+ "'";
                    if (it.hasNext()) {
                        sql = sql + " OR sc.subc = ";
                    }
                    
                }
                
                sql = sql +")"+")" +" ORDER BY b.name";
                
                
                ResultSet res;
                try {
                        res = stmt.executeQuery(sql);
                        ResultSetMetaData metaData = res.getMetaData();
                        int columnCount = metaData.getColumnCount();

        //Get all column names from meta data and add columns to table model
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
            model.addColumn(metaData.getColumnLabel(columnIndex));
        }

        //Create array of Objects with size of column count from meta data
        Object[] row = new Object[columnCount];

        //Scroll through result set
        while (res.next()){
            //Get object from column with specific index of result set to array of objects
            for (int i = 0; i < columnCount; i++){
                row[i] = res.getObject(i+1);
            }
            //Now add row to table model with that array of objects as an argument
            model.addRow(row);
        }

        //Now add that table model to your table and you are done :D
        resultsTable.setModel(model);} catch (SQLException ex) {
                        Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
                //review_table.getColumn("business_id").setMinWidth(0); // Must be set before maxWidth!!
                //review_table.getColumn("business_id").setMaxWidth(0);
                //review_table.getColumn("business_id").setWidth(0);
                review_table.addMouseListener( new MouseAdapter(){
                    @Override
                    public void mouseClicked(MouseEvent me) {
                        if (me.getClickCount() == 2 && !me.isConsumed()){
                            me.consume();
                            JTable target = (JTable)me.getSource();
                            int row = target.getSelectedRow();
                            String b_id = (String)resultsTable.getModel().getValueAt(row, 6);
                          
                            
                            review_frame.setSize(500, 600);
                            review_frame.setVisible(true);
                            
                            try {
                                String sql = "SELECT r.create_date, u.user_name, r.stars, r.text, r.votes"+
                                        " From yelp_User u RIGHT JOIN yelp_Review r"+
                                        " ON u.user_id = r.user_id WHERE r.business_id IN (SELECT b.business_id FROM yelp_Businesses b RIGHT JOIN yelp_Review r2"+
                                        " ON b.business_id = r2.business_id WHERE b.business_id = '"+b_id+"')";
                                System.out.println(sql);
                                ResultSet rev;
                                rev = stmt.executeQuery(sql);
                                
                                DefaultTableModel rmodel = (DefaultTableModel) review_table.getModel();
                                rmodel.setRowCount(0);
                                while (rev.next()){

                                    rmodel.addRow(new Object[]{rev.getString("create_date"), rev.getString("user_name"), rev.getDouble("stars"), rev.getString("text") ,rev.getInt("votes")});

                                }
                                review_table.revalidate();
                                review_table.repaint();
                            }  catch (SQLException ex) {
            Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);    
    }
                            
                        }
                        
                    }
                    
                } );
                resultsTable.revalidate();
                resultsTable.repaint();
                }
            } }catch (SQLException ex) {
            Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);    
        }
    }

            
        

                                                   
                                                

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        System.exit(0);        // TODO add your handling code here:
    }//GEN-LAST:event_closeButtonActionPerformed

    private void searchForComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchForComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchForComboBoxActionPerformed

    private void fromComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fromComboBoxActionPerformed

    private void toComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_toComboBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new hw3().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(hw3.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel attributeLabel;
    private javax.swing.JPanel attributesPanel;
    private javax.swing.JScrollPane attributesScrollPane;
    private javax.swing.JLabel businessLabel;
    private javax.swing.JPanel businessPanel;
    private javax.swing.JPanel categoriesPanel;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JButton closeButton;
    private javax.swing.JComboBox<String> dayOfTheWeekComboBox;
    private javax.swing.JLabel dayOfTheWeekLabel;
    private javax.swing.JComboBox<String> fromComboBox;
    private javax.swing.JLabel fromLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> locationComboBox;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JPanel mainCategoryPanel;
    private javax.swing.JScrollPane mainCategoryScrollPane;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JScrollPane resultsPane;
    private javax.swing.JTable resultsTable;
    private javax.swing.JFrame review_frame;
    private javax.swing.JTable review_table;
    private javax.swing.JButton searchButton;
    private javax.swing.JComboBox<String> searchForComboBox;
    private javax.swing.JLabel searchForLabel;
    private javax.swing.JLabel subCategoryLabel;
    private javax.swing.JPanel subCategoryPanel;
    private javax.swing.JScrollPane subCategoryScrollPane;
    private javax.swing.JComboBox<String> toComboBox;
    private javax.swing.JLabel toLabel;
    // End of variables declaration//GEN-END:variables
}
