/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BigDataAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.clusterers.SimpleKMeans;
import weka.clusterers.HierarchicalClusterer;

public class Performance extends javax.swing.JFrame {
 private static Logger LOGGER = Logger.getLogger("BigDataAnalysis");
    private FilteredClassifier classifier;
    //declare train and test data Instances
    private Instances trainData;
    //declare attributes of Instance
    private ArrayList< Attribute> wekaAttributes;

    /**
     * Creates new form Performance
     */
    public Performance() {
        initComponents();
        
        
    String curDir = System.getProperty("user.dir");
        TRAIN_DATA.setText(curDir + File.separator + "dataset" + File.separator + "train.txt");// TRAIN_DATA
        TRAIN_ARFF_ARFF.setText(curDir + File.separator + "dataset" + File.separator + "train.arff");//  TRAIN_ARFF_ARFF   
        TEST_DATA.setText(curDir + File.separator + "dataset" + File.separator + "test.txt");//TEST_DATA
        TEST_DATA_ARFF.setText(curDir + File.separator + "dataset" + File.separator + "test.arff");// TEST_DATA_ARFF

        classifier = new FilteredClassifier();
        //set Multinomial NaiveBayes as arbitrary classifier
        classifier.setClassifier(new NaiveBayes());
        //classifier.setClassifier(new SMO());

        // Declare text attribute to hold the message
        Attribute attributeText = new Attribute("text", (List< String>) null);
        // Declare the label attribute along with its values
        ArrayList< String> classAttributeValues = new ArrayList<>();
        classAttributeValues.add("legalMessages");
        classAttributeValues.add("illegalMessages");
        Attribute classAttribute = new Attribute("label", classAttributeValues);
        // Declare the feature vector
        wekaAttributes = new ArrayList<>();
        wekaAttributes.add(classAttribute);
        wekaAttributes.add(attributeText);

    }

    public void transform() {

        try {
            //trainData = loadRawDataset(TRAIN_DATA);
            //saveArff(trainData, TRAIN_ARFF_ARFF);

            String filename = TRAIN_DATA.getText();
            String filename1 = TRAIN_ARFF_ARFF.getText();
            trainData = loadRawDataset(filename);
            saveArff(trainData, filename1);



            StringToWordVector filter = new StringToWordVector();
            filter.setAttributeIndices("last");


            NGramTokenizer tokenizer = new NGramTokenizer();
            tokenizer.setNGramMinSize(1);
            tokenizer.setNGramMaxSize(1);
            //use word delimeter
            tokenizer.setDelimiters("\\W");
            filter.setTokenizer(tokenizer);

            //convert tokens to lowercase
            filter.setLowerCaseTokens(true);

            //add filter to classifier
            classifier.setFilter(filter);
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
        }


    }

    /**
     * build the classifier with the Training data
     */
    public void fit() {
        try {
            classifier.buildClassifier(trainData);
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
        }
    }

    /**
     * classify a new message into spam or ham.
     *
     * @param message to be classified.
     * @return a class label (spam or ham )
     */
    public String predict(String text) {
        try {
            // create new Instance for prediction.
            DenseInstance newinstance = new DenseInstance(2);

            //weka demand a dataset to be set to new Instance
            Instances newDataset = new Instances("predictiondata", wekaAttributes, 1);
            newDataset.setClassIndex(0);

            newinstance.setDataset(newDataset);

            // text attribute value set to value to be predicted
            newinstance.setValue(wekaAttributes.get(1), text);

            // predict most likely class for the instance
            double pred = classifier.classifyInstance(newinstance);

            // return original label
            return newDataset.classAttribute().value((int) pred);
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            return null;
        }
    }

    /**
     * evaluate the classifier with the Test data
     *
     * @return evaluation summary as string
     */
    public String evaluate() throws IOException {


        try {
            String filename2 = TEST_DATA_ARFF.getText();
            String filename3 = TEST_DATA.getText();

            Instances testData;

            if (new File(filename2).exists()) {
                testData = loadArff(filename2);
                testData.setClassIndex(0);
            } else {
                testData = loadRawDataset(filename3);

                saveArff(testData, filename2);
            }

            Evaluation eval = new Evaluation(testData);
            eval.evaluateModel(classifier, testData);

            //System.out.println(eval.toSummaryString("Evaluation results:\n", false));
            //  jTextArea1.append(eval.toSummaryString("Evaluation results:\n", false));
            //jtaBay.append(eval.toSummaryString("\nModel Information\n=================\n", true));

            try {// Richard
                System.out.println(eval.toClassDetailsString("\n  \n"));
                System.out.println(eval.toMatrixString("\n   \n"));
                jTextArea1.append(eval.toClassDetailsString("\n\n   \n\n"));
                jTextArea1.append(eval.toMatrixString("\n\n   \n\n"));


                for (int i = 0; i < testData.numInstances(); i++) {
                    double actualClass = testData.instance(i).classValue();
                    String actual = testData.classAttribute().value((int) actualClass);
                    Instance newInst = testData.instance(i);
                    double predNB = classifier.classifyInstance(newInst);
                    //String predString = testDataset.classAttribute().value((int) predNB);

                    //   jTextArea1.setText(jTextArea1.getText() + testData.instance(i) + " =====> " + testData.classAttribute().value((int) predNB));
                    //  jTextArea1.append(testData.instance(i) + " =====> " + testData.classAttribute().value((int) predNB) + "\n");
                    //jTextArea1.append("\n");

                    jTextArea1.append(" " +testData.instance(i) + " =====> " + testData.classAttribute().value((int) predNB) + "\n");
                    //jTextArea1.append(testData.instance(i) + " =====> " + testData.classAttribute().value((int) predNB));
                       
                    System.out.println(testData.instance(i) + " =====> " + testData.classAttribute().value((int) predNB));

                }
            } catch (IOException e) {// Richard
            }



            return eval.toSummaryString();
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            return null;
        }
    }

    /**
     * This method loads the model to be used as classifier.
     *
     * @param fileName The name of the file that stores the text.
     */
    public void loadModel(String fileName) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
            Object tmp = in.readObject();
            classifier = (FilteredClassifier) tmp;
            in.close();
            System.out.println("Loaded model2: " + fileName);

            jTextArea1.append("Loaded model2: " + fileName);

        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    /**
     * This method saves the trained model into a file. This is done by simple
     * serialization of the classifier object.
     *
     * @param fileName The name of the file that will store the trained model.
     */
    public void saveModel(String fileName) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(classifier);
            jTextArea1.append(" " + out.toString());

            out.close();
            System.out.println("Saved model2: " + fileName);

            jTextArea1.append("Saved model2: " + fileName);

        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    /**
     * Loads a dataset in space seperated text file and convert it to Arff
     * format.
     *
     * @param fileName The name of the file.
     */
    public Instances loadRawDataset(String filename) {
        /* 
         *  Create an empty training set
         *  name the relation “Rel”.
         *  set intial capacity of 10*
         */

        Instances dataset = new Instances("BIG legalmessages", wekaAttributes, 10);

        // Set class index
        dataset.setClassIndex(0);

        // read text file, parse data and add to instance
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            for (String line;
                    (line = br.readLine()) != null;) {
                // split at first occurance of n no. of words
                String[] parts = line.split("\\s+", 2);

                // basic validation
                if (!parts[0].isEmpty() && !parts[1].isEmpty()) {

                    DenseInstance row = new DenseInstance(2);
                    row.setValue(wekaAttributes.get(0), parts[0]);
                    row.setValue(wekaAttributes.get(1), parts[1]);

                    // add row to instances
                    dataset.add(row);
                }

            }

        } catch (IOException e) {
            //LOGGER.warning(e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("invalid row.");
        }
        return dataset;

    }

    /**
     * Loads a dataset in ARFF format. If the file does not exist, or it has a
     * wrong format, the attribute trainData is null.
     *
     * @param fileName The name of the file that stores the dataset.
     */
    public Instances loadArff(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
            Instances dataset = arff.getData();
            //replace with logger System.out.println("loaded dataset: " + fileName);
            reader.close();
            return dataset;
        } catch (IOException e) {
            // LOGGER.warning(e.getMessage());
            return null;
        }
    }

    /**
     * This method saves a dataset in ARFF format.
     *
     * @param dataset dataset in arff format
     * @param fileName The name of the file that stores the dataset.
     */
    public void saveArff(Instances dataset, String filename) {
        try {
            // initialize 
            ArffSaver arffSaverInstance = new ArffSaver();
            arffSaverInstance.setInstances(dataset);
            arffSaverInstance.setFile(new File(filename));
            arffSaverInstance.writeBatch();
        } catch (IOException e) {
            //LOGGER.warning(e.getMessage());
        }
    }

    
    
    
    
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TRAIN_DATA = new javax.swing.JTextField();
        TRAIN_ARFF_ARFF = new javax.swing.JTextField();
        TEST_DATA = new javax.swing.JTextField();
        TEST_DATA_ARFF = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        TRAIN_DATA.setEditable(false);

        TRAIN_ARFF_ARFF.setEditable(false);

        TEST_DATA.setEditable(false);

        TEST_DATA_ARFF.setEditable(false);

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabel1.setText("TRAIN_DATA");

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabel2.setText("TRAIN_ARFF_ARFF");

        jLabel3.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabel3.setText("TEST DATA");

        jLabel4.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabel4.setText("TEST_DATA_ARFF");

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(153, 153, 0));
        jButton1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("DATA ANALYTICS ");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton2.setText("POST COMMENT");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton3.setText("Refesh");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel5.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(204, 102, 0));
        jLabel5.setText("                  DEVELOP A MACHINE LEARNING MODEL FOR BIG DATA ANALYTICS ");

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
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 892, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(139, 139, 139))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(TRAIN_DATA)
                            .addComponent(TRAIN_ARFF_ARFF)
                            .addComponent(TEST_DATA)
                            .addComponent(TEST_DATA_ARFF, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE))
                        .addGap(28, 28, 28)
                        .addComponent(jButton1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TRAIN_DATA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TRAIN_ARFF_ARFF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TEST_DATA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TEST_DATA_ARFF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handl
        
        
        try {
            String MODEL = "model2/sms.dat";

            Performance wt = new Performance();

            if (new File(MODEL).exists()) {
                wt.loadModel(MODEL);
            } else {
                wt.transform();
                wt.fit();
                wt.saveModel(MODEL);

                //wt.predict(MODEL);
            }

            System.out.println("Evaluation Result: \n" + wt.evaluate());
            jTextArea1.append("Evaluation Result: \n" + wt.evaluate());

           

        } catch (IOException e) {
        }

        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:

        /**
        * System.out.println("jack"); String keyword1 = "or",keyword2 = "2";
        * System.out.println(jTextField1.getText());
        * (jTextField1.getText().toLowerCase().indexOf(keyword1.toLowerCase())
            * != -1 ) { if
            * (jTextField1.getText().toLowerCase().indexOf(keyword2.toLowerCase())
                * != -1) { jTextArea2.append("\n IlegalMessage"); }
            *
            * }*
        */
        try {
            //String keyword1 = "or" +"2" +"to";
            //if (jTextField1.getText().toLowerCase().indexOf(jTextField1.getText()) != -1) {
                /**
                * if ((!"bad".contains(jTextField1.getText()) &&
                    * !"idiot".contains(jTextField1.getText()) &&
                    * !"fool".contains(jTextField1.getText()))) { jTextArea2.append("\n
                    * legalMessage") } //} else { jTextArea2.append("\n IlegalMessage
                    * ");
                    *
                    *
                    * }*
                */
                //Negative words that start with A letter
                if (jTextField1.getText().indexOf("abnormal") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                    //System.err.printf("Yes '%s' contains word 'World' %n" , word);
                } else if (jTextField1.getText().indexOf("abolish") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abominable") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abominably") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abominate") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abomination") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abort") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aborted") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aborts") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abrade") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abrasive") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abrupt") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abruptly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abscond") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("absence") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("absent-minded") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("absentee") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("absurd") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("absurdity") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("absurdly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("absurdness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("absurdness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abuse") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abused") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abuses") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abusive") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abysmal") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abysmally") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("abyss") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("accidental") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("accost") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("accursed") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("accusation") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("accusations") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("accuse") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("accuses") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("accusing") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("accusingly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("acerbate") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("acerbic") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("acerbically") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("ache") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("ached") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aches") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aching") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("acrid") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("acridly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("acridness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("acrimoniously") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("acrimony") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("adamant") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("adamantly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("addict") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("addicted") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("addicting") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("addicts") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("admonish") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("admonisher") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("admonishingly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("admonishment") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("admonition") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("adulterate") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("adulterated") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("adulteration") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("adversarial") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("adversary") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("adverse") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("adversity") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("afflict") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("affliction") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("afflictive") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("affront") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("afraid") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aggravate") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aggravating") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aggravation") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aggression") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aggressive") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aggressiveness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aggressor") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aggrieve") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aggrieved") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aghast") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("agonies") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("agonize") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("agonizing") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("agonizingly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("agony") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aground") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("ail") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("ailing") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("ailment") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aimless") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("alarm") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("alarmed") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("alarming") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("alarmingly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("alienate") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("alienated") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("allegation") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("allegations") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("allege") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("allergic") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("allergies") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("allergy") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aloof") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("altercation") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("ambiguity") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("ambiguous") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("ambivalence") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("ambivalent") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("ambush") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("amiss") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("amputate") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anarchism") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anarchist") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anarchistic") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anarchy") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anemic") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anger") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("angrily") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("angriness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("angry") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anguish") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("animosity") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("annihilate") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("annihilation") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("annoy") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("annoyance") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("annoyances") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("annoyed") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("annoying") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("annoyingly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("annoys") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anomalous") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anomaly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("antagonism") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("antagonist") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("antagonistic") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("antagonize") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anti-") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anti-occupation") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anti-proliferation") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anti-social") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anti-us") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anti-white") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("antipathy") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("antiquated") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("antithetical") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anxieties") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anxiety") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anxious") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anxiously") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("anxiousness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("apathetic") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("apathetically") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("apathy") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("apocalypse") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("apocalyptic") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("apologist") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("appall") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("appalled") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("appalling") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("appallingly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("apprehension") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("apprehensions") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("apprehensive") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("apprehensively") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("arbitrary") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("archaic") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("arduous") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("arduously") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("argumentative") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("arrogance") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("arrogant") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("arrogantly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("ashamed") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("asinine") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("asininely") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("askance") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("asperse") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aspersion") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aspersions") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("assail") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("assassin") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("assassinate") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("assault") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("astray") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("asunder") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("atrocious") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("atrocities") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("atrocity") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("atrophy") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("attack") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("attacks") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("attacking") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("audacious") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("audaciously") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("audaciousness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("audacity") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("austere") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("authoritarian") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("autocratic") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("avalanche") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf(" avarice") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("avaricious") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("avariciously") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("avenge") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("averse") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("aversion") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("awful") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("awfully") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("awfulness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("awkward") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("awkwardness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("ax") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                    //Eending of Negative word starting with alphabet A

                    //Negative words that start with B letter
                } else if (jTextField1.getText().indexOf("babble") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("back-logged") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("back-wood") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("back-woods") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("backache") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("backaches") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("backbite") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("backbiting") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("backward") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("backwardness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("backwoods") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bad") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("badly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("baffle") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("baffled") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bafflement") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("baffling") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bait") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("balk") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("banal") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bane") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("banish") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("banishment") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bankrupt") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("barbarian") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("barbaric") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("barbarically") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("barbarity") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("barbarous") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("barbarously") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("barren") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("baseless") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bash") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bashed") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bashful") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bashing") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("battered") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("battering") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("batty") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bearish") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("beastly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bedlam") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bedlamite") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("befoul") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("beg") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("beggar") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("beggarly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("begging") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("beguile") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("belabor") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("belated") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("beleaguer") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("belie") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("belittle") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("belittled") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("belittling") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bellicose") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("belligerence") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("belligerent") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("belligerently") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bemoan") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bemoaning") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bemused") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bent") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("berate") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bereave") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bereavement") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bereft") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("berserk") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("beseech") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("beset") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("besiege") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("besmirch") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("betray") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("betrayal") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("betrayals") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("betraying") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("betrays") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bewail") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("beware") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bewilder") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bewildered") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bewildering") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bewilderingly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bewilderment") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bewitch") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("biased") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("biases") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bicker") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bickering") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bid-rigging") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bigotries") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bigotry") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("biting") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bitingly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bitter") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf(" bitterly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bitterness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bizarre") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blab") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blabber") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blackmail") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blame") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blameworthy") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bland") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blandish") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blaspheme") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blasphemous") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blasphemy") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blasted") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blatant") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blatantly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blather") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bleak") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bleakly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bleakness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bleed") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bleeding") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bleeds") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blemish") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blind") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blinding") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blindside") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blister") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blistering") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bloated") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blockage") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bloodshed") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bloodthirsty") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bloody") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blotchy") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blunder") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blundering") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blunt") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blur") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blurred") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blurring") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blurry") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("blurt") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("boggle") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bogus") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("boil") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("boiling") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("boisterous") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bomb") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bombard") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bombardment") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bombastic") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bonkers") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bore") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("boredom") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bores") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("boring") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("botch") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bother") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bothered") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bothering") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bothers") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bothersome") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bowdlerize") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("boycott") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("braggart") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bragger") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brainless") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brainwash") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brash") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brashly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brashness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brat") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bravado") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brazen") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brazenly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brazenness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("breach") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("break") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("break-up") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("break-ups") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("breakdown") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("breaking") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("breaks") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("breakup") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("breakups") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("breaks") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bribery") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brimstone") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bristle") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brittle") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("broke") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("broken") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("broken-hearted") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brood") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("browbeat") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bruise") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bruised") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bruises") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bruising") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brusque") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brutal") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brutalities") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brutality") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brutalize") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brutalizing") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brutally") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brute") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("brutish") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("buckle") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bug") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bugging") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("buggy") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bugs") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bulkier") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bulkiness") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bulky") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bull****") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bull—-") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bullies") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bulls..t") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bully") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bullying") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bullyingly") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bum") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bump") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bumped") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bumps") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bumpy") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bungle") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bungler") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bungling") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bunk") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("burden") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("burdensome") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("burdensomely") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("burn") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("burned") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("burning") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("burns") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("bust") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("busts") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("busybody") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("butcher") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("butchery") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("buzzing") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                } else if (jTextField1.getText().indexOf("byzantine") != -1) {
                    jTextArea2.append("\n IlegalMessage");
                
                    
                    ///Richard
                    
                                      
                  } else if (jTextField1.getText().indexOf("cackle") != -1) {
                jTextArea2.setText("\n IlegalMessage");
                
                 } else if (jTextField1.getText().indexOf("calamities") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("calamitous") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("calamitously") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("calamity") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("callous") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("calumniate") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("calumniation") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("calumnies") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("calumnious") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("damage") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("damaged") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("damages") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("damaging") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("damnable") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("damnably") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("damnation") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("damned") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("damning") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("damper") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("danger") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("dangerous") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("dangerousness") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("dark") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("darken") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("darkened") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("darker") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("darkness") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("dastard") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("dastardly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("daunt") != -1) {
                jTextArea2.setText("\n IllegalMessage");


            } else if (jTextField1.getText().indexOf("earsplitting") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("eccentric") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("eccentricity") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("effigy") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("effrontery") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("egocentric") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("egomania") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("egotism") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("egotistical") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("egotistically") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("egregious") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("egregiously") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("election-rigger") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("elimination") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("emaciated") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("emasculate") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("embarrass") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("embarrassing") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("embarrassingly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("embarrassment") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("embattled") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("embroil") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("embroiled") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("embroilment") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("emergency") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("emphatic") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("emphatically") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("emptiness") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("encroach") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("encroachment") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("endanger") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("enemies") != -1) {
                jTextArea2.setText("\n IllegalMessage");



            } else if (jTextField1.getText().indexOf("fuck") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("fabricate") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("fabrication") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("facetious") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("facetiously") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("fail") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("failed") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("failing") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("fails") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("failure") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("failures") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("faint") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("fainthearted") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("faithless") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("fake") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("fall") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("fool") != -1) {
                jTextArea2.setText("\n IllegalMessage");

            } else if (jTextField1.getText().indexOf("gabble") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gaff") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gaffe") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gainsay") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gainsayer") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gall") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("galling") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gallingly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("galls") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gangster") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gape") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("garbage") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("garish") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gasp") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gauche") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gaudy") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gawk") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gawky") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("geezer") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("genocide") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("get-rich") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ghastly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ghetto") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ghosting") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gibber") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gibberish") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gibe") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("giddy") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gimmick") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gimmicked") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gimmicking") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gimmicks") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gimmicky") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("glare") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("glaringly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("glaringly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("glib") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gliblyvic") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("glitch") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("glitches") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gloatingly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gloom") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gloomy") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("glower") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("glum") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("glut") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("gnawing") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("goad") != -1) {
                jTextArea2.setText("\n IllegalMessage");


            } else if (jTextField1.getText().indexOf("hack") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hacks") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("haggard") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("haggle") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("halfhearted") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("halfheartedly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hallucinate") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hallucination") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hamper") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hampered") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("handicapped") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hang") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hangs") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("haphazard") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hapless") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("harangue") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("harass") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("harassed") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("harasses") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("harboring") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("harbors") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hard") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hard-hit") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hard-liner") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hardball") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("harden") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hardened") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hardheaded") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hardhearted") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hardliner") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hardliners") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("hardship") != -1) {
                jTextArea2.setText("\n IllegalMessage");

            } else if (jTextField1.getText().indexOf("idiocies") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("idiocy") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("idiot") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("idiotic") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("idiotically") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("idiots") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("idle") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ignoble") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ignominious") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ignominiously") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ignominy") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ignorance") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ignorant") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ignore") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ill-advised") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ill-conceived") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ill-defined") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ill-designed") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ill-fated") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ill-favored") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ill-formed") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ill-mannered") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ill-natured") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ill-sorted") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ill-tempered") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ill-treated") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ill-treatment") != -1) {
                jTextArea2.setText("\n IllegalMessage");

            } else if (jTextField1.getText().indexOf("jabber") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jaded") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jagged") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jam") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jarring") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jaundiced") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jealous") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jealously") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jealousness") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jealousy") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jeer") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jeering") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jeeringly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jeers") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jeopardize") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jeopardy") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jerk") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jerky") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jitter") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jitters") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("jittery") != -1) {
                jTextArea2.setText("\n IllegalMessage");


            } else if (jTextField1.getText().indexOf("kill") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("killed") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("killer") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("killing") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("killjoy") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("kills") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("knave") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("knife") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("knock") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("knotted") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("kook") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("kooky") != -1) {
                jTextArea2.setText("\n IllegalMessage");

            } else if (jTextField1.getText().indexOf("lack") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("lackadaisical") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("lacked") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("lackey") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("lackeys") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("lacking") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("lackluster") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("lacks") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("laconic") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("lag") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("lagged") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("lagging") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("lags") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("lazy") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("leak") != -1) {
            } else if (jTextField1.getText().indexOf("lie") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("lied") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("lies") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("life-threatening") != -1) {
                jTextArea2.setText("\n IllegalMessage");

            } else if (jTextField1.getText().indexOf("macabre") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("mad") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("madden") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("maddening") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("maddeningly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("madder") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("madly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("madman") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("madness") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("maladjusted") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("maladjustment") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("malady") != -1) {
                jTextArea2.setText("\n IllegalMessage");

                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("mess") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("messed") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("messes") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("messing") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("messy") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("midget") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("miff") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("militancy") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("nag") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("nagging") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("naive") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("naively") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("narrower") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("nastily") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("nastiness") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("naughty") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("nauseate") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("nauseates") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("nauseating") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("nauseatingly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("naïve") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("nebulous") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("nebulously") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("needless") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("needy") != -1) {
                jTextArea2.setText("\n IllegalMessage");


            } else if (jTextField1.getText().indexOf("obese") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("object") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("objection") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("objectionable") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("objections") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("oblique") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obliterate") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obliterated") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("oblivious") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obnoxious") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obnoxiously") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obscene") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obscenely") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obscenity") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obscure") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obscured") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obscures") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obsess") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obsessive") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obsessively") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obsessiveness") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obsolete") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obstacle") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obstinate") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obstinately") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obstruct") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obstructed") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obstructing") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obstruction") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obstructs") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obtrusive") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("obtuse") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("occlude") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("occluded") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("occludes") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("occluding") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("odd") != -1) {
            } else if (jTextField1.getText().indexOf("pain") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("painful") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("painfully") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("pains") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("pale") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("pales") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("pan") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("pandemonium") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("pander") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("pandering") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("panders") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("panic") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("panicked") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("panicking") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("panicky") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("paradoxical") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("paradoxically") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("paralyzed") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("paranoia") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("paranoid") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("parasite") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("pariah") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("parody") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("partiality") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("partisan") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("partisans") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("passive") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("passiveness") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("pathetic") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("poor") != -1) {
            } else if (jTextField1.getText().indexOf("quack") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("qualm") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("qualms") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("quandary") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("quarrel") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("quarrels") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("quarrelsome") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("quash") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("queer") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("questionable") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("quibble") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("quibbles") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("quitter") != -1) {
                jTextArea2.setText("\n IllegalMessage");


            } else if (jTextField1.getText().indexOf("rabid") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("racism") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("racist") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("racists") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("racy") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("radical") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("radicalization") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("radically") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("radicals") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("rage") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ragged") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("raging") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("rail") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("raked") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("rampage") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("rampant") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ramshackle") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("rancor") != -1) {
                jTextArea2.setText("\n IllegalMessage");



            } else if (jTextField1.getText().indexOf("sabotage") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("sack") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("sacrificed") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("sad") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("sadden") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("sadly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("sadness") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("sag") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("sagged") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("sagging") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("saggy") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("sags") != -1) {

                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("stunt") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("stunted") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("stupid") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("stupidest") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("stupidity") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("stupidly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("stupor") != -1) {
                jTextArea2.setText("\n IllegalMessage");


            } else if (jTextField1.getText().indexOf("ugh") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("uglier") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ugliest") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ugliness") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ugly") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ulterior") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ultimatum") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ultimatums") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("ultra-hardline") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("un-viewable") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("unable") != -1) {
                jTextArea2.setText("\n IllegalMessage");
            } else if (jTextField1.getText().indexOf("vagrant") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vague") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vagueness") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vain") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vainly") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vanity") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vehement") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vehemently") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vengeance") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vengeful") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vengefully") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vengefulness") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("venom") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("venomous") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("venomously") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vent") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vestiges") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vex") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vexation") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vexing") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vexingly") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vibrate") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vibrated") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vibrates") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vibrating") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vibration") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vice") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vicious") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("viciously") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("viciousness") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("victimize") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vile") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vileness") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vilify") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("villainous") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("villainously") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("villains") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vindictive") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vindictively") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("vindictiveness") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("violate") != -1) {
                jTextArea2.setText("\n IlegalMessage");


            } else if (jTextField1.getText().indexOf("wail") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("wallow") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("wane") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("waning") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("wanton") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("war-like") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("warily") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("wariness") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("warlike") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("warned") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("warning") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("warp") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("warped") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("wary") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("waste") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("wasted") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("wasteful") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("wastefulness") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("wasting") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("water-down") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("watered-down") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("wayward") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("weak") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("weaken") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("weakening") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("weaker") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("weakness") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("weaknesses") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("weariness") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("wearisome") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("weary") != -1) {
                jTextArea2.setText("\n IlegalMessage");

            } else if (jTextField1.getText().indexOf("yawn") != -1) {
                jTextArea2.setText("\n IlegalMessage");

            } else if (jTextField1.getText().indexOf("zap") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("zapped") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("zaps") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("zealot") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("zealous") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("zealously") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("zombie") != -1) {
                jTextArea2.setText("\n IlegalMessage");


                
                } else if (jTextField1.getText().indexOf("mad") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("zombi") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("folish") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("callous") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("heartless") != -1) {
                jTextArea2.setText("\n IlegalMessage");
                
                } else if (jTextField1.getText().indexOf("mean") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("rubbish") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("dirty") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("crash") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("dangerous") != -1) {
                jTextArea2.setText("\n IlegalMessage");
                
                } else if (jTextField1.getText().indexOf("wicked") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("hash") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("evil") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("crazy") != -1) {
                jTextArea2.setText("\n IlegalMessage");
            } else if (jTextField1.getText().indexOf("cow head") != -1) {
                jTextArea2.setText("\n IlegalMessage");
                    
                    
                    
                
                
                
                
                

                } else {
                    jTextArea2.append("\n legalMessage");

                    //System.err.printf("Sorry %s does not contains word 'World' %n " , word);
                }

                //IlegalMessage

            } catch (Exception e) {
            }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        
        
        jTextField1.setText("");
        jTextArea1.setText("");
        jTextArea2.setText("");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

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
            java.util.logging.Logger.getLogger(Performance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Performance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Performance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Performance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Performance().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField TEST_DATA;
    private javax.swing.JTextField TEST_DATA_ARFF;
    private javax.swing.JTextField TRAIN_ARFF_ARFF;
    private javax.swing.JTextField TRAIN_DATA;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
