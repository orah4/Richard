/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package healthydiet;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import java.util.Random;
import javax.swing.table.DefaultTableModel;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author ORAH RICHARD
 */
public class HealthyDietRecommender extends  javax.swing.JFrame {
     public static final String TAB = "\t";
    public static final String SEPARATOR = "-------------------------------------------------------";
 
Connection conn;
PreparedStatement pst;
ResultSet rs;   
 File  file ;
 String ImgPath = null;
    HealthyDiet D;
    
    Instances Data;
    int nAttributes;
    NaiveBayes Classifier = new NaiveBayes();
    String DataSet = new String();
    public static File DataFile;

    public File GetFileTrainData(String DataSet) {
        File FileTrainData = null;
        java.net.URL res = getClass().getResource(DataSet);
        if (res.toString().startsWith("jar:")) {
            try {
                InputStream input = getClass().getResourceAsStream(DataSet);
                FileTrainData = File.createTempFile("tempfile", ".tmp");
                java.io.OutputStream out = new java.io.FileOutputStream(FileTrainData);

                byte[] bytes = new byte['Ѐ'];
                int read;
                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                FileTrainData.deleteOnExit();
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }

        } else {
            FileTrainData = new File(DataSet);
        }

        return FileTrainData;
    }

    
     // Function To Resize The Image To Fit Into JLabel
    public ImageIcon ResizeImage(String imagePath, byte[] pic) {
        ImageIcon myImage = null;

        if (imagePath != null) {
            myImage = new ImageIcon(imagePath);
        } else {
            myImage = new ImageIcon(pic);
        }

        Image img = myImage.getImage();
        Image img2 = img.getScaledInstance(jLabel26.getWidth(), jLabel26.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(img2);
        
        
        return image;

    }
    
    
    
    
    
    
    
    
    
    /**
     * Creates new form HealthyDietRecommender
     */
    public HealthyDietRecommender() throws IOException, Exception {
        initComponents();
        cl();
        DataFile = GetFileTrainData("data_2.arff");
        this.Data = new Instances(new java.io.FileReader(DataFile));
        this.nAttributes = this.Data.numAttributes();
        this.Data.setClassIndex(this.nAttributes - 1);
        this.Classifier.buildClassifier(this.Data);
        System.out.println("Status: Classifier trained.");

        for (int j = 0; j < this.Data.attribute(0).numValues(); j++) {
            this.cbGender.addItem(this.Data.attribute(0).value(j).toString());
        }

        //Passing the variable
        for (int j = 0; j < this.Data.attribute(1).numValues(); j++) {
            this.cbDays.addItem(this.Data.attribute(1).value(j).toString());
        }
         //start
        //Passing the variable
        this.jComboBox3.addItem("1");
        this.jComboBox3.addItem("3");
        this.jComboBox3.addItem("2");
        this.jComboBox3.addItem("4");
        this.jComboBox3.addItem("13");
        this.jComboBox3.addItem("27");
        this.jComboBox3.addItem("5");
        this.jComboBox3.addItem("8");
        this.jComboBox3.addItem("9");
        this.jComboBox3.addItem("10");
        this.jComboBox3.addItem("16");
        this.jComboBox3.addItem("26");
        this.jComboBox3.addItem("31");
        this.jComboBox3.addItem("37");
        this.jComboBox3.addItem("20");
        this.jComboBox3.addItem("75");
        this.jComboBox3.addItem("70");
        this.jComboBox3.addItem("6");
        this.jComboBox3.addItem("11");
        this.jComboBox3.addItem("12");
        this.jComboBox3.addItem("18");
        this.jComboBox3.addItem("19");
        this.jComboBox3.addItem("45");
        this.jComboBox3.addItem("78");
        this.jComboBox3.addItem("80");
        this.jComboBox3.addItem("73");
        this.jComboBox3.addItem("56");
        this.jComboBox3.addItem("17");
        this.jComboBox3.addItem("36");
        this.jComboBox3.addItem("62");
        this.jComboBox3.addItem("69");
        this.jComboBox3.addItem("21");
        this.jComboBox3.addItem("23");
        this.jComboBox3.addItem("24");
        this.jComboBox3.addItem("15");
        this.jComboBox3.addItem("28");
        this.jComboBox3.addItem("64");
        this.jComboBox3.addItem("67");
        this.jComboBox3.addItem("74");
        this.jComboBox3.addItem("68");
        this.jComboBox3.addItem("32");
        this.jComboBox3.addItem("25");
        this.jComboBox3.addItem("30");
        this.jComboBox3.addItem("40");
        this.jComboBox3.addItem("50");
        this.jComboBox3.addItem("60");
        this.jComboBox3.addItem("7");
        this.jComboBox3.addItem("49");
        this.jComboBox3.addItem("48");
        this.jComboBox3.addItem("34");
        this.jComboBox3.addItem("46");
        this.jComboBox3.addItem("53");
        this.jComboBox3.addItem("58");
        this.jComboBox3.addItem("54");
        this.jComboBox3.addItem("29");
        this.jComboBox3.addItem("33");
        this.jComboBox3.addItem("47");
        this.jComboBox3.addItem("35");
        this.jComboBox3.addItem("43");
        this.jComboBox3.addItem("61");
        this.jComboBox3.addItem("71");
        this.jComboBox3.addItem("44");
        this.jComboBox3.addItem("84");
        this.jComboBox3.addItem("55");
        this.jComboBox3.addItem("59");
        this.jComboBox3.addItem("66");
        this.jComboBox3.addItem("52");
        this.jComboBox3.addItem("38");
        this.jComboBox3.addItem("39");
        this.jComboBox3.addItem("22");
        this.jComboBox3.addItem("88");
        this.jComboBox3.addItem("86");

        //Passing the variable
        //
        this.jComboBox4.addItem("0.67");
        this.jComboBox4.addItem("0.77");
        this.jComboBox4.addItem("0.56");
        this.jComboBox4.addItem("0.66");
        this.jComboBox4.addItem("1");
        this.jComboBox4.addItem("0.45");
        this.jComboBox4.addItem("1.98");
        this.jComboBox4.addItem("0.88");
        this.jComboBox4.addItem("0.9");
        this.jComboBox4.addItem("0.89");
        this.jComboBox4.addItem("1.1");
        this.jComboBox4.addItem("1.95");
        this.jComboBox4.addItem("2.56");
        this.jComboBox4.addItem("2.54");
        this.jComboBox4.addItem("1.78");
        this.jComboBox4.addItem("1.87");
        this.jComboBox4.addItem("2.57");
        this.jComboBox4.addItem("1.67");
        this.jComboBox4.addItem("0.65");
        this.jComboBox4.addItem("1.12");
        this.jComboBox4.addItem("1.23");
        this.jComboBox4.addItem("1.45");
        this.jComboBox4.addItem("1.5");
        this.jComboBox4.addItem("3");
        this.jComboBox4.addItem("2.34");
        this.jComboBox4.addItem("3.21");
        this.jComboBox4.addItem("0.98");
        this.jComboBox4.addItem("2.98");
        this.jComboBox4.addItem("1.2");
        this.jComboBox4.addItem("2.75");
        this.jComboBox4.addItem("2.09");
        this.jComboBox4.addItem("4.01");
        this.jComboBox4.addItem("2.45");
        this.jComboBox4.addItem("1.99");
        this.jComboBox4.addItem("2.14");
        this.jComboBox4.addItem("2.12");
        this.jComboBox4.addItem("1.56");
        this.jComboBox4.addItem("0.78");
        this.jComboBox4.addItem("0.99");
        this.jComboBox4.addItem("1.01");
        this.jComboBox4.addItem("0.54");
        this.jComboBox4.addItem("0.95");
        this.jComboBox4.addItem("0.75");
        this.jComboBox4.addItem("0.16");
        this.jComboBox4.addItem("3.76");
        this.jComboBox4.addItem("3.45");
        this.jComboBox4.addItem("2.47");
        this.jComboBox4.addItem("1.66");
        this.jComboBox4.addItem("0.2");
        this.jComboBox4.addItem("0.63");
        this.jComboBox4.addItem("0.93");
        this.jComboBox4.addItem("1.2");
        this.jComboBox4.addItem("3");
        this.jComboBox4.addItem("1.02");
        //
        this.jComboBox5.addItem("6");
        this.jComboBox5.addItem("13");
        this.jComboBox5.addItem("7");
        this.jComboBox5.addItem("9");
        this.jComboBox5.addItem("20");
        this.jComboBox5.addItem("30");
        this.jComboBox5.addItem("18");
        this.jComboBox5.addItem("22");
        this.jComboBox5.addItem("24");
        this.jComboBox5.addItem("23");
        this.jComboBox5.addItem("70");
        this.jComboBox5.addItem("65");
        this.jComboBox5.addItem("78");
        this.jComboBox5.addItem("56");
        this.jComboBox5.addItem("45");
        this.jComboBox5.addItem("17");
        this.jComboBox5.addItem("21");
        this.jComboBox5.addItem("29");
        this.jComboBox5.addItem("34");
        this.jComboBox5.addItem("31");
        this.jComboBox5.addItem("39");
        this.jComboBox5.addItem("15");
        this.jComboBox5.addItem("79");
        this.jComboBox5.addItem("89");
        this.jComboBox5.addItem("67");
        this.jComboBox5.addItem("69");
        this.jComboBox5.addItem("27");
        this.jComboBox5.addItem("16");
        this.jComboBox5.addItem("22");
        this.jComboBox5.addItem("54");
        this.jComboBox5.addItem("75");
        this.jComboBox5.addItem("66");
        this.jComboBox5.addItem("86");
        this.jComboBox5.addItem("87");
        this.jComboBox5.addItem("60");
        this.jComboBox5.addItem("98");
        this.jComboBox5.addItem("32");
        this.jComboBox5.addItem("47");
        //this.jComboBox5.addItem("33");
        this.jComboBox5.addItem("10");
        this.jComboBox5.addItem("14");
        this.jComboBox5.addItem("25");
        this.jComboBox5.addItem("40");
        this.jComboBox5.addItem("28");
        this.jComboBox5.addItem("26");
        this.jComboBox5.addItem("59");
        this.jComboBox5.addItem("64");
        this.jComboBox5.addItem("35");
        this.jComboBox5.addItem("58");
        this.jComboBox5.addItem("37");
        this.jComboBox5.addItem("46");
        this.jComboBox5.addItem("101");
        this.jComboBox5.addItem("55");
        this.jComboBox5.addItem("99");
        this.jComboBox5.addItem("33");
        this.jComboBox5.addItem("19");

        // 
        this.jComboBox6.addItem("145.85");
        this.jComboBox6.addItem("229.35");
        this.jComboBox6.addItem("159.1");
        this.jComboBox6.addItem("180.4");
        this.jComboBox6.addItem("259.1");
        this.jComboBox6.addItem("227.75");
        this.jComboBox6.addItem("822.362");
        this.jComboBox6.addItem("807.512");
        this.jComboBox6.addItem("832.17");
        this.jComboBox6.addItem("827.451");
        this.jComboBox6.addItem("841.693");
        this.jComboBox6.addItem("804.63");
        this.jComboBox6.addItem("1214.205");
        this.jComboBox6.addItem("734.093");
        this.jComboBox6.addItem("765.5");
        this.jComboBox6.addItem("712.2");
        this.jComboBox6.addItem("520.05");
        this.jComboBox6.addItem("342.15");
        this.jComboBox6.addItem("675.25");
        this.jComboBox6.addItem("271.5");
        this.jComboBox6.addItem("304.95");
        this.jComboBox6.addItem("327.7");
        this.jComboBox6.addItem("287.1");
        this.jComboBox6.addItem("391.25");
        this.jComboBox6.addItem("434.15");
        this.jComboBox6.addItem("380.25");
        this.jComboBox6.addItem("943.05");
        this.jComboBox6.addItem("767.054");
        this.jComboBox6.addItem("726.028");
        this.jComboBox6.addItem("1244.464");
        this.jComboBox6.addItem("1303.7");
        this.jComboBox6.addItem("936.146");
        this.jComboBox6.addItem("947.599");
        this.jComboBox6.addItem("878.562");
        this.jComboBox6.addItem("786.055");
        this.jComboBox6.addItem("839.164");
        this.jComboBox6.addItem("940.826");
        this.jComboBox6.addItem("915.962");
        this.jComboBox6.addItem("903.88");
        this.jComboBox6.addItem("1134.325");
        this.jComboBox6.addItem("1167.571");
        this.jComboBox6.addItem("1004.919");
        this.jComboBox6.addItem("1275.299");
        this.jComboBox6.addItem("1203.555");
        this.jComboBox6.addItem("1140.862");
        this.jComboBox6.addItem("638.05");
        this.jComboBox6.addItem("532.6");
        this.jComboBox6.addItem("829.6");
        this.jComboBox6.addItem("997.1");
        this.jComboBox6.addItem("688.8");
        this.jComboBox6.addItem("512");
        this.jComboBox6.addItem("668.5");
        this.jComboBox6.addItem("993.45");
        this.jComboBox6.addItem("430.55");
        this.jComboBox6.addItem("412.35");
        this.jComboBox6.addItem("311.35");
        this.jComboBox6.addItem("321.15");
        this.jComboBox6.addItem("194.2");
        this.jComboBox6.addItem("352.85");
        this.jComboBox6.addItem("718.4");
        this.jComboBox6.addItem("92.9");
        this.jComboBox6.addItem("41.65");
        this.jComboBox6.addItem("1211.2");
        this.jComboBox6.addItem("1166.464");
        this.jComboBox6.addItem("1286.526");
        this.jComboBox6.addItem("984.582");
        this.jComboBox6.addItem("1034.253");
        this.jComboBox6.addItem("918.264");
        this.jComboBox6.addItem("1082.673");
        this.jComboBox6.addItem("969.005");
        this.jComboBox6.addItem("1411.662 ");
        this.jComboBox6.addItem("1197.226");
        this.jComboBox6.addItem("914.082");
        this.jComboBox6.addItem("761.653");
        this.jComboBox6.addItem("636.264");
        this.jComboBox6.addItem("441.85");
        this.jComboBox6.addItem("194.8");
        this.jComboBox6.addItem("223.7");
        this.jComboBox6.addItem("320.45");
        this.jComboBox6.addItem("293.95");
        this.jComboBox6.addItem("248.8");
        this.jComboBox6.addItem("396.45");
        this.jComboBox6.addItem("361.85");
        this.jComboBox6.addItem("423.05");
        this.jComboBox6.addItem("262.35");
        this.jComboBox6.addItem("302.5");
        this.jComboBox6.addItem("443.8");
        this.jComboBox6.addItem("437.75");
        this.jComboBox6.addItem("196.3");
        this.jComboBox6.addItem("543.75");
        this.jComboBox6.addItem("924.05");
        this.jComboBox6.addItem("232.8");
        this.jComboBox6.addItem("180.7");
        this.jComboBox6.addItem("1093.5");
        this.jComboBox6.addItem("611.45");
        this.jComboBox6.addItem("771.85");
        this.jComboBox6.addItem("482");
        this.jComboBox6.addItem("237.9");
        this.jComboBox6.addItem("1030.526");
        this.jComboBox6.addItem("1037.726");
        this.jComboBox6.addItem("967.282");
        this.jComboBox6.addItem("951.453");
        this.jComboBox6.addItem("913.964");
        this.jComboBox6.addItem("970.273");
        this.jComboBox6.addItem("1006.205");
        this.jComboBox6.addItem("880.662");
        this.jComboBox6.addItem("857.864");
        this.jComboBox6.addItem("832.426");
        this.jComboBox6.addItem("904.282");
        this.jComboBox6.addItem("1160.453");
        this.jComboBox6.addItem("1246.464");
        this.jComboBox6.addItem("1088.373");
        this.jComboBox6.addItem("394.65");
        this.jComboBox6.addItem("429.4");
        this.jComboBox6.addItem("525.85");
        this.jComboBox6.addItem("697.6");
        this.jComboBox6.addItem("776.2");
        this.jComboBox6.addItem("775.6");
        this.jComboBox6.addItem("791.25");
        this.jComboBox6.addItem("528.1");
        this.jComboBox6.addItem("270.85");
        this.jComboBox6.addItem("374.85");
        this.jComboBox6.addItem("492.4");
        this.jComboBox6.addItem("443.7");
        this.jComboBox6.addItem("421");
        this.jComboBox6.addItem("652.6");
        this.jComboBox6.addItem("666.2");
        this.jComboBox6.addItem("178.1");
        this.jComboBox6.addItem("173.1");
        this.jComboBox6.addItem("180.45");
        this.jComboBox6.addItem("436.45");
        this.jComboBox6.addItem("899.664");
        this.jComboBox6.addItem("914.373");
        this.jComboBox6.addItem("1126.305");
        this.jComboBox6.addItem("895.562");
        this.jComboBox6.addItem("954.664");
        this.jComboBox6.addItem("942.954");
        this.jComboBox6.addItem("1034.771");
        this.jComboBox6.addItem("1010.364");
        this.jComboBox6.addItem("1226.673");
        this.jComboBox6.addItem("1142.005");
        this.jComboBox6.addItem("843.562");
        this.jComboBox6.addItem("1422.764");
        this.jComboBox6.addItem("829.854");
        this.jComboBox6.addItem("863.271");
        this.jComboBox6.addItem("816.564");
        this.jComboBox6.addItem("785.426");
        this.jComboBox6.addItem("913.682");
        this.jComboBox6.addItem("1006.153");
        this.jComboBox6.addItem("1019.064");
        this.jComboBox6.addItem("899.373");
        this.jComboBox6.addItem("1000.805");
        this.jComboBox6.addItem("915.062");
        this.jComboBox6.addItem("735.953");
        this.jComboBox6.addItem("87.8");
        this.jComboBox6.addItem("166");
        this.jComboBox6.addItem("351.4");
        this.jComboBox6.addItem("730.05");
        this.jComboBox6.addItem("596.1");
        this.jComboBox6.addItem("162.05");
        this.jComboBox6.addItem("205.55");
        this.jComboBox6.addItem("311.25");
        this.jComboBox6.addItem("495.2");
        this.jComboBox6.addItem("636.3");
        this.jComboBox6.addItem("645.3");
        this.jComboBox6.addItem("885.45");
        this.jComboBox6.addItem("659.25");
        this.jComboBox6.addItem("211.2");
        this.jComboBox6.addItem("248.7");
        this.jComboBox6.addItem("366");
        this.jComboBox6.addItem("282.6");
        this.jComboBox6.addItem("416.6");
        this.jComboBox6.addItem("685.05");
        this.jComboBox6.addItem("767.3");
        this.jComboBox6.addItem("1174.573");
        this.jComboBox6.addItem("1247.205");
        this.jComboBox6.addItem("1090.026");
        this.jComboBox6.addItem("800.382");
        this.jComboBox6.addItem("1179.653");
        this.jComboBox6.addItem("1260.564");
        this.jComboBox6.addItem("1111.873");
        this.jComboBox6.addItem("819.505");
        this.jComboBox6.addItem("939.462");
        this.jComboBox6.addItem("1078.353");
        this.jComboBox6.addItem("925.973");
        this.jComboBox6.addItem("888.462");
        this.jComboBox6.addItem("782.282");
        this.jComboBox6.addItem("938.253");
        this.jComboBox6.addItem("1398.864");
        this.jComboBox6.addItem("790.353");
        this.jComboBox6.addItem("831.864");
        this.jComboBox6.addItem("873.919");
        this.jComboBox6.addItem("815.273");
        this.jComboBox6.addItem("249");
        this.jComboBox6.addItem("346.2");
        this.jComboBox6.addItem("299.75");
        this.jComboBox6.addItem("383.6");

        //end
        for (int j = 0; j < this.Data.attribute(2).numValues(); j++) {
            this.cbFat.addItem(this.Data.attribute(2).value(j).toString());
        }
        for (int j = 0; j < this.Data.attribute(3).numValues(); j++) {
            this.jComboBox8.addItem(this.Data.attribute(3).value(j).toString());
        }

        for (int j = 0; j < this.Data.attribute(4).numValues(); j++) {
            this.jComboBox9.addItem(this.Data.attribute(4).value(j).toString());
        }

        for (int j = 0; j < this.Data.attribute(5).numValues(); j++) {
            this.jComboBox10.addItem(this.Data.attribute(5).value(j).toString());
        }

        for (int j = 0; j < this.Data.attribute(6).numValues(); j++) {
            this.jComboBox11.addItem(this.Data.attribute(6).value(j).toString());
        }

        for (int j = 0; j < this.Data.attribute(7).numValues(); j++) {
            this.jComboBox12.addItem(this.Data.attribute(7).value(j).toString());
        }

        for (int j = 0; j < this.Data.attribute(8).numValues(); j++) {
            this.jComboBox14.addItem(this.Data.attribute(8).value(j).toString());
        }
        
        
        
        
        
        
      //  
       
   
   
     

   
        
   
   



        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

    }
 DefaultTableModel dtm;
 void cl() {
        dtm = (DefaultTableModel) jTable1.getModel();
        dtm.setRowCount(0);
        String sql = "select * from  datatbl";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/data", "root", "");
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                dtm.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3),rs.getString(4), rs.getString(5), rs.getString(6),rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10)});
            }
        } catch (Exception e) {
            System.out.println("" + e);
        }
    }
    //
   


    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        cbGender = new javax.swing.JComboBox<>();
        cbDays = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox<>();
        jComboBox6 = new javax.swing.JComboBox<>();
        cbFat = new javax.swing.JComboBox<>();
        jComboBox8 = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jComboBox9 = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jComboBox10 = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jComboBox11 = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jComboBox12 = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jComboBox14 = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        cbModel = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jTextField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jLabel26 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 0), 3, true));

        jTabbedPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 102, 0), 2, true));
        jTabbedPane1.setFont(new java.awt.Font("Verdana", 1, 16)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/healthydiet/images/logo_img1.jpg"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 1270, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("HOME PAGE", jPanel2);

        jPanel3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N

        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));

        cbGender.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N

        cbDays.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N

        jComboBox3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N

        jComboBox4.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel2.setText("Gender ");
        jLabel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jLabel3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel3.setText("Days ");
        jLabel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jLabel4.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel4.setText("AGE ");
        jLabel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jLabel5.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel5.setText("Height ");
        jLabel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jLabel6.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel6.setText("Weight ");
        jLabel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jLabel7.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel7.setText("Calories ");
        jLabel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jLabel8.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel8.setText("Fat ");
        jLabel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jLabel9.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel9.setText("Protein ");
        jLabel9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jComboBox5.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N

        jComboBox6.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Calorie" }));

        cbFat.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N

        jComboBox8.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "?" }));

        jLabel10.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel10.setText("Cabohydrate ");
        jLabel10.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jComboBox9.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "?" }));

        jLabel11.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel11.setText("Vegitable ");
        jLabel11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jComboBox10.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "?" }));

        jLabel12.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel12.setText("Vitamin ");
        jLabel12.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jComboBox11.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "?" }));

        jLabel13.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel13.setText("Water ");
        jLabel13.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jComboBox12.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "?" }));

        jLabel15.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel15.setText("Life_Style_Activity ");
        jLabel15.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jComboBox14.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jComboBox14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox14ActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Action", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 1, 24), new java.awt.Color(255, 0, 0))); // NOI18N

        jLabel14.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel14.setText("Select Model ");
        jLabel14.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        cbModel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        cbModel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "Naive Bayes", "J48" }));
        cbModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbModelActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton1.setText("Predict Diet");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jButton1))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbModel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbDays, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbGender, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox4, 0, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cbFat, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.Alignment.LEADING, 0, 152, Short.MAX_VALUE)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox8, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jComboBox11, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox10, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox9, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbGender, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(cbDays, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBox3))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jComboBox4, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
                                        .addGap(1, 1, 1))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cbFat, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));

        jTextField.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jTextField.setForeground(new java.awt.Color(0, 204, 0));
        jTextField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 0), 2, true));

        jLabel16.setFont(new java.awt.Font("Verdana", 1, 20)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 255));
        jLabel16.setText("Displayed the Recommender Diet:");

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Display the Sellected Diet", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 1, 24), new java.awt.Color(0, 153, 0))); // NOI18N

        jLabel18.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        jLabel18.setText("jLabel17");
        jLabel18.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 0), 3, true));
        jLabel18.setEnabled(false);

        jLabel19.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        jLabel19.setText("jLabel17");
        jLabel19.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 0), 3, true));
        jLabel19.setEnabled(false);

        jLabel20.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 51, 51));
        jLabel20.setText("        jLabel");
        jLabel20.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 0), 3, true));

        jLabel21.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        jLabel21.setText("jLabel17");
        jLabel21.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 0), 3, true));
        jLabel21.setEnabled(false);

        jLabel22.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        jLabel22.setText("jLabel17");
        jLabel22.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 0), 3, true));
        jLabel22.setEnabled(false);

        jLabel23.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        jLabel23.setText("jLabel17");
        jLabel23.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 0), 3, true));
        jLabel23.setEnabled(false);

        jLabel24.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        jLabel24.setText("jLabel17");
        jLabel24.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 0), 3, true));
        jLabel24.setEnabled(false);

        jLabel25.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        jLabel25.setText("jLabel17");
        jLabel25.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 0), 3, true));
        jLabel25.setEnabled(false);

        jLabel17.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        jLabel17.setText("jLabel17");
        jLabel17.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 0), 3, true));
        jLabel17.setEnabled(false);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19))
                .addGap(29, 29, 29)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jLabel26.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 0, 0), 3, true));

        jDesktopPane1.setLayer(jLabel26, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jTextField, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jDesktopPane1, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(jLabel16))
                .addGap(24, 24, 24)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDesktopPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("DIET RECOMMENDER SYSTEM", jPanel3);

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton2.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jButton2.setText("Refresh");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextArea3.setBackground(new java.awt.Color(0, 0, 0));
        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jTextArea3.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea3.setRows(5);
        jScrollPane3.setViewportView(jTextArea3);

        jLabel29.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 3, true));

        jLabel30.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 3, true));

        jLabel31.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 3, true));

        jLabel32.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 3, true));

        jLabel33.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 3, true));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 740, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3)
                            .addComponent(jScrollPane1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addGap(38, 38, 38))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 22, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab("ACTUAL CLASS AND PREDICTED CLASS", jPanel8);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jTable1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));
        jTable1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Gender", "Days", "Fat", "Protein", "Cabohydrate", "Vegitable", "Vitamin", "Water", "Life_Style_Activity", "class"
            }
        ));
        jScrollPane4.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 786, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                    .addComponent(jScrollPane4))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("EVALUATION RESULTS", jPanel9);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 27)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 255));
        jLabel1.setText("      HEALTHY DIET RECOMMENDER USING DATA MINING TECHNIQUES");
        jLabel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 153, 0), 2, true));

        jMenuBar1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox14ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
      int modelo =0;
       
       D = new HealthyDiet();

        try {
           //String algorithm = (String) (cbModel.getSelectedItem());
            
           
            D.generar(cbModel.getSelectedIndex());
            String r = D.evaluar(cbGender.getItemAt(cbGender.getSelectedIndex()),
                    cbDays.getItemAt(cbDays.getSelectedIndex()),
                  //  jComboBox3.getItemAt(jComboBox3.getSelectedIndex()),
                    //jComboBox4.getItemAt(jComboBox4.getSelectedIndex()),
                    //jComboBox5.getItemAt(jComboBox5.getSelectedIndex()),
                    //jComboBox6.getItemAt(jComboBox6.getSelectedIndex()),
                    cbFat.getItemAt(cbFat.getSelectedIndex()),
                    jComboBox8.getItemAt(jComboBox8.getSelectedIndex()),
                    jComboBox9.getItemAt(jComboBox9.getSelectedIndex()),
                    jComboBox10.getItemAt(jComboBox10.getSelectedIndex()),
                    jComboBox11.getItemAt(jComboBox11.getSelectedIndex()),
                    jComboBox12.getItemAt(jComboBox12.getSelectedIndex()),
                    jComboBox14.getItemAt(jComboBox14.getSelectedIndex()),
                    cbModel.getSelectedIndex());

            if (r.equals("Breakfast")) {
                 
              //ImgPath = path;
                 //jLabel26 =  new JLabel(new ImageIcon(getClass().getResource("/image/1.png")));
                //jLabel26.setIcon(new javax.swing.ImageIcon("image/".toUpperCase()+"1.png"));
          
                 //jLabel26.setPreferredSize(new Dimension(5, 5));
                ImageIcon image = new ImageIcon(new ImageIcon("images/".toUpperCase()+"1.jpg").getImage().getScaledInstance(330, 230, Image.SCALE_SMOOTH));
                jLabel26.setIcon(image);
                // jLabel26.setHorizontalAlignment(SwingConstants.CENTER);
                 //.getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT));
                 
                 //Display respecfull diet for breakfast
                 
                ImageIcon image1 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"4.jpg").getImage().getScaledInstance(230, 130, Image.SCALE_SMOOTH));
                jLabel29.setIcon(image1);
                
                ImageIcon image2 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"5.jpg").getImage().getScaledInstance(230, 130, Image.SCALE_SMOOTH));
                jLabel30.setIcon(image2);
   
                ImageIcon image3 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"6.jpg").getImage().getScaledInstance(230, 130, Image.SCALE_SMOOTH));
                jLabel31.setIcon(image3);
                
                ImageIcon image4 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"7.jpg").getImage().getScaledInstance(330, 230, Image.SCALE_SMOOTH));
                jLabel32.setIcon(image4);
                 ImageIcon image5 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"8.jpg").getImage().getScaledInstance(330, 230, Image.SCALE_SMOOTH));
                jLabel33.setIcon(image5);
                 
                jTextField.setText("The Diet Recommended is:  " +"Breakfast");       
                
            } else if (r.equals("Dinner")) { 
                     ImageIcon image = new ImageIcon(new ImageIcon("images/".toUpperCase()+"2.jpg").getImage().getScaledInstance(330, 230, Image.SCALE_SMOOTH));
                jLabel26.setIcon(image);
               
                //For Dinner
                 ImageIcon image1 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"9.jpg").getImage().getScaledInstance(230, 130, Image.SCALE_SMOOTH));
                jLabel29.setIcon(image1);
                
                ImageIcon image2 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"10.jpg").getImage().getScaledInstance(230, 130, Image.SCALE_SMOOTH));
                jLabel30.setIcon(image2);
   
                ImageIcon image3 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"11.jpg").getImage().getScaledInstance(230, 130, Image.SCALE_SMOOTH));
                jLabel31.setIcon(image3);
                
                ImageIcon image4 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"12.jpg").getImage().getScaledInstance(330, 230, Image.SCALE_SMOOTH));
                jLabel32.setIcon(image4);
                 ImageIcon image5 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"13.jpg").getImage().getScaledInstance(330, 230, Image.SCALE_SMOOTH));
                jLabel33.setIcon(image5); 
                jTextField.setText("The Diet Recommended is:  " +"Dinner");
               
               

            } else if (r.equals("Lunch")) {
                
                     ImageIcon image = new ImageIcon(new ImageIcon("images/".toUpperCase()+"3.jpg").getImage().getScaledInstance(330, 230, Image.SCALE_SMOOTH));
                jLabel26.setIcon(image);
                
                //For Dinner
                 ImageIcon image1 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"14.jpg").getImage().getScaledInstance(230, 130, Image.SCALE_SMOOTH));
                jLabel29.setIcon(image1);
                
                ImageIcon image2 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"15.jpg").getImage().getScaledInstance(230, 130, Image.SCALE_SMOOTH));
                jLabel30.setIcon(image2);
   
                ImageIcon image3 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"16.jpg").getImage().getScaledInstance(230, 130, Image.SCALE_SMOOTH));
                jLabel31.setIcon(image3);
                
                ImageIcon image4 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"17.jpg").getImage().getScaledInstance(330, 230, Image.SCALE_SMOOTH));
                jLabel32.setIcon(image4);
                 ImageIcon image5 = new ImageIcon(new ImageIcon("images/".toUpperCase()+"18.jpg").getImage().getScaledInstance(330, 230, Image.SCALE_SMOOTH));
                jLabel33.setIcon(image5); 
                jTextField.setText("The Diet Recommended is:  " +"Launch");

            } 

            
            //rest.append(D.TAB + "Actual Class" + D.TAB + "NB Predicted");
            
           //rest.append(D.TAB + "\n\nActual Class" + HealthyDiet.TAB + "NB Predicted\n\n");
       
            
           // cbGender
            //int i;
            jLabel17.setText(cbGender.getSelectedItem().toString());
            jLabel20.setText(cbDays.getSelectedItem().toString());
            jLabel23.setText(cbFat.getSelectedItem().toString());
            jLabel18.setText(jComboBox8.getSelectedItem().toString());
            jLabel21.setText(jComboBox9.getSelectedItem().toString());
            jLabel24.setText(jComboBox10.getSelectedItem().toString());           
            jLabel19.setText(jComboBox11.getSelectedItem().toString());
            jLabel22.setText(jComboBox12.getSelectedItem().toString());
            jLabel25.setText(jComboBox14.getSelectedItem().toString()); 

    
       try{       
                
        double percent = 80;
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("data_2.arff");
        Instances instances = source.getDataSet();
        instances.randomize(new java.util.Random(0));

        // 80% Train Set
        int trainSize = (int) Math.round(instances.numInstances() * percent / 100);
        // 20% : Test set
        int testSize = instances.numInstances() - trainSize;
        Instances train = new Instances(instances, 0, trainSize);
        Instances test = new Instances(instances, trainSize, testSize);

         jTextArea1.append(HealthyDiet.TAB + "\nTotal Sample instancias: " + instances.numInstances());
         jTextArea1.append(HealthyDiet.TAB + "\nTrain Set: " + trainSize);
         jTextArea1.append(HealthyDiet.TAB + "\nTest Set: " + testSize);    
                
        
        //
        //
        train.setClassIndex(train.numAttributes() - 1);
        //Obtiene el numero de clases 
        int numClasses = train.numClasses();
        // training dataset
        for (int i = 0; i < numClasses; i++) {
            //Get class string value using the class index
            String classValue = train.classAttribute().value(i);
            jTextArea1.append(HealthyDiet.TAB + "\n Value using clase index " + i + "  " + classValue);
        }        
                
                
       //Crear modelo the  clasification  80 % 
        NaiveBayes nb = new NaiveBayes();
        J48 jc = new J48();
        
        nb.buildClassifier(train);
        jc.buildClassifier(train);//train BD
        //System.out.println(HealthyDiet.TAB + "Testing process...");
        System.out.println(HealthyDiet.SEPARATOR);         
        test.setClassIndex(test.numAttributes() - 1);
        //System.out.println(HealthyDiet.TAB + "Actual Class" + HealthyDiet.TAB + "NB Predicted");
       jTextArea1.append(HealthyDiet.TAB + "\n===============================\n");
       jTextArea1.append(HealthyDiet.TAB + "Actual Class" + HealthyDiet.TAB + " Predicted Class" +"\n                       \n");
        
       // System.out.println(HealthyDiet.TAB + "===============================");       
        int correctSamples = 0;
          for (int i = 0; i < test.numInstances(); i++) {
            //Retorna el valor de la clase
            double actualClass = test.instance(i).classValue();
            //Obtiene el nombre de la clase según el valor de actualClass
            String actual = test.classAttribute().value((int) actualClass);
            //obtener la instancia de la lista de prueba
            Instance newInst = test.instance(i);
            //Predecir el valor de la clase
            double predNB = 0;
            //int modelo;      
            String algorithm = (String) (cbModel.getSelectedItem());
            String[] algorithms = {algorithm};
            for (int w = 0; w < algorithms.length; w++) {
                if (algorithms[w].equals("Naive Bayes")) {
                     predNB = nb.classifyInstance(newInst);
                }
                 
             if (algorithms[w].equals("J48")) {
                    predNB = jc.classifyInstance(newInst);

                }
                
             
            String predString = test.classAttribute().value((int) predNB);    
            jTextArea1.append(HealthyDiet.TAB + actual + HealthyDiet.TAB + predString+"\n                       \n");
            if ((int) actualClass == (int) predNB) {
                correctSamples++;
            }
        }
          }
          
          
          double accuracy = correctSamples / (testSize * 1.0);
          if (modelo == 0) {
            //System.out.println("Accuracy: " + accuracy * 100 + " %  Application model Naive Bayes");
             
            jTextArea1.append("\nModel Accuracy: " + String.format("%.2f%%", accuracy * 100));
             //jTextArea1.append("\n==========\n");
        }         //jTextArea3.append("\n accuracy : " + String.format("%.2f%%", accuracy));
        
 
        else {
            //System.out.println("Accuracy: " + accuracy * 100 + " %  Application model J48");
            
            jTextArea1.append("Accuracy: " + accuracy * 100 + " %  Application model J48"+"\n                       \n");
        }

    
           //Evaluation
                Evaluation eval = new Evaluation(test);

                eval.crossValidateModel(nb, test, 10, new Random(1));
                jTextArea2.append("\n  \n");
                jTextArea2.append(eval.toSummaryString("\nModel Information\n=================\n", true));
                jTextArea2.append(eval.toClassDetailsString("\n\n   \n\n"));
                jTextArea2.append(eval.toMatrixString("\n\n   \n\n"));
     
                for (int i = 0; i < test.numInstances(); i++) {
                    double actualClass = test.instance(i).classValue();
                    //String actual = testDataset.classAttribute().value((int) actualClass);
                    Instance newInst = test.instance(i);
                    double predNB = nb.classifyInstance(newInst);
                    //String predString = testDataset.classAttribute().value((int) predNB);

                   // System.out.println(test.instance(i) + " =====> " + testDataset.classAttribute().value((int) predNB));

                    
                    jTextArea3.append(test.instance(i) + " =====> " + test.classAttribute().value((int) predNB)+"\n                       \n");
                    
                    
                    
                }
          
          
        
          // Coding  
        
        
        
        
    
        
        
            }catch(Exception e){
                Logger.getLogger(HealthyDietRecommender.class.getName()).log(Level.SEVERE, null, e);
                
                
            }
     
        } catch (Exception ex) {
            Logger.getLogger(HealthyDietRecommender.class.getName()).log(Level.SEVERE, null, ex);
        } 


      
      
      
      
      
      
      
      
      
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void cbModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbModelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbModelActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        
        jTextArea1.setText("");
        jTextArea3.setText("");
        jTextArea2.setText("");
    }//GEN-LAST:event_jButton2ActionPerformed

    public String evaluar(
            String gener,
            String days,
            //String H,
            //String W,
            //String C,
            String f,
            String p,
            String ca,
            String ve,
            String vi,
            String wa,
            String Ls,
            int modelo) throws Exception {
        Instances data;

        data = ConverterUtils.DataSource.read("data_2.arff");
        data.setClassIndex(9);

        NaiveBayes nb = new NaiveBayes();
        J48 jc = new J48();

        nb.buildClassifier(data);
        jc.buildClassifier(data);//train BD

        double pred = 0;
        Instance instance;

        if (data.numInstances() == 0) {
            throw new Exception("clasification no disponible");
        }
        instance = crearNuevaInstancia(
                gener,
                days,
                //H,
                //W,
                //C,
                f,
                p,
                ca,
                ve,
                vi,
                wa,
                Ls,
                data);
        nb.buildClassifier(data);

        if (modelo == 0) {
            pred = nb.classifyInstance(instance);
        } else {
            pred = jc.classifyInstance(instance);
        }
        String namePred = data.classAttribute().value((int) pred);

        return namePred;

    }

    //Gender,Days,AGE,Height,Weight,Calories,Fat,Protein,Cabohydrate,Vegitable,Vitamin,Water,Life_Style_Activity
    public Instance crearNuevaInstancia(
            String Gender,
            String Days,
            //String AGE,
            //String Height,
            //String Weight,
            //String Calories,
            String Fat,
            String Protein,
            String Cabohydrate,
            String Vegitable,
            String Vitamin,
            String Water,
            String Life_Style_Activity,
            Instances train) 
            throws IOException {
        Instance instance = new Instance(9);

        Attribute atributo = train.attribute("Gender");
        Attribute atributo1 = train.attribute("Days");
        //Attribute atributo2 = train.attribute("AGE");
       // Attribute atributo3 = train.attribute("Height");
        //Attribute atributo4 = train.attribute("Weight");
        //Attribute atributo5 = train.attribute("Calories");
        Attribute atributo2 = train.attribute("Fat");
        Attribute atributo3 = train.attribute("Protein");
        Attribute atributo4 = train.attribute("Cabohydrate");
        Attribute atributo5 = train.attribute("Vegitable");
        Attribute atributo6 = train.attribute("Vitamin");
        Attribute atributo7 = train.attribute("Water");
        Attribute atributo8 = train.attribute("Life_Style_Activity");

        instance.setValue(atributo, Gender);
        instance.setValue(atributo1, Days);
       // instance.setValue(atributo2, AGE);
       // instance.setValue(atributo3, Height);
        //instance.setValue(atributo4, Weight);
        //instance.setValue(atributo5, Calories);
        instance.setValue(atributo2, Fat);
        instance.setValue(atributo3, Protein);
        instance.setValue(atributo4, Cabohydrate);
        instance.setValue(atributo5, Vegitable);
        instance.setValue(atributo6, Vitamin);
        instance.setValue(atributo7, Water);
        instance.setValue(atributo8, Life_Style_Activity);

        instance.setDataset(train);

        return instance;
    }

    
    
    
    
    
    
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
            java.util.logging.Logger.getLogger(HealthyDietRecommender.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HealthyDietRecommender.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HealthyDietRecommender.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HealthyDietRecommender.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new HealthyDietRecommender().setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(HealthyDietRecommender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbDays;
    private javax.swing.JComboBox<String> cbFat;
    private javax.swing.JComboBox<String> cbGender;
    private javax.swing.JComboBox<String> cbModel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox10;
    private javax.swing.JComboBox<String> jComboBox11;
    private javax.swing.JComboBox<String> jComboBox12;
    private javax.swing.JComboBox<String> jComboBox14;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox8;
    private javax.swing.JComboBox<String> jComboBox9;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextField jTextField;
    // End of variables declaration//GEN-END:variables
}
