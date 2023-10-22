/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Debug.Random;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSource;
//import weka.core.pmml.jaxbbindings.DecisionTree;
import weka.filters.unsupervised.attribute.Remove;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

/**
 *
 * @author hp
 */
public class AttritionModel extends javax.swing.JFrame {

    Connection conn;
    PreparedStatement pst;
    ResultSet rs;
    /**
     * Creates new form TaskPrediction
     */
    File file1;
    JFileChooser jfc = new JFileChooser();

    //NOTE FOR FINAL
    public static final String TAB = "\t";
    //Connection connection = null;
    Instances Data;
    int nAttributes;
    String DataSet = new String();
    public static File DataFile;

    predictingModel D;
    RandomForest Classifier = new RandomForest();

    /**
     * Creates new form
     */
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
    // Function To Resize The Image To Fit Into JLabel
    public ImageIcon ResizeImage(String imagePath, byte[] pic) {
        ImageIcon myImage = null;

        if (imagePath != null) {
            myImage = new ImageIcon(imagePath);
        } else {
            myImage = new ImageIcon(pic);
        }

        Image img = myImage.getImage();
        Image img2 = img.getScaledInstance(label26.getWidth(), label26.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(img2);

        return image;

    }

    public AttritionModel() throws IOException, Exception {
        getContentPane().setBackground(Color.pink);
        initComponents();

        DataFile = GetFileTrainData("Datasets-04-0-1.arff");
        this.Data = new Instances(new java.io.FileReader(DataFile));
        this.nAttributes = this.Data.numAttributes();
        this.Data.setClassIndex(this.nAttributes - 1);
        //this.Classifier.buildClassifier(this.Data);
        System.out.println("Status: Classifier trained.");

        //for (int j = 100; j <=400; j++) {
        // this.jComboBox2.addItem(String.valueOf(j));
        //}
        CurrentDate();
        cl();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        setTitle("        ");
        setResizable(false);
        setVisible(true);

        String curDir = System.getProperty("user.dir");
        jTextField1.setText(curDir + File.separator + "Dataset" + File.separator + "Datasets-04-0-1.arff");

        jTextField2.setText(curDir + File.separator + "Dataset" + File.separator + "Trainset-01.arff");
        jTextField3.setText(curDir + File.separator + "Dataset" + File.separator + "Testset-02.arff");

    }

    public static Evaluation classify(Classifier model,
            Instances trainingSet, Instances testingSet) throws Exception {
        Evaluation evaluation = new Evaluation(trainingSet);

        model.buildClassifier(trainingSet);
        evaluation.evaluateModel(model, testingSet);

        return evaluation;
    }

    public static double calculateAccuracy(FastVector predictions) {
        double correct = 0;

        for (int i = 0; i < predictions.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
            if (np.predicted() == np.actual()) {
                correct++;
            }
        }

        return correct * 100 / predictions.size();
    }

    //double accuracy = (cp * 100) / (cp + icp);
    public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds) {
        Instances[][] split = new Instances[2][numberOfFolds];

        for (int i = 0; i < numberOfFolds; i++) {
            split[0][i] = data.trainCV(numberOfFolds, i);
            split[1][i] = data.testCV(numberOfFolds, i);
        }

        return split;
    }

    DefaultTableModel dtm;

    void cl() {
        dtm = (DefaultTableModel) jTable1.getModel();
        dtm.setRowCount(0);
        String sql = "select * from attrition_tbl2";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/attrition", "root", "");
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                dtm.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11),
                    rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16), rs.getString(17), rs.getString(18), rs.getString(19), rs.getString(20), rs.getString(21), rs.getString(22), rs.getString(23),
                    rs.getString(24), rs.getString(25), rs.getString(26), rs.getString(27), rs.getString(28)});
            }

        } catch (Exception e) {
            System.out.println("" + e);
        }
    }

    public void CurrentDate() {
        Calendar cal = new GregorianCalendar();
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Date_txt.setText("DATE" + ":" + year + "/" + (month + 1) + "/" + day);

        int second = cal.get(Calendar.SECOND);
        int minute = cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR);
        Time_txt.setText("TIME" + ":" + hour + ":" + minute + ":" + second);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        Date_txt = new javax.swing.JLabel();
        Time_txt = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        Progresslabel = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel15 = new javax.swing.JPanel();
        jTextField5 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        pnlChart = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        Progresslabel1 = new javax.swing.JLabel();
        jProgressBar2 = new javax.swing.JProgressBar();
        jPanel21 = new javax.swing.JPanel();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        btnTraingBrowse = new javax.swing.JButton();
        btnTextBrowse = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jComboBox5 = new javax.swing.JComboBox<>();
        jComboBox6 = new javax.swing.JComboBox<>();
        jComboBox7 = new javax.swing.JComboBox<>();
        jComboBox8 = new javax.swing.JComboBox<>();
        jComboBox9 = new javax.swing.JComboBox<>();
        jComboBox10 = new javax.swing.JComboBox<>();
        jComboBox11 = new javax.swing.JComboBox<>();
        jLabel63 = new javax.swing.JLabel();
        jComboBox53 = new javax.swing.JComboBox<>();
        jPanel12 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jComboBox17 = new javax.swing.JComboBox<>();
        jComboBox18 = new javax.swing.JComboBox<>();
        jComboBox19 = new javax.swing.JComboBox<>();
        jComboBox20 = new javax.swing.JComboBox<>();
        jComboBox21 = new javax.swing.JComboBox<>();
        jComboBox22 = new javax.swing.JComboBox<>();
        jComboBox23 = new javax.swing.JComboBox<>();
        jComboBox24 = new javax.swing.JComboBox<>();
        jComboBox25 = new javax.swing.JComboBox<>();
        jComboBox26 = new javax.swing.JComboBox<>();
        jComboBox27 = new javax.swing.JComboBox<>();
        jComboBox28 = new javax.swing.JComboBox<>();
        jComboBox29 = new javax.swing.JComboBox<>();
        jComboBox30 = new javax.swing.JComboBox<>();
        jComboBox31 = new javax.swing.JComboBox<>();
        jComboBox32 = new javax.swing.JComboBox<>();
        jPanel14 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jComboBox12 = new javax.swing.JComboBox<>();
        jComboBox13 = new javax.swing.JComboBox<>();
        jComboBox14 = new javax.swing.JComboBox<>();
        jComboBox15 = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        jComboBox16 = new javax.swing.JComboBox<>();
        jPanel9 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jComboBox33 = new javax.swing.JComboBox<>();
        jComboBox34 = new javax.swing.JComboBox<>();
        jComboBox35 = new javax.swing.JComboBox<>();
        jComboBox36 = new javax.swing.JComboBox<>();
        jComboBox37 = new javax.swing.JComboBox<>();
        jPanel17 = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jComboBox38 = new javax.swing.JComboBox<>();
        jComboBox39 = new javax.swing.JComboBox<>();
        jComboBox40 = new javax.swing.JComboBox<>();
        jComboBox41 = new javax.swing.JComboBox<>();
        jComboBox42 = new javax.swing.JComboBox<>();
        jComboBox43 = new javax.swing.JComboBox<>();
        jComboBox44 = new javax.swing.JComboBox<>();
        jComboBox45 = new javax.swing.JComboBox<>();
        jComboBox46 = new javax.swing.JComboBox<>();
        jComboBox47 = new javax.swing.JComboBox<>();
        jLabel74 = new javax.swing.JLabel();
        jComboBox62 = new javax.swing.JComboBox<>();
        jPanel18 = new javax.swing.JPanel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jComboBox48 = new javax.swing.JComboBox<>();
        jComboBox49 = new javax.swing.JComboBox<>();
        jLabel62 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jComboBox50 = new javax.swing.JComboBox<>();
        jComboBox51 = new javax.swing.JComboBox<>();
        jComboBox52 = new javax.swing.JComboBox<>();
        jPanel24 = new javax.swing.JPanel();
        jLabel87 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jComboBox75 = new javax.swing.JComboBox<>();
        jComboBox76 = new javax.swing.JComboBox<>();
        jComboBox78 = new javax.swing.JComboBox<>();
        jComboBox79 = new javax.swing.JComboBox<>();
        jComboBox80 = new javax.swing.JComboBox<>();
        jComboBox81 = new javax.swing.JComboBox<>();
        jComboBox82 = new javax.swing.JComboBox<>();
        jComboBox86 = new javax.swing.JComboBox<>();
        jPanel13 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel22 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jComboBox71 = new javax.swing.JComboBox<>();
        jComboBox72 = new javax.swing.JComboBox<>();
        jComboBox73 = new javax.swing.JComboBox<>();
        jComboBox74 = new javax.swing.JComboBox<>();
        jLabel86 = new javax.swing.JLabel();
        jComboBox83 = new javax.swing.JComboBox<>();
        jTextField8 = new javax.swing.JTextField();
        jButton23 = new javax.swing.JButton();
        label26 = new javax.swing.JLabel();
        cbModels = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 102), 3, true));
        jTabbedPane1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mg/1.GIF"))); // NOI18N
        jLabel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mg/1.GIF"))); // NOI18N
        jLabel24.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 723, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(147, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("HOME PAGE", jPanel1);

        Date_txt.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        Date_txt.setForeground(new java.awt.Color(204, 0, 0));
        Date_txt.setText("jLabel2");
        Date_txt.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 102, 0), 2, true));

        Time_txt.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        Time_txt.setForeground(new java.awt.Color(204, 0, 0));
        Time_txt.setText("jLabel3");
        Time_txt.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 102, 0), 2, true));

        jTable1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "gender", "Marital_Stat ", "Prog", "City", "Mode_Study", "Age", "Employ_Stat ", "Family_Size", "Job_Course", "Sponsor", "prog_Motive", "NAAC", "Atten_ Lect ", "Sup_Area", "Sup_Busy", "incompact_Sup ", "Tatal_Grade_Score_snd ", "Sup_Not_uptodate", "Spu_Commitment", "Sup_Unavailable", "Sup_Expertise", "Study_conflits_job ", "Access-Internet ", "Diff_Research_Topic", "Lack_ICT Knowledge", "Insuff_Know_Research ", "Strike", "Lack_Proper Guide"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane9.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 1362, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Date_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(Time_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(98, 98, 98))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Time_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 549, Short.MAX_VALUE))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane9)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("View Dataset", jPanel20);

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));

        jButton1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton1.setText("Data Minining Classifier(RF)");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel2.setText("Full Training Model");

        jTextField1.setEditable(false);
        jTextField1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jTextField1.setText("jTextField1");

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        Progresslabel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        Progresslabel.setText("Progresslabel");

        jProgressBar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jProgressBar1MouseClicked(evt);
            }
        });
        jProgressBar1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jProgressBar1KeyReleased(evt);
            }
        });

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Computation Time", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(204, 51, 0))); // NOI18N

        jTextField5.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel16.setText("Training Time (second)");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jButton4.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton4.setText("Data Minining Classifier(RT)");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton6.setText("Data Minining Classifier(kNN)");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton7.setText("Data Minining Classifier(ANN)");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton8.setText("Data Minining Classifier(SVM)");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton9.setText("Data Minining Classifier(DT)");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jDesktopPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 6, true));

        pnlChart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlChart.setLayout(new java.awt.BorderLayout());

        jDesktopPane1.setLayer(pnlChart, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlChart, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDesktopPane1Layout.createSequentialGroup()
                .addComponent(pnlChart, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton11.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton11.setText("Refresh");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Progresslabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 141, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(129, 129, 129))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jDesktopPane1)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Progresslabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDesktopPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Training Model", jPanel2);

        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jTextField2.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jTextField2.setText("jTextField2");
        jTextField2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));

        jTextField3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jTextField3.setText("jTextField3");
        jTextField3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));

        jLabel3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel3.setText("Training Set (90%)");
        jLabel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));

        jLabel4.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel4.setText("Test Set (10%)");
        jLabel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Model Training and Testing", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(0, 0, 204))); // NOI18N

        jComboBox1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Classificier", "RandomTree", "RandomForest", "kNN", "SVM", "ANN", "DT" }));

        jButton2.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton2.setText("Execute Task");
        jButton2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Model Training Task", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(0, 0, 204))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton3.setText("Refresh");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Model on Testing Set Evaluation", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(0, 0, 204))); // NOI18N

        jButton5.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton5.setText("RF");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton17.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton17.setText("RT");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton18.setText("kNN");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton19.setText("SVM");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton20.setText("ANN");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton21.setText("DT");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton17)
                .addGap(18, 18, 18)
                .addComponent(jButton18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton21)
                .addGap(262, 262, 262))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton5)
                        .addComponent(jButton17)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 565, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(96, 96, 96))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Progresslabel1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        Progresslabel1.setText("Progresslabel");

        jProgressBar2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jProgressBar2MouseClicked(evt);
            }
        });
        jProgressBar2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jProgressBar2KeyReleased(evt);
            }
        });

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Computation Time", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(153, 0, 0))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel12.setText("Training Time(second)");

        jLabel13.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel13.setText("Testing Time(second)");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .addComponent(jTextField6))
                .addGap(67, 67, 67))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jLabel5.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel5.setText("MODEL TESTING SET SUMARY ");

        jTextArea3.setColumns(20);
        jTextArea3.setRows(5);
        jScrollPane3.setViewportView(jTextArea3);

        jLabel6.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel6.setText("MODEL TRAINING SET SUMARY ");

        btnTraingBrowse.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btnTraingBrowse.setText("BROWSE-TRAINING SET");
        btnTraingBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTraingBrowseActionPerformed(evt);
            }
        });

        btnTextBrowse.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btnTextBrowse.setText("BROWSE-TESTING SET");
        btnTextBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTextBrowseActionPerformed(evt);
            }
        });

        jButton12.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton12.setText("RF ROC");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton13.setText("RT ROC");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton14.setText("kNN ROC");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton15.setText("SVM ROC");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton16.setText("DT ROC");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton22.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton22.setText("ANN ROC");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(btnTraingBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(btnTextBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(Progresslabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(840, 840, 840)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 630, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton12)
                            .addComponent(jButton13)
                            .addComponent(jButton14)
                            .addComponent(jButton15)
                            .addComponent(jButton16)
                            .addComponent(jButton22))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTraingBrowse)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTextBrowse)
                            .addComponent(Progresslabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(11, 11, 11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(106, 106, 106)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane1.addTab("Training Set and Test set", jPanel3);

        jTextArea4.setColumns(20);
        jTextArea4.setRows(5);
        jScrollPane4.setViewportView(jTextArea4);

        jLabel7.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel7.setText("                                                          PREDICTING ON TESTING MODEL ");

        jTextArea5.setColumns(20);
        jTextArea5.setRows(5);
        jScrollPane5.setViewportView(jTextArea5);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 738, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab(" Evalaution ofTesting Model Prediction", jPanel7);

        jLabel23.setText("jLabel23");

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DEMOGRAPHIC FACTORS:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(102, 0, 255))); // NOI18N

        jLabel9.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel9.setText("Gender ");
        jLabel9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel10.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel10.setText("Marital Status");
        jLabel10.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel11.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel11.setText("What program are you currently running");
        jLabel11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel14.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel14.setText("City of Residence ");
        jLabel14.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel15.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel15.setText("Students’ Mode of Study");
        jLabel15.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel17.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel17.setText("Students’ Age ");
        jLabel17.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel18.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel18.setText("Are you currently employed full-time ");
        jLabel18.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel19.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel19.setText("Family size");
        jLabel19.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel20.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel20.setText("Job  course");
        jLabel20.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel21.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel21.setText("Sponsor’s highest educational qualifications");
        jLabel21.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1", "2" }));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "2", "1" }));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "2", "1", "3" }));

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1", "2" }));

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1", "2" }));

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "2", "1", "3", "4" }));

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1", "3", "2" }));

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "2", "3", "1" }));

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1", "4", "3", "2" }));

        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1", "2", "3", "4" }));

        jLabel63.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel63.setText("Reasons for Pursuit of Education Studies ");
        jLabel63.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jComboBox53.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "2", "5", "1", "3", "4" }));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel63, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox6, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox7, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox8, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox9, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox10, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox11, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox53, 0, 171, Short.MAX_VALUE)))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ACADEMIC RELATED FACTORS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(0, 204, 102))); // NOI18N
        jPanel12.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N

        jLabel29.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel29.setText("Incompatibility with supervisor");
        jLabel29.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel30.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel30.setText("Supervisor is not up to date in the field");
        jLabel30.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel31.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel31.setText("My supervisor lacks commitment");
        jLabel31.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel32.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel32.setText("My supervisor is not always available to devote sufficient time for supervision");
        jLabel32.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel33.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel33.setText("My Supervisor lacks expertise on students topic");
        jLabel33.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel34.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel34.setText("Modality of study conflicts with my employment");
        jLabel34.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel35.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel35.setText("Lack of access to research materials");
        jLabel35.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel36.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel36.setText("Difficulties in generating researchable topic");
        jLabel36.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel37.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel37.setText("Lack of ICT knowledge of research method");
        jLabel37.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel38.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel38.setText("Insufficient knowledge of research method");
        jLabel38.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel39.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel39.setText("Frequent closure due to strike actions");
        jLabel39.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel40.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel40.setText("Lack of proper guidance");
        jLabel40.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel41.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel41.setText("Funding is major problem in my academic pursuit");
        jLabel41.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel42.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel42.setText("Accommodation is a major problem in my academic pursuit");
        jLabel42.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel43.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel43.setText("Poor library facilities, Standard equipment and Laboratory");
        jLabel43.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel44.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel44.setText("Lecturer/Teachers submit Semester results on time ");
        jLabel44.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jComboBox17.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "5", "2", "1", "4", "3" }));

        jComboBox18.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "2", "4", "1", "5" }));

        jComboBox19.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "2", "4", "1", "5" }));

        jComboBox20.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "2", "5", "1", "4" }));

        jComboBox21.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "4", "2", "3", "1" }));

        jComboBox22.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "5", "4", "2", "1" }));

        jComboBox23.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1", "4", "3", "2", "5" }));

        jComboBox24.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "2", "1", "4", "5" }));

        jComboBox25.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "4", "2", "1", "5" }));

        jComboBox26.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "2", "4", "1", "5" }));

        jComboBox27.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "4", "2", "1", "5" }));

        jComboBox28.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "2", "4", "5", "1" }));

        jComboBox29.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "4", "3", "1", "2", "5" }));

        jComboBox30.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "2", "4", "1", "5" }));

        jComboBox31.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "4", "2", "5", "1" }));

        jComboBox32.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "2", "4", "1", "3", "5" }));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox17, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox18, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox19, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox20, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox21, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox22, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox23, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox24, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox25, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox26, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox27, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox28, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox29, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox30, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox31, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox32, 0, 119, Short.MAX_VALUE)))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(jComboBox29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(jComboBox31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44)
                    .addComponent(jComboBox32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ACADEMIC RELATED FACTORS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(0, 204, 102))); // NOI18N

        jLabel22.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel22.setText("Non-adherence to Academic calendars by Lecturers");
        jLabel22.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel25.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel25.setText("I am always Present for Lectures and Other activities");
        jLabel25.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel27.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel27.setText("My Supervisor is specialized in my area of Research ");
        jLabel27.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "1", "4", "2", "5" }));

        jComboBox13.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "1", "2", "4", "5" }));

        jComboBox14.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "2", "3", "1", "5", "4" }));

        jComboBox15.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "2", "4", "1", "3", "5" }));

        jLabel28.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel28.setText("The supervisor is too busy with extensive commitment");
        jLabel28.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jComboBox16.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "2", "4", "1", "3", "5" }));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox12, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox13, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox14, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox15, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox16, javax.swing.GroupLayout.Alignment.TRAILING, 0, 102, Short.MAX_VALUE)))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(49, 49, 49))))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 20, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(121, 121, 121)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Deployment", jPanel4);

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "SOCIAL FACTOR:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(204, 204, 0))); // NOI18N

        jLabel45.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel45.setText("I keep a considerable numbers of friends/family");
        jLabel45.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel46.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel46.setText("I do hang out with my friends/Family regularly");
        jLabel46.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel47.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel47.setText("The use of stimulants/drugs enhances my study");
        jLabel47.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel48.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel48.setText("I access the internet as often as possible for Research");
        jLabel48.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel49.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel49.setText("My Parents /Partner encourages me in my Education Career Pursuit");
        jLabel49.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jComboBox33.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "2", "3", "5", "4", "1" }));

        jComboBox34.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "5", "2", "4", "1" }));

        jComboBox35.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "2", "1" }));

        jComboBox36.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "1", "2", "4", "5" }));

        jComboBox37.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "3", "4", "2", "5", "1" }));

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel48, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox33, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox34, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox35, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox36, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox37, 0, 148, Short.MAX_VALUE)))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(jComboBox34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(251, 251, 251))
        );

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ACADEMIC ASSESSMENT FACTORS:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(0, 153, 153))); // NOI18N

        jLabel50.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel50.setText("First Semester Course");
        jLabel50.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel51.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel51.setText("Total Grade Score");
        jLabel51.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel52.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel52.setText("Grade Level");
        jLabel52.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel53.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel53.setText("Total Registered Credit Unit");
        jLabel53.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel54.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel54.setText("Total Earn Credit Unit");
        jLabel54.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel55.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel55.setText("Total Credit Point");
        jLabel55.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel56.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel56.setText("Grade Point Average");
        jLabel56.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel57.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel57.setText(" Cumulative Total Register Credit Unit");
        jLabel57.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel58.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel58.setText("Cumulative Total Earn Credit Unit");
        jLabel58.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel59.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel59.setText("CumulativeTotal Credit Point");
        jLabel59.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jComboBox38.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "200", "300", "400" }));

        jComboBox39.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "19", "14", "36", "40", "23", "37", "55", "69", "0", "26", "33", "29", "41", "27", "56", "21", "51", "62", "47", "32", "60", "30", "43", "59", "68", "53", "28", "61", "25", "84", "70", "75", "78", "58", "85", "77", "92", "24", "12", "16", "20", "49", "72", "46", "45", "50", "64", "57", "54", "42", "73", "66", "44", "71", "74", "48", "65", "35", "31", "17", "9", "2", "87", "67", "52", "13", "22", "81", "76", "63", "86", "38", "15", "11", "34", "79", "10", "5", "18", "88", "4", "8" }));

        jComboBox40.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "F", "E", "C", "B", "no", "D", "A" }));

        jComboBox41.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "22", "24", "23", "20", "19", "21", "18", "16", "17" }));

        jComboBox42.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "9", "10", "20", "15", "24", "6", "17", "21", "16", "22", "8", "2", "23", "12", "13", "18", "14", "19", "3", "5", "0" }));

        jComboBox43.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "19", "24", "52", "31", "114", "8", "48", "65", "44", "72", "84", "32", "58", "12", "2", "89", "69", "27", "61", "22", "57", "87", "106", "34", "30", "36", "59", "91", "51", "28", "53", "16", "35", "29", "71", "45", "93", "14", "56", "63", "111", "66", "26", "20", "60", "81", "54", "40", "6", "11", "90", "5", "46", "0", "85", "77", "73", "47", "80", "21", "82", "55", "75", "64", "9", "62" }));

        jComboBox44.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "0.86", "1", "2.17", "1.35", "4.75", "0.4", "2.53", "2.71", "1.83", "3", "3.5", "1.52", "2.42", "0.67", "0.1", "0.55", "3.71", "1.13", "2.54", "1.42", "2.88", "0.96", "2.38", "3.63", "4.42", "2.13", "1.25", "1.5", "2.46", "3.79", "0.5", "2", "2.32", "0.92", "1.4", "2.21", "0.7", "1.04", "1.32", "3.09", "2.52", "1.88", "3.88", "0.74", "2.33", "2.74", "4.63", "2.75", "0.79", "0.87", "2.5", "3.38", "2.25", "1.67", "0.25", "0.48", "3.75", "0.23", "1.57", "2.61", "0", "1.14", "4.72", "3.67", "3.04", "2.76", "4.44", "0.88", "3.42", "2.62", "4.56", "3.33", "2.65", "2.4", "1.6", "1.43", "3.13", "3.2", "0.22", "0.43", "3.16", "2.58", "3.65" }));

        jComboBox45.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "68", "70", "69", "66", "65", "67", "64", "86", "93", "60", "62", "114", "116", "115", "117", "110", "103", "118", "99", "108", "133", "143", "141", "140", "146", "147", "148", "138", "144", "145", "142" }));

        jComboBox46.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox47.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel74.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel74.setText("level ");
        jLabel74.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jComboBox62.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "MTH101", "MTH103", "MTH105", "PHY111", "BIO111", "CSC205", "CSC211", "STA201", "GST201", "GST203", "no", "CSC203", "CHM101", "STA101", "GST103", "GST105", "GST101", "PHY131", "MTH201", "MTH207", "MTH209", "CSC101", "CSC204", "CSC206", "CSC208", "CSC212", "MTH208", "STA202", "PHY222", "GST202", "GST204", "CHM121", "CSC305", "CSC307", "CSC315", "CSC321", "CSC311", "CSC301", "CSC303", "CSC313", "0", "MTH225", "STA211", "MTH221", "CSC319", "PHY211", "GST205", "MTH102", "MTH104", "MTH106", "BIO112", "CSC405", "CSC409", "CSC401", "CSC403", "CSC407", "CSC411", "CSC433" }));

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel57, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel55, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel58, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel59, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel74, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox40, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox39, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox41, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox42, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox43, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox44, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox45, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox46, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox47, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox38, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox62, 0, 351, Short.MAX_VALUE)))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox38, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(jComboBox39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox40, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jComboBox42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(58, 58, 58)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel59)
                    .addComponent(jComboBox47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ACADEMIC ASSESSMENT FACTORS:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(0, 153, 153))); // NOI18N

        jLabel60.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel60.setText("Cumulative Total Earn Credict Unit ");
        jLabel60.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel61.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel61.setText("Cumulative Total Credict Point");
        jLabel61.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jComboBox48.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "35", "21", "62", "54", "70", "11", "37", "68", "65", "64", "25", "4", "93", "44", "38", "66", "52", "48", "50", "59", "16", "32", "51", "58", "55", "30", "97", "95", "117", "74", "114", "111", "112", "109", "103", "77", "113", "108", "67", "102", "88", "104", "106", "91", "105", "115", "141", "132", "81", "126", "139", "135", "78", "79", "127", "76" }));

        jComboBox49.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "75", "40", "170", "114", "305", "13", "93", "196", "159", "171", "277", "71", "169", "224", "41", "4", "53", "263", "248", "89", "176", "84", "173", "126", "192", "229", "307", "219", "76", "96", "103", "137", "261", "22", "82", "118", "106", "123", "111", "56", "147", "109", "245", "200", "532", "174", "343", "295", "282", "461", "108", "300", "359", "314", "122", "254", "251", "163", "326", "360", "486", "319", "128", "127", "268", "431", "218", "236", "184", "275", "293", "647", "246", "444", "392", "571", "143", "412", "432", "573", "404", "323", "239", "217", "425", "448", "582", "400", "296", "151", "154", "346", "518", "153", "311", "304", "344", "371" }));

        jLabel62.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel62.setText("Cumulative Grade  Point Average");
        jLabel62.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel64.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel64.setText("Remarks ");
        jLabel64.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel65.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel65.setText("Cumulative Total Credit Unit");
        jLabel65.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jComboBox50.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1.1", "0.57", "2.43", "1.65", "4.36", "0.2", "1.43", "2.8", "2.27", "2.44", "3.96", "1.06", "2.41", "3.2", "0.64", "0.06", "0.62", "3.76", "2.67", "1.27", "2.51", "1.29", "2.47", "1.83", "2.74", "3.27", "4.39", "3.13", "1.37", "1.47", "1.96", "3.73", "0.35", "1.32", "1.74", "1.51", "1.76", "1.59", "0.85", "2.1", "0.96", "2.11", "4.55", "1.58", "2.96", "2.54", "3.94", "1.05", "2.56", "3.09", "2.68", "1.03", "2.17", "2.18", "1.41", "2.79", "3.08", "4.15", "2.73", "1.48", "1.09", "2.29", "3.68", "1.88", "2.02", "1.57", "2.37", "0.82", "2.05", "4.59", "3.1", "4.05", "0.97", "3.02", "4.06", "2.87", "1.19", "2.31", "1.73", "1.52", "2.89", "4.13", "2.78", "2.04", "1.08", "3.67", "1.11", "2.21", "2.08", "2.36" }));

        jComboBox51.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "MTH101@@@MTH103@@@MTH105@@@PHY111@@@BIO112@@@MTH102@@@MTH104@@@MTH106@@@PHY122@@@CSC211@@@STA201", "CHM101@@@GST101@@@MTH105@@@PHY111@@@CHM122@@@GST102@@@GST106@@@MTH102@@@MTH104@@@MTH106@@@PHY122@@@PHY124@@@STA102@@@CSC203@@@CSC205", "PHY131@@@CSC211", "PHY131@@@CSC212", "PHY131@@@CSC213", "PHY131@@@CSC214", "PHY111@@@CHM122@@@MTH102@@@PHY124@@@MTH201@@@STA201", "PHY111@@@CHM122@@@MTH102@@@PHY124@@@MTH201@@@STA202", "PHY111@@@CHM122@@@MTH102@@@PHY124@@@MTH201@@@STA203", "PHY111@@@CHM122@@@MTH102@@@PHY124@@@MTH201@@@STA204", "no", "BIO111@@@GST101@@@MTH101@@@MTH103@@@MTH105@@@PHY111@@@STA101@@@BIO112@@@CHM122@@@CSC102@@@GST102@@@GST106@@@MTH102@@@MTH104@@@MTH106@@@PHY122@@@PHY124@@@STA102", "PHY111@@@BIO112@@@CHM162@@@GST106@@@MTH102@@@MTH104@@@PHY122@@@PHY124@@@STA102", "MTH201", "GST102", "CHM101@@@BIO112@@@GST104@@@GST106@@@MTH104@@@MTH106@@@PHY122@@@STA102@@@CSC203", "CHM101", "BIO111@@@MTH105@@@STA101@@@BIO112@@@GST102@@@GST104@@@GST106@@@MTH102@@@MTH104@@@MTH106@@@STA102@@@GST203@@@GST201", "BIO111@@@CSC101@@@GST101@@@GST105@@@MTH101@@@MTH103@@@MTH105@@@PHY111@@@STA101@@@BIO112@@@CHM122@@@CSC102@@@GST102@@@GST104@@@GST106@@@GST108@@@MTH102@@@MTH104@@@MTH106@@@PHY122@@@PHY124@@@STA102", "BIO111@@@MTH105@@@STA101@@@GST104@@@GST106@@@MTH102@@@GST203@@@GST201@@@GST202@@@GST204@@@PHY222", "MTH105@@@PHY111@@@GST102@@@GST106@@@STA102@@@CSC205@@@CSC211@@@MTH207", "CHM122@@@PHY122@@@PHY124", "CHM121@@@MTH105@@@GST102@@@MTH102@@@MTH104@@@MTH106@@@PHY124@@@STA102@@@CSC211", "PHY122", "MTH102@@@MTH104@@@CSC205@@@CSC211@@@MTH201@@@STA201", "MTH201@@@MTH207", "CHM122@@@GST102@@@MTH102@@@MTH104@@@MTH106@@@PHY122@@@PHY124@@@STA102", "CHM101@@@MTH103@@@MTH105@@@GST104@@@MTH106@@@STA102@@@STA201", "MTH104@@@MTH106@@@PHY122@@@MTH201@@@MTH209", "PHY111@@@CHM122@@@PHY122@@@PHY124", "BIO111@@@GST101@@@MTH101@@@MTH105@@@PHY111@@@BIO112@@@CHM122@@@CSC102@@@GST102@@@GST104@@@GST106@@@MTH102@@@MTH106@@@PHY122@@@PHY124@@@STA102", "BIO111@@@BIO112@@@GST104@@@GST106@@@MTH102@@@MTH104@@@MTH106@@@PHY122@@@PHY124@@@STA102", "GST104@@@MTH104@@@MTH106@@@STA102", "PHY131@@@MTH201@@@MTH207", "GST102@@@PHY124@@@CSC211", "CHM121@@@GST101@@@PHY124@@@CSC211", "MTH105@@@BIO112@@@CHM132@@@GST102@@@GST104@@@GST106@@@MTH102@@@MTH104@@@MTH106@@@PHY124@@@STA102", "MTH101@@@MTH103@@@MTH105@@@PHY111@@@BIO112@@@MTH102@@@MTH104@@@MTH106@@@STA201@@@CSC204@@@STA202@@@CSC307@@@CSC315", "PHY131@@@CSC307@@@CSC315@@@CSC311", "CHM122@@@CSC301", "PHY111@@@CSC205@@@STA211", "CSC301", "CSC302", "CSC303", "CSC304", "CSC305", "CSC306", "CSC307", "CSC308", "CSC309", "CSC310", "CSC311", "CSC312", "CSC313", "CHM101@@@PHY122@@@CSC205@@@MTH225@@@STA211", "GST102@@@CSC208@@@MTH208@@@PHY222@@@GST202@@@CSC303@@@CSC307@@@MTH221", "PHY122@@@CSC301@@@CSC315", "MTH102@@@MTH104@@@CSC205@@@CSC204@@@STA202@@@PHY222@@@CSC307@@@CSC321", "CSC208@@@MTH208@@@CSC313", "CHM122@@@MTH106@@@CSC307@@@GST201", "CHM101@@@MTH103@@@MTH105@@@GST104@@@STA201@@@MTH208@@@STA202@@@CSC301@@@CSC307@@@CSC315@@@CSC321", "MTH104@@@MTH106@@@MTH201@@@CSC204@@@MTH208@@@GST202@@@GST204@@@CSC321@@@CSC205@@@PHY211@@@STA211", "PHY111@@@CHM122", "BIO111@@@BIO112@@@MTH104@@@PHY122@@@CSC307@@@CSC315@@@CSC321@@@STA201@@@GST203@@@GST205", "GST104@@@MTH104@@@MTH208@@@MTH201", "CSC208@@@MTH208@@@CSC321@@@GST201", "MTH101@@@MTH103@@@MTH105@@@PHY111@@@BIO112@@@MTH102@@@MTH104@@@MTH106@@@STA201@@@CSC204@@@STA202@@@CSC307@@@CSC315@@@PHY222@@@GST202", "PHY131@@@CSC311@@@CSC405@@@CSC409", "PHY111@@@CSC205@@@STA211@@@CSC305", "CSC409", "CHM101@@@PHY122@@@CSC205@@@CSC206@@@CSC208@@@MTH226@@@STA212@@@CSC321@@@CSC311", "GST102@@@CSC208@@@MTH208@@@PHY222@@@GST202@@@CSC303@@@MTH221@@@CSC401@@@CSC305", "CHM122", "MTH102@@@MTH104@@@MTH106@@@STA201", "PHY122@@@CSC401", "MTH102@@@MTH104@@@CSC205@@@CSC204@@@STA202@@@PHY222@@@CSC405@@@CSC303", "CSC208@@@MTH208", "CHM101@@@MTH103@@@GST104@@@STA201@@@MTH208@@@STA202@@@CSC307@@@CSC315@@@CSC321@@@CSC405@@@CSC409@@@CSC311", "MTH104@@@MTH106@@@MTH201@@@CSC204@@@MTH208@@@GST202@@@GST204@@@CSC205@@@PHY211@@@STA211@@@CSC301@@@CSC305", "BIO111@@@BIO112@@@MTH104@@@PHY122@@@CSC307@@@GST203@@@GST205@@@CSC305@@@CSC311", "GST104@@@MTH104@@@MTH208", "GST201" }));

        jComboBox52.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "110", "124", "83", "91", "75", "134", "108", "77", "80", "81", "120", "141", "52", "101", "107", "79", "93", "97", "95", "86", "129", "113", "94", "87", "90", "115", "48", "50", "28", "71", "31", "34", "33", "36", "42", "68", "32", "37", "78", "43", "57", "41", "39", "54", "40", "30", "4", "13", "64", "19", "6", "10", "67", "66", "18", "69" }));

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel61, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel60, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel64, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel65, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox48, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox49, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox50, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox51, 0, 1, Short.MAX_VALUE)
                    .addComponent(jComboBox52, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jComboBox49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel24.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ACADEMIC ASSESSMENT FACTORS:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(0, 153, 153))); // NOI18N

        jLabel87.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel87.setText("Second Semester Course");
        jLabel87.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel88.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel88.setText("Total Registered Credit Unit");
        jLabel88.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel89.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel89.setText("Total Earn Credit Unit");
        jLabel89.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel90.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel90.setText("Total Credit Point");
        jLabel90.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel91.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel91.setText("Grade Point Average");
        jLabel91.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel92.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel92.setText(" Cumulative Total Register Credit Unit");
        jLabel92.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel97.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel97.setText("Remarks Second Semester");
        jLabel97.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel98.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel98.setText("Total Grade Score");
        jLabel98.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jComboBox75.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "MTH102", "MTH104", "MTH106", "PHY122", "CSC204", "CSC206", "CSC208", "CSC212", "STA202", "GST204", "no", "STA102", "PHY124", "GST102", "GST106", "BIO112", "MTH208", "PHY222", "GST202", "CHM122", "CSC102", "CHM162", "GST104", "GST108", "CHM132", "CSC398", "MTH226", "STA212", "CSC222", "CSC402", "CSC400", "CSC412", "CSC406", "CSC422", "CSC404", "CSC424" }));

        jComboBox76.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "18", "7", "32", "41", "45", "43", "38", "28", "25", "40", "0", "50", "53", "54", "44", "57", "71", "51", "56", "13", "73", "82", "84", "72", "75", "76", "61", "70", "89", "16", "6", "1", "3", "2", "64", "62", "66", "60", "55", "58", "49", "47", "52", "67", "74", "46", "48", "31", "69", "63", "26", "5", "4", "33", "23", "42", "59", "17", "9", "65", "34", "35", "27", "20", "24", "22", "68", "11", "10", "29", "15", "79", "80", "37", "30", "8" }));

        jComboBox78.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "23", "0", "17", "21", "22", "24", "15", "20", "6" }));

        jComboBox79.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "11", "0", "10", "23", "21", "2", "15", "4", "17", "20", "13", "16", "9", "7", "18", "24", "12", "6", "8", "22" }));

        jComboBox80.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "18", "0", "12", "51", "113", "2", "52", "76", "78", "66", "91", "23", "75", "72", "4", "87", "69", "14", "42", "40", "17", "74", "71", "98", "46", "31", "26", "13", "59", "80", "9", "24", "37", "50", "19", "68", "30", "20", "21", "6" }));

        jComboBox81.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "0.78", "0", "0.71", "2.22", "4.91", "0.1", "2.26", "3.3", "3.39", "2.87", "3.96", "1.35", "3.26", "3.13", "0.55", "0.17", "3.78", "3", "0.58", "1.83", "1.74", "2.36", "3.22", "3.09", "4.26", "2", "2.07", "1.13", "0.57", "2.57", "3.48", "0.45", "1", "1.68", "2.08", "1.54", "0.86", "2.96", "4", "5", "0.7", "30", "86", "36", "66", "60", "67", "78", "24", "56", "59", "83", "55", "26", "41", "63", "65", "8", "74", "77", "6", "69", "76", "9", "61", "62", "45" }));

        jComboBox82.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "91", "0", "87", "93", "92", "88", "84", "86", "90", "94", "75", "82", "122", "121", "123", "116", "124", "118", "114", "120", "1.5", "4.3", "1.57", "3.3", "3", "3.05", "3.71", "4", "2.8", "2.95", "4.15", "2.75", "1.13", "1.78", "2.74", "2.83", "0.33", "3.7", "3.85", "3.9", "2.63", "0.25", "3.14", "3.8", "0.39", "2.65", "3.1", "1.88" }));

        jComboBox86.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel92, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .addComponent(jLabel91, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel90, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel89, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel88, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel98, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel87, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel97, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox75, 0, 261, Short.MAX_VALUE)
                    .addComponent(jComboBox76, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox78, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox79, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox80, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox81, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox82, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox86, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel87, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox75, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox76, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox78, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel89, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox79, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel90, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel91, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox81, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox82, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(99, 99, 99)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel97)
                    .addComponent(jComboBox86, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(251, 251, 251))
        );

        jTabbedPane1.addTab("Next ", jPanel9);

        jTabbedPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 1, 14))); // NOI18N

        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ACADEMIC ASSESSMENT FACTORS:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(0, 153, 153))); // NOI18N

        jLabel81.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel81.setText("Cumulative Total Earn Credit Unit");
        jLabel81.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel82.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel82.setText("CumulativeTotal Credit Point");
        jLabel82.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel84.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel84.setText("Cumulative Grade  Point Average");
        jLabel84.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel85.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel85.setText("Remarks Second Semester");
        jLabel85.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jComboBox71.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "46", "0", "31", "85", "75", "93", "13", "60", "91", "88", "50", "87", "35", "8", "55", "86", "63", "89", "48", "64", "59", "80", "23", "67", "81", "83", "73", "42", "103", "101", "123", "120", "117", "118", "66", "76", "115", "119", "114", "90", "108", "61", "94", "110", "112", "97", "111", "163", "161", "166", "162", "153", "167", "171", "164", "168", "151", "170" }));

        jComboBox72.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "93", "0", "52", "221", "165", "418", "15", "145", "272", "237", "368", "94", "244", "296", "53", "8", "350", "248", "103", "218", "124", "225", "143", "266", "300", "405", "265", "107", "122", "116", "196", "341", "31", "106", "155", "172", "173", "148", "75", "215", "269", "562", "198", "367", "319", "312", "491", "330", "377", "344", "146", "278", "191", "187", "384", "516", "343", "286", "455", "129", "242", "260", "208", "305", "127", "161", "109", "158", "151", "162", "87", "149", "118", "97", "156", "153", "119", "84", "88", "82", "132", "139" }));

        jComboBox73.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1.02", "0", "0.6", "2.38", "1.79", "4.49", "0.17", "1.65", "2.92", "2.55", "3.96", "1.12", "2.62", "3.18", "0.62", "0.09", "3.76", "2.67", "1.1", "2.34", "1.41", "2.45", "1.54", "2.86", "3.23", "4.35", "2.85", "1.43", "1.31", "1.25", "2.11", "3.67", "0.38", "1.23", "1.72", "1.85", "1.84", "1.57", "0.85", "2.31", "2.2", "1.8", "4.57", "1.71", "3.01", "2.61", "2.54", "3.99", "0.99", "2.68", "3.09", "2.8", "1.18", "2.26", "1.62", "1.53", "3.12", "4.2", "2.79", "1.19", "2.33", "3.7", "1.13", "2.07", "1.98", "1.69", "2.5", "323", "733", "282", "510", "452", "426", "649", "167", "468", "491", "656", "459", "202", "364", "302", "372", "225", "499", "525", "660", "463", "320", "157", "180", "415", "594", "162", "366", "304", "434" }));

        jComboBox74.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "MTH101@@@MTH103@@@MTH105@@@PHY111@@@BIO112@@@MTH102@@@MTH104@@@MTH106@@@CSC211@@@STA201@@@CSC204@@@STA202", "no", "CHM101@@@GST101@@@MTH105@@@PHY111@@@CHM122@@@GST102@@@MTH102@@@PHY122@@@PHY124@@@CSC203@@@CSC205", "CHM101@@@GST101@@@MTH105@@@PHY111@@@CHM122@@@GST102@@@MTH102@@@PHY122@@@PHY124@@@CSC203@@@CSC206", "CHM101@@@GST101@@@MTH105@@@PHY111@@@CHM122@@@GST102@@@MTH102@@@PHY122@@@PHY124@@@CSC203@@@CSC207", "CHM101@@@GST101@@@MTH105@@@PHY111@@@CHM122@@@GST102@@@MTH102@@@PHY122@@@PHY124@@@CSC203@@@CSC208", "PHY131@@@CSC211", "PHY131@@@CSC212", "PHY131@@@CSC213", "PHY131@@@CSC214", "PHY111@@@CHM122@@@MTH201@@@STA201", "PHY111@@@CHM122@@@MTH201@@@STA202", "PHY111@@@CHM122@@@MTH201@@@STA203", "BIO111@@@GST101@@@MTH101@@@MTH103@@@MTH105@@@PHY111@@@STA101@@@BIO112@@@CHM122@@@CSC102@@@GST102@@@GST106@@@MTH102@@@MTH104@@@MTH106@@@PHY122@@@PHY124", "PHY111", "MTH201", "CHM101@@@PHY122@@@CSC203", "CHM101", "BIO111@@@MTH105@@@STA101@@@GST104@@@GST106@@@MTH102@@@GST203@@@GST201@@@GST202@@@GST204@@@PHY222", "BIO111@@@CSC101@@@GST101@@@GST105@@@MTH101@@@MTH103@@@MTH105@@@PHY111@@@STA101@@@BIO112@@@CHM122@@@CSC102@@@GST104@@@GST108@@@MTH102@@@MTH104@@@MTH106@@@PHY122@@@PHY124@@@STA102", "MTH105@@@PHY111@@@GST102@@@CSC205@@@CSC211@@@MTH207@@@CSC208@@@MTH208@@@PHY222@@@GST202", "CHM122", "CHM121@@@MTH105@@@MTH102@@@MTH104@@@MTH106@@@CSC211", "PHY122", "MTH102@@@MTH104@@@CSC205@@@CSC211@@@MTH201@@@STA201@@@CSC204@@@STA202@@@PHY222", "MTH201@@@MTH207", "CSC208@@@MTH208", "CHM122@@@MTH106", "CHM101@@@MTH103@@@MTH105@@@GST104@@@STA201@@@MTH208@@@STA202", "MTH104@@@MTH106@@@MTH201@@@MTH209@@@CSC204@@@MTH208@@@GST202@@@GST204", "PHY111@@@CHM122", "BIO111@@@GST101@@@MTH101@@@MTH105@@@PHY111@@@CHM122@@@GST102@@@GST106@@@MTH102@@@MTH106@@@PHY124@@@STA102", "BIO111@@@BIO112@@@MTH104@@@PHY122", "GST104@@@MTH104@@@MTH208", "PHY131@@@MTH201@@@MTH207", "CSC211", "CHM121@@@GST101@@@CSC211@@@CSC208@@@MTH208", "MTH105@@@BIO112@@@GST106@@@MTH104@@@GST202@@@GST204", "PHY131@@@CSC307@@@CSC315@@@CSC311", "CHM122@@@CSC301", "PHY111@@@CSC205@@@STA211", "CSC301", "CHM101@@@PHY122@@@CSC205@@@MTH225@@@STA211@@@CSC206@@@CSC208@@@MTH226@@@STA212", "GST102@@@CSC208@@@MTH208@@@PHY222@@@GST202@@@CSC303@@@CSC307@@@MTH221", "MTH105@@@MTH102@@@MTH104@@@MTH106@@@STA201", "MTH102@@@MTH104@@@CSC205@@@CSC204@@@STA202@@@PHY222@@@CSC307@@@CSC321", "CSC208@@@MTH208@@@CSC313", "CSC307@@@GST201", "CHM101@@@MTH103@@@MTH105@@@GST104@@@STA201@@@MTH208@@@STA202@@@CSC301@@@CSC307@@@CSC315@@@CSC321", "MTH104@@@MTH106@@@MTH201@@@CSC204@@@MTH208@@@GST202@@@GST204@@@CSC321@@@CSC205@@@PHY211@@@STA211", "BIO111@@@BIO112@@@MTH104@@@PHY122@@@CSC307@@@CSC315@@@CSC321@@@STA201@@@GST203@@@GST205", "GST104@@@MTH104@@@MTH208@@@MTH201", "CSC208@@@MTH208@@@CSC321@@@GST201", "PHY131@@@CSC311@@@CSC405@@@CSC409@@@CSC412@@@CSC406@@@CSC422", "PHY111@@@CSC205@@@STA211@@@CSC305@@@CSC402@@@CSC404@@@CSC222", "CSC412", "CSC409@@@CSC412", "CHM101@@@PHY122@@@CSC205@@@CSC206@@@CSC208@@@MTH226@@@STA212@@@CSC321@@@CSC311", "PHY222@@@CSC303@@@MTH221@@@CSC401@@@CSC305@@@GST204", "CHM122@@@CSC412@@@CSC222", "MTH104@@@STA201", "CSC401@@@CSC412@@@CSC424", "MTH104@@@CSC205@@@CSC204@@@STA202@@@PHY222@@@CSC405@@@CSC303@@@CSC404@@@CSC422@@@CSC400", "CSC404@@@CSC412", "CHM101@@@MTH103@@@GST104@@@STA201@@@MTH208@@@STA202@@@CSC307@@@CSC315@@@CSC321@@@CSC405@@@CSC409@@@CSC311@@@CSC402@@@CSC404@@@CSC412@@@CSC422", "MTH104@@@MTH201@@@CSC204@@@CSC205@@@PHY211@@@STA211@@@CSC301@@@CSC305@@@CSC400@@@CSC222", "PHY111@@@CHM122@@@CSC222", "BIO111@@@BIO112@@@MTH104@@@PHY122@@@CSC307@@@GST203@@@GST205@@@CSC305@@@CSC311@@@CSC412@@@CSC400@@@CSC212@@@GST202@@@GST204", "CSC412@@@CSC222", "GST201@@@CSC412" }));

        jLabel86.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel86.setText("Cumulative Total Credit Unit");
        jLabel86.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jComboBox83.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "99", "0", "114", "60", "70", "52", "132", "85", "54", "57", "95", "58", "110", "137", "90", "59", "82", "56", "97", "81", "86", "65", "122", "78", "64", "62", "72", "103", "42", "44", "22", "25", "28", "27", "79", "69", "30", "26", "31", "55", "37", "84", "51", "35", "33", "48", "34", "1.98", "4.55", "1.73", "3.13", "2.72", "2.63", "4.01", "1.09", "2.8", "3.01", "4.07", "2.85", "1.18", "2.23", "1.88", "2.27", "1.35", "2.99", "3.22", "4.1", "2.76", "2.12", "0.92", "1.08", "2.53", "3.69", "1.01", "2.2", "1.79", "2.6" }));

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel84, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel82, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel81, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .addComponent(jLabel85, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel86, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox71, 0, 257, Short.MAX_VALUE)
                    .addComponent(jComboBox72, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox73, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox74, 0, 257, Short.MAX_VALUE)
                    .addComponent(jComboBox83, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jComboBox71)
                        .addGap(1, 1, 1)))
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel82)
                    .addComponent(jComboBox72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel84)
                    .addComponent(jComboBox73, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel85)
                    .addComponent(jComboBox74, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox83, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel86))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextField8.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N

        jButton23.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jButton23.setText("PREDICT");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        label26.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        cbModels.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "SMO" }));
        cbModels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbModelsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(48, 48, 48))
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(label26, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(cbModels, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton23, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                    .addComponent(jTextField8))
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label26, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(cbModels, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 27, Short.MAX_VALUE))
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jTextField8)))
                .addGap(38, 38, 38)
                .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(297, 297, 297))
        );

        jTabbedPane2.addTab("Make Prediction", jPanel22);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addGap(22, 22, 22))
        );

        jTabbedPane1.addTab("Next and Predict", jPanel13);

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel1.setText("Prediction of Students' Attrition from pursuing Computer Science Degree using Machine Learning Techniques");
        jLabel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));

        jMenu1.setText("File");

        jMenuItem1.setText("Exit/Close");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem2.setText("CreateAccount");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1357, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents


    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        long startTime; //start time
        long endTime;   //end time
        double time;    //time difference

        startTime = System.currentTimeMillis();

        try {

            String filename = jTextField2.getText();
            String filename1 = jTextField3.getText();
            DataSource source = new DataSource(filename);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            //testing Dataset
            DataSource source1 = new DataSource(filename1);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 1);

            if (!trainDataset.equalHeaders(testDataset)) {
                throw new IllegalArgumentException(
                        "Datasets are not compatible!");
            }

            Remove rm = new Remove();
            rm.setAttributeIndices("1");

            RandomForest objVote = new RandomForest();

            System.out.println("=== Number of Training Instances ===");
            System.out.println(trainDataset.numInstances());
            System.out.println();

            System.out.println("=== Classifier model (full training set) ===");
            System.out.println();
            System.out.println(objVote);

            System.out.println("=== Building Model ===");
            long starttime = System.currentTimeMillis();

            /*Build all the classifiers*/
            objVote.buildClassifier(trainDataset);
            int total_instances = 0;
            /*Use majority vote to predict test set*/
            Evaluation eval = new Evaluation(testDataset);
            eval.evaluateModel(objVote, testDataset);

            FastVector predictions = new FastVector();
            jTextArea1.append("\n  \n");
            predictions.appendElements(eval.predictions());
            jTextArea1.append(eval.toSummaryString("\nModel Information\n=================\n", true));
            jTextArea1.append(eval.toClassDetailsString("\n\n   \n\n"));
            jTextArea1.append(eval.toMatrixString("\n\n   \n\n"));

            jTextArea1.append(("\n\n                          \n\n"));

            jTextArea4.append("\n\nAttribute value                 Predicted class\n\n");

            for (int i = 0; i < testDataset.numInstances(); i++) {
                double pred = objVote.classifyInstance(testDataset.instance(i));

                //   labeled.instance(i).setClassValue(pred);
                //   System.out.println(pred + " -> " + testDataset.classAttribute().value((int) pred));
                String actual;
                String predicted;
                actual = testDataset.classAttribute().value((int) testDataset.instance(i).classValue());
                predicted = testDataset.classAttribute().value((int) pred);

                jTextArea4.append("ID: " + i);
                jTextArea4.append(", actual: " + actual);
                jTextArea4.append(", predicted: " + predicted);
                jTextArea4.append("\n-----------------------------------------------------------\n");

                total_instances++;
            }
            double accuracy = calculateAccuracy(predictions);
            jTextArea1.append("\n accuracy : " + String.format("%.2f%%", accuracy));

            //System.out.println(models.toString());
            //jProgressBar1.setValue(j);
            // btnStartTraining.setEnabled(false);
            jProgressBar1.setVisible(true);
            repaint();
            for (int n = 0; n <= 100; n += 2) {
                jProgressBar1.setBorderPainted(false);
                jProgressBar1.setValue(jProgressBar1.getValue() + n);
                Progresslabel.setText("Progress is completed... " + jProgressBar1.getString());

                jProgressBar1.setValue(n);

                try {
                    jProgressBar1.paintImmediately(0, 0, 200, 25);//0, 1, 100, 10
                    Thread.sleep(50);
                    jProgressBar1.setStringPainted(false);

                } catch (Exception e) {
                }
                //JOptionPane.showMessageDialog(null, "Finished Execution.");

            }

            // Calculate overall accuracy of current classifier on all splits
            endTime = System.currentTimeMillis();// end execution Time
            time = (endTime - startTime) / 1000.0;

            System.out.println("\nElapsed Time is:  " + time);

            jTextField5.setText("" + time);

            // DefaultCategoryDataset pieChart = new  DefaultCategoryDataset();
            DefaultPieDataset pieChart = new DefaultPieDataset();
            //Step one
            // pieChart.setValue("Model Accurancy",accurancy);

            pieChart.setValue("Model Accuracy  :" + String.format("%.2f%%", accuracy), eval.areaUnderROC(0));
            pieChart.setValue("Correctly Classified Instances :" + eval.correct(), eval.recall(0));
            pieChart.setValue("Incorrectly Classified Instances:" + eval.incorrect(), eval.precision(1));

            JFreeChart piebar = ChartFactory.createPieChart("Model Visualization Accuracy For Random Forest", pieChart, true, true, false);
            //CategoryPlot piebar2 = pieChart.getCategoryPlot();
            //piebar2.setRangeGridlinePaint(Color.blue);
            ChartPanel piebar3 = new ChartPanel(piebar);
            ChartPanel piebar4 = new ChartPanel(piebar);
            ChartPanel piebar5 = new ChartPanel(piebar);
            //pnlChart2.removeAll();
            //pnlChart2.add(piebar3, BorderLayout.CENTER);
            //pnlChart2.validate();

            //pnlChart1.removeAll();
            //pnlChart1.add(piebar4, BorderLayout.CENTER);
            //pnlChart1.validate();
            pnlChart.removeAll();
            pnlChart.add(piebar5, BorderLayout.CENTER);
            pnlChart.validate();

            String row3 = "Pass" + "  " + ",Probation, " + "  " + ",Withdraw, " + "  " + ",not_graduate," + "  " + ", Graduate," + "  ";
            row3.getClass().toString();

            DefaultCategoryDataset barChart3 = new DefaultCategoryDataset();
            /*barChart.setValue(2000,"Contribution Amount ", "January");
                barChart.setValue(15000,"Contribution Amount ", "February");
                barChart.setValue(3000, "Contribution Amount ", "March"); */
            final String TPRate8 = "TP Rate";
            final String FPRate8 = "FP Rate";
            final String Precision8 = "Precision";
            final String Recall8 = "Recall";
            final String FMeasure8 = "F-Measure";
            final String MCC8 = "MCC";
            final String ROCArea8 = "ROC Area";
            final String PRCArea8 = "PRC Area";

            //class value
            final String systemdeveloper = "Pass";
            final String BusinessProcessAnalyst = "Probation";
            final String developer = "Withdraw";
            final String testing = "not_graduate";
            final String security = "Graduate";
            //final String cloudcomputing = "Cloud Computing '";

            // System Developer
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            //Add Value

            // barChart3.setValue(eval.truePositiveRate(0),  TPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);

            //Add Value
            //barChart3.addValue(null,FPRate8,  systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);

            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);

            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);

            //barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);

            // barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            //barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            //barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            //barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            //barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            //barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            //BusinessProcessAnalyst
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);

            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);

            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);

            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);

            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);

            /* barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);*/
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);

            // barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            // barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            //barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            //barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            //barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            //barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            //developer
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);

            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);

            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);

            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);

            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);

            //barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);

            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //Testing
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);

            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);

            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);

            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);

            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);

            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);

            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //Security
            //barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            //barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            //barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            //barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            //barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            //barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            //barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            //barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            //barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            //barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            //barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            //barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);

            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);

            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);

            //barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);

            //barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            //barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            //barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            //barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            //barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            //barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            //Cloud Computing
            /*barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);

            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);

            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);

            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);

            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);

            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);

            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);

            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);*/
            JFreeChart bar9 = ChartFactory.createBarChart3D("Model Evaluation for Random Forest", "Metrics Evaluation ", " ", barChart3, PlotOrientation.VERTICAL, false, true, false);
            CategoryPlot bar10 = bar9.getCategoryPlot();
            bar10.setRangeGridlinePaint(Color.blue);
            ChartPanel bar11 = new ChartPanel(bar9);
            final CategoryPlot p = bar9.getCategoryPlot();

            BarRenderer barRenderer = (BarRenderer) p.getRenderer();
            DecimalFormat decimalFormat = new DecimalFormat("#.##");

            barRenderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{1}", decimalFormat));
            p.setRenderer(barRenderer);
            barRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.TOP_LEFT));
            barRenderer.setItemLabelsVisible(true);
            bar9.getCategoryPlot().setRenderer(barRenderer);

            ValueMarker marker = new ValueMarker(0.70);
            marker.setLabel("Require Level");

            marker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
            marker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            marker.setPaint(Color.BLACK);
            p.addRangeMarker(marker);
            //pnlChart2.removeAll();
            //pnlChart2.add(bar11, BorderLayout.CENTER);
            //pnlChart2.validate();

            // System.out.println(timeSeconds + " seconds");
            /// break;
            //default:
            //System.out.println("Invalid Choice, run the project again");
            // System.exit(0);

            /*Writing Output*/
            System.out.println("=== Time taken to Build Classifiers & Test ===");
            System.out.println();
            // System.out.println(timeSeconds + " seconds");

            System.out.println(eval.toSummaryString("\nResults\n===============\n", true));

            System.out.println("For <Pass " + "F-Measure :" + eval.fMeasure(0) + " Precision :" + eval.precision(0) + " Recall :" + eval.recall(0));

            System.out.println("For >Probation " + "F-Measure :" + eval.fMeasure(1) + " Precision :" + eval.precision(1) + " Recall :" + eval.recall(1));

            System.out.println("For <Withdraw" + "F-Measure :" + eval.fMeasure(2) + " Precision :" + eval.precision(2) + " Recall :" + eval.recall(2));

            System.out.println("For >not_graduate" + "F-Measure :" + eval.fMeasure(3) + " Precision :" + eval.precision(3) + " Recall :" + eval.recall(3));

            System.out.println("For <Graduate" + "F-Measure :" + eval.fMeasure(4) + " Precision :" + eval.precision(4) + " Recall :" + eval.recall(4));

            //System.out.println("For >cloud computing " + "F-Measure :" + eval.fMeasure(5) + " Precision :" + eval.precision(5) + " Recall :" + eval.recall(5));

            /*To print the confusion Matrix*/
            double[][] dConfusionMatrix = eval.confusionMatrix();
            System.out.println("=== Confusion Matrix ===");
            System.out.println("a" + "\t" + "b" + "\t" + "c" + "\t" + "d" + "\t" + "e" + "\t" + "f");
            for (int i = 0; i < 5; i++) {

                for (int j = 0; j < 5; j++) {
                    System.out.print((int) dConfusionMatrix[i][j] + "\t");
                }
                if (i == 0) {
                    System.out.print(" |a =  <=Pass");
                }
                if (i == 1) {
                    System.out.print(" |b =  >Probation");
                }
                if (i == 2) {
                    System.out.print(" |c =  <=Withdraw");
                }
                if (i == 3) {
                    System.out.print(" |d =  >not_graduate");
                }
                if (i == 4) {
                    System.out.print(" |e =  <=Graduate");
                }

                System.out.println();

            }

            JOptionPane.showMessageDialog(null, "Finished Execution.");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }


    }//GEN-LAST:event_jButton1ActionPerformed

    //Accuracy of Classifier[]: 82.76% Random Forest
    //Accuracy of Classifier[]: 71.55% Random Tree
    //Accuracy of Classifier[]: 64.08% K-nn
    //Accuracy of Classifier[]: 82.18%
    //Accuracy of Classifier[]: 95.69%  

    private void jProgressBar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jProgressBar1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jProgressBar1MouseClicked

    private void jProgressBar1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jProgressBar1KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jProgressBar1KeyReleased

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void btnTextBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTextBrowseActionPerformed
        // TODO add your handling code here:
        if (evt.getSource() == btnTextBrowse) {
            int returnValue = jfc.showOpenDialog(AttritionModel.this);
            if (returnValue == JFileChooser.CANCEL_OPTION) {
                return;
            }

            File f = jfc.getSelectedFile();
            String filename1 = f.getAbsolutePath();
            jTextField3.setText(filename1);
        }
    }//GEN-LAST:event_btnTextBrowseActionPerformed

    private void btnTraingBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTraingBrowseActionPerformed
        // TODO add your handling code here:

        if (evt.getSource() == btnTraingBrowse) {
            int returnValue = jfc.showOpenDialog(AttritionModel.this);
            if (returnValue == JFileChooser.CANCEL_OPTION) {
                return;
            }

            File f = jfc.getSelectedFile();
            String filename = f.getAbsolutePath();
            jTextField2.setText(filename);

        }
    }//GEN-LAST:event_btnTraingBrowseActionPerformed

    private void jProgressBar2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jProgressBar2KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jProgressBar2KeyReleased

    private void jProgressBar2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jProgressBar2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jProgressBar2MouseClicked

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        // TODO add your handling code here:

        long startTime; //start time
        long endTime;   //end time
        double time;    //time difference

        startTime = System.currentTimeMillis();

        try {

            String train = jTextField2.getText();
            String test = jTextField3.getText();
            DataSource source = new DataSource(train);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            //testing Dataset
            DataSource source1 = new DataSource(test);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 1);

            if (!trainDataset.equalHeaders(testDataset)) {
                throw new IllegalArgumentException(
                        "Datasets are not compatible!");
            }

            Remove rm = new Remove();
            rm.setAttributeIndices("1");
            RandomTree nb = new RandomTree();
            FilteredClassifier fc = new FilteredClassifier();
            fc.setFilter(rm);
            fc.setClassifier(nb);
            nb.buildClassifier(trainDataset);

            int total_anamoly = 0;
            int total_instances = 0;
            int ana_np = 0;
            int n_ana_p = 0;
            int ana_p = 0;
            int cp = 0;
            int icp = 0;

            Evaluation eval = new Evaluation(testDataset);
            FastVector predictions = new FastVector();

            eval.crossValidateModel(nb, testDataset, 10, new Random(1));
            jTextArea3.append("\n  \n");
            jTextArea3.append(eval.toSummaryString("\nModel Information\n=================\n", true));
            //jTextArea7.append(eval.toClassDetailsString("\n\n   \n\n"));

            jTextArea3.append("\n  \n");

            predictions.appendElements(eval.predictions());

            for (int i = 0; i < testDataset.numInstances(); i++) {
                double pred = nb.classifyInstance(testDataset.instance(i));
                // String a = "anomaly";
                String actual;
                String predicted;
                actual = testDataset.classAttribute().value((int) testDataset.instance(i).classValue());
                predicted = testDataset.classAttribute().value((int) pred);
                jTextArea4.append("ID: " + i);
                jTextArea4.append(", actual: " + actual);
                jTextArea4.append(", predicted: " + predicted);
                jTextArea4.append("\n-----------------------------------------------------------\n");

                /*  if (actual.equalsIgnoreCase(a)) {
                    total_anamoly++;
                }*/
                if (actual.equalsIgnoreCase(predicted)) {
                    cp++;
                }
                if (!actual.equalsIgnoreCase(predicted)) {
                    icp++;
                }

                /*if (actual.equalsIgnoreCase(a) && predicted.equalsIgnoreCase(a)) {
                    ana_p++;
                }
                if ((!actual.equalsIgnoreCase(a)) && predicted.equalsIgnoreCase(a)) {
                    n_ana_p++;
                }
                if (actual.equalsIgnoreCase(a) && (!predicted.equalsIgnoreCase(a))) {
                    ana_np++;

                }*/
                total_instances++;
            }
            //double accuracy = (cp * 100) / (cp + icp);
            // double recall = ana_p * 100 / (total_anamoly);
            // double precision = ana_p * 100 / (ana_p + n_ana_p);
            jTextArea5.append(" total_instances : " + total_instances);
            jTextArea5.append("\n correct pred : " + cp + "\n incorrect predictions : " + icp);
            //jTextArea7.append("\n precision : " + precision + "\n recall : " + recall);
            // jTextArea7.append("\n accuracy : " + accuracy);

            double accuracy = calculateAccuracy(predictions);

            //double accuracy = (cp * 100) / (cp + icp);
            jTextArea5.append("\n accuracy : " + String.format("%.2f%%", accuracy));

            endTime = System.currentTimeMillis();
            time = (endTime - startTime) / 1000.0;
            System.out.println("\nElapsed Time is:  " + time);
            jTextField7.setText("" + time);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
        // TODO add your handling code here:

        long startTime; //start time
        long endTime;   //end time
        double time;    //time difference

        startTime = System.currentTimeMillis();

        try {

            String train = jTextField2.getText();
            String test = jTextField3.getText();
            DataSource source = new DataSource(train);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            //testing Dataset
            DataSource source1 = new DataSource(test);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 1);

            if (!trainDataset.equalHeaders(testDataset)) {
                throw new IllegalArgumentException(
                        "Datasets are not compatible!");
            }

            Remove rm = new Remove();
            rm.setAttributeIndices("1");
            RandomForest nb = new RandomForest();
            FilteredClassifier fc = new FilteredClassifier();
            fc.setFilter(rm);
            fc.setClassifier(nb);
            nb.buildClassifier(trainDataset);

            int total_anamoly = 0;
            int total_instances = 0;
            int ana_np = 0;
            int n_ana_p = 0;
            int ana_p = 0;
            int cp = 0;
            int icp = 0;

            Evaluation eval = new Evaluation(testDataset);
            FastVector predictions = new FastVector();

            eval.crossValidateModel(nb, testDataset, 10, new Random(1));
            jTextArea3.append("\n  \n");
            jTextArea3.append(eval.toSummaryString("\nModel Information\n=================\n", true));
            //jTextArea7.append(eval.toClassDetailsString("\n\n   \n\n"));

            jTextArea3.append("\n  \n");

            predictions.appendElements(eval.predictions());

            for (int i = 0; i < testDataset.numInstances(); i++) {
                double pred = nb.classifyInstance(testDataset.instance(i));
                // String a = "anomaly";
                String actual;
                String predicted;
                actual = testDataset.classAttribute().value((int) testDataset.instance(i).classValue());
                predicted = testDataset.classAttribute().value((int) pred);
                jTextArea4.append("ID: " + i);
                jTextArea4.append(", actual: " + actual);
                jTextArea4.append(", predicted: " + predicted);
                jTextArea4.append("\n-----------------------------------------------------------\n");

                /*  if (actual.equalsIgnoreCase(a)) {
                    total_anamoly++;
                }*/
                if (actual.equalsIgnoreCase(predicted)) {
                    cp++;
                }
                if (!actual.equalsIgnoreCase(predicted)) {
                    icp++;
                }

                /*if (actual.equalsIgnoreCase(a) && predicted.equalsIgnoreCase(a)) {
                    ana_p++;
                }
                if ((!actual.equalsIgnoreCase(a)) && predicted.equalsIgnoreCase(a)) {
                    n_ana_p++;
                }
                if (actual.equalsIgnoreCase(a) && (!predicted.equalsIgnoreCase(a))) {
                    ana_np++;

                }*/
                total_instances++;
            }
            //double accuracy = (cp * 100) / (cp + icp);
            // double recall = ana_p * 100 / (total_anamoly);
            // double precision = ana_p * 100 / (ana_p + n_ana_p);
            jTextArea5.append(" total_instances : " + total_instances);
            jTextArea5.append("\n correct pred : " + cp + "\n incorrect predictions : " + icp);
            //jTextArea7.append("\n precision : " + precision + "\n recall : " + recall);
            // jTextArea7.append("\n accuracy : " + accuracy);

            double accuracy = calculateAccuracy(predictions);

            //double accuracy = (cp * 100) / (cp + icp);
            jTextArea5.append("\n accuracy : " + String.format("%.2f%%", accuracy));

            endTime = System.currentTimeMillis();
            time = (endTime - startTime) / 1000.0;
            System.out.println("\nElapsed Time is:  " + time);
            jTextField7.setText("" + time);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        jTextArea2.setText("");
        jTextArea3.setText("");
        jProgressBar2.setString("");
        Progresslabel1.setText("");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        double percent = 90;
        ConverterUtils.DataSource source4;
        try {
            source4 = new ConverterUtils.DataSource("Datasets-04-0-1.arff");
            Instances instances = source4.getDataSet();
            // instances.randomize(new java.util.Random(0));
            jTextArea2.append(AttritionModel.TAB + "\nTotal Sample instancias: " + instances.numInstances());
            //jTextArea2.append(AttritionModel.TAB + "\nTrain Set: " + trainDataset);
            // jTextArea2.append(AttritionModel.TAB + "\nTest Set: " + testDataset);

            jTextArea2.append(AttritionModel.TAB + "\n                   \n");

        } catch (Exception ex) {
            Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        jTextArea2.append(AttritionModel.TAB + "\n                   \n");

        // Instances instances = source.getDataSet();
        //instances.randomize(new java.util.Random(0));
        long startTime; //start time
        long endTime;   //end time
        double time;    //time difference

        startTime = System.currentTimeMillis();

        Classifier classifier = null;

        try {// Try and catch block

            String filename = jTextField2.getText();// get the training set from the source
            String filename1 = jTextField3.getText(); // get the testing set from the source
            DataSource source = new DataSource(filename);//get the file name from the datasource
            Instances trainDataset = source.getDataSet();// get the instance data from source file in arff
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);// get the training set

            //testing Dataset
            DataSource source1 = new DataSource(filename1);//get the file name for testing data
            Instances testDataset = source1.getDataSet(); // get the testing set from the source
            testDataset.setClassIndex(testDataset.numAttributes() - 1);//get the testing set

            if (!trainDataset.equalHeaders(testDataset)) {// Test for condiction for Datasets  compatible!
                throw new IllegalArgumentException(
                        "Datasets are not compatible!");
            }

            int numClasses = trainDataset.numClasses();
            // training dataset
            for (int i = 0; i < numClasses; i++) {
                //Get class string value using the class index
                String classValue = trainDataset.classAttribute().value(i);
                jTextArea2.append(AttritionModel.TAB + "\n Value using clase index " + i + "  " + classValue);

            }
            jTextArea2.append("\n                         \n");

            //jTextArea2.append(AttritionModel.TAB + "\nTrain Set: " + trainDataset);
            //jTextArea2.append(AttritionModel.TAB + "\nTest Set: " + testDataset);
            jTextArea2.append("\n                         \n");

            Remove rm = new Remove();
            rm.setAttributeIndices("1");

            String algorithm = (String) (jComboBox1.getSelectedItem());
            String[] algorithms = {algorithm};// declared the length of the model
            for (int w = 0; w < algorithms.length; w++) {// Model selected option for executing the task

                if (algorithms[w].equals("RandomForest")) {
                    classifier = new RandomForest();

                }

                if (algorithms[w].equals("RandomTree")) {
                    classifier = new RandomTree();
                    //RandomTree()
                }

                if (algorithms[w].equals("kNN")) {
                    classifier = new IBk();

                }

                if (algorithms[w].equals("ANN")) {
                    classifier = new MultilayerPerceptron();
                    //MultilayerPerceptron()
                }

                if (algorithms[w].equals("SVM")) {
                    classifier = new SMO();
                    //SMO()
                }

                if (algorithms[w].equals("DT")) {
                    classifier = new J48();
                    //J48()
                }

                /* Classifier Clfs = null;
    try {
    if (algorithm.equals("J48")) {
      Clfs = new J48();
    } else if (algorithm.equals("MLP")) {
      Clfs = new MultilayerPerceptron();
    } else if (algorithm.equals("IB3")) {
      Clfs = new IBk(3);
    } else if (algorithm.equals("RF")) {
      Clfs = new RandomForest();
    } else if (algorithm.equals("NB")) {
      Clfs = new NaiveBayes();
    }
    }catch(Exception e){

            } */
                FilteredClassifier fc = new FilteredClassifier();
                fc.setFilter(rm);
                fc.setClassifier(classifier);
                classifier.buildClassifier(trainDataset);
                //int cp = 0;
                //int icp = 0;
                int total_instances = 0;

                //Evaluation on Training
                Evaluation eva = new Evaluation(trainDataset);
                //Random rand = new Random(1);
                //int folds = 10;
                FastVector prediction = new FastVector();
                eva.evaluateModel(classifier, trainDataset);
                //jTextArea2.setText("\n  \n");

                prediction.appendElements(eva.predictions());

                //System.out.println(""+ann.toString());
                jTextArea2.append(eva.toSummaryString("\nModel Information\n=================\n", true));
                jTextArea2.append(eva.toClassDetailsString("\n\n   \n\n"));
                jTextArea2.append(eva.toMatrixString("\n\n   \n\n"));
                //  jTextArea1.append("\n\nAttribute value       

                jTextArea2.append("\n           \n");

                double accurancy1 = calculateAccuracy(prediction);
                //double accuracy = (cp * 100) / (cp + icp);
                jTextArea2.append("\n accuracy : " + String.format("%.2f%%", accurancy1));
                jTextArea2.append("\n  \n");

                //Evaluation
                Evaluation eval = new Evaluation(testDataset);
                //Random rand = new Random(1);
                //int folds = 10;
                FastVector predictions = new FastVector();
                eval.evaluateModel(classifier, testDataset);
                //jTextArea2.setText("\n  \n");

                predictions.appendElements(eval.predictions());

                //jProgressBar1.setValue(j);
                // btnStartTraining.setEnabled(false);
                jProgressBar2.setVisible(true);
                repaint();

                for (int n = 0; n <= 100; n += 2) {
                    jProgressBar2.setBorderPainted(false);
                    jProgressBar2.setValue(jProgressBar2.getValue() + n);
                    Progresslabel1.setText("Progress is completed... " + jProgressBar2.getString());

                    jProgressBar2.setValue(n);
                    //jProgressBar1.setValue(j);

                    // jProgressBar1.setIndeterminate(false);
                    try {
                        jProgressBar2.paintImmediately(0, 0, 200, 25);//0, 1, 100, 10
                        Thread.sleep(50);
                        jProgressBar2.setStringPainted(false);

                    } catch (Exception e) {
                    }
                }

                // txtTagLog.append());
                //System.out.println(""+ann.toString());
                jTextArea3.append(eval.toSummaryString("\nModel Information\n=================\n", true));
                jTextArea3.append(eval.toClassDetailsString("\n\n   \n\n"));
                jTextArea3.append(eval.toMatrixString("\n\n   \n\n"));
                //  jTextArea1.append("\n\nAttribute value                 Predicted class\n\n");

                // Instances labeled = new Instances(testDataset);
                for (int i = 0; i < testDataset.numInstances(); i++) {//
                    double pred = classifier.classifyInstance(testDataset.instance(i));//
                    //labeled.instance(i).setClassValue(pred);
                    // System.out.println(pred + " -> " + testDataset.classAttribute().value((int) pred));
                    String actual;
                    String predicted;
                    actual = testDataset.classAttribute().value((int) testDataset.instance(i).classValue());
                    predicted = testDataset.classAttribute().value((int) pred);
                    total_instances++;
                }

                int total_anamoly = 0;
                //int total_instances = 0;
                int ana_np = 0;
                int n_ana_p = 0;
                int ana_p = 0;
                int cp = 0;
                int icp = 0;

                predictions.appendElements(eval.predictions());
                for (int i = 0; i < testDataset.numInstances(); i++) {
                    double pred = classifier.classifyInstance(testDataset.instance(i));
                    // String a = "anomaly";
                    String actual;
                    String predicted;
                    actual = testDataset.classAttribute().value((int) testDataset.instance(i).classValue());
                    predicted = testDataset.classAttribute().value((int) pred);
                    jTextArea4.append("ID: " + i);
                    jTextArea4.append(", actual: " + actual);
                    jTextArea4.append(", predicted: " + predicted);
                    jTextArea4.append("\n-----------------------------------------------------------\n");

                    /*  if (actual.equalsIgnoreCase(a)) {
                    total_anamoly++;
                }*/
                    if (actual.equalsIgnoreCase(predicted)) {
                        cp++;
                    }
                    if (!actual.equalsIgnoreCase(predicted)) {
                        icp++;
                    }

                    /*if (actual.equalsIgnoreCase(a) && predicted.equalsIgnoreCase(a)) {
                    ana_p++;
                }
                if ((!actual.equalsIgnoreCase(a)) && predicted.equalsIgnoreCase(a)) {
                    n_ana_p++;
                }
                if (actual.equalsIgnoreCase(a) && (!predicted.equalsIgnoreCase(a))) {
                    ana_np++;

                }*/
                    total_instances++;
                }

                double accurancy = calculateAccuracy(predictions);
                //double accuracy = (cp * 100) / (cp + icp);
                jTextArea3.append("\n accuracy : " + String.format("%.2f%%", accurancy));
                jTextArea3.append("\n  \n");

                endTime = System.currentTimeMillis();
                time = (endTime - startTime) / 1000.0;
                System.out.println("\nElapsed Time is:  " + time);

                jTextField6.setText("" + time);

                JOptionPane.showMessageDialog(null, "Model Performance.");

                // Bar Chart
                String row = "Classification By class";
                DefaultCategoryDataset barChart = new DefaultCategoryDataset();
                /*barChart.setValue(2000,"Contribution Amount ", "January");
                    barChart.setValue(15000,"Contribution Amount ", "February");
                    barChart.setValue(3000, "Contribution Amount ", "March"); */
                final String TPRate3 = "TP Rate";
                // final String TPRate4 = "TP Rate";
                final String FPRate3 = "FP Rate";
                //final String FPRate4 = "FP Rate";
                // final String FPRate5 = "FP Rate"; 
                final String Precision3 = "Precision";
                //final String Precision4 = "Precision";
                final String Recall3 = "Recall";
                //final String Recall4 = "Recall";
                final String FMeasure3 = "F-Measure";
                //final String FMeasure4 = "F-Measure";
                final String MCC3 = "MCC";
                //final String MCC4 = "MCC";
                final String ROCArea3 = "ROC Area";
                //final String ROCArea4 = "ROC Area";
                final String PRCArea3 = "PRC Area";
                // final String PRCArea4 = "PRC Area";

                //class value
                final String Voluntary1 = "Pass";
                final String Involuntary2 = "Probation";
                final String Involuntary3 = "Withdraw";
                final String Involuntary4 = "not_graduate";
                final String Involuntary5 = "Graduate";

                //Step one
                barChart.addValue(eval.truePositiveRate(0), TPRate3, Voluntary1);
                barChart.addValue(eval.truePositiveRate(1), TPRate3, Involuntary2);
                barChart.addValue(eval.truePositiveRate(2), TPRate3, Involuntary3);
                barChart.addValue(eval.truePositiveRate(3), TPRate3, Involuntary4);
                barChart.addValue(eval.truePositiveRate(4), TPRate3, Involuntary5);

                //Step Two
                barChart.addValue(eval.falsePositiveRate(0), FPRate3, Voluntary1);
                barChart.addValue(eval.falsePositiveRate(1), FPRate3, Involuntary2);
                barChart.addValue(eval.falsePositiveRate(2), FPRate3, Involuntary3);
                barChart.addValue(eval.falsePositiveRate(3), FPRate3, Involuntary4);
                barChart.addValue(eval.falsePositiveRate(4), FPRate3, Involuntary5);

                //Step Three
                barChart.addValue(eval.precision(0), Precision3, Voluntary1);
                barChart.addValue(eval.precision(1), Precision3, Involuntary2);
                barChart.addValue(eval.precision(2), Precision3, Involuntary3);
                barChart.addValue(eval.precision(3), Precision3, Involuntary4);
                barChart.addValue(eval.precision(4), Precision3, Involuntary5);

                //Step Three
                barChart.addValue(eval.recall(0), Recall3, Voluntary1);
                barChart.addValue(eval.recall(1), Recall3, Involuntary2);
                barChart.addValue(eval.recall(2), Recall3, Involuntary3);
                barChart.addValue(eval.recall(3), Recall3, Involuntary4);
                barChart.addValue(eval.recall(4), Recall3, Involuntary5);

                //Step Four
                barChart.addValue(eval.fMeasure(0), FMeasure3, Voluntary1);
                barChart.addValue(eval.fMeasure(1), FMeasure3, Involuntary2);
                barChart.addValue(eval.fMeasure(2), FMeasure3, Involuntary3);
                barChart.addValue(eval.fMeasure(3), FMeasure3, Involuntary4);
                barChart.addValue(eval.fMeasure(4), FMeasure3, Involuntary5);

                //Step Five
                /* barChart.addValue(eval.matthewsCorrelationCoefficient(0), MCC3, Voluntary1);
                    barChart.addValue(eval.matthewsCorrelationCoefficient(1), MCC3, Involuntary2);
                    barChart.addValue(eval.matthewsCorrelationCoefficient(2), MCC3, Involuntary3);
                    barChart.addValue(eval.matthewsCorrelationCoefficient(3), MCC3, Involuntary4);
                    barChart.addValue(eval.matthewsCorrelationCoefficient(4), MCC3, Involuntary5);*/
                //Step Six
                barChart.addValue(eval.areaUnderROC(0), ROCArea3, Voluntary1);
                barChart.addValue(eval.areaUnderROC(1), ROCArea3, Involuntary2);
                barChart.addValue(eval.areaUnderROC(2), ROCArea3, Involuntary3);
                barChart.addValue(eval.areaUnderROC(3), ROCArea3, Involuntary4);
                barChart.addValue(eval.areaUnderROC(4), ROCArea3, Involuntary5);

                //Step Seven
                /* barChart.addValue(eval.areaUnderPRC(0) ,PRCArea3, Voluntary1);
                    barChart.addValue(eval.areaUnderPRC(1) ,PRCArea3, Involuntary2);
                    barChart.addValue(eval.areaUnderPRC(2) ,PRCArea3, Involuntary3);
                    barChart.addValue(eval.areaUnderPRC(3) ,PRCArea3, Involuntary4);
                    barChart.addValue(eval.areaUnderPRC(4) ,PRCArea3, Involuntary5);*/
                JFreeChart bar = ChartFactory.createBarChart3D("Model Evaluation ", "Metrics Evaluation ", " ", barChart, PlotOrientation.VERTICAL, rootPaneCheckingEnabled, rootPaneCheckingEnabled, rootPaneCheckingEnabled);
                CategoryPlot bar2 = bar.getCategoryPlot();
                bar2.setRangeGridlinePaint(Color.blue);
                ChartPanel bar3 = new ChartPanel(bar);
                //pnlChart1.removeAll();
                //pnlChart1.add(bar3,BorderLayout.CENTER);
                //pnlChart1.validate();

                // DefaultCategoryDataset pieChart = new  DefaultCategoryDataset();
                DefaultPieDataset pieChart = new DefaultPieDataset();
                //Step one
                // pieChart.setValue("Model Accurancy",accurancy);
                pieChart.setValue("ROC", eval.areaUnderROC(0));
                pieChart.setValue("Recal", eval.recall(1));
                pieChart.setValue("Precision", eval.precision(2));
                JFreeChart piebar = ChartFactory.createPieChart("Model Evaluation  Training Set", pieChart, true, true, false);
                //CategoryPlot piebar2 = bar.getCategoryPlot();
                //piebar2.setRangeGridlinePaint(Color.blue);
                ChartPanel piebar3 = new ChartPanel(piebar);
                //pnlChart2.removeAll();
                //pnlChart2.add(piebar3,BorderLayout.CENTER);
                //pnlChart2.validate();    

            }

        } catch (Exception e) {// End try and catch block
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        //MultilayerPerceptron
        // TODO add your handling code here:
        long startTime; //start time
        long endTime;   //end time
        double time;    //time difference

        startTime = System.currentTimeMillis();

        try {

            String filename = jTextField2.getText();
            String filename1 = jTextField3.getText();
            DataSource source = new DataSource(filename);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            //testing Dataset
            DataSource source1 = new DataSource(filename1);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 1);

            if (!trainDataset.equalHeaders(testDataset)) {
                throw new IllegalArgumentException(
                        "Datasets are not compatible!");
            }

            Remove rm = new Remove();
            rm.setAttributeIndices("1");

            RandomTree objVote = new RandomTree();

            System.out.println("=== Number of Training Instances ===");
            System.out.println(trainDataset.numInstances());
            System.out.println();

            System.out.println("=== Classifier model (full training set) ===");
            System.out.println();
            System.out.println(objVote);

            System.out.println("=== Building Model ===");
            long starttime = System.currentTimeMillis();

            /*Build all the classifiers*/
            objVote.buildClassifier(trainDataset);
            int total_instances = 0;
            /*Use majority vote to predict test set*/
            Evaluation eval = new Evaluation(testDataset);
            eval.evaluateModel(objVote, testDataset);

            FastVector predictions = new FastVector();
            jTextArea1.append("\n  \n");
            predictions.appendElements(eval.predictions());
            jTextArea1.append(eval.toSummaryString("\nModel Information\n=================\n", true));
            jTextArea1.append(eval.toClassDetailsString("\n\n   \n\n"));
            jTextArea1.append(eval.toMatrixString("\n\n   \n\n"));

            jTextArea1.append(("\n\n                          \n\n"));

            jTextArea4.append("\n\nAttribute value                 Predicted class\n\n");

            for (int i = 0; i < testDataset.numInstances(); i++) {
                double pred = objVote.classifyInstance(testDataset.instance(i));

                //   labeled.instance(i).setClassValue(pred);
                //   System.out.println(pred + " -> " + testDataset.classAttribute().value((int) pred));
                String actual;
                String predicted;
                actual = testDataset.classAttribute().value((int) testDataset.instance(i).classValue());
                predicted = testDataset.classAttribute().value((int) pred);

                jTextArea4.append("ID: " + i);
                jTextArea4.append(", actual: " + actual);
                jTextArea4.append(", predicted: " + predicted);
                jTextArea4.append("\n-----------------------------------------------------------\n");

                total_instances++;
            }
            double accuracy = calculateAccuracy(predictions);
            jTextArea1.append("\n accuracy : " + String.format("%.2f%%", accuracy));

            //System.out.println(models.toString());
            //jProgressBar1.setValue(j);
            // btnStartTraining.setEnabled(false);
            jProgressBar1.setVisible(true);
            repaint();
            for (int n = 0; n <= 100; n += 2) {
                jProgressBar1.setBorderPainted(false);
                jProgressBar1.setValue(jProgressBar1.getValue() + n);
                Progresslabel.setText("Progress is completed... " + jProgressBar1.getString());

                jProgressBar1.setValue(n);

                try {
                    jProgressBar1.paintImmediately(0, 0, 200, 25);//0, 1, 100, 10
                    Thread.sleep(50);
                    jProgressBar1.setStringPainted(false);

                } catch (Exception e) {
                }
                //JOptionPane.showMessageDialog(null, "Finished Execution.");

            }

            // Calculate overall accuracy of current classifier on all splits
            endTime = System.currentTimeMillis();// end execution Time
            time = (endTime - startTime) / 1000.0;

            System.out.println("\nElapsed Time is:  " + time);

            jTextField5.setText("" + time);

            // DefaultCategoryDataset pieChart = new  DefaultCategoryDataset();
            DefaultPieDataset pieChart = new DefaultPieDataset();
            //Step one
            // pieChart.setValue("Model Accurancy",accurancy);

            pieChart.setValue("Model Accuracy  :" + String.format("%.2f%%", accuracy), eval.areaUnderROC(0));
            pieChart.setValue("Correctly Classified Instances :" + eval.correct(), eval.recall(0));
            pieChart.setValue("Incorrectly Classified Instances:" + eval.incorrect(), eval.precision(1));

            JFreeChart piebar = ChartFactory.createPieChart("Model Visualization Accuracy For Random Tree", pieChart, true, true, false);
            //CategoryPlot piebar2 = pieChart.getCategoryPlot();
            //piebar2.setRangeGridlinePaint(Color.blue);
            ChartPanel piebar3 = new ChartPanel(piebar);
            ChartPanel piebar4 = new ChartPanel(piebar);
            ChartPanel piebar5 = new ChartPanel(piebar);
            //pnlChart2.removeAll();
            //pnlChart2.add(piebar3, BorderLayout.CENTER);
            //pnlChart2.validate();

            // pnlChart1.removeAll();
            //pnlChart1.add(piebar4, BorderLayout.CENTER);
            //pnlChart1.validate();
            pnlChart.removeAll();
            pnlChart.add(piebar5, BorderLayout.CENTER);
            pnlChart.validate();

            String row3 = "Pass" + "  " + ",Probation, " + "  " + ",Withdraw, " + "  " + ",not_graduate," + "  " + ", Graduate," + "  ";
            row3.getClass().toString();

            DefaultCategoryDataset barChart3 = new DefaultCategoryDataset();
            /*barChart.setValue(2000,"Contribution Amount ", "January");
                barChart.setValue(15000,"Contribution Amount ", "February");
                barChart.setValue(3000, "Contribution Amount ", "March"); */
            final String TPRate8 = "TP Rate";
            final String FPRate8 = "FP Rate";
            final String Precision8 = "Precision";
            final String Recall8 = "Recall";
            final String FMeasure8 = "F-Measure";
            final String MCC8 = "MCC";
            final String ROCArea8 = "ROC Area";
            final String PRCArea8 = "PRC Area";

            //class value
            final String systemdeveloper = "Pass";
            final String BusinessProcessAnalyst = "Probation";
            final String developer = "Withdraw";
            final String testing = "not_graduate";
            final String security = "Graduate";
            //final String cloudcomputing = "Cloud Computing '";

            // System Developer
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            //Add Value

            // barChart3.setValue(eval.truePositiveRate(0),  TPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);

            //Add Value
            //barChart3.addValue(null,FPRate8,  systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);

            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);

            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);

            /*barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);*/
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);

            /*  barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);*/
            //BusinessProcessAnalyst
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);

            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);

            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);

            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);

            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);

            /* barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);*/
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);

            /* barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);*/
            //developer
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);

            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);

            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);

            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);

            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);

            /* barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);*/
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);

            /* barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);*/
            //Testing
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);

            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);

            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);

            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);

            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);

            /* barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);*/
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);

            /*barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);*/
            //Security
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);

            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);

            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);

            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);

            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);

            /*barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);*/
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);

            /*barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);*/
            //Cloud Computing
            /*barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);

            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);

            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);

            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);

            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);

            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);

            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);

            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);*/
            JFreeChart bar9 = ChartFactory.createBarChart3D("Model Evaluation for Random Tree", "Metrics Evaluation ", " ", barChart3, PlotOrientation.VERTICAL, false, true, false);
            CategoryPlot bar10 = bar9.getCategoryPlot();
            bar10.setRangeGridlinePaint(Color.blue);
            ChartPanel bar11 = new ChartPanel(bar9);
            final CategoryPlot p = bar9.getCategoryPlot();

            BarRenderer barRenderer = (BarRenderer) p.getRenderer();
            DecimalFormat decimalFormat = new DecimalFormat("#.##");

            barRenderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{1}", decimalFormat));
            p.setRenderer(barRenderer);
            barRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.TOP_LEFT));
            barRenderer.setItemLabelsVisible(true);
            bar9.getCategoryPlot().setRenderer(barRenderer);

            ValueMarker marker = new ValueMarker(0.70);
            marker.setLabel("Require Level");

            marker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
            marker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            marker.setPaint(Color.BLACK);
            p.addRangeMarker(marker);
            //pnlChart2.removeAll();
            //pnlChart2.add(bar11, BorderLayout.CENTER);
            //pnlChart2.validate();

            // System.out.println(timeSeconds + " seconds");
            /// break;
            //default:
            //System.out.println("Invalid Choice, run the project again");
            // System.exit(0);

            /*Writing Output*/
            System.out.println("=== Time taken to Build Classifiers & Test ===");
            System.out.println();
            // System.out.println(timeSeconds + " seconds");

            System.out.println(eval.toSummaryString("\nResults\n===============\n", true));

            System.out.println("For <Pass " + "F-Measure :" + eval.fMeasure(0) + " Precision :" + eval.precision(0) + " Recall :" + eval.recall(0));

            System.out.println("For >Probation " + "F-Measure :" + eval.fMeasure(1) + " Precision :" + eval.precision(1) + " Recall :" + eval.recall(1));

            System.out.println("For <Withdraw" + "F-Measure :" + eval.fMeasure(2) + " Precision :" + eval.precision(2) + " Recall :" + eval.recall(2));

            System.out.println("For >not_graduate" + "F-Measure :" + eval.fMeasure(3) + " Precision :" + eval.precision(3) + " Recall :" + eval.recall(3));

            System.out.println("For <Graduate" + "F-Measure :" + eval.fMeasure(4) + " Precision :" + eval.precision(4) + " Recall :" + eval.recall(4));

            //System.out.println("For >cloud computing " + "F-Measure :" + eval.fMeasure(5) + " Precision :" + eval.precision(5) + " Recall :" + eval.recall(5));

            /*To print the confusion Matrix*/
            double[][] dConfusionMatrix = eval.confusionMatrix();
            System.out.println("=== Confusion Matrix ===");
            System.out.println("a" + "\t" + "b" + "\t" + "c" + "\t" + "d" + "\t" + "e" + "\t" + "f");
            for (int i = 0; i < 5; i++) {

                for (int j = 0; j < 5; j++) {
                    System.out.print((int) dConfusionMatrix[i][j] + "\t");
                }
                if (i == 0) {
                    System.out.print(" |a =  <=Pass");
                }
                if (i == 1) {
                    System.out.print(" |b =  >Probation");
                }
                if (i == 2) {
                    System.out.print(" |c =  <=Withdraw");
                }
                if (i == 3) {
                    System.out.print(" |d =  >not_graduate");
                }
                if (i == 4) {
                    System.out.print(" |e =  <=Graduate");
                }

                System.out.println();

            }

            JOptionPane.showMessageDialog(null, "Finished Execution.");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }


    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

        //MultilayerPerceptron
        // TODO add your handling code here:
        long startTime; //start time
        long endTime;   //end time
        double time;    //time difference

        startTime = System.currentTimeMillis();

        try {

            String filename = jTextField2.getText();
            String filename1 = jTextField3.getText();
            DataSource source = new DataSource(filename);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            //testing Dataset
            DataSource source1 = new DataSource(filename1);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 1);

            if (!trainDataset.equalHeaders(testDataset)) {
                throw new IllegalArgumentException(
                        "Datasets are not compatible!");
            }

            Remove rm = new Remove();
            rm.setAttributeIndices("1");

            IBk objVote = new IBk();

            System.out.println("=== Number of Training Instances ===");
            System.out.println(trainDataset.numInstances());
            System.out.println();

            System.out.println("=== Classifier model (full training set) ===");
            System.out.println();
            System.out.println(objVote);

            System.out.println("=== Building Model ===");
            long starttime = System.currentTimeMillis();

            /*Build all the classifiers*/
            objVote.buildClassifier(trainDataset);
            int total_instances = 0;
            /*Use majority vote to predict test set*/
            Evaluation eval = new Evaluation(testDataset);
            eval.evaluateModel(objVote, testDataset);

            FastVector predictions = new FastVector();
            jTextArea1.append("\n  \n");
            predictions.appendElements(eval.predictions());
            jTextArea1.append(eval.toSummaryString("\nModel Information\n=================\n", true));
            jTextArea1.append(eval.toClassDetailsString("\n\n   \n\n"));
            jTextArea1.append(eval.toMatrixString("\n\n   \n\n"));

            jTextArea1.append(("\n\n                          \n\n"));

            jTextArea4.append("\n\nAttribute value                 Predicted class\n\n");

            for (int i = 0; i < testDataset.numInstances(); i++) {
                double pred = objVote.classifyInstance(testDataset.instance(i));

                //   labeled.instance(i).setClassValue(pred);
                //   System.out.println(pred + " -> " + testDataset.classAttribute().value((int) pred));
                String actual;
                String predicted;
                actual = testDataset.classAttribute().value((int) testDataset.instance(i).classValue());
                predicted = testDataset.classAttribute().value((int) pred);

                jTextArea4.append("ID: " + i);
                jTextArea4.append(", actual: " + actual);
                jTextArea4.append(", predicted: " + predicted);
                jTextArea4.append("\n-----------------------------------------------------------\n");

                total_instances++;
            }
            double accuracy = calculateAccuracy(predictions);
            jTextArea1.append("\n accuracy : " + String.format("%.2f%%", accuracy));

            //System.out.println(models.toString());
            //jProgressBar1.setValue(j);
            // btnStartTraining.setEnabled(false);
            jProgressBar1.setVisible(true);
            repaint();
            for (int n = 0; n <= 100; n += 2) {
                jProgressBar1.setBorderPainted(false);
                jProgressBar1.setValue(jProgressBar1.getValue() + n);
                Progresslabel.setText("Progress is completed... " + jProgressBar1.getString());

                jProgressBar1.setValue(n);

                try {
                    jProgressBar1.paintImmediately(0, 0, 200, 25);//0, 1, 100, 10
                    Thread.sleep(50);
                    jProgressBar1.setStringPainted(false);

                } catch (Exception e) {
                }
                //JOptionPane.showMessageDialog(null, "Finished Execution.");

            }

            // Calculate overall accuracy of current classifier on all splits
            endTime = System.currentTimeMillis();// end execution Time
            time = (endTime - startTime) / 1000.0;

            System.out.println("\nElapsed Time is:  " + time);

            jTextField5.setText("" + time);

            // DefaultCategoryDataset pieChart = new  DefaultCategoryDataset();
            DefaultPieDataset pieChart = new DefaultPieDataset();
            //Step one
            // pieChart.setValue("Model Accurancy",accurancy);

            pieChart.setValue("Model Accuracy  :" + String.format("%.2f%%", accuracy), eval.areaUnderROC(0));
            pieChart.setValue("Correctly Classified Instances :" + eval.correct(), eval.recall(0));
            pieChart.setValue("Incorrectly Classified Instances:" + eval.incorrect(), eval.precision(1));

            JFreeChart piebar = ChartFactory.createPieChart("Model Visualization Accuracy For kNN", pieChart, true, true, false);
            //CategoryPlot piebar2 = pieChart.getCategoryPlot();
            //piebar2.setRangeGridlinePaint(Color.blue);
            ChartPanel piebar3 = new ChartPanel(piebar);
            ChartPanel piebar4 = new ChartPanel(piebar);
            ChartPanel piebar5 = new ChartPanel(piebar);
            //pnlChart2.removeAll();
            //pnlChart2.add(piebar3, BorderLayout.CENTER);
            //pnlChart2.validate();

            // pnlChart1.removeAll();
            //pnlChart1.add(piebar4, BorderLayout.CENTER);
            //pnlChart1.validate();
            pnlChart.removeAll();
            pnlChart.add(piebar5, BorderLayout.CENTER);
            pnlChart.validate();

            String row3 = "Pass" + "  " + ",Probation, " + "  " + ",Withdraw, " + "  " + ",not_graduate," + "  " + ", Graduate," + "  ";
            row3.getClass().toString();

            DefaultCategoryDataset barChart3 = new DefaultCategoryDataset();
            /*barChart.setValue(2000,"Contribution Amount ", "January");
                barChart.setValue(15000,"Contribution Amount ", "February");
                barChart.setValue(3000, "Contribution Amount ", "March"); */
            final String TPRate8 = "TP Rate";
            final String FPRate8 = "FP Rate";
            final String Precision8 = "Precision";
            final String Recall8 = "Recall";
            final String FMeasure8 = "F-Measure";
            final String MCC8 = "MCC";
            final String ROCArea8 = "ROC Area";
            final String PRCArea8 = "PRC Area";

            //class value
            final String systemdeveloper = "Pass";
            final String BusinessProcessAnalyst = "Probation";
            final String developer = "Withdraw";
            final String testing = "not_graduate";
            final String security = "Graduate";
            //final String cloudcomputing = "Cloud Computing '";

            // System Developer
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            //Add Value

            // barChart3.setValue(eval.truePositiveRate(0),  TPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);

            //Add Value
            //barChart3.addValue(null,FPRate8,  systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);

            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);

            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);

            /*barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);*/
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);

            /* barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);*/
            //BusinessProcessAnalyst
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);

            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);

            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);

            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);

            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);

            /* barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);*/
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);

            /* barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);*/
            //developer
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);

            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);

            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);

            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);

            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);

            /*barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);*/
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);

            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //Testing
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);

            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);

            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);

            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);

            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);

            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);

            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //Security
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);

            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);

            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);

            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);

            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);

            //  barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            // barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            // barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            // barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            // barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);

            // barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            //barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            //barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            //barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            //barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            //barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            //Cloud Computing
            /*barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);

            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);

            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);

            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);

            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);

            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);

            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);

            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);*/
            JFreeChart bar9 = ChartFactory.createBarChart3D("Model Evaluation for KNN", "Metrics Evaluation ", " ", barChart3, PlotOrientation.VERTICAL, false, true, false);
            CategoryPlot bar10 = bar9.getCategoryPlot();
            bar10.setRangeGridlinePaint(Color.blue);
            ChartPanel bar11 = new ChartPanel(bar9);
            final CategoryPlot p = bar9.getCategoryPlot();

            BarRenderer barRenderer = (BarRenderer) p.getRenderer();
            DecimalFormat decimalFormat = new DecimalFormat("#.##");

            barRenderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{1}", decimalFormat));
            p.setRenderer(barRenderer);
            barRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.TOP_LEFT));
            barRenderer.setItemLabelsVisible(true);
            bar9.getCategoryPlot().setRenderer(barRenderer);

            ValueMarker marker = new ValueMarker(0.70);
            marker.setLabel("Require Level");

            marker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
            marker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            marker.setPaint(Color.BLACK);
            p.addRangeMarker(marker);
            //pnlChart2.removeAll();
            //pnlChart2.add(bar11, BorderLayout.CENTER);
            //pnlChart2.validate();

            // System.out.println(timeSeconds + " seconds");
            /// break;
            //default:
            //System.out.println("Invalid Choice, run the project again");
            // System.exit(0);

            /*Writing Output*/
            System.out.println("=== Time taken to Build Classifiers & Test ===");
            System.out.println();
            // System.out.println(timeSeconds + " seconds");

            System.out.println(eval.toSummaryString("\nResults\n===============\n", true));

            System.out.println("For <Pass " + "F-Measure :" + eval.fMeasure(0) + " Precision :" + eval.precision(0) + " Recall :" + eval.recall(0));

            System.out.println("For >Probation " + "F-Measure :" + eval.fMeasure(1) + " Precision :" + eval.precision(1) + " Recall :" + eval.recall(1));

            System.out.println("For <Withdraw" + "F-Measure :" + eval.fMeasure(2) + " Precision :" + eval.precision(2) + " Recall :" + eval.recall(2));

            System.out.println("For >not_graduate" + "F-Measure :" + eval.fMeasure(3) + " Precision :" + eval.precision(3) + " Recall :" + eval.recall(3));

            System.out.println("For <Graduate" + "F-Measure :" + eval.fMeasure(4) + " Precision :" + eval.precision(4) + " Recall :" + eval.recall(4));

            //System.out.println("For >cloud computing " + "F-Measure :" + eval.fMeasure(5) + " Precision :" + eval.precision(5) + " Recall :" + eval.recall(5));

            /*To print the confusion Matrix*/
            double[][] dConfusionMatrix = eval.confusionMatrix();
            System.out.println("=== Confusion Matrix ===");
            System.out.println("a" + "\t" + "b" + "\t" + "c" + "\t" + "d" + "\t" + "e" + "\t" + "f");
            for (int i = 0; i < 5; i++) {

                for (int j = 0; j < 5; j++) {
                    System.out.print((int) dConfusionMatrix[i][j] + "\t");
                }
                if (i == 0) {
                    System.out.print(" |a =  <=Pass");
                }
                if (i == 1) {
                    System.out.print(" |b =  >Probation");
                }
                if (i == 2) {
                    System.out.print(" |c =  <=Withdraw");
                }
                if (i == 3) {
                    System.out.print(" |d =  >not_graduate");
                }
                if (i == 4) {
                    System.out.print(" |e =  <=Graduate");
                }

                System.out.println();

            }

            JOptionPane.showMessageDialog(null, "Finished Execution.");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:

        //MultilayerPerceptron
        // TODO add your handling code here:
        long startTime; //start time
        long endTime;   //end time
        double time;    //time difference

        startTime = System.currentTimeMillis();

        try {

            String filename = jTextField2.getText();
            String filename1 = jTextField3.getText();
            DataSource source = new DataSource(filename);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            //testing Dataset
            DataSource source1 = new DataSource(filename1);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 1);

            if (!trainDataset.equalHeaders(testDataset)) {
                throw new IllegalArgumentException(
                        "Datasets are not compatible!");
            }

            Remove rm = new Remove();
            rm.setAttributeIndices("1");

            MultilayerPerceptron objVote = new MultilayerPerceptron();

            System.out.println("=== Number of Training Instances ===");
            System.out.println(trainDataset.numInstances());
            System.out.println();

            System.out.println("=== Classifier model (full training set) ===");
            System.out.println();
            System.out.println(objVote);

            System.out.println("=== Building Model ===");
            long starttime = System.currentTimeMillis();

            /*Build all the classifiers*/
            objVote.buildClassifier(trainDataset);
            int total_instances = 0;
            /*Use majority vote to predict test set*/
            Evaluation eval = new Evaluation(testDataset);
            eval.evaluateModel(objVote, testDataset);

            FastVector predictions = new FastVector();
            jTextArea1.append("\n  \n");
            predictions.appendElements(eval.predictions());
            jTextArea1.append(eval.toSummaryString("\nModel Information\n=================\n", true));
            jTextArea1.append(eval.toClassDetailsString("\n\n   \n\n"));
            jTextArea1.append(eval.toMatrixString("\n\n   \n\n"));

            jTextArea1.append(("\n\n                          \n\n"));

            jTextArea4.append("\n\nAttribute value                 Predicted class\n\n");

            for (int i = 0; i < testDataset.numInstances(); i++) {
                double pred = objVote.classifyInstance(testDataset.instance(i));

                //   labeled.instance(i).setClassValue(pred);
                //   System.out.println(pred + " -> " + testDataset.classAttribute().value((int) pred));
                String actual;
                String predicted;
                actual = testDataset.classAttribute().value((int) testDataset.instance(i).classValue());
                predicted = testDataset.classAttribute().value((int) pred);

                jTextArea4.append("ID: " + i);
                jTextArea4.append(", actual: " + actual);
                jTextArea4.append(", predicted: " + predicted);
                jTextArea4.append("\n-----------------------------------------------------------\n");

                total_instances++;
            }
            double accuracy = calculateAccuracy(predictions);
            jTextArea1.append("\n accuracy : " + String.format("%.2f%%", accuracy));

            //System.out.println(models.toString());
            //jProgressBar1.setValue(j);
            // btnStartTraining.setEnabled(false);
            jProgressBar1.setVisible(true);
            repaint();
            for (int n = 0; n <= 100; n += 2) {
                jProgressBar1.setBorderPainted(false);
                jProgressBar1.setValue(jProgressBar1.getValue() + n);
                Progresslabel.setText("Progress is completed... " + jProgressBar1.getString());

                jProgressBar1.setValue(n);

                try {
                    jProgressBar1.paintImmediately(0, 0, 200, 25);//0, 1, 100, 10
                    Thread.sleep(50);
                    jProgressBar1.setStringPainted(false);

                } catch (Exception e) {
                }
                //JOptionPane.showMessageDialog(null, "Finished Execution.");

            }

            // Calculate overall accuracy of current classifier on all splits
            endTime = System.currentTimeMillis();// end execution Time
            time = (endTime - startTime) / 1000.0;

            System.out.println("\nElapsed Time is:  " + time);

            jTextField5.setText("" + time);

            // DefaultCategoryDataset pieChart = new  DefaultCategoryDataset();
            DefaultPieDataset pieChart = new DefaultPieDataset();
            //Step one
            // pieChart.setValue("Model Accurancy",accurancy);

            pieChart.setValue("Model Accuracy  :" + String.format("%.2f%%", accuracy), eval.areaUnderROC(0));
            pieChart.setValue("Correctly Classified Instances :" + eval.correct(), eval.recall(0));
            pieChart.setValue("Incorrectly Classified Instances:" + eval.incorrect(), eval.precision(1));

            JFreeChart piebar = ChartFactory.createPieChart("Model Visualization Accuracy For ANN", pieChart, true, true, false);
            //CategoryPlot piebar2 = pieChart.getCategoryPlot();
            //piebar2.setRangeGridlinePaint(Color.blue);
            ChartPanel piebar3 = new ChartPanel(piebar);
            ChartPanel piebar4 = new ChartPanel(piebar);
            ChartPanel piebar5 = new ChartPanel(piebar);
            //pnlChart2.removeAll();
            //pnlChart2.add(piebar3, BorderLayout.CENTER);
            //pnlChart2.validate();

            // pnlChart1.removeAll();
            //pnlChart1.add(piebar4, BorderLayout.CENTER);
            //pnlChart1.validate();
            pnlChart.removeAll();
            pnlChart.add(piebar5, BorderLayout.CENTER);
            pnlChart.validate();

            String row3 = "Pass" + "  " + ",Probation, " + "  " + ",Withdraw, " + "  " + ",not_graduate," + "  " + ", Graduate," + "  ";
            row3.getClass().toString();

            DefaultCategoryDataset barChart3 = new DefaultCategoryDataset();
            /*barChart.setValue(2000,"Contribution Amount ", "January");
                barChart.setValue(15000,"Contribution Amount ", "February");
                barChart.setValue(3000, "Contribution Amount ", "March"); */
            final String TPRate8 = "TP Rate";
            final String FPRate8 = "FP Rate";
            final String Precision8 = "Precision";
            final String Recall8 = "Recall";
            final String FMeasure8 = "F-Measure";
            final String MCC8 = "MCC";
            final String ROCArea8 = "ROC Area";
            final String PRCArea8 = "PRC Area";

            //class value
            final String systemdeveloper = "Pass";
            final String BusinessProcessAnalyst = "Probation";
            final String developer = "Withdraw";
            final String testing = "not_graduate";
            final String security = "Graduate";
            //final String cloudcomputing = "Cloud Computing '";

            // System Developer
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            //Add Value

            // barChart3.setValue(eval.truePositiveRate(0),  TPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);

            //Add Value
            //barChart3.addValue(null,FPRate8,  systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);

            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);

            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);

            // barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);

            //barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            //barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            //barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            //barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            //barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            //barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            //BusinessProcessAnalyst
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);

            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);

            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);

            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);

            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);

            //barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            // barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);

            //barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            //barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            //barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            //barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            //barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            //barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            //developer
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);

            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);

            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);

            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);

            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);

            //barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);

            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            //Testing
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);

            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);

            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);

            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);

            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);

            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            //barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);

            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            //Security
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);

            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);

            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);

            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);

            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);

            /*barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);*/
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);

            /*  barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);*/
            //Cloud Computing
            /*barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);

            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);

            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);

            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);

            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);

            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);

            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);

            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);*/
            JFreeChart bar9 = ChartFactory.createBarChart3D("Model Evaluation for ANN", "Metrics Evaluation ", " ", barChart3, PlotOrientation.VERTICAL, false, true, false);
            CategoryPlot bar10 = bar9.getCategoryPlot();
            bar10.setRangeGridlinePaint(Color.blue);
            ChartPanel bar11 = new ChartPanel(bar9);
            final CategoryPlot p = bar9.getCategoryPlot();

            BarRenderer barRenderer = (BarRenderer) p.getRenderer();
            DecimalFormat decimalFormat = new DecimalFormat("#.##");

            barRenderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{1}", decimalFormat));
            p.setRenderer(barRenderer);
            barRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.TOP_LEFT));
            barRenderer.setItemLabelsVisible(true);
            bar9.getCategoryPlot().setRenderer(barRenderer);

            ValueMarker marker = new ValueMarker(0.70);
            marker.setLabel("Require Level");

            marker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
            marker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            marker.setPaint(Color.BLACK);
            p.addRangeMarker(marker);
            //pnlChart2.removeAll();
            //pnlChart2.add(bar11, BorderLayout.CENTER);
            //pnlChart2.validate();

            // System.out.println(timeSeconds + " seconds");
            /// break;
            //default:
            //System.out.println("Invalid Choice, run the project again");
            // System.exit(0);

            /*Writing Output*/
            System.out.println("=== Time taken to Build Classifiers & Test ===");
            System.out.println();
            // System.out.println(timeSeconds + " seconds");

            System.out.println(eval.toSummaryString("\nResults\n===============\n", true));

            System.out.println("For <Pass " + "F-Measure :" + eval.fMeasure(0) + " Precision :" + eval.precision(0) + " Recall :" + eval.recall(0));

            System.out.println("For >Probation " + "F-Measure :" + eval.fMeasure(1) + " Precision :" + eval.precision(1) + " Recall :" + eval.recall(1));

            System.out.println("For <Withdraw" + "F-Measure :" + eval.fMeasure(2) + " Precision :" + eval.precision(2) + " Recall :" + eval.recall(2));

            System.out.println("For >not_graduate" + "F-Measure :" + eval.fMeasure(3) + " Precision :" + eval.precision(3) + " Recall :" + eval.recall(3));

            System.out.println("For <Graduate" + "F-Measure :" + eval.fMeasure(4) + " Precision :" + eval.precision(4) + " Recall :" + eval.recall(4));

            //System.out.println("For >cloud computing " + "F-Measure :" + eval.fMeasure(5) + " Precision :" + eval.precision(5) + " Recall :" + eval.recall(5));

            /*To print the confusion Matrix*/
            double[][] dConfusionMatrix = eval.confusionMatrix();
            System.out.println("=== Confusion Matrix ===");
            System.out.println("a" + "\t" + "b" + "\t" + "c" + "\t" + "d" + "\t" + "e" + "\t" + "f");
            for (int i = 0; i < 5; i++) {

                for (int j = 0; j < 5; j++) {
                    System.out.print((int) dConfusionMatrix[i][j] + "\t");
                }
                if (i == 0) {
                    System.out.print(" |a =  <=Pass");
                }
                if (i == 1) {
                    System.out.print(" |b =  >Probation");
                }
                if (i == 2) {
                    System.out.print(" |c =  <=Withdraw");
                }
                if (i == 3) {
                    System.out.print(" |d =  >not_graduate");
                }
                if (i == 4) {
                    System.out.print(" |e =  <=Graduate");
                }

                System.out.println();

            }

            JOptionPane.showMessageDialog(null, "Finished Execution.");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:

        //MultilayerPerceptron
        // TODO add your handling code here:
        long startTime; //start time
        long endTime;   //end time
        double time;    //time difference

        startTime = System.currentTimeMillis();

        try {

            String filename = jTextField2.getText();
            String filename1 = jTextField3.getText();
            DataSource source = new DataSource(filename);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            //testing Dataset
            DataSource source1 = new DataSource(filename1);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 1);

            if (!trainDataset.equalHeaders(testDataset)) {
                throw new IllegalArgumentException(
                        "Datasets are not compatible!");
            }

            Remove rm = new Remove();
            rm.setAttributeIndices("1");

            SMO objVote = new SMO();

            System.out.println("=== Number of Training Instances ===");
            System.out.println(trainDataset.numInstances());
            System.out.println();

            System.out.println("=== Classifier model (full training set) ===");
            System.out.println();
            System.out.println(objVote);

            System.out.println("=== Building Model ===");
            long starttime = System.currentTimeMillis();

            /*Build all the classifiers*/
            objVote.buildClassifier(trainDataset);
            int total_instances = 0;
            /*Use majority vote to predict test set*/
            Evaluation eval = new Evaluation(testDataset);
            eval.evaluateModel(objVote, testDataset);

            FastVector predictions = new FastVector();
            jTextArea1.append("\n  \n");
            predictions.appendElements(eval.predictions());
            jTextArea1.append(eval.toSummaryString("\nModel Information\n=================\n", true));
            jTextArea1.append(eval.toClassDetailsString("\n\n   \n\n"));
            jTextArea1.append(eval.toMatrixString("\n\n   \n\n"));

            jTextArea1.append(("\n\n                          \n\n"));

            jTextArea4.append("\n\nAttribute value                 Predicted class\n\n");

            for (int i = 0; i < testDataset.numInstances(); i++) {
                double pred = objVote.classifyInstance(testDataset.instance(i));

                //   labeled.instance(i).setClassValue(pred);
                //   System.out.println(pred + " -> " + testDataset.classAttribute().value((int) pred));
                String actual;
                String predicted;
                actual = testDataset.classAttribute().value((int) testDataset.instance(i).classValue());
                predicted = testDataset.classAttribute().value((int) pred);

                jTextArea4.append("ID: " + i);
                jTextArea4.append(", actual: " + actual);
                jTextArea4.append(", predicted: " + predicted);
                jTextArea4.append("\n-----------------------------------------------------------\n");

                total_instances++;
            }
            double accuracy = calculateAccuracy(predictions);
            jTextArea1.append("\n accuracy : " + String.format("%.2f%%", accuracy));

            //System.out.println(models.toString());
            //jProgressBar1.setValue(j);
            // btnStartTraining.setEnabled(false);
            jProgressBar1.setVisible(true);
            repaint();
            for (int n = 0; n <= 100; n += 2) {
                jProgressBar1.setBorderPainted(false);
                jProgressBar1.setValue(jProgressBar1.getValue() + n);
                Progresslabel.setText("Progress is completed... " + jProgressBar1.getString());

                jProgressBar1.setValue(n);

                try {
                    jProgressBar1.paintImmediately(0, 0, 200, 25);//0, 1, 100, 10
                    Thread.sleep(50);
                    jProgressBar1.setStringPainted(false);

                } catch (Exception e) {
                }
                //JOptionPane.showMessageDialog(null, "Finished Execution.");

            }

            // Calculate overall accuracy of current classifier on all splits
            endTime = System.currentTimeMillis();// end execution Time
            time = (endTime - startTime) / 1000.0;

            System.out.println("\nElapsed Time is:  " + time);

            jTextField5.setText("" + time);

            // DefaultCategoryDataset pieChart = new  DefaultCategoryDataset();
            DefaultPieDataset pieChart = new DefaultPieDataset();
            //Step one
            // pieChart.setValue("Model Accurancy",accurancy);

            pieChart.setValue("Model Accuracy  :" + String.format("%.2f%%", accuracy), eval.areaUnderROC(0));
            pieChart.setValue("Correctly Classified Instances :" + eval.correct(), eval.recall(0));
            pieChart.setValue("Incorrectly Classified Instances:" + eval.incorrect(), eval.precision(1));

            JFreeChart piebar = ChartFactory.createPieChart("Model Visualization Accuracy For SVM", pieChart, true, true, false);
            //CategoryPlot piebar2 = pieChart.getCategoryPlot();
            //piebar2.setRangeGridlinePaint(Color.blue);
            ChartPanel piebar3 = new ChartPanel(piebar);
            ChartPanel piebar4 = new ChartPanel(piebar);
            ChartPanel piebar5 = new ChartPanel(piebar);
            //pnlChart2.removeAll();
            //pnlChart2.add(piebar3, BorderLayout.CENTER);
            //pnlChart2.validate();

            // pnlChart1.removeAll();
            //pnlChart1.add(piebar4, BorderLayout.CENTER);
            //pnlChart1.validate();
            pnlChart.removeAll();
            pnlChart.add(piebar5, BorderLayout.CENTER);
            pnlChart.validate();

            String row3 = "Pass" + "  " + ",Probation, " + "  " + ",Withdraw, " + "  " + ",not_graduate," + "  " + ", Graduate," + "  ";
            row3.getClass().toString();

            DefaultCategoryDataset barChart3 = new DefaultCategoryDataset();
            /*barChart.setValue(2000,"Contribution Amount ", "January");
                barChart.setValue(15000,"Contribution Amount ", "February");
                barChart.setValue(3000, "Contribution Amount ", "March"); */
            final String TPRate8 = "TP Rate";
            final String FPRate8 = "FP Rate";
            final String Precision8 = "Precision";
            final String Recall8 = "Recall";
            final String FMeasure8 = "F-Measure";
            final String MCC8 = "MCC";
            final String ROCArea8 = "ROC Area";
            final String PRCArea8 = "PRC Area";

            //class value
            final String systemdeveloper = "Pass";
            final String BusinessProcessAnalyst = "Probation";
            final String developer = "Withdraw";
            final String testing = "not_graduate";
            final String security = "Graduate";
            //final String cloudcomputing = "Cloud Computing '";

            // System Developer
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            //Add Value

            // barChart3.setValue(eval.truePositiveRate(0),  TPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);

            //Add Value
            //barChart3.addValue(null,FPRate8,  systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);

            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);

            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);

            /*  barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);*/
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);

            /* barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);*/
            //BusinessProcessAnalyst
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);

            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);

            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);

            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);

            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);

            /* barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);*/
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);

            /*barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);*/
            //developer
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);

            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);

            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);

            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);

            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);

            /* barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);*/
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);

            /* barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);*/
            //Testing
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);

            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);

            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);

            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);

            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);

            /* barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);*/
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);

            /*barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);*/
            //Security
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);

            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);

            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);

            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);

            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);

            /* barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);*/
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);

            /* barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);*/
            //Cloud Computing
            /*barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);

            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);

            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);

            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);

            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);

            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);

            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);

            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);*/
            JFreeChart bar9 = ChartFactory.createBarChart3D("Model Evaluation for SVM", "Metrics Evaluation ", " ", barChart3, PlotOrientation.VERTICAL, false, true, false);
            CategoryPlot bar10 = bar9.getCategoryPlot();
            bar10.setRangeGridlinePaint(Color.blue);
            ChartPanel bar11 = new ChartPanel(bar9);
            final CategoryPlot p = bar9.getCategoryPlot();

            BarRenderer barRenderer = (BarRenderer) p.getRenderer();
            DecimalFormat decimalFormat = new DecimalFormat("#.##");

            barRenderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{1}", decimalFormat));
            p.setRenderer(barRenderer);
            barRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.TOP_LEFT));
            barRenderer.setItemLabelsVisible(true);
            bar9.getCategoryPlot().setRenderer(barRenderer);

            ValueMarker marker = new ValueMarker(0.70);
            marker.setLabel("Require Level");

            marker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
            marker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            marker.setPaint(Color.BLACK);
            p.addRangeMarker(marker);
            //pnlChart2.removeAll();
            //pnlChart2.add(bar11, BorderLayout.CENTER);
            //pnlChart2.validate();

            // System.out.println(timeSeconds + " seconds");
            /// break;
            //default:
            //System.out.println("Invalid Choice, run the project again");
            // System.exit(0);

            /*Writing Output*/
            System.out.println("=== Time taken to Build Classifiers & Test ===");
            System.out.println();
            // System.out.println(timeSeconds + " seconds");

            System.out.println(eval.toSummaryString("\nResults\n===============\n", true));

            System.out.println("For <Pass " + "F-Measure :" + eval.fMeasure(0) + " Precision :" + eval.precision(0) + " Recall :" + eval.recall(0));

            System.out.println("For >Probation " + "F-Measure :" + eval.fMeasure(1) + " Precision :" + eval.precision(1) + " Recall :" + eval.recall(1));

            System.out.println("For <Withdraw" + "F-Measure :" + eval.fMeasure(2) + " Precision :" + eval.precision(2) + " Recall :" + eval.recall(2));

            System.out.println("For >not_graduate" + "F-Measure :" + eval.fMeasure(3) + " Precision :" + eval.precision(3) + " Recall :" + eval.recall(3));

            System.out.println("For <Graduate" + "F-Measure :" + eval.fMeasure(4) + " Precision :" + eval.precision(4) + " Recall :" + eval.recall(4));

            //System.out.println("For >cloud computing " + "F-Measure :" + eval.fMeasure(5) + " Precision :" + eval.precision(5) + " Recall :" + eval.recall(5));

            /*To print the confusion Matrix*/
            double[][] dConfusionMatrix = eval.confusionMatrix();
            System.out.println("=== Confusion Matrix ===");
            System.out.println("a" + "\t" + "b" + "\t" + "c" + "\t" + "d" + "\t" + "e" + "\t" + "f");
            for (int i = 0; i < 5; i++) {

                for (int j = 0; j < 5; j++) {
                    System.out.print((int) dConfusionMatrix[i][j] + "\t");
                }
                if (i == 0) {
                    System.out.print(" |a =  <=Pass");
                }
                if (i == 1) {
                    System.out.print(" |b =  >Probation");
                }
                if (i == 2) {
                    System.out.print(" |c =  <=Withdraw");
                }
                if (i == 3) {
                    System.out.print(" |d =  >not_graduate");
                }
                if (i == 4) {
                    System.out.print(" |e =  <=Graduate");
                }

                System.out.println();

            }

            JOptionPane.showMessageDialog(null, "Finished Execution.");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }


    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:

        //MultilayerPerceptron
        // TODO add your handling code here:
        long startTime; //start time
        long endTime;   //end time
        double time;    //time difference

        startTime = System.currentTimeMillis();

        try {

            String filename = jTextField2.getText();
            String filename1 = jTextField3.getText();
            DataSource source = new DataSource(filename);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            //testing Dataset
            DataSource source1 = new DataSource(filename1);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 1);

            if (!trainDataset.equalHeaders(testDataset)) {
                throw new IllegalArgumentException(
                        "Datasets are not compatible!");
            }

            Remove rm = new Remove();
            rm.setAttributeIndices("1");

            RandomTree objVote = new RandomTree();

            System.out.println("=== Number of Training Instances ===");
            System.out.println(trainDataset.numInstances());
            System.out.println();

            System.out.println("=== Classifier model (full training set) ===");
            System.out.println();
            System.out.println(objVote);

            System.out.println("=== Building Model ===");
            long starttime = System.currentTimeMillis();

            /*Build all the classifiers*/
            objVote.buildClassifier(trainDataset);
            int total_instances = 0;
            /*Use majority vote to predict test set*/
            Evaluation eval = new Evaluation(testDataset);
            eval.evaluateModel(objVote, testDataset);

            FastVector predictions = new FastVector();
            jTextArea1.append("\n  \n");
            predictions.appendElements(eval.predictions());
            jTextArea1.append(eval.toSummaryString("\nModel Information\n=================\n", true));
            jTextArea1.append(eval.toClassDetailsString("\n\n   \n\n"));
            jTextArea1.append(eval.toMatrixString("\n\n   \n\n"));

            jTextArea1.append(("\n\n                          \n\n"));

            jTextArea4.append("\n\nAttribute value                 Predicted class\n\n");

            for (int i = 0; i < testDataset.numInstances(); i++) {
                double pred = objVote.classifyInstance(testDataset.instance(i));

                //   labeled.instance(i).setClassValue(pred);
                //   System.out.println(pred + " -> " + testDataset.classAttribute().value((int) pred));
                String actual;
                String predicted;
                actual = testDataset.classAttribute().value((int) testDataset.instance(i).classValue());
                predicted = testDataset.classAttribute().value((int) pred);

                jTextArea4.append("ID: " + i);
                jTextArea4.append(", actual: " + actual);
                jTextArea4.append(", predicted: " + predicted);
                jTextArea4.append("\n-----------------------------------------------------------\n");

                total_instances++;
            }
            double accuracy = calculateAccuracy(predictions);
            jTextArea1.append("\n accuracy : " + String.format("%.2f%%", accuracy));

            //System.out.println(models.toString());
            //jProgressBar1.setValue(j);
            // btnStartTraining.setEnabled(false);
            jProgressBar1.setVisible(true);
            repaint();
            for (int n = 0; n <= 100; n += 2) {
                jProgressBar1.setBorderPainted(false);
                jProgressBar1.setValue(jProgressBar1.getValue() + n);
                Progresslabel.setText("Progress is completed... " + jProgressBar1.getString());

                jProgressBar1.setValue(n);

                try {
                    jProgressBar1.paintImmediately(0, 0, 200, 25);//0, 1, 100, 10
                    Thread.sleep(50);
                    jProgressBar1.setStringPainted(false);

                } catch (Exception e) {
                }
                //JOptionPane.showMessageDialog(null, "Finished Execution.");

            }

            // Calculate overall accuracy of current classifier on all splits
            endTime = System.currentTimeMillis();// end execution Time
            time = (endTime - startTime) / 1000.0;

            System.out.println("\nElapsed Time is:  " + time);

            jTextField5.setText("" + time);

            // DefaultCategoryDataset pieChart = new  DefaultCategoryDataset();
            DefaultPieDataset pieChart = new DefaultPieDataset();
            //Step one
            // pieChart.setValue("Model Accurancy",accurancy);

            pieChart.setValue("Model Accuracy  :" + String.format("%.2f%%", accuracy), eval.areaUnderROC(0));
            pieChart.setValue("Correctly Classified Instances :" + eval.correct(), eval.recall(0));
            pieChart.setValue("Incorrectly Classified Instances:" + eval.incorrect(), eval.precision(1));

            JFreeChart piebar = ChartFactory.createPieChart("Model Visualization Accuracy For Decision Tree", pieChart, true, true, false);
            //CategoryPlot piebar2 = pieChart.getCategoryPlot();
            //piebar2.setRangeGridlinePaint(Color.blue);
            ChartPanel piebar3 = new ChartPanel(piebar);
            ChartPanel piebar4 = new ChartPanel(piebar);
            ChartPanel piebar5 = new ChartPanel(piebar);
            //pnlChart2.removeAll();
            //pnlChart2.add(piebar3, BorderLayout.CENTER);
            //pnlChart2.validate();

            // pnlChart1.removeAll();
            //pnlChart1.add(piebar4, BorderLayout.CENTER);
            //pnlChart1.validate();
            pnlChart.removeAll();
            pnlChart.add(piebar5, BorderLayout.CENTER);
            pnlChart.validate();

            String row3 = "Pass" + "  " + ",Probation, " + "  " + ",Withdraw, " + "  " + ",not_graduate," + "  " + ", Graduate," + "  ";
            row3.getClass().toString();

            DefaultCategoryDataset barChart3 = new DefaultCategoryDataset();
            /*barChart.setValue(2000,"Contribution Amount ", "January");
                barChart.setValue(15000,"Contribution Amount ", "February");
                barChart.setValue(3000, "Contribution Amount ", "March"); */
            final String TPRate8 = "TP Rate";
            final String FPRate8 = "FP Rate";
            final String Precision8 = "Precision";
            final String Recall8 = "Recall";
            final String FMeasure8 = "F-Measure";
            final String MCC8 = "MCC";
            final String ROCArea8 = "ROC Area";
            final String PRCArea8 = "PRC Area";

            //class value
            final String systemdeveloper = "Pass";
            final String BusinessProcessAnalyst = "Probation";
            final String developer = "Withdraw";
            final String testing = "not_graduate";
            final String security = "Graduate";
            //final String cloudcomputing = "Cloud Computing '";

            // System Developer
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            barChart3.addValue(eval.truePositiveRate(0), TPRate8, systemdeveloper);
            //Add Value

            // barChart3.setValue(eval.truePositiveRate(0),  TPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);
            barChart3.addValue(eval.falsePositiveRate(0), FPRate8, systemdeveloper);

            //Add Value
            //barChart3.addValue(null,FPRate8,  systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);
            barChart3.addValue(eval.precision(0), Precision8, systemdeveloper);

            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);
            barChart3.addValue(eval.recall(0), Recall8, systemdeveloper);

            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);
            barChart3.addValue(eval.fMeasure(0), FMeasure8, systemdeveloper);

            /*barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(0), MCC8, systemdeveloper);*/
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderROC(0), ROCArea8, systemdeveloper);

            /* barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);
            barChart3.addValue(eval.areaUnderPRC(0), PRCArea8, systemdeveloper);*/
            //BusinessProcessAnalyst
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.truePositiveRate(1), TPRate8, BusinessProcessAnalyst);

            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);
            barChart3.addValue(eval.falsePositiveRate(1), FPRate8, BusinessProcessAnalyst);

            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);
            barChart3.addValue(eval.precision(1), Precision8, BusinessProcessAnalyst);

            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);
            barChart3.addValue(eval.recall(1), Recall8, BusinessProcessAnalyst);

            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);
            barChart3.addValue(eval.fMeasure(1), FMeasure8, BusinessProcessAnalyst);

            /* barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(1), MCC8, BusinessProcessAnalyst);*/
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderROC(1), ROCArea8, BusinessProcessAnalyst);

            /* barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);
            barChart3.addValue(eval.areaUnderPRC(1), PRCArea8, BusinessProcessAnalyst);*/
            //developer
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);
            barChart3.addValue(eval.truePositiveRate(2), TPRate8, developer);

            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);
            barChart3.addValue(eval.falsePositiveRate(2), FPRate8, developer);

            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);
            barChart3.addValue(eval.precision(2), Precision8, developer);

            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);
            barChart3.addValue(eval.recall(2), Recall8, developer);

            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);
            barChart3.addValue(eval.fMeasure(2), FMeasure8, developer);

            /*barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(2), MCC8, developer);*/
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);
            barChart3.addValue(eval.areaUnderROC(2), ROCArea8, developer);

            /* barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);
            barChart3.addValue(eval.areaUnderPRC(2), PRCArea8, developer);*/
            //Testing
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);
            barChart3.addValue(eval.truePositiveRate(3), TPRate8, testing);

            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);
            barChart3.addValue(eval.falsePositiveRate(3), FPRate8, testing);

            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);
            barChart3.addValue(eval.precision(3), Precision8, testing);

            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);
            barChart3.addValue(eval.recall(3), Recall8, testing);

            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);
            barChart3.addValue(eval.fMeasure(3), FMeasure8, testing);

            /*barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(3), MCC8, testing);*/
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);
            barChart3.addValue(eval.areaUnderROC(3), ROCArea8, testing);

            /*barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);
            barChart3.addValue(eval.areaUnderPRC(3), PRCArea8, testing);*/
            //Security
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);
            barChart3.addValue(eval.truePositiveRate(4), TPRate8, security);

            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);
            barChart3.addValue(eval.falsePositiveRate(4), FPRate8, security);

            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);
            barChart3.addValue(eval.precision(4), Precision8, security);

            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);
            barChart3.addValue(eval.recall(4), Recall8, security);

            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);
            barChart3.addValue(eval.fMeasure(4), FMeasure8, security);

            /* barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(4), MCC8, security);*/
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);
            barChart3.addValue(eval.areaUnderROC(4), ROCArea8, security);

            /* barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);
            barChart3.addValue(eval.areaUnderPRC(4), PRCArea8, security);*/
            //Cloud Computing
            /*barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);
            barChart3.addValue(eval.truePositiveRate(5), TPRate8, cloudcomputing);

            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);
            barChart3.addValue(eval.falsePositiveRate(5), FPRate8, cloudcomputing);

            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);
            barChart3.addValue(eval.precision(5), Precision8, cloudcomputing);

            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);
            barChart3.addValue(eval.recall(5), Recall8, cloudcomputing);

            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);
            barChart3.addValue(eval.fMeasure(5), FMeasure8, cloudcomputing);

            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);
            barChart3.addValue(eval.matthewsCorrelationCoefficient(5), MCC8, cloudcomputing);

            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderROC(5), ROCArea8, cloudcomputing);

            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);
            barChart3.addValue(eval.areaUnderPRC(5), PRCArea8, cloudcomputing);*/
            JFreeChart bar9 = ChartFactory.createBarChart3D("Model Evaluation for Decision Tree", "Metrics Evaluation ", " ", barChart3, PlotOrientation.VERTICAL, false, true, false);
            CategoryPlot bar10 = bar9.getCategoryPlot();
            bar10.setRangeGridlinePaint(Color.blue);
            ChartPanel bar11 = new ChartPanel(bar9);
            final CategoryPlot p = bar9.getCategoryPlot();

            BarRenderer barRenderer = (BarRenderer) p.getRenderer();
            DecimalFormat decimalFormat = new DecimalFormat("#.##");

            barRenderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{1}", decimalFormat));
            p.setRenderer(barRenderer);
            barRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.TOP_LEFT));
            barRenderer.setItemLabelsVisible(true);
            bar9.getCategoryPlot().setRenderer(barRenderer);

            ValueMarker marker = new ValueMarker(0.70);
            marker.setLabel("Require Level");

            marker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
            marker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            marker.setPaint(Color.BLACK);
            p.addRangeMarker(marker);
            //pnlChart2.removeAll();
            //pnlChart2.add(bar11, BorderLayout.CENTER);
            //pnlChart2.validate();

            // System.out.println(timeSeconds + " seconds");
            /// break;
            //default:
            //System.out.println("Invalid Choice, run the project again");
            // System.exit(0);

            /*Writing Output*/
            System.out.println("=== Time taken to Build Classifiers & Test ===");
            System.out.println();
            // System.out.println(timeSeconds + " seconds");

            System.out.println(eval.toSummaryString("\nResults\n===============\n", true));

            System.out.println("For <Pass " + "F-Measure :" + eval.fMeasure(0) + " Precision :" + eval.precision(0) + " Recall :" + eval.recall(0));

            System.out.println("For >Probation " + "F-Measure :" + eval.fMeasure(1) + " Precision :" + eval.precision(1) + " Recall :" + eval.recall(1));

            System.out.println("For <Withdraw" + "F-Measure :" + eval.fMeasure(2) + " Precision :" + eval.precision(2) + " Recall :" + eval.recall(2));

            System.out.println("For >not_graduate" + "F-Measure :" + eval.fMeasure(3) + " Precision :" + eval.precision(3) + " Recall :" + eval.recall(3));

            System.out.println("For <Graduate" + "F-Measure :" + eval.fMeasure(4) + " Precision :" + eval.precision(4) + " Recall :" + eval.recall(4));

            //System.out.println("For >cloud computing " + "F-Measure :" + eval.fMeasure(5) + " Precision :" + eval.precision(5) + " Recall :" + eval.recall(5));

            /*To print the confusion Matrix*/
            double[][] dConfusionMatrix = eval.confusionMatrix();
            System.out.println("=== Confusion Matrix ===");
            System.out.println("a" + "\t" + "b" + "\t" + "c" + "\t" + "d" + "\t" + "e" + "\t" + "f");
            for (int i = 0; i < 5; i++) {

                for (int j = 0; j < 5; j++) {
                    System.out.print((int) dConfusionMatrix[i][j] + "\t");
                }
                if (i == 0) {
                    System.out.print(" |a =  <=Pass");
                }
                if (i == 1) {
                    System.out.print(" |b =  >Probation");
                }
                if (i == 2) {
                    System.out.print(" |c =  <=Withdraw");
                }
                if (i == 3) {
                    System.out.print(" |d =  >not_graduate");
                }
                if (i == 4) {
                    System.out.print(" |e =  <=Graduate");
                }

                System.out.println();

            }

            JOptionPane.showMessageDialog(null, "Finished Execution.");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

    }//GEN-LAST:event_jButton9ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:

        System.exit(0);


    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed

        long startTime; //start time
        long endTime;   //end time
        double time;    //time difference

        startTime = System.currentTimeMillis();

        try {

            String train = jTextField2.getText();
            String test = jTextField3.getText();
            DataSource source = new DataSource(train);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            //testing Dataset
            DataSource source1 = new DataSource(test);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 1);

            if (!trainDataset.equalHeaders(testDataset)) {
                throw new IllegalArgumentException(
                        "Datasets are not compatible!");
            }

            Remove rm = new Remove();
            rm.setAttributeIndices("1");
            IBk nb = new IBk();
            FilteredClassifier fc = new FilteredClassifier();
            fc.setFilter(rm);
            fc.setClassifier(nb);
            nb.buildClassifier(trainDataset);

            int total_anamoly = 0;
            int total_instances = 0;
            int ana_np = 0;
            int n_ana_p = 0;
            int ana_p = 0;
            int cp = 0;
            int icp = 0;

            Evaluation eval = new Evaluation(testDataset);
            FastVector predictions = new FastVector();

            eval.crossValidateModel(nb, testDataset, 10, new Random(1));
            jTextArea3.append("\n  \n");
            jTextArea3.append(eval.toSummaryString("\nModel Information\n=================\n", true));
            //jTextArea7.append(eval.toClassDetailsString("\n\n   \n\n"));

            jTextArea3.append("\n  \n");

            predictions.appendElements(eval.predictions());

            for (int i = 0; i < testDataset.numInstances(); i++) {
                double pred = nb.classifyInstance(testDataset.instance(i));
                // String a = "anomaly";
                String actual;
                String predicted;
                actual = testDataset.classAttribute().value((int) testDataset.instance(i).classValue());
                predicted = testDataset.classAttribute().value((int) pred);
                jTextArea4.append("ID: " + i);
                jTextArea4.append(", actual: " + actual);
                jTextArea4.append(", predicted: " + predicted);
                jTextArea4.append("\n-----------------------------------------------------------\n");

                /*  if (actual.equalsIgnoreCase(a)) {
                    total_anamoly++;
                }*/
                if (actual.equalsIgnoreCase(predicted)) {
                    cp++;
                }
                if (!actual.equalsIgnoreCase(predicted)) {
                    icp++;
                }

                /*if (actual.equalsIgnoreCase(a) && predicted.equalsIgnoreCase(a)) {
                    ana_p++;
                }
                if ((!actual.equalsIgnoreCase(a)) && predicted.equalsIgnoreCase(a)) {
                    n_ana_p++;
                }
                if (actual.equalsIgnoreCase(a) && (!predicted.equalsIgnoreCase(a))) {
                    ana_np++;

                }*/
                total_instances++;
            }
            //double accuracy = (cp * 100) / (cp + icp);
            // double recall = ana_p * 100 / (total_anamoly);
            // double precision = ana_p * 100 / (ana_p + n_ana_p);
            jTextArea5.append(" total_instances : " + total_instances);
            jTextArea5.append("\n correct pred : " + cp + "\n incorrect predictions : " + icp);
            //jTextArea7.append("\n precision : " + precision + "\n recall : " + recall);
            // jTextArea7.append("\n accuracy : " + accuracy);

            double accuracy = calculateAccuracy(predictions);

            //double accuracy = (cp * 100) / (cp + icp);
            jTextArea5.append("\n accuracy : " + String.format("%.2f%%", accuracy));

            endTime = System.currentTimeMillis();
            time = (endTime - startTime) / 1000.0;
            System.out.println("\nElapsed Time is:  " + time);
            jTextField7.setText("" + time);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
        long startTime; //start time
        long endTime;   //end time
        double time;    //time difference

        startTime = System.currentTimeMillis();

        try {

            String train = jTextField2.getText();
            String test = jTextField3.getText();
            DataSource source = new DataSource(train);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            //testing Dataset
            DataSource source1 = new DataSource(test);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 1);

            if (!trainDataset.equalHeaders(testDataset)) {
                throw new IllegalArgumentException(
                        "Datasets are not compatible!");
            }

            Remove rm = new Remove();
            rm.setAttributeIndices("1");
            SMO nb = new SMO();
            FilteredClassifier fc = new FilteredClassifier();
            fc.setFilter(rm);
            fc.setClassifier(nb);
            nb.buildClassifier(trainDataset);

            int total_anamoly = 0;
            int total_instances = 0;
            int ana_np = 0;
            int n_ana_p = 0;
            int ana_p = 0;
            int cp = 0;
            int icp = 0;

            Evaluation eval = new Evaluation(testDataset);
            FastVector predictions = new FastVector();

            eval.crossValidateModel(nb, testDataset, 10, new Random(1));
            jTextArea3.append("\n  \n");
            jTextArea3.append(eval.toSummaryString("\nModel Information\n=================\n", true));
            //jTextArea7.append(eval.toClassDetailsString("\n\n   \n\n"));

            jTextArea3.append("\n  \n");

            predictions.appendElements(eval.predictions());

            for (int i = 0; i < testDataset.numInstances(); i++) {
                double pred = nb.classifyInstance(testDataset.instance(i));
                // String a = "anomaly";
                String actual;
                String predicted;
                actual = testDataset.classAttribute().value((int) testDataset.instance(i).classValue());
                predicted = testDataset.classAttribute().value((int) pred);
                jTextArea4.append("ID: " + i);
                jTextArea4.append(", actual: " + actual);
                jTextArea4.append(", predicted: " + predicted);
                jTextArea4.append("\n-----------------------------------------------------------\n");

                /*  if (actual.equalsIgnoreCase(a)) {
                    total_anamoly++;
                }*/
                if (actual.equalsIgnoreCase(predicted)) {
                    cp++;
                }
                if (!actual.equalsIgnoreCase(predicted)) {
                    icp++;
                }

                /*if (actual.equalsIgnoreCase(a) && predicted.equalsIgnoreCase(a)) {
                    ana_p++;
                }
                if ((!actual.equalsIgnoreCase(a)) && predicted.equalsIgnoreCase(a)) {
                    n_ana_p++;
                }
                if (actual.equalsIgnoreCase(a) && (!predicted.equalsIgnoreCase(a))) {
                    ana_np++;

                }*/
                total_instances++;
            }
            //double accuracy = (cp * 100) / (cp + icp);
            // double recall = ana_p * 100 / (total_anamoly);
            // double precision = ana_p * 100 / (ana_p + n_ana_p);
            jTextArea5.append(" total_instances : " + total_instances);
            jTextArea5.append("\n correct pred : " + cp + "\n incorrect predictions : " + icp);
            //jTextArea7.append("\n precision : " + precision + "\n recall : " + recall);
            // jTextArea7.append("\n accuracy : " + accuracy);

            double accuracy = calculateAccuracy(predictions);

            //double accuracy = (cp * 100) / (cp + icp);
            jTextArea5.append("\n accuracy : " + String.format("%.2f%%", accuracy));

            endTime = System.currentTimeMillis();
            time = (endTime - startTime) / 1000.0;
            System.out.println("\nElapsed Time is:  " + time);
            jTextField7.setText("" + time);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        // TODO add your handling code here:

        long startTime; //start time
        long endTime;   //end time
        double time;    //time difference

        startTime = System.currentTimeMillis();

        try {

            String train = jTextField2.getText();
            String test = jTextField3.getText();
            DataSource source = new DataSource(train);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            //testing Dataset
            DataSource source1 = new DataSource(test);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 1);

            if (!trainDataset.equalHeaders(testDataset)) {
                throw new IllegalArgumentException(
                        "Datasets are not compatible!");
            }

            Remove rm = new Remove();
            rm.setAttributeIndices("1");
            MultilayerPerceptron nb = new MultilayerPerceptron();

            FilteredClassifier fc = new FilteredClassifier();
            fc.setFilter(rm);
            fc.setClassifier(nb);
            nb.buildClassifier(trainDataset);

            int total_anamoly = 0;
            int total_instances = 0;
            int ana_np = 0;
            int n_ana_p = 0;
            int ana_p = 0;
            int cp = 0;
            int icp = 0;

            Evaluation eval = new Evaluation(testDataset);
            FastVector predictions = new FastVector();

            eval.crossValidateModel(nb, testDataset, 10, new Random(1));
            jTextArea3.append("\n  \n");
            jTextArea3.append(eval.toSummaryString("\nModel Information\n=================\n", true));
            //jTextArea7.append(eval.toClassDetailsString("\n\n   \n\n"));

            jTextArea3.append("\n  \n");

            predictions.appendElements(eval.predictions());

            for (int i = 0; i < testDataset.numInstances(); i++) {
                double pred = nb.classifyInstance(testDataset.instance(i));
                // String a = "anomaly";
                String actual;
                String predicted;
                actual = testDataset.classAttribute().value((int) testDataset.instance(i).classValue());
                predicted = testDataset.classAttribute().value((int) pred);
                jTextArea4.append("ID: " + i);
                jTextArea4.append(", actual: " + actual);
                jTextArea4.append(", predicted: " + predicted);
                jTextArea4.append("\n-----------------------------------------------------------\n");

                /*  if (actual.equalsIgnoreCase(a)) {
                    total_anamoly++;
                }*/
                if (actual.equalsIgnoreCase(predicted)) {
                    cp++;
                }
                if (!actual.equalsIgnoreCase(predicted)) {
                    icp++;
                }

                /*if (actual.equalsIgnoreCase(a) && predicted.equalsIgnoreCase(a)) {
                    ana_p++;
                }
                if ((!actual.equalsIgnoreCase(a)) && predicted.equalsIgnoreCase(a)) {
                    n_ana_p++;
                }
                if (actual.equalsIgnoreCase(a) && (!predicted.equalsIgnoreCase(a))) {
                    ana_np++;

                }*/
                total_instances++;
            }
            //double accuracy = (cp * 100) / (cp + icp);
            // double recall = ana_p * 100 / (total_anamoly);
            // double precision = ana_p * 100 / (ana_p + n_ana_p);
            jTextArea5.append(" total_instances : " + total_instances);
            jTextArea5.append("\n correct pred : " + cp + "\n incorrect predictions : " + icp);
            //jTextArea7.append("\n precision : " + precision + "\n recall : " + recall);
            // jTextArea7.append("\n accuracy : " + accuracy);

            double accuracy = calculateAccuracy(predictions);

            //double accuracy = (cp * 100) / (cp + icp);
            jTextArea5.append("\n accuracy : " + String.format("%.2f%%", accuracy));

            endTime = System.currentTimeMillis();
            time = (endTime - startTime) / 1000.0;
            System.out.println("\nElapsed Time is:  " + time);
            jTextField7.setText("" + time);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:
        long startTime; //start time
        long endTime;   //end time
        double time;    //time difference

        startTime = System.currentTimeMillis();

        try {

            String train = jTextField2.getText();
            String test = jTextField3.getText();
            DataSource source = new DataSource(train);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            //testing Dataset
            DataSource source1 = new DataSource(test);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 1);

            if (!trainDataset.equalHeaders(testDataset)) {
                throw new IllegalArgumentException(
                        "Datasets are not compatible!");
            }

            Remove rm = new Remove();
            rm.setAttributeIndices("1");
            J48 nb = new J48();

            FilteredClassifier fc = new FilteredClassifier();
            fc.setFilter(rm);
            fc.setClassifier(nb);
            nb.buildClassifier(trainDataset);

            int total_anamoly = 0;
            int total_instances = 0;
            int ana_np = 0;
            int n_ana_p = 0;
            int ana_p = 0;
            int cp = 0;
            int icp = 0;

            Evaluation eval = new Evaluation(testDataset);
            FastVector predictions = new FastVector();

            eval.crossValidateModel(nb, testDataset, 10, new Random(1));
            jTextArea3.append("\n  \n");
            jTextArea3.append(eval.toSummaryString("\nModel Information\n=================\n", true));
            //jTextArea7.append(eval.toClassDetailsString("\n\n   \n\n"));

            jTextArea3.append("\n  \n");

            predictions.appendElements(eval.predictions());

            for (int i = 0; i < testDataset.numInstances(); i++) {
                double pred = nb.classifyInstance(testDataset.instance(i));
                // String a = "anomaly";
                String actual;
                String predicted;
                actual = testDataset.classAttribute().value((int) testDataset.instance(i).classValue());
                predicted = testDataset.classAttribute().value((int) pred);
                jTextArea4.append("ID: " + i);
                jTextArea4.append(", actual: " + actual);
                jTextArea4.append(", predicted: " + predicted);
                jTextArea4.append("\n-----------------------------------------------------------\n");

                /*  if (actual.equalsIgnoreCase(a)) {
                    total_anamoly++;
                }*/
                if (actual.equalsIgnoreCase(predicted)) {
                    cp++;
                }
                if (!actual.equalsIgnoreCase(predicted)) {
                    icp++;
                }

                /*if (actual.equalsIgnoreCase(a) && predicted.equalsIgnoreCase(a)) {
                    ana_p++;
                }
                if ((!actual.equalsIgnoreCase(a)) && predicted.equalsIgnoreCase(a)) {
                    n_ana_p++;
                }
                if (actual.equalsIgnoreCase(a) && (!predicted.equalsIgnoreCase(a))) {
                    ana_np++;

                }*/
                total_instances++;
            }
            //double accuracy = (cp * 100) / (cp + icp);
            // double recall = ana_p * 100 / (total_anamoly);
            // double precision = ana_p * 100 / (ana_p + n_ana_p);
            jTextArea5.append(" total_instances : " + total_instances);
            jTextArea5.append("\n correct pred : " + cp + "\n incorrect predictions : " + icp);
            //jTextArea7.append("\n precision : " + precision + "\n recall : " + recall);
            // jTextArea7.append("\n accuracy : " + accuracy);

            double accuracy = calculateAccuracy(predictions);

            //double accuracy = (cp * 100) / (cp + icp);
            jTextArea5.append("\n accuracy : " + String.format("%.2f%%", accuracy));

            endTime = System.currentTimeMillis();
            time = (endTime - startTime) / 1000.0;
            System.out.println("\nElapsed Time is:  " + time);
            jTextField7.setText("" + time);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        jTextArea1.setText("");
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:

        // jButton12.setEnabled(false);
        try {

            String filename = jTextField2.getText();
            String filename1 = jTextField3.getText();
            DataSource source = new DataSource(filename);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            IBk nc = new IBk();
            nc.buildClassifier(trainDataset);

            Evaluation eval = new Evaluation(trainDataset);
            //Random rand = new Random(1);
            //int folds = 10;

            DataSource source1 = new DataSource(filename1);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 2);

            eval.crossValidateModel(nc, trainDataset, 10, new java.util.Random(1));
            //eval.crossValidateModel(ann, testDataset, folds, rand);

            System.out.println(eval.toSummaryString("Evaluation results:\n", false));
            //  jTextArea1.append(eval.toSummaryString("Evaluation results:\n", false));

            ThresholdCurve tc = new ThresholdCurve();
            int classIndex = 0; // ROC for the 1st class label
            Instances curve = tc.getCurve(eval.predictions(), classIndex);
            PlotData2D plotdata = new PlotData2D(curve);
            plotdata.setPlotName(curve.relationName());
            plotdata.addInstanceNumberAttribute();
            ThresholdVisualizePanel tvp = new ThresholdVisualizePanel();
            tvp.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(curve), 4) + ")");
            tvp.setName(curve.relationName());
            tvp.addPlot(plotdata);
            final JFrame jf = new JFrame("kNN ROC: " + tvp.getName());
            jf.setSize(500, 400);
            jf.getContentPane().setLayout(new BorderLayout());
            jf.getContentPane().add(tvp, BorderLayout.CENTER);
            jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jf.setVisible(true);

        } catch (IOException ex) {
            Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:

        try {

            String filename = jTextField2.getText();
            String filename1 = jTextField3.getText();
            DataSource source = new DataSource(filename);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            RandomForest nc = new RandomForest();
            nc.buildClassifier(trainDataset);

            Evaluation eval = new Evaluation(trainDataset);
            //Random rand = new Random(1);
            //int folds = 10;

            DataSource source1 = new DataSource(filename1);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 2);

            eval.crossValidateModel(nc, trainDataset, 10, new java.util.Random(1));
            //eval.crossValidateModel(ann, testDataset, folds, rand);

            System.out.println(eval.toSummaryString("Evaluation results:\n", false));
            //  jTextArea1.append(eval.toSummaryString("Evaluation results:\n", false));

            ThresholdCurve tc = new ThresholdCurve();
            int classIndex = 0; // ROC for the 1st class label
            Instances curve = tc.getCurve(eval.predictions(), classIndex);
            PlotData2D plotdata = new PlotData2D(curve);
            plotdata.setPlotName(curve.relationName());
            plotdata.addInstanceNumberAttribute();
            ThresholdVisualizePanel tvp = new ThresholdVisualizePanel();
            tvp.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(curve), 4) + ")");
            tvp.setName(curve.relationName());
            tvp.addPlot(plotdata);
            final JFrame jf = new JFrame("Random Forest ROC: " + tvp.getName());
            jf.setSize(500, 400);
            jf.getContentPane().setLayout(new BorderLayout());
            jf.getContentPane().add(tvp, BorderLayout.CENTER);
            jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jf.setVisible(true);

        } catch (IOException ex) {
            Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:

        try {

            String filename = jTextField2.getText();
            String filename1 = jTextField3.getText();
            DataSource source = new DataSource(filename);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            RandomTree nc = new RandomTree();
            nc.buildClassifier(trainDataset);

            Evaluation eval = new Evaluation(trainDataset);
            //Random rand = new Random(1);
            //int folds = 10;

            DataSource source1 = new DataSource(filename1);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 2);

            eval.crossValidateModel(nc, trainDataset, 10, new java.util.Random(1));
            //eval.crossValidateModel(ann, testDataset, folds, rand);

            System.out.println(eval.toSummaryString("Evaluation results:\n", false));
            //  jTextArea1.append(eval.toSummaryString("Evaluation results:\n", false));

            ThresholdCurve tc = new ThresholdCurve();
            int classIndex = 0; // ROC for the 1st class label
            Instances curve = tc.getCurve(eval.predictions(), classIndex);
            PlotData2D plotdata = new PlotData2D(curve);
            plotdata.setPlotName(curve.relationName());
            plotdata.addInstanceNumberAttribute();
            ThresholdVisualizePanel tvp = new ThresholdVisualizePanel();
            tvp.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(curve), 4) + ")");
            tvp.setName(curve.relationName());
            tvp.addPlot(plotdata);
            final JFrame jf = new JFrame("Random Tree ROC: " + tvp.getName());
            jf.setSize(500, 400);
            jf.getContentPane().setLayout(new BorderLayout());
            jf.getContentPane().add(tvp, BorderLayout.CENTER);
            jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jf.setVisible(true);

        } catch (IOException ex) {
            Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        // TODO add your handling code here:

        try {

            String filename = jTextField2.getText();
            String filename1 = jTextField3.getText();
            DataSource source = new DataSource(filename);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            SMO nc = new SMO();
            nc.buildClassifier(trainDataset);

            Evaluation eval = new Evaluation(trainDataset);
            //Random rand = new Random(1);
            //int folds = 10;

            DataSource source1 = new DataSource(filename1);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 2);

            eval.crossValidateModel(nc, trainDataset, 10, new java.util.Random(1));
            //eval.crossValidateModel(ann, testDataset, folds, rand);

            System.out.println(eval.toSummaryString("Evaluation results:\n", false));
            //  jTextArea1.append(eval.toSummaryString("Evaluation results:\n", false));

            ThresholdCurve tc = new ThresholdCurve();
            int classIndex = 0; // ROC for the 1st class label
            Instances curve = tc.getCurve(eval.predictions(), classIndex);
            PlotData2D plotdata = new PlotData2D(curve);
            plotdata.setPlotName(curve.relationName());
            plotdata.addInstanceNumberAttribute();
            ThresholdVisualizePanel tvp = new ThresholdVisualizePanel();
            tvp.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(curve), 4) + ")");
            tvp.setName(curve.relationName());
            tvp.addPlot(plotdata);
            final JFrame jf = new JFrame("Support Vector Machine ROC: " + tvp.getName());
            jf.setSize(500, 400);
            jf.getContentPane().setLayout(new BorderLayout());
            jf.getContentPane().add(tvp, BorderLayout.CENTER);
            jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jf.setVisible(true);

        } catch (IOException ex) {
            Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:

        try {

            String filename = jTextField2.getText();
            String filename1 = jTextField3.getText();
            DataSource source = new DataSource(filename);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            J48 nc = new J48();
            nc.buildClassifier(trainDataset);

            Evaluation eval = new Evaluation(trainDataset);
            //Random rand = new Random(1);
            //int folds = 10;

            DataSource source1 = new DataSource(filename1);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 2);

            eval.crossValidateModel(nc, trainDataset, 10, new java.util.Random(1));
            //eval.crossValidateModel(ann, testDataset, folds, rand);

            System.out.println(eval.toSummaryString("Evaluation results:\n", false));
            //  jTextArea1.append(eval.toSummaryString("Evaluation results:\n", false));

            ThresholdCurve tc = new ThresholdCurve();
            int classIndex = 0; // ROC for the 1st class label
            Instances curve = tc.getCurve(eval.predictions(), classIndex);
            PlotData2D plotdata = new PlotData2D(curve);
            plotdata.setPlotName(curve.relationName());
            plotdata.addInstanceNumberAttribute();
            ThresholdVisualizePanel tvp = new ThresholdVisualizePanel();
            tvp.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(curve), 4) + ")");
            tvp.setName(curve.relationName());
            tvp.addPlot(plotdata);
            final JFrame jf = new JFrame("Decison Tree ROC: " + tvp.getName());
            jf.setSize(500, 400);
            jf.getContentPane().setLayout(new BorderLayout());
            jf.getContentPane().add(tvp, BorderLayout.CENTER);
            jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jf.setVisible(true);

        } catch (IOException ex) {
            Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        // TODO add your handling code here:

        try {

            String filename = jTextField2.getText();
            String filename1 = jTextField3.getText();
            DataSource source = new DataSource(filename);
            Instances trainDataset = source.getDataSet();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

            MultilayerPerceptron nc = new MultilayerPerceptron();
            nc.buildClassifier(trainDataset);

            Evaluation eval = new Evaluation(trainDataset);
            //Random rand = new Random(1);
            //int folds = 10;

            DataSource source1 = new DataSource(filename1);
            Instances testDataset = source1.getDataSet();
            testDataset.setClassIndex(testDataset.numAttributes() - 2);

            eval.crossValidateModel(nc, trainDataset, 10, new java.util.Random(1));
            //eval.crossValidateModel(ann, testDataset, folds, rand);

            System.out.println(eval.toSummaryString("Evaluation results:\n", false));
            //  jTextArea1.append(eval.toSummaryString("Evaluation results:\n", false));

            ThresholdCurve tc = new ThresholdCurve();
            int classIndex = 0; // ROC for the 1st class label
            Instances curve = tc.getCurve(eval.predictions(), classIndex);
            PlotData2D plotdata = new PlotData2D(curve);
            plotdata.setPlotName(curve.relationName());
            plotdata.addInstanceNumberAttribute();
            ThresholdVisualizePanel tvp = new ThresholdVisualizePanel();
            tvp.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(curve), 4) + ")");
            tvp.setName(curve.relationName());
            tvp.addPlot(plotdata);
            final JFrame jf = new JFrame("ANN ROC: " + tvp.getName());
            jf.setSize(500, 400);
            jf.getContentPane().setLayout(new BorderLayout());
            jf.getContentPane().add(tvp, BorderLayout.CENTER);
            jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jf.setVisible(true);

        } catch (IOException ex) {
            Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jButton22ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:

        new RegisterAccount().setVisible(true);

    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        // TODO add your handling code here:

        int model = 0;

        D = new predictingModel();

        try {

            D.predictionModel(cbModels.getSelectedIndex());
            String r = D.evaluation(jComboBox2.getItemAt(jComboBox2.getSelectedIndex()),
                    jComboBox3.getItemAt(jComboBox3.getSelectedIndex()),
                    jComboBox4.getItemAt(jComboBox4.getSelectedIndex()),
                    jComboBox5.getItemAt(jComboBox5.getSelectedIndex()),
                    jComboBox6.getItemAt(jComboBox6.getSelectedIndex()),
                    jComboBox7.getItemAt(jComboBox7.getSelectedIndex()),
                    jComboBox8.getItemAt(jComboBox8.getSelectedIndex()),
                    jComboBox9.getItemAt(jComboBox9.getSelectedIndex()),
                    jComboBox10.getItemAt(jComboBox10.getSelectedIndex()),
                    jComboBox11.getItemAt(jComboBox11.getSelectedIndex()),
                    jComboBox53.getItemAt(jComboBox53.getSelectedIndex()),
                    //end1 
                    jComboBox12.getItemAt(jComboBox12.getSelectedIndex()),
                    jComboBox13.getItemAt(jComboBox13.getSelectedIndex()),
                    jComboBox14.getItemAt(jComboBox14.getSelectedIndex()),
                    jComboBox16.getItemAt(jComboBox16.getSelectedIndex()),
                    jComboBox17.getItemAt(jComboBox17.getSelectedIndex()),
                    jComboBox18.getItemAt(jComboBox18.getSelectedIndex()),
                    jComboBox19.getItemAt(jComboBox19.getSelectedIndex()),
                    jComboBox20.getItemAt(jComboBox20.getSelectedIndex()),
                    jComboBox21.getItemAt(jComboBox21.getSelectedIndex()),
                    jComboBox22.getItemAt(jComboBox22.getSelectedIndex()),
                    jComboBox23.getItemAt(jComboBox23.getSelectedIndex()),
                    jComboBox24.getItemAt(jComboBox24.getSelectedIndex()),
                    jComboBox25.getItemAt(jComboBox25.getSelectedIndex()),
                    jComboBox26.getItemAt(jComboBox26.getSelectedIndex()),
                    jComboBox27.getItemAt(jComboBox27.getSelectedIndex()),
                    jComboBox28.getItemAt(jComboBox28.getSelectedIndex()),
                    jComboBox29.getItemAt(jComboBox29.getSelectedIndex()),
                    jComboBox30.getItemAt(jComboBox30.getSelectedIndex()),
                    jComboBox31.getItemAt(jComboBox31.getSelectedIndex()),
                    jComboBox32.getItemAt(jComboBox32.getSelectedIndex()),
                    jComboBox33.getItemAt(jComboBox33.getSelectedIndex()),
                    jComboBox34.getItemAt(jComboBox34.getSelectedIndex()),
                    jComboBox35.getItemAt(jComboBox35.getSelectedIndex()),
                    jComboBox36.getItemAt(jComboBox36.getSelectedIndex()),
                    jComboBox37.getItemAt(jComboBox37.getSelectedIndex()),
                    //end2 

                    jComboBox38.getItemAt(jComboBox38.getSelectedIndex()),
                    jComboBox62.getItemAt(jComboBox62.getSelectedIndex()),
                    jComboBox39.getItemAt(jComboBox39.getSelectedIndex()),
                    jComboBox40.getItemAt(jComboBox40.getSelectedIndex()),
                    jComboBox41.getItemAt(jComboBox41.getSelectedIndex()),
                    jComboBox42.getItemAt(jComboBox42.getSelectedIndex()),
                    jComboBox43.getItemAt(jComboBox43.getSelectedIndex()),
                    jComboBox44.getItemAt(jComboBox44.getSelectedIndex()),
                    jComboBox45.getItemAt(jComboBox45.getSelectedIndex()),
                    jComboBox48.getItemAt(jComboBox48.getSelectedIndex()),
                    jComboBox49.getItemAt(jComboBox49.getSelectedIndex()),
                    jComboBox50.getItemAt(jComboBox50.getSelectedIndex()),
                    jComboBox51.getItemAt(jComboBox51.getSelectedIndex()),
                    jComboBox52.getItemAt(jComboBox52.getSelectedIndex()),
                    jComboBox75.getItemAt(jComboBox75.getSelectedIndex()),
                    jComboBox76.getItemAt(jComboBox76.getSelectedIndex()),
                    jComboBox78.getItemAt(jComboBox78.getSelectedIndex()),
                    jComboBox79.getItemAt(jComboBox79.getSelectedIndex()),
                    jComboBox80.getItemAt(jComboBox80.getSelectedIndex()),
                    jComboBox81.getItemAt(jComboBox81.getSelectedIndex()),
                    jComboBox82.getItemAt(jComboBox82.getSelectedIndex()),
                    jComboBox71.getItemAt(jComboBox71.getSelectedIndex()),
                    jComboBox72.getItemAt(jComboBox72.getSelectedIndex()),
                    jComboBox73.getItemAt(jComboBox73.getSelectedIndex()),
                    jComboBox74.getItemAt(jComboBox74.getSelectedIndex()),
                    jComboBox83.getItemAt(jComboBox83.getSelectedIndex()),
                    cbModels.getSelectedIndex());
                   //Pass,Probation,Withdraw,not_graduate,Graduate
            if (r.equals("Pass")) {

                
                ImageIcon image = new ImageIcon(new ImageIcon("img/".toUpperCase() + "p-01.jpg").getImage().getScaledInstance(330, 230, Image.SCALE_SMOOTH));
                label26.setIcon(image);
                jTextField8.setText("Your Attrition Prediction  is:  " + "Pass");
                
                

            } else if (r.equals("Probation")) {
                ImageIcon image = new ImageIcon(new ImageIcon("img/".toUpperCase() + "pt-01.jpg").getImage().getScaledInstance(330, 230, Image.SCALE_SMOOTH));
                label26.setIcon(image);

                jTextField8.setText("Your Attrition Prediction is Under:  " + "Probation");
                
                
                

            }else if (r.equals("Withdraw")) {
                ImageIcon image = new ImageIcon(new ImageIcon("img/".toUpperCase() + "w-01.jpg").getImage().getScaledInstance(330, 230, Image.SCALE_SMOOTH));
                label26.setIcon(image);

                jTextField8.setText("Your Attrition Prediction is:  " + "Withdraw");
                
                
            }else if (r.equals("not_graduate")) {
                ImageIcon image = new ImageIcon(new ImageIcon("img/".toUpperCase() + "nt-02.jpeg").getImage().getScaledInstance(330, 230, Image.SCALE_SMOOTH));
                label26.setIcon(image);

                jTextField8.setText("Your Attrition Prediction is:  " + "not_graduate");
               
            }else if (r.equals("Graduate")) {
                ImageIcon image = new ImageIcon(new ImageIcon("img/".toUpperCase() + "gd-04.jpeg").getImage().getScaledInstance(330, 230, Image.SCALE_SMOOTH));
                label26.setIcon(image);

                jTextField8.setText("Your Attrition Prediction is:  " + "Graduate");
            }
      
            
            
            try {

                double percent = 80;
                ConverterUtils.DataSource source = new ConverterUtils.DataSource("Datasets-04-0-1.arff");
                Instances instances = source.getDataSet();
                instances.randomize(new java.util.Random(0));

                // 80% Train Set
                int trainSize = (int) Math.round(instances.numInstances() * percent / 100);
                // 20% : Test set
                int testSize = instances.numInstances() - trainSize;
                Instances train = new Instances(instances, 0, trainSize);
                Instances test = new Instances(instances, trainSize, testSize);

                jTextArea1.append(predictingModel.TAB + "\nTotal Sample instancias: " + instances.numInstances());
                jTextArea1.append(predictingModel.TAB + "\nTrain Set: " + trainSize);
                jTextArea1.append(predictingModel.TAB + "\nTest Set: " + testSize);

                //
                //
                train.setClassIndex(train.numAttributes() - 1);
                //Obtiene el numero de clases 
                int numClasses = train.numClasses();
                // training dataset
                for (int i = 0; i < numClasses; i++) {
                    //Get class string value using the class index
                    String classValue = train.classAttribute().value(i);
                    jTextArea1.append(predictingModel.TAB + "\n Value using clase index " + i + "  " + classValue);
                }

                // SMO tr = new SMO();
               SMO ml = new SMO();
                //  J48 jc = new J48();
                //Crear modelo the  clasification  80 % 
                // NaiveBayes nb = new NaiveBayes();
                // J48 jc = new J48();

                ml.buildClassifier(train);
                // jc.buildClassifier(train);//train BD
                //System.out.println(HealthyDiet.TAB + "Testing process...");
                System.out.println(predictingModel.SEPARATOR);
                test.setClassIndex(test.numAttributes() - 1);
                //System.out.println(HealthyDiet.TAB + "Actual Class" + HealthyDiet.TAB + "NB Predicted");
                jTextArea1.append(predictingModel.TAB + "\n===============================\n");
                jTextArea1.append(predictingModel.TAB + "Actual Class" + predictingModel.TAB + " Predicted Class" + "\n                       \n");

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
                    String algorithm = (String) (cbModels.getSelectedItem());
                    String[] algorithms = {algorithm};
                    for (int w = 0; w < algorithms.length; w++) {
                        if (algorithms[w].equals("SMO")) {
                            predNB = ml.classifyInstance(newInst);
                        }

                        if (algorithms[w].equals("J48")) {
                            //    predNB = jc.classifyInstance(newInst);

                        }

                        String predString = test.classAttribute().value((int) predNB);
                        jTextArea1.append(predictingModel.TAB + actual + predictingModel.TAB + predString + "\n                       \n");
                        if ((int) actualClass == (int) predNB) {
                            correctSamples++;
                        }
                    }
                }

                double accuracy = correctSamples / (testSize * 1.0);
                if (model == 0) {
                    //System.out.println("Accuracy: " + accuracy * 100 + " %  Application model Naive Bayes");

                    jTextArea1.append("\nModel Accuracy: " + String.format("%.2f%%", accuracy * 100));
                    //jTextArea1.append("\n==========\n");
                } //jTextArea3.append("\n accuracy : " + String.format("%.2f%%", accuracy));
                else {
                    //System.out.println("Accuracy: " + accuracy * 100 + " %  Application model J48");

                    jTextArea1.append("Accuracy: " + accuracy * 100 + " %  Application model J48" + "\n                       \n");
                }

                //Evaluation
                Evaluation eval = new Evaluation(test);

                //eval.crossValidateModel(nb, test, 10, new java.util.Random(1));
                eval.evaluateModel(ml, test);

                jTextArea2.append("\n  \n");
                jTextArea2.append(eval.toSummaryString("\nModel Information\n=================\n", true));
                jTextArea2.append(eval.toClassDetailsString("\n\n   \n\n"));
                jTextArea2.append(eval.toMatrixString("\n\n   \n\n"));

                for (int i = 0; i < test.numInstances(); i++) {
                    double actualClass = test.instance(i).classValue();
                    //String actual = testDataset.classAttribute().value((int) actualClass);
                    Instance newInst = test.instance(i);
                    double predNB = ml.classifyInstance(newInst);
                    //String predString = testDataset.classAttribute().value((int) predNB);

                    // System.out.println(test.instance(i) + " =====> " + testDataset.classAttribute().value((int) predNB));
                    jTextArea3.append(test.instance(i) + " =====> " + test.classAttribute().value((int) predNB) + "\n                       \n");

                }

                // Coding  
            } catch (Exception e) {
                Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, e);

            }

        } catch (Exception ex) {
            Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jButton23ActionPerformed

    private void cbModelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbModelsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbModelsActionPerformed

    public String[] getPopup(String pais) {
        String[] ciudades = new String[35]; //Highest Array
        if (pais.equalsIgnoreCase("100")) {
            ciudades[0] = "S10C";
            ciudades[1] = "S10 LTE";
            ciudades[2] = "S11 LTE";
            ciudades[3] = "A1";
            ciudades[4] = "A1 LTE";
            ciudades[5] = "F9";
            ciudades[6] = "F205 PRO";
            ciudades[7] = "F9 PLUS";
            ciudades[8] = "S6s";
            ciudades[9] = "S6 PRO";
            ciudades[10] = "F103 PRO";
            ciudades[11] = "F205";
            ciudades[12] = "F10";
            ciudades[13] = "X1";
            ciudades[14] = "X1s";
            ciudades[15] = "M7 Power";
            ciudades[16] = "P5 Mini";
            ciudades[17] = "f10 plus";
            ciudades[18] = "A1 PLUS";
            ciudades[19] = "M5 PLUS";
            ciudades[20] = "M6 PLUS";
            ciudades[21] = "M5 Mini";
            ciudades[22] = "F103";
            ciudades[23] = "MARATHON M5 LITE";
            ciudades[24] = "MARATHON M5 ENJOY";
            ciudades[25] = "F100";
            ciudades[26] = "M5 MARATHON PLUS";
            ciudades[27] = "P7 Max";
        }
        if (pais.equalsIgnoreCase("200")) {
            ciudades[0] = "S5";
            ciudades[1] = "S5 lite";
            ciudades[2] = "Hot 8";
            ciudades[3] = "Hot 7";
            ciudades[4] = "Hot 7 pro";
            ciudades[5] = "Note 6";
            ciudades[6] = "Hot 4 lite";
            ciudades[7] = "Hot 4 pro";
            ciudades[8] = "Note 4";
            ciudades[9] = "Hot 6";
            ciudades[10] = "Hot 6 pro";
            ciudades[11] = "Zero 5";
            ciudades[12] = "Zero 5 pro";
            ciudades[13] = "Hot S3x";
            ciudades[14] = "Hot S3";
            ciudades[15] = "S4";
            ciudades[16] = "Hot S4 pro";
            ciudades[17] = "Hot 3";
            ciudades[18] = "Zero 3";
            ciudades[19] = "Hot 9";
            ciudades[20] = "Smart 4";
            ciudades[21] = "Hot 8 pro";
            ciudades[22] = "Smart 2";
            ciudades[23] = "Smart 2 pro";
            ciudades[24] = "Smart";
            ciudades[25] = "Smart 3";
            ciudades[26] = "Smart 3 plus";
            ciudades[27] = "Note 10 pro";
            ciudades[28] = "Hot 10i";
            ciudades[29] = "Hot 10T";
            ciudades[30] = "Zero 8";
            ciudades[31] = "Note 7";
            ciudades[32] = "Note 7 lite";
            ciudades[33] = "Smart HD 2021";
        }
        if (pais.equalsIgnoreCase("300")) {
            ciudades[0] = "A32 F";
            ciudades[1] = "A45";
            ciudades[2] = "A62";
            ciudades[3] = "P32";
            ciudades[4] = "P33";
            ciudades[5] = "P33 plus";
            ciudades[6] = "P13 plus";
            ciudades[7] = "P13";
            ciudades[8] = "P31";
            ciudades[9] = "P12";
            ciudades[10] = "P51";
            ciudades[11] = "1556 PLUS";
            ciudades[12] = "1516 PLUS";
            ciudades[13] = "S32";
            ciudades[14] = "S32 LTE ";
            ciudades[15] = "S12";
            ciudades[16] = "A51";
            ciudades[17] = "A16";
            ciudades[18] = "A20 ";
            ciudades[19] = "A22";
            ciudades[20] = "A22 PRO";
            ciudades[21] = "A44";
            ciudades[22] = "A44 PRO";
            ciudades[23] = "A44 AIR";
            ciudades[24] = "A44 POWER";
            ciudades[25] = "A46";
            ciudades[26] = "A31";
            ciudades[27] = "S31";
            ciudades[28] = "S21";
        }
        if (pais.equalsIgnoreCase("TECNO")) {
            ciudades[0] = "Camon 16";
            ciudades[1] = "Camon 16 premier";
            ciudades[2] = "Spark 6";
            ciudades[3] = "Spark 6 air";
            ciudades[4] = "Spark 6 Go";
            ciudades[5] = "Spark Go 2020";
            ciudades[6] = "Spark 7";
            ciudades[7] = "Spark 7 pro";
            ciudades[8] = "Spark 7p";
            ciudades[9] = "Camon 17";
            ciudades[10] = "Camon 17p";
            ciudades[11] = "Camon 17 pro";
            ciudades[12] = "Camon 16s";
            ciudades[13] = "Camon 16 pro";
            ciudades[14] = "Pova";
            ciudades[15] = "Pova 2";
            ciudades[16] = "Phantom x";
            ciudades[17] = "POUVOIR 1";
            ciudades[18] = "POUVOIR 2";
            ciudades[19] = "POUVOIR 3";
            ciudades[20] = "POUVOIR 3 AIR";
            ciudades[21] = "POUVOIR 3 PLUS";
            ciudades[22] = "Camon 11";
            ciudades[23] = "Camon 11 pro";
            ciudades[24] = "Camon 12";
            ciudades[25] = "Camon 12 pro";
            ciudades[26] = "Spark 3";
            ciudades[27] = "Spark 3 pro";
            ciudades[28] = "WX3";
            ciudades[30] = "WX3 pro";
            ciudades[31] = "K7";
            ciudades[32] = "KA7";
            ciudades[33] = "L9";
            ciudades[34] = "L9 plus";
        }

        return ciudades;
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
            java.util.logging.Logger.getLogger(AttritionModel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AttritionModel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AttritionModel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AttritionModel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                try {

                    new AttritionModel().setVisible(true);

                } catch (Exception ex) {
                    Logger.getLogger(AttritionModel.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Date_txt;
    private javax.swing.JLabel Progresslabel;
    private javax.swing.JLabel Progresslabel1;
    private javax.swing.JLabel Time_txt;
    private javax.swing.JButton btnTextBrowse;
    private javax.swing.JButton btnTraingBrowse;
    private javax.swing.JComboBox<String> cbModels;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox10;
    private javax.swing.JComboBox<String> jComboBox11;
    private javax.swing.JComboBox<String> jComboBox12;
    private javax.swing.JComboBox<String> jComboBox13;
    private javax.swing.JComboBox<String> jComboBox14;
    private javax.swing.JComboBox<String> jComboBox15;
    private javax.swing.JComboBox<String> jComboBox16;
    private javax.swing.JComboBox<String> jComboBox17;
    private javax.swing.JComboBox<String> jComboBox18;
    private javax.swing.JComboBox<String> jComboBox19;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox20;
    private javax.swing.JComboBox<String> jComboBox21;
    private javax.swing.JComboBox<String> jComboBox22;
    private javax.swing.JComboBox<String> jComboBox23;
    private javax.swing.JComboBox<String> jComboBox24;
    private javax.swing.JComboBox<String> jComboBox25;
    private javax.swing.JComboBox<String> jComboBox26;
    private javax.swing.JComboBox<String> jComboBox27;
    private javax.swing.JComboBox<String> jComboBox28;
    private javax.swing.JComboBox<String> jComboBox29;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox30;
    private javax.swing.JComboBox<String> jComboBox31;
    private javax.swing.JComboBox<String> jComboBox32;
    private javax.swing.JComboBox<String> jComboBox33;
    private javax.swing.JComboBox<String> jComboBox34;
    private javax.swing.JComboBox<String> jComboBox35;
    private javax.swing.JComboBox<String> jComboBox36;
    private javax.swing.JComboBox<String> jComboBox37;
    private javax.swing.JComboBox<String> jComboBox38;
    private javax.swing.JComboBox<String> jComboBox39;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox40;
    private javax.swing.JComboBox<String> jComboBox41;
    private javax.swing.JComboBox<String> jComboBox42;
    private javax.swing.JComboBox<String> jComboBox43;
    private javax.swing.JComboBox<String> jComboBox44;
    private javax.swing.JComboBox<String> jComboBox45;
    private javax.swing.JComboBox<String> jComboBox46;
    private javax.swing.JComboBox<String> jComboBox47;
    private javax.swing.JComboBox<String> jComboBox48;
    private javax.swing.JComboBox<String> jComboBox49;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox50;
    private javax.swing.JComboBox<String> jComboBox51;
    private javax.swing.JComboBox<String> jComboBox52;
    private javax.swing.JComboBox<String> jComboBox53;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox62;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JComboBox<String> jComboBox71;
    private javax.swing.JComboBox<String> jComboBox72;
    private javax.swing.JComboBox<String> jComboBox73;
    private javax.swing.JComboBox<String> jComboBox74;
    private javax.swing.JComboBox<String> jComboBox75;
    private javax.swing.JComboBox<String> jComboBox76;
    private javax.swing.JComboBox<String> jComboBox78;
    private javax.swing.JComboBox<String> jComboBox79;
    private javax.swing.JComboBox<String> jComboBox8;
    private javax.swing.JComboBox<String> jComboBox80;
    private javax.swing.JComboBox<String> jComboBox81;
    private javax.swing.JComboBox<String> jComboBox82;
    private javax.swing.JComboBox<String> jComboBox83;
    private javax.swing.JComboBox<String> jComboBox86;
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
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JLabel label26;
    private javax.swing.JPanel pnlChart;
    // End of variables declaration//GEN-END:variables
}
