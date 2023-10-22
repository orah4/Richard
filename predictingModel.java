/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhs;

/**
 *
 * @author UniTECH
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Attribute;
//import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class predictingModel {

    public static final String TAB = "\t";

    public static final String SEPARATOR = "-------------------------------------------------------";

    public void predictionModel(int model) throws Exception {

        double percent = 80;
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("Datasets-04-0-1.arff");
        Instances instances = source.getDataSet();
        instances.randomize(new java.util.Random(0));

        // 80% de los datos a ser cargados a la base de casos
        int trainSize = (int) Math.round(instances.numInstances() * percent / 100);
        // 20% : datos para la prueba
        int testSize = instances.numInstances() - trainSize;
        Instances train = new Instances(instances, 0, trainSize);
        Instances test = new Instances(instances, trainSize, testSize);

        System.out.println(predictingModel.TAB + "Total Sample instancias: " + instances.numInstances());
        System.out.println(predictingModel.TAB + "Train Set: " + trainSize);
        System.out.println(predictingModel.TAB + "Test Set: " + testSize);

        //Obtenemos las clases en este caso seria el ultimo atributo
        //Seleccionar la posición del atributo que identifica la clase, calidad del vino {0 al 10}
        train.setClassIndex(train.numAttributes() - 1);
        //Obtiene el numero de clases 
        int numClasses = train.numClasses();
        //Imprimir las etiquetas de las clases del training dataset
        for (int i = 0; i < numClasses; i++) {
            //Get class string value using the class index
            String classValue = train.classAttribute().value(i);
            System.out.println(predictingModel.TAB + "Value using clase index " + i + "  " + classValue);
        }

        //Crear modelo del clasificador con el 80 % que son datos de la muestra
        // RandomForest rf = new RandomForest();
        // SMO tr = new SMO();
        SMO ml = new SMO();
        J48 jc = new J48();
        // IBk ib = new IBk();

        // rf.buildClassifier(train);
        //tr.buildClassifier(train);
        ml.buildClassifier(train);
        jc.buildClassifier(train);//train BD
        //ib.buildClassifier(train);

        System.out.println(predictingModel.TAB + "Testing process...");
        System.out.println(predictingModel.SEPARATOR);

        test.setClassIndex(test.numAttributes() - 1);
        System.out.println(predictingModel.TAB + "Actual Class" + predictingModel.TAB + " Predicted Class");
        System.out.println(predictingModel.TAB + "===============================");
        int correctSamples = 0;

        for (int i = 0; i < test.numInstances(); i++) {
            //Retorna el valor de la clase
            double actualClass = test.instance(i).classValue();
            //Obtiene el nombre de la clase según el valor de actualClass
            String actual = test.classAttribute().value((int) actualClass);
            //obtener la instancia de la lista de prueba
            Instance newInst = test.instance(i);
            //Predecir el valor de la clase
            double predModel = 0;

            //double predNB = 0;
            if (model == 0) {
                predModel = ml.classifyInstance(newInst);
            } else {
                predModel = jc.classifyInstance(newInst);
            }

            // switch (model) {
            //  case 0:
            //Random Forest
            //   predModel = rf.classifyInstance(newInst);
            //Decision Tree
            //  break;
            //case 1:
            //   predModel = jc.classifyInstance(newInst);
            // break;
            //case 2:
            // predModel = tr.classifyInstance(newInst);
            // break;
            // case 3:
            //  predModel = ml.classifyInstance(newInst);
            //  break;
            //case 4:
            //   predModel = ib.classifyInstance(newInst);
            //  break;
            //default:
            //  predModel = jc.classifyInstance(newInst);
            // break;
            // }
            String predString = test.classAttribute().value((int) predModel);
            System.out.println(predictingModel.TAB + actual + predictingModel.TAB + predString);
            if ((int) actualClass == (int) predModel) {
                correctSamples++;
            }

        }

        double accuracy = correctSamples / (testSize * 1.0);
        if (model == 0) {
            System.out.println("Accuracy: " + accuracy * 100 + " %  Application model ANN");
        } else {

            System.out.println("Accuracy: " + accuracy * 100 + " %  Application model J48");
        }

    }

    //Scanner input = new Scanner(System.in);
    //double accuracy = correctSamples / (testSize * 1.0);
    //switch (model) {
    //case 0:
    //  System.out.println("Enter 0 For Random Forest");
    //  model = input.nextInt();
    // System.out.println("Accuracy: " + accuracy * 100 + " %  Application model Radom Forest" + model);
    // break;
    //case 1:
    //  System.out.println("Enter 1 For RT");
    // model = input.nextInt();
    // System.out.println("Accuracy: " + accuracy * 100 + " %  Application model Random Tree" + model);
    // break;
    //case 2:
    //  System.out.println("Enter 2 SMO");
    // model = input.nextInt();
    // System.out.println("Accuracy: " + accuracy * 100 + " %  Application model SVM" + model);
    // break;
    // case 3:
    // System.out.println("Enter 3 ANN");
    // model = input.nextInt();
    // System.out.println("Accuracy: " + accuracy * 100 + " %  Application model MultilayerPerceptron" + model);
    // break;
    // case 4:
    // System.out.println("Enter 4 For kNN");
    // model = input.nextInt();
    // System.out.println("Accuracy: " + accuracy * 100 + " %  Application model kNN" + model);
    // break;
    //default:
    //System.out.println("No Model Executed");
    // break;
    //}
    //  }
    public String evaluation(
            String Gender,
            String Marital_Stat,
            String Prog,
            String City,
            String Mode_Study,
            String Age,
            String Employ_Stat,
            String Family_Size,
            String Job_Course,
            String Sponsor,
            String prog_Motive,
            String NAAC,
            String Atten_Lect,
            String Sup_Area,
            String Sup_Busy,
            String incompact_Sup,
            String Sup_Not_uptodate,
            String Spu_Commitment,
            String Sup_Unavailable,
            String Sup_Expertise,
            String Study_conflits_job,
            String Access_Internet,
            String Diff_Research_Topic,
            String Lack_ICT_Knowledge,
            String Insuff_Know_Research,
            String Strike,
            String Lack_Proper_Guide,
            String Funding_Prob,
            String Accom_Prob,
            String poor_Lib_Equip_Lab,
            String Sub_Result,
            String Keep_No_of_Friends,
            String Regular_Hangout,
            String Use_of_Stimulant,
            String AccessInternet,
            String Sponsor_Partne_Enourage,
            String level,
            String First_Semester_Course,
            String Tatal_Grade_Score,
            String grade_Level,
            String trcu,
            String tecu,
            String tcp,
            String gpa,
            String cumtrcu,
            String cumtecu,
            String cumtcp,
            String cumgpa,
            String remarks_fst,
            String cumtocu,
            String Second_Semester_Course,
            String Tatal_Grade_Score_snd,
            String trcu_snd,
            String tecu_snd,
            String tcp_snd,
            String gpa_snd,
            String cumtrcu_snd,
            String cumtecu_snd,
            String cumtcp_snd,
            String cumgpa_snd,
            String remarks,
            String cumtocu_snd,
            int model) throws Exception {
        Instances data;
        data = ConverterUtils.DataSource.read("Datasets-04-0-1.arff");
        data.setClassIndex(62);

        // RandomForest rf = new RandomForest();
        // SMO tr = new SMO();
        SMO ml = new SMO();
        J48 jc = new J48();
        //IBk ib = new IBk();        

        // NaiveBayes nb = new NaiveBayes();
        //J48 jc = new J48();
        // rf.buildClassifier(data);
        //tr.buildClassifier(data);
        ml.buildClassifier(data);//train BD
        jc.buildClassifier(data);//train BD
        //ib.buildClassifier(data);
        //jc.buildClassifier(data);//train BD

        double pred = 0;
        Instance instance;
        if (data.numInstances() == 0) {
            throw new Exception("clasification no disponible");
        }
        instance = crearNuevaInstancia(
                Gender,
                Marital_Stat,
                Prog,
                City,
                Mode_Study,
                Age,
                Employ_Stat,
                Family_Size,
                Job_Course,
                Sponsor,
                prog_Motive,
                NAAC,
                Atten_Lect,
                Sup_Area,
                Sup_Busy,
                incompact_Sup,
                Sup_Not_uptodate,
                Spu_Commitment,
                Sup_Unavailable,
                Sup_Expertise,
                Study_conflits_job,
                AccessInternet,
                Diff_Research_Topic,
                Lack_ICT_Knowledge,
                Insuff_Know_Research,
                Strike,
                Lack_Proper_Guide,
                Funding_Prob,
                Accom_Prob,
                poor_Lib_Equip_Lab,
                Sub_Result,
                Keep_No_of_Friends,
                Regular_Hangout,
                Use_of_Stimulant,
                Access_Internet,
                Sponsor_Partne_Enourage,
                level,
                First_Semester_Course,
                Tatal_Grade_Score,
                grade_Level,
                trcu,
                tecu,
                tcp,
                gpa,
                cumtrcu,
                cumtecu,
                cumtcp,
                cumgpa,
                remarks_fst,
                cumtocu,
                Second_Semester_Course,
                Tatal_Grade_Score_snd,
                trcu_snd,
                tecu_snd,
                tcp_snd,
                gpa_snd,
                cumtrcu_snd,
                cumtecu_snd,
                cumtcp_snd,
                cumgpa_snd,
                remarks,
                cumtocu_snd,
                data);
        ml.buildClassifier(data);

        if (model == 0) {
            pred = ml.classifyInstance(instance);

        } else {
            pred = jc.classifyInstance(instance);
        }
        String namePred = data.classAttribute().value((int) pred);

        return namePred;

    }

    public Instance crearNuevaInstancia(
            String Gender,
            String Marital_Stat,
            String Prog,
            String City,
            String Mode_Study,
            String Age,
            String Employ_Stat,
            String Family_Size,
            String Job_Course,
            String Sponsor,
            String prog_Motive,
            String NAAC,
            String Atten_Lect,
            String Sup_Area,
            String Sup_Busy,
            String incompact_Sup,
            String Sup_Not_uptodate,
            String Spu_Commitment,
            String Sup_Unavailable,
            String Sup_Expertise,
            String Study_conflits_job,
            String AccessInternet,
            String Diff_Research_Topic,
            String Lack_ICT_Knowledge,
            String Insuff_Know_Research,
            String Strike,
            String Lack_Proper_Guide,
            String Funding_Prob,
            String Accom_Prob,
            String poor_Lib_Equip_Lab,
            String Sub_Result,
            String Keep_No_of_Friends,
            String Regular_Hangout,
            String Use_of_Stimulant,
            String Access_Internet,
            String Sponsor_Partne_Enourage,
            String level,
            String First_Semester_Course,
            String Tatal_Grade_Score,
            String grade_Level,
            String trcu,
            String tecu,
            String tcp,
            String gpa,
            String cumtrcu,
            String cumtecu,
            String cumtcp,
            String cumgpa,
            String remarks_fst,
            String cumtocu,
            String Second_Semester_Course,
            String Tatal_Grade_Score_snd,
            String trcu_snd,
            String tecu_snd,
            String tcp_snd,
            String gpa_snd,
            String cumtrcu_snd,
            String cumtecu_snd,
            String cumtcp_snd,
            String cumgpa_snd,
            String remarks,
            String cumtocu_snd,
            Instances train)
            throws IOException {

        Instance instance = new Instance(62);
        Attribute atributo = train.attribute("Gender");
        Attribute atributo1 = train.attribute("Marital_Stat");
        Attribute atributo2 = train.attribute("Prog");
        Attribute atributo3 = train.attribute("City");
        Attribute atributo4 = train.attribute("Mode_Study");
        Attribute atributo5 = train.attribute("Age");
        Attribute atributo6 = train.attribute("Employ_Stat");
        Attribute atributo7 = train.attribute("Family_Size");
        Attribute atributo8 = train.attribute("Job_Course");
        Attribute atributo9 = train.attribute("Sponsor");
        Attribute atributo10 = train.attribute("prog_Motive");
        
        Attribute atributo11 = train.attribute("NAAC");
        Attribute atributo12 = train.attribute("Atten_Lect");
        Attribute atributo13 = train.attribute("Sup_Area");
        Attribute atributo14 = train.attribute("Sup_Busy");
        Attribute atributo15 = train.attribute("incompact_Sup");
        Attribute atributo16 = train.attribute("Sup_Not_uptodate");
        Attribute atributo17 = train.attribute("Spu_Commitment");
        Attribute atributo18 = train.attribute("Sup_Unavailable");
        Attribute atributo19 = train.attribute("Sup_Expertise");
        Attribute atributo20 = train.attribute("Study_conflits_job");
        Attribute atributo21 = train.attribute("Access-Internet");
        Attribute atributo22 = train.attribute("Diff_Research_Topic");
        Attribute atributo23 = train.attribute("Lack_ICT_Knowledge");
        Attribute atributo24 = train.attribute("Insuff_Know_Research");
        Attribute atributo25 = train.attribute("Strike");
        Attribute atributo26 = train.attribute("Lack_Proper_Guide");
        Attribute atributo27 = train.attribute("Funding_Prob");
        Attribute atributo28 = train.attribute("Accom_Prob");
        Attribute atributo29 = train.attribute("poor_Lib_Equip_Lab");
        Attribute atributo30 = train.attribute("Sub_Result");
        
        Attribute atributo31 = train.attribute("Keep_No_of_Friends");
        Attribute atributo32 = train.attribute("Regular_Hangout");
        Attribute atributo33 = train.attribute("Use_of_Stimulant");
        Attribute atributo34 = train.attribute("Access_Internet");// noted
        Attribute atributo35 = train.attribute("Sponsor_Partne_Enourage");
        
        
        Attribute atributo36 = train.attribute("level");
        Attribute atributo37 = train.attribute("First_Semester_Course");
        Attribute atributo38 = train.attribute("Tatal_Grade_Score");
        Attribute atributo39 = train.attribute("grade_Level");
        
        Attribute atributo40 = train.attribute("trcu");
        Attribute atributo41 = train.attribute("tecu");
        Attribute atributo42 = train.attribute("tcp");
        Attribute atributo43 = train.attribute("gpa");
        Attribute atributo44 = train.attribute("cumtrcu");
        Attribute atributo45 = train.attribute("cumtecu");
        Attribute atributo46 = train.attribute("cumtcp");
        Attribute atributo47 = train.attribute("cumgpa");// noted
        Attribute atributo48 = train.attribute("remarks_fst");
        Attribute atributo49 = train.attribute("cumtocu");
        Attribute atributo50 = train.attribute("Second_Semester_Course");
        Attribute atributo51 = train.attribute("Tatal_Grade_Score_snd");
        Attribute atributo52 = train.attribute("trcu_snd");
        
        Attribute atributo53 = train.attribute("tecu_snd");
        Attribute atributo54 = train.attribute("tcp_snd");
        Attribute atributo55 = train.attribute("gpa_snd");
        Attribute atributo56 = train.attribute("cumtrcu_snd");
        Attribute atributo57 = train.attribute("cumtecu_snd");
        Attribute atributo58 = train.attribute("cumtcp_snd");
        Attribute atributo59 = train.attribute("cumgpa_snd");
        Attribute atributo60 = train.attribute("remarks");// noted
        Attribute atributo61 = train.attribute("cumtocu_snd");
        

        
        instance.setValue(atributo, Gender);
        instance.setValue(atributo1, Marital_Stat);
        instance.setValue(atributo2, Prog);
        instance.setValue(atributo3, City);
        instance.setValue(atributo4, Mode_Study);
        instance.setValue(atributo5, Age);
        instance.setValue(atributo6, Employ_Stat);
        instance.setValue(atributo7, Family_Size);
        instance.setValue(atributo8, Job_Course);
        instance.setValue(atributo9, Sponsor);
        instance.setValue(atributo10, prog_Motive);
      
        
        instance.setValue(atributo11, NAAC);
        instance.setValue(atributo12, Atten_Lect);
        instance.setValue(atributo13, Sup_Area);
        instance.setValue(atributo14, Sup_Busy);
        instance.setValue(atributo15, incompact_Sup);
        instance.setValue(atributo16, Sup_Not_uptodate);
        instance.setValue(atributo17, Spu_Commitment);
        instance.setValue(atributo18, Sup_Unavailable);
        instance.setValue(atributo19, Sup_Expertise);
        instance.setValue(atributo20, Study_conflits_job);
        instance.setValue(atributo21, AccessInternet);
        instance.setValue(atributo22, Diff_Research_Topic);
        instance.setValue(atributo23, Lack_ICT_Knowledge);
        instance.setValue(atributo24, Insuff_Know_Research);
        instance.setValue(atributo25, Strike);
        instance.setValue(atributo26, Lack_Proper_Guide);
        instance.setValue(atributo27, Funding_Prob);
        instance.setValue(atributo28, Accom_Prob);
        instance.setValue(atributo29, poor_Lib_Equip_Lab);
        instance.setValue(atributo30, Sub_Result);
        
        
        instance.setValue(atributo31, Keep_No_of_Friends);
        instance.setValue(atributo32, Regular_Hangout);
        instance.setValue(atributo33, Use_of_Stimulant);
        instance.setValue(atributo34, Access_Internet);
        instance.setValue(atributo35, Sponsor_Partne_Enourage);
        
        instance.setValue(atributo36, level);
        instance.setValue(atributo37, First_Semester_Course);
        instance.setValue(atributo38, Tatal_Grade_Score);
        instance.setValue(atributo39, grade_Level);
        instance.setValue(atributo40, trcu);
        instance.setValue(atributo41, tecu);
        instance.setValue(atributo42, tcp);
        instance.setValue(atributo43, gpa);
        instance.setValue(atributo44, cumtrcu);
        instance.setValue(atributo45, cumtecu);
        instance.setValue(atributo46, cumtcp);
        instance.setValue(atributo47, cumgpa);
        instance.setValue(atributo48, remarks_fst);
        instance.setValue(atributo49, cumtocu);
        
        instance.setValue(atributo50, Second_Semester_Course);
        instance.setValue(atributo51, Tatal_Grade_Score_snd);
        instance.setValue(atributo52, trcu_snd);
        instance.setValue(atributo53, tecu_snd);
        instance.setValue(atributo54, tcp_snd);
        instance.setValue(atributo55, gpa_snd);
        instance.setValue(atributo56, cumtrcu_snd);
        instance.setValue(atributo57, cumtecu_snd);
        instance.setValue(atributo58, cumtcp_snd);
        instance.setValue(atributo59, cumgpa_snd);
        instance.setValue(atributo60, remarks);
        instance.setValue(atributo61, cumtocu_snd);
        
        
        
        
        
        
        instance.setDataset(train);
        return instance;
    }

}
